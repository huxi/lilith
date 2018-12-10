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

package de.huxhorn.lilith.log4j.xml;

import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		String eventString = "<log4j:event logger=\"de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass\" timestamp=\"1234567890000\" level=\"DEBUG\" thread=\"main\">\n" +
			"<log4j:message><![CDATA[Foo!]]></log4j:message>\n" +
			"<log4j:NDC><![CDATA[NDC1 NDC2]]></log4j:NDC>\n" +
			"<log4j:throwable><![CDATA[java.lang.RuntimeException: Hello\n" +
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.execute(Log4jSandbox.java:18)\n" +
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox.main(Log4jSandbox.java:38)\n" +
			"Caused by: java.lang.RuntimeException: Hi.\n" +
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.foobar(Log4jSandbox.java:24)\n" +
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.execute(Log4jSandbox.java:14)\n" +
			"\t... 1 more\n" +
			"]]></log4j:throwable>\n" +
			"<log4j:locationInfo class=\"de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass\" method=\"execute\" file=\"Log4jSandbox.java\" line=\"18\"/>\n" +
			"<log4j:properties>\n" +
			"<log4j:data name=\"key1\" value=\"value1\"/>\n" +
			"<log4j:data name=\"key2\" value=\"value2\"/>\n" +
			"</log4j:properties>\n" +
			"</log4j:event>";
		LoggingEvent readEvent = read(eventString);
		logEvent(readEvent);

		// Logger
		assertEquals("de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass", readEvent.getLogger());

		// TimeStamp
		assertEquals((Long) 1_234_567_890_000L, readEvent.getTimeStamp());

		// Level
		assertEquals(LoggingEvent.Level.DEBUG, readEvent.getLevel());

		// Message
		assertEquals(new Message("Foo!"), readEvent.getMessage());

		// MDC
		{
			Map<String, String> expectedMdc = new HashMap<>();
			expectedMdc.put("key1", "value1");
			expectedMdc.put("key2", "value2");
			assertEquals(expectedMdc, readEvent.getMdc());
		}

		// NDC
		{
			Message[] expectedNdc = new Message[]
				{
					new Message("NDC1"),
					new Message("NDC2"),
				};
			assertArrayEquals(expectedNdc, readEvent.getNdc());
		}

		// call stack
		{
			ExtendedStackTraceElement[] expectedCallStack = new ExtendedStackTraceElement[]
				{
					new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass", "execute", "Log4jSandbox.java", 18),
				};
			assertArrayEquals(expectedCallStack, readEvent.getCallStack());
		}

		// thread info
		{
			ThreadInfo threadInfo = new ThreadInfo();
			threadInfo.setName("main");
			assertEquals(threadInfo, readEvent.getThreadInfo());
		}

		// ThrowableInfo
		{
			ThrowableInfo throwableInfo = new ThrowableInfo();
			throwableInfo.setName("java.lang.RuntimeException");
			throwableInfo.setMessage("Hello");
			throwableInfo.setStackTrace(new ExtendedStackTraceElement[]{
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass", "execute", "Log4jSandbox.java", 18),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox", "main", "Log4jSandbox.java", 38),
			});

			ThrowableInfo cause = new ThrowableInfo();
			cause.setName("java.lang.RuntimeException");
			cause.setMessage("Hi.");
			cause.setStackTrace(new ExtendedStackTraceElement[]{
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass", "foobar", "Log4jSandbox.java", 24),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass", "execute", "Log4jSandbox.java", 14),
			});
			cause.setOmittedElements(1);

			throwableInfo.setCause(cause);
			ThrowableInfo actual = readEvent.getThrowable();
			if(logger.isInfoEnabled()) logger.info("Expected: {}", throwableInfo.toString());
			if(logger.isInfoEnabled()) logger.info("Actual  : {}", actual.toString());
			assertEquals(throwableInfo, readEvent.getThrowable());
		}
	}

	@Test
	public void singleThrowable()
		throws XMLStreamException
	{
		String eventString = "<log4j:event logger=\"de.huxhorn.lilith.sandbox.Log4jSandbox\" timestamp=\"1234567890000\" level=\"DEBUG\" thread=\"main\">\n" +
			"<log4j:message><![CDATA[Foobar!]]></log4j:message>\n" +
			"<log4j:NDC><![CDATA[NDC1 NDC2 NDC with spaces...]]></log4j:NDC>\n" +
			"<log4j:throwable><![CDATA[java.lang.Throwable\n" +
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox.main(Log4jSandbox.java:37)\n" +
			"]]></log4j:throwable>\n" +
			"<log4j:locationInfo class=\"de.huxhorn.lilith.sandbox.Log4jSandbox\" method=\"main\" file=\"Log4jSandbox.java\" line=\"37\"/>\n" +
			"<log4j:properties>\n" +
			"<log4j:data name=\"key1\" value=\"value1\"/>\n" +
			"<log4j:data name=\"key2\" value=\"value2\"/>\n" +
			"</log4j:properties>\n" +
			"</log4j:event>";

		LoggingEvent readEvent = read(eventString);
		logEvent(readEvent);

		// Logger
		assertEquals("de.huxhorn.lilith.sandbox.Log4jSandbox", readEvent.getLogger());

		// TimeStamp
		assertEquals((Long) 1_234_567_890_000L, readEvent.getTimeStamp());

		// Level
		assertEquals(LoggingEvent.Level.DEBUG, readEvent.getLevel());

		// Message
		assertEquals(new Message("Foobar!"), readEvent.getMessage());

		// MDC
		{
			Map<String, String> expectedMdc = new HashMap<>();
			expectedMdc.put("key1", "value1");
			expectedMdc.put("key2", "value2");
			assertEquals(expectedMdc, readEvent.getMdc());
		}

		// NDC
		{
			Message[] expectedNdc = new Message[]
				{
					new Message("NDC1"),
					new Message("NDC2"),
					new Message("NDC"),
					new Message("with"),
					new Message("spaces..."),
				};
			assertArrayEquals(expectedNdc, readEvent.getNdc());
		}

		// call stack
		{
			ExtendedStackTraceElement[] expectedCallStack = new ExtendedStackTraceElement[]
				{
					new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox", "main", "Log4jSandbox.java", 37),
				};
			assertArrayEquals(expectedCallStack, readEvent.getCallStack());
		}

		// thread info
		{
			ThreadInfo threadInfo = new ThreadInfo();
			threadInfo.setName("main");
			assertEquals(threadInfo, readEvent.getThreadInfo());
		}

		// ThrowableInfo
		{
			ThrowableInfo throwableInfo = new ThrowableInfo();
			throwableInfo.setName("java.lang.Throwable");
			throwableInfo.setMessage(null);
			throwableInfo.setStackTrace(new ExtendedStackTraceElement[]{
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox", "main", "Log4jSandbox.java", 37),
			});

			ThrowableInfo actual = readEvent.getThrowable();
			if(logger.isInfoEnabled()) logger.info("Expected: {}", throwableInfo.toString());
			if(logger.isInfoEnabled()) logger.info("Actual  : {}", actual.toString());
			assertEquals(throwableInfo, actual);
		}
	}

	@Test
	public void multiLineMessage()
		throws XMLStreamException
	{
		String eventString = "<log4j:event logger=\"de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass\" timestamp=\"1234567890000\" level=\"DEBUG\" thread=\"main\">\n" +
			"<log4j:message><![CDATA[Foo!]]></log4j:message>\n" +
			"<log4j:NDC><![CDATA[NDC1 NDC2 NDC with spaces...]]></log4j:NDC>\n" +
			"<log4j:throwable><![CDATA[java.lang.RuntimeException: Multi\n" +
			"line\n" +
			"message\n" +
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.execute(Log4jSandbox.java:28)\n" +
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox.main(Log4jSandbox.java:51)\n" +
			"Caused by: java.lang.RuntimeException: Hi.\n" +
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.foobar(Log4jSandbox.java:35)\n" +
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.execute(Log4jSandbox.java:24)\n" +
			"\t... 1 more\n" +
			"]]></log4j:throwable>\n" +
			"<log4j:locationInfo class=\"de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass\" method=\"execute\" file=\"Log4jSandbox.java\" line=\"29\"/>\n" +
			"<log4j:properties>\n" +
			"<log4j:data name=\"key1\" value=\"value1\"/>\n" +
			"<log4j:data name=\"key2\" value=\"value2\"/>\n" +
			"</log4j:properties>\n" +
			"</log4j:event>";

		LoggingEvent readEvent = read(eventString);
		logEvent(readEvent);

		// Logger
		assertEquals("de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass", readEvent.getLogger());

		// TimeStamp
		assertEquals((Long) 1_234_567_890_000L, readEvent.getTimeStamp());

		// Level
		assertEquals(LoggingEvent.Level.DEBUG, readEvent.getLevel());

		// Message
		assertEquals(new Message("Foo!"), readEvent.getMessage());

		// MDC
		{
			Map<String, String> expectedMdc = new HashMap<>();
			expectedMdc.put("key1", "value1");
			expectedMdc.put("key2", "value2");
			assertEquals(expectedMdc, readEvent.getMdc());
		}

		// NDC
		{
			Message[] expectedNdc = new Message[]
				{
					new Message("NDC1"),
					new Message("NDC2"),
					new Message("NDC"),
					new Message("with"),
					new Message("spaces..."),
				};
			assertArrayEquals(expectedNdc, readEvent.getNdc());
		}

		// call stack
		{
			ExtendedStackTraceElement[] expectedCallStack = new ExtendedStackTraceElement[]
				{
					new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass", "execute", "Log4jSandbox.java", 29),
				};
			assertArrayEquals(expectedCallStack, readEvent.getCallStack());
		}

		// thread info
		{
			ThreadInfo threadInfo = new ThreadInfo();
			threadInfo.setName("main");
			assertEquals(threadInfo, readEvent.getThreadInfo());
		}

		// ThrowableInfo
		{
			ThrowableInfo throwableInfo = new ThrowableInfo();
			throwableInfo.setName("java.lang.RuntimeException");
			throwableInfo.setMessage("Multi\nline\nmessage");
			throwableInfo.setStackTrace(new ExtendedStackTraceElement[]{
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass", "execute", "Log4jSandbox.java", 28),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox", "main", "Log4jSandbox.java", 51),
			});

			ThrowableInfo cause = new ThrowableInfo();
			cause.setName("java.lang.RuntimeException");
			cause.setMessage("Hi.");
			cause.setStackTrace(new ExtendedStackTraceElement[]{
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass", "foobar", "Log4jSandbox.java", 35),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass", "execute", "Log4jSandbox.java", 24),
			});
			cause.setOmittedElements(1);

			throwableInfo.setCause(cause);
			ThrowableInfo actual = readEvent.getThrowable();
			if(logger.isInfoEnabled()) logger.info("Expected: {}", throwableInfo.toString());
			if(logger.isInfoEnabled()) logger.info("Actual  : {}", actual.toString());
			assertEquals(throwableInfo, readEvent.getThrowable());
		}
	}

	@Test
	public void multiLineMessageWithEmptyLine()
		throws XMLStreamException
	{
		String eventString = "<log4j:event logger=\"de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass\" timestamp=\"1234567890000\" level=\"DEBUG\" thread=\"main\">\n" +
			"<log4j:message><![CDATA[Foo!]]></log4j:message>\n" +
			"<log4j:NDC><![CDATA[NDC1 NDC2 NDC with spaces...]]></log4j:NDC>\n" +
			"<log4j:throwable><![CDATA[java.lang.RuntimeException: Multi\n" +
			"line\n" +
			"message\n" +
			"\n" +
			"after empty line\n" +
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.execute(Log4jSandbox.java:28)\n" +
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox.main(Log4jSandbox.java:51)\n" +
			"Caused by: java.lang.RuntimeException: Hi.\n" +
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.foobar(Log4jSandbox.java:35)\n" +
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.execute(Log4jSandbox.java:24)\n" +
			"\t... 1 more\n" +
			"]]></log4j:throwable>\n" +
			"<log4j:locationInfo class=\"de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass\" method=\"execute\" file=\"Log4jSandbox.java\" line=\"29\"/>\n" +
			"<log4j:properties>\n" +
			"<log4j:data name=\"key1\" value=\"value1\"/>\n" +
			"<log4j:data name=\"key2\" value=\"value2\"/>\n" +
			"</log4j:properties>\n" +
			"</log4j:event>";

		LoggingEvent readEvent = read(eventString);
		logEvent(readEvent);

		// Logger
		assertEquals("de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass", readEvent.getLogger());

		// TimeStamp
		assertEquals((Long) 1_234_567_890_000L, readEvent.getTimeStamp());

		// Level
		assertEquals(LoggingEvent.Level.DEBUG, readEvent.getLevel());

		// Message
		assertEquals(new Message("Foo!"), readEvent.getMessage());

		// MDC
		{
			Map<String, String> expectedMdc = new HashMap<>();
			expectedMdc.put("key1", "value1");
			expectedMdc.put("key2", "value2");
			assertEquals(expectedMdc, readEvent.getMdc());
		}

		// NDC
		{
			Message[] expectedNdc = new Message[]
				{
					new Message("NDC1"),
					new Message("NDC2"),
					new Message("NDC"),
					new Message("with"),
					new Message("spaces..."),
				};
			assertArrayEquals(expectedNdc, readEvent.getNdc());
		}

		// call stack
		{
			ExtendedStackTraceElement[] expectedCallStack = new ExtendedStackTraceElement[]
				{
					new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass", "execute", "Log4jSandbox.java", 29),
				};
			assertArrayEquals(expectedCallStack, readEvent.getCallStack());
		}

		// thread info
		{
			ThreadInfo threadInfo = new ThreadInfo();
			threadInfo.setName("main");
			assertEquals(threadInfo, readEvent.getThreadInfo());
		}

		// ThrowableInfo
		{
			ThrowableInfo throwableInfo = new ThrowableInfo();
			throwableInfo.setName("java.lang.RuntimeException");
			throwableInfo.setMessage("Multi\nline\nmessage\n\nafter empty line");
			throwableInfo.setStackTrace(new ExtendedStackTraceElement[]{
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass", "execute", "Log4jSandbox.java", 28),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox", "main", "Log4jSandbox.java", 51),
			});

			ThrowableInfo cause = new ThrowableInfo();
			cause.setName("java.lang.RuntimeException");
			cause.setMessage("Hi.");
			cause.setStackTrace(new ExtendedStackTraceElement[]{
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass", "foobar", "Log4jSandbox.java", 35),
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass", "execute", "Log4jSandbox.java", 24),
			});
			cause.setOmittedElements(1);

			throwableInfo.setCause(cause);
			ThrowableInfo actual = readEvent.getThrowable();
			if(logger.isInfoEnabled()) logger.info("Expected: {}", throwableInfo.toString());
			if(logger.isInfoEnabled()) logger.info("Actual  : {}", actual.toString());
			assertEquals(throwableInfo, readEvent.getThrowable());
		}
	}

	private LoggingEvent read(String eventStr)
		throws XMLStreamException
	{
		if(logger.isDebugEnabled()) logger.debug("Before change: {}", eventStr);
		if(!eventStr.contains("xmlns:log4j=\"http://jakarta.apache.org/log4j/\""))
		{
			eventStr = eventStr
				.replace("<log4j:event ", "<log4j:event xmlns:log4j=\"http://jakarta.apache.org/log4j/\" ");
			if(logger.isDebugEnabled()) logger.debug("After change: {}", eventStr);
		}
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
