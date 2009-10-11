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

public class LevelCondition
	implements LilithCondition
{
	private static final long serialVersionUID = -5498023202272568557L;

	private String searchString;
	private transient LoggingEvent.Level level;

	public LevelCondition()
	{
		this(null);
	}

	public LevelCondition(String searchString)
	{
		setSearchString(searchString);
	}

	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
		try
		{
			level = LoggingEvent.Level.valueOf(searchString);
		}
		catch(Throwable e)
		{
			level = null;
		}
	}

	public String getSearchString()
	{
		return searchString;
	}

	public String getDescription()
	{
		return "Level>=";
	}

	public boolean isTrue(Object value)
	{
		if(level == null)
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

				LoggingEvent.Level eventLevel = event.getLevel();
				return eventLevel != null && level.compareTo(eventLevel) <= 0;
			}
		}
		return false;
	}

	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		LevelCondition that = (LevelCondition) o;

		return level == that.level;
	}

	public int hashCode()
	{
		return (level != null ? level.hashCode() : 0);
	}

	public LevelCondition clone()
		throws CloneNotSupportedException
	{
		LevelCondition result = (LevelCondition) super.clone();
		result.setSearchString(searchString);
		return result;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append(getDescription());
		result.append(level);
		return result.toString();
	}
}
