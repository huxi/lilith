/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.huxhorn.lilith.conditions;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;

import java.io.IOException;
import java.io.ObjectInputStream;

public class MessageContainsCondition
	implements LilithCondition
{
	private static final long serialVersionUID = -5047505055619482146L;

	private String searchString;

	public MessageContainsCondition()
	{
		this(null);
	}

	public MessageContainsCondition(String searchString)
	{
		setSearchString(searchString);
	}

	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
	}

	public String getSearchString()
	{
		return searchString;
	}

	public boolean isTrue(Object value)
	{
		if(searchString == null)
		{
			return false;
		}
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) value;
			Object eventObj = wrapper.getEvent();
			if(searchString.length() == 0)
			{
				return true;
			}
			if(eventObj instanceof LoggingEvent)
			{
				LoggingEvent event = (LoggingEvent) eventObj;

				String message = event.getMessage();

				return message != null && message.contains(searchString);
			}
		}
		return false;
	}

	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		final MessageContainsCondition that = (MessageContainsCondition) o;

		return !(searchString != null ? !searchString.equals(that.searchString) : that.searchString != null);
	}

	public int hashCode()
	{
		int result;
		result = (searchString != null ? searchString.hashCode() : 0);
		return result;
	}

	private void readObject(ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		setSearchString(this.searchString);
	}

	public MessageContainsCondition clone()
		throws CloneNotSupportedException
	{
		return (MessageContainsCondition) super.clone();
	}

	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append(getDescription()).append("(");
		if(searchString != null)
		{
			result.append("\"");
			result.append(searchString);
			result.append("\"");
		}
		else
		{
			result.append("null");
		}
		result.append(")");
		return result.toString();
	}

	public String getDescription()
	{
		return "message.contains";
	}
}
