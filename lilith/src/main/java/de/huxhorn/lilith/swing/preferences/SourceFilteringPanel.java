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

import de.huxhorn.lilith.prefs.LilithPreferences;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceFilteringPanel
	extends JPanel
{
	private static final long serialVersionUID = 5989553538111458319L;

	final Logger logger = LoggerFactory.getLogger(SourceFilteringPanel.class);

	private final BlacklistAction blacklistAction;
	private final WhitelistAction whitelistAction;
	private final PreferencesDialog preferencesDialog;
	private final JComboBox<String> blacklistNames;
	private final DefaultComboBoxModel<String> blacklistNamesModel;
	private final JComboBox<String> whitelistNames;
	private final DefaultComboBoxModel<String> whitelistNamesModel;
	private final JRadioButton disabledButton;
	private final JRadioButton blacklistButton;
	private final JRadioButton whitelistButton;
	private final ListItemListener listItemListener;

	SourceFilteringPanel(PreferencesDialog preferencesDialog)
	{
		super();
		this.preferencesDialog = preferencesDialog;

		DisabledAction disabledAction = new DisabledAction();
		blacklistAction = new BlacklistAction();
		whitelistAction = new WhitelistAction();
		disabledButton = new JRadioButton(disabledAction);
		blacklistButton = new JRadioButton(blacklistAction);
		whitelistButton = new JRadioButton(whitelistAction);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(disabledButton);
		buttonGroup.add(blacklistButton);
		buttonGroup.add(whitelistButton);

		blacklistNamesModel = new DefaultComboBoxModel<>();
		whitelistNamesModel = new DefaultComboBoxModel<>();
		blacklistNames = new JComboBox<>(blacklistNamesModel);
		whitelistNames = new JComboBox<>(whitelistNamesModel);
		listItemListener = new ListItemListener();
		blacklistNames.addItemListener(listItemListener);
		whitelistNames.addItemListener(listItemListener);

		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		add(disabledButton, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		add(blacklistButton, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(blacklistNames, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		add(whitelistButton, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(whitelistNames, gbc);
	}

	public void initUI()
	{
		listItemListener.setInitializing(true);
		List<String> sourceListNames = preferencesDialog.getSourceListNames();
		boolean hasSourceLists = false;
		String fallbackSourceListName;
		if(sourceListNames.isEmpty())
		{
			fallbackSourceListName = "";
		}
		else
		{
			Collections.sort(sourceListNames);
			fallbackSourceListName = sourceListNames.get(0);
			hasSourceLists = true;
		}

		blacklistAction.setEnabled(hasSourceLists);
		whitelistAction.setEnabled(hasSourceLists);

		blacklistNames.setEnabled(hasSourceLists);
		whitelistNames.setEnabled(hasSourceLists);

		blacklistNamesModel.removeAllElements();
		whitelistNamesModel.removeAllElements();

		LilithPreferences.SourceFiltering filtering = preferencesDialog.getSourceFiltering();
		String blackListName = preferencesDialog.getBlackListName();
		String whiteListName = preferencesDialog.getWhiteListName();
		if(!sourceListNames.contains(blackListName))
		{
			if(logger.isInfoEnabled())
			{
				logger.info("Resetting blackListName '{}' to '{}'.", blackListName, fallbackSourceListName);
			}
			blackListName = fallbackSourceListName;
			preferencesDialog.setBlackListName(blackListName);
			if(filtering == LilithPreferences.SourceFiltering.BLACKLIST)
			{
				if(logger.isInfoEnabled()) logger.info("Resetting filtering '{}'.", filtering);
				filtering = LilithPreferences.SourceFiltering.NONE;
				preferencesDialog.setSourceFiltering(filtering);
			}
		}
		if(!sourceListNames.contains(whiteListName))
		{
			if(logger.isInfoEnabled())
			{
				logger.info("Resetting whiteListName '{}' to '{}'.", whiteListName, fallbackSourceListName);
			}
			whiteListName = fallbackSourceListName;
			preferencesDialog.setWhiteListName(whiteListName);
			if(filtering == LilithPreferences.SourceFiltering.WHITELIST)
			{
				if(logger.isInfoEnabled()) logger.info("Resetting filtering '{}'.", filtering);
				filtering = LilithPreferences.SourceFiltering.NONE;
				preferencesDialog.setSourceFiltering(filtering);
			}
		}

		if(hasSourceLists)
		{
			for(String s : sourceListNames)
			{
				blacklistNamesModel.addElement(s);
				whitelistNamesModel.addElement(s);
			}

			// black- and whitelistNames are already corrected.
			blacklistNamesModel.setSelectedItem(blackListName);
			whitelistNamesModel.setSelectedItem(whiteListName);
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
				break;
		}

		listItemListener.setInitializing(false);
	}

	private class DisabledAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -4154256012969198212L;

		DisabledAction()
		{
			super("None");
			putValue(Action.SHORT_DESCRIPTION, "No source filtering.");
		}

		/**
		 * Invoked when an action occurs.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isInfoEnabled()) logger.info("Disabled");
			preferencesDialog.setSourceFiltering(LilithPreferences.SourceFiltering.NONE);
		}
	}


	private class BlacklistAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -1181737422196108645L;

		BlacklistAction()
		{
			super("Blacklist on…");
			putValue(Action.SHORT_DESCRIPTION, "Blacklist on the selected source list.");
		}

		/**
		 * Invoked when an action occurs.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isInfoEnabled()) logger.info("Blacklist");
			preferencesDialog.setSourceFiltering(LilithPreferences.SourceFiltering.BLACKLIST);
		}
	}


	private class WhitelistAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3403085106091507255L;

		WhitelistAction()
		{
			super("Whitelist on…");
			putValue(Action.SHORT_DESCRIPTION, "Whitelist on the selected source list.");
		}

		/**
		 * Invoked when an action occurs.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isInfoEnabled()) logger.info("Whitelist");
			preferencesDialog.setSourceFiltering(LilithPreferences.SourceFiltering.WHITELIST);
		}
	}

	private class ListItemListener
		implements ItemListener
	{
		private boolean initializing = false;

		void setInitializing(boolean initializing)
		{
			this.initializing = initializing;
		}

		/**
		 * Invoked when an item has been selected or deselected by the user.
		 * The code written for this method performs the operations
		 * that need to occur when an item is selected (or deselected).
		 */
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			if(!initializing && e.getStateChange() == ItemEvent.SELECTED)
			{
				String item = (String) e.getItem();
				Object source = e.getSource();

				if(source == whitelistNames) // NOPMD
				{
					if(logger.isInfoEnabled()) logger.info("WhiteList Selected: {}", item);
					preferencesDialog.setWhiteListName(item);
					return;
				}

				if(source == blacklistNames) // NOPMD
				{
					if(logger.isInfoEnabled()) logger.info("BlackList Selected: {}", item);
					preferencesDialog.setBlackListName(item);
				}
			}
		}
	}
}
