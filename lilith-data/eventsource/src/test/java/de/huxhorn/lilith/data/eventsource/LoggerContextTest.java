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

package de.huxhorn.lilith.data.eventsource;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static de.huxhorn.sulky.junit.JUnitTools.testClone;
import static de.huxhorn.sulky.junit.JUnitTools.testSerialization;
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
			assertNotEquals(fresh, obj);
		}
		{
			LoggerContext obj = testXmlSerialization(instance);
			assertEquals(value, obj.getName());
			assertNotEquals(fresh, obj);
		}
		{
			LoggerContext obj = testClone(instance);
			assertEquals(value, obj.getName());
			assertNotEquals(fresh, obj);
		}
	}

	@Test
	public void birthTime()
		throws ClassNotFoundException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		LoggerContext instance = new LoggerContext();

		Long value = 1_234_567_890_000L;
		instance.setBirthTime(value);

		{
			LoggerContext obj = testSerialization(instance);
			assertEquals(value, obj.getBirthTime());
			assertNotEquals(fresh, obj);
		}
		{
			LoggerContext obj = testXmlSerialization(instance);
			assertEquals(value, obj.getBirthTime());
			assertNotEquals(fresh, obj);
		}
		{
			LoggerContext obj = testClone(instance);
			assertEquals(value, obj.getBirthTime());
			assertNotEquals(fresh, obj);
		}
	}

	@Test
	public void properties()
		throws ClassNotFoundException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		LoggerContext instance = new LoggerContext();

		Map<String, String> value = new HashMap<>();
		value.put("foo", "bar");
		instance.setProperties(value);

		{
			LoggerContext obj = testSerialization(instance);
			assertEquals(value, obj.getProperties());
			assertNotEquals(fresh, obj);
		}
		{
			LoggerContext obj = testXmlSerialization(instance);
			assertEquals(value, obj.getProperties());
			assertNotEquals(fresh, obj);
		}
		{
			LoggerContext obj = testClone(instance);
			assertEquals(value, obj.getProperties());
			assertNotEquals(fresh, obj);
		}
	}
}
