/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2013 Joern Huxhorn
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

public class HttpStatusTypeCondition
	implements LilithCondition, SearchStringCondition
{
	private static final long serialVersionUID = -3335718950761221210L;

	public static final String DESCRIPTION = "HttpStatusType==";

	private String searchString;
	private transient HttpStatus.Type type;

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
		try
		{
			type = HttpStatus.Type.valueOf(searchString);
		}
		catch(Throwable e)
		{
			type = null;
		}
	}

	public String getSearchString()
	{
		return searchString;
	}

	public String getDescription()
	{
		return DESCRIPTION;
	}

	public boolean isTrue(Object value)
	{
		if(searchString == null)
		{
			return false;
		}
		if(searchString.length() == 0)
		{
			return true;
		}
		if(type == null)
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
				return eventType == type;
			}
		}
		return false;
	}

	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		HttpStatusTypeCondition that = (HttpStatusTypeCondition) o;

		return type == that.type;
	}

	public int hashCode()
	{
		return (type != null ? type.hashCode() : 0);
	}

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
		StringBuilder result = new StringBuilder();
		result.append(getDescription());
		result.append(type);
		return result.toString();
	}
}
