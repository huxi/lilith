/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2013 Joern Huxhorn
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
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.sulky.conditions.Condition;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class AbstractFilterAction
	extends AbstractAction
	implements FilterAction
{
	private static final long serialVersionUID = -8702163293653882073L;

	protected transient ViewContainer viewContainer;

	protected AbstractFilterAction()
	{
	}

	protected AbstractFilterAction(String name)
	{
		super(name);
	}

	protected AbstractFilterAction(String name, Icon icon)
	{
		super(name, icon);
	}

	@Override
	public void setViewContainer(ViewContainer viewContainer) {
		if(this.viewContainer != viewContainer)
		{
			this.viewContainer = viewContainer;
			updateState();
		}
	}

	@Override
	public ViewContainer getViewContainer()
	{
		return viewContainer;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(this.viewContainer == null)
		{
			return;
		}
		viewContainer.applyCondition(resolveCondition(), e);
	}


	protected abstract void updateState();

	public abstract void setEventWrapper(EventWrapper eventWrapper);

	public abstract Condition resolveCondition();

}
