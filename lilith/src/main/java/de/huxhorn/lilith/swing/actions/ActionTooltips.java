/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

import de.huxhorn.lilith.swing.TextPreprocessor;
import de.huxhorn.sulky.conditions.Condition;
import javax.swing.Action;

public final class ActionTooltips
{
	static
	{
		new ActionTooltips(); // STFU, coverage
	}

	private ActionTooltips() {}

	public static void initializeConditionTooltip(Condition condition, Action action, boolean htmlTooltip)
	{
		initializeCroppedTooltip(TextPreprocessor.formatCondition(condition), action, htmlTooltip);
	}

	public static void initializeCroppedTooltip(String tooltip, Action action, boolean htmlTooltip)
	{
		tooltip = TextPreprocessor.cropTextBlock(tooltip);
		if(htmlTooltip)
		{
			tooltip = TextPreprocessor.preformattedTooltip(tooltip);
		}
		action.putValue(Action.SHORT_DESCRIPTION, tooltip);
	}
}
