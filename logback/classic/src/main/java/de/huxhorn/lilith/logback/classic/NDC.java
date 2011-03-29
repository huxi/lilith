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

public class NDC
{
	private static final NDCAdapter ndcAdapter;

	static
	{
		// TODO: configuration
		ndcAdapter = new SimpleNDCAdapter();
	}

	private NDC()
	{

	}


	public static void push(String message)
	{
		ndcAdapter.push(message);
	}

	public static void push(String messagePattern, Object[] arguments)
	{
		ndcAdapter.push(messagePattern, arguments);
	}

	/**
	 * Pops the last message from the stack.
	 * <p/>
	 * This method does not return the popped message to discourage it's usage in application logic.
	 */
	public static void pop()
	{
		ndcAdapter.pop();
	}

	public static int getDepth()
	{
		return ndcAdapter.getDepth();
	}

	public static void setMaximumDepth(int maximumDepth)
	{
		ndcAdapter.setMaximumDepth(maximumDepth);
	}

	public static boolean isEmpty()
	{
		return ndcAdapter.isEmpty();
	}

	public static void clear()
	{
		ndcAdapter.clear();
	}

	/**
	 * Returns an array containing all messages of the stack.
	 * <p/>
	 * The messages from the NDC stack should not be used in application logic.
	 *
	 * @return an array containing all messages of the stack.
	 */
	public static Message[] getContextStack()
	{
		return ndcAdapter.getContextStack();
	}
}
