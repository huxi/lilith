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

import de.huxhorn.lilith.swing.AbstractLilithAction;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.LilithActionId;
import de.huxhorn.lilith.swing.actions.FilterAction;
import de.huxhorn.lilith.swing.actions.FocusCallLocationAction;
import de.huxhorn.lilith.swing.actions.FocusFormattedMessageAction;
import de.huxhorn.lilith.swing.actions.FocusHttpMethodAction;
import de.huxhorn.lilith.swing.actions.FocusHttpRemoteUserAction;
import de.huxhorn.lilith.swing.actions.FocusHttpRequestUrlAction;
import de.huxhorn.lilith.swing.actions.FocusHttpStatusCodeAction;
import de.huxhorn.lilith.swing.actions.FocusMessagePatternAction;
import de.huxhorn.lilith.swing.actions.FocusThreadGroupNameAction;
import de.huxhorn.lilith.swing.actions.FocusThreadNameAction;
import de.huxhorn.lilith.swing.actions.FocusThrowableAction;
import de.huxhorn.lilith.swing.actions.FocusThrowablesAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JMenuItem;

public class FocusMenu
	extends AbstractFilterMainMenu
{
	private static final long serialVersionUID = 348653831948777676L;

	private static final Action ACTION_INSTANCE = new MenuAction();

	public FocusMenu(ApplicationPreferences applicationPreferences, boolean htmlTooltip)
	{
		super(ACTION_INSTANCE, applicationPreferences, htmlTooltip);
		createUI();
		viewContainerUpdated();
	}

	private void createUI()
	{
		FilterAction messagePatternAction = new FocusMessagePatternAction(htmlTooltip);
		registerFilterAction(messagePatternAction);

		FilterAction formattedMessageAction = new FocusFormattedMessageAction(htmlTooltip);
		registerFilterAction(formattedMessageAction);

		FilterAction callLocationAction = new FocusCallLocationAction();
		registerFilterAction(callLocationAction);

		FilterAction throwablesAction = new FocusThrowablesAction();
		registerFilterAction(throwablesAction);

		FilterAction throwableAction = new FocusThrowableAction();
		registerFilterAction(throwableAction);

		FilterAction threadNameAction = new FocusThreadNameAction();
		registerFilterAction(threadNameAction);

		FilterAction threadGroupNameAction = new FocusThreadGroupNameAction();
		registerFilterAction(threadGroupNameAction);

		FilterAction statusCodeAction = new FocusHttpStatusCodeAction();
		registerFilterAction(statusCodeAction);

		FilterAction methodAction = new FocusHttpMethodAction();
		registerFilterAction(methodAction);

		FilterAction requestUrlAction = new FocusHttpRequestUrlAction();
		registerFilterAction(requestUrlAction);

		FilterAction remoteUserAction = new FocusHttpRemoteUserAction();
		registerFilterAction(remoteUserAction);

		AbstractFilterMenu savedMenu = new FocusSavedConditionsMenu(applicationPreferences, htmlTooltip);
		registerAbstractFilterMenu(savedMenu);

		AbstractFilterMenu loggerMenu = new FocusLoggerMenu();
		registerAbstractFilterMenu(loggerMenu);

		AbstractFilterMenu levelMenu = new FocusLevelMenu();
		registerAbstractFilterMenu(levelMenu);

		AbstractFilterMenu mdcMenu = new FocusMDCMenu();
		registerAbstractFilterMenu(mdcMenu);

		AbstractFilterMenu markerMenu = new FocusMarkerMenu();
		registerAbstractFilterMenu(markerMenu);

		AbstractFilterMenu ndcMenu = new FocusNDCMenu(htmlTooltip);
		registerAbstractFilterMenu(ndcMenu);

		AbstractFilterMenu statusTypeMenu = new FocusHttpStatusTypeMenu();
		registerAbstractFilterMenu(statusTypeMenu);

		AbstractFilterMenu requestUriMenu = new FocusHttpRequestUriMenu();
		registerAbstractFilterMenu(requestUriMenu);

		AbstractFilterMenu requestParameterMenu = new FocusRequestParameterMenu();
		registerAbstractFilterMenu(requestParameterMenu);

		AbstractFilterMenu requestHeaderMenu = new FocusRequestHeaderMenu();
		registerAbstractFilterMenu(requestHeaderMenu);

		AbstractFilterMenu responseHeaderMenu = new FocusResponseHeaderMenu();
		registerAbstractFilterMenu(responseHeaderMenu);


		JMenuItem messagePatternItem = new JMenuItem(messagePatternAction);
		JMenuItem formattedMessageItem = new JMenuItem(formattedMessageAction);
		JMenuItem callLocationItem = new JMenuItem(callLocationAction);
		JMenuItem throwablesItem = new JMenuItem(throwablesAction);
		JMenuItem throwableItem = new JMenuItem(throwableAction);
		JMenuItem threadNameItem = new JMenuItem(threadNameAction);
		JMenuItem threadGroupNameItem = new JMenuItem(threadGroupNameAction);
		JMenuItem statusCodeItem = new JMenuItem(statusCodeAction);
		JMenuItem methodItem = new JMenuItem(methodAction);
		JMenuItem requestUrlItem = new JMenuItem(requestUrlAction);
		JMenuItem remoteUserItem = new JMenuItem(remoteUserAction);


		registerLoggingComponent(savedMenu);
		registerLoggingComponent(null);
		registerLoggingComponent(loggerMenu);
		registerLoggingComponent(null);
		registerLoggingComponent(messagePatternItem);
		registerLoggingComponent(formattedMessageItem);
		registerLoggingComponent(null);
		registerLoggingComponent(levelMenu);
		registerLoggingComponent(null);
		registerLoggingComponent(callLocationItem);
		registerLoggingComponent(null);
		registerLoggingComponent(throwablesItem);
		registerLoggingComponent(throwableItem);
		registerLoggingComponent(null);
		registerLoggingComponent(threadNameItem);
		registerLoggingComponent(threadGroupNameItem);
		registerLoggingComponent(null);
		registerLoggingComponent(mdcMenu);
		registerLoggingComponent(markerMenu);
		registerLoggingComponent(ndcMenu);


		registerAccessComponent(savedMenu);
		registerAccessComponent(null);
		registerAccessComponent(statusCodeItem);
		registerAccessComponent(statusTypeMenu);
		registerAccessComponent(null);
		registerAccessComponent(methodItem);
		registerAccessComponent(null);
		registerAccessComponent(requestUriMenu);
		registerAccessComponent(requestUrlItem);
		registerAccessComponent(null);
		registerAccessComponent(requestParameterMenu);
		registerAccessComponent(requestHeaderMenu);
		registerAccessComponent(responseHeaderMenu);
		registerAccessComponent(null);
		registerAccessComponent(remoteUserItem);
	}

	private static class MenuAction
			extends AbstractLilithAction
	{
		private static final long serialVersionUID = -6989836389361332927L;

		MenuAction()
		{
			super(LilithActionId.FOCUS);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// no-op
		}
	}
}
