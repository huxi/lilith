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
import de.huxhorn.lilith.swing.actions.FocusMDCAction;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

class FocusMDCMenu
	extends AbstractLoggingFilterMenu
{
	private static final long serialVersionUID = -8657270846680308139L;

	FocusMDCMenu()
	{
		super("MDC");

		setToolTipText("Mapped Diagnostic Context");

		setViewContainer(null);
	}

	protected void updateState()
	{
		removeAll();
		SortedMap<String, String> sorted = null;
		if (loggingEvent != null)
		{
			Map<String, String> mdc = loggingEvent.getMdc();
			if (mdc != null && !mdc.isEmpty())
			{
				if(mdc.containsKey(null))
				{
					mdc = new HashMap<>(mdc);
					mdc.remove(null);
				}
				sorted = new TreeMap<>(mdc);
			}
		}

		if(sorted == null || sorted.isEmpty())
		{
			setEnabled(false);
			return;
		}

		for (Map.Entry<String, String> entry : sorted.entrySet())
		{
			BasicFilterAction filterAction = createAction(entry.getKey(), entry.getValue());
			filterAction.setViewContainer(viewContainer);
			add(filterAction);
		}
		setEnabled(true);
	}

	protected BasicFilterAction createAction(String key, String value)
	{
		return new FocusMDCAction(key, value);
	}
}
