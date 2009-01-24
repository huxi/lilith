/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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

import de.huxhorn.lilith.swing.EventWrapperViewPanel;
import de.huxhorn.sulky.swing.Windows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SourcesPanel
	extends JPanel
{
	final Logger logger = LoggerFactory.getLogger(SourcesPanel.class);

	private JTable sourceNameTable;
	private SourceNameTableModel sourceNameTableModel;
	private EditSourceNameAction editSourceNameAction;
	private RemoveSourceNameAction removeSourceNameAction;
	private EditSourceNameDialog editSourceNameDialog;
	private Method convertMethod;
	private PreferencesDialog preferencesDialog;
	//private ApplicationPreferences applicationPreferences;

	public SourcesPanel(PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog = preferencesDialog;
		//applicationPreferences=preferencesDialog.getApplicationPreferences();
		createUI();
	}

	private void createUI()
	{
		setLayout(new BorderLayout());
		editSourceNameDialog = new EditSourceNameDialog(preferencesDialog);
		Map<String, String> sourceNames = new HashMap<String, String>();
		sourceNameTableModel = new SourceNameTableModel(sourceNames);
		sourceNameTable = new JTable(sourceNameTableModel);
		convertMethod = null;
		try
		{
			Method method = JTable.class.getMethod("setAutoCreateRowSorter", boolean.class);
			method.invoke(sourceNameTable, true);
			convertMethod = JTable.class.getMethod("convertRowIndexToModel", int.class);
		}
		catch(Throwable e)
		{
			if(logger.isInfoEnabled()) logger.info("While trying to activate autoRowSorter: {}", e.toString());
		}
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
//		if(sourceNames==null)
//		{
//			sourceNames=new HashMap<String, String>();
//		}
//		else
//		{
//			sourceNames=new HashMap<String, String>(sourceNames);
//		}
		sourceNameTableModel.setData(sourceNames);
		updateSourceNames();
	}

	public void updateSourceNames()
	{
		int selectedRow = sourceNameTable.getSelectedRow();
		if(logger.isDebugEnabled()) logger.debug("selectedRow={}", selectedRow);
		// no need to call convert since we only want to know if selected or not.
		editSourceNameAction.setEnabled(selectedRow != -1);
		removeSourceNameAction.setEnabled(selectedRow != -1);
	}

	public void editSourceName(final String sourceIdentifier)
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

	public void editSourceName(final String sourceIdentifier, boolean add)
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
			/*
			if(data.containsKey(sourceIdentifier))
			{
				data.remove(sourceIdentifier);
			}

			data.put(newIdentifier, sourceName);

			sourceNameTableModel.setData(data);
			*/
			preferencesDialog.setSourceName(sourceIdentifier, newIdentifier, sourceName);
		}
	}

/*
	public void saveSettings()
	{
		applicationPreferences.setSourceNames(sourceNameTableModel.getData());
	}
*/

	private int convertSourceNameRow(int row)
	{
		int result = row;
		if(convertMethod != null)
		{
			try
			{
				result = (Integer) convertMethod.invoke(sourceNameTable, row);
				if(logger.isInfoEnabled()) logger.info("Converted view-row {} to model-row {}.", row, result);
			}
			catch(Throwable e)
			{
				if(logger.isWarnEnabled()) logger.warn("Exception while converting row!!", e);
			}
		}
		return result;
	}

	private class SourceNameTableRowSelectionListener
		implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			updateSourceNames();
		}
	}

	private class AddSourceNameAction
		extends AbstractAction
	{
		public AddSourceNameAction()
		{
			super("Add");
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
			putValue(Action.SHORT_DESCRIPTION, "Add a new Source Name.");
		}

		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Add");
			editSourceName("", true);
		}
	}

	private class EditSourceNameAction
		extends AbstractAction
	{
		public EditSourceNameAction()
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
			putValue(Action.SHORT_DESCRIPTION, "Edit a Source Name.");
		}

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
		public RemoveSourceNameAction()
		{
			super("Remove");
			Icon icon;
			{
				URL url = EventWrapperViewPanel.class.getResource("/tango/16x16/actions/list-remove.png");
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
			putValue(Action.SHORT_DESCRIPTION, "Remove the selected Source Name.");
		}

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
		implements MouseListener
	{
		private final Logger logger = LoggerFactory.getLogger(SourceNameTableMouseListener.class);


		public SourceNameTableMouseListener()
		{
		}

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
			else if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}


		/**
		 * @noinspection UNUSED_SYMBOL
		 */
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
