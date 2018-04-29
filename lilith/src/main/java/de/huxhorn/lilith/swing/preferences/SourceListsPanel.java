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
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceListsPanel
	extends JPanel
{
	private static final long serialVersionUID = -4847362143627827385L;

	final Logger logger = LoggerFactory.getLogger(SourceListsPanel.class);

	private final JList<String> sourceListList;
	private final GenericSortedListModel<String> sourceListListModel;
	private final EditSourceListAction editSourceListAction;
	private final RemoveSourceListAction removeSourceListAction;
	private final PreferencesDialog preferencesDialog;
	private final EditSourceListDialog editSourceListDialog;
	private final GenericSortedListModel<Source> listModel;

	SourceListsPanel(PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog = preferencesDialog;

		editSourceListDialog = new EditSourceListDialog(preferencesDialog);
		setLayout(new BorderLayout());
		sourceListListModel = new GenericSortedListModel<>();
		sourceListList = new JList<>(sourceListListModel);
		sourceListList.addMouseListener(new SourceListMouseListener());
		JScrollPane sourceListScrollPane = new JScrollPane(sourceListList);
		sourceListScrollPane.setBorder(new TitledBorder("Source Lists"));

		ListCellRenderer<Source> sourceCellRenderer = new SourceCellRenderer();

		listModel = new GenericSortedListModel<>();
		JList<Source> listList = new JList<>(listModel);
		listList.setEnabled(false);
		listList.setCellRenderer(sourceCellRenderer);
		sourceListList.addListSelectionListener(new SourceListListSelectionListener());


		JScrollPane listScrollPane = new JScrollPane(listList);
		listScrollPane.setBorder(new TitledBorder("List content"));

		JPanel listsPanel = new JPanel(new GridLayout(1, 2));
		listsPanel.add(sourceListScrollPane);
		listsPanel.add(listScrollPane);


		JToolBar sourceListsToolbar = new JToolBar();
		sourceListsToolbar.setFloatable(false);

		AddSourceListAction addSourceListAction = new AddSourceListAction();
		editSourceListAction = new EditSourceListAction();
		removeSourceListAction = new RemoveSourceListAction();

		JButton addSourceListButton = new JButton(addSourceListAction);
		JButton editSourceListButton = new JButton(editSourceListAction);
		JButton removeSourceListButton = new JButton(removeSourceListAction);

		sourceListsToolbar.add(addSourceListButton);
		sourceListsToolbar.add(editSourceListButton);
		sourceListsToolbar.add(removeSourceListButton);

		ListSelectionModel sourceListSelectionModel = sourceListList.getSelectionModel();
		sourceListSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sourceListSelectionModel.addListSelectionListener(new SourceListSelectionListener());

		add(sourceListsToolbar, BorderLayout.NORTH);
		add(listsPanel, BorderLayout.CENTER);
	}

	private class SourceListSelectionListener
		implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			updateActions();
		}
	}

	public void initUI()
	{
		updateSourceLists();
		updateListContents();
		updateActions();
	}

	private void updateSourceLists()
	{
		sourceListListModel.setData(new ArrayList<>(preferencesDialog.getSourceListNames()));
	}

	private void updateListContents()
	{
		String selected = sourceListList.getSelectedValue();

		List<Source> list = null;
		if(selected != null)
		{
			list = preferencesDialog.getSourceList(selected);
		}
		if(list == null)
		{
			list = new ArrayList<>();
		}
		listModel.setData(list);
	}

	private void updateActions()
	{
		int selectedIndex = sourceListList.getSelectedIndex();
		if(logger.isDebugEnabled()) logger.debug("selectedIndex={}", selectedIndex);
		editSourceListAction.setEnabled(selectedIndex != -1);
		removeSourceListAction.setEnabled(selectedIndex != -1);
	}

	private void editSourceList(final String sourceListName, boolean add)
	{
		editSourceListDialog.setSourceListName(sourceListName);
		List<Source> data = preferencesDialog.getSourceList(sourceListName);
		if(data == null)
		{
			data = new ArrayList<>();
		}
		else
		{
			data = new ArrayList<>(data);
		}
		editSourceListDialog.setAdding(add);
		editSourceListDialog.setSources(data);
		Windows.showWindow(editSourceListDialog, preferencesDialog, true);
		if(!editSourceListDialog.isCanceled())
		{
			String newSourceListName = editSourceListDialog.getSourceListName();
			List<Source> sources = editSourceListDialog.getSources();
			preferencesDialog.setSourceList(sourceListName, newSourceListName, sources);
		}
		if(logger.isDebugEnabled()) logger.debug("After show of editSourceListDialog...");
	}

	private class AddSourceListAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -8798134029913043849L;

		AddSourceListAction()
		{
			super("Add");
			putValue(Action.SMALL_ICON, Icons.ADD_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Add a new Source List.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Add");
			String sourceListName = "";
			editSourceList(sourceListName, true);
		}
	}

	private class EditSourceListAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -951778721167306615L;

		EditSourceListAction()
		{
			super("Edit");
			putValue(Action.SMALL_ICON, Icons.ADD_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Edit a Source List.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Edit");
			int row = sourceListList.getSelectedIndex();
			if(row >= 0)
			{
				String sourceListName = sourceListListModel.getElementAt(row);
				editSourceList(sourceListName, false);
			}
		}
	}

	private class RemoveSourceListAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 1225472137465986071L;

		RemoveSourceListAction()
		{
			super("Remove");
			putValue(Action.SMALL_ICON, Icons.REMOVE_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Remove the selected Source List.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Remove");

			int row = sourceListList.getSelectedIndex();
			if(row >= 0)
			{
				String sourceListName = sourceListListModel.getElementAt(row);
				preferencesDialog.removeSourceList(sourceListName);
			}
		}
	}

	private class SourceListMouseListener
		extends MouseAdapter
	{
		private final Logger logger = LoggerFactory.getLogger(SourceListMouseListener.class);

		@Override
		public void mouseClicked(MouseEvent evt)
		{
			if(evt.getClickCount() >= 2 && evt.getButton() == MouseEvent.BUTTON1)
			{
				Point p = evt.getPoint();
				int row = sourceListList.locationToIndex(p);
				if(logger.isDebugEnabled()) logger.debug("Source-List-Index: {}", row);
				if(row >= 0)
				{
					String sourceListName = sourceListListModel.getElementAt(row);
					if(sourceListName == null)
					{
						sourceListName = "";
					}

					editSourceList(sourceListName, false);
					if(logger.isInfoEnabled()) logger.info("After show...");
				}
			}
		}
	}

	private class SourceListListSelectionListener
		implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			updateListContents();
			updateActions();
		}
	}
}
