/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
 * Copyright 2007-2011 Joern Huxhorn
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
import java.util.Date;

public class SourceInfo
	implements Serializable
{
	private static final long serialVersionUID = -8802196344031020166L;

	private long numberOfEvents;
	private Date oldestEventTimestamp;
	private boolean active;
	private SourceIdentifier sourceIdentifier;

	public long getNumberOfEvents()
	{
		return numberOfEvents;
	}

	public void setNumberOfEvents(long numberOfEvents)
	{
		this.numberOfEvents = numberOfEvents;
	}

	public Date getOldestEventTimestamp()
	{
		return oldestEventTimestamp;
	}

	public void setOldestEventTimestamp(Date oldestEventTimestamp)
	{
		this.oldestEventTimestamp = oldestEventTimestamp;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public SourceIdentifier getSource()
	{
		return sourceIdentifier;
	}

	public void setSource(SourceIdentifier sourceIdentifier)
	{
		this.sourceIdentifier = sourceIdentifier;
	}

	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		final SourceInfo that = (SourceInfo) o;

		if(active != that.active) return false;
		if(numberOfEvents != that.numberOfEvents) return false;
		if(oldestEventTimestamp != null ? !oldestEventTimestamp
			.equals(that.oldestEventTimestamp) : that.oldestEventTimestamp != null)
		{
			return false;
		}
		return !(sourceIdentifier != null ? !sourceIdentifier
			.equals(that.sourceIdentifier) : that.sourceIdentifier != null);
	}

	public int hashCode()
	{
		int result;
		result = (int) (numberOfEvents ^ (numberOfEvents >>> 32));
		result = 29 * result + (oldestEventTimestamp != null ? oldestEventTimestamp.hashCode() : 0);
		result = 29 * result + (active ? 1 : 0);
		result = 29 * result + (sourceIdentifier != null ? sourceIdentifier.hashCode() : 0);
		return result;
	}
}
