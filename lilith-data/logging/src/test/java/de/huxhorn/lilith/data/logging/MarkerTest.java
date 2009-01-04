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

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class MarkerTest
{
	private Marker fresh;

	@Before
	public void initFresh()
	{
		fresh = new Marker();
	}

	@Test
	public void defaultConstructor() throws ClassNotFoundException, IOException
	{
		Marker instance=new Marker();

		testSerialization(instance);
		testXmlSerialization(instance);
	}

	@Test
	public void name() throws ClassNotFoundException, IOException
	{
		Marker instance=new Marker();

		String value="value";
		instance.setName(value);

		{
			Marker obj = testSerialization(instance);
			assertEquals(value, obj.getName());
			assertFalse(fresh.equals(obj));
		}
		{
			Marker obj = testXmlSerialization(instance);
			assertEquals(value, obj.getName());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void references() throws ClassNotFoundException, IOException
	{
		Marker instance=new Marker();

		Map<String, Marker> value=new HashMap<String, Marker>();
		value.put("foo", new Marker());
		instance.setReferences(value);

		{
			Marker obj = testSerialization(instance);
			assertEquals(value, obj.getReferences());
			//assertFalse(fresh.equals(obj));
			// Marker.equals does only take the markername into account.
		}
		{
			Marker obj = testXmlSerialization(instance);
			assertEquals(value, obj.getReferences());
			//assertFalse(fresh.equals(obj));
			// Marker.equals does only take the markername into account.
		}
	}
}