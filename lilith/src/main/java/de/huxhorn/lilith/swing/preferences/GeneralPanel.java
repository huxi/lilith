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

import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.EventWrapperViewPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class GeneralPanel
	extends JPanel
{
	private PreferencesDialog preferencesDialog;
	private ApplicationPreferences applicationPreferences;

	// Views
	private JCheckBox scrollingToBottomCheckbox;
	private JCheckBox coloringWholeRowCheckbox;

	// Details view
	private JCheckBox showFullCallstackCheckbox;
	private JCheckBox showStackTraceCheckbox;

	// ???
	private JFileChooser applicationPathFileChooser;
	private JTextField appPathTextField;
	private JComboBox lookAndFeelCombo;

	private JCheckBox globalLoggingEnabledCheckbox;
	private JCheckBox loggingStatsEnabledCheckbox;

	public GeneralPanel(PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog = preferencesDialog;
		applicationPreferences = preferencesDialog.getApplicationPreferences();
		createUI();
	}

	private void createUI()
	{
		// General
		scrollingToBottomCheckbox = new JCheckBox("Initial 'Scrolling to Bottom' setting");
		coloringWholeRowCheckbox = new JCheckBox("Color whole row according to Level or Status");

		showFullCallstackCheckbox = new JCheckBox("Show full Callstack.");
		showStackTraceCheckbox = new JCheckBox("Show stacktrace of Throwables");

		applicationPathFileChooser = new JFileChooser();
		applicationPathFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		appPathTextField = new JTextField();
		appPathTextField.setEditable(false);
		Action browseAppPathAction = new BrowseApplicationPathAction();
		JButton browseAppPathButton = new JButton(browseAppPathAction);

		JPanel appPathPanel = new JPanel();
		appPathPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Application Path"));
		appPathPanel.setLayout(new GridBagLayout());
		// Application Path
		{
			GridBagConstraints gbc = new GridBagConstraints();

			gbc.gridwidth = 1;
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;

			gbc.gridx = 0;
			gbc.gridy = 0;
			appPathPanel.add(appPathTextField, gbc);

			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 0;
			appPathPanel.add(browseAppPathButton, gbc);
		}
		lookAndFeelCombo = new JComboBox();

		globalLoggingEnabledCheckbox = new JCheckBox("Enable global logs.");
		loggingStatsEnabledCheckbox = new JCheckBox("Enable logging statistics.");


		JPanel viewPanel = new JPanel(new GridLayout(2, 1));
		viewPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "View"));
		viewPanel.add(scrollingToBottomCheckbox);
		viewPanel.add(coloringWholeRowCheckbox);

		JPanel detailsPanel = new JPanel(new GridLayout(2, 1));
		detailsPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Details View"));
		detailsPanel.add(showFullCallstackCheckbox);
		detailsPanel.add(showStackTraceCheckbox);

		lookAndFeelCombo.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Look & Feel"));

		JPanel globalPanel = new JPanel(new GridLayout(2, 1));
		globalPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Global settings"));
		globalPanel.add(globalLoggingEnabledCheckbox);
		globalPanel.add(loggingStatsEnabledCheckbox);

		setLayout(new GridBagLayout());

		{
			GridBagConstraints gbc = new GridBagConstraints();

			gbc.gridwidth = 1;
			gbc.weightx = 1;
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.fill = GridBagConstraints.HORIZONTAL;

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(viewPanel, gbc);

			gbc.gridy = 1;
			add(detailsPanel, gbc);

			gbc.gridy = 2;
			add(lookAndFeelCombo, gbc);

			gbc.gridy = 3;
			add(appPathPanel, gbc);

			gbc.gridy = 4;
			gbc.weighty = 1;
			add(globalPanel, gbc);
		}
	}

	public void initUI()
	{
		scrollingToBottomCheckbox.setSelected(applicationPreferences.isScrollingToBottom());
		coloringWholeRowCheckbox.setSelected(applicationPreferences.isColoringWholeRow());
		showFullCallstackCheckbox.setSelected(applicationPreferences.isShowingFullCallstack());
		showStackTraceCheckbox.setSelected(applicationPreferences.isShowingStackTrace());

		ArrayList<String> lookAndFeels = new ArrayList<String>();
		for(UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
		{
			lookAndFeels.add(info.getName());
		}
		Collections.sort(lookAndFeels);
		int selectedIndex = 0;
		String lookAndFeel = applicationPreferences.getLookAndFeel();
		if(lookAndFeel == null || "".equals(lookAndFeel))
		{
			lookAndFeel = UIManager.getLookAndFeel().getName();
		}
		int idx = lookAndFeels.indexOf(lookAndFeel);
		if(idx > -1)
		{
			selectedIndex = idx;
		}
		else
		{
			idx = lookAndFeels.indexOf(ApplicationPreferences.STARTUP_LOOK_AND_FEEL);
			if(idx > -1)
			{
				selectedIndex = idx;
			}
		}
		lookAndFeelCombo.setModel(new DefaultComboBoxModel(lookAndFeels.toArray()));
		lookAndFeelCombo.setSelectedIndex(selectedIndex);

		String appPath = applicationPreferences.getApplicationPath().getAbsolutePath();
		appPathTextField.setText(appPath);
		appPathTextField.setToolTipText(appPath);

		globalLoggingEnabledCheckbox.setSelected(applicationPreferences.isGlobalLoggingEnabled());
		loggingStatsEnabledCheckbox.setSelected(applicationPreferences.isLoggingStatisticEnabled());
	}

	public void saveSettings()
	{
		applicationPreferences.setScrollingToBottom(scrollingToBottomCheckbox.isSelected());
		applicationPreferences.setColoringWholeRow(coloringWholeRowCheckbox.isSelected());
		applicationPreferences.setShowingFullCallstack(showFullCallstackCheckbox.isSelected());
		applicationPreferences.setShowingStackTrace(showStackTraceCheckbox.isSelected());

		applicationPreferences.setLookAndFeel((String) lookAndFeelCombo.getSelectedItem());

		applicationPreferences.setApplicationPath(new File(appPathTextField.getText()));

		applicationPreferences.setGlobalLoggingEnabled(globalLoggingEnabledCheckbox.isSelected());
		applicationPreferences.setLoggingStatisticEnabled(loggingStatsEnabledCheckbox.isSelected());
	}

	private class BrowseApplicationPathAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -5563121695654253673L;

		public BrowseApplicationPathAction()
		{
			super();
			Icon icon;
			{
				URL url = EventWrapperViewPanel.class.getResource("/tango/16x16/actions/document-open.png");
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
			putValue(Action.SHORT_DESCRIPTION, "Browse for the application path.");
		}

		public void actionPerformed(ActionEvent e)
		{
			applicationPathFileChooser.setCurrentDirectory(applicationPreferences.getApplicationPath());
			int returnVal = applicationPathFileChooser.showDialog(preferencesDialog, "Select");
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = applicationPathFileChooser.getSelectedFile();
				String appPath = file.getAbsolutePath();
				appPathTextField.setText(appPath);
				appPathTextField.setToolTipText(appPath);
			}
		}
	}
}
