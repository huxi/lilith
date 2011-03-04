/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.prefs.LilithPreferences;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.MainFrame;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.swing.KeyStrokes;

import groovy.ui.Console;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class PreferencesDialog
	extends JDialog
{
	public enum Panes
	{
		General,
		StartupShutdown,
		Windows,
		Sounds,
		Sources,
		SourceLists,
		SourceFiltering,
		Conditions,
		Troubleshooting
	}

	private final Logger logger = LoggerFactory.getLogger(PreferencesDialog.class);

	private ApplicationPreferences applicationPreferences;
	private MainFrame mainFrame;

	private JTabbedPane tabbedPane;

	private GeneralPanel generalPanel;
	private StartupShutdownPanel startupShutdownPanel;
	private WindowsPanel windowsPanel;
	private SoundsPanel soundsPanel;
	private SourcesPanel sourcesPanel;
	private SourceListsPanel sourceListsPanel;
	private ConditionsPanel conditionsPanel;
	private Map<String, String> sourceNames;
	private Map<String, Set<String>> sourceLists;
	private SourceFilteringPanel sourceFilteringPanel;
	private String blackListName;
	private String whiteListName;
	private LilithPreferences.SourceFiltering sourceFiltering;
	private TroubleshootingPanel troubleshootingPanel;

	public PreferencesDialog(MainFrame mainFrame)
	{
		super(mainFrame, "Preferences");
		this.mainFrame = mainFrame;
		this.applicationPreferences = mainFrame.getApplicationPreferences();
		createUI();
	}

	public ApplicationPreferences getApplicationPreferences()
	{
		return applicationPreferences;
	}

	public MainFrame getMainFrame()
	{
		return mainFrame;
	}

	private void createUI()
	{
		generalPanel = new GeneralPanel(this);
		startupShutdownPanel = new StartupShutdownPanel(this);
		windowsPanel = new WindowsPanel(this);
		soundsPanel = new SoundsPanel(this);
		sourcesPanel = new SourcesPanel(this);
		sourceListsPanel = new SourceListsPanel(this);
		sourceFilteringPanel = new SourceFilteringPanel(this);
		conditionsPanel = new ConditionsPanel(this);
		troubleshootingPanel = new TroubleshootingPanel(this);

		tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(600, 500));

		tabbedPane.add("General", generalPanel);
		tabbedPane.add("Startup & Shutdown", startupShutdownPanel);
		tabbedPane.add("Windows", windowsPanel);
		tabbedPane.add("Sounds", soundsPanel);
		tabbedPane.add("Sources", sourcesPanel);
		tabbedPane.add("Source Lists", sourceListsPanel);
		tabbedPane.add("Source Filtering", sourceFilteringPanel);
		tabbedPane.add("Conditions", conditionsPanel);
		tabbedPane.add("Troubleshooting", troubleshootingPanel);

		// Main buttons
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		OkAction okAction = new OkAction();
		buttonPanel.add(new JButton(okAction));
		buttonPanel.add(new JButton(new ApplyAction()));
		buttonPanel.add(new JButton(new ResetAction()));
		CancelAction cancelAction = new CancelAction();
		buttonPanel.add(new JButton(cancelAction));


		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		KeyStrokes.registerCommand(tabbedPane, okAction, "OK_ACTION");
		KeyStrokes.registerCommand(buttonPanel, okAction, "OK_ACTION");
		KeyStrokes.registerCommand(tabbedPane, cancelAction, "CANCEL_ACTION");
		KeyStrokes.registerCommand(buttonPanel, cancelAction, "CANCEL_ACTION");
	}


	private void initUI()
	{
		generalPanel.initUI();
		startupShutdownPanel.initUI();
		windowsPanel.initUI();
		soundsPanel.initUI();
		sourceNames = applicationPreferences.getSourceNames();
		if(sourceNames == null)
		{
			sourceNames = new HashMap<String, String>();
		}
		else
		{
			sourceNames = new HashMap<String, String>(sourceNames);
		}
		sourceLists = applicationPreferences.getSourceLists();
		conditionsPanel.initUI();
		sourcesPanel.initUI();
		sourceListsPanel.initUI();
		sourceFilteringPanel.initUI();
		conditionsPanel.initUI();
	}

	public Map<String, String> getSourceNames()
	{
		return sourceNames;
	}

	public void setSourceNames(Map<String, String> sourceNames)
	{
		this.sourceNames = sourceNames;
		sourcesPanel.initUI();
		sourceListsPanel.initUI();
	}

	public void setSourceName(String oldIdentifier, String newIdentifier, String sourceName)
	{
		if(sourceNames.containsKey(oldIdentifier))
		{
			sourceNames.remove(oldIdentifier);
		}
		sourceNames.put(newIdentifier, sourceName);
		sourcesPanel.initUI();
		sourceListsPanel.initUI();
	}

	public void setSourceList(String oldName, String newName, List<Source> sourceList)
	{
		if(sourceLists.containsKey(oldName))
		{
			sourceLists.remove(oldName);
		}
		Set<String> newList = new HashSet<String>();
		for(Source s : sourceList)
		{
			newList.add(s.getIdentifier());
		}
		sourceLists.put(newName, newList);
		sourceListsPanel.initUI();
		sourceFilteringPanel.initUI();
	}

	/**
	 *
	 * @param name the name of the source list
	 * @return the source list of the given name or an empty List
	 */
	public List<Source> getSourceList(String name)
	{
		Set<String> srcList = sourceLists.get(name);
		if(srcList != null)
		{
			List<Source> result = new ArrayList<Source>();
			for(String current : srcList)
			{
				Source s = new Source();
				s.setIdentifier(current);
				s.setName(getSourceName(current));
				result.add(s);
			}
			Collections.sort(result);
			return result;
		}
		return new ArrayList<Source>();
	}

	private String getSourceName(String identifier)
	{
		String result = sourceNames.get(identifier);
		if(result == null)
		{
			result = identifier;
		}
		return result;
	}


	private void saveSettings()
	{
		generalPanel.saveSettings();
		startupShutdownPanel.saveSettings();
		windowsPanel.saveSettings();
		soundsPanel.saveSettings();
		conditionsPanel.saveSettings();
		applicationPreferences.setSourceNames(sourceNames);
		applicationPreferences.setSourceLists(sourceLists);
		applicationPreferences.setBlackListName(blackListName);
		applicationPreferences.setWhiteListName(whiteListName);
		applicationPreferences.setSourceFiltering(sourceFiltering);
	}

	private void resetSettings()
	{
		// just reinit from preferences, nobody would expect anything else...
		initUI();
	}

	public void setVisible(boolean visible)
	{
		if(visible != isVisible())
		{
			if(visible)
			{
				initUI();
			}
			super.setVisible(visible);
		}
	}

	public List<String> getSourceListNames()
	{
		return new ArrayList<String>(sourceLists.keySet());
	}

	public void removeSourceList(String sourceListName)
	{
		if(sourceLists.containsKey(sourceListName))
		{
			sourceLists.remove(sourceListName);
			sourceListsPanel.initUI();
			sourceFilteringPanel.initUI();
		}
	}

	public String getBlackListName()
	{
		if(blackListName == null)
		{
			blackListName = applicationPreferences.getBlackListName();
		}
		return blackListName;
	}

	public String getWhiteListName()
	{
		if(whiteListName == null)
		{
			whiteListName = applicationPreferences.getWhiteListName();
		}
		return whiteListName;
	}

	public LilithPreferences.SourceFiltering getSourceFiltering()
	{
		if(sourceFiltering == null)
		{
			sourceFiltering = applicationPreferences.getSourceFiltering();
		}
		return sourceFiltering;
	}

	public void setSourceFiltering(LilithPreferences.SourceFiltering sourceFiltering)
	{
		this.sourceFiltering = sourceFiltering;
	}

	public void setBlackListName(String blackListName)
	{
		this.blackListName = blackListName;
	}

	public void setWhiteListName(String whiteListName)
	{
		this.whiteListName = whiteListName;
	}

	public void setShowingTipOfTheDay(boolean showingTipOfTheDay)
	{
		startupShutdownPanel.setShowingTipOfTheDay(showingTipOfTheDay);
	}

	public void setCheckingForUpdate(boolean checkingForUpdate)
	{
		startupShutdownPanel.setCheckingForUpdate(checkingForUpdate);
	}

	public void setCheckingForSnapshot(boolean checkingForSnapshot)
	{
		startupShutdownPanel.setCheckingForSnapshot(checkingForSnapshot);
	}

	private class OkAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 3395474960394431088L;

		public OkAction()
		{
			super("Ok");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke("ENTER");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		}

		public void actionPerformed(ActionEvent e)
		{
			saveSettings();
			setVisible(false);
		}
	}

	private class ApplyAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -4047672339764590549L;

		public ApplyAction()
		{
			super("Apply");
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
		}

		public void actionPerformed(ActionEvent e)
		{
			saveSettings();
		}
	}

	private class ResetAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -7109027518233905200L;

		public ResetAction()
		{
			super("Reset");
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
		}

		public void actionPerformed(ActionEvent e)
		{
			resetSettings();
		}
	}

	private class CancelAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 6933499606501725571L;

		public CancelAction()
		{
			super("Cancel");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke("ESCAPE");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
		}

		public void actionPerformed(ActionEvent e)
		{
			setVisible(false);
		}
	}

	public void editSourceName(String sourceIdentifier)
	{
		showPane(Panes.Sources);
		sourcesPanel.editSourceName(sourceIdentifier);
	}

	public void showPane(Panes pane)
	{
		switch(pane)
		{
			case General:
				tabbedPane.setSelectedComponent(generalPanel);
				break;
			case StartupShutdown:
				tabbedPane.setSelectedComponent(startupShutdownPanel);
				break;
			case Windows:
				tabbedPane.setSelectedComponent(windowsPanel);
				break;
			case Sounds:
				tabbedPane.setSelectedComponent(soundsPanel);
				break;
			case Sources:
				tabbedPane.setSelectedComponent(sourcesPanel);
				break;
			case SourceLists:
				tabbedPane.setSelectedComponent(sourceListsPanel);
				break;
			case SourceFiltering:
				tabbedPane.setSelectedComponent(sourceFilteringPanel);
				break;
			case Conditions:
				tabbedPane.setSelectedComponent(conditionsPanel);
				break;
			case Troubleshooting:
				tabbedPane.setSelectedComponent(troubleshootingPanel);
				break;
		}
		if(!isVisible())
		{
			mainFrame.showPreferencesDialog();
		}
	}

	public void editDetailsFormatter()
	{
		Console console = new Console();
		File messageViewRoot = applicationPreferences.getDetailsViewRoot();
		File messageViewGroovyFile = new File(messageViewRoot, ApplicationPreferences.DETAILS_VIEW_GROOVY_FILENAME);

		EventWrapper<LoggingEvent> eventWrapper = new EventWrapper<LoggingEvent>(new SourceIdentifier("identifier", "secondaryIdentifier"), 17, new LoggingEvent());
		console.setVariable("eventWrapper", eventWrapper);

		console.setCurrentFileChooserDir(messageViewRoot);
		String text = "";
		if(!messageViewGroovyFile.isFile())
		{
			applicationPreferences.initDetailsViewRoot(true);
		}
		if(messageViewGroovyFile.isFile())
		{
			InputStream is;
			try
			{
				is = new FileInputStream(messageViewGroovyFile);
				List lines = IOUtils.readLines(is, "UTF-8");
				boolean isFirst = true;
				StringBuilder textBuffer = new StringBuilder();
				for(Object o : lines)
				{
					String s = (String) o;
					if(isFirst)
					{
						isFirst = false;
					}
					else
					{
						textBuffer.append("\n");
					}
					textBuffer.append(s);
				}
				text = textBuffer.toString();
			}
			catch(IOException e)
			{
				if(logger.isInfoEnabled())
				{
					logger.info("Exception while reading '" + messageViewGroovyFile.getAbsolutePath() + "'.", e);
				}
			}
		}
		else
		{
			if(logger.isWarnEnabled()) logger.warn("Failed to initialize detailsView file '{}'!", messageViewGroovyFile.getAbsolutePath());
		}
		console.run(); // initializes everything

		console.setScriptFile(messageViewGroovyFile);
		JTextPane inputArea = console.getInputArea();
		//inputArea.setText(text);
		Document doc = inputArea.getDocument();
		try
		{
			doc.remove( 0, doc.getLength() );
			doc.insertString( 0, text, null );
		}
		catch(BadLocationException e)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while setting source!", e);
		}
		console.setDirty(false);
		inputArea.setCaretPosition(0);
		inputArea.requestFocusInWindow();
//		console.selectFilename();
//		console.fileOpen();


	}


	public void editCondition(Condition condition)
	{
		tabbedPane.setSelectedComponent(conditionsPanel);
		if(!isVisible())
		{
			mainFrame.showPreferencesDialog();
		}
		conditionsPanel.editCondition(condition);
	}
}
