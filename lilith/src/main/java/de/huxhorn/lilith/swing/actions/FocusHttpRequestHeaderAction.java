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

import de.huxhorn.lilith.conditions.HttpRequestHeadersContainsCondition;
import de.huxhorn.lilith.swing.TextPreprocessor;
import de.huxhorn.sulky.conditions.Condition;
import java.awt.event.ActionEvent;

public class FocusHttpRequestHeaderAction
		extends AbstractBasicFilterAction
{
	private static final long serialVersionUID = -1245643497938628684L;

	private final String key;
	private final String value;

	public FocusHttpRequestHeaderAction(String key, String value)
	{
		super(TextPreprocessor.cropToSingleLine(key), false);
		this.key = key;
		this.value = value;
		initializeCroppedTooltip(value);
		viewContainerUpdated();
	}

	@Override
	public Condition resolveCondition(ActionEvent e)
	{
		if(!isEnabled())
		{
			return null;
		}
		if(isAlternativeBehaviorRequested(e))
		{
			return new HttpRequestHeadersContainsCondition(key, null);
		}
		return new HttpRequestHeadersContainsCondition(key, value);
	}
}
