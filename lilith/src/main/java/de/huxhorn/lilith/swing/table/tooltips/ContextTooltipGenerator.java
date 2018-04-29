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

package de.huxhorn.lilith.swing.table.tooltips;

import de.huxhorn.lilith.DateTimeFormatters;
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.swing.table.TooltipGenerator;
import java.time.Instant;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.JTable;

public class ContextTooltipGenerator
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
			LoggerContext context = null;
			if(eventObj instanceof LoggingEvent)
			{
				context = ((LoggingEvent) eventObj).getLoggerContext();
			}
			else if(eventObj instanceof AccessEvent)
			{
				context = ((AccessEvent) eventObj).getLoggerContext();
			}

			if(context != null)
			{
				StringBuilder msg = new StringBuilder();
				msg.append("<html><h4>Name</h4>").append(context.getName());
				Long timestamp = context.getBirthTime();
				if(timestamp != null)
				{
					msg.append("<h4>Birth time</h4>");
					msg.append(DateTimeFormatters.DATETIME_IN_SYSTEM_ZONE_SPACE.format(Instant.ofEpochMilli(timestamp)));
				}
				Map<String, String> props = context.getProperties();
				if(props != null && !props.isEmpty())
				{
					msg.append("<h4>Properties</h4><table><tr><th>Key</th><th>Value</th></tr>");
					SortedMap<String, String> sortedProps = new TreeMap<>(props);
					for(Map.Entry<String, String> current : sortedProps.entrySet())
					{
						msg.append("<tr><td>").append(current.getKey())
								.append("</td><td>").append(current.getValue())
								.append("</td></tr>");
					}
					msg.append("</table>");
				}
				msg.append("</html>");
				tooltip = msg.toString();
			}
		}
		return tooltip;
	}
}
