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

public class HttpRequestUrlCondition
	implements LilithCondition, SearchStringCondition, Cloneable
{
	private static final long serialVersionUID = -2026756450071662287L;

	public static final String DESCRIPTION = "HttpRequestURL==";

	private String searchString;

	public HttpRequestUrlCondition()
	{
		this(null);
	}

	public HttpRequestUrlCondition(String searchString)
	{
		this.searchString=searchString;
	}

	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
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

	@Override
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
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) value;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof AccessEvent)
			{
				AccessEvent event = (AccessEvent) eventObj;

				return searchString.equals(event.getRequestURL());
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		HttpRequestUrlCondition that = (HttpRequestUrlCondition) o;

		return searchString != null ? searchString.equals(that.searchString) : that.searchString == null;
	}

	@Override
	public int hashCode()
	{
		return searchString != null ? searchString.hashCode() : 0;
	}

	@Override
	public HttpRequestUrlCondition clone()
		throws CloneNotSupportedException
	{
		return (HttpRequestUrlCondition) super.clone();
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append(getDescription());
		if(searchString != null)
		{
			result.append('"').append(searchString).append('"');
		}
		else
		{
			result.append("null");
		}
		return result.toString();
	}
}
