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

import java.util.ArrayList;
import java.util.List;

public class SimpleNDCAdapter
	implements NDCAdapter
{
	private CloningNdcStackThreadLocal ndcStackThreadLocal = new CloningNdcStackThreadLocal();

	public void push(String message)
	{
		getNdcStack().push(message);
	}

	public void push(String messagePattern, Object[] arguments)
	{
		getNdcStack().push(messagePattern, arguments);
	}

	public void pop()
	{
		getNdcStack().pop();
	}

	public int getDepth()
	{
		return getNdcStack().getDepth();
	}

	public void setMaximumDepth(int maximumDepth)
	{
		getNdcStack().setMaximumDepth(maximumDepth);
	}

	public boolean isEmpty()
	{
		return getNdcStack().isEmpty();
	}

	public void clear()
	{
		getNdcStack().clear();
	}

	public Message[] getContextStack()
	{
		return getNdcStack().getContextStack();
	}

	private NdcStack getNdcStack()
	{
		NdcStack result = ndcStackThreadLocal.get();
		if(result == null)
		{
			result = new NdcStack();
			ndcStackThreadLocal.set(result);
		}
		return result;
	}

	private static class CloningNdcStackThreadLocal
		extends InheritableThreadLocal<NdcStack>
	{
		@Override
		protected NdcStack childValue(NdcStack parentValue)
		{
			NdcStack result = null;
			if(parentValue != null)
			{
				// this method seems to get called only if parent
				// is not null but this isn't documented so I'll make sure...
				try
				{
					result = parentValue.clone();
				}
				catch(CloneNotSupportedException e)
				{
					// can't happen, see above...
				}
			}
			return result;
		}
	}

	private static class NdcStack
		implements Cloneable
	{
		private List<Message> stackList;

		private NdcStack()
		{
			stackList = new ArrayList<Message>();
		}

		public int getDepth()
		{
			return stackList.size();
		}

		public void setMaximumDepth(int maximumDepth)
		{
			int overflow = stackList.size() - maximumDepth;
			for(int i = 0; i < overflow; i++)
			{
				pop();
			}
		}

		public void push(String message)
		{
			stackList.add(new Message(message));
		}

		public void push(String messagePattern, Object[] arguments)
		{
			if(arguments == null || arguments.length == 0)
			{
				push(messagePattern);
				return;
			}
			MessageFormatter.ArgumentResult argumentResults = MessageFormatter
				.evaluateArguments(messagePattern, arguments);
			if(argumentResults == null)
			{
				// this should not be possible but I'm paranoid...
				stackList.add(new Message(messagePattern, null));
			}
			else
			{
				stackList.add(new Message(messagePattern, argumentResults.getArguments()));
			}
		}

		public void pop()
		{
			int size = stackList.size();
			if(size > 0)
			{
				stackList.remove(size - 1);
			}
		}

		public boolean isEmpty()
		{
			return stackList.isEmpty();
		}

		public void clear()
		{
			stackList.clear();
		}

		public Message[] getContextStack()
		{
			if(stackList.isEmpty())
			{
				return NO_MESSAGES;
			}

			Message[] result = new Message[stackList.size()];
			try
			{
				for(int i = 0; i < stackList.size(); i++)
				{
					result[i] = stackList.get(i).clone();
				}
			}
			catch(CloneNotSupportedException e)
			{
				// can't happen... yeah, I know... it *will* happen one day :p
			}
			return result;
		}

		public NdcStack clone()
			throws CloneNotSupportedException
		{
			NdcStack result = (NdcStack) super.clone();

			ArrayList<Message> clonedStackList = new ArrayList<Message>(stackList.size());
			for(Message current : stackList)
			{
				clonedStackList.add(current.clone());
			}
			result.stackList = clonedStackList;

			return result;
		}
	}
}
