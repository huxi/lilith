/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2016 Joern Huxhorn
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
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;

public class LoggingCallStackFormatter
	implements ClipboardFormatter
{
	private static final long serialVersionUID = 861522045350829907L;

	public String getName()
	{
		return "Copy call stack";
	}

	public String getDescription()
	{
		return "Copies the call stack of the logging event to the clipboard.";
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
					ExtendedStackTraceElement[] callStack = loggingEvent.getCallStack();
					return callStack != null && callStack.length > 0;
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
					ExtendedStackTraceElement[] callStack = loggingEvent.getCallStack();
					if(callStack != null && callStack.length > 0)
					{
						StringBuilder text = new StringBuilder();
						boolean first = true;
						for(ExtendedStackTraceElement current : callStack)
						{
							if(first)
							{
								first = false;
							}
							else
							{
								text.append("\n");
							}
							text.append("\tat ").append(current.toString(true));
						}
						return text.toString();
					}
				}
			}
		}

		return null;
	}

	public boolean isNative()
	{
		return true;
	}
}
