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
import java.util.Map;
import java.awt.BorderLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiltersPanel
	extends JPanel
{
	private final Logger logger = LoggerFactory.getLogger(FiltersPanel.class);

	private PreferencesDialog preferencesDialog;
	private ApplicationPreferences applicationPreferences;
	private EditConditionDialog editConditionDialog;

	public FiltersPanel(PreferencesDialog preferencesDialog)
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
		// TODO: implement initUI
	}

	public void editCondition(Condition condition)
	{
		//TODO: finish implementation.
		Map<String, Condition> conditions=preferencesDialog.getConditions();
		String conditionName=null;
		for(Map.Entry<String, Condition> current : conditions.entrySet())
		{
			Condition value=current.getValue();
			if(value.equals(condition))
			{
				conditionName=current.getKey();
				break;
			}
		}
		boolean adding=false;
		if(conditionName==null)
		{
			conditionName="";
			adding = true;
		}

		editConditionDialog.setConditionName(conditionName);
		editConditionDialog.setAdding(adding);
		Windows.showWindow(editConditionDialog, preferencesDialog, true);
		if(!editConditionDialog.isCanceled())
		{
			String newName = editConditionDialog.getConditionName();
			newName=newName.trim();
			conditions.remove(conditionName);
			// TODO: add question.
			conditions.put(newName, condition);
			if(logger.isInfoEnabled()) logger.info("Setting conditions to {}.", conditions);
			preferencesDialog.setConditions(conditions);
		}
	}
}
