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
package de.huxhorn.lilith.swing.actions;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.conditions.Condition;
import java.awt.event.ActionEvent;

public abstract class AbstractFilterAction
	extends AbstractBasicFilterAction
	implements FilterAction
{
	private static final long serialVersionUID = -8402480035772204416L;

	protected AbstractFilterAction(String name, boolean htmlTooltip)
	{
		super(name, htmlTooltip);
	}

	@Override
	protected void viewContainerUpdated()
	{
		super.viewContainerUpdated();
		EventWrapper eventWrapper = null;
		if(viewContainer != null)
		{
			eventWrapper = viewContainer.getSelectedEvent();
		}
		setEventWrapper(eventWrapper);
	}

	/**
	 * To be called after setEventWrapper.
	 */
	protected abstract void updateState();

	@Override
	public abstract void setEventWrapper(EventWrapper eventWrapper);

	@Override
	public abstract Condition resolveCondition(ActionEvent e);
}
