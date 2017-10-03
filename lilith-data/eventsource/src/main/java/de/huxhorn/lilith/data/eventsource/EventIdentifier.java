/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

/*
 * Copyright 2007-2017 Joern Huxhorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EventIdentifier that = (EventIdentifier) o;

		return localId == that.localId
				&& (sourceIdentifier != null ? sourceIdentifier.equals(that.sourceIdentifier) : that.sourceIdentifier == null);
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
		return "EventIdentifier{" +
				"sourceIdentifier=" + sourceIdentifier +
				", localId=" + localId +
				'}';
	}
}
