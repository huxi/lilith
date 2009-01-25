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
import de.huxhorn.lilith.data.logging.MessageFormatter;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class SimpleNDCAdapter
	implements NDCAdapter
{
	private InheritableThreadLocal<List<Message>> inheritableThreadLocal = new InheritableThreadLocal<List<Message>>();

	private List<Message> getStackList()
	{
		List<Message> result=inheritableThreadLocal.get();
		if(result==null)
		{
			result=new ArrayList<Message>();
			inheritableThreadLocal.set(result);
		}
		return result;
	}

	public void push(String message)
	{
		List<Message> stackList = getStackList();
		stackList.add(new Message(message));
	}

	public void push(String messagePattern, Object[] arguments)
	{
		if(arguments==null || arguments.length==0)
		{
			push(messagePattern);
			return;
		}
		List<Message> stackList = getStackList();
		MessageFormatter.ArgumentResult argumentResults = MessageFormatter.evaluateArguments(messagePattern, arguments);
		if(argumentResults==null)
		{
			System.out.println("messagePattern="+messagePattern+", arguments="+ Arrays.toString(arguments));
		}
		else
		stackList.add(new Message(messagePattern, argumentResults.getArguments()));
	}

	public void pop()
	{
		List<Message> stackList = getStackList();
		int size=stackList.size();
		if(size>0)
		{
			stackList.remove(size-1);
		}
	}

	public boolean isEmpty()
	{
		return getStackList().isEmpty();
	}

	public void clear()
	{
		getStackList().clear();
	}

	public Message[] getContextStack()
	{
		List<Message> stackList = getStackList();
		if(stackList.isEmpty())
		{
			return NO_MESSAGES;
		}

		Message[] result=new Message[stackList.size()];
		try
		{
			for(int i=0;i<stackList.size();i++)
			{
				result[i]=stackList.get(i).clone();
			}
		}
		catch(CloneNotSupportedException e)
		{
			// can't happen... yeah, I know... it *will* happen one day :p
		}
		return result;
	}
}
