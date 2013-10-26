/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
 * Copyright 2007-2011 Joern Huxhorn
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

import static de.huxhorn.sulky.junit.JUnitTools.testSerialization;
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ExtendedStackTraceElementTest
{
	private final Logger logger = LoggerFactory.getLogger(ExtendedStackTraceElementTest.class);

	private ExtendedStackTraceElement fresh;

	@Before
	public void initFresh()
	{
		fresh = new ExtendedStackTraceElement();
	}

	@Test
	public void defaultConstructor()
		throws ClassNotFoundException, IOException
	{
		ExtendedStackTraceElement instance = new ExtendedStackTraceElement();

		testSerialization(instance);
		testXmlSerialization(instance);
	}

	@Test
	public void className()
		throws ClassNotFoundException, IOException
	{
		ExtendedStackTraceElement instance = new ExtendedStackTraceElement();

		String value = "value";
		instance.setClassName(value);

		{
			ExtendedStackTraceElement obj = testSerialization(instance);
			assertEquals(value, obj.getClassName());
			assertFalse(fresh.equals(obj));
		}
		{
			ExtendedStackTraceElement obj = testXmlSerialization(instance);
			assertEquals(value, obj.getClassName());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void codeLocation()
		throws ClassNotFoundException, IOException
	{
		ExtendedStackTraceElement instance = new ExtendedStackTraceElement();

		String value = "value";
		instance.setCodeLocation(value);

		{
			ExtendedStackTraceElement obj = testSerialization(instance);
			assertEquals(value, obj.getCodeLocation());
			assertFalse(fresh.equals(obj));
		}
		{
			ExtendedStackTraceElement obj = testXmlSerialization(instance);
			assertEquals(value, obj.getCodeLocation());
			assertFalse(fresh.equals(obj));
		}
	}


	@Test
	public void fileName()
		throws ClassNotFoundException, IOException
	{
		ExtendedStackTraceElement instance = new ExtendedStackTraceElement();

		String value = "value";
		instance.setFileName(value);

		{
			ExtendedStackTraceElement obj = testSerialization(instance);
			assertEquals(value, obj.getFileName());
			assertFalse(fresh.equals(obj));
		}
		{
			ExtendedStackTraceElement obj = testXmlSerialization(instance);
			assertEquals(value, obj.getFileName());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void methodName()
		throws ClassNotFoundException, IOException
	{
		ExtendedStackTraceElement instance = new ExtendedStackTraceElement();

		String value = "value";
		instance.setMethodName(value);

		{
			ExtendedStackTraceElement obj = testSerialization(instance);
			assertEquals(value, obj.getMethodName());
			assertFalse(fresh.equals(obj));
		}
		{
			ExtendedStackTraceElement obj = testXmlSerialization(instance);
			assertEquals(value, obj.getMethodName());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void version()
		throws ClassNotFoundException, IOException
	{
		ExtendedStackTraceElement instance = new ExtendedStackTraceElement();

		String value = "value";
		instance.setVersion(value);

		{
			ExtendedStackTraceElement obj = testSerialization(instance);
			assertEquals(value, obj.getVersion());
			assertFalse(fresh.equals(obj));
		}
		{
			ExtendedStackTraceElement obj = testXmlSerialization(instance);
			assertEquals(value, obj.getVersion());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void exact()
		throws ClassNotFoundException, IOException
	{
		ExtendedStackTraceElement instance = new ExtendedStackTraceElement();

		boolean value = true;
		instance.setExact(value);

		{
			ExtendedStackTraceElement obj = testSerialization(instance);
			assertEquals(value, obj.isExact());
			assertFalse(fresh.equals(obj));
		}
		{
			ExtendedStackTraceElement obj = testXmlSerialization(instance);
			assertEquals(value, obj.isExact());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void lineNumber()
		throws ClassNotFoundException, IOException
	{
		ExtendedStackTraceElement instance = new ExtendedStackTraceElement();

		int value = 17;
		instance.setLineNumber(value);
		assertEquals(false, instance.isNativeMethod());

		{
			ExtendedStackTraceElement obj = testSerialization(instance);
			assertEquals(value, obj.getLineNumber());
			assertFalse(fresh.equals(obj));
		}
		{
			ExtendedStackTraceElement obj = testXmlSerialization(instance);
			assertEquals(value, obj.getLineNumber());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void lineNumberNative()
		throws ClassNotFoundException, IOException
	{
		ExtendedStackTraceElement instance = new ExtendedStackTraceElement();

		int value = ExtendedStackTraceElement.NATIVE_METHOD;
		instance.setLineNumber(value);
		assertEquals(true, instance.isNativeMethod());

		{
			ExtendedStackTraceElement obj = testSerialization(instance);
			assertEquals(value, obj.getLineNumber());
			assertFalse(fresh.equals(obj));
		}
		{
			ExtendedStackTraceElement obj = testXmlSerialization(instance);
			assertEquals(value, obj.getLineNumber());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void parseNative()
	{
		ExtendedStackTraceElement instance = ExtendedStackTraceElement
			.parseStackTraceElement("\tat java.lang.Thread.sleep(Native Method)");
		ExtendedStackTraceElement expected = new ExtendedStackTraceElement("java.lang.Thread", "sleep", null, ExtendedStackTraceElement.NATIVE_METHOD);
		assertEquals(expected, instance);
		assertNull(instance.getExtendedString());
	}

	@Test
	public void parseFull()
	{
		ExtendedStackTraceElement instance = ExtendedStackTraceElement
			.parseStackTraceElement("\tat java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:303)");
		ExtendedStackTraceElement expected = new ExtendedStackTraceElement("java.util.concurrent.FutureTask$Sync", "innerRun", "FutureTask.java", 303);
		assertEquals(expected, instance);
		assertNull(instance.getExtendedString());
	}

	@Test
	public void parseUnknownSource()
	{
		ExtendedStackTraceElement instance = ExtendedStackTraceElement
			.parseStackTraceElement("\tat java.util.concurrent.FutureTask$Sync.innerRun(Unknown Source)");
		ExtendedStackTraceElement expected = new ExtendedStackTraceElement("java.util.concurrent.FutureTask$Sync", "innerRun", null, ExtendedStackTraceElement.UNKNOWN_SOURCE);
		assertEquals(expected, instance);
		assertNull(instance.getExtendedString());
	}

	@Test
	public void parseNoStackTrace()
	{
		ExtendedStackTraceElement instance = ExtendedStackTraceElement.parseStackTraceElement("Just a random string.");
		assertNull(instance);
	}

	@Test
	public void parseNull()
	{
		ExtendedStackTraceElement instance = ExtendedStackTraceElement.parseStackTraceElement(null);
		assertNull(instance);
	}

	@Test
	public void parseFullExtended()
	{
		ExtendedStackTraceElement instance = ExtendedStackTraceElement
			.parseStackTraceElement("\tat de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) [lilith.jar:0.9.35-SNAPSHOT]");
		ExtendedStackTraceElement expected = new ExtendedStackTraceElement("de.huxhorn.lilith.swing.MainFrame", "setAccessEventSourceManager", "MainFrame.java", 1079, "lilith.jar", "0.9.35-SNAPSHOT", true);
		if(logger.isInfoEnabled()) logger.info("instance.getExtendedString(): {}", instance.getExtendedString());
		if(logger.isInfoEnabled()) logger.info("expected.getExtendedString(): {}", expected.getExtendedString());
		assertEquals(expected, instance);
	}

	@Test
	public void parseFullExtendedApprox()
	{
		ExtendedStackTraceElement instance = ExtendedStackTraceElement
			.parseStackTraceElement("\tat de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) ~[lilith.jar:0.9.35-SNAPSHOT]");
		ExtendedStackTraceElement expected = new ExtendedStackTraceElement("de.huxhorn.lilith.swing.MainFrame", "setAccessEventSourceManager", "MainFrame.java", 1079, "lilith.jar", "0.9.35-SNAPSHOT", false);
		if(logger.isInfoEnabled()) logger.info("instance.getExtendedString(): {}", instance.getExtendedString());
		if(logger.isInfoEnabled()) logger.info("expected.getExtendedString(): {}", expected.getExtendedString());
		assertEquals(expected, instance);
	}

	@Test
	public void parseFullExtendedNoVersion()
	{
		ExtendedStackTraceElement instance = ExtendedStackTraceElement
			.parseStackTraceElement("\tat de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) [:0.9.35-SNAPSHOT]");
		ExtendedStackTraceElement expected = new ExtendedStackTraceElement("de.huxhorn.lilith.swing.MainFrame", "setAccessEventSourceManager", "MainFrame.java", 1079, null, "0.9.35-SNAPSHOT", true);
		if(logger.isInfoEnabled()) logger.info("instance.getExtendedString(): {}", instance.getExtendedString());
		if(logger.isInfoEnabled()) logger.info("expected.getExtendedString(): {}", expected.getExtendedString());
		assertEquals(expected, instance);
	}

	@Test
	public void parseFullExtendedNoLocation()
	{
		ExtendedStackTraceElement instance = ExtendedStackTraceElement
			.parseStackTraceElement("\tat de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) [lilith.jar:]");
		ExtendedStackTraceElement expected = new ExtendedStackTraceElement("de.huxhorn.lilith.swing.MainFrame", "setAccessEventSourceManager", "MainFrame.java", 1079, "lilith.jar", null, true);
		if(logger.isInfoEnabled()) logger.info("instance.getExtendedString(): {}", instance.getExtendedString());
		if(logger.isInfoEnabled()) logger.info("expected.getExtendedString(): {}", expected.getExtendedString());
		assertEquals(expected, instance);
	}

	@Test
	public void parseFullExtendedNoLocationNoVersion()
	{
		ExtendedStackTraceElement instance = ExtendedStackTraceElement
			.parseStackTraceElement("\tat de.huxhorn.lilith.swing.MainFrame.setAccessEventSourceManager(MainFrame.java:1079) [:]");
		ExtendedStackTraceElement expected = new ExtendedStackTraceElement("de.huxhorn.lilith.swing.MainFrame", "setAccessEventSourceManager", "MainFrame.java", 1079, null, null, true);
		if(logger.isInfoEnabled()) logger.info("instance.getExtendedString(): {}", instance.getExtendedString());
		if(logger.isInfoEnabled()) logger.info("expected.getExtendedString(): {}", expected.getExtendedString());
		assertEquals(expected, instance);
		assertNull(instance.getExtendedString());
	}
}
