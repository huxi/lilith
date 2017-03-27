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

package de.huxhorn.lilith.swing.menu;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.lilith.swing.actions.EventWrapperRelated;
import de.huxhorn.lilith.swing.actions.ViewContainerRelated;
import javax.swing.Action;
import javax.swing.JMenu;

abstract class AbstractFilterMenu
	extends JMenu implements ViewContainerRelated, EventWrapperRelated
{
	private static final long serialVersionUID = -2749976234225574839L;
	protected transient ViewContainer viewContainer;

	AbstractFilterMenu(Action action)
	{
		super(action);
	}

	AbstractFilterMenu(String s)
	{
		super(s);
	}

	@Override
	public final void setViewContainer(ViewContainer viewContainer) {
		if(this.viewContainer != viewContainer)
		{
			this.viewContainer = viewContainer;
			viewContainerUpdated();
		}
	}

	protected void viewContainerUpdated()
	{
		EventWrapper eventWrapper = null;
		if(viewContainer != null)
		{
			eventWrapper = viewContainer.getSelectedEvent();
		}
		setEventWrapper(eventWrapper);
	}

	@Override
	public final ViewContainer getViewContainer()
	{
		return viewContainer;
	}

	@Override
	public abstract void setEventWrapper(EventWrapper eventWrapper);
}
