/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

public final class LoggerStartsWithCondition
	implements LilithCondition, SearchStringCondition, Cloneable
{
	private static final long serialVersionUID = -9144937393832722541L;

	public static final String DESCRIPTION = "logger.startsWith";

	private String searchString;

	public LoggerStartsWithCondition()
	{
		this(null);
	}

	public LoggerStartsWithCondition(String searchString)
	{
		setSearchString(searchString);
	}

	public void setSearchString(String searchString)
	{
		if(searchString != null)
		{
			searchString = searchString.replace('$','.');  // better handling of inner classes
		}
		this.searchString = searchString;
	}

	@Override
	public String getSearchString()
	{
		return searchString;
	}

	@Override
	public boolean isTrue(Object value)
	{
		if(searchString == null)
		{
			return false;
		}
		if(searchString.length() == 0)
		{
			return true;
		}
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) value;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof LoggingEvent)
			{
				LoggingEvent event = (LoggingEvent) eventObj;

				String logger = event.getLogger();
				if(logger == null)
				{
					return false;
				}
				logger=logger.replace('$', '.'); // better handling of inner classes
				return logger.startsWith(searchString);
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		final LoggerStartsWithCondition that = (LoggerStartsWithCondition) o;

		return !(searchString != null ? !searchString.equals(that.searchString) : that.searchString != null);
	}

	@Override
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

	@Override
	public LoggerStartsWithCondition clone()
		throws CloneNotSupportedException
	{
		return (LoggerStartsWithCondition) super.clone();
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append(getDescription()).append('(');
		if(searchString != null)
		{
			result.append('"').append(searchString).append('"');
		}
		else
		{
			result.append("null");
		}
		result.append(')');
		return result.toString();
	}

	@Override
	public String getDescription()
	{
		return DESCRIPTION;
	}
}
