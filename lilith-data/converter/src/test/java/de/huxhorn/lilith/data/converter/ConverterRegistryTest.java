/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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
 * Copyright 2007-2018 Joern Huxhorn
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

package de.huxhorn.lilith.data.converter;

import java.util.Locale;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ConverterRegistryTest
{
	private ConverterRegistry<String> instance = null;

	@Before
	public void setUp()
	{
		instance = new ConverterRegistry<>();
	}

	@Test
	public void checkEmpty()
	{
		assertNull(instance.resolveConverter("foo"));
	}

	@Test(expected = IllegalStateException.class)
	public void brokenConverter()
	{
		instance.addConverter(new BrokenConverter());
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullConverter()
	{
		instance.addConverter(null);
	}

	@Test
	public void resolveByInstance()
	{
		instance.addConverter(new UpperCaseConverter());
		assertNotNull(instance.resolveConverter("FooBar"));
	}

	@Test
	public void resolveByClass()
	{
		instance.addConverter(new UpperCaseConverter());
		assertNotNull(instance.resolveConverter(String.class));
	}

	@Test
	public void converterReplacement()
	{
		instance.addConverter(new LowerCaseConverter());
		instance.addConverter(new UpperCaseConverter());
		Converter<String> converter = instance.resolveConverter(String.class);
		assertEquals("FOOBAR", converter.convert("FooBar"));
	}

	@Test
	public void inheritance()
	{
		instance.addConverter(new AConverter());
		B value = new B();
		Converter<String> converter = instance.resolveConverter(value);
		assertEquals("B", converter.convert(value));
	}

	private static class BrokenConverter
		implements Converter<String>
	{
		@Override
		public String convert(Object o)
		{
			return "foo";
		}

		@Override
		public Class getSourceClass()
		{
			return null;
		}
	}

	private static class UpperCaseConverter
		implements Converter<String>
	{
		@Override
		public String convert(Object o)
		{
			if(o instanceof String)
			{
				String string = (String) o;
				return string.toUpperCase(Locale.US);
			}
			throw new IllegalArgumentException("object is not a String!");
		}

		@Override
		public Class getSourceClass()
		{
			return String.class;
		}
	}

	private static class LowerCaseConverter
		implements Converter<String>
	{
		@Override
		public String convert(Object o)
		{
			if(o instanceof String)
			{
				String string = (String) o;
				return string.toLowerCase(Locale.US);
			}
			throw new IllegalArgumentException("object is not a String!");
		}

		@Override
		public Class getSourceClass()
		{
			return String.class;
		}
	}

	@SuppressWarnings({"PMD.ShortClassName", "PMD.ClassNamingConventions"})
	private static class A
	{
		@Override
		public String toString()
		{
			return "A";
		}
	}

	@SuppressWarnings({"PMD.ShortClassName", "PMD.ClassNamingConventions"})
	private static class B
		extends A
	{
		@Override
		public String toString()
		{
			return "B";
		}
	}

	private static class AConverter
		implements Converter<String>
	{

		@Override
		public String convert(Object o)
		{
			return String.valueOf(o);
		}

		@Override
		public Class getSourceClass()
		{
			return A.class;
		}
	}
}
