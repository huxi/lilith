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
package de.huxhorn.lilith.swing.actions;

import de.huxhorn.lilith.conditions.LoggerStartsWithCondition;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.sulky.conditions.Condition;

public class FocusLoggerAction
		extends AbstractFilterAction
{
	private static final long serialVersionUID = -7615646386307125745L;

	private final String loggerName;

	public FocusLoggerAction(ViewContainer viewContainer, String loggerName)
	{
		super(loggerName, false);
		this.loggerName = loggerName;
		setViewContainer(viewContainer);
	}

	@Override
	protected void updateState()
	{
		if(viewContainer == null)
		{
			setEnabled(false);
			return;
		}
		setEnabled(true);
	}

	@Override
	public void setEventWrapper(EventWrapper eventWrapper)
	{
		// ignore
	}

	@Override
	public Condition resolveCondition()
	{
		if(loggerName == null)
		{
			return null;
		}
		return new LoggerStartsWithCondition(loggerName);
	}
}
