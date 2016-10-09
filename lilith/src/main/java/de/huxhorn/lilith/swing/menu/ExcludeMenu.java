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

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.lilith.swing.actions.AbstractAccessFilterAction;
import de.huxhorn.lilith.swing.actions.AbstractLoggingFilterAction;
import de.huxhorn.lilith.swing.actions.EventWrapperRelated;
import de.huxhorn.lilith.swing.actions.FilterAction;
import de.huxhorn.lilith.swing.actions.FocusCallLocationAction;
import de.huxhorn.lilith.swing.actions.FocusFormattedMessageAction;
import de.huxhorn.lilith.swing.actions.FocusHttpMethodAction;
import de.huxhorn.lilith.swing.actions.FocusHttpRemoteUserAction;
import de.huxhorn.lilith.swing.actions.FocusHttpRequestUriAction;
import de.huxhorn.lilith.swing.actions.FocusHttpRequestUrlAction;
import de.huxhorn.lilith.swing.actions.FocusHttpStatusCodeAction;
import de.huxhorn.lilith.swing.actions.FocusMessagePatternAction;
import de.huxhorn.lilith.swing.actions.FocusThreadGroupNameAction;
import de.huxhorn.lilith.swing.actions.FocusThreadNameAction;
import de.huxhorn.lilith.swing.actions.FocusThrowableAction;
import de.huxhorn.lilith.swing.actions.FocusThrowablesAction;
import de.huxhorn.lilith.swing.actions.NegateFilterAction;
import de.huxhorn.lilith.swing.actions.ViewContainerRelated;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.util.List;

