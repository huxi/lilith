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

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
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
import java.util.HashMap;
import java.util.Map;

public class LoggingEventIOTest
	extends TestCase
{
	private final Logger logger = LoggerFactory.getLogger(LoggingEventIOTest.class);
	private XMLOutputFactory outputFactory;
	private LoggingEventWriter loggingEventWriter;
	private XMLInputFactory inputFactory;
	private LoggingEventReader loggingEventReader;

	public void setUp()
	{
		outputFactory = XMLOutputFactory.newInstance();
		inputFactory = XMLInputFactory.newInstance();
		loggingEventWriter=new LoggingEventWriter();
		loggingEventWriter.setWritingSchemaLocation(true);
		loggingEventReader=new LoggingEventReader();
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
		LoggingEvent event=createMinimalEvent();
		check(event, true);
	}

	public void testAppId() throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingEvent event=createMinimalEvent();
		event.setApplicationIdentifier("App");
		check(event, true);
	}

	public void testThreadName() throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingEvent event=createMinimalEvent();
		event.setThreadName("Thread-Name");
		check(event, true);
	}

	public void testArguments() throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingEvent event=createMinimalEvent();
		String[] arguments=new String[]{"arg1", "arg2"};
		event.setArguments(arguments);
		check(event, true);
	}

	public void testNullArguments() throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingEvent event=createMinimalEvent();
		String[] arguments=new String[]{"arg1", null, "arg3"};
		event.setArguments(arguments);
		check(event, true);
	}

	public ThrowableInfo createThrowableInfo(String className, String message)
	{
		ThrowableInfo ti=new ThrowableInfo();
		ti.setName(className);
		ti.setMessage(message);
		ti.setStackTrace(createStackTraceElements());
		return ti;
	}

	public void testSingleThrowable() throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingEvent event=createMinimalEvent();
		ThrowableInfo ti=createThrowableInfo("the.exception.class.Name", "Huhu! Exception Message");
		event.setThrowable(ti);
		check(event, true);
	}

	public void testMultiThrowable() throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingEvent event=createMinimalEvent();
		ThrowableInfo ti=createThrowableInfo("the.exception.class.Name", "Huhu! Exception Message");
		ThrowableInfo ti2=createThrowableInfo("another.exception.class.Name", "Huhu! Exception Message");
		ThrowableInfo ti3=createThrowableInfo("yet.another.exception.class.Name", "Huhu! Exception Message");
		ti.setCause(ti2);
		ti2.setCause(ti3);
		event.setThrowable(ti);
		check(event, true);
	}

	public void testMdc() throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingEvent event=createMinimalEvent();
		Map<String, String> mdc=new HashMap<String, String>();
		mdc.put("key1", "value1");
		mdc.put("key2", "value2");
		mdc.put("key3", "value3");
		event.setMdc(mdc);
		check(event, true);
	}

	public void testSingleMarker() throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingEvent event=createMinimalEvent();
		Marker marker = new Marker("marker");
		event.setMarker(marker);
		check(event, true);
	}

	public void testChildMarker() throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingEvent event=createMinimalEvent();
		Marker marker = new Marker("marker");
		Marker marker2_1 = new Marker("marker2-1");
		Marker marker2_2 = new Marker("marker2-2");
		marker.add(marker2_1);
		marker.add(marker2_2);
		event.setMarker(marker);
		check(event, true);
	}

	public void testRecursiveMarker() throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingEvent event=createMinimalEvent();
		Marker marker = new Marker("marker");
		Marker marker2_1 = new Marker("marker2-1");
		Marker marker2_2 = new Marker("marker2-2");
		Marker marker3_1 = new Marker("marker3-1");
		marker.add(marker2_1);
		marker.add(marker2_2);
		marker2_2.add(marker3_1);
		marker3_1.add(marker2_1);
		event.setMarker(marker);
		check(event, true);
	}

	public ExtendedStackTraceElement[] createStackTraceElements()
	{
		//noinspection ThrowableInstanceNeverThrown
		Throwable t=new Throwable();
		StackTraceElement[] original=t.getStackTrace();

		ExtendedStackTraceElement[] result=new ExtendedStackTraceElement[original.length];
		for(int i=0;i<original.length;i++)
		{
			StackTraceElement current=original[i];
			result[i]=new ExtendedStackTraceElement(current);

			if(i==0)
			{
				// codeLocation, version and exact
				result[i].setCodeLocation("CodeLocation");
				result[i].setVersion("Version");
				result[i].setExact(true);
			}
			else if(i==1)
			{
				// codeLocation, version and exact
				result[i].setCodeLocation("CodeLocation");
				result[i].setVersion("Version");
				result[i].setExact(false);
			}
		}

		return result; 
	}

	public void testCallStack() throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingEvent event=createMinimalEvent();
		event.setCallStack(createStackTraceElements());
		check(event, true);
	}

	public void testFull() throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingEvent event=createMinimalEvent();

		event.setThreadName("Thread-Name");

		String[] arguments=new String[]{"arg1", null, "arg3"};
		event.setArguments(arguments);

		ThrowableInfo ti=createThrowableInfo("the.exception.class.Name", "Huhu! Exception Message");
		ThrowableInfo ti2=createThrowableInfo("another.exception.class.Name", "Huhu! Exception Message");
		ThrowableInfo ti3=createThrowableInfo("yet.another.exception.class.Name", "Huhu! Exception Message");
		ti.setCause(ti2);
		ti2.setCause(ti3);
		event.setThrowable(ti);

		Map<String, String> mdc=new HashMap<String, String>();
		mdc.put("key1", "value1");
		mdc.put("key2", "value2");
		mdc.put("key3", "value3");
		event.setMdc(mdc);

		Marker marker = new Marker("marker");
		Marker marker2_1 = new Marker("marker2-1");
		Marker marker2_2 = new Marker("marker2-2");
		Marker marker3_1 = new Marker("marker3-1");
		marker.add(marker2_1);
		marker.add(marker2_2);
		marker2_2.add(marker3_1);
		marker3_1.add(marker2_1);
		event.setMarker(marker);

		event.setCallStack(createStackTraceElements());
		check(event, true);
	}

	public void testFullPrefix() throws XMLStreamException, UnsupportedEncodingException
	{
		loggingEventWriter.setPreferredPrefix("foo");
		loggingEventWriter.setWritingSchemaLocation(true);
		LoggingEvent event=createMinimalEvent();

		event.setThreadName("Thread-Name");

		String[] arguments=new String[]{"arg1", null, "arg3"};
		event.setArguments(arguments);

		ThrowableInfo ti=createThrowableInfo("the.exception.class.Name", "Huhu! Exception Message");
		ThrowableInfo ti2=createThrowableInfo("another.exception.class.Name", "Huhu! Exception Message");
		ThrowableInfo ti3=createThrowableInfo("yet.another.exception.class.Name", "Huhu! Exception Message");
		ti.setCause(ti2);
		ti2.setCause(ti3);
		event.setThrowable(ti);

		Map<String, String> mdc=new HashMap<String, String>();
		mdc.put("key1", "value1");
		mdc.put("key2", "value2");
		mdc.put("key3", "value3");
		event.setMdc(mdc);

		Marker marker = new Marker("marker");
		Marker marker2_1 = new Marker("marker2-1");
		Marker marker2_2 = new Marker("marker2-2");
		Marker marker3_1 = new Marker("marker3-1");
		marker.add(marker2_1);
		marker.add(marker2_2);
		marker2_2.add(marker3_1);
		marker3_1.add(marker2_1);
		event.setMarker(marker);

		event.setCallStack(createStackTraceElements());
		check(event, true);
	}

	public void check(LoggingEvent event, boolean indent) throws UnsupportedEncodingException, XMLStreamException
	{
		if(logger.isDebugEnabled()) logger.debug("Processing LoggingEvent:\n{}", event);
		byte[] bytes = write(event, indent);
		String eventStr =new String(bytes, "UTF-8");
		if(logger.isDebugEnabled()) logger.debug("LoggingEvent marshalled to:\n{}", eventStr);
		LoggingEvent readEvent = read(bytes);
		if(logger.isDebugEnabled()) logger.debug("LoggingEvent read.");
		if(logger.isInfoEnabled()) logger.info("Original marker: {}", toString(event.getMarker()));
		if(logger.isInfoEnabled()) logger.info("Read     marker: {}", toString(readEvent.getMarker()));
		assertEquals(event, readEvent);
		if(logger.isDebugEnabled()) logger.debug("LoggingEvents were equal.");
		bytes = write(event, indent);
		String readEventStr=new String(bytes, "UTF-8");
		assertEquals(eventStr, readEventStr);
		if(logger.isDebugEnabled()) logger.debug("LoggingEvents xml were equal.");
	}

	public byte[] write(LoggingEvent event, boolean indent) throws XMLStreamException, UnsupportedEncodingException
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

	public LoggingEvent read(byte[] bytes) throws XMLStreamException, UnsupportedEncodingException
	{
		ByteArrayInputStream in=new ByteArrayInputStream(bytes);
		XMLStreamReader reader=inputFactory.createXMLStreamReader(new InputStreamReader(in, "utf-8"));
		return loggingEventReader.read(reader);
	}

	String toString(Marker marker)
	{
		if(marker==null)
		{
			return null;
		}
		StringBuffer result=new StringBuffer();
		Map<String, Marker> processedMarkers=new HashMap<String, Marker>();
		recursiveToString(result, processedMarkers, marker);
		return result.toString();
	}

	private void recursiveToString(StringBuffer result, Map<String, Marker> processedMarkers, Marker marker)
	{
		if(processedMarkers.containsKey(marker.getName()))
		{
			result.append("Marker[ref=").append(marker.getName());
		}
		else
		{
			processedMarkers.put(marker.getName(), marker);
			result.append("Marker[name=").append(marker.getName());
			if(marker.hasReferences())
			{
				result.append(", children={");
				Map<String, Marker> children = marker.getReferences();
				boolean first=true;
				for(Map.Entry<String,Marker> current : children.entrySet())
				{
					if(first)
					{
						first=false;
					}
					else
					{
						result.append(", ");
					}
					recursiveToString(result, processedMarkers, current.getValue());
				}
				result.append("}");
			}
			result.append("]");
		}
	}

//	public void testMulti() throws XMLStreamException, UnsupportedEncodingException
//	{
//		LoggingEvent event=createMinimalEvent();
//		check(event, true);
//	}
}
