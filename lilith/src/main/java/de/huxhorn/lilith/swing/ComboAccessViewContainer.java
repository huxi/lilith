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

package de.huxhorn.lilith.swing;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.engine.EventSource;

public class ComboAccessViewContainer
	extends ComboPaneViewContainer<AccessEvent>
{
	private static final long serialVersionUID = 7450348702013390368L;

	ComboAccessViewContainer(MainFrame mainFrame, EventSource<AccessEvent> eventSource)
	{
		super(mainFrame, eventSource);
	}

	@Override
	protected EventWrapperViewPanel<AccessEvent> createViewPanel(EventSource<AccessEvent> eventSource)
	{
		return new AccessEventViewPanel(getMainFrame(), eventSource);
	}

	@Override
	public Class getWrappedClass()
	{
		return AccessEvent.class;
	}
}