public class ExcludeMenu
	extends JMenu
	implements ViewContainerRelated, EventWrapperRelated
{
	private static final long serialVersionUID = -663125573199455498L;

	private final ApplicationPreferences applicationPreferences;
	private final boolean htmlTooltip;

	private EventWrapper eventWrapper;

	private ExcludeSavedConditionsMenu savedMenu;

	private FilterAction messagePatternAction;
	private JMenuItem messagePatternItem;

	private FilterAction formattedMessageAction;
	private JMenuItem formattedMessageItem;

	private FilterAction callLocationAction;
	private JMenuItem callLocationItem;

	private FilterAction throwablesAction;
	private JMenuItem throwablesItem;

	private FilterAction throwableAction;
	private JMenuItem throwableItem;

	private FilterAction threadNameAction;
	private JMenuItem threadNameItem;

	private FilterAction threadGroupNameAction;
	private JMenuItem threadGroupNameItem;

	private ExcludeMDCMenu mdcMenu;
	private ExcludeMarkerMenu markerMenu;
	private ExcludeNDCMenu ndcMenu;
	private ExcludeLoggerMenu loggerMenu;
	// no levelMenu since logging levels stack so excluding events with a higher level than e.g. WARN does
	// not make sense.

	private FilterAction statusCodeAction;
	private JMenuItem statusCodeItem;

	private ExcludeHttpStatusTypeMenu statusTypeMenu;

	private FilterAction methodAction;
	private JMenuItem methodItem;

	private FilterAction requestUriAction;
	private JMenuItem requestUriItem;

	private FilterAction requestUrlAction;
	private JMenuItem requestUrlItem;

	private FilterAction remoteUserAction;
	private JMenuItem remoteUserItem;

	private ViewContainer viewContainer;

	public ExcludeMenu(ApplicationPreferences applicationPreferences, boolean htmlTooltip)
	{
		super("Exclude");
		this.applicationPreferences = applicationPreferences;
		this.htmlTooltip = htmlTooltip;
		createUI();
		setViewContainer(null);
		setEventWrapper(null);
	}

	private void createUI()
	{
		savedMenu = new ExcludeSavedConditionsMenu(applicationPreferences, htmlTooltip);

		messagePatternAction = new NegateFilterAction(new FocusMessagePatternAction(htmlTooltip));
		messagePatternItem = new JMenuItem(messagePatternAction);

		formattedMessageAction = new NegateFilterAction(new FocusFormattedMessageAction(htmlTooltip));
		formattedMessageItem = new JMenuItem(formattedMessageAction);

		callLocationAction = new NegateFilterAction(new FocusCallLocationAction());
		callLocationItem = new JMenuItem(callLocationAction);

		throwablesAction = new NegateFilterAction(new FocusThrowablesAction());
		throwablesItem = new JMenuItem(throwablesAction);

		throwableAction = new NegateFilterAction(new FocusThrowableAction());
		throwableItem = new JMenuItem(throwableAction);

		threadNameAction = new NegateFilterAction(new FocusThreadNameAction());
		threadNameItem = new JMenuItem(threadNameAction);

		threadGroupNameAction = new NegateFilterAction(new FocusThreadGroupNameAction());
		threadGroupNameItem = new JMenuItem(threadGroupNameAction);

		mdcMenu = new ExcludeMDCMenu();
		markerMenu = new ExcludeMarkerMenu();
		ndcMenu = new ExcludeNDCMenu(htmlTooltip);
		loggerMenu = new ExcludeLoggerMenu();

		statusCodeAction = new NegateFilterAction(new FocusHttpStatusCodeAction());
		statusCodeItem = new JMenuItem(statusCodeAction);

		statusTypeMenu = new ExcludeHttpStatusTypeMenu();

		methodAction = new NegateFilterAction(new FocusHttpMethodAction());
		methodItem = new JMenuItem(methodAction);

		requestUriAction = new NegateFilterAction(new FocusHttpRequestUriAction());
		requestUriItem = new JMenuItem(requestUriAction);

		requestUrlAction = new NegateFilterAction(new FocusHttpRequestUrlAction());
		requestUrlItem = new JMenuItem(requestUrlAction);

		remoteUserAction = new NegateFilterAction(new FocusHttpRemoteUserAction());
		remoteUserItem = new JMenuItem(remoteUserAction);
	}

	public void setEventWrapper(EventWrapper eventWrapper)
	{
		this.eventWrapper = eventWrapper;
		messagePatternAction.setEventWrapper(eventWrapper);
		formattedMessageAction.setEventWrapper(eventWrapper);
		callLocationAction.setEventWrapper(eventWrapper);

		throwablesAction.setEventWrapper(eventWrapper);
		throwableAction.setEventWrapper(eventWrapper);

		threadNameAction.setEventWrapper(eventWrapper);
		threadGroupNameAction.setEventWrapper(eventWrapper);

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
		this.viewContainer = viewContainer;
		savedMenu.setViewContainer(viewContainer);

		messagePatternAction.setViewContainer(viewContainer);
		formattedMessageAction.setViewContainer(viewContainer);
		callLocationAction.setViewContainer(viewContainer);

		throwablesAction.setViewContainer(viewContainer);
		throwableAction.setViewContainer(viewContainer);

		threadNameAction.setViewContainer(viewContainer);
		threadGroupNameAction.setViewContainer(viewContainer);

		mdcMenu.setViewContainer(viewContainer);
		markerMenu.setViewContainer(viewContainer);
		ndcMenu.setViewContainer(viewContainer);
		loggerMenu.setViewContainer(viewContainer);

		statusCodeAction.setViewContainer(viewContainer);
		statusTypeMenu.setViewContainer(viewContainer);
		methodAction.setViewContainer(viewContainer);
		requestUriAction.setViewContainer(viewContainer);
		requestUrlAction.setViewContainer(viewContainer);
		remoteUserAction.setViewContainer(viewContainer);
		updateState();
	}

	public ViewContainer getViewContainer()
	{
		return viewContainer;
	}

	private void updateState()
	{
		EventWrapper wrapper = this.eventWrapper;
		removeAll();

		LoggingEvent loggingEvent = AbstractLoggingFilterAction.resolveLoggingEvent(wrapper);
		if(loggingEvent != null)
		{
			add(savedMenu);
			addSeparator();
			add(messagePatternItem);
			add(formattedMessageItem);
			addSeparator();
			add(callLocationItem);
			addSeparator();
			add(throwablesItem);
			add(throwableItem);
			addSeparator();
			add(threadNameItem);
			add(threadGroupNameItem);
			addSeparator();
			add(mdcMenu);
			add(markerMenu);
			add(ndcMenu);
			addSeparator();
			add(loggerMenu);

			setEnabled(
					messagePatternItem.isEnabled() ||
					formattedMessageItem.isEnabled() ||
					callLocationItem.isEnabled() ||
					throwablesItem.isEnabled() ||
					throwableItem.isEnabled() ||
					threadNameItem.isEnabled() ||
					threadGroupNameItem.isEnabled() ||
					mdcMenu.isEnabled() ||
					markerMenu.isEnabled() ||
					loggerMenu.isEnabled() ||
					savedMenu.isEnabled()
				);
			return;
		}

		AccessEvent accessEvent = AbstractAccessFilterAction.resolveAccessEvent(eventWrapper);
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
