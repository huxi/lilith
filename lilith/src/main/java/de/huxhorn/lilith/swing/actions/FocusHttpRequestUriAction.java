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

import de.huxhorn.lilith.conditions.HttpRequestUriCondition;
import de.huxhorn.lilith.swing.TextPreprocessor;
import de.huxhorn.sulky.conditions.Condition;

import javax.swing.*;

public class FocusHttpRequestUriAction
		extends AbstractAccessFilterAction
{
	private static final long serialVersionUID = -5114706063267599039L;

	private String searchString;

	public FocusHttpRequestUriAction()
	{
		super("Request URI");
	}

	protected void setSearchString(String searchString)
	{
		this.searchString = searchString;
		putValue(Action.SHORT_DESCRIPTION, TextPreprocessor.cropLine(searchString));

		setEnabled(searchString != null);
	}

	@Override
	protected void updateState()
	{
		if(viewContainer == null)
		{
			setSearchString(null);
			return;
		}

		if(accessEvent != null)
		{
			setSearchString(accessEvent.getRequestURI());
		}
		else
		{
			setSearchString(null);
		}
	}

	@Override
	public Condition resolveCondition()
	{
		if(searchString == null)
		{
			return null;
		}
		return new HttpRequestUriCondition(searchString);
	}
}
