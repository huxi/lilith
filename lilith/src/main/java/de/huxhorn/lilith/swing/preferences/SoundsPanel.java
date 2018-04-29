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

import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.Icons;
import de.huxhorn.lilith.swing.LilithActionId;
import de.huxhorn.lilith.swing.filefilters.Mp3FileFilter;
import de.huxhorn.sulky.sounds.Sounds;
import de.huxhorn.sulky.sounds.jlayer.JLayerSounds;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoundsPanel
	extends JPanel
{
	private static final long serialVersionUID = 4941824789717300256L;

	final Logger logger = LoggerFactory.getLogger(SoundsPanel.class);

	private final BrowseSoundAction browseSoundAction;
	private final PlaySoundAction playSoundAction;
	private final JCheckBox muteCheckbox;
	private final JFileChooser soundFileChooser;
	private final SoundLocationTableModel soundLocationTableModel;
	private final JTable soundLocationTable;
	private final Sounds sounds;
	private final PreferencesDialog preferencesDialog;
	private final ApplicationPreferences applicationPreferences;

	SoundsPanel(PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog = preferencesDialog;
		applicationPreferences = preferencesDialog.getApplicationPreferences();
		this.sounds = new JLayerSounds();

		setLayout(new BorderLayout());
		// Sounds
		muteCheckbox = new JCheckBox("Mute");
		muteCheckbox.addActionListener(new MuteActionListener());
		soundFileChooser = new JFileChooser();
		soundFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileFilter mp3FileFilter = new Mp3FileFilter();
		soundFileChooser.setFileFilter(mp3FileFilter);
		//JPanel soundsPanel = new JPanel(new BorderLayout());
		//soundsPanel.add(muteCheckbox, BorderLayout.NORTH);
		Map<String, String> soundLocations = new HashMap<>();
		soundLocationTableModel = new SoundLocationTableModel(soundLocations);
		soundLocationTable = new JTable(soundLocationTableModel);
		soundLocationTable.setRowSelectionAllowed(true);
		soundLocationTable.setColumnSelectionAllowed(false);
		ListSelectionModel soundRowSelectionModel = soundLocationTable.getSelectionModel();
		soundRowSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		soundRowSelectionModel.addListSelectionListener(new SoundLocationTableRowSelectionListener());

		JScrollPane soundLocationTableScrollPane = new JScrollPane(soundLocationTable);
		JPanel soundLocationsPanel = new JPanel(new GridLayout(1, 1));
		soundLocationsPanel.add(soundLocationTableScrollPane);

		JToolBar soundLocationsToolbar = new JToolBar();
		soundLocationsToolbar.setFloatable(false);

		playSoundAction = new PlaySoundAction();
		browseSoundAction = new BrowseSoundAction();

		JButton browseSoundButton = new JButton(browseSoundAction);
		JButton playButton = new JButton(playSoundAction);

		soundLocationsToolbar.add(browseSoundButton);
		soundLocationsToolbar.add(playButton);
		soundLocationsToolbar.add(muteCheckbox);

		add(soundLocationsToolbar, BorderLayout.NORTH);
		//add(muteCheckbox, BorderLayout.NORTH);
		add(soundLocationsPanel, BorderLayout.CENTER);
	}

	public void initUI()
	{
		boolean mute = applicationPreferences.isMute();
		Map<String, String> soundLocations = applicationPreferences.getSoundLocations();
		if(soundLocations == null)
		{
			soundLocations = new HashMap<>();
		}
		soundLocationTableModel.setData(soundLocations);
		sounds.setSoundLocations(soundLocations);
		updateSounds(mute);
	}

	void saveSettings()
	{
		applicationPreferences.setMute(muteCheckbox.isSelected());
		applicationPreferences.setSoundLocations(soundLocationTableModel.getData());
	}

	private void updateSounds(boolean mute)
	{
		if(logger.isDebugEnabled()) logger.debug("Updating mute settings... mute={}", mute);
		muteCheckbox.setSelected(mute);
		soundLocationTable.setEnabled(!mute);
		int selectedRow = soundLocationTable.getSelectedRow();
		if(logger.isDebugEnabled()) logger.debug("selectedRow={}", selectedRow);
		playSoundAction.setEnabled(!mute && selectedRow != -1);
		browseSoundAction.setEnabled(!mute && selectedRow != -1);
	}

	private class MuteActionListener
		implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			boolean mute = muteCheckbox.isSelected();
			updateSounds(mute);
		}
	}

	private class SoundLocationTableRowSelectionListener
		implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			boolean mute = muteCheckbox.isSelected();
			updateSounds(mute);
		}
	}

	private class PlaySoundAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 5777380069273022677L;

		PlaySoundAction()
		{
			super();
			putValue(Action.SMALL_ICON, Icons.PAUSED_MENU_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Play the selected sound.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Play");
			if(sounds != null)
			{
				int row = soundLocationTable.getSelectedRow();
				if(row != -1)
				{
					String eventName = (String) soundLocationTable.getValueAt(row, 0);
					sounds.play(eventName);
				}
			}
		}
	}

	private class BrowseSoundAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 4433633396782474246L;

		BrowseSoundAction()
		{
			super();
			putValue(Action.SMALL_ICON, Icons.resolveMenuIcon(LilithActionId.OPEN));
			putValue(Action.SHORT_DESCRIPTION, "Browse for a sound file.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			soundFileChooser.setCurrentDirectory(applicationPreferences.getSoundPath());
			int returnVal = soundFileChooser.showDialog(preferencesDialog, "Select");
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = soundFileChooser.getSelectedFile();
				String selectedFile = file.getAbsolutePath();
				int row = soundLocationTable.getSelectedRow();
				if(row != -1)
				{
					soundLocationTable.setValueAt(selectedFile, row, 1);
					if(logger.isDebugEnabled()) logger.debug("Set sound to {}.", selectedFile);
				}
				File parent = file.getParentFile();
				applicationPreferences.setSoundPath(parent);
			}
		}
	}
}
