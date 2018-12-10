/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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

/*
 * Copyright 2007-2018 Joern Huxhorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.huxhorn.lilith.data.logging.xml;

import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.sulky.stax.IndentingXMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoggingEventIOTest
{
	// thread-safe, see http://www.cowtowncoder.com/blog/archives/2006/06/entry_2.html
	// XMLInputFactory.newFactory() is not deprecated. See http://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8183519
	@SuppressWarnings("deprecation")
	private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newFactory();
	static
	{
		XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		XML_INPUT_FACTORY.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_VALIDATING, false);
	}
	private static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newFactory();

	private final Logger logger = LoggerFactory.getLogger(LoggingEventIOTest.class);
	private LoggingEventWriter loggingEventWriter;
	private LoggingEventReader loggingEventReader;

	@Before
	public void setUp()
	{
		loggingEventWriter = new LoggingEventWriter();
		loggingEventWriter.setWritingSchemaLocation(true);
		loggingEventReader = new LoggingEventReader();
	}

	@Test
	public void correctOutputFactoryIsObtained()
	{
		String factoryClassName = XML_OUTPUT_FACTORY.getClass().getName();
		assertTrue(factoryClassName, factoryClassName.startsWith("com.ctc.wstx.stax"));
	}

	@Test
	public void correctInputFactoryIsObtained()
	{
		String factoryClassName = XML_INPUT_FACTORY.getClass().getName();
		assertTrue(factoryClassName, factoryClassName.startsWith("com.ctc.wstx.stax"));
	}

	@Test
	public void minimal()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		check(event, true);
	}

	@Test
	public void sequenceNumber()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		Long value = 17L;
		event.setSequenceNumber(value);
		check(event, true);
	}

	@Test
	public void loggerContext()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		LoggerContext value = new LoggerContext();
		value.setName("ContextName");
		value.setBirthTime(1_234_567_890_000L);
		Map<String, String> propperties = new HashMap<>();
		propperties.put("foo", "bar");
		value.setProperties(propperties);
		event.setLoggerContext(value);
		check(event, true);
	}

	@Test
	public void loggerContextMissingName()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		LoggerContext value = new LoggerContext();
		value.setBirthTime(1_234_567_890_000L);
		Map<String, String> properties = new HashMap<>();
		properties.put("foo", "bar");
		value.setProperties(properties);
		event.setLoggerContext(value);
		check(event, true);
	}

	@Test
	public void loggerContextMissingBirthTime()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		LoggerContext value = new LoggerContext();
		value.setName("ContextName");
		Map<String, String> propperties = new HashMap<>();
		propperties.put("foo", "bar");
		value.setProperties(propperties);
		event.setLoggerContext(value);
		check(event, true);
	}


	@Test
	public void loggerContextMissingProperties()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		LoggerContext value = new LoggerContext();
		value.setName("ContextName");
		value.setBirthTime(1_234_567_890_000L);
		event.setLoggerContext(value);
		check(event, true);
	}

	@Test
	public void threadInfo()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		ThreadInfo threadInfo = new ThreadInfo(17L, "Thread-Name", 42L, "ThreadGroup-Name");
		event.setThreadInfo(threadInfo);
		check(event, true);
	}

	@Test
	public void arguments()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		String[] arguments = new String[]{"arg1", "arg2", "arg3"};
		event.setMessage(new Message("message", arguments));
		check(event, true);
	}

	@Test
	public void nullArgument()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		String[] arguments = new String[]{"arg1", null, "arg3"};
		event.setMessage(new Message("message", arguments));
		check(event, true);
	}

	@Test
	public void singleThrowable()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		ThrowableInfo ti = createThrowableInfo("the.exception.class.Name", "Huhu! Exception Message");
		event.setThrowable(ti);
		check(event, true);
	}

	@Test
	public void multiThrowable()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		ThrowableInfo ti = createThrowableInfo("the.exception.class.Name", "Huhu! Exception Message");
		ThrowableInfo ti2 = createThrowableInfo("another.exception.class.Name", "Huhu! Exception Message");
		ti2.setOmittedElements(17);
		ThrowableInfo ti3 = createThrowableInfo("yet.another.exception.class.Name", "Huhu! Exception Message");
		ti.setCause(ti2);
		ti2.setCause(ti3);
		event.setThrowable(ti);
		check(event, true);
	}

	@Test
	public void mdc()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		Map<String, String> mdc = new HashMap<>();
		mdc.put("key1", "value1");
		mdc.put("key2", "value2");
		mdc.put("key3", "value3");
		event.setMdc(mdc);
		check(event, true);
	}

	@Test
	public void ndc()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		Message[] ndc = new Message[]{
			new Message("Pattern 1 {} {}", new String[]{"foo", "bar"}),
			new Message("Pattern 2 {} {}", new String[]{"foo", "bar"}),
		};
		event.setNdc(ndc);
		check(event, true);
	}

	@Test
	public void singleMarker()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		Marker marker = new Marker("marker");
		event.setMarker(marker);
		check(event, true);
	}

	@Test
	public void referenceMarker()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		Marker marker = new Marker("marker");
		Marker markerTwo1 = new Marker("markerTwo1");
		Marker markerTwo2 = new Marker("markerTwo2");
		marker.add(markerTwo1);
		marker.add(markerTwo2);
		event.setMarker(marker);
		check(event, true);
	}

	/*
	 * This is not supported by SLF4J/Logback at the moment.
	 */
	@Test
	public void recursiveMarker()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		Marker marker = new Marker("marker");
		Marker markerTwo1 = new Marker("markerTwo1");
		Marker markerTwo2 = new Marker("markerTwo2");
		Marker markerThree = new Marker("markerThree");
		marker.add(markerTwo1);
		marker.add(markerTwo2);
		markerTwo2.add(markerThree);
		markerThree.add(markerTwo1);
		event.setMarker(marker);
		check(event, true);
	}

	@Test
	public void callStack()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();
		event.setCallStack(createStackTraceElements());
		check(event, true);
	}

	@Test
	public void full()
		throws XMLStreamException
	{
		LoggingEvent event = createMinimalEvent();

		ThreadInfo threadInfo = new ThreadInfo(17L, "Thread-Name", 42L, "ThreadGroup-Name");
		event.setThreadInfo(threadInfo);

		Message value = new Message("pattern", new String[]{"arg1", null, "arg3"});
		event.setMessage(value);

		ThrowableInfo ti = createThrowableInfo("the.exception.class.Name", "Huhu! Exception Message");
		ThrowableInfo ti2 = createThrowableInfo("another.exception.class.Name", "Huhu! Exception Message");
		{
			ThrowableInfo sup1 = createThrowableInfo("yet.another.exception.class.Name", "Suppressed1");
			ThrowableInfo sup2 = createThrowableInfo("yet.another.exception.class.Name", "Suppressed2");
			ThrowableInfo sup3 = createThrowableInfo("yet.another.exception.class.Name", "Suppressed3");
			ThrowableInfo[] sup = new ThrowableInfo[3];
			sup[0] = sup1;
			sup[1] = sup2;
			sup[2] = sup3;
			ti2.setSuppressed(sup);
		}

		ThrowableInfo ti3 = createThrowableInfo("yet.another.exception.class.Name", "Huhu! Exception Message");
		ti.setCause(ti2);
		ti2.setCause(ti3);
		event.setThrowable(ti);

		Map<String, String> mdc = new HashMap<>();
		mdc.put("key1", "value1");
		mdc.put("key2", "value2");
		mdc.put("key3", "value3");
		event.setMdc(mdc);

		Message[] ndc = new Message[]{
			new Message("Pattern 1 {} {}", new String[]{"foo", "bar"}),
			new Message("Pattern 2 {} {}", new String[]{"foo", "bar"}),
		};
		event.setNdc(ndc);

		Marker marker = new Marker("marker");
		Marker markerTwo1 = new Marker("markerTwo1");
		Marker markerTwo2 = new Marker("markerTwo2");
		Marker markerThree = new Marker("markerThree");
		marker.add(markerTwo1);
		marker.add(markerTwo2);
		markerTwo2.add(markerThree);
		markerThree.add(markerTwo1);
		event.setMarker(marker);

		event.setCallStack(createStackTraceElements());
		check(event, true);
	}

	@Test
	public void fullWithPrefix()
		throws XMLStreamException
	{
		loggingEventWriter.setPreferredPrefix("foo");
		loggingEventWriter.setWritingSchemaLocation(true);
		LoggingEvent event = createMinimalEvent();

		ThreadInfo threadInfo = new ThreadInfo(17L, "Thread-Name", 42L, "ThreadGroup-Name");
		event.setThreadInfo(threadInfo);

		Message value = new Message("pattern", new String[]{"arg1", null, "arg3"});
		event.setMessage(value);

		ThrowableInfo ti = createThrowableInfo("the.exception.class.Name", "Huhu! Exception Message");
		ThrowableInfo ti2 = createThrowableInfo("another.exception.class.Name", "Huhu! Exception Message");
		ThrowableInfo ti3 = createThrowableInfo("yet.another.exception.class.Name", "Huhu! Exception Message");
		ti.setCause(ti2);
		ti2.setCause(ti3);
		event.setThrowable(ti);

		Map<String, String> mdc = new HashMap<>();
		mdc.put("key1", "value1");
		mdc.put("key2", "value2");
		mdc.put("key3", "value3");
		event.setMdc(mdc);

		Message[] ndc = new Message[]{
			new Message("Pattern 1 {} {}", new String[]{"foo", "bar"}),
			new Message("Pattern 2 {} {}", new String[]{"foo", "bar"}),
		};
		event.setNdc(ndc);

		Marker marker = new Marker("marker");
		Marker markerTwo1 = new Marker("markerTwo1");
		Marker markerTwo2 = new Marker("markerTwo2");
		Marker markerThree = new Marker("markerThree");
		marker.add(markerTwo1);
		marker.add(markerTwo2);
		markerTwo2.add(markerThree);
		markerThree.add(markerTwo1);
		event.setMarker(marker);

		event.setCallStack(createStackTraceElements());
		check(event, true);
	}

	private LoggingEvent createMinimalEvent()
	{
		LoggingEvent event = new LoggingEvent();
		event.setLogger("Logger");
		event.setLevel(LoggingEvent.Level.INFO);
		event.setTimeStamp(1_234_567_890_000L);
		return event;
	}

	private ExtendedStackTraceElement[] createStackTraceElements()
	{
		//noinspection ThrowableInstanceNeverThrown
		Throwable t = new Throwable();
		StackTraceElement[] original = t.getStackTrace();

		ExtendedStackTraceElement[] result = new ExtendedStackTraceElement[original.length];
		for(int i = 0; i < original.length; i++)
		{
			StackTraceElement current = original[i];
			result[i] = new ExtendedStackTraceElement(current); // NOPMD - AvoidInstantiatingObjectsInLoops

			if(i == 0)
			{
				// codeLocation, version and exact
				result[i].setCodeLocation("CodeLocation");
				result[i].setVersion("Version");
				result[i].setExact(true);
			}
			else if(i == 1)
			{
				// codeLocation, version and exact
				result[i].setCodeLocation("CodeLocation");
				result[i].setVersion("Version");
				result[i].setExact(false);
			}
		}

		return result;
	}

	private ThrowableInfo createThrowableInfo(String className, String message)
	{
		ThrowableInfo ti = new ThrowableInfo();
		ti.setName(className);
		ti.setMessage(message);
		ti.setStackTrace(createStackTraceElements());
		return ti;
	}

	private void check(LoggingEvent event, boolean indent)
		throws XMLStreamException
	{
		if(logger.isDebugEnabled()) logger.debug("Processing LoggingEvent:\n{}", event);
		byte[] bytes = write(event, indent);
		String eventStr = new String(bytes, StandardCharsets.UTF_8);
		if(logger.isDebugEnabled()) logger.debug("LoggingEvent marshalled to:\n{}", eventStr);
		LoggingEvent readEvent = read(bytes);
		if(logger.isDebugEnabled()) logger.debug("LoggingEvent read.");
		if(logger.isInfoEnabled()) logger.info("Original marker: {}", toString(event.getMarker()));
		if(logger.isInfoEnabled()) logger.info("Read     marker: {}", toString(readEvent.getMarker()));
		assertEquals(event, readEvent);
		if(logger.isDebugEnabled()) logger.debug("LoggingEvents were equal.");
		bytes = write(event, indent);
		String readEventStr = new String(bytes, StandardCharsets.UTF_8);
		assertEquals(eventStr, readEventStr);
		if(logger.isDebugEnabled()) logger.debug("LoggingEvents xml were equal.");
	}

	private byte[] write(LoggingEvent event, boolean indent)
		throws XMLStreamException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLStreamWriter writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
		if(indent)
		{
			writer = new IndentingXMLStreamWriter(writer);
		}
		loggingEventWriter.write(writer, event, true);
		writer.flush();
		return out.toByteArray();
	}

	private LoggingEvent read(byte[] bytes)
		throws XMLStreamException
	{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		XMLStreamReader reader = XML_INPUT_FACTORY.createXMLStreamReader(new InputStreamReader(in, StandardCharsets.UTF_8));
		return loggingEventReader.read(reader);
	}

	private String toString(Marker marker)
	{
		if(marker == null)
		{
			return null;
		}
		StringBuilder result = new StringBuilder();
		Map<String, Marker> processedMarkers = new HashMap<>();
		recursiveToString(result, processedMarkers, marker);
		return result.toString();
	}

	private void recursiveToString(StringBuilder result, Map<String, Marker> processedMarkers, Marker marker)
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
				boolean first = true;
				for(Map.Entry<String, Marker> current : children.entrySet())
				{
					if(first)
					{
						first = false;
					}
					else
					{
						result.append(", ");
					}
					recursiveToString(result, processedMarkers, current.getValue());
				}
				result.append('}');
			}
			result.append(']');
		}
	}
}
