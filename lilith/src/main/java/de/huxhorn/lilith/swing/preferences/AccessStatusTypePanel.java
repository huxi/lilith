/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
import de.huxhorn.lilith.swing.EventWrapperViewPanel;
import de.huxhorn.lilith.swing.preferences.table.AccessStatusTypeColumnModel;
import de.huxhorn.lilith.swing.preferences.table.AccessStatusTypeTableModel;
import de.huxhorn.lilith.swing.table.ColorScheme;
import de.huxhorn.sulky.swing.Windows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class AccessStatusTypePanel
	extends JPanel
{
	private final Logger logger = LoggerFactory.getLogger(AccessStatusTypePanel.class);

	private PreferencesDialog preferencesDialog;
	private ApplicationPreferences applicationPreferences;
	private EditAccessStatusTypeDialog editDialog;
	private JTable table;
	private AccessStatusTypeTableModel tableModel;
	private AccessStatusTypeColumnModel tableColumnModel;
	private EditConditionAction editAction;
	private Map<HttpStatus.Type, ColorScheme> colors;

	public AccessStatusTypePanel(PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog = preferencesDialog;
		applicationPreferences = preferencesDialog.getApplicationPreferences();
		createUI();
	}

	private void createUI()
	{
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

	public void updateConditions()
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

	public void saveSettings()
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
		public void valueChanged(ListSelectionEvent e)
		{
			updateConditions();
		}
	}


	private class EditConditionAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 95425194239658313L;

		public EditConditionAction()
		{
			super("Edit");
			Icon icon;
			{
				URL url = EventWrapperViewPanel.class.getResource("/tango/16x16/actions/list-add.png");
				if(url != null)
				{
					icon = new ImageIcon(url);
				}
				else
				{
					icon = null;
				}
			}
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.SHORT_DESCRIPTION, "Edit colors.");
		}

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
		implements MouseListener
	{
		public ConditionTableMouseListener()
		{
		}

		public void mouseClicked(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
			else if(evt.getButton() == MouseEvent.BUTTON1)
			{
				Point p = evt.getPoint();
				int row = table.rowAtPoint(p);

				List<HttpStatus.Type> types = tableModel.getData();
				if(row >= 0 && row < types.size())
				{
					if(evt.getClickCount() >= 2)
					{
						HttpStatus.Type type = types.get(row);
						edit(type);
					}
				}
			}
		}


		@SuppressWarnings({"UnusedDeclaration"})
		private void showPopup(MouseEvent evt)
		{
		}

		public void mousePressed(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mouseReleased(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
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
