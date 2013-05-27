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
import de.huxhorn.lilith.swing.actions.FocusCallLocationAction;
import de.huxhorn.lilith.swing.actions.FocusFormattedMessageAction;
import de.huxhorn.lilith.swing.actions.FocusHttpMethodAction;
import de.huxhorn.lilith.swing.actions.FocusHttpRemoteUserAction;
import de.huxhorn.lilith.swing.actions.FocusHttpRequestUriAction;
import de.huxhorn.lilith.swing.actions.FocusHttpRequestUrlAction;
import de.huxhorn.lilith.swing.actions.FocusHttpStatusCodeAction;
import de.huxhorn.lilith.swing.actions.FocusMessagePatternAction;
import de.huxhorn.lilith.swing.actions.LoggingFilterBaseAction;
import de.huxhorn.lilith.swing.actions.ViewContainerRelated;

import javax.swing.*;
import java.util.List;

public class FocusMenu
	extends JMenu
	implements ViewContainerRelated, EventWrapperRelated
{
	private static final long serialVersionUID = 2301518754828320721L;

	private final ApplicationPreferences applicationPreferences;

	private EventWrapper eventWrapper;

	private FocusSavedConditionsMenu savedMenu;
	private FocusMessagePatternAction messagePatternAction;
	private JMenuItem messagePatternItem;
	private FocusFormattedMessageAction formattedMessageAction;
	private JMenuItem formattedMessageItem;
	private FocusCallLocationAction callLocationAction;
	private JMenuItem callLocationItem;
	private FocusMDCMenu mdcMenu;
	private FocusMarkerMenu markerMenu;
	private FocusNDCMenu ndcMenu;
	private FocusLoggerMenu loggerMenu;
	private FocusLevelMenu levelMenu;

	private FocusHttpStatusCodeAction statusCodeAction;
	private JMenuItem statusCodeItem;
	private FocusHttpStatusTypeMenu statusTypeMenu;
	private FocusHttpMethodAction methodAction;
	private JMenuItem methodItem;
	private FocusHttpRequestUriAction requestUriAction;
	private JMenuItem requestUriItem;
	private FocusHttpRequestUrlAction requestUrlAction;
	private JMenuItem requestUrlItem;
	private FocusHttpRemoteUserAction remoteUserAction;
	private JMenuItem remoteUserItem;

	public FocusMenu(ApplicationPreferences applicationPreferences)
	{
		super("Focus");
		this.applicationPreferences = applicationPreferences;
		createUI();
		setViewContainer(null);
		setEventWrapper(null);
	}

	private void createUI()
	{
		savedMenu = new FocusSavedConditionsMenu(applicationPreferences);

		messagePatternAction = new FocusMessagePatternAction();
		formattedMessageAction=new FocusFormattedMessageAction();
		callLocationAction=new FocusCallLocationAction();
		messagePatternItem = new JMenuItem(messagePatternAction);
		formattedMessageItem = new JMenuItem(formattedMessageAction);
		callLocationItem = new JMenuItem(callLocationAction);
		mdcMenu = new FocusMDCMenu();
		markerMenu = new FocusMarkerMenu();
		ndcMenu = new FocusNDCMenu();
		loggerMenu = new FocusLoggerMenu();
		levelMenu = new FocusLevelMenu();

		statusCodeAction = new FocusHttpStatusCodeAction();
		statusCodeItem = new JMenuItem(statusCodeAction);
		statusTypeMenu = new FocusHttpStatusTypeMenu();
		methodAction = new FocusHttpMethodAction();
		methodItem = new JMenuItem(methodAction);
		requestUriAction = new FocusHttpRequestUriAction();
		requestUriItem = new JMenuItem(requestUriAction);
		requestUrlAction = new FocusHttpRequestUrlAction();
		requestUrlItem = new JMenuItem(requestUrlAction);
		remoteUserAction = new FocusHttpRemoteUserAction();
		remoteUserItem = new JMenuItem(remoteUserAction);
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

		statusCodeAction.setEventWrapper(eventWrapper);
		methodAction.setEventWrapper(eventWrapper);
		requestUriAction.setEventWrapper(eventWrapper);
		requestUrlAction.setEventWrapper(eventWrapper);
		remoteUserAction.setEventWrapper(eventWrapper);
		updateState();
	}

	public void setViewContainer(ViewContainer viewContainer)
	{
		savedMenu.setViewContainer(viewContainer);

		messagePatternAction.setViewContainer(viewContainer);
		formattedMessageAction.setViewContainer(viewContainer);
		callLocationAction.setViewContainer(viewContainer);
		mdcMenu.setViewContainer(viewContainer);
		markerMenu.setViewContainer(viewContainer);
		ndcMenu.setViewContainer(viewContainer);
		loggerMenu.setViewContainer(viewContainer);
		levelMenu.setViewContainer(viewContainer);

		statusCodeAction.setViewContainer(viewContainer);
		statusTypeMenu.setViewContainer(viewContainer);
		methodAction.setViewContainer(viewContainer);
		requestUriAction.setViewContainer(viewContainer);
		requestUrlAction.setViewContainer(viewContainer);
		remoteUserAction.setViewContainer(viewContainer);
		updateState();
	}

	private void updateState()
	{
		EventWrapper wrapper = this.eventWrapper;
		removeAll();

		LoggingEvent loggingEvent = LoggingFilterBaseAction.resolveLoggingEvent(wrapper);
		if(loggingEvent != null)
		{
			add(savedMenu);
			addSeparator();
			add(messagePatternItem);
			add(formattedMessageItem);
			addSeparator();
			add(levelMenu);
			addSeparator();
			add(callLocationItem);
			addSeparator();
			add(mdcMenu);
			add(markerMenu);
			add(ndcMenu);
			addSeparator();
			add(loggerMenu);

			// levelMenu will always be enabled if an event exists at all
			setEnabled(true);
			return;
		}

		AccessEvent accessEvent = AccessFilterBaseAction.resolveAccessEvent(eventWrapper);
		if(accessEvent != null)
		{
			add(savedMenu);
			addSeparator();
			add(statusCodeItem);
			add(statusTypeMenu);
			addSeparator();
			add(methodItem);
			addSeparator();
			add(requestUriItem);
			add(requestUrlItem);
			addSeparator();
			add(remoteUserItem);

			// statusTypeMenu will always be enabled if an event exists at all
			setEnabled(true);
			return;
		}

		setEnabled(false);
	}


	public void setConditionNames(List<String> conditionNames)
	{
		savedMenu.setConditionNames(conditionNames);
	}
}
