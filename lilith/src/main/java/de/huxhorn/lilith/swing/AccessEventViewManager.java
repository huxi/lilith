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
package de.huxhorn.lilith.swing;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.engine.EventSource;

public class AccessEventViewManager
	extends ViewManager<AccessEvent>
{
	public AccessEventViewManager(MainFrame mainFrame)
	{
		super(mainFrame);
	}

	@Override
	protected ViewContainer<AccessEvent> createViewContainer(EventSource<AccessEvent> eventSource)
	{
		//return new TabbedAccessViewContainer(getMainFrame(), eventSource);
		return new ComboAccessViewContainer(getMainFrame(), eventSource);
	}
}
