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

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.lilith.swing.actions.AbstractLoggingFilterAction;
import de.huxhorn.lilith.swing.actions.EventWrapperRelated;
import de.huxhorn.lilith.swing.actions.FilterAction;
import de.huxhorn.lilith.swing.actions.FocusLoggerAction;
import de.huxhorn.lilith.swing.actions.ViewContainerRelated;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FocusLoggerMenu
	extends JMenu
	implements ViewContainerRelated, EventWrapperRelated
{
	private static final long serialVersionUID = -1383728062587884548L;

	private ViewContainer viewContainer;
	private String loggerName;

	public FocusLoggerMenu()
	{
		super("Logger");
		setViewContainer(null);
		setEventWrapper(null);
	}

	public void setViewContainer(ViewContainer viewContainer)
	{
		this.viewContainer = viewContainer;
		updateState();
	}

	public ViewContainer getViewContainer()
	{
		return viewContainer;
	}

	public void setEventWrapper(EventWrapper eventWrapper) {
		String loggerName = null;
		LoggingEvent loggingEvent = AbstractLoggingFilterAction.resolveLoggingEvent(eventWrapper);
		if (loggingEvent != null) {
			loggerName = loggingEvent.getLogger();
		}
		setLoggerName(loggerName);
	}

	public void setLoggerName(String loggerName)
	{
		this.loggerName = loggerName;
		updateState();
	}

	private void updateState()
	{
		removeAll();
		if(viewContainer == null || loggerName == null)
		{
			setEnabled(false);
			return;
		}

		for (String current : prepareLoggerNames(loggerName))
		{
			add(createAction(current));
		}
		setEnabled(true);
	}

	protected FilterAction createAction(String loggerName)
	{
		return new FocusLoggerAction(loggerName);
	}

	public static List<String> prepareLoggerNames(String loggerName)
	{
		if(loggerName == null)
		{
			return new ArrayList<>();
		}
		List<String> tokens = new ArrayList<>();
		loggerName = loggerName.replace('$', '.'); // better handling of inner classes
		StringTokenizer tok = new StringTokenizer(loggerName, ".", false);
		while(tok.hasMoreTokens())
		{
			String current=tok.nextToken();
			tokens.add(current);
		}

		List<String> result=new ArrayList<>(tokens.size());
		for(int i=tokens.size();i>0;i--)
		{
			StringBuilder builder=new StringBuilder();
			boolean first=true;
			for(int j=0;j<i;j++)
			{
				if(first)
				{
					first = false;
				}
				else
				{
					builder.append(".");
				}
				builder.append(tokens.get(j));
			}
			result.add(builder.toString());
		}
		return result;
	}

}
