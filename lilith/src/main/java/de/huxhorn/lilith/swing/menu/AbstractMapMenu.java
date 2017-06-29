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
import de.huxhorn.lilith.swing.actions.BasicFilterAction;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.JLabel;

abstract class AbstractMapMenu<V>
	extends AbstractFilterMenu
{
	private static final long serialVersionUID = 4739951240440898716L;

	private final JLabel hintLabel;
	protected Map<String, V> map;

	AbstractMapMenu(String name)
	{
		super(name);

		hintLabel = new JLabel("Hold "+ KeyEvent.getModifiersExText(KeyEvent.ALT_DOWN_MASK)+" to match any value.");
		viewContainerUpdated();
	}

	protected void updateState()
	{
		removeAll();
		Map<String, V> map = this.map;
		SortedMap<String, V> sorted = null;
		if (map != null && !map.isEmpty())
		{
			if(map.containsKey(null))
			{
				map = new HashMap<>(map);
				map.remove(null);
			}
			sorted = new TreeMap<>(map);
		}

		if(sorted == null || sorted.isEmpty())
		{
			setEnabled(false);
			return;
		}

		add(hintLabel);
		addSeparator();
		List<BasicFilterAction> actions = new ArrayList<>();
		for (Map.Entry<String, V> entry : sorted.entrySet())
		{
			addActions(entry.getKey(), entry.getValue(), actions);
		}
		for (BasicFilterAction current : actions)
		{
			current.setViewContainer(viewContainer);
			add(current);
		}
		setEnabled(true);
	}

	protected abstract void addActions(String key, V value, List<BasicFilterAction> actions);

	protected abstract BasicFilterAction createAction(String key, String value);

	/*
	 * Implementing classes must call setMap.
	 */
	@Override
	public abstract void setEventWrapper(EventWrapper eventWrapper);

	protected void setMap(Map<String, V> map)
	{
		this.map = map;
		updateState();
	}
}
