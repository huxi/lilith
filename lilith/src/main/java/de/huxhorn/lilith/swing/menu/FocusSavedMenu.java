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
import de.huxhorn.lilith.swing.actions.FocusSavedAction;
import de.huxhorn.lilith.swing.actions.ViewContainerRelated;
import de.huxhorn.lilith.swing.preferences.SavedCondition;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class FocusSavedMenu
	extends JMenu
	implements ViewContainerRelated
{
	private static final long serialVersionUID = 5642118791633046024L;
	private final ApplicationPreferences applicationPreferences;

	private List<SavedCondition> savedConditions;
	private ViewContainer viewContainer;

	public FocusSavedMenu(ApplicationPreferences applicationPreferences)
	{
		super("Saved...");
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
			savedConditions = null;
		}
		else
		{
			savedConditions = new ArrayList<SavedCondition>(conditionNames.size());
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
				savedConditions.add(savedCondition);
			}
		}
		updateState();
	}

	private void updateState()
	{
		if(viewContainer == null || savedConditions == null || savedConditions.isEmpty())
		{
			setEnabled(false);
			return;
		}
		if(getMenuComponentCount() == 0)
		{
			// only reinitialize if empty
			for(SavedCondition current : savedConditions)
			{
				add(createAction(viewContainer, current));
			}
		}
		setEnabled(true);
	}

	protected Action createAction(ViewContainer viewContainer, SavedCondition savedCondition)
	{
		return new FocusSavedAction(viewContainer, savedCondition);
	}
}
