/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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
package de.huxhorn.lilith.swing;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.EventSource;

public class ComboLoggingViewContainer
	extends ComboPaneViewContainer<LoggingEvent>
{
	public ComboLoggingViewContainer(MainFrame mainFrame, EventSource<LoggingEvent> eventSource)
	{
		super(mainFrame, eventSource);
	}

	protected EventWrapperViewPanel<LoggingEvent> createViewPanel(EventSource<LoggingEvent> eventSource)
	{
		MainFrame mainFrame = getMainFrame();
		boolean scrollingToBottom = mainFrame.getApplicationPreferences().isScrollingToBottom();
		EventWrapperViewPanel<LoggingEvent> result = new LoggingEventViewPanel(mainFrame, eventSource);
		result.setScrollingToBottom(scrollingToBottom);
		return result;
	}

	public Class getWrappedClass()
	{
		return LoggingEvent.class;
	}
}
