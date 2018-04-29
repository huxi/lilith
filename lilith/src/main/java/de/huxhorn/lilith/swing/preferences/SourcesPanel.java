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

import de.huxhorn.lilith.swing.Icons;
import de.huxhorn.sulky.swing.Windows;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
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

public class SourcesPanel
	extends JPanel
{
	private static final long serialVersionUID = -1430756927138063766L;

	final Logger logger = LoggerFactory.getLogger(SourcesPanel.class);

	private final JTable sourceNameTable;
	private final SourceNameTableModel sourceNameTableModel;
	private final EditSourceNameAction editSourceNameAction;
	private final RemoveSourceNameAction removeSourceNameAction;
	private final EditSourceNameDialog editSourceNameDialog;
	private final PreferencesDialog preferencesDialog;

	SourcesPanel(PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog = preferencesDialog;

		setLayout(new BorderLayout());
		editSourceNameDialog = new EditSourceNameDialog(preferencesDialog);
		Map<String, String> sourceNames = new HashMap<>();
		sourceNameTableModel = new SourceNameTableModel(sourceNames);
		sourceNameTable = new JTable(sourceNameTableModel);
		sourceNameTable.setAutoCreateRowSorter(true);
		sourceNameTable.addMouseListener(new SourceNameTableMouseListener());
		JScrollPane sourceNameTableScrollPane = new JScrollPane(sourceNameTable);

		JPanel sourceNamesPanel = new JPanel(new GridLayout(1, 1));
		sourceNamesPanel.add(sourceNameTableScrollPane, BorderLayout.CENTER);
		JToolBar sourceNamesToolbar = new JToolBar();
		sourceNamesToolbar.setFloatable(false);

		ListSelectionModel sourceNameRowSelectionModel = sourceNameTable.getSelectionModel();
		sourceNameRowSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sourceNameRowSelectionModel.addListSelectionListener(new SourceNameTableRowSelectionListener());

		AddSourceNameAction addSourceNameAction = new AddSourceNameAction();
		editSourceNameAction = new EditSourceNameAction();
		removeSourceNameAction = new RemoveSourceNameAction();

		JButton addSourceNameButton = new JButton(addSourceNameAction);
		JButton editSourceNameButton = new JButton(editSourceNameAction);
		JButton removeSourceNameButton = new JButton(removeSourceNameAction);

		sourceNamesToolbar.add(addSourceNameButton);
		sourceNamesToolbar.add(editSourceNameButton);
		sourceNamesToolbar.add(removeSourceNameButton);

		add(sourceNamesToolbar, BorderLayout.NORTH);
		add(sourceNamesPanel, BorderLayout.CENTER);
	}

	public void initUI()
	{
		Map<String, String> sourceNames = preferencesDialog.getSourceNames();
		sourceNameTableModel.setData(sourceNames);
		updateSourceNames();
	}

	private void updateSourceNames()
	{
		int selectedRow = sourceNameTable.getSelectedRow();
		if(logger.isDebugEnabled()) logger.debug("selectedRow={}", selectedRow);
		// no need to call convert since we only want to know if selected or not.
		editSourceNameAction.setEnabled(selectedRow != -1);
		removeSourceNameAction.setEnabled(selectedRow != -1);
	}

	void editSourceName(final String sourceIdentifier)
	{
		Map<String, String> data = sourceNameTableModel.getData();
		if(data.containsKey(sourceIdentifier))
		{
			editSourceName(sourceIdentifier, false);
		}
		else
		{
			editSourceName(sourceIdentifier, true);
		}
	}

	private void editSourceName(final String sourceIdentifier, boolean add)
	{
		Map<String, String> data = sourceNameTableModel.getData();
		String sourceName = data.get(sourceIdentifier);
		if(sourceName == null)
		{
			sourceName = "";
		}
		editSourceNameDialog.setSourceIdentifier(sourceIdentifier);
		editSourceNameDialog.setSourceName(sourceName);
		editSourceNameDialog.setAdding(add);
		Windows.showWindow(editSourceNameDialog, preferencesDialog, true);
		if(!editSourceNameDialog.isCanceled())
		{
			String newIdentifier = editSourceNameDialog.getSourceIdentifier();
			newIdentifier = newIdentifier.trim();
			sourceName = editSourceNameDialog.getSourceName();
			sourceName = sourceName.trim();
			preferencesDialog.setSourceName(sourceIdentifier, newIdentifier, sourceName);
		}
	}

	private int convertSourceNameRow(int row)
	{
		return sourceNameTable.convertRowIndexToModel(row);
	}

	private class SourceNameTableRowSelectionListener
		implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			updateSourceNames();
		}
	}

	private class AddSourceNameAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 2159800920473132058L;

		AddSourceNameAction()
		{
			super("Add");
			putValue(Action.SMALL_ICON, Icons.ADD_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Add a new Source Name.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Add");
			editSourceName("", true);
		}
	}

	private class EditSourceNameAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -6686916429941183752L;

		EditSourceNameAction()
		{
			super("Edit");
			putValue(Action.SMALL_ICON, Icons.ADD_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Edit a Source Name.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Edit");
			int row = sourceNameTable.getSelectedRow();
			if(row >= 0)
			{
				row = convertSourceNameRow(row);
				String sourceIdentifier = (String) sourceNameTableModel
					.getValueAt(row, SourceNameTableModel.SOURCE_IDENTIFIER_COLUMN);
				editSourceName(sourceIdentifier, false);
			}
		}
	}

	private class RemoveSourceNameAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -4376772972629739348L;

		RemoveSourceNameAction()
		{
			super("Remove");
			putValue(Action.SMALL_ICON, Icons.REMOVE_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Remove the selected Source Name.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Remove");
			int row = sourceNameTable.getSelectedRow();
			if(row >= 0)
			{
				// this removes the row...
				row = convertSourceNameRow(row);
				sourceNameTableModel.setValueAt("", row, 0);
			}
		}
	}

	private class SourceNameTableMouseListener
		extends MouseAdapter
	{
		private final Logger logger = LoggerFactory.getLogger(SourceNameTableMouseListener.class);

		@Override
		public void mouseClicked(MouseEvent evt)
		{
			if(evt.getClickCount() >= 2 && evt.getButton() == MouseEvent.BUTTON1)
			{
				Point p = evt.getPoint();
				int row = sourceNameTable.rowAtPoint(p);
				row = convertSourceNameRow(row);
				if(logger.isDebugEnabled()) logger.debug("Source-Name-Row: {}", row);
				if(row >= 0)
				{
					String source = (String) sourceNameTableModel
						.getValueAt(row, SourceNameTableModel.SOURCE_IDENTIFIER_COLUMN);
					if(source == null)
					{
						source = "";
					}

					editSourceName(source, false);
					if(logger.isInfoEnabled()) logger.info("After show...");
				}
			}
		}
	}
}
