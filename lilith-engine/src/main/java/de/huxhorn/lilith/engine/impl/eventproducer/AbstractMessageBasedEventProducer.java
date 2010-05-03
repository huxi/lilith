/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractMessageBasedEventProducer<T extends Serializable>
	extends AbstractEventProducer<T>
{
	private final Logger logger = LoggerFactory.getLogger(AbstractMessageBasedEventProducer.class);

	private final DataInputStream dataInput;
	private Decoder<T> decoder;
	private boolean compressing;
	private final AtomicLong heartbeatTimestamp;

	public AbstractMessageBasedEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<T>> eventQueue, InputStream inputStream, boolean compressing)
	{
		super(sourceIdentifier, eventQueue);
		this.dataInput = new DataInputStream(new BufferedInputStream(inputStream));
		this.compressing = compressing;
		this.decoder = createDecoder();
		this.heartbeatTimestamp = new AtomicLong();
	}

	protected abstract Decoder<T> createDecoder();

	public void start()
	{
		updateHeartbeatTimestamp();
		Thread t = new Thread(new ReceiverRunnable(getSourceIdentifier()), "" + getSourceIdentifier() + "-Receiver");
		t.setDaemon(false);
		t.start();

		t = new Thread(new HeartbeatObserverRunnable(), "" + getSourceIdentifier() + "-HeartbeatObserver");
		t.setDaemon(false);
		t.start();
	}

	protected void updateHeartbeatTimestamp()
	{
		heartbeatTimestamp.set(System.currentTimeMillis());
	}

	protected long getHeartbeatTimestamp()
	{
		return heartbeatTimestamp.get();
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	private class HeartbeatObserverRunnable
		implements Runnable
	{
		public void run()
		{
			for(; ;)
			{
				try
				{
					Thread.sleep(HeartbeatRunnable.HEARTBEAT_RATE);
					long heartbeat = getHeartbeatTimestamp();
					if(System.currentTimeMillis() - heartbeat > 2 * HeartbeatRunnable.HEARTBEAT_RATE)
					{
						if(logger.isInfoEnabled())
						{
							logger.info("Closing receiver because heartbeat of {} was missing.", getSourceIdentifier());
						}
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
		private SourceIdentifier sourceIdentifier;
		private static final String SOURCE_IDENTIFIER_MDC_KEY = "sourceIdentifier";

		public ReceiverRunnable(SourceIdentifier sourceIdentifier)
		{
			this.sourceIdentifier = sourceIdentifier;
		}

		public void run()
		{
			MDC.put(SOURCE_IDENTIFIER_MDC_KEY, sourceIdentifier.toString());
			for(; ;)
			{
				try
				{
					boolean allocating = true;
					int size = 0;
					try
					{
						// TODO: obtain transfer size info						
						size = dataInput.readInt();
						updateHeartbeatTimestamp();
						if(size > 0)
						{
							byte[] bytes = new byte[size];
							allocating = false;
							dataInput.readFully(bytes);

							Object object = decoder.decode(bytes);
							if(object == null)
							{
								if(logger.isInfoEnabled()) logger.info("Retrieved null!");
							}
							else// if(object instanceof T)
							{
								try
								{
									//noinspection unchecked
									T event = (T) object;
									addEvent(event);
								}
								catch(ClassCastException ex)
								{
									if(logger.isInfoEnabled())
									{
										logger
											.info("Ignoring object of class '" + object.getClass().getName() + "'...");
									}
								}
							}
						}
						else
						{
							if(logger.isDebugEnabled())
							{
								logger.debug("Received heartbeat from {}.", getSourceIdentifier());
							}
						}
					}
					catch(OutOfMemoryError ex)
					{
						if(allocating)
						{
							if(logger.isWarnEnabled())
							{
								logger
									.warn("Out of memory while trying to allocate {} bytes! Skipping them instead...", size);
							}
							skipBytes(size, dataInput);
						}
						else
						{
							if(logger.isWarnEnabled())
							{
								logger.warn("Out of memory while deserializing from {} bytes!", size);
							}
						}
					}
				}
				catch(Throwable e)
				{
					if(logger.isInfoEnabled())
					{
						logger
							.info("Exception ({}: '{}') while reading events. Adding eventWrapper with empty event and stopping...", e
								.getClass().getName(), e.getMessage());
					}
					addEvent(null);
					break;
				}
			}
			MDC.remove(SOURCE_IDENTIFIER_MDC_KEY); //this shouldn't be necessary but I don't care :p
		}

		public void skipBytes(long numberOfBytes, InputStream input)
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

	public void close()
	{
		if(logger.isInfoEnabled()) logger.info("Closing {}.", this.getClass().getName());
		IOUtils.closeQuietly(dataInput);
	}
}
