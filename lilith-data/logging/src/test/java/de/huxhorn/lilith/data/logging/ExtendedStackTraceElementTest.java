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
package de.huxhorn.lilith.data.logging;

import static de.huxhorn.sulky.junit.JUnitTools.testSerialization;
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ExtendedStackTraceElementTest
{
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
}
