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
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.lilith.swing.actions.EventWrapperRelated;
import de.huxhorn.lilith.swing.actions.FocusMDCAction;
import de.huxhorn.lilith.swing.actions.LoggingFilterBaseAction;
import de.huxhorn.lilith.swing.actions.ViewContainerRelated;

import javax.swing.*;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class FocusMDCMenu
	extends JMenu
	implements ViewContainerRelated, EventWrapperRelated
{
	private static final long serialVersionUID = -1383728062587884548L;

	private SortedMap<String, String> mdc;
	private ViewContainer viewContainer;

	public FocusMDCMenu()
	{
		super("MDC...");
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
		SortedMap<String, String> sorted = null;
		LoggingEvent loggingEvent = LoggingFilterBaseAction.resolveLoggingEvent(eventWrapper);
		if (loggingEvent != null)
		{
			Map<String, String> mdc = loggingEvent.getMdc();
			if (mdc != null && !mdc.isEmpty())
			{
				sorted = new TreeMap<String, String>(mdc);
			}
		}
		setMdc(sorted);
	}

	public void setMdc(SortedMap<String,String> mdc)
	{
		this.mdc = mdc;
		updateState();
	}

	private void updateState()
	{
		removeAll();
		if(viewContainer == null || mdc == null || mdc.isEmpty())
		{
			setEnabled(false);
			return;
		}
		for (Map.Entry<String, String> entry : mdc.entrySet())
		{
			add(createAction(viewContainer, entry.getKey(), entry.getValue()));
		}
		setEnabled(true);
	}

	protected Action createAction(ViewContainer viewContainer, String key, String value)
	{
		return new FocusMDCAction(viewContainer, key, value);
	}
}
