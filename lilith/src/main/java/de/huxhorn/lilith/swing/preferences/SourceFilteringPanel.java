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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.Collections;
import java.util.List;

public class SourceFilteringPanel
	extends JPanel
{
	final Logger logger = LoggerFactory.getLogger(SourceFilteringPanel.class);
	private BlacklistAction blacklistAction;
	private WhitelistAction whitelistAction;
	private PreferencesDialog preferencesDialog;
	private JComboBox blackListNames;
	private JComboBox whiteListNames;
	private DefaultComboBoxModel blackListNamesModel;
	private DefaultComboBoxModel whiteListNamesModel;
	private JRadioButton disabledButton;
	private JRadioButton blacklistButton;
	private JRadioButton whitelistButton;
	private ListItemListener listItemListener;

	public SourceFilteringPanel(PreferencesDialog preferencesDialog)
	{
		super();
		this.preferencesDialog=preferencesDialog;
		createUI();
	}

	private void createUI()
	{
		DisabledAction disabledAction = new DisabledAction();
		blacklistAction = new BlacklistAction();
		whitelistAction = new WhitelistAction();
		disabledButton=new JRadioButton(disabledAction);
		blacklistButton =new JRadioButton(blacklistAction);
		whitelistButton =new JRadioButton(whitelistAction);

		ButtonGroup buttonGroup=new ButtonGroup();
		buttonGroup.add(disabledButton);
		buttonGroup.add(blacklistButton);
		buttonGroup.add(whitelistButton);

		blackListNamesModel = new DefaultComboBoxModel();
		whiteListNamesModel = new DefaultComboBoxModel();
		blackListNames = new JComboBox(blackListNamesModel);
		whiteListNames = new JComboBox(whiteListNamesModel);
		listItemListener=new ListItemListener();
		blackListNames.addItemListener(listItemListener);
		whiteListNames.addItemListener(listItemListener);

		setLayout(new GridBagLayout());

		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(disabledButton, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		add(blacklistButton, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(blackListNames, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		add(whitelistButton, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(whiteListNames, gbc);
	}

	public void initUI()
	{
		listItemListener.setInitializing(true);
		List<String> sourceListNames = preferencesDialog.getSourceListNames();
		boolean hasSourceLists=false;
		String fallbackSourceListName;
		if(sourceListNames.size()>0)
		{
			Collections.sort(sourceListNames);
			fallbackSourceListName =sourceListNames.get(0);
			hasSourceLists=true;
		}
		else
		{
			fallbackSourceListName ="";
		}

		blacklistAction.setEnabled(hasSourceLists);
		whitelistAction.setEnabled(hasSourceLists);

		blackListNames.setEnabled(hasSourceLists);
		whiteListNames.setEnabled(hasSourceLists);

		blackListNamesModel.removeAllElements();
		whiteListNamesModel.removeAllElements();

		ApplicationPreferences.SourceFiltering filtering=preferencesDialog.getSourceFiltering();
		String blackListName = preferencesDialog.getBlackListName();
		String whiteListName = preferencesDialog.getWhiteListName();
		if(!sourceListNames.contains(blackListName))
		{
			if(logger.isInfoEnabled()) logger.info("Resetting blackListName '{}' to '{}'.", blackListName, fallbackSourceListName);
			blackListName=fallbackSourceListName;
			preferencesDialog.setBlackListName(blackListName);
			if(filtering == ApplicationPreferences.SourceFiltering.BLACKLIST)
			{
				if(logger.isInfoEnabled()) logger.info("Resetting filtering '{}'.", filtering);
				filtering = ApplicationPreferences.SourceFiltering.NONE;
				preferencesDialog.setSourceFiltering(filtering);
			}
		}
		if(!sourceListNames.contains(whiteListName))
		{
			if(logger.isInfoEnabled()) logger.info("Resetting whiteListName '{}' to '{}'.", whiteListName, fallbackSourceListName);
			whiteListName=fallbackSourceListName;
			preferencesDialog.setWhiteListName(whiteListName);
			if(filtering == ApplicationPreferences.SourceFiltering.WHITELIST)
			{
				if(logger.isInfoEnabled()) logger.info("Resetting filtering '{}'.", filtering);
				filtering = ApplicationPreferences.SourceFiltering.NONE;
				preferencesDialog.setSourceFiltering(filtering);
			}
		}

		if(hasSourceLists)
		{
			for(String s : sourceListNames)
			{
				blackListNamesModel.addElement(s);
				whiteListNamesModel.addElement(s);
			}

			// black- and whiteListNames are already corrected.
			blackListNamesModel.setSelectedItem(blackListName);
			whiteListNamesModel.setSelectedItem(whiteListName);
		}

		// filtering is already corrected.
		switch(filtering)
		{
			case BLACKLIST:
				blacklistButton.setSelected(true);
				break;
			case WHITELIST:
				whitelistButton.setSelected(true);
				break;
			default:
				disabledButton.setSelected(true);
		}

		listItemListener.setInitializing(false);
	}

	private class DisabledAction
		extends AbstractAction
	{
		public DisabledAction()
		{
			super("None");
			putValue(Action.SHORT_DESCRIPTION, "No source filtering.");
		}

		/**
		 * Invoked when an action occurs.
		 */
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isInfoEnabled()) logger.info("Disabled");
			preferencesDialog.setSourceFiltering(ApplicationPreferences.SourceFiltering.NONE);
		}
	}


	private class BlacklistAction
		extends AbstractAction
	{
		public BlacklistAction()
		{
			super("Blacklist on...");
			putValue(Action.SHORT_DESCRIPTION, "Blacklist on the selected source list.");
		}

		/**
		 * Invoked when an action occurs.
		 */
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isInfoEnabled()) logger.info("Blacklist");
			preferencesDialog.setSourceFiltering(ApplicationPreferences.SourceFiltering.BLACKLIST);
		}
	}


	private class WhitelistAction
		extends AbstractAction
	{
		public WhitelistAction()
		{
			super("Whitelist on...");
			putValue(Action.SHORT_DESCRIPTION, "Whitelist on the selected source list.");
		}

		/**
		 * Invoked when an action occurs.
		 */
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isInfoEnabled()) logger.info("Whitelist");
			preferencesDialog.setSourceFiltering(ApplicationPreferences.SourceFiltering.WHITELIST);
		}
	}

	private class ListItemListener implements ItemListener
	{
		private boolean initializing=false;

		public boolean isInitializing()
		{
			return initializing;
		}

		public void setInitializing(boolean initializing)
		{
			this.initializing = initializing;
		}

		/**
		 * Invoked when an item has been selected or deselected by the user.
		 * The code written for this method performs the operations
		 * that need to occur when an item is selected (or deselected).
		 */
		public void itemStateChanged(ItemEvent e)
		{
			if(!initializing)
			{
				if(e.getStateChange()== ItemEvent.SELECTED)
				{
					String item=(String) e.getItem();
					if(e.getSource() == whiteListNames)
					{
						if(logger.isInfoEnabled()) logger.info("WhiteList Selected: {}", item);
						preferencesDialog.setWhiteListName(item);
					}
					else if(e.getSource() == blackListNames)
					{
						if(logger.isInfoEnabled()) logger.info("BlackList Selected: {}", item);
						preferencesDialog.setBlackListName(item);
					}
				}
			}
		}
	}
}
