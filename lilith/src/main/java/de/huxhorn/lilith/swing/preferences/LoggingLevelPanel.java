/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.EventWrapperViewPanel;
import de.huxhorn.lilith.swing.preferences.table.LoggingLevelColumnModel;
import de.huxhorn.lilith.swing.preferences.table.LoggingLevelTableModel;
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

public class LoggingLevelPanel
	extends JPanel
{
	private final Logger logger = LoggerFactory.getLogger(LoggingLevelPanel.class);

	private PreferencesDialog preferencesDialog;
	private ApplicationPreferences applicationPreferences;
	private EditLoggingLevelDialog editDialog;
	private JTable table;
	private LoggingLevelTableModel tableModel;
	private EditConditionAction editAction;
	private Map<LoggingEvent.Level, ColorScheme> colors;
	private LoggingLevelColumnModel tableColumnModel;

	public LoggingLevelPanel(PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog = preferencesDialog;
		applicationPreferences = preferencesDialog.getApplicationPreferences();
		createUI();
	}

	private void createUI()
	{
		setLayout(new BorderLayout());
		editDialog = new EditLoggingLevelDialog(preferencesDialog);

		tableModel = new LoggingLevelTableModel();
		table = new JTable(tableModel);
		table.addMouseListener(new ConditionTableMouseListener());

		tableColumnModel = new LoggingLevelColumnModel();
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
		colors = applicationPreferences.getLevelColors();
		if(logger.isDebugEnabled()) logger.debug("Conditions retrieved: {}", colors);
		tableColumnModel.setSchemes(colors);
		updateConditions();
	}

	public void updateConditions()
	{
		int selectedRow = table.getSelectedRow();
		if(logger.isDebugEnabled()) logger.debug("selectedRow={}", selectedRow);
		// no need to call convert since we only want to know if selected or not.
		LoggingEvent.Level level = null;
		List<LoggingEvent.Level> levels = tableModel.getData();
		int conditionCount = levels.size();
		if(selectedRow > -1 && selectedRow < conditionCount)
		{
			level = levels.get(selectedRow);
		}
		editAction.setEnabled(level != null);
	}

	public void saveSettings()
	{
		if(logger.isInfoEnabled()) logger.info("Setting level colors to {}.", colors);
		applicationPreferences.setLevelColors(colors);
	}

	public void edit(LoggingEvent.Level level)
	{
		if(level != null)
		{
			ColorScheme scheme = colors.get(level);

			editDialog.setScheme(scheme);
			editDialog.setLevel(level);
			Windows.showWindow(editDialog, preferencesDialog, true);
			if(editDialog.isCanceled())
			{
				return;
			}
			scheme = editDialog.getScheme();
			colors.put(level, scheme);
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
				List<LoggingEvent.Level> levels = tableModel.getData();
				if(row < levels.size())
				{
					LoggingEvent.Level level = levels.get(row);
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

				List<LoggingEvent.Level> levels = tableModel.getData();
				if(row >= 0 && row < levels.size())
				{
					if(evt.getClickCount() >= 2)
					{
						LoggingEvent.Level level = levels.get(row);
						edit(level);
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
