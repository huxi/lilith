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

import de.huxhorn.lilith.conditions.ThreadNameCondition;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.sulky.conditions.Condition;
import java.awt.event.ActionEvent;
import javax.swing.Action;

public class FocusThreadNameAction
		extends AbstractLoggingFilterAction
{
	private static final long serialVersionUID = 9061907557795859959L;

	private String name;

	public FocusThreadNameAction()
	{
		super("Thread name", false);
	}

	private void setName(String name)
	{
		this.name = name;
		putValue(Action.SHORT_DESCRIPTION, name);
		setEnabled(name != null);
	}

	@Override
	protected void updateState()
	{
		String resolvedName = null;
		if(loggingEvent != null)
		{
			ThreadInfo threadInfo = loggingEvent.getThreadInfo();
			if(threadInfo != null)
			{
				resolvedName = threadInfo.getName();
			}
		}
		setName(resolvedName);
	}

	@Override
	public Condition resolveCondition(ActionEvent e)
	{
		if(!isEnabled())
		{
			return null;
		}
		return new ThreadNameCondition(name);
	}
}
