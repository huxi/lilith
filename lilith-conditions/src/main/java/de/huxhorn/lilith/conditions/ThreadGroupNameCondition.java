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
import de.huxhorn.lilith.data.logging.ThreadInfo;

public final class ThreadGroupNameCondition
	implements LilithCondition, SearchStringCondition, Cloneable
{
	private static final long serialVersionUID = 7387711195995084613L;

	public static final String DESCRIPTION = "ThreadGroupName";

	private String searchString;

	public ThreadGroupNameCondition()
	{
		this(null);
	}

	public ThreadGroupNameCondition(String searchString)
	{
		setSearchString(searchString);
	}

	public void setSearchString(String searchString)
	{
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
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) value;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof LoggingEvent)
			{
				LoggingEvent event = (LoggingEvent) eventObj;

				ThreadInfo threadInfo = event.getThreadInfo();
				if(threadInfo == null)
				{
					return false;
				}
				if(searchString == null || "".equals(searchString))
				{
					// no search string means "match any event with thread info"
					return true;
				}

				if(searchString.equals(threadInfo.getGroupName()))
				{
					return true;
				}
			}
		}
		return false;
	}


	@Override
	public ThreadGroupNameCondition clone()
		throws CloneNotSupportedException
	{
		ThreadGroupNameCondition result = (ThreadGroupNameCondition) super.clone();
		result.setSearchString(searchString);
		return result;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ThreadGroupNameCondition that = (ThreadGroupNameCondition) o;

		return searchString != null ? searchString.equals(that.searchString) : that.searchString == null;

	}

	@Override
	public int hashCode()
	{
		return searchString != null ? searchString.hashCode() : 0;
	}

	@Override
	public String getDescription()
	{
		return DESCRIPTION;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append(getDescription()).append('(');
		if(searchString != null)
		{
			result.append(searchString);
		}
		result.append(')');
		return result.toString();
	}
}
