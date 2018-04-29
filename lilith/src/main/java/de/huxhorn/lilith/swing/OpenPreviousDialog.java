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

package de.huxhorn.lilith.swing;

import de.huxhorn.lilith.DateTimeFormatters;
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.FileBufferFactory;
import de.huxhorn.lilith.engine.LogFileFactory;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.formatting.HumanReadable;
import de.huxhorn.sulky.swing.KeyStrokes;
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
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	private static final SourceIdentifierWrapper[] EMPTY_SECONDARY_ARRAY = new SourceIdentifierWrapper[]{};

	private final MainFrame mainFrame;
	private final OpenAction openAction;
	private final JTabbedPane tabbedPane;

	private OpenPreviousPanel<LoggingEvent> loggingPanel;
	private OpenPreviousPanel<AccessEvent> accessPanel;

	OpenPreviousDialog(MainFrame owner)
	{
		super(owner, "Open previous log…");
		this.mainFrame = owner;

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

	@Override
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
			SourceIdentifierWrapper selectedSource = panel.getSelectedSourceWrapper();
			openAction.setEnabled(selectedSource != null);
		}
	}


	private void openSelection()
	{
		Component comp = tabbedPane.getSelectedComponent();
		if(comp instanceof OpenPreviousPanel)
		{
			OpenPreviousPanel panel = (OpenPreviousPanel) comp;
			SourceIdentifierWrapper selectedSource = panel.getSelectedSourceWrapper();
			if(selectedSource != null)
			{
				if(panel.getEventType() == EventType.LOGGING)
				{
					mainFrame.openPreviousLogging(selectedSource.getSourceIdentifier());
				}
				else
				{
					mainFrame.openPreviousAccess(selectedSource.getSourceIdentifier());
				}
			}
		}
		setVisible(false);
	}

	private class TabChangeListener
		implements ChangeListener
	{
		@Override
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

		OpenAction()
		{
			super("Open");
			KeyStroke accelerator = LilithKeyStrokes.getKeyStroke(LilithKeyStrokes.ENTER);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		@Override
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

		CancelAction()
		{
			super("Cancel");
			KeyStroke accelerator = LilithKeyStrokes.getKeyStroke(LilithKeyStrokes.ESCAPE);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			OpenPreviousDialog.this.setVisible(false);
		}
	}

	private class OpenPreviousPanel<T extends Serializable>
		extends JPanel
	{
		private static final long serialVersionUID = 1635486188020609000L;

		private final DecimalFormat eventCountFormat;
		private final FileBufferFactory<T> fileBufferFactory;
		private final LogFileFactory logFileFactory;
		private final EventType eventType;
		private final JList<String> primaryList;
		private final JList<SourceIdentifierWrapper> secondaryList;
		private final JTextArea infoArea;

		private List<List<SourceIdentifierWrapper>> secondaries;
		private SourceIdentifierWrapper selectedSourceWrapper;

		OpenPreviousPanel(FileBufferFactory<T> fileBufferFactory, EventType eventType)
		{
			this.fileBufferFactory = fileBufferFactory;
			this.logFileFactory = fileBufferFactory.getLogFileFactory();
			this.eventType = eventType;
			eventCountFormat = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.US));

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
			SortedMap<String, List<SourceIdentifierWrapper>> inactiveMap = new TreeMap<>();
			for(SourceIdentifier current : inactiveLogs)
			{
				String primary = ViewActions.getPrimarySourceTitle(current.getIdentifier(), sourceNames, false);
				List<SourceIdentifierWrapper> sourceList = inactiveMap.get(primary);
				if(sourceList == null)
				{
					sourceList = new ArrayList<>(); // NOPMD - AvoidInstantiatingObjectsInLoops
					inactiveMap.put(primary, sourceList);
				}
				long numberOfEvents = logFileFactory.getNumberOfEvents(current);
				long sizeOnDisk = logFileFactory.getSizeOnDisk(current);
				File dataFile = logFileFactory.getDataFile(current);
				long lastModified = dataFile.lastModified();

				Buffer<?> buffer=fileBufferFactory.createBuffer(current);
				String applicationName=ViewActions.resolveApplicationName(buffer);

				sourceList.add(new SourceIdentifierWrapper(current, sizeOnDisk, lastModified, numberOfEvents, applicationName)); // NOPMD - AvoidInstantiatingObjectsInLoops
			}

			int primaryCount = inactiveMap.size();
			if(primaryCount > 0)
			{
				ArrayList<String> primaries = new ArrayList<>(primaryCount);
				secondaries = new ArrayList<>(primaryCount);
				for(Map.Entry<String, List<SourceIdentifierWrapper>> current : inactiveMap.entrySet())
				{
					primaries.add(current.getKey());
					List<SourceIdentifierWrapper> value = current.getValue();
					Collections.sort(value);
					secondaries.add(value);
				}

				primaryList.setListData(primaries.toArray(EMPTY_STRING_ARRAY));
				SourceIdentifierWrapper[] currentSecondary = EMPTY_SECONDARY_ARRAY;
				if(!secondaries.isEmpty())
				{
					List<SourceIdentifierWrapper> zero = secondaries.get(0);
					if(!zero.isEmpty())
					{
						currentSecondary = zero.toArray(EMPTY_SECONDARY_ARRAY);
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

		SourceIdentifierWrapper getSelectedSourceWrapper()
		{
			return selectedSourceWrapper;
		}

		String getPanelName()
		{
			return eventType.getTypeName();
		}

		EventType getEventType()
		{
			return eventType;
		}

		private void setSelectedSourceWrapper(SourceIdentifierWrapper selected)
		{
			if(logger.isDebugEnabled()) logger.debug("Selected source: {}", selected);
			this.selectedSourceWrapper = selected;
			initOpenAction();
			updateInfoArea();
		}

		private String getLogInfo(SourceIdentifierWrapper selectedSource) {
			if (selectedSource == null) {
				return "";
			}

			ApplicationPreferences applicationPreferences = mainFrame.getApplicationPreferences();
			Map<String, String> sourceNames = null;
			if (applicationPreferences != null) {
				sourceNames = applicationPreferences.getSourceNames();
			}
			StringBuilder result = new StringBuilder(200);
			SourceIdentifier sourceIdentifier = selectedSource.getSourceIdentifier();
			result.append(ViewActions.getPrimarySourceTitle(sourceIdentifier.getIdentifier(), sourceNames, true));
			String secondary = sourceIdentifier.getSecondaryIdentifier();
			if (secondary != null) {
				result.append(" - ").append(sourceIdentifier.getSecondaryIdentifier());
			}
			result.append('\n');

			String applicationName = selectedSource.getApplicationName();
			if(applicationName != null && !"".equals(applicationName))
			{
				result.append("Application: ").append(applicationName);
			}
			result.append("\nNumber of events: ")
				.append(eventCountFormat.format(selectedSource.getNumberOfEvents()))
				.append("\nSize: ")
				.append(HumanReadable.getHumanReadableSize(selectedSource.getSizeOnDisk(), true, false))
				.append("bytes\nTimestamp: ")
				.append(DateTimeFormatters.DATETIME_IN_SYSTEM_ZONE_SPACE.format(Instant.ofEpochMilli(selectedSource.getLastModified())));

			return result.toString();
		}

		void updateInfoArea()
		{
			infoArea.setText(getLogInfo(selectedSourceWrapper));
		}


		private class PrimaryListSelectionListener
			implements ListSelectionListener
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				JList source = (JList) e.getSource();
				int selectedIndex = source.getSelectedIndex();
				if(selectedIndex >= 0 && selectedIndex < secondaries.size())
				{
					List<SourceIdentifierWrapper> sources = secondaries.get(selectedIndex);
					secondaryList.setListData(sources.toArray(EMPTY_SECONDARY_ARRAY));
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
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				SourceIdentifierWrapper selected = secondaryList.getSelectedValue();
				int selectedIndex = secondaryList.getSelectedIndex();
				if(selectedIndex != -1)
				{
					Rectangle selectRect = secondaryList.getCellBounds(selectedIndex, selectedIndex);
					if(selectRect != null)
					{
						secondaryList.scrollRectToVisible(selectRect);
					}
				}

				setSelectedSourceWrapper(selected);
			}

		}

		private class SecondaryMouseListener
			extends MouseAdapter
		{

			@Override
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
	}

	private class SourceIdentifierWrapper
		implements Comparable<SourceIdentifierWrapper>
	{
		private final SourceIdentifier sourceIdentifier;
		private final long sizeOnDisk;
		private final long lastModified;
		private final long numberOfEvents;
		private final String applicationName;

		SourceIdentifierWrapper(SourceIdentifier sourceIdentifier, long sizeOnDisk, long lastModified, long numberOfEvents, String applicationName)
		{
			this.sourceIdentifier = sourceIdentifier;
			this.sizeOnDisk = sizeOnDisk;
			this.lastModified = lastModified;
			this.numberOfEvents = numberOfEvents;
			this.applicationName = applicationName;
		}

		public SourceIdentifier getSourceIdentifier()
		{
			return sourceIdentifier;
		}

		long getLastModified()
		{
			return lastModified;
		}

		long getSizeOnDisk()
		{
			return sizeOnDisk;
		}

		long getNumberOfEvents()
		{
			return numberOfEvents;
		}

		String getApplicationName()
		{
			return applicationName;
		}

		@SuppressWarnings("NullableProblems")
		@Override
		public int compareTo(SourceIdentifierWrapper other)
		{
			Objects.requireNonNull(other, "other must not be null!");
			return Long.compare(other.lastModified, lastModified);
		}

		@Override
		public String toString()
		{
			if(applicationName != null && !"".equals(applicationName))
			{
				return applicationName;
			}
			String secondary = sourceIdentifier.getSecondaryIdentifier();
			if(secondary != null)
			{
				return secondary;
			}
			return "";
		}
	}
}
