/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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

import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.engine.LogFileFactory;
import de.huxhorn.sulky.formatting.HumanReadable;
import de.huxhorn.sulky.swing.KeyStrokes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class OpenPreviousDialog
	extends JDialog
{
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

	public static final Object[] EMPTY_OBJECT_ARRAY = new Object[]{};
	private MainFrame mainFrame;
	private OpenAction openAction;
	private JTabbedPane tabbedPane;
	private OpenPreviousPanel loggingPanel;
	private OpenPreviousPanel accessPanel;

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
		loggingPanel = new OpenPreviousPanel(mainFrame.getLoggingFileFactory(), EventType.LOGGING);
		accessPanel = new OpenPreviousPanel(mainFrame.getAccessFileFactory(), EventType.ACCESS);
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
		public OpenAction()
		{
			super("Open");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke("ENTER");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
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
		public CancelAction()
		{
			super("Cancel");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke("ESCAPE");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			OpenPreviousDialog.this.setVisible(false);
		}
	}

	private class OpenPreviousPanel
		extends JPanel
	{
		private List<List<SourceIdentifier>> secondaries;
		private JList primaryList;
		private JList secondaryList;
		private JTextArea infoArea;
		private DecimalFormat eventCountFormat;
		private LogFileFactory fileFactory;
		private EventType eventType;
		private SourceIdentifier selectedSource;

		public OpenPreviousPanel(LogFileFactory fileFactory, EventType eventType)
		{
			this.fileFactory = fileFactory;
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
			primaryList = new JList();
			secondaryList = new JList();

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
			infoPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Log Informations"));
			add(infoPanel, BorderLayout.SOUTH);
		}

		public void initUI()
		{
			List<SourceIdentifier> inactives = mainFrame.collectInactiveLogs(fileFactory);
			SortedMap<String, List<SourceIdentifier>> inactiveMap = new TreeMap<String, List<SourceIdentifier>>();
			for(SourceIdentifier current : inactives)
			{
				String primary = mainFrame.getPrimarySourceTitle(current);
				List<SourceIdentifier> sourceList = inactiveMap.get(primary);
				if(sourceList == null)
				{
					sourceList = new ArrayList<SourceIdentifier>();
					inactiveMap.put(primary, sourceList);
				}
				sourceList.add(current);
			}

			int primaryCount = inactiveMap.size();
			if(primaryCount > 0)
			{
				ArrayList<String> primaries = new ArrayList<String>(primaryCount);
				secondaries = new ArrayList<List<SourceIdentifier>>(primaryCount);
				for(Map.Entry<String, List<SourceIdentifier>> current : inactiveMap.entrySet())
				{
					primaries.add(current.getKey());
					List<SourceIdentifier> value = current.getValue();
					Collections.reverse(value); // newest first
					secondaries.add(value);
				}
				primaryList.setListData(primaries.toArray());
				secondaryList.setListData(secondaries.toArray());
				primaryList.setSelectedIndex(0);
			}
			else
			{
				primaryList.setListData(EMPTY_OBJECT_ARRAY);
				secondaryList.setListData(EMPTY_OBJECT_ARRAY);
			}

		}


		private String getLogInfo(SourceIdentifier selectedSource)
		{
			if(selectedSource == null)
			{
				return "";
			}

			StringBuilder result = new StringBuilder();
			result.append(mainFrame.getPrimarySourceTitle(selectedSource));
			String secondary = selectedSource.getSecondaryIdentifier();
			if(secondary != null)
			{
				result.append(" - ").append(selectedSource.getSecondaryIdentifier());
			}
			result.append("\n");

			result.append("Number of events: ")
				.append(eventCountFormat.format(fileFactory.getNumberOfEvents(selectedSource))).append("\n");
			result.append("Size: ")
				.append(HumanReadable.getHumanReadableSize(fileFactory.getSizeOnDisk(selectedSource), true, false))
				.append("bytes");

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
					secondaryList.setListData(sources.toArray());
					secondaryList.setSelectedIndex(0);
				}
				else
				{
					secondaryList.setListData(EMPTY_OBJECT_ARRAY);
				}
			}
		}

		private class SecondaryListSelectionListener
			implements ListSelectionListener
		{

			public void valueChanged(ListSelectionEvent e)
			{
				SourceIdentifier selected = (SourceIdentifier) secondaryList.getSelectedValue();
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
	}
}
