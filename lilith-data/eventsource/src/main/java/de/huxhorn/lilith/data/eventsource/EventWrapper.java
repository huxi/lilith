/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
	private static final long serialVersionUID = 3808543272632091647L;

	private SourceIdentifier sourceIdentifier;
	private T event;
	private long localId;
	
	/**
	 * This attribute is ignored in equals and hashCode.
	 * It's transient and won't survive serialization.
	 */
	private transient TransferSizeInfo transferSizeInfo;

	public EventWrapper(SourceIdentifier sourceIdentifier, long localId, T event)
	{
		this.sourceIdentifier=sourceIdentifier;
		this.localId=localId;
		this.event=event;
	}

	public EventWrapper()
	{
		this(null, -1, null);
	}

	public SourceIdentifier getSourceIdentifier()
	{
		return sourceIdentifier;
	}

	public void setSourceIdentifier(SourceIdentifier sourceIdentifier)
	{
		this.sourceIdentifier = sourceIdentifier;
	}

	public long getLocalId()
	{
		return localId;
	}

	public void setLocalId(long localId)
	{
		this.localId = localId;
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

	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EventWrapper that = (EventWrapper) o;

		if (localId != that.localId) return false;
		if (event != null ? !event.equals(that.event) : that.event != null) return false;
		if (sourceIdentifier != null ? !sourceIdentifier.equals(that.sourceIdentifier) : that.sourceIdentifier != null)
			return false;

		return true;
	}

	public int hashCode()
	{
		int result;
		result = (sourceIdentifier != null ? sourceIdentifier.hashCode() : 0);
		result = 31 * result + (event != null ? event.hashCode() : 0);
		result = 31 * result + (int) (localId ^ (localId >>> 32));
		return result;
	}

	public String toString()
	{
		StringBuilder result=new StringBuilder();
		result.append("eventWrapper[");
		result.append("sourceIdentifier=").append(sourceIdentifier);
		result.append(", ");
		result.append("localId=").append(localId);
		result.append(", ");
		result.append("event=").append(event);
		result.append("]");
		return result.toString();
	}
}
