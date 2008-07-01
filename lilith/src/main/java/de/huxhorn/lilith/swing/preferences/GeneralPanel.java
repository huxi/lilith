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
import de.huxhorn.lilith.swing.EventWrapperViewPanel;

import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;

public class GeneralPanel
	extends JPanel
{
	private JCheckBox internalFramesCheckbox;
	private JCheckBox scrollingToBottomCheckbox;
	private JCheckBox autoOpenCheckbox;
	private JCheckBox autoCloseCheckbox;
	private JCheckBox autoFocusCheckbox;
	private JFileChooser applicationPathFileChooser;
	private JTextArea appPathTextArea;
	private JCheckBox checkForUpdateCheckbox;

	private PreferencesDialog preferencesDialog;
	private ApplicationPreferences applicationPreferences;
	private JCheckBox showIdentifierCheckbox;

	public GeneralPanel(PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog=preferencesDialog;
		applicationPreferences=preferencesDialog.getApplicationPreferences();
		createUI();
	}

	private void createUI()
	{
		// General
		internalFramesCheckbox=new JCheckBox("Use internal frames.");
		showIdentifierCheckbox=new JCheckBox("Show identifier for named sources.");
		autoOpenCheckbox=new JCheckBox("Automatically open new views on connection.");
		autoCloseCheckbox=new JCheckBox("Automatically close inactive views on disconnection.");
		autoFocusCheckbox=new JCheckBox("Automatically focus window of new view.");
		checkForUpdateCheckbox=new JCheckBox("Check for updates on startup.");
		scrollingToBottomCheckbox = new JCheckBox("Initial 'Scrolling to Bottom' setting");
		applicationPathFileChooser=new JFileChooser();
		applicationPathFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		JPanel appPathPanel = new JPanel();
		appPathPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Application Path"));
		appPathTextArea =new JTextArea(5,20);
		appPathTextArea.setEditable(false);
		appPathPanel.add(appPathTextArea);
		Action browseAppPathAction=new BrowseApplicationPathAction();
		JButton browseAppPathButton=new JButton(browseAppPathAction);
		appPathPanel.add(browseAppPathButton);

		setLayout(new FlowLayout());
		add(internalFramesCheckbox);
		add(scrollingToBottomCheckbox);
		add(showIdentifierCheckbox);
		add(autoOpenCheckbox);
		add(autoCloseCheckbox);
		add(autoFocusCheckbox);
		add(checkForUpdateCheckbox);
		add(appPathPanel);
	}

	public void initUI()
	{
		internalFramesCheckbox.setSelected(applicationPreferences.isUsingInternalFrames());
		scrollingToBottomCheckbox.setSelected(applicationPreferences.isScrollingToBottom());
		appPathTextArea.setText(applicationPreferences.getApplicationPath().getAbsolutePath());

		autoOpenCheckbox.setSelected(applicationPreferences.isAutoOpening());
		autoCloseCheckbox.setSelected(applicationPreferences.isAutoClosing());
		autoFocusCheckbox.setSelected(applicationPreferences.isAutoFocusingWindow());
		checkForUpdateCheckbox.setSelected(applicationPreferences.isCheckingForUpdate());
		showIdentifierCheckbox.setSelected(applicationPreferences.isShowingIdentifier());
	}

	public void saveSettings()
	{
		applicationPreferences.setUsingInternalFrames(internalFramesCheckbox.isSelected());
		applicationPreferences.setScrollingToBottom(scrollingToBottomCheckbox.isSelected());
		applicationPreferences.setAutoOpening(autoOpenCheckbox.isSelected());
		applicationPreferences.setAutoClosing(autoCloseCheckbox.isSelected());
		applicationPreferences.setAutoFocusingWindow(autoFocusCheckbox.isSelected());
		applicationPreferences.setCheckingForUpdate(checkForUpdateCheckbox.isSelected());
		applicationPreferences.setApplicationPath(new File(appPathTextArea.getText()));
		applicationPreferences.setShowingIdentifier(showIdentifierCheckbox.isSelected());
	}

	private class BrowseApplicationPathAction
		extends AbstractAction
	{
		public BrowseApplicationPathAction()
		{
			super();
			Icon icon;
			{
				URL url= EventWrapperViewPanel.class.getResource("/tango/16x16/actions/document-open.png");
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
			putValue(Action.SHORT_DESCRIPTION, "Browse for the application path.");
		}

		public void actionPerformed(ActionEvent e)
		{
			applicationPathFileChooser.setCurrentDirectory(applicationPreferences.getApplicationPath());
			int returnVal = applicationPathFileChooser.showDialog(preferencesDialog, "Select");
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = applicationPathFileChooser.getSelectedFile();
				appPathTextArea.setText(file.getAbsolutePath());
			}
		}
	}
}
