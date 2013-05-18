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

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;

import java.util.Map;

public class MDCContainsCondition
	implements LilithCondition
{
	private static final long serialVersionUID = -7175187972553499074L;

	public static final String DESCRIPTION = "MDC.contains";

	private String key;
	private String value;

	public MDCContainsCondition()
	{
		this(null, null);
	}

	public MDCContainsCondition(String key, String value)
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
			if(eventObj instanceof LoggingEvent)
			{
				LoggingEvent event = (LoggingEvent) eventObj;

				Map<String, String> mdc = event.getMdc();
				if(mdc == null || mdc.isEmpty())
				{
					return false;
				}

				String actualValue = mdc.get(key);
				//noinspection StringEquality
				return actualValue == value || value != null && value.equals(actualValue);
			}
		}
		return false;
	}

	@Override
	public MDCContainsCondition clone() throws CloneNotSupportedException {
		return (MDCContainsCondition) super.clone();
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

		MDCContainsCondition that = (MDCContainsCondition) o;

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
