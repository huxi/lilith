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

import de.huxhorn.lilith.swing.EventWrapperViewPanel;
import de.huxhorn.sulky.swing.Windows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SourceListsPanel
	extends JPanel
{
	final Logger logger = LoggerFactory.getLogger(SourceListsPanel.class);

	private JList sourceListList;
	private GenericSortedListModel<String> sourceListListModel;
	private EditSourceListAction editSourceListAction;
	private RemoveSourceListAction removeSourceListAction;
	private PreferencesDialog preferencesDialog;
	//private ApplicationPreferences applicationPreferences;
	private EditSourceListDialog editSourceListDialog;
	//private Map<String, List<Source>> sourceLists;
	private GenericSortedListModel<Source> listModel;

	public SourceListsPanel(PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog = preferencesDialog;
		//applicationPreferences=preferencesDialog.getApplicationPreferences();
		createUI();
	}

	private void createUI()
	{
		editSourceListDialog = new EditSourceListDialog(preferencesDialog);
		setLayout(new BorderLayout());
		sourceListListModel = new GenericSortedListModel<String>();
		sourceListList = new JList(sourceListListModel);
		sourceListList.addMouseListener(new SourceListMouseListener());
		JScrollPane sourceListScrollPane = new JScrollPane(sourceListList);
		//JPanel sourceListsListPanel = new JPanel(new GridLayout(1,1));
		//sourceListsListPanel.add(sourceListScrollPane);
		//sourceListsListPanel.setBorder(new TitledBorder("Source Lists"));
		sourceListScrollPane.setBorder(new TitledBorder("Source Lists"));

		ListCellRenderer sourceCellRenderer = new SourceCellRenderer();

		listModel = new GenericSortedListModel<Source>();
		JList listList = new JList(listModel);
		listList.setEnabled(false);
		listList.setCellRenderer(sourceCellRenderer);
		sourceListList.addListSelectionListener(new SourceListListSelectionListener());


		JScrollPane listScrollPane = new JScrollPane(listList);
		//JPanel listPanel = new JPanel(new GridLayout(1,1));
		//listPanel.add(listScrollPane);
		//listPanel.setBorder(new TitledBorder("List content"));
		listScrollPane.setBorder(new TitledBorder("List content"));

//		JPanel listsPanel = new JPanel(new GridBagLayout());
//		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.weightx = 0.5;
//		gbc.weighty = 0.5;
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.gridwidth = 1;
//		gbc.fill = GridBagConstraints.BOTH;
//
//		listsPanel.add(sourceListsListPanel, gbc);
//		gbc.gridx = 1;
//		listsPanel.add(listPanel, gbc);
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

/*
	public void saveSettings()
	{
		applicationPreferences.setSourceLists(this.sourceLists);
	}
*/

	private class SourceListSelectionListener
		implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			updateActions();
		}
	}

	public void initUI()
	{
		/*
		Map<String, List<Source>> sourceLists = applicationPreferences.getSourceLists();
		if(sourceLists == null)
		{
			this.sourceLists = new HashMap<String, List<Source>>();
		}
		else
		{
			this.sourceLists = new HashMap<String, List<Source>>(sourceLists);
		}
		*/
		updateSourceLists();
		updateListContents();
		updateActions();
	}

	private void updateSourceLists()
	{
		sourceListListModel.setData(new ArrayList<String>(preferencesDialog.getSourceListNames()));
	}

	private void updateListContents()
	{
		String selected = (String) sourceListList.getSelectedValue();

		List<Source> list = null;
		if(selected != null)
		{
			list = preferencesDialog.getSourceList(selected);
		}
		if(list == null)
		{
			list = new ArrayList<Source>();
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

	public void editSourceList(final String sourceListName, boolean add)
	{
		editSourceListDialog.setSourceListName(sourceListName);
		List<Source> data = preferencesDialog.getSourceList(sourceListName);
		/*
		if(sourceListName!=null)
		{
			if(sourceLists.containsKey(sourceListName))
			{
				data=new ArrayList<Source>(sourceLists.get(sourceListName));
			}
		}
		*/
		if(data == null)
		{
			data = new ArrayList<Source>();
		}
		else
		{
			data = new ArrayList<Source>(data);
		}
		editSourceListDialog.setAdding(add);
		editSourceListDialog.setSources(data);
		Windows.showWindow(editSourceListDialog, preferencesDialog, true);
		if(!editSourceListDialog.isCanceled())
		{
			String newSourceListName = editSourceListDialog.getSourcListeName();
			List<Source> sources = editSourceListDialog.getSources();
			preferencesDialog.setSourceList(sourceListName, newSourceListName, sources);
			/*
			if(sourceListName != null && sourceLists.containsKey(sourceListName))
			{
				sourceLists.remove(sourceListName);
			}
			sourceLists.put(newSourceListName, sources);
			updateSourceLists();
			updateListContents();
			*/
		}
		if(logger.isDebugEnabled()) logger.debug("After show of editSourceListDialog...");
	}

	private class AddSourceListAction
		extends AbstractAction
	{
		public AddSourceListAction()
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
			putValue(Action.SHORT_DESCRIPTION, "Add a new Source List.");
		}

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
		public EditSourceListAction()
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
			putValue(Action.SHORT_DESCRIPTION, "Edit a Source List.");
		}

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
		public RemoveSourceListAction()
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
			putValue(Action.SHORT_DESCRIPTION, "Remove the selected Source List.");
		}

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
		implements MouseListener
	{
		private final Logger logger = LoggerFactory.getLogger(SourceListMouseListener.class);


		public SourceListMouseListener()
		{
		}

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

	private class SourceListListSelectionListener
		implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			updateListContents();
			updateActions();
		}
	}
}
