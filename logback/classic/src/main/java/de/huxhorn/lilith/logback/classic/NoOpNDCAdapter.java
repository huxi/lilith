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

public class NoOpNDCAdapter
	implements NDCAdapter
{
	public void push(String message)
	{
	}

	public void push(String messagePattern, Object[] arguments)
	{
	}

	public void pop()
	{
	}

	public int getDepth()
	{
		return 0;
	}

	public void setMaximumDepth(int maximumDepth)
	{
	}

	public boolean isEmpty()
	{
		return true;
	}

	public void clear()
	{
	}

	public Message[] getContextStack()
	{
		return NO_MESSAGES;
	}
}
