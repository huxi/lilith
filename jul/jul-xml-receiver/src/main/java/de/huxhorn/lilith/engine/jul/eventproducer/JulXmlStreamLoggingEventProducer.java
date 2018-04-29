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

package de.huxhorn.lilith.engine.jul.eventproducer;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.impl.eventproducer.AbstractEventProducer;
import de.huxhorn.lilith.engine.impl.eventproducer.LoggingEventSourceIdentifierUpdater;
import de.huxhorn.lilith.jul.xml.LoggingEventReader;
import de.huxhorn.sulky.buffers.AppendOperation;
import de.huxhorn.sulky.formatting.ReplaceInvalidXmlCharacterReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JulXmlStreamLoggingEventProducer
	extends AbstractEventProducer<LoggingEvent>
{
	// thread-safe, see http://www.cowtowncoder.com/blog/archives/2006/06/entry_2.html
	// XMLInputFactory.newFactory() is not deprecated. See http://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8183519
	@SuppressWarnings("deprecation")
	static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newFactory();
	static
	{
		XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		XML_INPUT_FACTORY.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_VALIDATING, false);
	}

	private final Logger logger = LoggerFactory.getLogger(JulXmlStreamLoggingEventProducer.class);

	private final LoggingEventReader loggingEventReader;
	private final BufferedInputStream inputStream;

	public JulXmlStreamLoggingEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<LoggingEvent>> eventQueue, InputStream inputStream)
	{
		super(sourceIdentifier, eventQueue, new LoggingEventSourceIdentifierUpdater());
		this.loggingEventReader = new LoggingEventReader();
		this.inputStream = new BufferedInputStream(inputStream);
	}

	@Override
	public void start()
	{
		Thread t = new Thread(new ReceiverRunnable(), getSourceIdentifier().toString() + "-Receiver");
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
				XMLStreamReader reader = XML_INPUT_FACTORY.createXMLStreamReader(new ReplaceInvalidXmlCharacterReader(
						new InputStreamReader(inputStream, StandardCharsets.UTF_8))
				);
				for(;;)
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
			}
			finally
			{
				close();
			}
		}
	}
}
