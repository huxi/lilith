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

import de.huxhorn.sulky.conditions.Condition;
import java.awt.event.ActionEvent;
import javax.swing.Action;

/**
 * A Filter action that does not care about EventWrapper.
 */
public interface BasicFilterAction
	extends ViewContainerRelated, Action
{
	/**
	 * Returns the Condition for this FilterAction, if available.
	 *
	 * Implementations of this method must be able to cope with a null ActionEvent.
	 * This should return the "default" condition. The ActionEvent can be used to
	 * support "alternative behavior" if the Alt key is pressed while the event is
	 * fired.
	 *
	 * Use this ability wisely because there is no way to inform the user about
	 * the existence of this "alternative behavior". It will be magic.
	 *
	 * @param e the action event, can be null.
	 * @return the resolved Condition or null if no Condition can be resolved.
	 */
	Condition resolveCondition(ActionEvent e);
}
