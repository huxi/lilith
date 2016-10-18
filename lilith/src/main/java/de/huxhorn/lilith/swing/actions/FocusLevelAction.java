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

import de.huxhorn.lilith.conditions.LevelCondition;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.sulky.conditions.Condition;
import java.awt.event.ActionEvent;
import javax.swing.Action;

public class FocusLevelAction
		extends AbstractBasicFilterAction
{
	private static final long serialVersionUID = -7615646386307125745L;

	private final LoggingEvent.Level level;

	public FocusLevelAction(LoggingEvent.Level level)
	{
		super(level.name(), false);
		this.level = level;
		putValue(Action.SHORT_DESCRIPTION, new LevelCondition(level.name()).toString());
		viewContainerUpdated();
	}

	@Override
	public Condition resolveCondition(ActionEvent e)
	{
		if(!isEnabled())
		{
			return null;
		}
		return new LevelCondition(level.name());
	}
}
