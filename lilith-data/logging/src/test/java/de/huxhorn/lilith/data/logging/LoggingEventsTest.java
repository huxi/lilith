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

import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import static de.huxhorn.sulky.junit.JUnitTools.testSerialization;
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoggingEventsTest
{
	private LoggingEvents fresh;

	@Before
	public void initFresh()
	{
		fresh = new LoggingEvents();
	}

	@Test
	public void defaultConstructor()
		throws ClassNotFoundException, IOException
	{
		LoggingEvents instance = new LoggingEvents();

		testSerialization(instance);
		testXmlSerialization(instance);
	}

	@Test
	public void source()
		throws ClassNotFoundException, IOException
	{
		LoggingEvents instance = new LoggingEvents();

		SourceIdentifier value = new SourceIdentifier();
		instance.setSource(value);

		{
			LoggingEvents obj = testSerialization(instance);
			assertEquals(value, obj.getSource());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggingEvents obj = testXmlSerialization(instance);
			assertEquals(value, obj.getSource());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void startIndex()
		throws ClassNotFoundException, IOException
	{
		LoggingEvents instance = new LoggingEvents();

		long value = 17;
		instance.setStartIndex(value);

		{
			LoggingEvents obj = testSerialization(instance);
			assertEquals(value, obj.getStartIndex());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggingEvents obj = testXmlSerialization(instance);
			assertEquals(value, obj.getStartIndex());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void events()
		throws ClassNotFoundException, IOException
	{
		LoggingEvents instance = new LoggingEvents();


		List<LoggingEvent> value = new ArrayList<LoggingEvent>();
		value.add(new LoggingEvent());
		instance.setEvents(value);

		{
			LoggingEvents obj = testSerialization(instance);
			assertEquals(value, obj.getEvents());
			assertFalse(fresh.equals(obj));
		}
		{
			LoggingEvents obj = testXmlSerialization(instance);
			assertEquals(value, obj.getEvents());
			assertFalse(fresh.equals(obj));
		}
	}
}
