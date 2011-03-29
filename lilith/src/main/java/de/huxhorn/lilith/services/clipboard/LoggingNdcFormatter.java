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
import de.huxhorn.lilith.data.logging.Message;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class LoggingNdcFormatter
	implements ClipboardFormatter
{
	private static final long serialVersionUID = 5898595765166630166L;

	public String getName()
	{
		return "Copy NDC";
	}

	public String getDescription()
	{
		return "Copies the Nested Diagnostic Context of the logging event to the clipboard.";
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
					Message[] ndc = loggingEvent.getNdc();
					return ndc != null && ndc.length>0;
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
					Message[] ndc = loggingEvent.getNdc();
					if(ndc != null && ndc.length>0)
					{
						StringBuilder text = new StringBuilder();
						for(Message current : ndc)
						{
							if(text.length() != 0)
							{
								text.append("\n");
							}
							text.append(current.getMessage());
						}
						return text.toString();
					}
				}
			}
		}

		return null;
	}
}
