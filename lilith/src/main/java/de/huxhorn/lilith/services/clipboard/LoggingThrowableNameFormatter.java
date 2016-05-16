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
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.lilith.swing.LilithKeyStrokes;

public class LoggingThrowableNameFormatter
	implements ClipboardFormatter
{
	private static final long serialVersionUID = 1139703047038656939L;

	private static String ACCELERATOR = LilithKeyStrokes.getKeyStroke(LilithKeyStrokes.COPY_THROWABLE_NAME_ACTION).toString();

	public String getName()
	{
		return "Copy Throwable name";
	}

	public String getDescription()
	{
		return "Copies the Throwable class name of the logging event to the clipboard.";
	}

	public String getAccelerator()
	{
		return ACCELERATOR;
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
					ThrowableInfo throwable = loggingEvent.getThrowable();
					if(throwable != null)
					{
						String throwableName = throwable.getName();
						if(throwableName != null && !"".equals(throwableName))
						{
							return true;
						}
					}
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
					ThrowableInfo throwable = loggingEvent.getThrowable();
					if(throwable != null)
					{
						String throwableName = throwable.getName();
						if(throwableName != null && !"".equals(throwableName))
						{
							return throwableName;
						}
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
