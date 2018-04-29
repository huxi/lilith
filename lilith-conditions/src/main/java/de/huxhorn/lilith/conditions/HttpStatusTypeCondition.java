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

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.access.HttpStatus;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import java.io.ObjectStreamException;
import java.util.Locale;

public final class HttpStatusTypeCondition
	implements LilithCondition, SearchStringCondition, Cloneable
{
	private static final long serialVersionUID = -3335718950761221210L;

	public static final String DESCRIPTION = "HttpStatusType==";

	private String searchString;
	private transient HttpStatus.Type statusType;

	public HttpStatusTypeCondition()
	{
		this(null);
	}

	public HttpStatusTypeCondition(String searchString)
	{
		setSearchString(searchString);
	}

	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
		if(searchString == null)
		{
			statusType = null;
			return;
		}

		String actualString = searchString.trim();
		if("".equals(actualString))
		{
			statusType = null;
			return;
		}
		try
		{
			statusType = HttpStatus.Type.valueOf(searchString);
			return;
		}
		catch (Throwable t)
		{
			// ignore
		}
		actualString = actualString.toLowerCase(Locale.ENGLISH);
		for(HttpStatus.Type current : HttpStatus.Type.values())
		{
			if(current.toString().toLowerCase(Locale.ENGLISH).startsWith(actualString))
			{
				statusType = current;
				return;
			}
			if(current.getRange().startsWith(actualString))
			{
				statusType = current;
				return;
			}
		}
		statusType = null;
	}

	@Override
	public String getSearchString()
	{
		return searchString;
	}

	@Override
	public String getDescription()
	{
		return DESCRIPTION;
	}

	public HttpStatus.Type getStatusType()
	{
		return statusType;
	}

	@Override
	public boolean isTrue(Object value)
	{
		if(statusType == null)
		{
			return false;
		}
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) value;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof AccessEvent)
			{
				AccessEvent event = (AccessEvent) eventObj;

				HttpStatus.Type eventType = HttpStatus.getType(event.getStatusCode());
				return eventType == statusType;
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		HttpStatusTypeCondition that = (HttpStatusTypeCondition) o;

		return statusType == that.statusType;
	}

	@Override
	public int hashCode()
	{
		return (statusType != null ? statusType.hashCode() : 0);
	}

	@Override
	public HttpStatusTypeCondition clone()
		throws CloneNotSupportedException
	{
		HttpStatusTypeCondition result = (HttpStatusTypeCondition) super.clone();
		result.setSearchString(searchString);
		return result;
	}

	private Object readResolve()
		throws ObjectStreamException
	{
		setSearchString(searchString);
		return this;
	}

	@Override
	public String toString()
	{
		return getDescription() + statusType;
	}
}
