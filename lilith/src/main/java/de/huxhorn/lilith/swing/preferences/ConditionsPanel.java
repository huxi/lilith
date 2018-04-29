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

import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.Icons;
import de.huxhorn.lilith.swing.LilithActionId;
import de.huxhorn.lilith.swing.TextPreprocessor;
import de.huxhorn.lilith.swing.preferences.table.ConditionTableColumnModel;
import de.huxhorn.lilith.swing.preferences.table.ConditionTableModel;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.swing.Windows;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionsPanel
	extends JPanel
{
	private static final long serialVersionUID = -2757940105037104853L;

	private final Logger logger = LoggerFactory.getLogger(ConditionsPanel.class);

	private final PreferencesDialog preferencesDialog;
	private final ApplicationPreferences applicationPreferences;
	private final EditConditionDialog editConditionDialog;
	private final JTable conditionTable;
	private final ConditionTableModel conditionTableModel;
	private final EditConditionAction editConditionAction;
	private final RemoveConditionAction removeConditionAction;
	private final JTextArea conditionTextArea;
	private final MoveUpAction moveUpAction;
	private final MoveDownAction moveDownAction;

	private List<SavedCondition> conditions;

	ConditionsPanel(PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog = preferencesDialog;
		applicationPreferences = preferencesDialog.getApplicationPreferences();

		setLayout(new BorderLayout());
		editConditionDialog = new EditConditionDialog(preferencesDialog);

		conditionTableModel = new ConditionTableModel(null);
		conditionTable = new JTable(conditionTableModel);
		conditionTable.addMouseListener(new ConditionTableMouseListener());

		// TODO: D&D? Probably not worth the effort...

		conditionTable.setColumnModel(new ConditionTableColumnModel());

		conditionTextArea = new JTextArea();
		conditionTextArea.setEditable(false);
		JScrollPane tableScrollPane = new JScrollPane(conditionTable);
		JScrollPane descriptionScrollPane = new JScrollPane(conditionTextArea);
		descriptionScrollPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Details"));

		JPanel conditionsPanel = new JPanel(new GridLayout(2, 1));
		conditionsPanel.add(tableScrollPane);
		conditionsPanel.add(descriptionScrollPane);
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		ListSelectionModel sourceNameRowSelectionModel = conditionTable.getSelectionModel();
		sourceNameRowSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sourceNameRowSelectionModel.addListSelectionListener(new ConditionTableRowSelectionListener());

		//AddConditionAction addConditionAction = new AddConditionAction();
		editConditionAction = new EditConditionAction();
		removeConditionAction = new RemoveConditionAction();
		moveUpAction = new MoveUpAction();
		moveDownAction = new MoveDownAction();

		JButton editConditionButton = new JButton(editConditionAction);
		JButton removeConditionButton = new JButton(removeConditionAction);
		JButton moveUpButton = new JButton(moveUpAction);
		JButton moveDownButton = new JButton(moveDownAction);


		toolBar.add(editConditionButton);
		toolBar.add(removeConditionButton);
		toolBar.add(moveUpButton);
		toolBar.add(moveDownButton);

		add(toolBar, BorderLayout.NORTH);
		add(conditionsPanel, BorderLayout.CENTER);
	}

	public void initUI()
	{
		conditions = applicationPreferences.getConditions();
		if(logger.isDebugEnabled()) logger.debug("Conditions retrieved: {}", conditions);
		conditionTableModel.setData(conditions);
		updateConditions();
	}

	private void updateConditions()
	{
		int selectedRow = conditionTable.getSelectedRow();
		if(logger.isDebugEnabled()) logger.debug("selectedRow={}", selectedRow);
		// no need to call convert since we only want to know if selected or not.
		SavedCondition condition = null;
		int conditionCount = conditions.size();
		if(selectedRow > -1 && selectedRow < conditionCount)
		{
			condition = conditions.get(selectedRow);
		}
		editConditionAction.setEnabled(condition != null);
		removeConditionAction.setEnabled(condition != null);
		moveUpAction.setEnabled(selectedRow > 0);
		moveDownAction.setEnabled(selectedRow > -1 && selectedRow < conditionCount - 1);
		String description = "";
		if(condition != null)
		{
			description = TextPreprocessor.formatCondition(condition.getCondition());
		}
		conditionTextArea.setText(description);
	}

	void saveSettings()
	{
		if(logger.isInfoEnabled()) logger.info("Setting conditions to {}.", conditions);
		applicationPreferences.setConditions(conditions);
	}

	void editCondition(Condition condition)
	{
		SavedCondition savedCondition = null;
		for(SavedCondition current : conditions)
		{
			if(condition.equals(current.getCondition()))
			{
				savedCondition = current;
				if(logger.isDebugEnabled()) logger.debug("Found saved condition {}.", savedCondition);
				break;
			}
		}
		boolean adding = false;
		if(savedCondition == null)
		{
			adding = true;
			savedCondition = new SavedCondition(condition);
		}

		try
		{
			savedCondition = savedCondition.clone();
		}
		catch(CloneNotSupportedException e)
		{
			if(logger.isErrorEnabled()) logger.error("Couldn't clone saved condition!", e);
		}

		editConditionDialog.setSavedCondition(savedCondition);
		editConditionDialog.setAdding(adding);
		for(;;)
		{
			Windows.showWindow(editConditionDialog, preferencesDialog, true);
			if(editConditionDialog.isCanceled())
			{
				break;
			}

			SavedCondition newCondition = editConditionDialog.getSavedCondition();
			String newName = newCondition.getName();
			Condition containedCondition = newCondition.getCondition();
			int conditionIndex = -1;
			int nameIndex = -1;
			for(int i = 0; i < conditions.size(); i++)
			{
				if(containedCondition.equals(conditions.get(i).getCondition()))
				{
					conditionIndex = i;
				}
				if(newName.equals(conditions.get(i).getName()))
				{
					nameIndex = i;
				}
			}
			if(logger.isDebugEnabled()) logger.debug("conditionIndex={}, nameIndex={}", conditionIndex, nameIndex);
			boolean done = false;
			// check for duplicate name
			if(nameIndex >= 0 && nameIndex != conditionIndex)
			{
				// replace?
				String dialogTitle = "Duplicate condition name!";
				String message = "A different condition with the same name does already exist!\nOverwrite that condition?";
				int result = JOptionPane.showConfirmDialog(this, message, dialogTitle,
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(JOptionPane.OK_OPTION == result)
				{
					// yes, replace condition@nameIndex
					conditionTableModel.set(nameIndex, savedCondition);
					if(conditionIndex >= 0)
					{
						// remove other with same condition if it exists
						conditionTableModel.remove(conditionIndex);
						if(conditionIndex < nameIndex)
						{
							// correct name index
							nameIndex--;
						}
					}
					conditionIndex = nameIndex;
					done = true;
				}
				// no, leave done=false => editDialog will reopen
			}
			else if(conditionIndex < 0)
			{
				conditionIndex = conditionTableModel.add(savedCondition);
				done = true;
			}
			else
			{
				conditionTableModel.set(conditionIndex, savedCondition);
				done = true;
			}

			if(done)
			{
				conditionTable.setRowSelectionInterval(conditionIndex, conditionIndex);
				updateConditions();
				break;
			}
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
			putValue(Action.SHORT_DESCRIPTION, "Edit a Condition.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Edit");
			int row = conditionTable.getSelectedRow();
			if(row >= 0 && row < conditions.size())
			{
				SavedCondition condition = conditions.get(row);
				editCondition(condition.getCondition());
				updateConditions();
			}
		}
	}

	private class RemoveConditionAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 4573645407508010450L;

		RemoveConditionAction()
		{
			super("Remove");
			putValue(Action.SMALL_ICON, Icons.REMOVE_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Remove the selected Condition.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Remove");
			int row = conditionTable.getSelectedRow();
			if(row >= 0)
			{
				conditionTableModel.remove(row);
				int rowCount = conditionTableModel.getRowCount();
				if(row >= rowCount)
				{
					if(rowCount > 0)
					{
						row = rowCount - 1;
					}
					else
					{
						row = -1;
					}
				}

				if(row >= 0)
				{
					conditionTable.setRowSelectionInterval(row, row);
				}
				updateConditions();
			}
		}
	}

	private class MoveUpAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -5414336722079117405L;

		MoveUpAction()
		{
			super("Move up");
			putValue(Action.SMALL_ICON, Icons.resolveMenuIcon(LilithActionId.FIND_PREVIOUS));
			putValue(Action.SHORT_DESCRIPTION, "Move the selected Condition up.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("MoveUp");
			int row = conditionTable.getSelectedRow();
			if(row >= 0)
			{
				int newRow = conditionTableModel.moveUp(row);
				if(newRow >= 0)
				{
					conditionTable.setRowSelectionInterval(newRow, newRow);
				}

				updateConditions();
			}

		}
	}

	private class MoveDownAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -1115999498183305487L;

		MoveDownAction()
		{
			super("Move down");
			putValue(Action.SMALL_ICON, Icons.resolveMenuIcon(LilithActionId.FIND_NEXT));
			putValue(Action.SHORT_DESCRIPTION, "Move the selected Condition down.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("MoveDown");
			int row = conditionTable.getSelectedRow();
			if(row >= 0)
			{
				int newRow = conditionTableModel.moveDown(row);
				if(newRow >= 0)
				{
					conditionTable.setRowSelectionInterval(newRow, newRow);
				}
				updateConditions();
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
				int row = conditionTable.rowAtPoint(p);
				int col = conditionTable.columnAtPoint(p);
				if(row >= 0 && row < conditions.size())
				{
					if(col == ConditionTableColumnModel.DEFAULT_COLUMN_INDEX_ACTIVE)
					{
						SavedCondition condition = conditions.get(row);
						condition.setActive(!condition.isActive());
						conditionTableModel.set(row, condition);
					}
					else if(evt.getClickCount() >= 2)
					{
						SavedCondition condition = conditions.get(row);
						editCondition(condition.getCondition());
					}
				}
			}
		}
	}
}
