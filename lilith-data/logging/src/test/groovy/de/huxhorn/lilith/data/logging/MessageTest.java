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

import static de.huxhorn.sulky.junit.JUnitTools.*;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class MessageTest
{
	private Message fresh;

	@Before
	public void initFresh()
	{
		fresh = new Message();
	}

	@Test
	public void defaultConstructor()
		throws ClassNotFoundException, IOException
	{
		Message instance = new Message();

		testSerialization(instance);
		testXmlSerialization(instance);
	}

	@Test
	public void messagePattern()
		throws ClassNotFoundException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Message instance = new Message();

		String value = "value";
		instance.setMessagePattern(value);

		{
			Message obj = testSerialization(instance);
			assertEquals(value, obj.getMessagePattern());
			assertFalse(fresh.equals(obj));
		}
		{
			Message obj = testXmlSerialization(instance);
			assertEquals(value, obj.getMessagePattern());
			assertFalse(fresh.equals(obj));
		}
		{
			Message obj = testClone(instance);
			assertEquals(value, obj.getMessagePattern());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void arguments()
		throws ClassNotFoundException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Message instance = new Message();

		String value[] = new String[]{"value1", "value2"};
		instance.setArguments(value);

		{
			Message obj = testSerialization(instance);
			assertArrayEquals(value, obj.getArguments());
			assertNotSame(value, obj.getArguments());
			assertFalse(fresh.equals(obj));
		}
		{
			Message obj = testXmlSerialization(instance);
			assertArrayEquals(value, obj.getArguments());
			assertNotSame(value, obj.getArguments());
			assertFalse(fresh.equals(obj));
		}
		{
			Message obj = testClone(instance);
			assertArrayEquals(value, obj.getArguments());
			assertNotSame(value, obj.getArguments());
			assertFalse(fresh.equals(obj));
		}
	}


}
