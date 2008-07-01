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

import de.huxhorn.lilith.swing.EventWrapperViewPanel;
import de.huxhorn.sulky.swing.KeyStrokes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditSourceListDialog
	extends JDialog
{
	private final Logger logger = LoggerFactory.getLogger(EditSourceListDialog.class);

	private JList sourceList;
	private JList listList;
	private boolean adding;
	private boolean canceled;
	private JTextField sourceListName;
	private GenericSortedListModel<Source> sourcesListModel;
	private GenericSortedListModel<Source> listModel;
	private PreferencesDialog preferencesDialog;
	private AddSourceAction addSourceAction;
	private RemoveSourceAction removeSourceAction;
	private OkAction okAction;

	public EditSourceListDialog(PreferencesDialog owner)
	{
		super(owner);
		preferencesDialog=owner;
		setModal(true);
		createUi();
	}

	private void createUi()
	{
		okAction = new OkAction();
		Action cancelAction = new EditSourceListDialog.CancelAction();
		sourceListName = new JTextField(25);
		sourceListName.addKeyListener(new NameKeyListener());

		ListCellRenderer sourceCellRenderer=new SourceCellRenderer();

		sourcesListModel=new GenericSortedListModel<Source>();
		listModel=new GenericSortedListModel<Source>();
		sourceList = new JList(sourcesListModel);
		listList = new JList(listModel);
		sourceList.setCellRenderer(sourceCellRenderer);
		listList.setCellRenderer(sourceCellRenderer);
		sourceList.addMouseListener(new SourcesListMouseListener());
		listList.addMouseListener(new ListListMouseListener());

		JScrollPane sourceListScrollPane = new JScrollPane(sourceList);
//		JPanel sourcePanel = new JPanel(new GridLayout(1,1));
//		sourcePanel.add(sourceListScrollPane);
//		sourcePanel.setBorder(new TitledBorder("Sources"));
		sourceListScrollPane.setBorder(new TitledBorder("Sources"));

		JScrollPane listScrollPane = new JScrollPane(listList);
//		JPanel listPanel = new JPanel(new GridLayout(1,1));
//		listPanel.add(listScrollPane);
//		listPanel.setBorder(new TitledBorder("List content"));
		listScrollPane.setBorder(new TitledBorder("List content"));

		addSourceAction=new AddSourceAction();
		removeSourceAction=new RemoveSourceAction();
		JToolBar sourceToolbar=new JToolBar();
		sourceToolbar.setFloatable(false);

		JButton addSourceButton = new JButton(addSourceAction);
		JButton removeSourceButton = new JButton(removeSourceAction);

		sourceToolbar.add(addSourceButton);
		sourceToolbar.add(removeSourceButton);

		sourceList.addListSelectionListener(new SourceListSelectionListener());
		listList.addListSelectionListener(new ListListSelectionListener());

		JPanel listsPanel = new JPanel(new GridLayout(1,2));
		listsPanel.add(sourceListScrollPane);
		listsPanel.add(listScrollPane);


		JPanel centerPanel = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		centerPanel.add(sourceToolbar, gbc);

		gbc.weightx = 0.5;
		gbc.weighty = 0.5;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.BOTH;
		centerPanel.add(listsPanel, gbc);
//		centerPanel.add(sourcePanel, gbc);
//
//		gbc.gridx = 1;
//		centerPanel.add(listPanel, gbc);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.CENTER;

		gbc.insets = new Insets(0,5,0,0);
		mainPanel.add(new JLabel("Source List Name: "), gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		mainPanel.add(sourceListName, gbc);


		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		mainPanel.add(centerPanel, gbc);

		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);

		JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(new JButton(okAction));
		buttonPanel.add(new JButton(cancelAction));
		add(buttonPanel, BorderLayout.SOUTH);

		KeyStrokes.registerCommand(mainPanel, cancelAction, "CANCEL_ACTION");
		KeyStrokes.registerCommand(buttonPanel, cancelAction, "CANCEL_ACTION");

	}

	public void setAdding(boolean adding)
	{
		this.adding=adding;
		if(adding)
		{
			setTitle("Add a source list...");
			//sourceListName.setEditable(true);
		}
		else
		{
			setTitle("Edit a source list...");
			//sourceListName.setEditable(false);
		}
	}

	public void setVisible(boolean b)
	{
		if(b)
		{
			initUI();
			sourceListName.requestFocusInWindow();
		}
		super.setVisible(b);
	}

	private void initUI()
	{
		Map<String, String> sourceNames = new HashMap<String, String>(preferencesDialog.getSourceNames());
		List<Source> sourcesList=new ArrayList<Source>();
		for(Map.Entry<String, String> current: sourceNames.entrySet())
		{
			Source source=new Source();
			source.setIdentifier(current.getKey());
			source.setName(current.getValue());
			sourcesList.add(source);
		}
		sourcesListModel.setData(sourcesList);
		updateActions();
	}

	private void updateActions()
	{
		okAction.update();
		addSourceAction.update();
		removeSourceAction.update();
	}

	public boolean isAdding()
	{
		return adding;
	}

	public boolean isCanceled()
	{
		return canceled;
	}

	public void setSourceListName(String sourceName)
	{
		this.sourceListName.setText(sourceName);
	}

	public String getSourcListeName()
	{
		return sourceListName.getText();
	}

	public void setSources(List<Source> sources)
	{
		listModel.setData(sources);
	}

	public List<Source> getSources()
	{
		return listModel.getData();
	}

	private class OkAction
		extends AbstractAction
	{
		public OkAction()
		{
			super("Ok");
		}

		public void update()
		{
			String name = sourceListName.getText();
			if(name !=null && !"".equals(name.trim()))
			{
				setEnabled(true);
			}
			else
			{
				setEnabled(false);
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			String name = sourceListName.getText();
			if(name !=null && !"".equals(name.trim()))
			{
				canceled=false;
				setVisible(false);
			}
		}
	}

	private class CancelAction
		extends AbstractAction
	{

		public CancelAction()
		{
			super("Cancel");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke("ESCAPE");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			canceled=true;
			setVisible(false);
		}
	}

	private class SourcesListMouseListener extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			if(e.getClickCount()>1)
			{
				int index=sourceList.locationToIndex(e.getPoint());
				if(index!=-1)
				{
					Source source=sourcesListModel.getElementAt(index);
					if(source!=null)
					{
						listModel.add(source);
					}
				}
			}
		}
	}

	private class ListListMouseListener extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			if(e.getClickCount()>1)
			{
				int index=listList.locationToIndex(e.getPoint());
				if(index!=-1)
				{
					Source source=listModel.getElementAt(index);
					if(source!=null)
					{
						listModel.remove(source);
					}
				}
			}
		}
	}

	private class AddSourceAction
		extends AbstractAction
	{
		public AddSourceAction()
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
			putValue(Action.SHORT_DESCRIPTION, "Add the selected source(s).");
		}

		public void update()
		{
			setEnabled(!sourceList.isSelectionEmpty());
		}

		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Add");
			Object[] selected = sourceList.getSelectedValues();
			if(selected!=null && selected.length>0)
			{
				for(Object o:selected)
				{
					listModel.add((Source) o);
				}
			}
			updateActions();
		}
	}

	private class RemoveSourceAction
		extends AbstractAction
	{
		public RemoveSourceAction()
		{
			super("Remove");
			Icon icon;
			{
				URL url= EventWrapperViewPanel.class.getResource("/tango/16x16/actions/list-remove.png");
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
			putValue(Action.SHORT_DESCRIPTION, "Remove the selected source(s).");
		}

		public void update()
		{
			setEnabled(!listList.isSelectionEmpty());
		}

		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Remove");
			Object[] selected = listList.getSelectedValues();
			if(selected!=null && selected.length>0)
			{
				for(Object o:selected)
				{
					listModel.remove((Source) o);
				}
			}
			updateActions();
		}
	}

	private class SourceListSelectionListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			updateActions();
		}
	}

	private class ListListSelectionListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			updateActions();
		}
	}

	private class NameKeyListener implements KeyListener
	{
		public void keyTyped(KeyEvent e)
		{
			updateActions();
		}

		public void keyPressed(KeyEvent e)
		{
		}

		public void keyReleased(KeyEvent e)
		{
		}
	}
}
