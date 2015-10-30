/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2015 Joern Huxhorn
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
package de.huxhorn.lilith.swing;

import de.huxhorn.lilith.DateTimeFormatters;
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.FileBufferFactory;
import de.huxhorn.lilith.engine.LogFileFactory;
import de.huxhorn.sulky.formatting.HumanReadable;
import de.huxhorn.sulky.swing.KeyStrokes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class OpenPreviousDialog
	extends JDialog
{
	private static final long serialVersionUID = 8731011406364451059L;

	private final Logger logger = LoggerFactory.getLogger(OpenPreviousDialog.class);

	private enum EventType
	{
		LOGGING("Logging"), ACCESS("Access");
		private String typeName;

		EventType(String typeName)
		{
			this.typeName = typeName;
		}

		public String getTypeName()
		{
			return typeName;
		}
	}

	private static final String[] EMPTY_STRING_ARRAY = new String[]{};
	private static final SourceIdentifier[] EMPTY_SECONDARY_ARRAY = new SourceIdentifier[]{};

	private MainFrame mainFrame;
	private OpenAction openAction;
	private JTabbedPane tabbedPane;
	private OpenPreviousPanel<LoggingEvent> loggingPanel;
	private OpenPreviousPanel<AccessEvent> accessPanel;

	public OpenPreviousDialog(MainFrame owner)
	{
		super(owner, "Open previous log...");
		this.mainFrame = owner;
		createUI();
	}

	private void createUI()
	{
		openAction = new OpenAction();
		CancelAction cancelAction = new CancelAction();

		tabbedPane = new JTabbedPane();
		loggingPanel = new OpenPreviousPanel<>(mainFrame.getLoggingFileBufferFactory(), EventType.LOGGING);
		accessPanel = new OpenPreviousPanel<>(mainFrame.getAccessFileBufferFactory(), EventType.ACCESS);
		tabbedPane.add(loggingPanel.getPanelName(), loggingPanel);
		tabbedPane.add(accessPanel.getPanelName(), accessPanel);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(new JButton(openAction));
		buttonPanel.add(new JButton(cancelAction));

		JPanel contentPane = new JPanel(new BorderLayout());
		KeyStrokes.registerCommand(contentPane, openAction, "OPEN_ACTION");
		KeyStrokes.registerCommand(contentPane, cancelAction, "CANCEL_ACTION");

		contentPane.add(tabbedPane, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		setContentPane(contentPane);
		tabbedPane.addChangeListener(new TabChangeListener());
	}

	public void setVisible(boolean b)
	{
		if(b)
		{
			initUI();
		}
		super.setVisible(b);
	}

	private void initUI()
	{
		loggingPanel.initUI();
		accessPanel.initUI();
		initOpenAction();
	}

	private void initOpenAction()
	{
		Component comp = tabbedPane.getSelectedComponent();
		if(comp instanceof OpenPreviousPanel)
		{
			OpenPreviousPanel panel = (OpenPreviousPanel) comp;
			SourceIdentifier selectedSource = panel.getSelectedSource();
			openAction.setEnabled(selectedSource != null);
		}
	}


	public void openSelection()
	{
		Component comp = tabbedPane.getSelectedComponent();
		if(comp instanceof OpenPreviousPanel)
		{
			OpenPreviousPanel panel = (OpenPreviousPanel) comp;
			SourceIdentifier selectedSource = panel.getSelectedSource();
			if(selectedSource != null)
			{
				if(panel.getEventType() == EventType.LOGGING)
				{
					mainFrame.openPreviousLogging(selectedSource);
				}
				else
				{
					mainFrame.openPreviousAccess(selectedSource);
				}
			}
		}
		OpenPreviousDialog.this.setVisible(false);
	}

	private class TabChangeListener
		implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("stateChanged");
			initOpenAction();
		}
	}

	private class OpenAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -7076284393995744935L;

		public OpenAction()
		{
			super("Open");
			KeyStroke accelerator = LilithKeyStrokes.getKeyStroke(LilithKeyStrokes.ENTER);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			openSelection();
			OpenPreviousDialog.this.setVisible(false);
		}
	}

	private class CancelAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3717298306270939316L;

		public CancelAction()
		{
			super("Cancel");
			KeyStroke accelerator = LilithKeyStrokes.getKeyStroke(LilithKeyStrokes.ESCAPE);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			OpenPreviousDialog.this.setVisible(false);
		}
	}

	private class OpenPreviousPanel<T extends Serializable>
		extends JPanel
	{
		private static final long serialVersionUID = 1635486188020609000L;

		private List<List<SourceIdentifier>> secondaries;
		private JList<String> primaryList;
		private JList<SourceIdentifier> secondaryList;
		private JTextArea infoArea;
		private DecimalFormat eventCountFormat;
		private final FileBufferFactory<T> fileBufferFactory;
		private final LogFileFactory logFileFactory;
		private EventType eventType;
		private SourceIdentifier selectedSource;

		public OpenPreviousPanel(FileBufferFactory<T> fileBufferFactory, EventType eventType)
		{
			this.fileBufferFactory = fileBufferFactory;
			this.logFileFactory = fileBufferFactory.getLogFileFactory();
			this.eventType = eventType;
			eventCountFormat = new DecimalFormat("#,###");
			this.createUI();
		}

		public SourceIdentifier getSelectedSource()
		{
			return selectedSource;
		}

		public String getPanelName()
		{
			return eventType.getTypeName();
		}

		public EventType getEventType()
		{
			return eventType;
		}

		private void setSelectedSource(SourceIdentifier selected)
		{
			if(logger.isDebugEnabled()) logger.debug("Selected source: {}", selected);
			this.selectedSource = selected;
			initOpenAction();
			updateInfoArea();
		}

		private void createUI()
		{
			primaryList = new JList<>();
			secondaryList = new JList<>();

			primaryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			primaryList.addListSelectionListener(new PrimaryListSelectionListener());

			secondaryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			secondaryList.addListSelectionListener(new SecondaryListSelectionListener());
			secondaryList.addMouseListener(new SecondaryMouseListener());

			JScrollPane primaryPane = new JScrollPane(primaryList);
			primaryPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Primary"));
			primaryPane.setPreferredSize(new Dimension(200, 300));
			JScrollPane secondaryPane = new JScrollPane(secondaryList);
			secondaryPane.setPreferredSize(new Dimension(300, 300));
			secondaryPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Secondary"));
			JSplitPane filePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, primaryPane, secondaryPane);
			setLayout(new BorderLayout());
			add(filePane, BorderLayout.CENTER);
			JPanel infoPanel = new JPanel(new GridLayout(1, 1));
			infoArea = new JTextArea(3, 40);
			infoArea.setEditable(false);
			infoPanel.add(infoArea);
			infoPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Log Information"));
			add(infoPanel, BorderLayout.SOUTH);
		}

		public void initUI()
		{
			List<SourceIdentifier> inactiveLogs = mainFrame.collectInactiveLogs(logFileFactory);
			ApplicationPreferences applicationPreferences = mainFrame.getApplicationPreferences();
			Map<String, String> sourceNames = null;
			if(applicationPreferences != null)
			{
				sourceNames = applicationPreferences.getSourceNames();
			}
			SortedMap<String, List<SourceIdentifier>> inactiveMap = new TreeMap<>();
			for(SourceIdentifier current : inactiveLogs)
			{
				String primary = ViewActions.getPrimarySourceTitle(current.getIdentifier(), sourceNames, false);
				List<SourceIdentifier> sourceList = inactiveMap.get(primary);
				if(sourceList == null)
				{
					sourceList = new ArrayList<>();
					inactiveMap.put(primary, sourceList);
				}
				sourceList.add(current);
			}

			int primaryCount = inactiveMap.size();
			if(primaryCount > 0)
			{
				ArrayList<String> primaries = new ArrayList<>(primaryCount);
				secondaries = new ArrayList<>(primaryCount);
				for(Map.Entry<String, List<SourceIdentifier>> current : inactiveMap.entrySet())
				{
					primaries.add(current.getKey());
					List<SourceIdentifier> value = current.getValue();
					Collections.sort(value, new LastModifiedComparator());
					secondaries.add(value);
				}

				primaryList.setListData(primaries.toArray(new String[primaries.size()]));
				SourceIdentifier[] currentSecondary = EMPTY_SECONDARY_ARRAY;
				if(!secondaries.isEmpty())
				{
					List<SourceIdentifier> zero = secondaries.get(0);
					if(!zero.isEmpty())
					{
						currentSecondary = zero.toArray(new SourceIdentifier[zero.size()]);
					}
				}
				secondaryList.setListData(currentSecondary);
				primaryList.setSelectedIndex(0);
			}
			else
			{
				primaryList.setListData(EMPTY_STRING_ARRAY);
				secondaryList.setListData(EMPTY_SECONDARY_ARRAY);
			}

		}


		private String getLogInfo(SourceIdentifier selectedSource)
		{
			if(selectedSource == null)
			{
				return "";
			}

			ApplicationPreferences applicationPreferences = mainFrame.getApplicationPreferences();
			Map<String, String> sourceNames = null;
			if(applicationPreferences != null)
			{
				sourceNames = applicationPreferences.getSourceNames();
			}
			StringBuilder result = new StringBuilder();
			result.append(ViewActions.getPrimarySourceTitle(selectedSource.getIdentifier(), sourceNames, true));
			String secondary = selectedSource.getSecondaryIdentifier();
			if(secondary != null)
			{
				result.append(" - ").append(selectedSource.getSecondaryIdentifier());
			}
			result.append("\n");

			long numberOfEvents = logFileFactory.getNumberOfEvents(selectedSource);
			long sizeOnDisk = logFileFactory.getSizeOnDisk(selectedSource);
			File dataFile = logFileFactory.getDataFile(selectedSource);
			long timestamp = dataFile.lastModified();

			result.append("Number of events: ")
				.append(eventCountFormat.format(numberOfEvents)).append("\n")
				.append("Size: ")
				.append(HumanReadable.getHumanReadableSize(sizeOnDisk, true, false))
				.append("bytes\n")
				.append("Timestamp: ")
				.append(DateTimeFormatters.DATETIME_IN_SYSTEM_ZONE_SPACE.format(Instant.ofEpochMilli(timestamp)));

			return result.toString();
		}

		public void updateInfoArea()
		{
			infoArea.setText(getLogInfo(selectedSource));
		}


		private class PrimaryListSelectionListener
			implements ListSelectionListener
		{

			public void valueChanged(ListSelectionEvent e)
			{
				JList source = (JList) e.getSource();
				int selectedIndex = source.getSelectedIndex();
				if(selectedIndex >= 0 && selectedIndex < secondaries.size())
				{
					List<SourceIdentifier> sources = secondaries.get(selectedIndex);
					secondaryList.setListData(sources.toArray(new SourceIdentifier[sources.size()]));
					secondaryList.setSelectedIndex(0);
				}
				else
				{
					secondaryList.setListData(EMPTY_SECONDARY_ARRAY);
				}
			}
		}

		private class SecondaryListSelectionListener
			implements ListSelectionListener
		{
			public void valueChanged(ListSelectionEvent e)
			{
				SourceIdentifier selected = secondaryList.getSelectedValue();
				int selectedIndex = secondaryList.getSelectedIndex();
				if(selectedIndex != -1)
				{
					Rectangle selectRect = secondaryList.getCellBounds(selectedIndex, selectedIndex);
					if(selectRect != null)
					{
						secondaryList.scrollRectToVisible(selectRect);
					}
				}

				setSelectedSource(selected);
			}

		}

		private class SecondaryMouseListener
			extends MouseAdapter
		{

			public void mouseClicked(MouseEvent e)
			{
				if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() > 1)
				{
					Component component = secondaryList.getComponentAt(e.getPoint());
					if(component != null)
					{
						// double-clicked on actual content... it's already selected...
						openSelection();
					}
				}

			}
		}

		private class LastModifiedComparator implements Comparator<SourceIdentifier>
		{
			@Override
			public int compare(SourceIdentifier o1, SourceIdentifier o2) {
				if(o1 == o2)
				{
					return 0;
				}
				if(o1 == null)
				{
					return -1;
				}
				if(o2 == null)
				{
					return 1;
				}
				long t1 = logFileFactory.getDataFile(o1).lastModified();
				long t2 = logFileFactory.getDataFile(o2).lastModified();
				if(t1 == t2)
				{
					return 0;
				}
				if(t1 < t2)
				{
					return 1;
				}
				return -1;
			}
		}
	}
}
