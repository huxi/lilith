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

package de.huxhorn.lilith.data.eventsource;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.junit.Before;
import org.junit.Test;

import static de.huxhorn.sulky.junit.JUnitTools.testSerialization;
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EventWrapperTest
{
	private EventWrapper<String> fresh;

	@Before
	public void initFresh()
	{
		fresh = new EventWrapper<>();
	}

	@Test
	public void constructorDefault()
		throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		EventWrapper<String> instance = new EventWrapper<>();
		testSerialization(instance);
		testXmlSerialization(instance);
	}

	@Test
	public void constructorFull()
		throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		EventWrapper<String> instance = new EventWrapper<>(new SourceIdentifier(), 17, "Foo");
		testSerialization(instance);
		testXmlSerialization(instance);

		instance = new EventWrapper<>(null, 17, "Foo");
		testSerialization(instance);
		testXmlSerialization(instance);
	}

	@Test
	public void sourceIdentifier()
		throws ClassNotFoundException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException
	{
		EventWrapper<String> instance = new EventWrapper<>();

		SourceIdentifier value = new SourceIdentifier();
		instance.setSourceIdentifier(value);

		{
			EventWrapper<String> obj = testSerialization(instance);
			assertEquals(value, obj.getSourceIdentifier());
			assertFalse(fresh.equals(obj));
		}
		{
			EventWrapper<String> obj = testXmlSerialization(instance);
			assertEquals(value, obj.getSourceIdentifier());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void localId()
		throws ClassNotFoundException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException
	{
		EventWrapper<String> instance = new EventWrapper<>();

		long value = 17;
		instance.setLocalId(value);

		{
			EventWrapper<String> obj = testSerialization(instance);
			assertEquals(value, obj.getLocalId());
			assertFalse(fresh.equals(obj));
		}
		{
			EventWrapper<String> obj = testXmlSerialization(instance);
			assertEquals(value, obj.getLocalId());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void event()
		throws ClassNotFoundException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException
	{
		EventWrapper<String> instance = new EventWrapper<>();

		String value = "value";
		instance.setEvent(value);

		{
			EventWrapper<String> obj = testSerialization(instance);
			assertEquals(value, obj.getEvent());
			assertFalse(fresh.equals(obj));
		}
		{
			EventWrapper<String> obj = testXmlSerialization(instance);
			assertEquals(value, obj.getEvent());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void eventIdentifier()
		throws ClassNotFoundException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException
	{
		EventWrapper<String> instance = new EventWrapper<>();

		EventIdentifier value = new EventIdentifier();
		instance.setEventIdentifier(value);

		{
			EventWrapper<String> obj = testSerialization(instance);
			assertEquals(value, obj.getEventIdentifier());
			//assertFalse(fresh.equals(obj));
		}
		{
			EventWrapper<String> obj = testXmlSerialization(instance);
			assertEquals(value, obj.getEventIdentifier());
			//assertFalse(fresh.equals(obj));
		}
	}
}
