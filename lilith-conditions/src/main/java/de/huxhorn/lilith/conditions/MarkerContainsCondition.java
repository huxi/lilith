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
import de.huxhorn.lilith.data.logging.Marker;

public class MarkerContainsCondition
	implements LilithCondition
{
	private static final long serialVersionUID = -4925872725394540757L;

	public static final String DESCRIPTION = "Marker.contains";

	private String markerName;

	public MarkerContainsCondition()
	{
		this(null);
	}

	public MarkerContainsCondition(String markerName)
	{
		this.markerName = markerName;
	}

	public String getMarkerName() {
		return markerName;
	}

	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}

	@Override
	public boolean isTrue(Object element)
	{
		if(markerName == null)
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

				Marker marker = event.getMarker();
				if(marker == null)
				{
					return false;
				}
				return marker.contains(markerName);
			}
		}
		return false;
	}

	@Override
	public MarkerContainsCondition clone() throws CloneNotSupportedException {
		return (MarkerContainsCondition) super.clone();
	}

	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append(getDescription()).append("(");
		if(markerName != null)
		{
			result.append("\"");
			result.append(markerName);
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MarkerContainsCondition that = (MarkerContainsCondition) o;

		if (markerName != null ? !markerName.equals(that.markerName) : that.markerName != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return markerName != null ? markerName.hashCode() : 0;
	}

	public String getDescription()
	{
		return DESCRIPTION;
	}
}
