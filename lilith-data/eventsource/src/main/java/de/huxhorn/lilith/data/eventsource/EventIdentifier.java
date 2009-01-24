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
package de.huxhorn.lilith.data.eventsource;

import java.io.Serializable;

public class EventIdentifier
	implements Serializable, Cloneable
{
	private static final long serialVersionUID = -4019164988350212853L;

	public static final long NO_LOCAL_ID = -1;

	private SourceIdentifier sourceIdentifier;
	private long localId;

	public EventIdentifier()
	{
		this(null, NO_LOCAL_ID);
	}

	public EventIdentifier(SourceIdentifier sourceIdentifier, long localId)
	{
		this.sourceIdentifier = sourceIdentifier;
		this.localId = localId;
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

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		EventIdentifier that = (EventIdentifier) o;

		if(localId != that.localId) return false;
		if(sourceIdentifier != null ? !sourceIdentifier.equals(that.sourceIdentifier) : that.sourceIdentifier != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = sourceIdentifier != null ? sourceIdentifier.hashCode() : 0;
		result = 31 * result + (int) (localId ^ (localId >>> 32));
		return result;
	}

	@Override
	public EventIdentifier clone()
		throws CloneNotSupportedException
	{
		EventIdentifier result = (EventIdentifier) super.clone();
		if(sourceIdentifier != null)
		{
			result.sourceIdentifier = sourceIdentifier.clone();
		}
		return result;
	}

	@Override
	public String toString()
	{
		return "EventIdentifier[sourceIdentifier=" + sourceIdentifier + ", localId=" + localId + "]";
	}
}
