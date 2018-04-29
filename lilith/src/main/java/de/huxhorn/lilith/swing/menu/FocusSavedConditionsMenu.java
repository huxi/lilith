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

package de.huxhorn.lilith.swing.menu;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.actions.BasicFilterAction;
import de.huxhorn.lilith.swing.actions.FocusSavedConditionAction;
import de.huxhorn.lilith.swing.preferences.SavedCondition;
import java.util.ArrayList;
import java.util.List;

class FocusSavedConditionsMenu
	extends AbstractFilterMenu
	implements ConditionNamesAware
{
	private static final long serialVersionUID = 5145343038578903089L;
	private final ApplicationPreferences applicationPreferences;
	protected final boolean htmlTooltip;

	private List<BasicFilterAction> savedConditionActions;
	private EventWrapper eventWrapper;

	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	FocusSavedConditionsMenu(ApplicationPreferences applicationPreferences, boolean htmlTooltip)
	{
		super("Saved conditions");

		this.applicationPreferences = applicationPreferences;
		this.htmlTooltip = htmlTooltip;
		setConditionNames(applicationPreferences.getConditionNames());

		setViewContainer(null);
	}

	@Override
	public final void setConditionNames(List<String> conditionNames)
	{
		// *only* remove if conditions changed.
		removeAll();
		if(conditionNames == null)
		{
			savedConditionActions = null;
		}
		else
		{
			conditionNames = new ArrayList<>(conditionNames);
			conditionNames.sort(String.CASE_INSENSITIVE_ORDER);
			savedConditionActions = new ArrayList<>(conditionNames.size());
			for(String current : conditionNames)
			{
				SavedCondition savedCondition = applicationPreferences.resolveSavedCondition(current);
				if(savedCondition == null)
				{
					continue;
				}
				if(savedCondition.getCondition() == null)
				{
					// something went wrong, ignore.
					continue;
				}
				BasicFilterAction filterAction = createAction(savedCondition);
				filterAction.setViewContainer(viewContainer);
				savedConditionActions.add(filterAction);
			}
		}
		updateState();
	}

	private void updateState()
	{
		if(eventWrapper == null || savedConditionActions == null || savedConditionActions.isEmpty())
		{
			setEnabled(false);
			return;
		}

		// update viewContainer of all actions
		for(BasicFilterAction current : savedConditionActions)
		{
			current.setViewContainer(viewContainer);
		}

		if(getMenuComponentCount() == 0)
		{
			// this indicates that the conditions have changed.
			savedConditionActions.forEach(this::add);
		}

		setEnabled(true);
	}

	protected BasicFilterAction createAction(SavedCondition savedCondition)
	{
		return new FocusSavedConditionAction(savedCondition, htmlTooltip);
	}

	@Override
	public void setEventWrapper(EventWrapper eventWrapper)
	{
		this.eventWrapper = eventWrapper;
		updateState();
	}
}
