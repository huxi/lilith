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
package de.huxhorn.lilith.swing.menu;

import de.huxhorn.lilith.swing.actions.BasicFilterAction;
import java.util.List;

abstract class AbstractStringStringArrayMapMenu
	extends AbstractMapMenu<String[]>
{
	private static final long serialVersionUID = 5476262722332299274L;

	AbstractStringStringArrayMapMenu(String name)
	{
		super(name);
	}

	@Override
	protected void addActions(String key, String[] value, List<BasicFilterAction> actions)
	{
		if(value == null)
		{
			return;
		}

		for (String current : value)
		{
			actions.add(createAction(key, current));
		}
	}
}
