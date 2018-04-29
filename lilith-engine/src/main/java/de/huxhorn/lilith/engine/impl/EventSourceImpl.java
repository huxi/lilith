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
package de.huxhorn.lilith.engine.impl;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.conditions.Condition;
import java.io.Serializable;
import java.util.Objects;

public class EventSourceImpl<T extends Serializable>
	implements EventSource<T>
{
	private final SourceIdentifier sourceIdentifier;
	private final Buffer<EventWrapper<T>> buffer;
	private final boolean global;
	private final Condition filter;

	public EventSourceImpl(SourceIdentifier sourceIdentifier, Buffer<EventWrapper<T>> buffer, boolean global)
	{
		this(sourceIdentifier, buffer, null, global);
	}

	public EventSourceImpl(SourceIdentifier sourceIdentifier, Buffer<EventWrapper<T>> buffer, Condition filter, boolean global)
	{
		this.sourceIdentifier = Objects.requireNonNull(sourceIdentifier, "sourceIdentifier must not be null!");
		this.buffer = Objects.requireNonNull(buffer, "buffer must not be null!");
		this.filter = filter;
		this.global = global;
	}

	@Override
	public boolean isGlobal()
	{
		return global;
	}

	@Override
	public SourceIdentifier getSourceIdentifier()
	{
		return sourceIdentifier;
	}

	@Override
	public Buffer<EventWrapper<T>> getBuffer()
	{
		return buffer;
	}

	@Override
	public Condition getFilter()
	{
		return filter;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EventSourceImpl<?> that = (EventSourceImpl<?>) o;

		if (global != that.global) return false;
		if (!sourceIdentifier.equals(that.sourceIdentifier)) return false;
		return filter != null ? filter.equals(that.filter) : that.filter == null;
	}

	@Override
	public int hashCode()
	{
		int result = sourceIdentifier.hashCode();
		result = 31 * result + (global ? 1 : 0);
		result = 31 * result + (filter != null ? filter.hashCode() : 0);
		return result;
	}

	@Override
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

		int result = sourceIdentifier.compareTo(o.getSourceIdentifier());
		if(result != 0)
		{
			return result;
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

	@Override
	public String toString()
	{
		return "EventSource[" +
				"id=" + sourceIdentifier +
				", " +
				"filter=" + filter +
				", " +
				"global=" + global +
				"]";
	}
}
