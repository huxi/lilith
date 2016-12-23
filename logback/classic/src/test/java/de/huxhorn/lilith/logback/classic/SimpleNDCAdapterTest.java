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

package de.huxhorn.lilith.logback.classic;

import de.huxhorn.lilith.data.logging.Message;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimpleNDCAdapterTest
{
	private SimpleNDCAdapter instance;

	@Before
	public void setUp()
	{
		instance = new SimpleNDCAdapter();
	}

	@Test
	public void isEmpty()
	{
		assertTrue(instance.isEmpty());

	}

	@Test
	public void pushMessageUsingIsEmpty()
	{
		instance.push("message");
		assertFalse(instance.isEmpty());
	}

	@Test
	public void pushMessagePatternUsingIsEmpty()
	{
		instance.push("messagePattern", "foo", "bar");
		assertFalse(instance.isEmpty());
	}

	@Test
	public void popUsingPushAndIsEmpty()
	{
		instance.push("message");
		assertFalse(instance.isEmpty());
		instance.pop();
		assertTrue(instance.isEmpty());
	}

	@Test
	public void clearUsingPushAndIsEmpty()
	{
		instance.push("message");
		instance.push("message");
		assertFalse(instance.isEmpty());
		instance.clear();
		assertTrue(instance.isEmpty());
	}

	@Test
	public void getContextStackEmpty()
	{
		Message[] stack = instance.getContextStack();
		assertArrayEquals(new Message[0], stack);
	}

	@Test
	public void getContextStackUsingPush()
	{
		Message[] messages = new Message[]
			{
				new Message("message1", new String[]{"foo", "bar"}),
				new Message("message2", new String[]{"foo", "null"}),
				new Message("message3"),
				new Message(null),
				new Message(null, new String[]{"foo", "bar"}),
			};


		for(Message current : messages)
		{
			instance.push(current.getMessagePattern(), (Object[])current.getArguments());
		}

		Message[] stack = instance.getContextStack();
		assertArrayEquals(messages, stack);
	}

	@Test
	public void depth()
	{
		assertEquals(0, instance.getDepth());
		instance.push("Foo");
		instance.push("Bar");
		assertEquals(2, instance.getDepth());
		instance.pop();
		assertEquals(1, instance.getDepth());
		instance.pop();
		assertEquals(0, instance.getDepth());
		instance.pop();
		assertEquals(0, instance.getDepth());
	}

	@Test
	public void maximumDepthChange()
	{
		instance.push("Foo");
		instance.push("Bar");
		instance.setMaximumDepth(1);
		assertEquals(1, instance.getDepth());
		instance.pop();
		assertEquals(0, instance.getDepth());
	}

	@Test
	public void maximumDepthNoChange()
	{
		instance.push("Foo");
		instance.push("Bar");
		instance.setMaximumDepth(3);
		assertEquals(2, instance.getDepth());
	}
}
