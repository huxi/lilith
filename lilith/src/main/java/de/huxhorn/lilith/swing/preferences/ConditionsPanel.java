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
import de.huxhorn.lilith.swing.EventWrapperViewPanel;
import de.huxhorn.lilith.swing.preferences.table.ConditionIndexRenderer;
import de.huxhorn.lilith.swing.preferences.table.ConditionNameRenderer;
import de.huxhorn.lilith.swing.preferences.table.ConditionPreviewRenderer;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.swing.Windows;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.Action;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionsPanel
	extends JPanel
{
	private final Logger logger = LoggerFactory.getLogger(ConditionsPanel.class);

	private PreferencesDialog preferencesDialog;
	private ApplicationPreferences applicationPreferences;
	private EditConditionDialog editConditionDialog;
	private List<SavedCondition> conditions;
	private JTable conditionTable;
	private ConditionTableModel conditionTableModel;
	private EditConditionAction editConditionAction;
	private RemoveConditionAction removeConditionAction;
	private static final String DEFAULT_COLUMN_NAME_INDEX = "#";
	private static final String DEFAULT_COLUMN_NAME_NAME = "Name";
	private static final String DEFAULT_COLUMN_NAME_PREVIEW = "Preview";
	//private static final String DEFAULT_COLUMN_NAME_ACTIVE = "Active";
	private JTextArea conditionTextArea;

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

		conditionTableModel = new ConditionTableModel(null);
		conditionTable = new JTable(conditionTableModel);
		conditionTable.addMouseListener(new ConditionTableMouseListener());

		// TODO: MoveUpAction
		// TODO: MoveDownAction
		// TODO: active
		// TODO: implement ConditionTableColumnModel
		DefaultTableColumnModel columnModel=new DefaultTableColumnModel();

		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_INDEX);
			col.setCellRenderer(new ConditionIndexRenderer());
			columnModel.addColumn(col);
		}

		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_NAME);
			col.setCellRenderer(new ConditionNameRenderer());
			columnModel.addColumn(col);
		}

		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_PREVIEW);
			col.setCellRenderer(new ConditionPreviewRenderer());
			columnModel.addColumn(col);
		}

		/*

		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_ACTIVE);
			col.setCellRenderer(new IdRenderer());
			columnModel.addColumn(col);
		}
		*/

		conditionTable.setColumnModel(columnModel);

		conditionTextArea=new JTextArea();
		conditionTextArea.setEditable(false);
		JScrollPane tableScrollPane = new JScrollPane(conditionTable);
		JScrollPane descriptionScrollPane = new JScrollPane(conditionTextArea);
		descriptionScrollPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Details"));

		JPanel conditionsPanel = new JPanel(new GridLayout(2, 1));
		conditionsPanel.add(tableScrollPane);
		conditionsPanel.add(descriptionScrollPane);
		JToolBar toolBar=new JToolBar();
		toolBar.setFloatable(false);

		ListSelectionModel sourceNameRowSelectionModel = conditionTable.getSelectionModel();
		sourceNameRowSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sourceNameRowSelectionModel.addListSelectionListener(new ConditionTableRowSelectionListener());

		AddConditionAction addConditionAction = new AddConditionAction();
		editConditionAction = new EditConditionAction();
		removeConditionAction = new RemoveConditionAction();

		JButton addConditionButton = new JButton(addConditionAction);
		JButton editConditionButton = new JButton(editConditionAction);
		JButton removeConditionButton = new JButton(removeConditionAction);

		toolBar.add(addConditionButton);
		toolBar.add(editConditionButton);
		toolBar.add(removeConditionButton);

		add(toolBar, BorderLayout.NORTH);
		add(conditionsPanel, BorderLayout.CENTER);
	}

	public void initUI()
	{
		conditions=applicationPreferences.getConditions();
		if(logger.isDebugEnabled()) logger.debug("Conditions retrieved: {}", conditions);
		conditionTableModel.setData(conditions);
		updateConditions();
	}

	public void updateConditions()
	{
		int selectedRow = conditionTable.getSelectedRow();
		if(logger.isDebugEnabled()) logger.debug("selectedRow={}", selectedRow);
		// no need to call convert since we only want to know if selected or not.
		SavedCondition condition=null;
		if(selectedRow>-1 && selectedRow<conditions.size())
		{
			condition=conditions.get(selectedRow);
		}
		editConditionAction.setEnabled(condition!=null);
		removeConditionAction.setEnabled(condition!=null);
		String description="";
		if(condition!=null)
		{
			description=""+condition.getCondition();
		}
		conditionTextArea.setText(description);
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

		editConditionDialog.setSavedCondition(savedCondition);
		editConditionDialog.setAdding(adding);
		Windows.showWindow(editConditionDialog, preferencesDialog, true);
		if(!editConditionDialog.isCanceled())
		{
			// TODO: check for duplicate name
			SavedCondition newCondition = editConditionDialog.getSavedCondition();
			int index=-1;
			for(int i=0;i<conditions.size();i++)
			{
				if(newCondition.getCondition().equals(conditions.get(i).getCondition()))
				{
					index=i;
					break;
				}
			}
			//int index=conditions.indexOf(savedCondition);
			if(index>-1)
			{
				conditions.remove(index);
				conditions.add(index, savedCondition);
			}
			else
			{
				conditions.add(savedCondition);
			}
			conditionTableModel.setData(conditions);
			updateConditions();
		}
	}

	private class ConditionTableRowSelectionListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			updateConditions();
		}
	}

	private class AddConditionAction
		extends AbstractAction
	{
		public AddConditionAction()
		{
			super("Add");
			Icon icon;
			{
				URL url= EventWrapperViewPanel.class.getResource("/tango/16x16/actions/list-add.png");
				if(url!=null)
				{
					icon =new ImageIcon(url);
				}
				else
				{
					icon =null;
				}
			}
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.SHORT_DESCRIPTION, "Add a new Condition. Not yet implemented!");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Add");
			// TODO: implement
		}
	}

	private class EditConditionAction
		extends AbstractAction
	{
		public EditConditionAction()
		{
			super("Edit");
			Icon icon;
			{
				URL url= EventWrapperViewPanel.class.getResource("/tango/16x16/actions/list-add.png");
				if(url!=null)
				{
					icon =new ImageIcon(url);
				}
				else
				{
					icon =null;
				}
			}
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.SHORT_DESCRIPTION, "Edit a Condition.");
		}

		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Edit");
			int row = conditionTable.getSelectedRow();
			if(row >= 0)
			{
				if(row<conditions.size())
				{
					SavedCondition condition = conditions.get(row);
					editCondition(condition.getCondition());
					updateConditions();
				}
			}
		}
	}

	private class RemoveConditionAction
		extends AbstractAction
	{
		public RemoveConditionAction()
		{
			super("Remove");
			Icon icon;
			{
				URL url=EventWrapperViewPanel.class.getResource("/tango/16x16/actions/list-remove.png");
				if(url!=null)
				{
					icon =new ImageIcon(url);
				}
				else
				{
					icon =null;
				}
			}
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.SHORT_DESCRIPTION, "Remove the selected Condition.");
		}

		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Remove");
			int row=conditionTable.getSelectedRow();
			if(row >= 0)
			{
				if(row<conditions.size())
				{
					conditions.remove(row);
					conditionTableModel.setData(conditions);
					updateConditions();
				}
			}
		}
	}

	private class ConditionTableMouseListener
			implements MouseListener
	{
		public ConditionTableMouseListener()
		{
		}

		public void mouseClicked(MouseEvent evt)
		{
			if(evt.getClickCount()>=2 && evt.getButton()==MouseEvent.BUTTON1)
			{
				Point p = evt.getPoint();
				int row = conditionTable.rowAtPoint(p);
				if(row>=0 && row<conditions.size())
				{
					SavedCondition condition = conditions.get(row);
					editCondition(condition.getCondition());
				}
			}
			else if (evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}



		/** @noinspection UNUSED_SYMBOL*/
		private void showPopup(MouseEvent evt)
		{
		}

		public void mousePressed(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mouseReleased(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mouseEntered(MouseEvent e)
		{
		}

		public void mouseExited(MouseEvent e)
		{
		}

	}
}
