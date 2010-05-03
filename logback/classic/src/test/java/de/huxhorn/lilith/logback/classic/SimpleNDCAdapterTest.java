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
package de.huxhorn.lilith.logback.classic;

import de.huxhorn.lilith.data.logging.Message;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

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
		instance.push("messagePattern", new String[]{"foo", "bar"});
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
				new Message("message2", new String[]{"foo", null}),
				new Message("message3"),
				new Message(null),
				new Message(null, new String[]{"foo", "bar"}),
			};


		for(Message current : messages)
		{
			instance.push(current.getMessagePattern(), current.getArguments());
		}

		Message[] stack = instance.getContextStack();
		assertArrayEquals(messages, stack);
	}

	@Test
	public void inheritance()
		throws InterruptedException
	{
		Thread parent = new Thread(new Level1Runnable());
		parent.start();
		parent.join();
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

	public class Level1Runnable
		implements Runnable
	{

		public void run()
		{
			instance.push("Foo");
			assertFalse(instance.isEmpty());
			Thread child = new Thread(new Level2Runnable());
			child.start();
			try
			{
				child.join();
			}
			catch(InterruptedException e)
			{
				// ignore
			}
			assertFalse(instance.isEmpty());
			instance.pop();
			assertTrue(instance.isEmpty());
		}
	}

	public class Level2Runnable
		implements Runnable
	{

		public void run()
		{
			instance.push("Bar");
			Message[] contextStack = instance.getContextStack();
			assertEquals(2, contextStack.length);
			instance.pop();
			instance.pop();
		}
	}
}
