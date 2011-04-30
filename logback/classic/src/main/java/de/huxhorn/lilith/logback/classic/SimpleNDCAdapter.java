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
import de.huxhorn.lilith.data.logging.MessageFormatter;

import java.util.LinkedList;
import java.util.List;

public class SimpleNDCAdapter
	implements NDCAdapter
{
	private ThreadLocal<List<String>> threadLocalMessagePatterns =new ThreadLocal<List<String>>();
	private ThreadLocal<List<String[]>> threadLocalMessageArguments =new ThreadLocal<List<String[]>>();

	public void push(String message)
	{
		List<String> messages = threadLocalMessagePatterns.get();
		List<String[]> args = threadLocalMessageArguments.get();
		if(messages == null)
		{
			messages = new LinkedList<String>();
			args = new LinkedList<String[]>();
			threadLocalMessagePatterns.set(messages);
			threadLocalMessageArguments.set(args);
		}
		messages.add(message);
		args.add(null);
	}

	public void push(String messagePattern, Object[] arguments)
	{
		if(arguments == null || arguments.length == 0)
		{
			push(messagePattern);
			return;
		}
		MessageFormatter.ArgumentResult argumentResults = MessageFormatter.evaluateArguments(messagePattern, arguments);

		if(argumentResults == null)
		{
			push(messagePattern);
			return;
		}

		List<String> messages = threadLocalMessagePatterns.get();
		List<String[]> args = threadLocalMessageArguments.get();
		if(messages == null)
		{
			messages = new LinkedList<String>();
			args = new LinkedList<String[]>();
			threadLocalMessagePatterns.set(messages);
			threadLocalMessageArguments.set(args);
		}
		messages.add(messagePattern);
		args.add(argumentResults.getArguments());
	}

	public void pop()
	{
		List<String> messages = threadLocalMessagePatterns.get();
		if(messages == null)
		{
			return;
		}
		int count=messages.size();
		if(count == 0 || count == 1)
		{
			clear();
			return;
		}
		List<String[]> args = threadLocalMessageArguments.get();
		messages.remove(count - 1);
		args.remove(count -1);
	}

	public int getDepth()
	{
		List<String> messages = threadLocalMessagePatterns.get();
		if(messages == null)
		{
			return 0;
		}
		int count=messages.size();
		if(count == 0)
		{
			// should never happen
			clear();
		}
		return count;
	}

	public void setMaximumDepth(int maximumDepth)
	{
		int overflow = getDepth() - maximumDepth;
		for(int i = 0; i < overflow; i++)
		{
			pop();
		}
	}

	public boolean isEmpty()
	{
		return getDepth()==0;
	}

	public void clear()
	{
		threadLocalMessagePatterns.remove();
		threadLocalMessageArguments.remove();
	}

	public Message[] getContextStack()
	{
		List<String> messages = threadLocalMessagePatterns.get();
		if(messages == null)
		{
			return NO_MESSAGES;
		}
		int count=messages.size();
		if(count == 0)
		{
			// should never happen
			clear();
			return NO_MESSAGES;
		}
		List<String[]> args = threadLocalMessageArguments.get();

		Message[] result=new Message[count];
		for(int i=0;i<count;i++)
		{
			result[i]=new Message(messages.get(i), args.get(i));
		}
		return result;
	}
}
