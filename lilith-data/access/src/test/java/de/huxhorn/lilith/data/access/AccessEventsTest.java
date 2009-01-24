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
package de.huxhorn.lilith.data.access;

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

public class AccessEventsTest
{
	private AccessEvents fresh;

	@Before
	public void initFresh()
	{
		fresh = new AccessEvents();
	}

	@Test
	public void defaultConstructor()
		throws ClassNotFoundException, IOException
	{
		AccessEvents instance = new AccessEvents();

		testSerialization(instance);
		testXmlSerialization(instance);
	}

	@Test
	public void source()
		throws ClassNotFoundException, IOException
	{
		AccessEvents instance = new AccessEvents();

		SourceIdentifier value = new SourceIdentifier();
		instance.setSource(value);

		{
			AccessEvents obj = testSerialization(instance);
			assertEquals(value, obj.getSource());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvents obj = testXmlSerialization(instance);
			assertEquals(value, obj.getSource());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void startIndex()
		throws ClassNotFoundException, IOException
	{
		AccessEvents instance = new AccessEvents();

		long value = 17;
		instance.setStartIndex(value);

		{
			AccessEvents obj = testSerialization(instance);
			assertEquals(value, obj.getStartIndex());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvents obj = testXmlSerialization(instance);
			assertEquals(value, obj.getStartIndex());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void events()
		throws ClassNotFoundException, IOException
	{
		AccessEvents instance = new AccessEvents();


		List<AccessEvent> value = new ArrayList<AccessEvent>();
		value.add(new AccessEvent());
		instance.setEvents(value);

		{
			AccessEvents obj = testSerialization(instance);
			assertEquals(value, obj.getEvents());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvents obj = testXmlSerialization(instance);
			assertEquals(value, obj.getEvents());
			assertFalse(fresh.equals(obj));
		}
	}
}