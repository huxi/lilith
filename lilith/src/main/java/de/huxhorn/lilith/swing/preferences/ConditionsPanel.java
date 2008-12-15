/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
package de.huxhorn.lilith.swing.preferences;

import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.swing.Windows;

import javax.swing.JPanel;
import java.util.List;
import java.awt.BorderLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionsPanel
	extends JPanel
{
	private final Logger logger = LoggerFactory.getLogger(ConditionsPanel.class);

	private PreferencesDialog preferencesDialog;
	private ApplicationPreferences applicationPreferences;
	private EditConditionDialog editConditionDialog;
	List<SavedCondition> conditions;

	public ConditionsPanel(PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog=preferencesDialog;
		applicationPreferences=preferencesDialog.getApplicationPreferences();
		createUI();
	}

	private void createUI()
	{
		setLayout(new BorderLayout());
		editConditionDialog=new EditConditionDialog(preferencesDialog);
		// TODO: implement createUI
	}

	public void initUI()
	{
		conditions=applicationPreferences.getConditions();
		if(logger.isDebugEnabled()) logger.debug("Conditions retrieved: {}", conditions);
	}

	public void saveSettings()
	{
		if(logger.isInfoEnabled()) logger.info("Setting conditions to {}.", conditions);
		applicationPreferences.setConditions(conditions);
	}

	public void editCondition(Condition condition)
	{
		//TODO: finish implementation.
		//String conditionName=null;
		SavedCondition savedCondition=null;
		for(SavedCondition current : conditions)
		{
			if(condition.equals(current.getCondition()))
			{
				savedCondition=current;
				if(logger.isDebugEnabled()) logger.debug("Found saved condition {}.", savedCondition);
				break;
			}
		}
		boolean adding=false;
		if(savedCondition==null)
		{
			adding = true;
			savedCondition=new SavedCondition(condition);
		}

		editConditionDialog.setConditionName(savedCondition.getName());
		editConditionDialog.setAdding(adding);
		Windows.showWindow(editConditionDialog, preferencesDialog, true);
		if(!editConditionDialog.isCanceled())
		{
			// TODO: check for duplicate name
			String newName = editConditionDialog.getConditionName();
			int index=conditions.indexOf(savedCondition);
			if(index>-1)
			{
				conditions.remove(index);
				conditions.add(index, savedCondition);
			}
			else
			{
				conditions.add(savedCondition);
			}
			newName=newName.trim();
			savedCondition.setName(newName);
		}
	}
}
