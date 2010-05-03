/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
package de.huxhorn.lilith.data.eventsource;

import java.io.Serializable;

public class EventWrapper<T extends Serializable>
	implements Serializable
{

	private EventIdentifier eventIdentifier;
	private T event;

	/**
	 * This attribute is ignored in equals and hashCode.
	 * It's transient and won't survive serialization.
	 */
	private transient TransferSizeInfo transferSizeInfo;
	private static final long serialVersionUID = 6302031645772429174L;

	public EventWrapper()
	{
		this(null, -1, null);
	}

	public EventWrapper(SourceIdentifier sourceIdentifier, long localId, T event)
	{
		this(new EventIdentifier(sourceIdentifier, localId), event);
	}

	public EventWrapper(EventIdentifier eventIdentifier, T event)
	{
		this.eventIdentifier = eventIdentifier;
		this.event = event;
	}

	public SourceIdentifier getSourceIdentifier()
	{
		if(eventIdentifier != null)
		{
			return eventIdentifier.getSourceIdentifier();
		}
		return null;
	}

	public void setSourceIdentifier(SourceIdentifier sourceIdentifier)
	{
		if(eventIdentifier == null)
		{
			eventIdentifier = new EventIdentifier();
		}
		eventIdentifier.setSourceIdentifier(sourceIdentifier);
	}

	public long getLocalId()
	{
		if(eventIdentifier != null)
		{
			return eventIdentifier.getLocalId();
		}
		return EventIdentifier.NO_LOCAL_ID;
	}

	public void setLocalId(long localId)
	{
		if(eventIdentifier == null)
		{
			eventIdentifier = new EventIdentifier();
		}
		eventIdentifier.setLocalId(localId);
	}

	public EventIdentifier getEventIdentifier()
	{
		return eventIdentifier;
	}

	public void setEventIdentifier(EventIdentifier eventIdentifier)
	{
		this.eventIdentifier = eventIdentifier;
	}

	public TransferSizeInfo getTransferSizeInfo()
	{
		return transferSizeInfo;
	}

	public void setTransferSizeInfo(TransferSizeInfo transferSizeInfo)
	{
		this.transferSizeInfo = transferSizeInfo;
	}

	public T getEvent()
	{
		return event;
	}

	public void setEvent(T event)
	{
		this.event = event;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		EventWrapper that = (EventWrapper) o;

		if(eventIdentifier != null ? !eventIdentifier.equals(that.eventIdentifier) : that.eventIdentifier != null)
		{
			return false;
		}
		if(event != null ? !event.equals(that.event) : that.event != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		if(eventIdentifier != null)
		{
			return eventIdentifier.hashCode();
		}
		return (event != null ? event.hashCode() : 0);
	}

	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("eventWrapper[");
		result.append("eventIdentifier=").append(eventIdentifier);
		result.append(", event=").append(event);
		result.append("]");
		return result.toString();
	}
}
