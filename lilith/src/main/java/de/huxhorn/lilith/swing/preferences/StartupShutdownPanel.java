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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class StartupShutdownPanel
	extends JPanel
{
	private static final long serialVersionUID = -9006510598658979763L;

	private final ApplicationPreferences applicationPreferences;

	// Startup
	private final JCheckBox showSplashCheckbox;
	private final JCheckBox showTipOfTheDayCheckbox;
	private final JCheckBox checkForUpdateCheckbox;
	private final JCheckBox checkForSnapshotCheckbox;

	// Shutdown
	private final JCheckBox askBeforeQuitCheckbox;
	private final JCheckBox cleaningLogsOnExitCheckbox;

	StartupShutdownPanel(PreferencesDialog preferencesDialog)
	{
		this.applicationPreferences = preferencesDialog.getApplicationPreferences();

		showSplashCheckbox = new JCheckBox("Show splash screen.");
		checkForUpdateCheckbox = new JCheckBox("Check for updates on startup.");
		checkForSnapshotCheckbox = new JCheckBox("Check also for pre-release versions instead of just releases.");
		showTipOfTheDayCheckbox = new JCheckBox("Show Tip of the Day on startup.");

		askBeforeQuitCheckbox = new JCheckBox("Ask before exit.");
		cleaningLogsOnExitCheckbox = new JCheckBox("Clean logs on exit.");

		JPanel startupPanel = new JPanel(new GridLayout(4, 1));
		startupPanel.add(showSplashCheckbox);
		startupPanel.add(checkForUpdateCheckbox);
		startupPanel.add(checkForSnapshotCheckbox);
		startupPanel.add(showTipOfTheDayCheckbox);
		startupPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Startup"));

		JPanel shutdownPanel = new JPanel(new GridLayout(2, 1));
		shutdownPanel.add(askBeforeQuitCheckbox);
		shutdownPanel.add(cleaningLogsOnExitCheckbox);
		shutdownPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Shutdown"));

		setLayout(new GridBagLayout());

		{
			GridBagConstraints gbc = new GridBagConstraints();

			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.gridwidth = 1;
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(startupPanel, gbc);

			gbc.gridy = 1;
			gbc.weighty = 1;
			add(shutdownPanel, gbc);
		}
	}

	public void initUI()
	{
		showSplashCheckbox.setSelected(!applicationPreferences.isSplashScreenDisabled());
		checkForUpdateCheckbox.setSelected(applicationPreferences.isCheckingForUpdate());
		checkForSnapshotCheckbox.setSelected(applicationPreferences.isCheckingForSnapshot());
		showTipOfTheDayCheckbox.setSelected(applicationPreferences.isShowingTipOfTheDay());

		askBeforeQuitCheckbox.setSelected(applicationPreferences.isAskingBeforeQuit());
		cleaningLogsOnExitCheckbox.setSelected(applicationPreferences.isCleaningLogsOnExit());
	}

	void saveSettings()
	{
		applicationPreferences.setSplashScreenDisabled(!showSplashCheckbox.isSelected());
		applicationPreferences.setCheckingForUpdate(checkForUpdateCheckbox.isSelected());
		applicationPreferences.setCheckingForSnapshot(checkForSnapshotCheckbox.isSelected());
		applicationPreferences.setShowingTipOfTheDay(showTipOfTheDayCheckbox.isSelected());

		applicationPreferences.setAskingBeforeQuit(askBeforeQuitCheckbox.isSelected());
		applicationPreferences.setCleaningLogsOnExit(cleaningLogsOnExitCheckbox.isSelected());
	}

	void setShowingTipOfTheDay(boolean showingTipOfTheDay)
	{
		showTipOfTheDayCheckbox.setSelected(showingTipOfTheDay);
	}

	void setCheckingForUpdate(boolean checkingForUpdate)
	{
		checkForUpdateCheckbox.setSelected(checkingForUpdate);
	}

	void setCheckingForSnapshot(boolean checkingForSnapshot)
	{
		checkForSnapshotCheckbox.setSelected(checkingForSnapshot);
	}
}
