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
package de.huxhorn.lilith.data.logging;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertFalse;
import static de.huxhorn.sulky.junit.JUnitTools.testSerialization;
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization;

import java.io.IOException;
import java.util.*;

public class LoggingEventTest
{
	private LoggingEvent fresh;

	@Before
	public void initFresh()
	{
		fresh = new LoggingEvent();
	}

	@Test
	public void defaultConstructor() throws ClassNotFoundException, IOException
	{
		LoggingEvent instance=new LoggingEvent();

		testSerialization(instance);
		testXmlSerialization(instance);
	}

	@Test
	public void applicationIdentifier() throws ClassNotFoundException, IOException
	{
		LoggingEvent instance=new LoggingEvent();

		String value="value";
		instance.setApplicationIdentifier(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getApplicationIdentifier());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getApplicationIdentifier());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void logger() throws ClassNotFoundException, IOException
	{
		LoggingEvent instance=new LoggingEvent();

		String value="value";
		instance.setLogger(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getLogger());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getLogger());
			assertFalse(fresh.equals(obj));
		}
	}


	@Test
	public void arguments() throws ClassNotFoundException, IOException
	{
		LoggingEvent instance=new LoggingEvent();

		String value[]={"value"};
		instance.setArguments(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertArrayEquals(value, obj.getArguments());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertArrayEquals(value, obj.getArguments());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void level() throws ClassNotFoundException, IOException
	{
		LoggingEvent instance=new LoggingEvent();

		LoggingEvent.Level value= LoggingEvent.Level.ERROR;
		instance.setLevel(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertSame(value, obj.getLevel());
			assertFalse(fresh.equals(obj));
		}
		{
			// http://weblogs.java.net/blog/malenkov/archive/2006/08/how_to_encode_e.html
			LoggingEvent obj = testXmlSerialization(instance, LoggingEvent.Level.class);
			assertSame(value, obj.getLevel());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void messagePattern() throws ClassNotFoundException, IOException
	{
		LoggingEvent instance=new LoggingEvent();

		String value="value";
		instance.setMessagePattern(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getMessagePattern());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getMessagePattern());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void threadName() throws ClassNotFoundException, IOException
	{
		LoggingEvent instance=new LoggingEvent();

		String value="value";
		instance.setThreadName(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getThreadName());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getThreadName());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void throwable() throws ClassNotFoundException, IOException
	{
		LoggingEvent instance=new LoggingEvent();

		ThrowableInfo value=new ThrowableInfo();
		instance.setThrowable(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getThrowable());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getThrowable());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void timeStamp() throws ClassNotFoundException, IOException
	{
		LoggingEvent instance=new LoggingEvent();

		Date value=new Date();
		instance.setTimeStamp(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getTimeStamp());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getTimeStamp());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void callStack() throws ClassNotFoundException, IOException
	{
		LoggingEvent instance=new LoggingEvent();

		ExtendedStackTraceElement[] value=new ExtendedStackTraceElement[]{new ExtendedStackTraceElement()};
		instance.setCallStack(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertArrayEquals(value, obj.getCallStack());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertArrayEquals(value, obj.getCallStack());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void marker() throws ClassNotFoundException, IOException
	{
		LoggingEvent instance=new LoggingEvent();

		Marker value=new Marker();
		instance.setMarker(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getMarker());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getMarker());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void mdc() throws ClassNotFoundException, IOException
	{
		LoggingEvent instance=new LoggingEvent();

		Map<String, String> value=new HashMap<String, String>();
		value.put("foo", "bar");
		
		instance.setMdc(value);

		{
			LoggingEvent obj = testSerialization(instance);
			assertEquals(value, obj.getMdc());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggingEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getMdc());
			assertFalse(fresh.equals(obj));
		}
	}

    @Test
    public void ndc() throws ClassNotFoundException, IOException
    {
        LoggingEvent instance=new LoggingEvent();

        List<Message> value=new ArrayList<Message>();
        value.add(new Message("pattern", new String[]{"foo", "bar"}));

        instance.setNdc(value);

        {
            LoggingEvent obj = testSerialization(instance);
            assertEquals(value, obj.getNdc());
            assertFalse(fresh.equals(obj));
        }
        {
            LoggingEvent obj = testXmlSerialization(instance);
            assertEquals(value, obj.getNdc());
            assertFalse(fresh.equals(obj));
        }
    }
}
