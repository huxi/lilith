/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
package de.huxhorn.lilith.swing.table.tooltips;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.swing.table.TooltipGenerator;
import de.huxhorn.sulky.formatting.SimpleXml;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JTable;

public class MarkerTooltipGenerator
	implements TooltipGenerator
{
	@Override
	public String createTooltipText(JTable table, int row)
	{
		String tooltip = null;
		Object value = table.getValueAt(row, 0);
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) value;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof LoggingEvent)
			{
				LoggingEvent event = (LoggingEvent) eventObj;
				Marker marker = event.getMarker();
				if(marker != null)
				{
					StringBuilder buffer = new StringBuilder();
					buffer.append("<html>");
					appendMarker(marker, buffer, null, false, true);
					buffer.append("</html>");
					tooltip = buffer.toString();
				}
			}
		}
		return tooltip;
	}

	public static void appendMarker(Marker marker, StringBuilder buffer, List<String> processedMarkers, boolean xhtml, boolean first)
	{
		if(marker != null)
		{
			if(processedMarkers == null)
			{
				processedMarkers = new ArrayList<>();
			}
			String markerName = marker.getName();
			buffer.append(SimpleXml.escape(markerName));
			if(first)
			{
				if(xhtml)
				{
					buffer.append("<br/>");
				}
				else
				{
					buffer.append("<br>");
				}
			}
			if(!processedMarkers.contains(markerName))
			{
				processedMarkers.add(markerName);
				if(marker.hasReferences())
				{
					buffer.append("<ul>");
					Map<String, Marker> children = marker.getReferences();
					for(Map.Entry<String, Marker> current : children.entrySet())
					{

						Marker childMarker = current.getValue();
						buffer.append("<li>");
						appendMarker(childMarker, buffer, processedMarkers, xhtml, false);
						buffer.append("</li>");
					}
					buffer.append("</ul>");
				}
			}
		}
	}

}
