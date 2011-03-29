/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
package de.huxhorn.lilith.services.clipboard;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoggingMarkerFormatter
	implements ClipboardFormatter
{
	private static final long serialVersionUID = 8972697463195544172L;

	public String getName()
	{
		return "Copy Marker";
	}

	public String getDescription()
	{
		return "Copies the Marker hierarchy of the logging event to the clipboard.";
	}

	public String getAccelerator()
	{
		return null;
	}

	public boolean isCompatible(Object object)
	{
		if(object instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) object;
			if(wrapper.getEvent() != null)
			{
				Object eventObj = wrapper.getEvent();
				if(eventObj instanceof LoggingEvent)
				{
					LoggingEvent loggingEvent = (LoggingEvent) eventObj;
					return loggingEvent.getMarker() != null;
				}
			}
		}
		return false;
	}

	public String toString(Object object)
	{
		if(object instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) object;
			if(wrapper.getEvent() != null)
			{
				Object eventObj = wrapper.getEvent();
				if(eventObj instanceof LoggingEvent)
				{
					LoggingEvent loggingEvent = (LoggingEvent) eventObj;
					Marker marker = loggingEvent.getMarker();
					if(marker != null)
					{
						StringBuilder text = new StringBuilder();
						buildMarker(text, 0, marker, new ArrayList<String>());
						return text.toString();
					}
				}
			}
		}

		return null;
	}

	private void buildMarker(StringBuilder text, int indent, Marker marker, List<String> handledMarkers)
	{
		if(marker != null)
		{
			for(int i = 0; i < indent; i++)
			{
				text.append("  ");
			}
			String markerName = marker.getName();
			text.append("- ").append(markerName);
			if(handledMarkers.contains(markerName))
			{
				text.append(" [..]\n");
			}
			else
			{
				text.append("\n");
				handledMarkers.add(markerName);
				Map<String, Marker> references = marker.getReferences();
				if(references != null)
				{
					for(Map.Entry<String, Marker> current : references.entrySet())
					{
						buildMarker(text, indent + 1, current.getValue(), handledMarkers);
					}
				}
			}
		}
	}
}
