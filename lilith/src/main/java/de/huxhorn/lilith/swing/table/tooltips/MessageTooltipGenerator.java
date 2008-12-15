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

import java.util.StringTokenizer;

public class MessageTooltipGenerator
	implements TooltipGenerator
{
	public String createTooltipText(JTable table, int row)
	{
		final int MAX_LINE_LENGTH=80;
		final int MAX_LINES=20;
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
					// crop to a sane size, e.g. 80x25 characters
					StringTokenizer tok=new StringTokenizer(text, "\n", false);
					StringBuilder string=new StringBuilder();
					int lineCounter=0;
					while(tok.hasMoreTokens())
					{
						String line=tok.nextToken();
						line=line.trim();
						if(line.length()>0)
						{
							line=line.replace('\t',' ');
							if(lineCounter<MAX_LINES)
							{
								if(lineCounter>0)
								{
									string.append("\n>");
								}
								if(line.length()>MAX_LINE_LENGTH)
								{
									string.append(line.substring(0,MAX_LINE_LENGTH-4));
									string.append("[..]");
								}
								else
								{
									string.append(line);
								}
							}
							lineCounter++;
						}
					}
					if(lineCounter>=MAX_LINES)
					{
						int remaining=lineCounter-MAX_LINES+1;
						string.append("\n[.. ").append(remaining).append(" more lines ..]");
					}
					tooltip=SimpleXml.escape(string.toString());
					tooltip=tooltip.replace("\n","<br>");
					tooltip="<html><tt><pre>"+tooltip+"</pre></tt></html>";
				}
			}
		}
		return tooltip;
	}

}
