/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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

import de.huxhorn.lilith.data.eventsource.EventWrapper
import de.huxhorn.lilith.data.logging.LoggingEvent
import de.huxhorn.lilith.services.clipboard.ClipboardFormatter

/**
 * Copy username to the clipboard if 'username' is available in the MDC.
 */
class CopyUsername
	implements ClipboardFormatter
{

	String getName()
	{
		return 'Copy username'
	}

	String getDescription()
	{
		return 'Copy username from MDC, if available.'
	}

	String getAccelerator()
	{
		return 'command shift U'
	}

	boolean isCompatible(Object object)
	{
		if(object instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper)object
			def event = wrapper.getEvent();
			if(event instanceof LoggingEvent)
			{
				LoggingEvent loggingEvent = (LoggingEvent)event
				return loggingEvent.getMdc()!=null && loggingEvent.getMdc().containsKey('username')
			}
		}
		return false;
	}

	String toString(Object object)
	{
		if(object instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper)object
			def event = wrapper.getEvent();
			if(event instanceof LoggingEvent)
			{
				LoggingEvent loggingEvent = (LoggingEvent)event
				return loggingEvent.getMdc()?.get('username')
			}
		}
		return null;
	}
}
