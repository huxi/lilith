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
import static org.junit.Assert.assertFalse;
import static de.huxhorn.sulky.junit.JUnitTools.testSerialization;
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class LoggingEventsTest
{
	private LoggingEvents fresh;

	@Before
	public void initFresh()
	{
		fresh = new LoggingEvents();
	}

	@Test
	public void defaultConstructor() throws ClassNotFoundException, IOException
	{
		LoggingEvents instance=new LoggingEvents();

		testSerialization(instance);
		testXmlSerialization(instance);
	}

	@Test
	public void source() throws ClassNotFoundException, IOException
	{
		LoggingEvents instance=new LoggingEvents();

		SourceIdentifier value=new SourceIdentifier();
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
	public void startIndex() throws ClassNotFoundException, IOException
	{
		LoggingEvents instance=new LoggingEvents();

		long value=17;
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
	public void events() throws ClassNotFoundException, IOException
	{
		LoggingEvents instance=new LoggingEvents();


		List<LoggingEvent> value=new ArrayList<LoggingEvent>();
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