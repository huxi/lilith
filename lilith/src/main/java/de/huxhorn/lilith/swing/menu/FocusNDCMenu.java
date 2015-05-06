/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2015 Joern Huxhorn
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
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.lilith.swing.actions.AbstractLoggingFilterAction;
import de.huxhorn.lilith.swing.actions.EventWrapperRelated;
import de.huxhorn.lilith.swing.actions.FilterAction;
import de.huxhorn.lilith.swing.actions.FocusNDCAction;
import de.huxhorn.lilith.swing.actions.FocusNDCPatternAction;
import de.huxhorn.lilith.swing.actions.ViewContainerRelated;

import javax.swing.JMenu;

public class FocusNDCMenu
	extends JMenu
	implements ViewContainerRelated, EventWrapperRelated
{
	private static final long serialVersionUID = 2934068317229029302L;
	protected final boolean htmlTooltip;

	private ViewContainer viewContainer;
	private Message[] ndc;

	public FocusNDCMenu(boolean htmlTooltip)
	{
		super("NDC");
		this.htmlTooltip = htmlTooltip;
		setToolTipText("Nested Diagnostic Context");
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

	public void setEventWrapper(EventWrapper eventWrapper)
	{
		Message[] ndc = null;
		LoggingEvent loggingEvent = AbstractLoggingFilterAction.resolveLoggingEvent(eventWrapper);
		if (loggingEvent != null)
		{
			ndc = loggingEvent.getNdc();
		}
		setNdc(ndc);
	}

	public void setNdc(Message[] ndc)
	{
		this.ndc = ndc;
		updateState();
	}

	private void updateState()
	{
		removeAll();
		if(viewContainer == null || ndc == null || ndc.length == 0)
		{
			setEnabled(false);
			return;
		}
		boolean first = true;
		for (Message current : ndc)
		{
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
			add(createMessageAction(viewContainer, message));
			if(!message.equals(messagePattern))
			{
				add(createMessagePatternAction(viewContainer, messagePattern));
			}
		}
		setEnabled(true);
	}

	protected FilterAction createMessageAction(ViewContainer viewContainer, String message)
	{
		return new FocusNDCAction(viewContainer, message, htmlTooltip);
	}

	protected FilterAction createMessagePatternAction(ViewContainer viewContainer, String pattern)
	{
		return new FocusNDCPatternAction(viewContainer, pattern, htmlTooltip);
	}
}
