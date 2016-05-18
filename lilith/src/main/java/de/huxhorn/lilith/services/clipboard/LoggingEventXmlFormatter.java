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
import de.huxhorn.lilith.data.logging.xml.LoggingXmlEncoder;
import java.nio.charset.StandardCharsets;

public class LoggingEventXmlFormatter
		implements ClipboardFormatter
{
	private static final long serialVersionUID = 2263706767713579277L;

	private LoggingXmlEncoder encoder = new LoggingXmlEncoder(false, true);

	public String getName()
	{
		return "Copy event as XML";
	}

	public String getDescription()
	{
		return "Copies the XML representation of the event to the clipboard.";
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
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof LoggingEvent)
			{
				// this is only an approximation.
				// there are likely other cases causing NPE since XML requires schema conformance.
				LoggingEvent event = (LoggingEvent) eventObj;
				String loggerName = event.getLogger();
				return loggerName != null && !"".equals(loggerName);
			}
		}
		return false;
	}

	public String toString(Object object)
	{
		if(object instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) object;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof LoggingEvent)
			{
				LoggingEvent event = (LoggingEvent) eventObj;
				String loggerName = event.getLogger();
				if(loggerName == null || "".equals(loggerName))
				{
					return null;
				}
				byte[] bytes = encoder.encode(event);

				if(bytes == null)
				{
					return null;
				}

				return new String(bytes, StandardCharsets.UTF_8);
			}
		}
		return null;
	}

	public boolean isNative()
	{
		return true;
	}
}
