/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class LoggingEventsIOTest
	extends TestCase
{
	private final Logger logger = LoggerFactory.getLogger(LoggingEventsIOTest.class);
	private XMLOutputFactory outputFactory;
	private LoggingEventsWriter loggingEventWriter;
	private XMLInputFactory inputFactory;
	private LoggingEventsReader loggingEventReader;

	public void setUp()
	{
		outputFactory = XMLOutputFactory.newInstance();
		inputFactory = XMLInputFactory.newInstance();
		loggingEventWriter=new LoggingEventsWriter();
		loggingEventWriter.setWritingSchemaLocation(true);
		loggingEventReader=new LoggingEventsReader();
	}


	public LoggingEvents createMinimalEvents()
	{
		LoggingEvents events =new LoggingEvents();
		events.setSource(createMinimalEventSource());
		return events;
	}

	public SourceIdentifier createMinimalEventSource()
	{
		SourceIdentifier result=new SourceIdentifier();
		result.setIdentifier("primary");
		return result;
	}

	public LoggingEvent createMinimalEvent()
	{
		LoggingEvent event=new LoggingEvent();
		event.setLogger("Logger");
		event.setLevel(LoggingEvent.Level.INFO);
		event.setTimeStamp(new Date());
		event.setMessagePattern("EventMessage");
		return event;
	}

	public void testMinimal() throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingEvents events=createMinimalEvents();
		check(events, true);
	}

	public void testFull() throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingEvents events=createMinimalEvents();
		events.setStartIndex(17);
		List<LoggingEvent> eventsList=new ArrayList<LoggingEvent>();
		eventsList.add(createMinimalEvent());
		eventsList.add(createMinimalEvent());
		eventsList.add(createMinimalEvent());
		events.setEvents(eventsList);
		check(events, true);
	}

	public void testFullPrefix() throws XMLStreamException, UnsupportedEncodingException
	{
		loggingEventWriter.setPreferredPrefix("foo");
		LoggingEvents events=createMinimalEvents();
		events.setStartIndex(17);
		List<LoggingEvent> eventsList=new ArrayList<LoggingEvent>();
		eventsList.add(createMinimalEvent());
		eventsList.add(createMinimalEvent());
		eventsList.add(createMinimalEvent());
		events.setEvents(eventsList);
		check(events, true);
	}

	public void testFullPrefixInverted() throws XMLStreamException, UnsupportedEncodingException
	{
		loggingEventWriter.setPreferredPrefix("foo");
		loggingEventWriter.setEventSourcePrefix(null);
		LoggingEvents events=createMinimalEvents();
		events.setStartIndex(17);
		List<LoggingEvent> eventsList=new ArrayList<LoggingEvent>();
		eventsList.add(createMinimalEvent());
		eventsList.add(createMinimalEvent());
		eventsList.add(createMinimalEvent());
		events.setEvents(eventsList);
		check(events, true);
	}

	public void check(LoggingEvents event, boolean indent) throws UnsupportedEncodingException, XMLStreamException
	{
		if(logger.isDebugEnabled()) logger.debug("Processing LoggingEvent:\n{}", event);
		byte[] bytes = write(event, indent);
		if(logger.isDebugEnabled()) logger.debug("LoggingEvent marshalled to:\n{}", new String(bytes, "UTF-8"));
		LoggingEvents readEvent = read(bytes);
		if(logger.isDebugEnabled()) logger.debug("LoggingEvent read.");
		assertEquals(event, readEvent);
		if(logger.isDebugEnabled()) logger.debug("LoggingEvents were equal.");
	}

	public byte[] write(LoggingEvents event, boolean indent) throws XMLStreamException, UnsupportedEncodingException
	{
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		XMLStreamWriter writer=outputFactory.createXMLStreamWriter(new OutputStreamWriter(out,"utf-8"));
		if(indent && writer.getClass().getName().equals("com.bea.xml.stream.XMLWriterBase"))
		{

			if(logger.isInfoEnabled()) logger.info("Won't indent because of http://jira.codehaus.org/browse/STAX-42");
			indent=false;
		}
		if(indent)
		{
			writer=new IndentingXMLStreamWriter(writer);
		}
		loggingEventWriter.write(writer, event, true);
		writer.flush();
		return out.toByteArray();
	}

	public LoggingEvents read(byte[] bytes) throws XMLStreamException, UnsupportedEncodingException
	{
		ByteArrayInputStream in=new ByteArrayInputStream(bytes);
		XMLStreamReader reader=inputFactory.createXMLStreamReader(new InputStreamReader(in, "utf-8"));
		return loggingEventReader.read(reader);
	}
}
