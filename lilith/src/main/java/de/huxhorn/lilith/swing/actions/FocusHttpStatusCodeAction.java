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

import de.huxhorn.lilith.conditions.HttpStatusCodeCondition;
import de.huxhorn.lilith.data.access.HttpStatus;
import de.huxhorn.sulky.conditions.Condition;

import javax.swing.Action;

public class FocusHttpStatusCodeAction
		extends AbstractAccessFilterAction
{
	private static final long serialVersionUID = -4237035769242851225L;
	private Integer statusCode;

	public FocusHttpStatusCodeAction()
	{
		super("Status code", false);
	}

	protected void setStatusCode(Integer statusCode)
	{
		if(statusCode == null || statusCode < 100 || statusCode >= 600)
		{
			this.statusCode = null;
			putValue(Action.SHORT_DESCRIPTION, null);

			setEnabled(false);
			return;
		}

		this.statusCode = statusCode;
		HttpStatus status = HttpStatus.getStatus(statusCode);
		if(status != null)
		{
			putValue(Action.SHORT_DESCRIPTION, status.getCode() + " - " + status.getDescription());
		}
		else
		{
			putValue(Action.SHORT_DESCRIPTION, statusCode);
		}

		setEnabled(true);
	}

	@Override
	protected void updateState()
	{
		if(accessEvent != null)
		{
			setStatusCode(accessEvent.getStatusCode());
		}
		else
		{
			setStatusCode(null);
		}
	}

	@Override
	public Condition resolveCondition()
	{
		if(statusCode == null)
		{
			return null;
		}
		return new HttpStatusCodeCondition(""+statusCode);
	}
}
