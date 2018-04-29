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

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;

public class MarkerContainsCondition
	implements LilithCondition, SearchStringCondition, Cloneable
{
	private static final long serialVersionUID = -4925872725394540757L;

	public static final String DESCRIPTION = "Marker.contains";

	private String searchString;

	public MarkerContainsCondition()
	{
		this(null);
	}

	public MarkerContainsCondition(String searchString)
	{
		this.searchString = searchString;
	}

	@Override
	public String getSearchString()
	{
		return searchString;
	}

	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
	}

	@Override
	public boolean isTrue(Object element)
	{
		if(searchString == null)
		{
			return false;
		}
		if(searchString.length() == 0)
		{
			return true;
		}
		if(element instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) element;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof LoggingEvent)
			{
				LoggingEvent event = (LoggingEvent) eventObj;

				Marker marker = event.getMarker();
				return marker != null && marker.contains(searchString);
			}
		}
		return false;
	}

	@Override
	public MarkerContainsCondition clone() throws CloneNotSupportedException
	{
		return (MarkerContainsCondition) super.clone();
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append(getDescription()).append('(');
		if(searchString != null)
		{
			result.append('"').append(searchString).append('"');
		}
		else
		{
			result.append("null");
		}
		result.append(')');
		return result.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MarkerContainsCondition that = (MarkerContainsCondition) o;

		return searchString != null ? searchString.equals(that.searchString) : that.searchString == null;
	}

	@Override
	public int hashCode() {
		return searchString != null ? searchString.hashCode() : 0;
	}

	@Override
	public String getDescription()
	{
		return DESCRIPTION;
	}
}
