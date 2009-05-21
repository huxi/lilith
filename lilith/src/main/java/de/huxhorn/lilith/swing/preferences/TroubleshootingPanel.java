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

import java.awt.event.ActionEvent;

import javax.swing.*;

public class TroubleshootingPanel
	extends JPanel
{
	private PreferencesDialog preferencesDialog;

	public TroubleshootingPanel(PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog = preferencesDialog;
		createUI();
	}

	private void createUI()
	{
		add(new JButton(new InitDetailsViewAction()));
		add(new JButton(new InitExampleScriptsAction()));
		add(new JButton(new DeleteAllLogsAction()));
	}

	public class InitDetailsViewAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 8374235720899930441L;

		public InitDetailsViewAction()
		{
			super("Reinitialize details view files.");
		}

		public void actionPerformed(ActionEvent actionEvent)
		{
			String dialogTitle = "Reinitialize details view files?";
			String message = "This resets all details view related files, all manual changes will be lost!\nReinitialize details view right now?";
			int result = JOptionPane.showConfirmDialog(preferencesDialog, message, dialogTitle,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(JOptionPane.OK_OPTION != result)
			{
				return;
			}

			ApplicationPreferences prefs = preferencesDialog.getApplicationPreferences();
			prefs.initDetailsViewRoot(true);
		}
	}

	public class InitExampleScriptsAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -4197531497673863904L;

		public InitExampleScriptsAction()
		{
			super("Reinitialize example groovy conditions.");
		}

		public void actionPerformed(ActionEvent actionEvent)
		{
			String dialogTitle = "Reinitialize example groovy conditions?";
			String message = "This overwrites all example groovy conditions. Other conditions are not changed!\nReinitialize example groovy conditions right now?";
			int result = JOptionPane.showConfirmDialog(preferencesDialog, message, dialogTitle,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(JOptionPane.OK_OPTION != result)
			{
				return;
			}

			ApplicationPreferences prefs = preferencesDialog.getApplicationPreferences();
			prefs.installExampleConditions();
		}
	}

	public class DeleteAllLogsAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 5218712842261152334L;

		public DeleteAllLogsAction()
		{
			super("Delete *all* logs.");
		}

		public void actionPerformed(ActionEvent actionEvent)
		{
			String dialogTitle = "Delete all log files?";
			String message = "This deletes *all* log files, even the Lilith logs and the global logs!\nDelete all log files right now?";
			int result = JOptionPane.showConfirmDialog(preferencesDialog, message, dialogTitle,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(JOptionPane.OK_OPTION != result)
			{
				return;
			}

			// TODO: delete all log files, starting with the Lilith log.
		}
	}
}
