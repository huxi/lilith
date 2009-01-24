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
package de.huxhorn.lilith.engine.impl;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.conditions.Condition;

import java.io.Serializable;

public class EventSourceImpl<T extends Serializable>
	implements EventSource<T>
{
	private SourceIdentifier sourceIdentifier;
	private Buffer<EventWrapper<T>> buffer;
	private boolean global;
	private Condition filter;

	public EventSourceImpl(SourceIdentifier sourceIdentifier, Buffer<EventWrapper<T>> buffer, boolean global)
	{
		this(sourceIdentifier, buffer, null, global);
	}

	public EventSourceImpl(SourceIdentifier sourceIdentifier, Buffer<EventWrapper<T>> buffer, Condition filter, boolean global)
	{
		this.sourceIdentifier = sourceIdentifier;
		this.buffer = buffer;
		this.filter = filter;
		this.global = global;
	}

	public boolean isGlobal()
	{
		return global;
	}

	public SourceIdentifier getSourceIdentifier()
	{
		return sourceIdentifier;
	}

	public Buffer<EventWrapper<T>> getBuffer()
	{
		return buffer;
	}

	public Condition getFilter()
	{
		return filter;
	}

	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		final EventSourceImpl that = (EventSourceImpl) o;

		if(filter != null ? !filter.equals(that.filter) : that.filter != null) return false;
		return !(sourceIdentifier != null ? !sourceIdentifier
			.equals(that.sourceIdentifier) : that.sourceIdentifier != null);

	}

	public int hashCode()
	{
		int result;
		result = (sourceIdentifier != null ? sourceIdentifier.hashCode() : 0);
		result = 29 * result + (filter != null ? filter.hashCode() : 0);
		return result;
	}

	public int compareTo(EventSource<T> o)
	{
		if(global)
		{
			if(!o.isGlobal())
			{
				return -1;
			}
		}
		else
		{
			if(o.isGlobal())
			{
				return 1;
			}
		}

		if(sourceIdentifier == null)
		{
			if(o.getSourceIdentifier() != null)
			{
				return -1;
			}
			//return 0;
		}
		else
		{
			if(o.getSourceIdentifier() == null)
			{
				return 1;
			}
			int result = sourceIdentifier.compareTo(o.getSourceIdentifier());
			if(result != 0)
			{
				return result;
			}
		}

		if(filter == null)
		{
			if(o.getFilter() != null)
			{
				return 1; // unfiltered is bigger
			}
			return 0;
		}
		else
		{
			Condition otherFilter = o.getFilter();
			if(otherFilter == null)
			{
				return -1; // unfiltered is bigger
			}
			return filter.toString().compareTo(otherFilter.toString());
		}
	}

	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("EventSource[");
		result.append("id=").append(sourceIdentifier);
		result.append(", ");
		result.append("filter=").append(filter);
		result.append(", ");
		result.append("global=").append(global);
		result.append("]");
		return result.toString();
	}
}
