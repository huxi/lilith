/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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
package de.huxhorn.lilith.jul.xml;

import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class LoggingEventReaderTest
{
	private final Logger logger = LoggerFactory.getLogger(LoggingEventReaderTest.class);
	private LoggingEventReader instance;

	@Before
	public void setUp()
	{
		instance = new LoggingEventReader();
	}

	@Test
	public void full()
		throws XMLStreamException, UnsupportedEncodingException
	{
		String eventString = "<record>\n" +
			"  <date>2009-03-20T14:06:45</date>\n" +
			"  <millis>1234567890000</millis>\n" +
			"  <sequence>2</sequence>\n" +
			"  <logger>de.huxhorn.lilith.sandbox.JulSandbox$InnerClass</logger>\n" +
			"  <level>WARNING</level>\n" +
			"  <class>de.huxhorn.lilith.sandbox.JulSandbox$InnerClass</class>\n" +
			"  <method>execute</method>\n" +
			"  <thread>10</thread>\n" +
			"  <message>Foo!</message>\n" +
			"  <exception>\n" +
			"    <message>java.lang.RuntimeException: Exception</message>\n" +
			"    <frame>\n" +
			"      <class>de.huxhorn.lilith.sandbox.JulSandbox</class>\n" +
			"      <method>createRuntimeException</method>\n" +
			"      <line>27</line>\n" +
			"    </frame>\n" +
			"    <frame>\n" +
			"      <class>de.huxhorn.lilith.sandbox.JulSandbox</class>\n" +
			"      <method>access$000</method>\n" +
			"      <line>6</line>\n" +
			"    </frame>\n" +
			"    <frame>\n" +
			"      <class>de.huxhorn.lilith.sandbox.JulSandbox$InnerClass</class>\n" +
			"      <method>execute</method>\n" +
			"      <line>14</line>\n" +
			"    </frame>\n" +
			"    <frame>\n" +
			"      <class>de.huxhorn.lilith.sandbox.JulSandbox</class>\n" +
			"      <method>main</method>\n" +
			"      <line>47</line>\n" +
			"    </frame>\n" +
			"  </exception>\n" +
			"</record>";
		LoggingEvent readEvent = read(eventString);
		logEvent(readEvent);

		// Logger
		assertEquals("de.huxhorn.lilith.sandbox.JulSandbox$InnerClass", readEvent.getLogger());

		// TimeStamp
		assertEquals((Long) 1234567890000L, readEvent.getTimeStamp());

		// Level
		assertEquals(LoggingEvent.Level.WARN, readEvent.getLevel());

		// Message
		assertEquals(new Message("Foo!"), readEvent.getMessage());

		// call stack
		{
			ExtendedStackTraceElement[] expectedCallStack = new ExtendedStackTraceElement[]
				{
					new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox$InnerClass", "execute", null, -1)
				};
			assertArrayEquals(expectedCallStack, readEvent.getCallStack());
		}

		// thread info
		{
			ThreadInfo threadInfo = new ThreadInfo();
			threadInfo.setId(10L);
			assertEquals(threadInfo, readEvent.getThreadInfo());
		}

		// ThrowableInfo
		{
			ThrowableInfo throwableInfo = new ThrowableInfo();
			throwableInfo.setName("java.lang.RuntimeException");
			throwableInfo.setMessage("Exception");
			throwableInfo.setStackTrace(new ExtendedStackTraceElement[]{
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox", "createRuntimeException", null, 27),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox", "access$000", null, 6),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox$InnerClass", "execute", null, 14),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox", "main", null, 47),
			});

			ThrowableInfo actual = readEvent.getThrowable();
			if(logger.isInfoEnabled()) logger.info("Expected: {}", throwableInfo.toString(true));
			if(logger.isInfoEnabled()) logger.info("Actual  : {}", actual.toString(true));
			assertEquals(throwableInfo, readEvent.getThrowable());
		}
	}

	@Test
	public void fullWith3rdPartyLevel()
		throws XMLStreamException, UnsupportedEncodingException
	{
		String eventString = "<record>\n" +
			"  <date>2009-03-20T14:06:45</date>\n" +
			"  <millis>1234567890000</millis>\n" +
			"  <sequence>2</sequence>\n" +
			"  <logger>de.huxhorn.lilith.sandbox.JulSandbox$InnerClass</logger>\n" +
			"  <level>" + (Level.WARNING.intValue() + 1) + "</level>\n" +
			"  <class>de.huxhorn.lilith.sandbox.JulSandbox$InnerClass</class>\n" +
			"  <method>execute</method>\n" +
			"  <thread>10</thread>\n" +
			"  <message>Foo!</message>\n" +
			"  <exception>\n" +
			"    <message>java.lang.RuntimeException: Exception</message>\n" +
			"    <frame>\n" +
			"      <class>de.huxhorn.lilith.sandbox.JulSandbox</class>\n" +
			"      <method>createRuntimeException</method>\n" +
			"      <line>27</line>\n" +
			"    </frame>\n" +
			"    <frame>\n" +
			"      <class>de.huxhorn.lilith.sandbox.JulSandbox</class>\n" +
			"      <method>access$000</method>\n" +
			"      <line>6</line>\n" +
			"    </frame>\n" +
			"    <frame>\n" +
			"      <class>de.huxhorn.lilith.sandbox.JulSandbox$InnerClass</class>\n" +
			"      <method>execute</method>\n" +
			"      <line>14</line>\n" +
			"    </frame>\n" +
			"    <frame>\n" +
			"      <class>de.huxhorn.lilith.sandbox.JulSandbox</class>\n" +
			"      <method>main</method>\n" +
			"      <line>47</line>\n" +
			"    </frame>\n" +
			"  </exception>\n" +
			"</record>";
		LoggingEvent readEvent = read(eventString);
		logEvent(readEvent);

		// Logger
		assertEquals("de.huxhorn.lilith.sandbox.JulSandbox$InnerClass", readEvent.getLogger());

		// TimeStamp
		assertEquals((Long) 1234567890000L, readEvent.getTimeStamp());

		// Level
		assertEquals(LoggingEvent.Level.ERROR, readEvent.getLevel());

		// Message
		assertEquals(new Message("Foo!"), readEvent.getMessage());

		// call stack
		{
			ExtendedStackTraceElement[] expectedCallStack = new ExtendedStackTraceElement[]
				{
					new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox$InnerClass", "execute", null, -1)
				};
			assertArrayEquals(expectedCallStack, readEvent.getCallStack());
		}

		// thread info
		{
			ThreadInfo threadInfo = new ThreadInfo();
			threadInfo.setId(10L);
			assertEquals(threadInfo, readEvent.getThreadInfo());
		}

		// ThrowableInfo
		{
			ThrowableInfo throwableInfo = new ThrowableInfo();
			throwableInfo.setName("java.lang.RuntimeException");
			throwableInfo.setMessage("Exception");
			throwableInfo.setStackTrace(new ExtendedStackTraceElement[]{
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox", "createRuntimeException", null, 27),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox", "access$000", null, 6),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox$InnerClass", "execute", null, 14),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox", "main", null, 47),
			});

			ThrowableInfo actual = readEvent.getThrowable();
			if(logger.isInfoEnabled()) logger.info("Expected: {}", throwableInfo.toString(true));
			if(logger.isInfoEnabled()) logger.info("Actual  : {}", actual.toString(true));
			assertEquals(throwableInfo, readEvent.getThrowable());
		}
	}

	@Test
	public void fullWithoutExceptionMessage()
		throws XMLStreamException, UnsupportedEncodingException
	{
		String eventString = "<record>\n" +
			"  <date>2009-03-20T14:06:45</date>\n" +
			"  <millis>1234567890000</millis>\n" +
			"  <sequence>2</sequence>\n" +
			"  <logger>de.huxhorn.lilith.sandbox.JulSandbox$InnerClass</logger>\n" +
			"  <level>WARNING</level>\n" +
			"  <class>de.huxhorn.lilith.sandbox.JulSandbox$InnerClass</class>\n" +
			"  <method>execute</method>\n" +
			"  <thread>10</thread>\n" +
			"  <message>Foo!</message>\n" +
			"  <exception>\n" +
			"    <message>java.lang.RuntimeException</message>\n" +
			"    <frame>\n" +
			"      <class>de.huxhorn.lilith.sandbox.JulSandbox</class>\n" +
			"      <method>createRuntimeException</method>\n" +
			"      <line>27</line>\n" +
			"    </frame>\n" +
			"    <frame>\n" +
			"      <class>de.huxhorn.lilith.sandbox.JulSandbox</class>\n" +
			"      <method>access$000</method>\n" +
			"      <line>6</line>\n" +
			"    </frame>\n" +
			"    <frame>\n" +
			"      <class>de.huxhorn.lilith.sandbox.JulSandbox$InnerClass</class>\n" +
			"      <method>execute</method>\n" +
			"      <line>14</line>\n" +
			"    </frame>\n" +
			"    <frame>\n" +
			"      <class>de.huxhorn.lilith.sandbox.JulSandbox</class>\n" +
			"      <method>main</method>\n" +
			"      <line>47</line>\n" +
			"    </frame>\n" +
			"  </exception>\n" +
			"</record>";
		LoggingEvent readEvent = read(eventString);
		logEvent(readEvent);

		// Logger
		assertEquals("de.huxhorn.lilith.sandbox.JulSandbox$InnerClass", readEvent.getLogger());

		// TimeStamp
		assertEquals((Long) 1234567890000L, readEvent.getTimeStamp());

		// Level
		assertEquals(LoggingEvent.Level.WARN, readEvent.getLevel());

		// Message
		assertEquals(new Message("Foo!"), readEvent.getMessage());

		// call stack
		{
			ExtendedStackTraceElement[] expectedCallStack = new ExtendedStackTraceElement[]
				{
					new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox$InnerClass", "execute", null, -1)
				};
			assertArrayEquals(expectedCallStack, readEvent.getCallStack());
		}

		// thread info
		{
			ThreadInfo threadInfo = new ThreadInfo();
			threadInfo.setId(10L);
			assertEquals(threadInfo, readEvent.getThreadInfo());
		}

		// ThrowableInfo
		{
			ThrowableInfo throwableInfo = new ThrowableInfo();
			throwableInfo.setName("java.lang.RuntimeException");
			throwableInfo.setStackTrace(new ExtendedStackTraceElement[]{
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox", "createRuntimeException", null, 27),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox", "access$000", null, 6),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox$InnerClass", "execute", null, 14),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox", "main", null, 47),
			});

			ThrowableInfo actual = readEvent.getThrowable();
			if(logger.isInfoEnabled()) logger.info("Expected: {}", throwableInfo.toString(true));
			if(logger.isInfoEnabled()) logger.info("Actual  : {}", actual.toString(true));
			assertEquals(throwableInfo, readEvent.getThrowable());
		}
	}

	@Test
	public void fullWithIgnoredKeyCatalogParams()
		throws XMLStreamException, UnsupportedEncodingException
	{
		String eventString = "<record>\n" +
			"  <date>2009-03-20T14:06:45</date>\n" +
			"  <millis>1234567890000</millis>\n" +
			"  <sequence>2</sequence>\n" +
			"  <logger>de.huxhorn.lilith.sandbox.JulSandbox$InnerClass</logger>\n" +
			"  <level>WARNING</level>\n" +
			"  <class>de.huxhorn.lilith.sandbox.JulSandbox$InnerClass</class>\n" +
			"  <method>execute</method>\n" +
			"  <thread>10</thread>\n" +
			"  <message>Foo!</message>\n" +
			"  <key>Key</key>\n" +
			"  <catalog>Catalog</catalog>\n" +
			"  <param>Param1</param>\n" +
			"  <param>Param2</param>\n" +
			"  <exception>\n" +
			"    <message>java.lang.RuntimeException: Exception</message>\n" +
			"    <frame>\n" +
			"      <class>de.huxhorn.lilith.sandbox.JulSandbox</class>\n" +
			"      <method>createRuntimeException</method>\n" +
			"      <line>27</line>\n" +
			"    </frame>\n" +
			"    <frame>\n" +
			"      <class>de.huxhorn.lilith.sandbox.JulSandbox</class>\n" +
			"      <method>access$000</method>\n" +
			"      <line>6</line>\n" +
			"    </frame>\n" +
			"    <frame>\n" +
			"      <class>de.huxhorn.lilith.sandbox.JulSandbox$InnerClass</class>\n" +
			"      <method>execute</method>\n" +
			"      <line>14</line>\n" +
			"    </frame>\n" +
			"    <frame>\n" +
			"      <class>de.huxhorn.lilith.sandbox.JulSandbox</class>\n" +
			"      <method>main</method>\n" +
			"      <line>47</line>\n" +
			"    </frame>\n" +
			"  </exception>\n" +
			"</record>";
		LoggingEvent readEvent = read(eventString);
		logEvent(readEvent);

		// Logger
		assertEquals("de.huxhorn.lilith.sandbox.JulSandbox$InnerClass", readEvent.getLogger());

		// TimeStamp
		assertEquals((Long) 1234567890000L, readEvent.getTimeStamp());

		// Level
		assertEquals(LoggingEvent.Level.WARN, readEvent.getLevel());

		// Message
		assertEquals(new Message("Foo!"), readEvent.getMessage());

		// call stack
		{
			ExtendedStackTraceElement[] expectedCallStack = new ExtendedStackTraceElement[]
				{
					new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox$InnerClass", "execute", null, -1)
				};
			assertArrayEquals(expectedCallStack, readEvent.getCallStack());
		}

		// thread info
		{
			ThreadInfo threadInfo = new ThreadInfo();
			threadInfo.setId(10L);
			assertEquals(threadInfo, readEvent.getThreadInfo());
		}

		// ThrowableInfo
		{
			ThrowableInfo throwableInfo = new ThrowableInfo();
			throwableInfo.setName("java.lang.RuntimeException");
			throwableInfo.setMessage("Exception");
			throwableInfo.setStackTrace(new ExtendedStackTraceElement[]{
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox", "createRuntimeException", null, 27),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox", "access$000", null, 6),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox$InnerClass", "execute", null, 14),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox", "main", null, 47),
			});

			ThrowableInfo actual = readEvent.getThrowable();
			if(logger.isInfoEnabled()) logger.info("Expected: {}", throwableInfo.toString(true));
			if(logger.isInfoEnabled()) logger.info("Actual  : {}", actual.toString(true));
			assertEquals(throwableInfo, readEvent.getThrowable());
		}
	}

	private LoggingEvent read(String eventStr)
		throws XMLStreamException, UnsupportedEncodingException
	{
		return read((eventStr).getBytes("UTF-8"));
	}

	private void logEvent(LoggingEvent event)
	{
		if(logger.isInfoEnabled())
		{
			StringBuilder msg = new StringBuilder();
			msg.append("loggingEvent=");
			if(event == null)
			{
				msg.append((String) null);
			}
			else
			{
				msg.append("[");
				msg.append("logger=").append(event.getLogger());
				msg.append(", level=").append(event.getLevel());
				msg.append(", threadInfo=").append(event.getThreadInfo());
				msg.append(", timeStamp=").append(event.getTimeStamp());
				msg.append(", message=").append(event.getMessage());
				appendCallStack(msg, event.getCallStack());
				appendThrowable(msg, event.getThrowable());
				msg.append(", mdc=").append(event.getMdc());
				appendNdc(msg, event.getNdc());

				msg.append("]");
			}
			logger.info(msg.toString());
		}
	}

	private void appendNdc(StringBuilder msg, Message[] ndc)
	{
		if(ndc != null)
		{
			List<Message> list = Arrays.asList(ndc);
			msg.append(", ndc=").append(list);
		}
	}

	private void appendCallStack(StringBuilder msg, ExtendedStackTraceElement[] callStack)
	{
		if(callStack != null)
		{
			List<ExtendedStackTraceElement> list = Arrays.asList(callStack);
			msg.append(", callStack=").append(list);
		}
	}

	private void appendThrowable(StringBuilder msg, ThrowableInfo throwable)
	{
		if(throwable != null)
		{
			msg.append(", throwable=");
			msg.append(throwable);
		}
	}

	private LoggingEvent read(byte[] bytes)
		throws XMLStreamException, UnsupportedEncodingException
	{
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();

		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		XMLStreamReader reader = inputFactory.createXMLStreamReader(new InputStreamReader(in, "utf-8"));
		return instance.read(reader);
	}
}
