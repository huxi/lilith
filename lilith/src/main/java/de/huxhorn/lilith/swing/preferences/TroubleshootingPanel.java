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

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.lilith.swing.AccessEventViewManager;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.LoggingEventViewManager;
import de.huxhorn.lilith.swing.MainFrame;
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.Reset;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

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
		add(new JButton(new CopySystemPropertiesAction()));
		add(new JButton(new GarbageCollectionAction()));
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
			// TODO: add "Show in Finder/Explorer" button if running on Mac/Windows
			if (JOptionPane.OK_OPTION != result)
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
			// TODO: add "Show in Finder/Explorer" button if running on Mac/Windows
			if (JOptionPane.OK_OPTION != result)
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
			// TODO: add "Show in Finder/Explorer" button if running on Mac/Windows
			if (JOptionPane.OK_OPTION != result)
			{
				return;
			}

			MainFrame mainFrame = preferencesDialog.getMainFrame();
			{ // Logging
				LoggingEventViewManager levm = mainFrame.getLoggingEventViewManager();
				Map<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> views = levm.getViews();
				for (Map.Entry<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> current : views.entrySet())
				{
					reset(current.getValue());
				}
			}

			{ // Access
				AccessEventViewManager levm = mainFrame.getAccessEventViewManager();
				Map<EventSource<AccessEvent>, ViewContainer<AccessEvent>> views = levm.getViews();
				for (Map.Entry<EventSource<AccessEvent>, ViewContainer<AccessEvent>> current : views.entrySet())
				{
					reset(current.getValue());
				}
			}

			mainFrame.cleanAllInactiveLogs();
		}
	}

	public class CopySystemPropertiesAction
			extends AbstractAction
	{
		private static final long serialVersionUID = -2375370123070284280L;

		public CopySystemPropertiesAction()
		{
			super("Copy properties");
			putValue(SHORT_DESCRIPTION, "Copy system properties to the clipboard.");
		}

		public void actionPerformed(ActionEvent actionEvent)
		{
			Properties props = System.getProperties();
			SortedMap<String, String> sortedProps=new TreeMap<String, String>();
			Enumeration<?> keys = props.propertyNames();
			while(keys.hasMoreElements())
			{
				String current=(String)keys.nextElement();
				String value=props.getProperty(current);
				if("line.separator".equals(current))
				{
					value=value.replace("\n","\\n");
					value=value.replace("\r","\\r");
				}
				sortedProps.put(current, value);

			}
			StringBuilder builder=new StringBuilder();
			for(Map.Entry<String, String> current:sortedProps.entrySet())
			{
				builder.append(current.getKey()).append("=").append(current.getValue()).append("\n");
			}
			preferencesDialog.getMainFrame().copyText(builder.toString());
		}
	}

	public class GarbageCollectionAction
			extends AbstractAction
	{
		private static final long serialVersionUID = -4636919088257143096L;

		public GarbageCollectionAction()
		{
			super("Execute GC");
			putValue(SHORT_DESCRIPTION, "Execute garbage collection.");
		}

		public void actionPerformed(ActionEvent actionEvent)
		{
			System.gc();
		}
	}

	public static void reset(ViewContainer<?> container)
	{
		if (container == null)
		{
			return;
		}
		EventSource eventSource = container.getEventSource();
		if (eventSource == null)
		{
			return;
		}
		Buffer<?> buffer = eventSource.getBuffer();
		Reset.reset(buffer);

	}
}
