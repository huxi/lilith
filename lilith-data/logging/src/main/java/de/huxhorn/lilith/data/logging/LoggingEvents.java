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
package de.huxhorn.lilith.data.logging;

import de.huxhorn.lilith.data.eventsource.SourceIdentifier;

import java.io.Serializable;
import java.util.List;

public class LoggingEvents
	implements Serializable
{
	private static final long serialVersionUID = -3632476446868236269L;

	private long startIndex;
	private SourceIdentifier sourceIdentifier;
	private List<LoggingEvent> events;

	public long getStartIndex()
	{
		return startIndex;
	}

	public void setStartIndex(long startIndex)
	{
		this.startIndex = startIndex;
	}

	public SourceIdentifier getSource()
	{
		return sourceIdentifier;
	}

	public void setSource(SourceIdentifier sourceIdentifier)
	{
		this.sourceIdentifier = sourceIdentifier;
	}

	public List<LoggingEvent> getEvents()
	{
		return events;
	}

	public void setEvents(List<LoggingEvent> events)
	{
		this.events = events;
	}

	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		LoggingEvents that = (LoggingEvents) o;

		if(startIndex != that.startIndex) return false;
		if(events != null ? !events.equals(that.events) : that.events != null) return false;
		if(sourceIdentifier != null ? !sourceIdentifier.equals(that.sourceIdentifier) : that.sourceIdentifier != null)
		{
			return false;
		}

		return true;
	}

	public int hashCode()
	{
		int result;
		result = (int) (startIndex ^ (startIndex >>> 32));
		result = 31 * result + (sourceIdentifier != null ? sourceIdentifier.hashCode() : 0);
		result = 31 * result + (events != null ? events.hashCode() : 0);
		return result;
	}
}
