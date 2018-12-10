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

package de.huxhorn.lilith.jul.xml;

import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoggingEventReaderTest
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

	private final Logger logger = LoggerFactory.getLogger(LoggingEventReaderTest.class);
	private LoggingEventReader instance;

	@Before
	public void setUp()
	{
		instance = new LoggingEventReader();
	}

	@Test
	public void correctInputFactoryIsObtained()
	{
		String factoryClassName = XML_INPUT_FACTORY.getClass().getName();
		assertTrue(factoryClassName, factoryClassName.startsWith("com.ctc.wstx.stax"));
	}

	@Test
	public void full()
		throws XMLStreamException
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
		assertEquals((Long) 1_234_567_890_000L, readEvent.getTimeStamp());

		// Level
		assertEquals(LoggingEvent.Level.WARN, readEvent.getLevel());

		// Message
		assertEquals(new Message("Foo!"), readEvent.getMessage());

		// call stack
		{
			ExtendedStackTraceElement[] expectedCallStack = new ExtendedStackTraceElement[]
				{
					new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox$InnerClass", "execute", null, -1),
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
			if(logger.isInfoEnabled()) logger.info("Expected: {}", throwableInfo.toString());
			if(logger.isInfoEnabled()) logger.info("Actual  : {}", actual.toString());
			assertEquals(throwableInfo, readEvent.getThrowable());
		}
	}

	@Test
	public void fullWith3rdPartyLevel()
		throws XMLStreamException
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
		assertEquals((Long) 1_234_567_890_000L, readEvent.getTimeStamp());

		// Level
		assertEquals(LoggingEvent.Level.ERROR, readEvent.getLevel());

		// Message
		assertEquals(new Message("Foo!"), readEvent.getMessage());

		// call stack
		{
			ExtendedStackTraceElement[] expectedCallStack = new ExtendedStackTraceElement[]
				{
					new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox$InnerClass", "execute", null, -1),
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
			if(logger.isInfoEnabled()) logger.info("Expected: {}", throwableInfo.toString());
			if(logger.isInfoEnabled()) logger.info("Actual  : {}", actual.toString());
			assertEquals(throwableInfo, readEvent.getThrowable());
		}
	}

	@Test
	public void fullWithoutExceptionMessage()
		throws XMLStreamException
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
		assertEquals((Long) 1_234_567_890_000L, readEvent.getTimeStamp());

		// Level
		assertEquals(LoggingEvent.Level.WARN, readEvent.getLevel());

		// Message
		assertEquals(new Message("Foo!"), readEvent.getMessage());

		// call stack
		{
			ExtendedStackTraceElement[] expectedCallStack = new ExtendedStackTraceElement[]
				{
					new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox$InnerClass", "execute", null, -1),
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
			if(logger.isInfoEnabled()) logger.info("Expected: {}", throwableInfo.toString());
			if(logger.isInfoEnabled()) logger.info("Actual  : {}", actual.toString());
			assertEquals(throwableInfo, readEvent.getThrowable());
		}
	}

	@Test
	public void fullWithIgnoredKeyCatalogParams()
		throws XMLStreamException
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
		assertEquals((Long) 1_234_567_890_000L, readEvent.getTimeStamp());

		// Level
		assertEquals(LoggingEvent.Level.WARN, readEvent.getLevel());

		// Message
		assertEquals(new Message("Foo!"), readEvent.getMessage());

		// call stack
		{
			ExtendedStackTraceElement[] expectedCallStack = new ExtendedStackTraceElement[]
				{
					new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.JulSandbox$InnerClass", "execute", null, -1),
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
			if(logger.isInfoEnabled()) logger.info("Expected: {}", throwableInfo.toString());
			if(logger.isInfoEnabled()) logger.info("Actual  : {}", actual.toString());
			assertEquals(throwableInfo, readEvent.getThrowable());
		}
	}

	private LoggingEvent read(String eventStr)
		throws XMLStreamException
	{
		return read((eventStr).getBytes(StandardCharsets.UTF_8));
	}

	private void logEvent(LoggingEvent event)
	{
		if(logger.isInfoEnabled())
		{
			StringBuilder msg = new StringBuilder();
			msg.append("loggingEvent=");
			if(event == null)
			{
				msg.append("null");
			}
			else
			{
				msg.append("[logger=").append(event.getLogger())
						.append(", level=").append(event.getLevel())
						.append(", threadInfo=").append(event.getThreadInfo())
						.append(", timeStamp=").append(event.getTimeStamp())
						.append(", message=").append(event.getMessage());
				appendCallStack(msg, event.getCallStack());
				appendThrowable(msg, event.getThrowable());
				msg.append(", mdc=").append(event.getMdc());
				appendNdc(msg, event.getNdc());

				msg.append(']');
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
		throws XMLStreamException
	{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		XMLStreamReader reader = XML_INPUT_FACTORY.createXMLStreamReader(new InputStreamReader(in, StandardCharsets.UTF_8));
		return instance.read(reader);
	}
}
