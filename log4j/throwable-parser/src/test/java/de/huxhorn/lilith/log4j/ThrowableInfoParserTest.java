/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
 * Copyright 2007-2010 Joern Huxhorn
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

package de.huxhorn.lilith.log4j;

import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;

public class ThrowableInfoParserTest
{
	private final Logger logger = LoggerFactory.getLogger(ThrowableInfoParserTest.class);

	@Test
	public void full()
	{
		String[] throwableString = new String[]{
			"java.lang.RuntimeException: Hello",
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.execute(Log4jSandbox.java:18)",
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox.main(Log4jSandbox.java:38)",
			"Caused by: java.lang.RuntimeException: Hi.",
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.foobar(Log4jSandbox.java:24)",
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.execute(Log4jSandbox.java:14)",
			"\t... 1 more"
		};

		ThrowableInfo actual = ThrowableInfoParser.parseThrowableInfo(Arrays.asList(throwableString));

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
			if(logger.isInfoEnabled()) logger.info("Expected: {}", throwableInfo.toString(true));
			if(logger.isInfoEnabled()) logger.info("Actual  : {}", actual.toString(true));
			assertEquals(throwableInfo, actual);
		}
	}

	@Test
	public void singleThrowable()
	{
		String[] throwableString = new String[]{
			"java.lang.Throwable",
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox.main(Log4jSandbox.java:37)"
		};

		ThrowableInfo actual = ThrowableInfoParser.parseThrowableInfo(Arrays.asList(throwableString));

		// ThrowableInfo
		{
			ThrowableInfo throwableInfo = new ThrowableInfo();
			throwableInfo.setName("java.lang.Throwable");
			throwableInfo.setMessage(null);
			throwableInfo.setStackTrace(new ExtendedStackTraceElement[]{
				new ExtendedStackTraceElement("de.huxhorn.lilith.sandbox.Log4jSandbox", "main", "Log4jSandbox.java", 37),
			});

			if(logger.isInfoEnabled()) logger.info("Expected: {}", throwableInfo.toString(true));
			if(logger.isInfoEnabled()) logger.info("Actual  : {}", actual.toString(true));
			assertEquals(throwableInfo, actual);
		}
	}

	@Test
	public void multiLineMessage()
	{
		String[] throwableString = new String[]{
			"java.lang.RuntimeException: Multi\n",
			"line\n",
			"message\n",
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.execute(Log4jSandbox.java:28)\n",
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox.main(Log4jSandbox.java:51)\n",
			"Caused by: java.lang.RuntimeException: Hi.\n",
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.foobar(Log4jSandbox.java:35)\n",
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.execute(Log4jSandbox.java:24)\n",
			"\t... 1 more\n"
		};

		ThrowableInfo actual = ThrowableInfoParser.parseThrowableInfo(Arrays.asList(throwableString));

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

			if(logger.isInfoEnabled()) logger.info("Expected: {}", throwableInfo.toString(true));
			if(logger.isInfoEnabled()) logger.info("Actual  : {}", actual.toString(true));
			assertEquals(throwableInfo, actual);
		}
	}

	@Test
	public void multiLineMessageWithEmptyLine()
	{
		String[] throwableString = new String[]{
			"java.lang.RuntimeException: Multi\n",
			"line\n",
			"message\n",
			"",
			"with empty line",
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.execute(Log4jSandbox.java:28)\n",
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox.main(Log4jSandbox.java:51)\n",
			"Caused by: java.lang.RuntimeException: Hi.\n",
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.foobar(Log4jSandbox.java:35)\n",
			"\tat de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.execute(Log4jSandbox.java:24)\n",
			"\t... 1 more\n"
		};

		ThrowableInfo actual = ThrowableInfoParser.parseThrowableInfo(Arrays.asList(throwableString));

		// ThrowableInfo
		{
			ThrowableInfo throwableInfo = new ThrowableInfo();
			throwableInfo.setName("java.lang.RuntimeException");
			throwableInfo.setMessage("Multi\nline\nmessage\n\nwith empty line");
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

			if(logger.isInfoEnabled()) logger.info("Expected: {}", throwableInfo.toString(true));
			if(logger.isInfoEnabled()) logger.info("Actual  : {}", actual.toString(true));
			assertEquals(throwableInfo, actual);
		}
	}
}
