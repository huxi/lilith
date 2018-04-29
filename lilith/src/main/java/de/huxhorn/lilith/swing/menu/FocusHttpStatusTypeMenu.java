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

import de.huxhorn.lilith.data.access.HttpStatus;
import de.huxhorn.lilith.swing.actions.BasicFilterAction;
import de.huxhorn.lilith.swing.actions.FocusHttpStatusTypeAction;

class FocusHttpStatusTypeMenu
	extends AbstractAccessFilterMenu
{
	private static final long serialVersionUID = -675455690657800050L;

	private final BasicFilterAction[] statusTypeActions;

	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	FocusHttpStatusTypeMenu()
	{
		super("Status Type");

		HttpStatus.Type[] values = HttpStatus.Type.values();
		statusTypeActions = new BasicFilterAction[values.length];
		for(int i=0;i<values.length;i++)
		{
			statusTypeActions[i]=createAction(values[i]);
			add(statusTypeActions[i]);
		}

		setViewContainer(null);
	}

	protected BasicFilterAction createAction(HttpStatus.Type type)
	{
		return new FocusHttpStatusTypeAction(type);
	}

	@Override
	protected void updateState()
	{
		if(accessEvent == null)
		{
			setEnabled(false);
			return;
		}
		setEnabled(true);

		for(BasicFilterAction current : statusTypeActions)
		{
			current.setViewContainer(viewContainer);
		}
	}
}
