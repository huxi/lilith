/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.swing.actions.BasicFilterAction;
import de.huxhorn.lilith.swing.actions.FocusLevelAction;

class FocusLevelMenu
	extends AbstractLoggingFilterMenu
{
	private static final long serialVersionUID = -2715600759548679303L;
	private final BasicFilterAction[] levelActions;

	FocusLevelMenu()
	{
		super("Level");

		LoggingEvent.Level[] values = LoggingEvent.Level.values();
		levelActions = new BasicFilterAction[values.length-1];
		for(int i=0;i<values.length-1;i++)
		{
			levelActions[i]=new FocusLevelAction(values[i+1]); // NOPMD - AvoidInstantiatingObjectsInLoops
			add(levelActions[i]);
		}

		setViewContainer(null);
	}

	@Override
	protected void updateState()
	{
		if(loggingEvent == null)
		{
			setEnabled(false);
			return;
		}
		setEnabled(true);

		for(BasicFilterAction current : levelActions)
		{
			current.setViewContainer(viewContainer);
		}
	}
}
