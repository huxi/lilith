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

package de.huxhorn.lilith.data.logging;

import de.huxhorn.lilith.data.eventsource.LoggerContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static de.huxhorn.sulky.junit.JUnitTools.testSerialization;
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

public class LoggingEventTest
{
	private LoggingEvent fresh;

	@Before
	public void initFresh()
	{
		fresh = new LoggingEvent();
	}

	@Test
	public void defaultConstructor()
		throws ClassNotFoundException, IOException
	{
		LoggingEvent instance = new LoggingEvent();

		testSerialization(instance);
		testXmlSerialization(instance);
	}

	@Test
	public void loggerContext()
		throws ClassNotFoundException, IOException
	{
		LoggingEvent instance = new LoggingEvent();

		LoggerContext value = new LoggerContext();
		value.setBirthTime(1_234_567_890_000L);
		value.setName("contextName");
		Map<String, String> properties = new HashMap<>();
		properties.put("foo", "bar");
		value.setProperties(properties);
		instance.setLoggerContext(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getLoggerContext());
			assertNotEquals(fresh, obj);
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getLoggerContext());
			assertNotEquals(fresh, obj);
		}
	}

	@Test
	public void logger()
		throws ClassNotFoundException, IOException
	{
		LoggingEvent instance = new LoggingEvent();

		String value = "value";
		instance.setLogger(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getLogger());
			assertNotEquals(fresh, obj);
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getLogger());
			assertNotEquals(fresh, obj);
		}
	}

	@Test
	public void message()
		throws ClassNotFoundException, IOException
	{
		LoggingEvent instance = new LoggingEvent();


		Message value = new Message("pattern", new String[]{"value"});
		instance.setMessage(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getMessage());
			assertNotEquals(fresh, obj);
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getMessage());
			assertNotEquals(fresh, obj);
		}
	}

	@Test
	public void level()
		throws ClassNotFoundException, IOException
	{
		LoggingEvent instance = new LoggingEvent();

		LoggingEvent.Level value = LoggingEvent.Level.ERROR;
		instance.setLevel(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertSame(value, obj.getLevel());
			assertNotEquals(fresh, obj);
		}
		{
			// http://weblogs.java.net/blog/malenkov/archive/2006/08/how_to_encode_e.html
			LoggingEvent obj = testXmlSerialization(instance, LoggingEvent.Level.class);
			assertSame(value, obj.getLevel());
			assertNotEquals(fresh, obj);
		}
	}

	@Test
	public void threadInfo()
		throws ClassNotFoundException, IOException
	{
		LoggingEvent instance = new LoggingEvent();

		ThreadInfo value = new ThreadInfo();
		instance.setThreadInfo(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getThreadInfo());
			assertNotEquals(fresh, obj);
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getThreadInfo());
			assertNotEquals(fresh, obj);
		}
	}

	@Test
	public void throwable()
		throws ClassNotFoundException, IOException
	{
		LoggingEvent instance = new LoggingEvent();

		ThrowableInfo value = new ThrowableInfo();
		instance.setThrowable(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getThrowable());
			assertNotEquals(fresh, obj);
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getThrowable());
			assertNotEquals(fresh, obj);
		}
	}

	@Test
	public void timeStamp()
		throws ClassNotFoundException, IOException
	{
		LoggingEvent instance = new LoggingEvent();

		Long value = 1_234_567_890_000L;
		instance.setTimeStamp(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getTimeStamp());
			assertNotEquals(fresh, obj);
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getTimeStamp());
			assertNotEquals(fresh, obj);
		}
	}

	@Test
	public void sequenceNumber()
		throws ClassNotFoundException, IOException
	{
		LoggingEvent instance = new LoggingEvent();

		Long value = 17L;
		instance.setSequenceNumber(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getSequenceNumber());
			assertNotEquals(fresh, obj);
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getSequenceNumber());
			assertNotEquals(fresh, obj);
		}
	}

	@Test
	public void callStack()
		throws ClassNotFoundException, IOException
	{
		LoggingEvent instance = new LoggingEvent();

		ExtendedStackTraceElement[] value = new ExtendedStackTraceElement[]{new ExtendedStackTraceElement()};
		instance.setCallStack(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertArrayEquals(value, obj.getCallStack());
			assertNotEquals(fresh, obj);
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertArrayEquals(value, obj.getCallStack());
			assertNotEquals(fresh, obj);
		}
	}

	@Test
	public void marker()
		throws ClassNotFoundException, IOException
	{
		LoggingEvent instance = new LoggingEvent();

		Marker value = new Marker();
		instance.setMarker(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getMarker());
			assertNotEquals(fresh, obj);
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getMarker());
			assertNotEquals(fresh, obj);
		}
	}

	@Test
	public void mdc()
		throws ClassNotFoundException, IOException
	{
		LoggingEvent instance = new LoggingEvent();

		Map<String, String> value = new HashMap<>();
		value.put("foo", "bar");

		instance.setMdc(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getMdc());
			assertNotEquals(fresh, obj);
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getMdc());
			assertNotEquals(fresh, obj);
		}
	}

	@Test
	public void ndc()
		throws ClassNotFoundException, IOException
	{
		LoggingEvent instance = new LoggingEvent();

		Message[] value = new Message[]{
			new Message("pattern", new String[]{"foo", "bar"}),
		};

		instance.setNdc(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertArrayEquals(value, obj.getNdc());
			assertNotEquals(fresh, obj);
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertArrayEquals(value, obj.getNdc());
			assertNotEquals(fresh, obj);
		}
	}
}
