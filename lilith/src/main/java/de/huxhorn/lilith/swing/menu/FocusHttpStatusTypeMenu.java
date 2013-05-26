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
package de.huxhorn.lilith.swing.menu;

import de.huxhorn.lilith.data.access.HttpStatus;
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.lilith.swing.actions.FocusHttpStatusTypeAction;
import de.huxhorn.lilith.swing.actions.ViewContainerRelated;

import javax.swing.*;

public class FocusHttpStatusTypeMenu
	extends JMenu
	implements ViewContainerRelated
{
	private static final long serialVersionUID = -6987929141687901690L;

	private final FocusHttpStatusTypeAction[] statusTypeActions;

	private ViewContainer viewContainer;

	public FocusHttpStatusTypeMenu()
	{
		super("Status Type");
		setViewContainer(null);
		HttpStatus.Type[] values = HttpStatus.Type.values();
		statusTypeActions = new FocusHttpStatusTypeAction[values.length];
		for(int i=0;i<values.length;i++)
		{
			statusTypeActions[i]=createAction(values[i]);
			add(statusTypeActions[i]);
		}
	}

	protected FocusHttpStatusTypeAction createAction(HttpStatus.Type type)
	{
		return new FocusHttpStatusTypeAction(type);
	}

	public void setViewContainer(ViewContainer viewContainer)
	{
		this.viewContainer = viewContainer;
		updateState();
	}

	private void updateState()
	{
		if(viewContainer == null)
		{
			setEnabled(false);
			return;
		}
		setEnabled(true);

		for(FocusHttpStatusTypeAction current : statusTypeActions)
		{
			current.setViewContainer(viewContainer);
		}
	}
}
