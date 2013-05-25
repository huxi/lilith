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

import de.huxhorn.lilith.conditions.NDCContainsPatternCondition;
import de.huxhorn.lilith.swing.TextPreprocessor;
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.sulky.conditions.Condition;

import javax.swing.*;

public class FocusNDCPatternAction
		extends FilterBaseAction
{
	private static final long serialVersionUID = -4802481730349145765L;

	private final String pattern;

	public FocusNDCPatternAction(ViewContainer viewContainer, String pattern)
	{
		super(TextPreprocessor.cropToSingleLine(pattern));
		this.pattern = pattern;
		putValue(Action.SHORT_DESCRIPTION, TextPreprocessor.preformattedTooltip(TextPreprocessor.cropTextBlock(pattern)));
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
	protected Condition resolveCondition()
	{
		if(pattern == null)
		{
			return null;
		}
		return new NDCContainsPatternCondition(pattern);
	}
}
