/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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
 * Copyright 2007-2017 Joern Huxhorn
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

package de.huxhorn.lilith.data.logging.logback.converter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ThrowableProxy;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LogbackLoggingConverterTest
{
	private static final String INDENT = "  ";
	private final Logger logger = LoggerFactory.getLogger(LogbackLoggingConverterTest.class);

	private LogbackLoggingConverter instance;

	@Before
	public void setUp()
		throws Exception
	{
		instance = new LogbackLoggingConverter();
	}

	@Test
	public void testThrowableProxy()
	{
		Throwable t = produceThrowable();
		ThrowableProxy ti = new ThrowableProxy(t);
		ti.calculatePackagingData();
		ThrowableInfo tinfo = instance.initFromThrowableProxy(ti, true);
		assertEquals("yyy", tinfo.getMessage());
		assertEquals("java.lang.RuntimeException: foo", tinfo.getCause().getMessage());
		assertEquals("foo", tinfo.getCause().getCause().getMessage());
		assertNull(tinfo.getCause().getCause().getCause());
	}

	private Throwable produceThrowable()
	{
		Throwable t;
		try
		{
			try
			{
				try
				{
					throw new RuntimeException("foo"); // NOPMD
				}
				catch(Throwable x)
				{
					throw new RuntimeException(x); // NOPMD
				}
			}
			catch(Throwable x)
			{
				throw new RuntimeException("yyy", x); // NOPMD
			}
		}
		catch(Throwable x)
		{
			t = x;
		}
		return t;
	}

	@Test
	public void testConvertEvent()
	{
		// LoggingEvent(String fqcn, Logger logger, Level level, String message, Throwable throwable, Object[] argArray)
		@SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
		ch.qos.logback.classic.spi.LoggingEvent logbackEvent =
			new ch.qos.logback.classic.spi.LoggingEvent(
				"de.huxhorn.lilith.data.logging.logback.LogbackLoggingConverterTest",
				(ch.qos.logback.classic.Logger) logger,
				Level.INFO,
				"Message",
				produceThrowable(),
				new String[]{"First", null, "Third"}

			);
		LoggingEvent lilithEvent = instance.convert(logbackEvent);
		if(logger.isInfoEnabled()) logger.info("lilithEvent: {}", lilithEvent);
		prettyPrint(lilithEvent);
	}

	private void prettyPrint(LoggingEvent event)
	{
		if(logger.isDebugEnabled())
		{
			StringBuilder msg = new StringBuilder();
			msg.append("Logger         : ").append(event.getLogger())
					.append("\nMessage        : ").append(event.getMessage())
					.append("\nLevel          : ").append(event.getLevel())
					.append('\n');
			ThreadInfo threadInfo = event.getThreadInfo();
			if(threadInfo != null)
			{
				msg.append("ThreadInfo     : ").append(threadInfo).append('\n');
			}

			msg.append("TimeStamp      : ").append(event.getTimeStamp())
					.append("\nMessage        : ").append(event.getMessage()).append('\n');

			ExtendedStackTraceElement[] callStack = event.getCallStack();
			if(callStack != null)
			{
				msg.append("Call-Stack     :\n");
				for(ExtendedStackTraceElement ste : callStack)
				{
					msg.append('\t').append(ste).append('\n');
				}
				msg.append('\n');
			}

			Marker marker = event.getMarker();
			if(marker != null)
			{
				msg.append("Marker         : ").append(marker).append('\n');
			}
			Map<String, String> mdc = event.getMdc();
			if(mdc != null)
			{
				msg.append("MDC            :\n");
				for(Map.Entry<String, String> current : mdc.entrySet())
				{
					msg.append('\t').append(current.getKey()).append(": ")
							.append(current.getValue()).append('\n');
				}
			}
			ThrowableInfo ti = event.getThrowable();
			if(ti != null)
			{
				msg.append("Throwable      :\n");
				ThrowableInfo current = ti;
				StringBuilder indent = new StringBuilder(INDENT);
				while(current != null)
				{
					msg.append(indent.toString()).append("Name      : ").append(current.getName()).append('\n')
							.append(indent.toString()).append("Message   : ").append(current.getMessage()).append('\n')
							.append(indent.toString()).append("StackTrace:\n");

					indent.append(INDENT);
					ExtendedStackTraceElement[] stackTrace = current.getStackTrace();
					if(stackTrace != null)
					{
						for(ExtendedStackTraceElement ste : stackTrace)
						{
							msg.append(indent.toString())
									.append(ste.toString(true))
									.append('\n');
						}
					}
					indent.append(INDENT);
					current = current.getCause();
				}
			}
			logger.debug(msg.toString());
		}
	}
}
