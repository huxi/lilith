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
package de.huxhorn.lilith.swing.actions;

import de.huxhorn.lilith.conditions.CallLocationCondition;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.sulky.conditions.Condition;

import javax.swing.*;

public class FocusCallLocationAction
		extends LoggingFilterBaseAction
{
	private static final long serialVersionUID = 1459610752807978322L;
	private String callLocationString;

	public FocusCallLocationAction()
	{
		super("Call location");
	}

	protected void setCallLocationString(String callLocationString)
	{
		this.callLocationString = callLocationString;

		putValue(Action.SHORT_DESCRIPTION, callLocationString);

		setEnabled(callLocationString != null);
	}

	@Override
	protected void updateState()
	{
		if(viewContainer == null)
		{
			setCallLocationString(null);
			return;
		}

		String callLocationString = null;
		if(loggingEvent != null)
		{
			ExtendedStackTraceElement[] callStack = loggingEvent.getCallStack();
			if(callStack != null && callStack.length > 0)
			{
				ExtendedStackTraceElement callLocation = callStack[0];
				if(callLocation != null)
				{
					callLocationString = callLocation.toString();
				}
			}
		}
		setCallLocationString(callLocationString);
	}

	@Override
	protected Condition resolveCondition()
	{
		if(callLocationString == null)
		{
			return null;
		}
		return new CallLocationCondition(callLocationString);
	}
}
