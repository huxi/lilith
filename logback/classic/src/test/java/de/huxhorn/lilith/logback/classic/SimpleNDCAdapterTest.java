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
package de.huxhorn.lilith.logback.classic;

import de.huxhorn.lilith.data.logging.Message;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleNDCAdapterTest
{
	private final Logger logger = LoggerFactory.getLogger(SimpleNDCAdapterTest.class);

	private SimpleNDCAdapter instance;

	@Before
	public void setUp()
	{
		instance=new SimpleNDCAdapter();
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
		Message[] messages=new Message[]
			{
				new Message("message1", new String[]{"foo", "bar"}),
				new Message("message2", new String[]{"foo", null}),
				new Message("message3"),
				new Message(null),
				new Message(null, new String[]{"foo", "bar"}),
			};


		for(Message current:messages)
		{
			instance.push(current.getMessagePattern(), current.getArguments());
		}

		Message[] stack = instance.getContextStack();
		if(logger.isDebugEnabled()) logger.debug("Retrieved contextStack={}.", new Object[]{stack});
		assertArrayEquals(messages, stack);
	}

	// TODO: ad tests for maxDepth and inheritance
}
