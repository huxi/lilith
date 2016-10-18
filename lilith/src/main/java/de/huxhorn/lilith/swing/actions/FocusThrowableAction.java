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
package de.huxhorn.lilith.swing.actions;

import de.huxhorn.lilith.conditions.ThrowableCondition;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.sulky.conditions.Condition;
import java.awt.event.ActionEvent;
import javax.swing.Action;

public class FocusThrowableAction
		extends AbstractLoggingFilterAction
{
	private static final long serialVersionUID = 4108085160715159802L;

	private String throwableName;

	public FocusThrowableAction()
	{
		super("Throwable", false);
	}

	private void setThrowableName(String throwableName)
	{
		this.throwableName = throwableName;
		putValue(Action.SHORT_DESCRIPTION, throwableName);
		setEnabled(throwableName != null);
	}

	@Override
	protected void updateState()
	{
		String throwableName = null;
		if(loggingEvent != null)
		{
			ThrowableInfo throwable = loggingEvent.getThrowable();
			if(throwable != null)
			{
				throwableName = throwable.getName();
			}
		}
		setThrowableName(throwableName);
	}

	@Override
	public Condition resolveCondition(ActionEvent e)
	{
		if(!isEnabled())
		{
			return null;
		}
		return new ThrowableCondition(throwableName);
	}
}
