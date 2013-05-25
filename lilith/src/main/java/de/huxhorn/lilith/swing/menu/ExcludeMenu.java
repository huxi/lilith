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

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.lilith.swing.actions.AccessFilterBaseAction;
import de.huxhorn.lilith.swing.actions.EventWrapperRelated;
import de.huxhorn.lilith.swing.actions.ExcludeCallLocationAction;
import de.huxhorn.lilith.swing.actions.ExcludeFormattedMessageAction;
import de.huxhorn.lilith.swing.actions.ExcludeMessagePatternAction;
import de.huxhorn.lilith.swing.actions.LoggingFilterBaseAction;
import de.huxhorn.lilith.swing.actions.ViewContainerRelated;

import javax.swing.*;
import java.util.List;

public class ExcludeMenu
	extends JMenu
	implements ViewContainerRelated, EventWrapperRelated
{
	private static final long serialVersionUID = -663125573199455498L;

	private final ApplicationPreferences applicationPreferences;

	private EventWrapper eventWrapper;

	private ExcludeMessagePatternAction messagePatternAction;
	private ExcludeFormattedMessageAction formattedMessageAction;
	private ExcludeCallLocationAction callLocationAction;
	private JMenuItem messagePatternItem;
	private JMenuItem formattedMessageItem;
	private JMenuItem callLocationItem;
	private ExcludeMDCMenu mdcMenu;
	private ExcludeMarkerMenu markerMenu;
	private ExcludeNDCMenu ndcMenu;
	private ExcludeLoggerMenu loggerMenu;
	private ExcludeSavedMenu savedMenu;
	// no levelMenu since logging levels stack so excluding events with a higher level than e.g. WARN does
	// not make sense.

	public ExcludeMenu(ApplicationPreferences applicationPreferences)
	{
		super("Exclude...");
		this.applicationPreferences = applicationPreferences;
		createUI();
		setViewContainer(null);
		setEventWrapper(null);
	}

	private void createUI()
	{
		messagePatternAction = new ExcludeMessagePatternAction();
		formattedMessageAction=new ExcludeFormattedMessageAction();
		callLocationAction=new ExcludeCallLocationAction();
		messagePatternItem = new JMenuItem(messagePatternAction);
		formattedMessageItem = new JMenuItem(formattedMessageAction);
		callLocationItem = new JMenuItem(callLocationAction);
		mdcMenu = new ExcludeMDCMenu();
		markerMenu = new ExcludeMarkerMenu();
		ndcMenu = new ExcludeNDCMenu();
		loggerMenu = new ExcludeLoggerMenu();
		savedMenu = new ExcludeSavedMenu(applicationPreferences);
	}

	public void setEventWrapper(EventWrapper eventWrapper)
	{
		this.eventWrapper = eventWrapper;
		messagePatternAction.setEventWrapper(eventWrapper);
		formattedMessageAction.setEventWrapper(eventWrapper);
		callLocationAction.setEventWrapper(eventWrapper);
		mdcMenu.setEventWrapper(eventWrapper);
		markerMenu.setEventWrapper(eventWrapper);
		ndcMenu.setEventWrapper(eventWrapper);
		loggerMenu.setEventWrapper(eventWrapper);
		updateState();
	}

	public void setViewContainer(ViewContainer viewContainer)
	{
		messagePatternAction.setViewContainer(viewContainer);
		formattedMessageAction.setViewContainer(viewContainer);
		callLocationAction.setViewContainer(viewContainer);
		mdcMenu.setViewContainer(viewContainer);
		markerMenu.setViewContainer(viewContainer);
		ndcMenu.setViewContainer(viewContainer);
		loggerMenu.setViewContainer(viewContainer);
		savedMenu.setViewContainer(viewContainer);
		updateState();
	}

	private void updateState()
	{
		EventWrapper wrapper = this.eventWrapper;
		removeAll();

		LoggingEvent loggingEvent = LoggingFilterBaseAction.resolveLoggingEvent(wrapper);
		if(loggingEvent != null)
		{
			add(messagePatternItem);
			add(formattedMessageItem);
			addSeparator();
			add(callLocationItem);
			addSeparator();
			add(mdcMenu);
			add(markerMenu);
			add(ndcMenu);
			addSeparator();
			add(loggerMenu);
			addSeparator();
			add(savedMenu);

			setEnabled(
					messagePatternItem.isEnabled() ||
					formattedMessageItem.isEnabled() ||
					callLocationItem.isEnabled() ||
					mdcMenu.isEnabled() ||
					markerMenu.isEnabled() ||
					loggerMenu.isEnabled() ||
					savedMenu.isEnabled()
				);
			return;
		}

		AccessEvent accessEvent = AccessFilterBaseAction.resolveAccessEvent(eventWrapper);
		if(accessEvent != null)
		{
			add(savedMenu);

			setEnabled(
					savedMenu.isEnabled()
			);
			return;
		}

		setEnabled(false);
	}

	public void setConditionNames(List<String> conditionNames)
	{
		savedMenu.setConditionNames(conditionNames);
	}
}
