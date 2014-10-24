/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
package de.huxhorn.lilith.engine.json.eventproducer;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.json.LoggingJsonDecoder;
import de.huxhorn.lilith.engine.impl.eventproducer.AbstractEventProducer;
import de.huxhorn.lilith.engine.impl.eventproducer.LoggingEventSourceIdentifierUpdater;
import de.huxhorn.sulky.buffers.AppendOperation;

import de.huxhorn.sulky.io.IOUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class LilithJsonStreamLoggingEventProducer
	extends AbstractEventProducer<LoggingEvent>
{
	private final Logger logger = LoggerFactory.getLogger(LilithJsonStreamLoggingEventProducer.class);

	private LoggingJsonDecoder decoder;
	private BufferedInputStream inputStream;

	public LilithJsonStreamLoggingEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<LoggingEvent>> eventQueue, InputStream inputStream)
	{
		super(sourceIdentifier, eventQueue, new LoggingEventSourceIdentifierUpdater());
		decoder=new LoggingJsonDecoder(false);

		this.inputStream = new BufferedInputStream(inputStream);
	}

	public void start()
	{
		Thread t = new Thread(new ReceiverRunnable(), "" + getSourceIdentifier() + "-Receiver");
		t.setDaemon(true);
		t.start();
	}

	public void close()
	{
		IOUtilities.closeQuietly(inputStream);
	}

	private class ReceiverRunnable
		implements Runnable
	{
		public void run()
		{
			try
			{
				ArrayList<Byte> bytes = new ArrayList<Byte>();
				for(; ;)
				{
					for(; ;)
					{
						int readByte = inputStream.read();
						if(readByte == -1)
						{
							if(logger.isDebugEnabled()) logger.debug("Read -1, stopping...");
							return;
						}
						byte current = (byte) readByte;
						if(current == 0)
						{
							break;
						}
						bytes.add(current);
					}

					if(bytes.size() > 0)
					{
						byte[] ba = new byte[bytes.size()];
						for(int i = 0; i < bytes.size(); i++)
						{
							ba[i] = bytes.get(i);
						}
						bytes.clear();
						LoggingEvent event = decoder.decode(ba);
						addEvent(event);
					}
					else
					{
						if(logger.isDebugEnabled()) logger.debug("bytes.size()==0!!");
					}
				}
			}
			catch(Throwable e)
			{
				if(logger.isDebugEnabled()) logger.debug("Exception ({}: '{}') while reading events. Adding eventWrapper with empty event and stopping...", e.getClass().getName(), e.getMessage(), e);
				addEvent(null);
				IOUtilities.interruptIfNecessary(e);
			}
			finally
			{
				close();
			}
		}
	}
}
