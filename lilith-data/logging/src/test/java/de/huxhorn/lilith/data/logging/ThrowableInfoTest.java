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
import static org.junit.Assert.assertFalse;
import static de.huxhorn.sulky.junit.JUnitTools.testSerialization;
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization;

import java.io.IOException;

public class ThrowableInfoTest
{
	private ThrowableInfo fresh;

	@Before
	public void initFresh()
	{
		fresh = new ThrowableInfo();
	}

	@Test
	public void defaultConstructor() throws ClassNotFoundException, IOException
	{
		ThrowableInfo instance=new ThrowableInfo();

		testSerialization(instance);
		testXmlSerialization(instance);
	}

	@Test
	public void name() throws ClassNotFoundException, IOException
	{
		ThrowableInfo instance=new ThrowableInfo();

		String value="value";
		instance.setName(value);

		{
			ThrowableInfo obj = testSerialization(instance);
			assertEquals(value, obj.getName());
			assertFalse(fresh.equals(obj));
		}
		{
			ThrowableInfo obj = testXmlSerialization(instance);
			assertEquals(value, obj.getName());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void message() throws ClassNotFoundException, IOException
	{
		ThrowableInfo instance=new ThrowableInfo();

		String value="value";
		instance.setMessage(value);

		{
			ThrowableInfo obj = testSerialization(instance);
			assertEquals(value, obj.getMessage());
			assertFalse(fresh.equals(obj));
		}
		{
			ThrowableInfo obj = testXmlSerialization(instance);
			assertEquals(value, obj.getMessage());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void omittedElements() throws ClassNotFoundException, IOException
	{
		ThrowableInfo instance=new ThrowableInfo();

		int value=17;
		instance.setOmittedElements(value);

		{
			ThrowableInfo obj = testSerialization(instance);
			assertEquals(value, obj.getOmittedElements());
			assertFalse(fresh.equals(obj));
		}
		{
			ThrowableInfo obj = testXmlSerialization(instance);
			assertEquals(value, obj.getOmittedElements());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void cause() throws ClassNotFoundException, IOException
	{
		ThrowableInfo instance=new ThrowableInfo();

		ThrowableInfo value=new ThrowableInfo();
		instance.setCause(value);

		{
			ThrowableInfo obj = testSerialization(instance);
			assertEquals(value, obj.getCause());
			assertFalse(fresh.equals(obj));
		}
		{
			ThrowableInfo obj = testXmlSerialization(instance);
			assertEquals(value, obj.getCause());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void stackTrace() throws ClassNotFoundException, IOException
	{
		ThrowableInfo instance=new ThrowableInfo();

		ExtendedStackTraceElement[] value=new ExtendedStackTraceElement[]{new ExtendedStackTraceElement()};
		instance.setStackTrace(value);

		{
			ThrowableInfo obj = testSerialization(instance);
			assertArrayEquals(value, obj.getStackTrace());
			assertFalse(fresh.equals(obj));
		}
		{
			ThrowableInfo obj = testXmlSerialization(instance);
			assertArrayEquals(value, obj.getStackTrace());
			assertFalse(fresh.equals(obj));
		}
	}
}