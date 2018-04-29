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

package de.huxhorn.lilith.services.clipboard;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.swing.LilithActionId;
import de.huxhorn.lilith.swing.MainFrame;

public class EventHtmlFormatter
		extends AbstractNativeClipboardFormatter
{
	private static final long serialVersionUID = 4012048231193993897L;

	private final MainFrame mainFrame;

	public EventHtmlFormatter(MainFrame mainFrame)
	{
		super(LilithActionId.COPY_HTML);
		this.mainFrame = mainFrame;
	}

	@Override
	public boolean isCompatible(Object object)
	{
		if(object instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) object;
			Object eventObj = wrapper.getEvent();
			return eventObj instanceof LoggingEvent || eventObj instanceof AccessEvent;
		}
		return false;
	}

	@Override
	public String toString(Object object)
	{
		if(object instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) object;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof LoggingEvent || eventObj instanceof AccessEvent)
			{
				return mainFrame.createMessage(wrapper);
			}
		}
		return null;
	}
}
