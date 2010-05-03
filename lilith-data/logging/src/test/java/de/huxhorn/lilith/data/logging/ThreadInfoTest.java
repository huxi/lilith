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

package de.huxhorn.lilith.data.logging;

import static de.huxhorn.sulky.junit.JUnitTools.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ThreadInfoTest
{
	private ThreadInfo fresh;

	@Before
	public void initFresh()
	{
		fresh = new ThreadInfo();
	}

	@Test
	public void defaultConstructor()
		throws ClassNotFoundException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		ThreadInfo instance = new ThreadInfo();

		testSerialization(instance);
		testXmlSerialization(instance);
		testClone(instance);
	}

	@Test
	public void fullConstructor()
		throws ClassNotFoundException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		ThreadInfo instance = new ThreadInfo(17L, "threadName", 42L, "groupName");

		assertEquals(17L, (long)instance.getId());
		assertEquals("threadName", instance.getName());
		assertEquals(42L, (long)instance.getGroupId());
		assertEquals("groupName", instance.getGroupName());

		testSerialization(instance);
		testXmlSerialization(instance);
		testClone(instance);
	}

	@Test
	public void name()
		throws ClassNotFoundException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		ThreadInfo instance = new ThreadInfo();

		String value = "value";
		instance.setName(value);

		{
			ThreadInfo obj = testSerialization(instance);
			assertEquals(value, obj.getName());
			assertFalse(fresh.equals(obj));
		}
		{
			ThreadInfo obj = testXmlSerialization(instance);
			assertEquals(value, obj.getName());
			assertFalse(fresh.equals(obj));
		}
		{
			ThreadInfo obj = testClone(instance);
			assertEquals(value, obj.getName());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void groupName()
		throws ClassNotFoundException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		ThreadInfo instance = new ThreadInfo();

		String value = "value";
		instance.setGroupName(value);

		{
			ThreadInfo obj = testSerialization(instance);
			assertEquals(value, obj.getGroupName());
			assertFalse(fresh.equals(obj));
		}
		{
			ThreadInfo obj = testXmlSerialization(instance);
			assertEquals(value, obj.getGroupName());
			assertFalse(fresh.equals(obj));
		}
		{
			ThreadInfo obj = testClone(instance);
			assertEquals(value, obj.getGroupName());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void id()
		throws ClassNotFoundException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		ThreadInfo instance = new ThreadInfo();

		Long value = 17L;
		instance.setId(value);

		{
			ThreadInfo obj = testSerialization(instance);
			assertEquals(value, obj.getId());
			assertFalse(fresh.equals(obj));
		}
		{
			ThreadInfo obj = testXmlSerialization(instance);
			assertEquals(value, obj.getId());
			assertFalse(fresh.equals(obj));
		}
		{
			ThreadInfo obj = testClone(instance);
			assertEquals(value, obj.getId());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void groupId()
		throws ClassNotFoundException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		ThreadInfo instance = new ThreadInfo();

		Long value = 17L;
		instance.setGroupId(value);

		{
			ThreadInfo obj = testSerialization(instance);
			assertEquals(value, obj.getGroupId());
			assertFalse(fresh.equals(obj));
		}
		{
			ThreadInfo obj = testXmlSerialization(instance);
			assertEquals(value, obj.getGroupId());
			assertFalse(fresh.equals(obj));
		}
		{
			ThreadInfo obj = testClone(instance);
			assertEquals(value, obj.getGroupId());
			assertFalse(fresh.equals(obj));
		}
	}
}
