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

package de.huxhorn.lilith.data.logging.test;

import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.sulky.junit.LoggingTestBase;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;


public abstract class LoggingEventIOTestBase
	extends LoggingTestBase
{
	private final Logger logger = LoggerFactory.getLogger(LoggingEventIOTestBase.class);

	public LoggingEventIOTestBase(Boolean logging)
	{
		super(logging);
	}

	@Test
	public void minimal()
		throws Throwable
	{
		LoggingEvent event = createMinimalEvent();
		check(event);
	}

	@Test
	public void loggerContext()
		throws Throwable
	{
		LoggingEvent event = createMinimalEvent();
		LoggerContext value = new LoggerContext();
		value.setName("ContextName");
		value.setBirthTime(1_234_567_890_000L);
		Map<String, String> properties = new HashMap<>();
		properties.put("foo", "bar");
		value.setProperties(properties);
		event.setLoggerContext(value);
		check(event);
	}

	@Test
	public void sequenceNumber()
		throws Throwable
	{
		LoggingEvent event = createMinimalEvent();
		Long value = 17L;
		event.setSequenceNumber(value);
		check(event);
	}

	@Test
	public void threadInfo()
		throws Throwable
	{
		LoggingEvent event = createMinimalEvent();
		ThreadInfo threadInfo = new ThreadInfo(17L, "Thread-Name", 42L, "ThreadGroup-Name");
		threadInfo.setPriority(1337);
		event.setThreadInfo(threadInfo);
		check(event);
	}

	@Test
	public void arguments()
		throws Throwable
	{
		LoggingEvent event = createMinimalEvent();
		String[] arguments = new String[]{"arg1", "arg2"};
		event.setMessage(new Message("message", arguments));
		check(event);
	}

	@Test
	public void nullArgument()
		throws Throwable
	{
		LoggingEvent event = createMinimalEvent();
		String[] arguments = new String[]{"arg1", null, "arg3"};
		event.setMessage(new Message("message", arguments));
		check(event);
	}

	@Test
	public void singleThrowable()
		throws Throwable
	{
		LoggingEvent event = createMinimalEvent();
		ThrowableInfo ti = createThrowableInfo("the.exception.class.Name", "Huhu! Exception Message");
		event.setThrowable(ti);
		check(event);
	}

	@Test
	public void multiThrowable()
		throws Throwable
	{
		LoggingEvent event = createMinimalEvent();
		ThrowableInfo ti = createThrowableInfo("the.exception.class.Name", "Huhu! Exception Message");
		ThrowableInfo ti2 = createThrowableInfo("another.exception.class.Name", "Huhu! Another Exception Message");
		ThrowableInfo ti3 = createThrowableInfo("yet.another.exception.class.Name", "Huhu! Yet another Exception Message");
		ti.setCause(ti2);
		ti2.setCause(ti3);
		event.setThrowable(ti);
		check(event);
	}

	@Test
	public void mdc()
		throws Throwable
	{
		LoggingEvent event = createMinimalEvent();
		Map<String, String> mdc = new HashMap<>();
		mdc.put("key1", "value1");
		mdc.put("key2", "value2");
		mdc.put("key3", "value3");
		event.setMdc(mdc);
		check(event);
	}

	@Test
	public void ndc()
		throws Throwable
	{
		LoggingEvent event = createMinimalEvent();
		Message[] ndc = new Message[]{
			new Message("message"),
			new Message("messagePattern {}", new String[]{"foo"}),
		};
		event.setNdc(ndc);
		check(event);
	}

	@Test
	public void singleMarker()
		throws Throwable
	{
		LoggingEvent event = createMinimalEvent();
		Marker marker = new Marker("marker");
		event.setMarker(marker);
		check(event);
	}

	@Test
	public void childMarker()
		throws Throwable
	{
		LoggingEvent event = createMinimalEvent();
		Marker marker = new Marker("marker");
		Marker markerTwo1 = new Marker("markerTwo1");
		Marker markerTwo2 = new Marker("markerTwo2");
		marker.add(markerTwo1);
		marker.add(markerTwo2);
		event.setMarker(marker);
		check(event);
	}

	@Test
	public void recursiveMarker()
		throws Throwable
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
		check(event);
	}

	@Test
	public void callStack()
		throws Throwable
	{
		LoggingEvent event = createMinimalEvent();
		event.setCallStack(createStackTraceElements());
		check(event);
	}

	@Test
	public void full()
		throws Throwable
	{
		if(logger.isInfoEnabled()) logger.info("Full");
		LoggingEvent event = createMinimalEvent();

		event.setSequenceNumber(42L);

		ThreadInfo threadInfo = new ThreadInfo(17L, "Thread-Name", 42L, "ThreadGroup-Name");
		event.setThreadInfo(threadInfo);

		LoggerContext value = new LoggerContext();
		value.setName("ContextName");
		value.setBirthTime(1_234_567_890_000L);
		Map<String, String> propperties = new HashMap<>();
		propperties.put("foo", "bar");
		value.setProperties(propperties);
		event.setLoggerContext(value);

		String[] arguments = new String[]{"arg1", null, "arg3"};
		event.setMessage(new Message("message", arguments));

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
			new Message("message"),
			new Message("messagePattern {}", new String[]{"foo"}),
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
		check(event);
	}

	private static LoggingEvent createMinimalEvent()
	{
		LoggingEvent event = new LoggingEvent();
		event.setLogger("Logger");
		event.setLevel(LoggingEvent.Level.INFO);
		event.setTimeStamp(1_234_567_890_000L);
		return event;
	}

	private static ThrowableInfo createThrowableInfo(String className, String message)
	{
		ThrowableInfo ti = new ThrowableInfo();
		ti.setName(className);
		ti.setMessage(message);
		ti.setStackTrace(createStackTraceElements());
		return ti;
	}

	private static ExtendedStackTraceElement[] createStackTraceElements()
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
			else if(i == 2)
			{
				result[i].setClassLoaderName("ClassLoaderName");
				result[i].setModuleName("ModuleName");
				result[i].setModuleVersion("ModuleVersion");
			}
		}

		return result;
	}

	private void check(LoggingEvent event)
		throws Throwable
	{
		if(logger.isDebugEnabled()) logger.debug("Processing LoggingEvent:\n{}", event);
		byte[] bytes;
		LoggingEvent readEvent;

		bytes = write(event, false);
		readEvent = read(bytes, false);
		if(logger.isInfoEnabled()) logger.info("LoggingEvent read uncompressed. (size={})", bytes.length);
		if(logger.isDebugEnabled()) logger.debug("Original marker: {}", toString(event.getMarker()));
		if(logger.isDebugEnabled()) logger.debug("Read     marker: {}", toString(readEvent.getMarker()));
		logUncompressedData(bytes);
		assertEquals(event, readEvent);

		bytes = write(event, true);
		readEvent = read(bytes, true);
		if(logger.isInfoEnabled()) logger.info("LoggingEvent read compressed. (size={})", bytes.length);
		if(logger.isDebugEnabled()) logger.debug("Original marker: {}", toString(event.getMarker()));
		if(logger.isDebugEnabled()) logger.debug("Read     marker: {}", toString(readEvent.getMarker()));
	}

	protected abstract void logUncompressedData(byte[] bytes);

	private static String toString(Marker marker)
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

	private static void recursiveToString(StringBuilder result, Map<String, Marker> processedMarkers, Marker marker)
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
				if(children != null)
				{
					boolean first = true;
					for (Map.Entry<String, Marker> current : children.entrySet())
					{
						if (first)
						{
							first = false;
						}
						else
						{
							result.append(", ");
						}
						recursiveToString(result, processedMarkers, current.getValue());
					}
				}
				result.append('}');
			}
			result.append(']');
		}
	}

	public abstract byte[] write(LoggingEvent event, boolean compressing)
		throws Throwable;

	public abstract LoggingEvent read(byte[] bytes, boolean compressing)
		throws Throwable;
}
