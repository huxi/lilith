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
package de.huxhorn.lilith.data.eventsource;

import static de.huxhorn.sulky.junit.JUnitTools.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class EventIdentifierTest
{
	private EventIdentifier fresh;

	@Before
	public void initFresh()
	{
		fresh = new EventIdentifier();
	}

	@Test
	public void constructorDefault()
		throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		EventIdentifier instance = new EventIdentifier();
		testSerialization(instance);
		testXmlSerialization(instance);
		testClone(instance);
	}

	@Test
	public void constructorFull()
		throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		EventIdentifier instance = new EventIdentifier(new SourceIdentifier(), 17);
		testSerialization(instance);
		testXmlSerialization(instance);
		testClone(instance);

		instance = new EventIdentifier(null, 17);
		testSerialization(instance);
		testXmlSerialization(instance);
		testClone(instance);
	}

	@Test
	public void sourceIdentifier()
		throws ClassNotFoundException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException
	{
		EventIdentifier instance = new EventIdentifier();

		SourceIdentifier value = new SourceIdentifier();
		instance.setSourceIdentifier(value);

		{
			EventIdentifier obj = testSerialization(instance);
			assertEquals(value, obj.getSourceIdentifier());
			assertFalse(fresh.equals(obj));
		}
		{
			EventIdentifier obj = testXmlSerialization(instance);
			assertEquals(value, obj.getSourceIdentifier());
			assertFalse(fresh.equals(obj));
		}
		{
			EventIdentifier obj = testClone(instance);
			assertEquals(value, obj.getSourceIdentifier());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void localId()
		throws ClassNotFoundException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException
	{
		EventIdentifier instance = new EventIdentifier();

		long value = 17;
		instance.setLocalId(value);

		{
			EventIdentifier obj = testSerialization(instance);
			assertEquals(value, obj.getLocalId());
			assertFalse(fresh.equals(obj));
		}
		{
			EventIdentifier obj = testXmlSerialization(instance);
			assertEquals(value, obj.getLocalId());
			assertFalse(fresh.equals(obj));
		}
		{
			EventIdentifier obj = testClone(instance);
			assertEquals(value, obj.getLocalId());
			assertFalse(fresh.equals(obj));
		}
	}
}
