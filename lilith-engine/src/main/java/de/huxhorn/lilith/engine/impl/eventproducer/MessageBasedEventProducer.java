/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.huxhorn.lilith.engine.impl.eventproducer;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.sender.HeartbeatRunnable;
import de.huxhorn.sulky.buffers.AppendOperation;
import de.huxhorn.sulky.codec.Decoder;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class MessageBasedEventProducer<T extends Serializable>
	extends AbstractEventProducer<T>
{
	private final Logger logger = LoggerFactory.getLogger(MessageBasedEventProducer.class);

	private final DataInputStream dataInput;
	private final Decoder<T> decoder;
	private final AtomicLong heartbeatTimestamp;
	private final boolean requiresHeartbeat;

	public MessageBasedEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<T>> eventQueue, SourceIdentifierUpdater<T> sourceIdentifierUpdater, Decoder<T> decoder, InputStream inputStream, boolean requiresHeartbeat)
	{
		super(sourceIdentifier, eventQueue, sourceIdentifierUpdater);
		this.decoder = Objects.requireNonNull(decoder, "decoder must not be null!");
		this.dataInput = new DataInputStream(new BufferedInputStream(Objects.requireNonNull(inputStream, "inputStream must not be null!")));
		this.heartbeatTimestamp = new AtomicLong(System.currentTimeMillis());
		this.requiresHeartbeat = requiresHeartbeat;
	}

	@Override
	public void start()
	{
		updateHeartbeatTimestamp();
		String sourceIdentifierString=String.valueOf(getSourceIdentifier());
		Thread t = new Thread(new ReceiverRunnable(getSourceIdentifier()), sourceIdentifierString + "-Receiver");
		t.setDaemon(false);
		t.start();

		if(requiresHeartbeat)
		{
			t = new Thread(new HeartbeatObserverRunnable(), sourceIdentifierString + "-HeartbeatObserver");
			t.setDaemon(false);
			t.start();
		}
	}

	private void updateHeartbeatTimestamp()
	{
		heartbeatTimestamp.set(System.currentTimeMillis());
	}

	private long getMillisSinceLastHeartbeat()
	{
		return System.currentTimeMillis() - heartbeatTimestamp.get();
	}

	@Override
	public void close()
	{
		if(logger.isInfoEnabled()) logger.info("Closing {} for source {}.", this.getClass().getName(), getSourceIdentifier());
		if(dataInput != null)
		{
			try
			{
				dataInput.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}

	private class HeartbeatObserverRunnable
		implements Runnable
	{
		@Override
		public void run()
		{
			for(;;)
			{
				try
				{
					Thread.sleep(HeartbeatRunnable.HEARTBEAT_RATE);
					if(getMillisSinceLastHeartbeat() > 2 * HeartbeatRunnable.HEARTBEAT_RATE)
					{
						if(logger.isInfoEnabled()) logger.info("Closing receiver because heartbeat of {} was missing.", getSourceIdentifier());
						close();
						return;
					}
				}
				catch(InterruptedException e)
				{
					if(logger.isInfoEnabled()) logger.info("Interrupted...", e);
					close();
					return;
				}
			}
		}
	}

	private class ReceiverRunnable
		implements Runnable
	{
		private final SourceIdentifier sourceIdentifier;
		private static final String SOURCE_IDENTIFIER_MDC_KEY = "sourceIdentifier";

		ReceiverRunnable(SourceIdentifier sourceIdentifier)
		{
			this.sourceIdentifier = sourceIdentifier;
		}

		@Override
		public void run()
		{
			MDC.put(SOURCE_IDENTIFIER_MDC_KEY, sourceIdentifier.toString());
			for(;;)
			{
				try
				{
					boolean allocating = true;
					int size = 0;
					try
					{
						size = dataInput.readInt();
						updateHeartbeatTimestamp();
						if(size > 0)
						{
							byte[] bytes = new byte[size]; // NOPMD - AvoidInstantiatingObjectsInLoops
							allocating = false;
							dataInput.readFully(bytes);

							T object = decoder.decode(bytes);
							if(object == null)
							{
								if(logger.isInfoEnabled()) logger.info("Retrieved null!");
							}
							else
							{
								addEvent(object);
							}
						}
						else
						{
							if(logger.isDebugEnabled()) logger.debug("Received heartbeat from {}.", getSourceIdentifier());
						}
					}
					catch(OutOfMemoryError ex)
					{
						if(allocating)
						{
							if(logger.isWarnEnabled()) logger.warn("Out of memory while trying to allocate {} bytes! Skipping them instead...", size);
							skipBytes(size, dataInput);
						}
						else
						{
							if(logger.isWarnEnabled()) logger.warn("Out of memory while deserializing from {} bytes!", size);
						}
					}
				}
				catch(Throwable e)
				{
					if(logger.isDebugEnabled()) logger.debug("Exception ({}: '{}') while reading events. Adding eventWrapper with empty event and stopping...", e.getClass().getName(), e.getMessage(), e);
					addEvent(null);
					break;
				}
			}
			close();
			MDC.remove(SOURCE_IDENTIFIER_MDC_KEY); //this shouldn't be necessary but I don't care :p
		}

		void skipBytes(long numberOfBytes, InputStream input)
			throws IOException
		{
			long skippedTotal = 0;
			while(skippedTotal < numberOfBytes)
			{
				long skipped = input.skip(numberOfBytes - skippedTotal);
				if(skipped < 0)
				{
					throw new IOException("Negative skipped bytes value while trying to skip " + numberOfBytes + " bytes!");
				}
				skippedTotal = skippedTotal + skipped;
			}
		}
	}
}
