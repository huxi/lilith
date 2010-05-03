/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.huxhorn.lilith.data.logging.xml;

import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.LoggingEvents;
import de.huxhorn.sulky.stax.IndentingXMLStreamWriter;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class LoggingEventsIOTest
{
	private final Logger logger = LoggerFactory.getLogger(LoggingEventsIOTest.class);
	private LoggingEventsWriter loggingEventWriter;
	private LoggingEventsReader loggingEventReader;

	@Before
	public void setUp()
	{
		loggingEventWriter = new LoggingEventsWriter();
		loggingEventWriter.setWritingSchemaLocation(true);
		loggingEventReader = new LoggingEventsReader();
	}

	@Test
	public void minimal()
		throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingEvents events = createMinimalEvents();
		check(events, true);
	}

	@Test
	public void full()
		throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingEvents events = createMinimalEvents();
		events.setStartIndex(17);
		List<LoggingEvent> eventsList = new ArrayList<LoggingEvent>();
		eventsList.add(createMinimalEvent());
		eventsList.add(createMinimalEvent());
		eventsList.add(createMinimalEvent());
		events.setEvents(eventsList);
		check(events, true);
	}

	@Test
	public void fullPrefix()
		throws XMLStreamException, UnsupportedEncodingException
	{
		loggingEventWriter.setPreferredPrefix("foo");
		LoggingEvents events = createMinimalEvents();
		events.setStartIndex(17);
		List<LoggingEvent> eventsList = new ArrayList<LoggingEvent>();
		eventsList.add(createMinimalEvent());
		eventsList.add(createMinimalEvent());
		eventsList.add(createMinimalEvent());
		events.setEvents(eventsList);
		check(events, true);
	}

	@Test
	public void fullPrefixInverted()
		throws XMLStreamException, UnsupportedEncodingException
	{
		loggingEventWriter.setPreferredPrefix("foo");
		loggingEventWriter.setEventSourcePrefix(null);
		LoggingEvents events = createMinimalEvents();
		events.setStartIndex(17);
		List<LoggingEvent> eventsList = new ArrayList<LoggingEvent>();
		eventsList.add(createMinimalEvent());
		eventsList.add(createMinimalEvent());
		eventsList.add(createMinimalEvent());
		events.setEvents(eventsList);
		check(events, true);
	}

	private void check(LoggingEvents event, boolean indent)
		throws UnsupportedEncodingException, XMLStreamException
	{
		if(logger.isDebugEnabled()) logger.debug("Processing LoggingEvent:\n{}", event);
		byte[] bytes = write(event, indent);
		if(logger.isDebugEnabled()) logger.debug("LoggingEvent marshalled to:\n{}", new String(bytes, "UTF-8"));
		LoggingEvents readEvent = read(bytes);
		if(logger.isDebugEnabled()) logger.debug("LoggingEvent read.");
		assertEquals(event, readEvent);
		if(logger.isDebugEnabled()) logger.debug("LoggingEvents were equal.");
	}


	private LoggingEvents createMinimalEvents()
	{
		LoggingEvents events = new LoggingEvents();
		events.setSource(createMinimalEventSource());
		return events;
	}

	private SourceIdentifier createMinimalEventSource()
	{
		SourceIdentifier result = new SourceIdentifier();
		result.setIdentifier("primary");
		return result;
	}

	private LoggingEvent createMinimalEvent()
	{
		LoggingEvent event = new LoggingEvent();
		event.setLogger("Logger");
		event.setLevel(LoggingEvent.Level.INFO);
		event.setTimeStamp(1234567890000L);
		return event;
	}

	private byte[] write(LoggingEvents event, boolean indent)
		throws XMLStreamException, UnsupportedEncodingException
	{
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLStreamWriter writer = outputFactory.createXMLStreamWriter(new OutputStreamWriter(out, "utf-8"));
		if(indent && writer.getClass().getName().equals("com.bea.xml.stream.XMLWriterBase"))
		{

			if(logger.isInfoEnabled()) logger.info("Won't indent because of http://jira.codehaus.org/browse/STAX-42");
			indent = false;
		}
		if(indent)
		{
			writer = new IndentingXMLStreamWriter(writer);
		}
		loggingEventWriter.write(writer, event, true);
		writer.flush();
		return out.toByteArray();
	}

	private LoggingEvents read(byte[] bytes)
		throws XMLStreamException, UnsupportedEncodingException
	{
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();

		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		XMLStreamReader reader = inputFactory.createXMLStreamReader(new InputStreamReader(in, "utf-8"));
		return loggingEventReader.read(reader);
	}
}
