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

public class LoggerEqualsCondition
	implements LilithCondition
{
	private static final long serialVersionUID = -7972026995883628318L;

	private String searchString;

	public LoggerEqualsCondition()
	{
		this(null);
	}

	public LoggerEqualsCondition(String searchString)
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
			if(eventObj instanceof LoggingEvent)
			{
				LoggingEvent event = (LoggingEvent) eventObj;

				String logger = event.getLogger();

				return logger != null && logger.equals(searchString);
			}
		}
		return false;
	}

	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		final LoggerEqualsCondition that = (LoggerEqualsCondition) o;

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

	public LoggerEqualsCondition clone()
		throws CloneNotSupportedException
	{
		return (LoggerEqualsCondition) super.clone();
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
		return "logger.equals";
	}
}