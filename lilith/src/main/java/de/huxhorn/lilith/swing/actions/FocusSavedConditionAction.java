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

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.lilith.swing.preferences.SavedCondition;
import de.huxhorn.sulky.conditions.Condition;

public class FocusSavedConditionAction
		extends AbstractFilterAction
{
	private static final long serialVersionUID = -1245643497938628684L;

	private final SavedCondition savedCondition;
	private EventWrapper eventWrapper;

	public FocusSavedConditionAction(SavedCondition savedCondition, boolean htmlTooltip)
	{
		super(savedCondition.getName(), htmlTooltip);
		this.savedCondition = savedCondition;
		Condition condition = savedCondition.getCondition();
		if(condition == null)
		{
			throw new IllegalArgumentException("Condition of "+savedCondition+" is null!");
		}
		initializeConditionTooltip(condition);
	}

	@Override
	protected void updateState()
	{
		setEnabled(eventWrapper != null);
	}

	@Override
	public void setEventWrapper(EventWrapper eventWrapper)
	{
		this.eventWrapper = eventWrapper;
		updateState();
	}

	@Override
	public Condition resolveCondition()
	{
		return savedCondition.getCondition();
	}
}
