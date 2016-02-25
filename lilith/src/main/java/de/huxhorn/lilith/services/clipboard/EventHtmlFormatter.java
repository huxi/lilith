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

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.swing.MainFrame;

public class EventHtmlFormatter
		implements ClipboardFormatter
{
	private static final long serialVersionUID = 2263706767713579277L;

	private MainFrame mainFrame;

	public EventHtmlFormatter(MainFrame mainFrame)
	{
		this.mainFrame = mainFrame;
	}

	public String getName()
	{
		return "Copy event";
	}

	public String getDescription()
	{
		return "Copies the HTML code of this events details view to the clipboard.";
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
			return eventObj instanceof LoggingEvent || eventObj instanceof AccessEvent;
		}
		return false;
	}

	public String toString(Object object)
	{
		if(object instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) object;
			return mainFrame.createMessage(wrapper);
		}
		return null;
	}

	public boolean isNative()
	{
		return true;
	}
}
