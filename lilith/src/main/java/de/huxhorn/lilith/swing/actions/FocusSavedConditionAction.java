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

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.swing.TextPreprocessor;
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.lilith.swing.preferences.SavedCondition;
import de.huxhorn.sulky.conditions.Condition;

import javax.swing.*;

public class FocusSavedConditionAction
		extends AbstractFilterAction
{
	private static final long serialVersionUID = -1245643497938628684L;

	private final SavedCondition savedCondition;
	//private EventWrapper eventWrapper;

	public FocusSavedConditionAction(ViewContainer viewContainer, SavedCondition savedCondition)
	{
		super(savedCondition.getName());
		this.savedCondition = savedCondition;
		Condition condition = savedCondition.getCondition();
		if(condition == null)
		{
			throw new IllegalArgumentException("Condition of "+savedCondition+" is null!");
		}
		putValue(Action.SHORT_DESCRIPTION, TextPreprocessor.preformattedTooltip(TextPreprocessor.cropTextBlock(TextPreprocessor.formatCondition(condition))));
		setViewContainer(viewContainer);
	}

	@Override
	protected void updateState()
	{
		if(viewContainer == null) // || eventWrapper == null)
		{
			setEnabled(false);
			return;
		}
		//setEnabled(eventWrapper.getEvent() != null);
		setEnabled(true);
	}

	@Override
	public void setEventWrapper(EventWrapper eventWrapper)
	{
		// ignore
		// this.eventWrapper = eventWrapper;
	}

	@Override
	public Condition resolveCondition()
	{
		return savedCondition.getCondition();
	}
}
