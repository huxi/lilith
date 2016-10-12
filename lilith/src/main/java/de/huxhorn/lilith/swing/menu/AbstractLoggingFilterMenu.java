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

import static de.huxhorn.lilith.swing.actions.AbstractLoggingFilterAction.resolveLoggingEvent;

abstract class AbstractLoggingFilterMenu
	extends AbstractFilterMenu
{
	private static final long serialVersionUID = -8149200436837131780L;
	protected LoggingEvent loggingEvent;

	AbstractLoggingFilterMenu(String s)
	{
		super(s);
	}

	@Override
	public final void setEventWrapper(EventWrapper eventWrapper)
	{
		setLoggingEvent(resolveLoggingEvent(eventWrapper));
	}

	public final void setLoggingEvent(LoggingEvent loggingEvent)
	{
		this.loggingEvent = loggingEvent;
		updateState();
	}

	protected abstract void updateState();
}
