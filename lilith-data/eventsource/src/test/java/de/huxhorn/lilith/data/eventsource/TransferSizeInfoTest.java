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
package de.huxhorn.lilith.data.eventsource;

import static de.huxhorn.sulky.junit.JUnitTools.testClone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class TransferSizeInfoTest
{
	private TransferSizeInfo fresh;

	@Before
	public void initFresh()
	{
		fresh = new TransferSizeInfo();
	}

	@Test
	public void constructorDefault()
		throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		TransferSizeInfo original = new TransferSizeInfo();
		testClone(original);
	}

	@Test
	public void transferSize()
		throws ClassNotFoundException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException
	{
		TransferSizeInfo instance = new TransferSizeInfo();

		Long value = 17L;
		instance.transferSize = value;

		{
			TransferSizeInfo obj = testClone(instance);
			assertEquals(value, obj.transferSize);
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void uncompressedSize()
		throws ClassNotFoundException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException
	{
		TransferSizeInfo instance = new TransferSizeInfo();

		Long value = 17L;
		instance.uncompressedSize = value;

		{
			TransferSizeInfo obj = testClone(instance);
			assertEquals(value, obj.uncompressedSize);
			assertFalse(fresh.equals(obj));
		}
	}

}