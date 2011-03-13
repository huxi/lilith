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
package de.huxhorn.lilith.swing.table.tooltips;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.swing.table.TooltipGenerator;
import de.huxhorn.sulky.formatting.SimpleXml;

import javax.swing.*;

public class MessageTooltipGenerator
	implements TooltipGenerator
{
	private static final int MAX_LINE_LENGTH = 80;
	private static final int MAX_LINES = 20;
	private static final String TAB_REPLACEMENT = "    ";
	private static final String LINE_TRUNCATION = "[..]";

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
				Message messageObj = event.getMessage();
				String text = null;
				if(messageObj != null)
				{
					text = messageObj.getMessage();
				}
				if(text != null)
				{
					// crop to a sane size, e.g. 80x25 characters
					StringBuilder tooltipBuilder=new StringBuilder();
					StringBuilder lineBuilder=new StringBuilder();
					int lineCounter = 0;
					for(int i=0;i<text.length();i++)
					{
						char current = text.charAt(i);
						if(current == '\t')
						{
							lineBuilder.append(TAB_REPLACEMENT);
						}
						else if(current == '\n')
						{
							if(lineCounter < MAX_LINES)
							{
								appendTruncated(lineBuilder, tooltipBuilder);
								tooltipBuilder.append('\n');
							}
							lineBuilder.setLength(0);
							lineCounter++;
						}
						else if(current != '\r')
						{
							lineBuilder.append(current);
						}
					}
					if(lineCounter >= MAX_LINES)
					{
						int remaining = lineCounter - MAX_LINES + 1;
						tooltipBuilder.append("[.. ").append(remaining).append(" more lines ..]");
					}
					else
					{
						appendTruncated(lineBuilder, tooltipBuilder);
					}
					tooltip = SimpleXml.escape(tooltipBuilder.toString());
					tooltip = tooltip.replace("\n", "<br>");
					tooltip = "<html><tt><pre>" + tooltip + "</pre></tt></html>";
				}
			}
		}
		return tooltip;
	}

	private void appendTruncated(StringBuilder sourceBuilder, StringBuilder targetBuilder)
	{
		if(sourceBuilder.length() > MAX_LINE_LENGTH)
		{
			targetBuilder.append(sourceBuilder.substring(0, MAX_LINE_LENGTH - 4));
			targetBuilder.append(LINE_TRUNCATION);
		}
		else
		{
			targetBuilder.append(sourceBuilder.toString());
		}
	}

}
