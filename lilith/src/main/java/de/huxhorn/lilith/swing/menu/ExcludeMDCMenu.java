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

import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.lilith.swing.actions.ExcludeMDCAction;

import javax.swing.*;

public class ExcludeMDCMenu
	extends FocusMDCMenu
{
	private static final long serialVersionUID = 6995608490657897758L;

	@Override
	protected Action createAction(ViewContainer viewContainer, String key, String value) {
		return new ExcludeMDCAction(viewContainer, key, value);
	}
}
