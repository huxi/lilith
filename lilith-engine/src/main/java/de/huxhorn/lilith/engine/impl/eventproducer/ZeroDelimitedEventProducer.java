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
import de.huxhorn.sulky.buffers.AppendOperation;
import de.huxhorn.sulky.codec.Decoder;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.SocketException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZeroDelimitedEventProducer<T extends Serializable>
		extends AbstractEventProducer<T>
{
	private final Logger logger = LoggerFactory.getLogger(ZeroDelimitedEventProducer.class);

	private final Decoder<T> decoder;
	private final BufferedInputStream inputStream;

	public ZeroDelimitedEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<T>> eventQueue, SourceIdentifierUpdater<T> sourceIdentifierUpdater, Decoder<T> decoder, InputStream inputStream)
	{
		super(sourceIdentifier, eventQueue, sourceIdentifierUpdater);
		this.decoder = decoder;

		this.inputStream = new BufferedInputStream(inputStream);
	}

	@Override
	public void start()
	{
		Thread t = new Thread(new ReceiverRunnable(), getSourceIdentifier() + "-Receiver");
		t.setDaemon(true);
		t.start();
	}

	@Override
	public void close()
	{
		if(inputStream != null)
		{
			try
			{
				inputStream.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}

	private class ReceiverRunnable
			implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				ArrayList<Byte> bytes = new ArrayList<>();
				for (;;)
				{
					for (;;)
					{
						int readByte = inputStream.read();
						if (readByte == -1)
						{
							if (logger.isDebugEnabled()) logger.debug("Read -1, stopping...");
							addEvent(null);
							return;
						}
						byte current = (byte) readByte;
						if (current == 0)
						{
							break;
						}
						bytes.add(current);
					}

					if (!bytes.isEmpty())
					{
						byte[] ba = new byte[bytes.size()]; // NOPMD - AvoidInstantiatingObjectsInLoops
						for (int i = 0; i < bytes.size(); i++)
						{
							ba[i] = bytes.get(i);
						}
						bytes.clear();
						T event = decoder.decode(ba);
						addEvent(event);
					}
					else
					{
						if (logger.isDebugEnabled()) logger.debug("bytes.size()==0!!");
					}
				}
			}
			catch (SocketException e)
			{
				// this is thrown when inputStream is closed in close()
				if(logger.isDebugEnabled()) logger.debug("Exception ({}: '{}') while reading events. Adding eventWrapper with empty event and stopping...", e.getClass().getName(), e.getMessage(), e);
				addEvent(null);
			}
			catch (Throwable e)
			{
				// this indicates that something has actually gone wrong
				if(logger.isWarnEnabled()) logger.warn("Exception ({}: '{}') while reading events. Adding eventWrapper with empty event and stopping...", e.getClass().getName(), e.getMessage(), e);
				addEvent(null);
			}
			finally
			{
				close();
			}
		}
	}
}
