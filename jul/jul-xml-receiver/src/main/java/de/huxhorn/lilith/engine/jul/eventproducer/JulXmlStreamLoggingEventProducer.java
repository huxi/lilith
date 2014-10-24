/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2014 Joern Huxhorn
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
package de.huxhorn.lilith.engine.jul.eventproducer;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.impl.eventproducer.AbstractEventProducer;
import de.huxhorn.lilith.engine.impl.eventproducer.LoggingEventSourceIdentifierUpdater;
import de.huxhorn.lilith.jul.xml.LoggingEventReader;
import de.huxhorn.sulky.buffers.AppendOperation;

import de.huxhorn.sulky.io.IOUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JulXmlStreamLoggingEventProducer
	extends AbstractEventProducer<LoggingEvent>
{
	private final Logger logger = LoggerFactory.getLogger(JulXmlStreamLoggingEventProducer.class);

	private LoggingEventReader loggingEventReader;
	private BufferedInputStream inputStream;

	public JulXmlStreamLoggingEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<LoggingEvent>> eventQueue, InputStream inputStream)
	{
		super(sourceIdentifier, eventQueue, new LoggingEventSourceIdentifierUpdater());
		this.loggingEventReader = new LoggingEventReader();
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
				XMLInputFactory inputFactory = XMLInputFactory.newInstance();
				inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
				inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
				inputFactory.setProperty(XMLInputFactory.IS_VALIDATING, false);

				XMLStreamReader reader = inputFactory.createXMLStreamReader(new InputStreamReader(inputStream, "utf-8"));
				for(; ;)
				{
					try
					{
						LoggingEvent event = loggingEventReader.read(reader);
						if(event == null)
						{
							addEvent(null);
							break;
						}
						addEvent(event);
					}
					catch(XMLStreamException ex)
					{
						if(logger.isWarnEnabled()) logger.warn("Exception while importing...", ex);
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
