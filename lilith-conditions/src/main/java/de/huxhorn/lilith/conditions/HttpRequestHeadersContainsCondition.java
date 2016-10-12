/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2016 Joern Huxhorn
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
import java.util.Map;

public class HttpRequestHeadersContainsCondition
	implements LilithCondition
{
	private static final long serialVersionUID = -4837874129991318629L;

	public static final String DESCRIPTION = "RequestHeaders.contains";

	private String key;
	private String value;

	public HttpRequestHeadersContainsCondition()
	{
		this(null, null);
	}

	public HttpRequestHeadersContainsCondition(String key, String value)
	{
		this.key = key;
		this.value = value;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public boolean isTrue(Object element)
	{
		if(key == null)
		{
			return false;
		}
		if(element instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) element;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof AccessEvent)
			{
				AccessEvent event = (AccessEvent) eventObj;

				Map<String, String> requestHeaders = event.getRequestHeaders();
				if(requestHeaders == null || requestHeaders.isEmpty())
				{
					return false;
				}
				if(value == null)
				{
					// no value means any value for the given key is true.
					return requestHeaders.containsKey(key);
				}

				return value.equals(requestHeaders.get(key));
			}
		}
		return false;
	}

	@Override
	public HttpRequestHeadersContainsCondition clone() throws CloneNotSupportedException {
		return (HttpRequestHeadersContainsCondition) super.clone();
	}

	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append(getDescription()).append("(");
		if(key != null)
		{
			result.append("\"");
			result.append(key);
			result.append("\"");
		}
		else
		{
			result.append("null");
		}
		result.append(",");
		if(value != null)
		{
			result.append("\"");
			result.append(value);
			result.append("\"");
		}
		else
		{
			result.append("null");
		}
		result.append(")");
		return result.toString();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		HttpRequestHeadersContainsCondition that = (HttpRequestHeadersContainsCondition) o;

		if (key != null ? !key.equals(that.key) : that.key != null) return false;
		if (value != null ? !value.equals(that.value) : that.value != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = key != null ? key.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}

	public String getDescription()
	{
		return DESCRIPTION;
	}
}
