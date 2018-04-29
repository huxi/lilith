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
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import java.io.ObjectStreamException;

public final class HttpStatusCodeCondition
	implements LilithCondition, SearchStringCondition, Cloneable
{
	private static final long serialVersionUID = -3335718950761221210L;

	public static final String DESCRIPTION = "HttpStatusCode==";

	public static final int INVALID_CODE = -1;

	private String searchString;
	private transient int statusCode;

	public HttpStatusCodeCondition()
	{
		this(null);
	}

	public HttpStatusCodeCondition(String searchString)
	{
		setSearchString(searchString);
	}

	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
		if(searchString == null)
		{
			statusCode = INVALID_CODE;
			return;
		}
		String actualString = searchString.trim();
		try
		{
			statusCode = Integer.parseInt(actualString);
			if(statusCode < 100 || statusCode > 599)
			{
				statusCode = INVALID_CODE;
			}
		}
		catch(Throwable e)
		{
			statusCode = INVALID_CODE;
		}
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

	public int getStatusCode()
	{
		return statusCode;
	}

	@Override
	public boolean isTrue(Object value)
	{
		if(statusCode == INVALID_CODE)
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

				return event.getStatusCode() == statusCode;
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		HttpStatusCodeCondition that = (HttpStatusCodeCondition) o;

		return statusCode == that.statusCode;
	}

	@Override
	public int hashCode()
	{
		return statusCode;
	}

	@Override
	public HttpStatusCodeCondition clone()
		throws CloneNotSupportedException
	{
		HttpStatusCodeCondition result = (HttpStatusCodeCondition) super.clone();
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
		if(statusCode == INVALID_CODE)
		{
			result.append("invalid");
		}
		else
		{
			result.append(statusCode);
		}
		return result.toString();
	}
}
