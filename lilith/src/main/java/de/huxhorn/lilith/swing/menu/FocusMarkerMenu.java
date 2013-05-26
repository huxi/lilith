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

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.lilith.swing.actions.EventWrapperRelated;
import de.huxhorn.lilith.swing.actions.FocusMarkerAction;
import de.huxhorn.lilith.swing.actions.LoggingFilterBaseAction;
import de.huxhorn.lilith.swing.actions.ViewContainerRelated;

import javax.swing.*;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class FocusMarkerMenu
	extends JMenu
	implements ViewContainerRelated, EventWrapperRelated
{

	private static final long serialVersionUID = -6549986627607364431L;
	private ViewContainer viewContainer;
	private Marker marker;

	public FocusMarkerMenu()
	{
		super("Marker");
		setViewContainer(null);
		setEventWrapper(null);
	}

	public void setViewContainer(ViewContainer viewContainer)
	{
		this.viewContainer = viewContainer;
		updateState();
	}

	public void setEventWrapper(EventWrapper eventWrapper)
	{
		LoggingEvent loggingEvent = LoggingFilterBaseAction.resolveLoggingEvent(eventWrapper);
		Marker marker=null;
		if (loggingEvent != null)
		{
			marker = loggingEvent.getMarker();
		}
		setMarker(marker);
	}

	public void setMarker(Marker marker)
	{
		this.marker = marker;
		updateState();
	}

	private void updateState()
	{
		removeAll();
		if(viewContainer == null || marker == null)
		{
			setEnabled(false);
			return;
		}
		Set<String> collected = marker.collectMarkerNames();
		if(collected == null || collected.isEmpty())
		{
			setEnabled(false);
			return;
		}
		SortedSet<String> sorted = new TreeSet<String>(collected);
		for (String current : sorted)
		{
			add(createAction(viewContainer, current));
		}
		setEnabled(true);
	}

	protected Action createAction(ViewContainer viewContainer, String markerName)
	{
		return new FocusMarkerAction(viewContainer, markerName);
	}
}
