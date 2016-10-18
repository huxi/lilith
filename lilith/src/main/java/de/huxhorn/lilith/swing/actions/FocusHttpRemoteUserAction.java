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

import de.huxhorn.lilith.conditions.HttpRemoteUserCondition;
import de.huxhorn.sulky.conditions.Condition;
import java.awt.event.ActionEvent;
import javax.swing.Action;

public class FocusHttpRemoteUserAction
		extends AbstractAccessFilterAction
{
	private static final long serialVersionUID = -6112185413862260091L;

	private static final String NA = "-"; // IAccessEvent.NA

	private String searchString;

	public FocusHttpRemoteUserAction()
	{
		super("Remote User", false);
	}

	protected void setSearchString(String searchString)
	{
		this.searchString = searchString;
		putValue(Action.SHORT_DESCRIPTION, searchString);

		setEnabled(searchString != null);
	}

	@Override
	protected void updateState()
	{
		if(accessEvent != null)
		{
			String value = accessEvent.getRemoteUser();
			if(NA.equals(value))
			{
				// don't ask
				value = null;
			}
			setSearchString(value);
		}
		else
		{
			setSearchString(null);
		}
	}

	@Override
	public Condition resolveCondition(ActionEvent e)
	{
		if(!isEnabled())
		{
			return null;
		}
		return new HttpRemoteUserCondition(searchString);
	}
}
