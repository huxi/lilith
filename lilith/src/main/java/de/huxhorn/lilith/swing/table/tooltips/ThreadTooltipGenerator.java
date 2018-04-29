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

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.swing.table.TooltipGenerator;
import javax.swing.JTable;

public class ThreadTooltipGenerator
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
				ThreadInfo threadInfo = event.getThreadInfo();
				if(threadInfo != null)
				{
					StringBuilder builder=new StringBuilder();
					{
						String name = threadInfo.getName();
						Long id = threadInfo.getId();
						if(name != null || id != null)
						{
							if(name == null)
							{
								builder.append(id);
							}
							else if(id == null)
							{
								builder.append(name);
							}
							else
							{
								builder.append(name).append(" (id=").append(id).append(')');
							}
						}
					}
					{
						Integer priority = threadInfo.getPriority();
						if(priority != null && priority > 0)
						{
							if(builder.length() > 0)
							{
								builder.append(", priority=").append(priority);
							}
							String description = null;
							if(priority == Thread.NORM_PRIORITY)
							{
								description = "default";
							}
							else if(priority == Thread.MIN_PRIORITY)
							{
								description = "minimum";
							}
							else if(priority >= Thread.MAX_PRIORITY)
							{
								description = "maximum";
							}

							if(description != null)
							{
								builder.append(" (").append(description).append(')');
							}

						}
					}
					{
						String name = threadInfo.getGroupName();
						Long id = threadInfo.getGroupId();
						if(name != null || id != null)
						{
							if(builder.length() > 0)
							{
								builder.append(", ");
							}
							if(name == null)
							{
								builder.append("groupId=").append(id);
							}
							else if(id == null)
							{
								builder.append("group=").append(name);
							}
							else
							{
								builder.append("group=").append(name).append(" (id=").append(id).append(')');
							}

						}
					}
					tooltip=builder.toString();
				}

			}
		}
		return tooltip;
	}

}
