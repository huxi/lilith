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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.KeyStroke;

public class LoggingThrowableFormatter
	implements ClipboardFormatter
{
	private static final long serialVersionUID = 830054294833389446L;

	private static String ACCELERATOR = LilithKeyStrokes.getKeyStroke(LilithKeyStrokes.COPY_THROWABLE_ACTION).toString();

	public String getName()
	{
		return "Copy Throwable";
	}

	public String getDescription()
	{
		return "Copies the Throwable of the logging event to the clipboard.";
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
					return loggingEvent.getThrowable() != null;
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
					ThrowableInfo info = loggingEvent.getThrowable();
					if(info != null)
					{
						return info.toString();
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
