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

package de.huxhorn.lilith.swing.preferences;

import de.huxhorn.lilith.data.access.HttpStatus;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.Icons;
import de.huxhorn.lilith.swing.preferences.table.AccessStatusTypeColumnModel;
import de.huxhorn.lilith.swing.preferences.table.AccessStatusTypeTableModel;
import de.huxhorn.lilith.swing.table.ColorScheme;
import de.huxhorn.sulky.swing.Windows;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessStatusTypePanel
	extends JPanel
{
	private static final long serialVersionUID = -2356865948513043440L;
	private final Logger logger = LoggerFactory.getLogger(AccessStatusTypePanel.class);

	private final PreferencesDialog preferencesDialog;
	private final ApplicationPreferences applicationPreferences;
	private final EditAccessStatusTypeDialog editDialog;
	private final JTable table;
	private final AccessStatusTypeTableModel tableModel;
	private final AccessStatusTypeColumnModel tableColumnModel;
	private final EditConditionAction editAction;

	private Map<HttpStatus.Type, ColorScheme> colors;

	AccessStatusTypePanel(PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog = preferencesDialog;
		applicationPreferences = preferencesDialog.getApplicationPreferences();

		setLayout(new BorderLayout());
		editDialog = new EditAccessStatusTypeDialog(preferencesDialog);

		tableModel = new AccessStatusTypeTableModel();
		table = new JTable(tableModel);
		table.addMouseListener(new ConditionTableMouseListener());

		tableColumnModel = new AccessStatusTypeColumnModel();
		table.setColumnModel(tableColumnModel);

		JScrollPane tableScrollPane = new JScrollPane(table);

		JPanel conditionsPanel = new JPanel(new GridLayout(1, 1));
		conditionsPanel.add(tableScrollPane);
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		ListSelectionModel sourceNameRowSelectionModel = table.getSelectionModel();
		sourceNameRowSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sourceNameRowSelectionModel.addListSelectionListener(new ConditionTableRowSelectionListener());

		//AddConditionAction addConditionAction = new AddConditionAction();
		editAction = new EditConditionAction();

		//JButton addConditionButton = new JButton(addConditionAction);
		JButton editConditionButton = new JButton(editAction);


		//toolBar.add(addConditionButton);
		toolBar.add(editConditionButton);

		add(toolBar, BorderLayout.NORTH);
		add(conditionsPanel, BorderLayout.CENTER);
	}

	public void initUI()
	{
		colors = applicationPreferences.getStatusColors();
		if(logger.isDebugEnabled()) logger.debug("Conditions retrieved: {}", colors);
		tableColumnModel.setSchemes(colors);
		updateConditions();
	}

	private void updateConditions()
	{
		int selectedRow = table.getSelectedRow();
		if(logger.isDebugEnabled()) logger.debug("selectedRow={}", selectedRow);
		// no need to call convert since we only want to know if selected or not.
		HttpStatus.Type type = null;
		List<HttpStatus.Type> levels = tableModel.getData();
		int conditionCount = levels.size();
		if(selectedRow > -1 && selectedRow < conditionCount)
		{
			type = levels.get(selectedRow);
		}
		editAction.setEnabled(type != null);
	}

	void saveSettings()
	{
		if(logger.isInfoEnabled()) logger.info("Setting level colors to {}.", colors);
		applicationPreferences.setStatusColors(colors);
	}

	public void edit(HttpStatus.Type type)
	{
		if(type != null)
		{
			ColorScheme scheme = colors.get(type);

			editDialog.setScheme(scheme);
			editDialog.setType(type);
			Windows.showWindow(editDialog, preferencesDialog, true);
			if(editDialog.isCanceled())
			{
				return;
			}
			scheme = editDialog.getScheme();
			colors.put(type, scheme);
			updateConditions();
		}
	}

	private class ConditionTableRowSelectionListener
		implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			updateConditions();
		}
	}


	private class EditConditionAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 95425194239658313L;

		EditConditionAction()
		{
			super("Edit");
			putValue(Action.SMALL_ICON, Icons.ADD_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Edit colors.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Edit");
			int row = table.getSelectedRow();
			if(row >= 0)
			{
				List<HttpStatus.Type> types = tableModel.getData();
				if(row < types.size())
				{
					HttpStatus.Type level = types.get(row);
					edit(level);
					updateConditions();
				}
			}
		}
	}


	private class ConditionTableMouseListener
		extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent evt)
		{
			if(evt.getButton() == MouseEvent.BUTTON1)
			{
				Point p = evt.getPoint();
				int row = table.rowAtPoint(p);

				List<HttpStatus.Type> types = tableModel.getData();
				if(row >= 0 && row < types.size() && evt.getClickCount() >= 2)
				{
					HttpStatus.Type type = types.get(row);
					edit(type);
				}
			}
		}
	}
}
