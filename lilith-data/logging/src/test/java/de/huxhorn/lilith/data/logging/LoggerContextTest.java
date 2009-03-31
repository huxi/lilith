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

import static de.huxhorn.sulky.junit.JUnitTools.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoggerContextTest
{
	private LoggerContext fresh;

	@Before
	public void initFresh()
	{
		fresh = new LoggerContext();
	}

	@Test
	public void defaultConstructor()
		throws ClassNotFoundException, IOException
	{
		LoggerContext instance = new LoggerContext();

		testSerialization(instance);
		testXmlSerialization(instance);
	}

	@Test
	public void name()
		throws ClassNotFoundException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		LoggerContext instance = new LoggerContext();

		String value = "value";
		instance.setName(value);

		{
			LoggerContext obj = testSerialization(instance);
			assertEquals(value, obj.getName());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggerContext obj = testXmlSerialization(instance);
			assertEquals(value, obj.getName());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggerContext obj = testClone(instance);
			assertEquals(value, obj.getName());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void birthTime()
		throws ClassNotFoundException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		LoggerContext instance = new LoggerContext();

		Date value = new Date(1234567890000L);
		instance.setBirthTime(value);

		{
			LoggerContext obj = testSerialization(instance);
			assertEquals(value, obj.getBirthTime());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggerContext obj = testXmlSerialization(instance);
			assertEquals(value, obj.getBirthTime());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggerContext obj = testClone(instance);
			assertEquals(value, obj.getBirthTime());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void properties()
		throws ClassNotFoundException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		LoggerContext instance = new LoggerContext();

		Map<String, String> value = new HashMap<String, String>();
		value.put("foo", "bar");
		instance.setProperties(value);

		{
			LoggerContext obj = testSerialization(instance);
			assertEquals(value, obj.getProperties());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggerContext obj = testXmlSerialization(instance);
			assertEquals(value, obj.getProperties());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggerContext obj = testClone(instance);
			assertEquals(value, obj.getProperties());
			assertFalse(fresh.equals(obj));
		}
	}
}
