/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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

import de.huxhorn.lilith.swing.table.TooltipGenerator;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.formatting.SimpleXml;

import javax.swing.JTable;

import de.huxhorn.lilith.data.logging.LoggingEvent;

public class MessageTooltipGenerator
	implements TooltipGenerator
{
	public String createTooltipText(JTable table, int row)
	{
		String tooltip=null;
		Object value=table.getValueAt(row,0);
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper=(EventWrapper)value;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof LoggingEvent)
			{
				LoggingEvent event=(LoggingEvent) eventObj;
				String text=event.getMessage();
				if(text!=null)
				{
					tooltip=text.trim(); // remove empty lines etc.
					// TODO: crop to a sane size, e.g. 80x25 characters
					tooltip=SimpleXml.escape(tooltip);
					int newlineIndex=text.indexOf("\n");
					if(newlineIndex>-1)
					{
						tooltip=tooltip.replace("\n","<br>");
					}
					tooltip=tooltip.replace("\t","&nbsp;&nbsp;&nbsp;&nbsp;");
					tooltip="<html><tt><pre>"+tooltip+"</pre></tt></html>";
				}
			}
		}
		return tooltip;
	}

}
