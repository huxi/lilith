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
package de.huxhorn.lilith.swing.menu;

import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.lilith.swing.actions.FocusSavedConditionAction;
import de.huxhorn.lilith.swing.actions.ViewContainerRelated;
import de.huxhorn.lilith.swing.preferences.SavedCondition;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FocusSavedConditionsMenu
	extends JMenu
	implements ViewContainerRelated
{
	private static final long serialVersionUID = 5642118791633046024L;
	private final ApplicationPreferences applicationPreferences;

	private List<FocusSavedConditionAction> savedConditionActions;
	private ViewContainer viewContainer;

	public FocusSavedConditionsMenu(ApplicationPreferences applicationPreferences)
	{
		super("Saved conditions");
		this.applicationPreferences = applicationPreferences;
		setViewContainer(null);
		setConditionNames(applicationPreferences.getConditionNames());
	}

	public void setViewContainer(ViewContainer viewContainer)
	{
		this.viewContainer = viewContainer;
		updateState();
	}

	public void setConditionNames(List<String> conditionNames)
	{
		// *only* remove if conditions changed.
		removeAll();
		if(conditionNames == null)
		{
			savedConditionActions = null;
		}
		else
		{
			conditionNames = new ArrayList<String>(conditionNames);
			Collections.sort(conditionNames, String.CASE_INSENSITIVE_ORDER);
			savedConditionActions = new ArrayList<FocusSavedConditionAction>(conditionNames.size());
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
				savedConditionActions.add(createAction(viewContainer, savedCondition));
			}
		}
		updateState();
	}

	private void updateState()
	{
		if(viewContainer == null || savedConditionActions == null || savedConditionActions.isEmpty())
		{
			setEnabled(false);
			return;
		}

		// update viewContainer of all actions
		for(FocusSavedConditionAction current : savedConditionActions)
		{
			current.setViewContainer(viewContainer);
		}

		if(getMenuComponentCount() == 0)
		{
			// this indicates that the conditions have changed.
			for(FocusSavedConditionAction current : savedConditionActions)
			{
				add(current);
			}
		}

		setEnabled(true);
	}

	protected FocusSavedConditionAction createAction(ViewContainer viewContainer, SavedCondition savedCondition)
	{
		return new FocusSavedConditionAction(viewContainer, savedCondition);
	}
}
