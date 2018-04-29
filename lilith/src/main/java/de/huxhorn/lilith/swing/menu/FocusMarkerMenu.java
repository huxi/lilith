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

import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.swing.actions.BasicFilterAction;
import de.huxhorn.lilith.swing.actions.FocusMarkerAction;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

class FocusMarkerMenu
	extends AbstractLoggingFilterMenu
{
	private static final long serialVersionUID = 3621448237085341280L;

	FocusMarkerMenu()
	{
		super("Marker");

		setViewContainer(null);
	}

	@Override
	protected void updateState()
	{
		removeAll();
		Marker marker=null;
		if (loggingEvent != null)
		{
			marker = loggingEvent.getMarker();
		}
		if(marker == null)
		{
			setEnabled(false);
			return;
		}
		Set<String> collected = marker.collectMarkerNames();
		// "collected" is never null or empty
		SortedSet<String> sorted = new TreeSet<>(collected);
		for (String current : sorted)
		{
			BasicFilterAction filterAction = createAction(current);
			filterAction.setViewContainer(viewContainer);
			add(filterAction);
		}
		setEnabled(true);
	}

	protected BasicFilterAction createAction(String markerName)
	{
		return new FocusMarkerAction(markerName);
	}
}
