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

import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.swing.actions.BasicFilterAction;
import de.huxhorn.lilith.swing.actions.FocusNDCAction;
import de.huxhorn.lilith.swing.actions.FocusNDCPatternAction;

class FocusNDCMenu
	extends AbstractLoggingFilterMenu
{
	private static final long serialVersionUID = 7925987112162466999L;
	protected final boolean htmlTooltip;

	FocusNDCMenu(boolean htmlTooltip)
	{
		super("NDC");

		this.htmlTooltip = htmlTooltip;
		setToolTipText("Nested Diagnostic Context");
		viewContainerUpdated();
	}

	@Override
	protected void updateState()
	{
		removeAll();

		Message[] ndc = null;
		if (loggingEvent != null)
		{
			ndc = loggingEvent.getNdc();
		}

		if(ndc == null || ndc.length == 0)
		{
			setEnabled(false);
			return;
		}

		boolean first = true;
		for (Message current : ndc)
		{
			if(current == null)
			{
				continue;
			}
			String message = current.getMessage();
			String messagePattern = current.getMessagePattern();
			if(message == null)
			{
				continue;
			}
			if(first)
			{
				first = false;
			}
			else
			{
				addSeparator();
			}
			BasicFilterAction filterAction = createMessageAction(message);
			filterAction.setViewContainer(viewContainer);
			add(filterAction);
			if(!message.equals(messagePattern))
			{
				BasicFilterAction patternFilterAction = createMessagePatternAction(messagePattern);
				patternFilterAction.setViewContainer(viewContainer);
				add(patternFilterAction);
			}
		}
		setEnabled(!first);
	}

	protected BasicFilterAction createMessageAction(String message)
	{
		return new FocusNDCAction(message, htmlTooltip);
	}

	protected BasicFilterAction createMessagePatternAction(String pattern)
	{
		return new FocusNDCPatternAction(pattern, htmlTooltip);
	}
}
