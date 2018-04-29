/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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
import de.huxhorn.lilith.swing.Icons;
import de.huxhorn.lilith.swing.LilithActionId;
import de.huxhorn.lilith.tray.TraySupport;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class GeneralPanel
	extends JPanel
{
	private static final long serialVersionUID = -3070943109124678227L;

	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	private final PreferencesDialog preferencesDialog;
	private final ApplicationPreferences applicationPreferences;

	// Views
	private final JCheckBox scrollingSmoothlyCheckbox;
	private final JCheckBox scrollingToBottomCheckbox;
	private final JCheckBox coloringWholeRowCheckbox;

	// Details view
	private final JCheckBox showFullCallStackCheckbox;
	private final JCheckBox showStackTraceCheckbox;
	private final JCheckBox usingWrappedExceptionStyleCheckbox;

	// ???
	private final JFileChooser applicationPathFileChooser;
	private final JTextField appPathTextField;
	private final JComboBox<String> lookAndFeelCombo;
	private final JComboBox<String> defaultConditionCombo;

	private final JCheckBox globalLoggingEnabledCheckbox;
	private final JCheckBox trayActiveCheckbox;
	private final JCheckBox hidingOnCloseCheckbox;

	GeneralPanel(PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog = preferencesDialog;
		applicationPreferences = preferencesDialog.getApplicationPreferences();

		// General
		scrollingSmoothlyCheckbox = new JCheckBox("Smooth horizontal table scrolling");
		scrollingToBottomCheckbox = new JCheckBox("Initial 'Scrolling to Bottom' setting");
		coloringWholeRowCheckbox = new JCheckBox("Color whole row according to Level or Status");

		showFullCallStackCheckbox = new JCheckBox("Show full call stack.");
		showStackTraceCheckbox = new JCheckBox("Show stacktrace of Throwables");
		usingWrappedExceptionStyleCheckbox = new JCheckBox("Use wrapped exception style.");

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
		lookAndFeelCombo = new JComboBox<>();
		defaultConditionCombo = new JComboBox<>();

		globalLoggingEnabledCheckbox = new JCheckBox("Enable global logs.");
		trayActiveCheckbox = new JCheckBox("Enable tray icon.");
		hidingOnCloseCheckbox = new JCheckBox("Hide windows on close.");
		hidingOnCloseCheckbox.setToolTipText("Hide all windows on close of main frame instead of exiting the application. This is only relevant if tray icon is enabled.");

		JPanel viewPanel = new JPanel(new GridLayout(3, 1));
		viewPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "View"));
		viewPanel.add(scrollingSmoothlyCheckbox);
		viewPanel.add(scrollingToBottomCheckbox);
		viewPanel.add(coloringWholeRowCheckbox);

		JPanel detailsPanel = new JPanel(new GridLayout(3, 1));
		detailsPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Details View"));
		detailsPanel.add(showFullCallStackCheckbox);
		detailsPanel.add(showStackTraceCheckbox);
		detailsPanel.add(usingWrappedExceptionStyleCheckbox);

		lookAndFeelCombo.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Look & Feel"));
		defaultConditionCombo.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Default search condition"));

		JPanel globalPanel = new JPanel(new GridLayout(3, 1));
		globalPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Global settings"));
		globalPanel.add(globalLoggingEnabledCheckbox);
		globalPanel.add(trayActiveCheckbox);
		globalPanel.add(hidingOnCloseCheckbox);

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
			add(defaultConditionCombo, gbc);

			gbc.gridy = 4;
			add(appPathPanel, gbc);

			gbc.gridy = 5;
			gbc.weighty = 1;
			add(globalPanel, gbc);
		}
	}

	public void initUI()
	{
		scrollingSmoothlyCheckbox.setSelected(applicationPreferences.isScrollingSmoothly());
		scrollingToBottomCheckbox.setSelected(applicationPreferences.isScrollingToBottom());
		coloringWholeRowCheckbox.setSelected(applicationPreferences.isColoringWholeRow());
		showFullCallStackCheckbox.setSelected(applicationPreferences.isShowingFullCallStack());
		showStackTraceCheckbox.setSelected(applicationPreferences.isShowingStackTrace());
		usingWrappedExceptionStyleCheckbox.setSelected(applicationPreferences.isUsingWrappedExceptionStyle());

		// look and feel
		{
			ArrayList<String> lookAndFeels = new ArrayList<>();
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
			lookAndFeelCombo.setModel(new DefaultComboBoxModel<>(lookAndFeels.toArray(EMPTY_STRING_ARRAY)));
			lookAndFeelCombo.setSelectedIndex(selectedIndex);
		}

		// default condition name
		{
			List<String> conditionNames = applicationPreferences.retrieveAllConditions();
			String defaultName=applicationPreferences.getDefaultConditionName();
			int idx = conditionNames.indexOf(defaultName);
			if(idx < 0)
			{
				idx=0;
			}

			defaultConditionCombo.setModel(new DefaultComboBoxModel<>(conditionNames.toArray(EMPTY_STRING_ARRAY)));
			defaultConditionCombo.setSelectedIndex(idx);
		}
		String appPath = applicationPreferences.getApplicationPath().getAbsolutePath();
		appPathTextField.setText(appPath);
		appPathTextField.setToolTipText(appPath);

		globalLoggingEnabledCheckbox.setSelected(applicationPreferences.isGlobalLoggingEnabled());
		trayActiveCheckbox.setSelected(applicationPreferences.isTrayActive());
		trayActiveCheckbox.setEnabled(TraySupport.isAvailable());
		hidingOnCloseCheckbox.setSelected(applicationPreferences.isHidingOnClose());
		hidingOnCloseCheckbox.setEnabled(TraySupport.isAvailable());
	}

	void saveSettings()
	{
		applicationPreferences.setScrollingSmoothly(scrollingSmoothlyCheckbox.isSelected());
		applicationPreferences.setScrollingToBottom(scrollingToBottomCheckbox.isSelected());
		applicationPreferences.setColoringWholeRow(coloringWholeRowCheckbox.isSelected());
		applicationPreferences.setShowingFullCallStack(showFullCallStackCheckbox.isSelected());
		applicationPreferences.setShowingStackTrace(showStackTraceCheckbox.isSelected());
		applicationPreferences.setUsingWrappedExceptionStyle(usingWrappedExceptionStyleCheckbox.isSelected());

		applicationPreferences.setLookAndFeel((String) lookAndFeelCombo.getSelectedItem());
		applicationPreferences.setDefaultConditionName((String) defaultConditionCombo.getSelectedItem());

		applicationPreferences.setApplicationPath(new File(appPathTextField.getText()));

		applicationPreferences.setGlobalLoggingEnabled(globalLoggingEnabledCheckbox.isSelected());
		applicationPreferences.setTrayActive(trayActiveCheckbox.isSelected());
		applicationPreferences.setHidingOnClose(hidingOnCloseCheckbox.isSelected());
	}

	private class BrowseApplicationPathAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -5563121695654253673L;

		BrowseApplicationPathAction()
		{
			super();
			putValue(Action.SMALL_ICON, Icons.resolveMenuIcon(LilithActionId.OPEN));
			putValue(Action.SHORT_DESCRIPTION, "Browse for the application path.");
		}

		@Override
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
