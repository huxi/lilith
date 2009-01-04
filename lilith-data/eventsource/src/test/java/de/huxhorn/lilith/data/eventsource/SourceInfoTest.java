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
package de.huxhorn.lilith.data.eventsource;

import static de.huxhorn.sulky.junit.JUnitTools.testSerialization;
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class SourceInfoTest
{
	private SourceInfo fresh;

	@Before
	public void initFresh()
	{
		fresh = new SourceInfo();
	}

    @Test
    public void constructorDefault() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        SourceInfo original=new SourceInfo();
        testSerialization(original);
		testXmlSerialization(original);
    }

	@Test
	public void source() throws ClassNotFoundException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException
	{
		SourceInfo instance=new SourceInfo();

		SourceIdentifier value=new SourceIdentifier();
		instance.setSource(value);

		{
			SourceInfo obj = testSerialization(instance);
			assertEquals(value, obj.getSource());
			assertFalse(fresh.equals(obj));
		}
		{
			SourceInfo obj = testXmlSerialization(instance);
			assertEquals(value, obj.getSource());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void numberOfEvents() throws ClassNotFoundException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException
	{
		SourceInfo instance=new SourceInfo();

		long value=17;
		instance.setNumberOfEvents(value);

		{
			SourceInfo obj = testSerialization(instance);
			assertEquals(value, obj.getNumberOfEvents());
			assertFalse(fresh.equals(obj));
		}
		{
			SourceInfo obj = testXmlSerialization(instance);
			assertEquals(value, obj.getNumberOfEvents());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void active() throws ClassNotFoundException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException
	{
		SourceInfo instance=new SourceInfo();

		boolean value=true;
		instance.setActive(value);

		{
			SourceInfo obj = testSerialization(instance);
			assertEquals(value, obj.isActive());
			assertFalse(fresh.equals(obj));
		}
		{
			SourceInfo obj = testXmlSerialization(instance);
			assertEquals(value, obj.isActive());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void oldestEventTimestamp() throws ClassNotFoundException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException
	{
		SourceInfo instance=new SourceInfo();

		Date value=new Date();
		instance.setOldestEventTimestamp(value);

		{
			SourceInfo obj = testSerialization(instance);
			assertEquals(value, obj.getOldestEventTimestamp());
			assertFalse(fresh.equals(obj));
		}
		{
			SourceInfo obj = testXmlSerialization(instance);
			assertEquals(value, obj.getOldestEventTimestamp());
			assertFalse(fresh.equals(obj));
		}
	}
}