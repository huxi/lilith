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

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class LoggingMdcFormatter
	implements ClipboardFormatter
{
	private static final long serialVersionUID = -8079057597454959907L;

	public String getName()
	{
		return "Copy MDC";
	}

	public String getDescription()
	{
		return "Copies the Mapped Diagnostic Context of the logging event to the clipboard.";
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
					Map<String, String> mdc = loggingEvent.getMdc();
					return mdc != null && mdc.size()>0;
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
					Map<String, String> mdc = loggingEvent.getMdc();
					if(mdc != null && mdc.size()>0)
					{
						StringBuilder text = new StringBuilder();
						SortedMap<String, String> sorted = new TreeMap<String, String>(mdc);
						for(Map.Entry<String, String> current : sorted.entrySet())
						{
							text.append(current.getKey()).append("\t").append(current.getValue()).append("\n");
						}
						return text.toString();
					}
				}
			}
		}

		return null;
	}
}
