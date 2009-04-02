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
package de.huxhorn.lilith.swing;

import de.huxhorn.lilith.buffers.FilteringBuffer;
import de.huxhorn.lilith.buffers.SoftReferenceCachingBuffer;
import de.huxhorn.lilith.conditions.EventContainsCondition;
import de.huxhorn.lilith.conditions.GroovyCondition;
import de.huxhorn.lilith.conditions.LoggerEqualsCondition;
import de.huxhorn.lilith.conditions.LoggerStartsWithCondition;
import de.huxhorn.lilith.conditions.MessageContainsCondition;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.lilith.swing.callables.CallableMetaData;
import de.huxhorn.lilith.swing.callables.FindNextCallable;
import de.huxhorn.lilith.swing.callables.FindPreviousCallable;
import de.huxhorn.lilith.swing.linklistener.StackTraceElementLinkListener;
import de.huxhorn.lilith.swing.preferences.SavedCondition;
import de.huxhorn.lilith.swing.table.EventWrapperViewTable;
import de.huxhorn.lilith.swing.table.model.EventWrapperTableModel;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.DisposeOperation;
import de.huxhorn.sulky.codec.filebuffer.CodecFileBuffer;
import de.huxhorn.sulky.conditions.And;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.conditions.Not;
import de.huxhorn.sulky.formatting.HumanReadable;
import de.huxhorn.sulky.swing.KeyStrokes;
import de.huxhorn.sulky.tasks.ProgressingCallable;
import de.huxhorn.sulky.tasks.Task;
import de.huxhorn.sulky.tasks.TaskListener;
import de.huxhorn.sulky.tasks.TaskManager;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.simple.FSScrollPane;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;
import org.xhtmlrenderer.swing.LinkListener;
import org.xhtmlrenderer.swing.SelectionHighlighter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public abstract class EventWrapperViewPanel<T extends Serializable>
	extends JPanel
	implements DisposeOperation
{
	public static final String STATE_PROPERTY = "state";
	public static final String FILTER_CONDITION_PROPERTY = "filterCondition";
	public static final String EVENT_SOURCE_PROPERTY = "eventSource";
	public static final String SCROLLING_TO_BOTTOM_PROPERTY = "scrollingToBottom";
	public static final String PAUSED_PROPERTY = "paused";
	public static final String SELECTED_EVENT_PROPERTY = "selectedEvent";

	private final Logger logger = LoggerFactory.getLogger(EventWrapperViewPanel.class);

	private EventSource<T> eventSource;
	private LoggingViewState state;
	private MainFrame mainFrame;
	private boolean showingFilters;
	private Condition filterCondition;
	private TaskManager<Long> taskManager;

	private EventWrapperViewTable<T> table;
	private EventWrapperTableModel<T> tableModel;

	private FindNextAction findNextAction;
	private FindPreviousAction findPrevAction;
	private CloseFindAction closeFindAction;

	private JButton findPrevButton;
	private JButton findNextButton;

	private JToolBar findPanel;
	private JToggleButton findNotButton;
	private JComboBox findTypeCombo;
	private JTextField findTextField;
	private JLabel statusLabel;
	private JScrollBar verticalLogScrollbar;

	private StatusTableModelListener tableModelListener;
	private FocusTraversalPolicy focusTraversalPolicy;
	private MatteBorder focusedBorder;
	private MatteBorder unfocusedBorder;
	private DecimalFormat eventCountFormat;
	private FindResultListener findResultListener;
	private static final String GROOVY_IDENTIFIER = "#groovy#";
	private static final String SAVED_CONDITION_IDENTIFIER = "#condition#";
	private static final Color ERROR_COLOR = new Color(0xffaaaa);
	protected JMenu sendToMenuItem;

	private XHTMLPanel messagePane;
	private XhtmlNamespaceHandler xhtmlNamespaceHandler;
	private EventWrapper<T> selectedEvent;
	private SelectionHighlighter messagePaneCaret;
	private SelectionHighlighter.CopyAction copyAction;


	public EventWrapperViewPanel(MainFrame mainFrame, EventSource<T> eventSource)
	{
		super(true);
		eventCountFormat = new DecimalFormat("#,###");
		this.taskManager = mainFrame.getLongWorkManager();
		findResultListener = new FindResultListener();
		taskManager.addTaskListener(findResultListener);
		this.mainFrame = mainFrame;
		this.eventSource = eventSource;
		showingFilters = false;

		tableModelListener = new StatusTableModelListener();
		initUi();
	}

	private void initUi()
	{
		Insets borderInsets = new Insets(2, 2, 2, 2);
		focusedBorder = new MatteBorder(borderInsets, Color.YELLOW);
		unfocusedBorder = new MatteBorder(borderInsets, Color.WHITE);

		SoftReferenceCachingBuffer<EventWrapper<T>> cachedBuffer = createCachedBuffer(eventSource.getBuffer());
		tableModel = createTableModel(cachedBuffer);
		tableModel.addTableModelListener(tableModelListener);
		table = createTable(tableModel);
		table.getSelectionModel().addListSelectionListener(new TableRowSelectionListener());
		JScrollPane tableScrollPane = new JScrollPane(table);
		verticalLogScrollbar = tableScrollPane.getVerticalScrollBar();

		messagePane = new XHTMLPanel();
		messagePaneCaret = new SelectionHighlighter();
		messagePaneCaret.install(messagePane);

		copyAction = new SelectionHighlighter.CopyAction();
		copyAction.install(messagePaneCaret);

		// TODO: Copy action calling caret.copy()
		messagePane.addMouseListener(new EventViewMouseListener());


		messagePane.addFocusListener(new MessageFocusListener());
		messagePane.setBorder(unfocusedBorder);

		{
			List mouseTrackingList = messagePane.getMouseTrackingListeners();
			if(mouseTrackingList != null)
			{
				for(Object o : mouseTrackingList)
				{
					if(logger.isDebugEnabled()) logger.debug("Before MTL {}", o);
					if(o instanceof LinkListener)
					{
						messagePane.removeMouseTrackingListener((LinkListener) o);
					}
				}
			}
		}

		messagePane.addMouseTrackingListener(new StackTraceElementLinkListener(mainFrame));

		xhtmlNamespaceHandler = new XhtmlNamespaceHandler();
		FSScrollPane messageScrollPane = new FSScrollPane(messagePane);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, messageScrollPane);
		PropertyChangeListener splitPaneListener = new SplitPaneListener();
		splitPane.addPropertyChangeListener(splitPaneListener);
		splitPane.setResizeWeight(0.5); // divide space equally in case of resize.
		splitPane.setOneTouchExpandable(true);
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
		ScrollbarChangeListner scrollBarChangeListener = new ScrollbarChangeListner();
		verticalLogScrollbar.getModel().addChangeListener(scrollBarChangeListener);


		table.addMouseListener(new TableMouseListener());
		table.addPropertyChangeListener(new EventWrapperViewChangeListener());

		focusTraversalPolicy = new MyFocusTraversalPolicy();

		initFindPanel();

		ReplaceFilterAction replaceFilterAction = new ReplaceFilterAction();

		FindTextFieldListener findTextFieldListener = new FindTextFieldListener();
		findTextField.addActionListener(findTextFieldListener);
		findTextField.getDocument().addDocumentListener(findTextFieldListener);
		findTextField.setBackground(Color.WHITE);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		JPanel statusPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		bottomPanel.add(findPanel, BorderLayout.CENTER);
		bottomPanel.add(statusPanel, BorderLayout.SOUTH);

		statusLabel = new JLabel();

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 5, 0, 0);

		statusPanel.add(statusLabel, gbc);

		add(bottomPanel, BorderLayout.SOUTH);

		setScrollingToBottom(false);
		setPaused(false);
		setFocusTraversalPolicy(focusTraversalPolicy);
		setFocusCycleRoot(true);
		setFocusTraversalPolicyProvider(true);

		if(logger.isDebugEnabled()) logger.debug("table.isFocusCycleRoot()={}", table.isFocusCycleRoot());
		if(logger.isDebugEnabled())
		{
			logger.debug("table.isFocusTraversalPolicyProvider()={}", table.isFocusTraversalPolicyProvider());
		}
		table.setFocusTraversalPolicy(focusTraversalPolicy);
		table.setFocusCycleRoot(true);

		// setting table traversal back to "normal"...
		table
			.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
		table
			.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));

		updateStatusText();

		KeyStrokes.registerCommand(this, findNextAction, "FIND_NEXT_ACTION");
		KeyStrokes.registerCommand(this, findPrevAction, "FIND_PREV_ACTION");
		KeyStrokes.registerCommand(this, closeFindAction, "CLOSE_FIND_ACTION");
		KeyStrokes.registerCommand(findTextField, replaceFilterAction, "REPLACE_FILTER_ACTION");
	}

	private void initFindPanel()
	{
		findPanel = new JToolBar(SwingConstants.HORIZONTAL);
		findPanel.setFloatable(false);
		findPanel.setFocusTraversalPolicy(focusTraversalPolicy);
		findPanel.setFocusCycleRoot(true);

		closeFindAction = new CloseFindAction();
		JButton findViewButton = new JButton(closeFindAction);
		findPanel.add(findViewButton);
		findPanel.addSeparator();
		findPanel.add(new JLabel("Find: "));

		ActionListener findTypeModifiedListener = new FindTypeSelectionActionListener();
		findTypeCombo = new JComboBox();
		findTypeCombo.addActionListener(findTypeModifiedListener);
		findNotButton = new JToggleButton("!");
		findNotButton.addActionListener(findTypeModifiedListener);
		findNotButton.setToolTipText("Not - inverts condition");
		findTextField = new JTextField();
		findTextField.setColumns(15);
		findPanel.add(findNotButton);
		findPanel.add(findTypeCombo);
		findPanel.add(findTextField);

		findPrevAction = new FindPreviousAction();
		findPrevButton = new JButton(findPrevAction);
		findPanel.add(findPrevButton);

		findNextAction = new FindNextAction();
		findNextButton = new JButton(findNextAction);
		findPanel.add(findNextButton);
		enableFindComponents(true);
	}

	public EventWrapperViewTable<T> getTable()
	{
		return table;
	}

	public LoggingViewState getState()
	{
		if(!EventQueue.isDispatchThread())
		{
			if(logger.isWarnEnabled())
			{
				//noinspection ThrowableInstanceNeverThrown
				logger.warn("!DispatchThread - getState: state=" + state, new Throwable());
			}
		}
		return state;
	}

	public MainFrame getMainFrame()
	{
		return mainFrame;
	}

	public void setState(LoggingViewState state)
	{
		Object oldValue = this.state;
		this.state = state;
		Object newValue = this.state;
		firePropertyChange(STATE_PROPERTY, oldValue, newValue);
	}

	public boolean isShowingFilters()
	{
		return showingFilters;
	}

	public void setShowingFilters(boolean showingFilters)
	{
		this.showingFilters = showingFilters;
		if(showingFilters)
		{
			initTypeCombo();
			// select correct type in combo
			Condition condition = getFilterCondition();
			boolean not = false;
			if(condition instanceof Not)
			{
				Not notCondition = (Not) condition;
				not = true;
				condition = notCondition.getCondition();
			}
			if(condition != null)
			{
				String conditionName = null;
				if(condition instanceof EventContainsCondition)
				{
					conditionName = EVENT_CONTAINS_CONDITION;
				}
				else if(condition instanceof MessageContainsCondition)
				{
					conditionName = MESSAGE_CONTAINS_CONDITION;
				}
				else if(condition instanceof LoggerStartsWithCondition)
				{
					conditionName = LOGGER_STARTS_WITH_CONDITION;
				}
				else if(condition instanceof LoggerEqualsCondition)
				{
					conditionName = LOGGER_EQUALS_CONDITION;
				}
				else if(condition instanceof GroovyCondition)
				{
					GroovyCondition groovyCondition = (GroovyCondition) condition;
					String scriptFileName = groovyCondition.getScriptFileName();
					if(scriptFileName != null)
					{
						File scriptFile = new File(scriptFileName);
						conditionName = scriptFile.getName();
					}
				}
				if(conditionName != null)
				{
					findTypeCombo.setSelectedItem(conditionName);
				}
			}
			findNotButton.setSelected(not);
		}
		findPanel.setVisible(showingFilters);
		if(showingFilters)
		{
			findTextField.requestFocusInWindow();
			findTextField.selectAll();
			applyFilter();
		}
		scrollToEvent();
	}

	/**
	 * scrolls to bottom if it is enabled. Otherwise makes sure that an event is selected if available.
	 */
	public void scrollToEvent()
	{
		if(table.isScrollingToBottom())
		{
			SwingUtilities.invokeLater(new ScrollToBottomRunnable());
		}
		else if(table.getSelectedRow() < 0)
		{
			SwingUtilities.invokeLater(new SelectFirstEventRunnable());
		}
	}

	private class ScrollToBottomRunnable
		implements Runnable
	{
		public void run()
		{
			table.scrollToBottom();
		}
	}

	private class SelectFirstEventRunnable
		implements Runnable
	{
		public void run()
		{
			table.scrollToFirst();
		}
	}

	public void validate()
	{
		super.validate();
		if(logger.isDebugEnabled()) logger.debug("Validate");
	}

	private SoftReferenceCachingBuffer<EventWrapper<T>> createCachedBuffer(Buffer<EventWrapper<T>> buffer)
	{
		return new SoftReferenceCachingBuffer<EventWrapper<T>>(buffer);
	}

	void setEventSource(EventSource<T> eventSource)
	{
		EventSource oldValue = this.eventSource;
		this.eventSource = eventSource;
		SoftReferenceCachingBuffer<EventWrapper<T>> cachedBuffer = createCachedBuffer(eventSource.getBuffer());
		tableModel.setBuffer(cachedBuffer);
		EventSource newValue = this.eventSource;
		if(logger.isDebugEnabled()) logger.debug("EventSource\nOld: {}\nNew: {}", oldValue, newValue);
		firePropertyChange(EVENT_SOURCE_PROPERTY, oldValue, newValue);
	}

	public EventSource<T> getEventSource()
	{
		return eventSource;
	}

	public void setScrollingToBottom(boolean scrollingToBottom)
	{
		Object oldValue = table.isScrollingToBottom();
		table.setScrollingToBottom(scrollingToBottom);
		Object newValue = table.isScrollingToBottom();
		firePropertyChange(SCROLLING_TO_BOTTOM_PROPERTY, oldValue, newValue);
	}

	public boolean isScrollingToBottom()
	{
		return table.isScrollingToBottom();
	}

	public boolean isPaused()
	{
		return tableModel.isPaused();
	}

	public void setPaused(boolean paused)
	{
		Object oldValue = tableModel.isPaused();
		tableModel.setPaused(paused);
		Object newValue = tableModel.isPaused();
		firePropertyChange(PAUSED_PROPERTY, oldValue, newValue);
	}

	public ViewContainer<T> resolveContainer()
	{
		Container parent = getParent();
		while(parent != null && !(parent instanceof ViewContainer))
		{
			parent = parent.getParent();
		}
		// not 100% typesafe
		//noinspection unchecked
		return (ViewContainer<T>) parent;
	}

	public void addNotify()
	{
		super.addNotify();
		if(logger.isDebugEnabled()) logger.debug("addNotify - parent: {}", getParent());
		findPanel.setVisible(isShowingFilters());
	}

	public void removeNotify()
	{
		super.removeNotify();
		if(logger.isDebugEnabled()) logger.debug("removeNotify");
	}


	protected abstract EventWrapperTableModel<T> createTableModel(Buffer<EventWrapper<T>> buffer);

	protected abstract EventWrapperViewTable<T> createTable(EventWrapperTableModel<T> tableModel);


	private static final String EVENT_CONTAINS_CONDITION = "event.contains";
	private static final String MESSAGE_CONTAINS_CONDITION = "message.contains";
	private static final String LOGGER_STARTS_WITH_CONDITION = "logger.startsWith";
	private static final String LOGGER_EQUALS_CONDITION = "logger.equals";
	private static final String[] DEFAULT_CONDITIONS = new String[]{
		EVENT_CONTAINS_CONDITION,
		MESSAGE_CONTAINS_CONDITION,
		LOGGER_STARTS_WITH_CONDITION,
		LOGGER_EQUALS_CONDITION};

	private void initTypeCombo()
	{
		Vector<String> itemsVector = new Vector<String>();

		itemsVector.addAll(Arrays.asList(DEFAULT_CONDITIONS));

		String[] groovyConditions = mainFrame.getAllConditionScriptFiles();
		if(groovyConditions != null)
		{
			itemsVector.addAll(Arrays.asList(groovyConditions));
		}

		ComboBoxModel model = new DefaultComboBoxModel(itemsVector);
		findTypeCombo.setModel(model);
	}

	public void dispose()
	{
		tableModel.dispose();
		taskManager.removeTaskListener(findResultListener);
	}

	public boolean isDisposed()
	{
		return tableModel.isDisposed();
	}

	public void resetFind()
	{
		findTextField.setText("");
		setFilterCondition(null);
	}

	protected void setSelectedEvent(EventWrapper<T> selectedEvent)
	{
		Object oldValue = this.selectedEvent;
		this.selectedEvent = selectedEvent;
		Object newValue = this.selectedEvent;
		firePropertyChange(SELECTED_EVENT_PROPERTY, oldValue, newValue);
	}

	public EventWrapper<T> getSelectedEvent()
	{
		return selectedEvent;
	}

	class MyFocusTraversalPolicy
		extends FocusTraversalPolicy
	{
		public Component getComponentAfter(Container aContainer, Component aComponent)
		{
			if(aComponent.equals(table))
			{
				return messagePane;
			}
			if(aComponent.equals(messagePane))
			{
				if(isShowingFilters())
				{
					return findNotButton;
				}
				return table;
			}
			if(aComponent.equals(findNotButton))
			{
				return findTypeCombo;
			}
			if(aComponent.equals(findTypeCombo))
			{
				return findTextField;
			}
			if(aComponent.equals(findTextField))
			{
				if(findPrevButton.isEnabled())
				{
					return findPrevButton;
				}
				if(findNextButton.isEnabled())
				{
					return findNextButton;
				}
				return table;
			}
			if(aComponent.equals(findPrevButton))
			{
				if(findNextButton.isEnabled())
				{
					return findNextButton;
				}
				return table;
			}
			if(aComponent.equals(findNextButton))
			{
				return table;
			}
			if(aComponent.equals(findPanel))
			{
				return table;
			}
			// I guess focus was inside table so focus component after table.
			if(logger.isInfoEnabled())
			{
				logger
					.info("Moving focus forward to messagePane since it was not explicitly handled. (component={})", aComponent);
			}
			return messagePane;
		}

		public Component getComponentBefore(Container aContainer, Component aComponent)
		{
			if(aComponent.equals(messagePane))
			{
				return table;
			}
			if(aComponent.equals(findNotButton))
			{
				return messagePane;
			}
			if(aComponent.equals(findTypeCombo))
			{
				return findNotButton;
			}
			if(aComponent.equals(findTextField))
			{
				return findTypeCombo;
			}
			if(aComponent.equals(findPrevButton))
			{
				return findTextField;
			}
			if(aComponent.equals(findNextButton))
			{
				if(findPrevButton.isEnabled())
				{
					return findPrevButton;
				}
				return findTextField;
			}

			if(aComponent.equals(findPanel))
			{
				return messagePane;
			}

			// table
			if(isShowingFilters())
			{
				if(findNextButton.isEnabled())
				{
					return findNextButton;
				}
				if(findPrevButton.isEnabled())
				{
					return findPrevButton;
				}
				return findTextField;
			}
			// I guess focus was inside table so focus component before table.
			if(logger.isInfoEnabled())
			{
				logger
					.info("Moving focus backward to messagePane since it was not explicitly handled. (component={})", aComponent);
			}
			return messagePane;
		}

		public Component getFirstComponent(Container aContainer)
		{
			return table;
		}

		public Component getLastComponent(Container aContainer)
		{
			return messagePane;
		}

		public Component getDefaultComponent(Container aContainer)
		{
			return table;
		}
	}

	public EventWrapperTableModel<T> getTableModel()
	{
		return tableModel;
	}


	protected void initMessage(EventWrapper wrapper)
	{
		String message = mainFrame.createMessage(wrapper);
		URL messageViewRootUrl = mainFrame.getApplicationPreferences().getDetailsViewRootUrl();
		try
		{
			messagePane.setDocumentFromString(message, messageViewRootUrl.toExternalForm(), xhtmlNamespaceHandler);
		}
		catch(Throwable t)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while setting message!", t);
			writeErrorMessage(message);
		}
	}

	protected void resetMessage()
	{
		String message = "<html><body>No event selected.</body></html>";
		URL messageViewRootUrl = mainFrame.getApplicationPreferences().getDetailsViewRootUrl();
		try
		{
			messagePane.setDocumentFromString(message, messageViewRootUrl.toExternalForm(), xhtmlNamespaceHandler);
		}
		catch(Throwable t)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while setting message!", t);
			writeErrorMessage(message);
		}
	}

	private void writeErrorMessage(String message)
	{
		File appPath = mainFrame.getApplicationPreferences().getStartupApplicationPath();
		File errorPath = new File(appPath, "errors");
		if(errorPath.mkdirs())
		{
			if(logger.isDebugEnabled()) logger.debug("Created errors directory '{}'.", errorPath);
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSSZ");
		String filename = format.format(new Date());
		File errorFile = new File(errorPath, filename);
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(errorFile);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			osw.append(message);
			osw.flush();
			if(logger.isInfoEnabled()) logger.info("Faulty message written to '{}'.", errorFile.getAbsolutePath());
		}
		catch(Throwable e)
		{
			if(logger.isWarnEnabled())
			{
				logger.warn("Exception while writing faulty message to '" + errorFile.getAbsolutePath() + "'!", e);
			}
		}
		finally
		{
			IOUtils.closeQuietly(fos);
		}

	}

	/**
	 * Does also disable scrollingToBottom and selects the clicked row, if any.
	 *
	 * @param p the point
	 * @return the EventWrapper at the given point.
	 */
	private EventWrapper<T> getEventWrapper(Point p)
	{
		int row = table.rowAtPoint(p);
		if(-1 == row)
		{
			return null;
		}
		// row = table.convertRowIndexToModel(row);
		// 1.6, not needed
		table.setScrollingToBottom(false);
		table.selectRow(row);
		return tableModel.getValueAt(row);
	}

	private void applyFilter()
	{
		String text = findTextField.getText();
		Condition condition;
		//boolean error=false;
		String errorMessage = null;
		if(text == null)
		{
			text = "";
		}
		if(text.startsWith(GROOVY_IDENTIFIER))
		{
			String scriptName = text.substring(GROOVY_IDENTIFIER.length());

			int idx = scriptName.indexOf('#');
			if(idx > -1)
			{
				if(idx + 1 < scriptName.length())
				{
					text = scriptName.substring(idx + 1);
				}
				else
				{
					text = "";
				}
				scriptName = scriptName.substring(0, idx);
			}
			else
			{
				text = "";
			}
			if(logger.isDebugEnabled())
			{
				logger.debug("GroovyCondition with scriptName '{}' and searchString '{}'", scriptName, text);
			}
			File resolvedScriptFile = mainFrame.resolveConditionScriptFile(scriptName);
			if(resolvedScriptFile != null)
			{
				// there is a file...
				condition = new GroovyCondition(resolvedScriptFile.getAbsolutePath(), text);
			}
			else
			{
				errorMessage = "Couldn't find groovy script '" + scriptName + "'.";
				condition = null;
			}
		}
		else if(text.startsWith(SAVED_CONDITION_IDENTIFIER))
		{
			String conditionName = text.substring(SAVED_CONDITION_IDENTIFIER.length());
			SavedCondition savedCondition = mainFrame.getApplicationPreferences().resolveSavedCondition(conditionName);
			if(savedCondition != null)
			{
				condition = savedCondition.getCondition();
			}
			else
			{
				errorMessage = "Couldn't find saved condition '" + conditionName + "'.";
				condition = null;
			}
		}
		else
		{
			// create condition matching the selected type
			String selectedType = (String) findTypeCombo.getSelectedItem();
			if(EVENT_CONTAINS_CONDITION.equals(selectedType))
			{
				condition = new EventContainsCondition(text);
			}
			else if(MESSAGE_CONTAINS_CONDITION.equals(selectedType))
			{
				condition = new MessageContainsCondition(text);
			}
			else if(LOGGER_STARTS_WITH_CONDITION.equals(selectedType))
			{
				condition = new LoggerStartsWithCondition(text);
			}
			else if(LOGGER_EQUALS_CONDITION.equals(selectedType))
			{
				condition = new LoggerEqualsCondition(text);
			}
			else
			{
				// we assume a groovy condition...
				File resolvedScriptFile = mainFrame.resolveConditionScriptFile(selectedType);
				if(resolvedScriptFile != null)
				{
					// there is a file...
					condition = new GroovyCondition(resolvedScriptFile.getAbsolutePath(), text);
				}
				else
				{
					condition = null;
				}
			}
		}
		if(errorMessage != null)
		{
			// problem with condition
			findTextField.setBackground(ERROR_COLOR);
			findTextField.setToolTipText(errorMessage);
		}
		else
		{
			findTextField.setBackground(Color.WHITE);
			findTextField.setToolTipText(null);
		}
		if(condition != null)
		{
			// wrap in Not if not is selected.
			if(findNotButton.isSelected())
			{
				condition = new Not(condition);
			}
		}
		if(logger.isDebugEnabled()) logger.debug("Setting condition: {}", condition);
		setFilterCondition(condition);
	}

	public void setFilterCondition(Condition condition)
	{
		Object old = this.filterCondition;
		this.filterCondition = condition;
		table.setFilterCondition(filterCondition);
		firePropertyChange(FILTER_CONDITION_PROPERTY, old, condition);
	}

	public Condition getFilterCondition()
	{
		return filterCondition;
	}

	public void clear()
	{
		tableModel.clear();
		table.requestFocusInWindow();
	}

	public void findPrevious(int currentRow, Condition condition)
	{
		if(condition != null)
		{
			ProgressingCallable<Long> callable = new FindPreviousCallable<T>(tableModel, currentRow, condition);
			executeFind(callable, "Find previous", currentRow, condition);
		}
	}

	public void findNext(int currentRow, Condition condition)
	{
		if(condition != null)
		{
			ProgressingCallable<Long> callable = new FindNextCallable<T>(tableModel, currentRow, condition);
			executeFind(callable, "Find next", currentRow, condition);
		}
	}

	public void setSelectedRow(int row)
	{
		if(row > -1)
		{
			if(isScrollingToBottom())
			{
				setScrollingToBottom(false);
			}
			table.selectRow(row);
		}
	}

	public int getSelectedRow()
	{
		ListSelectionModel selectionModel = table.getSelectionModel();
		return selectionModel.getLeadSelectionIndex();
	}

	private void updateStatusText()
	{
		int eventCount = tableModel.getRowCount();
		StringBuilder statusText = new StringBuilder();
		if(eventCount < 1)
		{
			statusText.append("No events.");
		}
		else
		{
			if(eventCount == 1)
			{
				statusText.append("One event.");
			}
			else
			{
				statusText.append(eventCountFormat.format(eventCount)).append(" events.");
			}
			long size = getSizeOnDisk();
			if(size > 0)
			{
				statusText.append("   Size on disk: ")
					.append(HumanReadable.getHumanReadableSize(size, true, false)).append("bytes");

				statusText.append("   Average event: ")
					.append(HumanReadable.getHumanReadableSize(size / eventCount, true, false)).append("bytes");
			}
		}

		statusLabel.setText(statusText.toString());
	}

	protected long getSizeOnDisk()
	{
		Buffer buffer = getEventSource().getBuffer();
		if(buffer instanceof CodecFileBuffer)
		{
			CodecFileBuffer cfb = (CodecFileBuffer) buffer;
			return cfb.getDataFile().length();
		}
		return -1;
	}

	public Buffer<EventWrapper<T>> getSourceBuffer()
	{
		Buffer<EventWrapper<T>> buffer = eventSource.getBuffer();

		if(buffer instanceof FilteringBuffer)
		{
			FilteringBuffer<EventWrapper<T>> filteringBuffer = (FilteringBuffer<EventWrapper<T>>) buffer;
			return filteringBuffer.getSourceBuffer();
		}
		return buffer;
	}

	public Condition getBufferCondition()
	{
		Buffer<EventWrapper<T>> buffer = eventSource.getBuffer();

		if(buffer instanceof FilteringBuffer)
		{
			FilteringBuffer<EventWrapper<T>> filteringBuffer = (FilteringBuffer<EventWrapper<T>>) buffer;
			return filteringBuffer.getCondition();
		}
		return null;
	}

	public void copySelection()
	{
		copyAction.actionPerformed(null);
	}

	/**
	 * @return the combination of the tables filter condition and the condition of a filtered buffer.
	 */
	public Condition getCombinedCondition()
	{
		Condition previousCondition = getBufferCondition();

		Condition currentFilter = table.getFilterCondition();

		if(previousCondition == null)
		{
			return currentFilter;
		}

		try
		{
			// clone the previous condition so we don't change it while active
			Condition previousClone = previousCondition.clone();
			if(currentFilter == null)
			{
				return previousClone;
			}
			And and;
			if(previousClone instanceof And)
			{
				and = (And) previousClone;
			}
			else
			{
				and = new And();
				ArrayList<Condition> conditions = new ArrayList<Condition>();
				conditions.add(previousClone);
				and.setConditions(conditions);
			}
			List<Condition> conditions = and.getConditions();
			if(conditions == null)
			{
				conditions = new ArrayList<Condition>();
			}
			if(!conditions.contains(currentFilter))
			{
				// don't add duplicates
				conditions.add(currentFilter);
			}
			if(conditions.size() > 1)
			{
				if(logger.isInfoEnabled()) logger.info("Setting and-conditions: {}", conditions);
				and.setConditions(conditions);
				return and;
			}
			else
			{
				return currentFilter;
			}
		}
		catch(CloneNotSupportedException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while cloning!", ex);
		}
		return null;
	}

	private void createFilteredView()
	{
		ViewContainer<T> container = resolveContainer();
		if(container != null)
		{
			container.addFilteredView(this);
		}
	}

	/**
	 * If the currently selected event is in a filtered tab, the same event is displayed in the
	 * unfiltered view.
	 */
	public void showUnfilteredEvent()
	{
		int row = getSelectedRow();
		if(row >= 0)
		{
			ViewContainer<T> container = resolveContainer();
			if(container != null)
			{

				Buffer<EventWrapper<T>> buffer = eventSource.getBuffer();
				if(buffer instanceof FilteringBuffer)
				{
					FilteringBuffer<EventWrapper<T>> filteringBuffer = (FilteringBuffer<EventWrapper<T>>) buffer;
					long unfilteredRow = filteringBuffer.getSourceIndex(row);
					if(unfilteredRow >= 0)
					{
						if(logger.isInfoEnabled())
						{
							logger.info("Show unfiltered event {} for filtered event {}...", unfilteredRow, row);
						}
						EventWrapperViewPanel<T> defaultView = container.getDefaultView();
						container.showDefaultView();
						defaultView.setSelectedRow((int) unfilteredRow);
					}
				}
			}
		}
	}

	private class TableMouseListener
		implements MouseListener
	{
		public TableMouseListener()
		{
		}

		public void mouseClicked(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
			else if(evt.getClickCount() > 1 && evt.getButton() == MouseEvent.BUTTON1)
			{
				if((evt.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0)
				{
					Point p = evt.getPoint();
					EventWrapper<T> wrapper = getEventWrapper(p);
					if(wrapper != null)
					{
						T event = wrapper.getEvent();
						if(event instanceof LoggingEvent)
						{
							LoggingEvent loggingEvent = (LoggingEvent) event;
							ExtendedStackTraceElement[] callStack = loggingEvent.getCallStack();
							if(callStack != null && callStack.length > 0)
							{
								mainFrame.goToSource(callStack[0].getStackTraceElement());
							}
						}
					}
				}
				else
				{
					showUnfilteredEvent();
				}
			}
		}


		private void showPopup(MouseEvent evt)
		{
			Point p = evt.getPoint();
			EventWrapper<T> wrapper = getEventWrapper(p); // To ensure that the event below the mouse is selected.
			if(logger.isDebugEnabled()) logger.debug("Show popup at {} for event {}.", p, wrapper);
			mainFrame.showPopup(table, p);
		}

		public void mousePressed(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mouseReleased(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mouseEntered(MouseEvent e)
		{
		}

		public void mouseExited(MouseEvent e)
		{
		}

	}

	private class EventViewMouseListener
		implements MouseListener
	{
		public EventViewMouseListener()
		{
		}

		private void showPopup(MouseEvent evt)
		{
			Point p = evt.getPoint();
			mainFrame.showPopup(messagePane, p);
		}

		public void mouseClicked(MouseEvent evt)
		{
			messagePane.requestFocusInWindow();
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mousePressed(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mouseReleased(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mouseEntered(MouseEvent e)
		{
		}

		public void mouseExited(MouseEvent e)
		{
		}

	}

	private class TableRowSelectionListener
		implements ListSelectionListener
	{
		private final Logger logger = LoggerFactory.getLogger(TableRowSelectionListener.class);

		public void valueChanged(ListSelectionEvent e)
		{
			int row = getSelectedRow();
			if(logger.isDebugEnabled()) logger.debug("Selected row: {}.", row);

			if(row >= 0)
			{
				EventWrapper<T> event = tableModel.getValueAt(row);
				setSelectedEvent(event);
				initMessage(event);
			}
			else
			{
				setSelectedEvent(null);
				resetMessage();
			}
		}
	}

	/**
	 * Disables "Scroll to Bottom" in case of adjusting, i.e. if the scrollbar is dragged.
	 */
	private class ScrollbarChangeListner
		implements ChangeListener
	{
		private final Logger logger = LoggerFactory.getLogger(ScrollbarChangeListner.class);

		public void stateChanged(ChangeEvent evt)
		{
			if(logger.isDebugEnabled()) logger.debug("changeEvent: {}", evt);

			if(isScrollingToBottom())
			{
				if(verticalLogScrollbar.getModel().getValueIsAdjusting())
				{
					setScrollingToBottom(false);
				}
			}
		}
	}

	public void focusTable()
	{
		table.requestFocusInWindow();
	}

	public void focusMessagePane()
	{
		messagePane.requestFocusInWindow();
	}

	class EventWrapperViewChangeListener
		implements PropertyChangeListener
	{
		final Logger logger = LoggerFactory.getLogger(EventWrapperViewChangeListener.class);

		public void propertyChange(PropertyChangeEvent event)
		{
			String propertyName = event.getPropertyName();
			if(EventWrapperViewTable.SCROLLING_TO_BOTTOM_PROPERTY.equals(propertyName))
			{
				Object oldValue = event.getOldValue();
				Object newValue = event.getNewValue();
				firePropertyChange(SCROLLING_TO_BOTTOM_PROPERTY, oldValue, newValue);
			}
			else if(EventWrapperViewTable.FILTER_CONDITION_PROPERTY.equals(propertyName))
			{
				if(null == event.getNewValue())
				{
					findPrevAction.setEnabled(false);
					findNextAction.setEnabled(false);
				}
				else
				{
					findPrevAction.setEnabled(true);
					findNextAction.setEnabled(true);
				}
			}
		}
	}

	private class ReplaceFilterAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 3876315232050114189L;

		public ReplaceFilterAction()
		{
			super();
			putValue(Action.SHORT_DESCRIPTION, "Replace filter.");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke("shift ENTER");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			if(logger.isInfoEnabled()) logger.info("Replace filter.");
			ViewContainer<T> container = resolveContainer();
			if(container != null)
			{
				container.replaceFilteredView(EventWrapperViewPanel.this);
			}
		}
	}

	private class FindTextFieldListener
		implements ActionListener, DocumentListener
	{

		public void actionPerformed(ActionEvent e)
		{
			applyFilter();
			if(logger.isDebugEnabled()) logger.debug("modifiers: " + e.getModifiers());
			findTextField.selectAll();
			createFilteredView();
		}

		public void insertUpdate(DocumentEvent e)
		{
			applyFilter();
		}

		public void removeUpdate(DocumentEvent e)
		{
			applyFilter();
		}

		public void changedUpdate(DocumentEvent e)
		{
			applyFilter();
		}
	}

	/**
	 * This action has different enabled logic than the one in ViewActions
	 */
	private class FindNextAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -6469494975854597398L;

		public FindNextAction()
		{
			super();
			Icon icon;
			{
				URL url = EventWrapperViewPanel.class.getResource("/tango/16x16/actions/go-down.png");
				if(url != null)
				{
					icon = new ImageIcon(url);
				}
				else
				{
					icon = null;
				}
			}
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.SHORT_DESCRIPTION, "Find next.");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift G");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			findNext(getSelectedRow(), getFilterCondition());
		}
	}

	/**
	 * This action has different enabled logic than the one in ViewActions
	 */
	private class FindPreviousAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -8192948220602398223L;

		public FindPreviousAction()
		{
			super();
			Icon icon;
			{
				URL url = EventWrapperViewPanel.class.getResource("/tango/16x16/actions/go-up.png");
				if(url != null)
				{
					icon = new ImageIcon(url);
				}
				else
				{
					icon = null;
				}
			}
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.SHORT_DESCRIPTION, "Find previous.");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " G");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			findPrevious(getSelectedRow(), getFilterCondition());
		}
	}

	private class CloseFindAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -7757686292973276423L;

		public CloseFindAction()
		{
			super();
			Icon icon;
			{
				URL url = EventWrapperViewPanel.class.getResource("/tango/16x16/emblems/emblem-unreadable.png");
				if(url != null)
				{
					icon = new ImageIcon(url);
				}
				else
				{
					icon = null;
				}
			}
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.SHORT_DESCRIPTION, "Close");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke("ESCAPE");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			ViewContainer<T> container = resolveContainer();
			if(container != null)
			{
				ProgressGlassPane progressPanel = container.getProgressPanel();
				progressPanel.getFindCancelAction().cancelSearch();
				setShowingFilters(false);
			}
		}
	}

	private void executeFind(Callable<Long> callable, String name, int currentRow, Condition condition)
	{
		Map<String, String> metaData = CallableMetaData.createFindMetaData(condition, eventSource, currentRow);

		String description = "Executing '" + name + "' for condition " + metaData
			.get(CallableMetaData.FIND_TASK_META_CONDITION)
			+ " on " + metaData
			.get(CallableMetaData.FIND_TASK_META_SOURCE_IDENTIFIER) + " starting at row " + currentRow + ".";

		enableFindComponents(false);
		findResultListener.setCallable(callable);
		Task<Long> task = taskManager.startTask(callable, name, description, metaData);
		ViewContainer<T> container = resolveContainer();
		if(container != null)
		{
			container.showSearchPanel(task);
		}
	}

	class FindResultListener
		implements TaskListener<Long>
	{
		private Callable<Long> callable;

		public void taskCreated(Task<Long> longTask)
		{

		}

		public void executionFailed(Task<Long> task, ExecutionException exception)
		{
			if(logger.isDebugEnabled())
			{
				logger.debug("in executionFailed:\n     task: {}\nthis.callable: {}", task, this.callable);
			}
			if(this.callable == task.getCallable())
			{
				if(logger.isInfoEnabled()) logger.info("Find execution failed!", exception);
				finished();
			}
		}

		public void executionFinished(Task<Long> task, Long result)
		{
			if(logger.isDebugEnabled())
			{
				logger.debug("in executionFinished:\n     task: {}\nthis.callable: {}", task, this.callable);
			}
			if(this.callable == task.getCallable())
			{
				if(logger.isInfoEnabled()) logger.info("Find execution finished: {}!", result);
				if(result != null && result >= 0)
				{
					int row = result.intValue(); // this will always work
					setSelectedRow(row);
				}
				finished();
			}
		}

		public void executionCanceled(Task<Long> task)
		{
			if(logger.isDebugEnabled())
			{
				logger.debug("in executionCanceled:\n     task: {}\nthis.callable: {}", task, this.callable);
			}
			if(this.callable == task.getCallable())
			{
				if(logger.isInfoEnabled()) logger.info("Find execution canceled.");
				finished();
			}
		}

		public void progressUpdated(Task<Long> task, int progress)
		{
			if(logger.isDebugEnabled())
			{
				logger
					.debug("in progressUpdated:\task: {}\n   this.callable: {}", task, this.callable);
			}
			if(this.callable == task.getCallable())
			{
				// to catch updates after cancel.
				if(logger.isDebugEnabled()) logger.debug("Progress update: {}", progress);
				ViewContainer<T> container = resolveContainer();
				if(container != null)
				{
					ProgressGlassPane progressPanel = container.getProgressPanel();
					progressPanel.setProgress(progress);
				}
			}
		}

		private void finished()
		{
			if(logger.isDebugEnabled()) logger.debug("Executing FindResultListener.finished().");

			ViewContainer<T> container = resolveContainer();
			if(container != null)
			{
				ProgressGlassPane progressPanel = container.getProgressPanel();
				progressPanel.getFindCancelAction().setTask(null);
				setCallable(null);
				container.hideSearchPanel();
			}

			enableFindComponents(true);
		}

		public void setCallable(Callable<Long> callable)
		{
			if(logger.isDebugEnabled())
			{
				//noinspection ThrowableInstanceNeverThrown
				logger
					.debug("Setting task...\n     newCallable: " + callable + "\npreviousCallable: " + this.callable, new Throwable());
			}
			this.callable = callable;
		}
	}

	void enableFindComponents(boolean enabled)
	{
		closeFindAction.setEnabled(enabled);
		findTextField.setEnabled(enabled);
		if(table.getFilterCondition() != null)
		{
			findPrevAction.setEnabled(enabled);
			findNextAction.setEnabled(enabled);
		}
		else
		{
			findPrevAction.setEnabled(false);
			findNextAction.setEnabled(false);
		}
	}

	protected abstract void closeConnection(SourceIdentifier sourceIdentifier);

	private class StatusTableModelListener
		implements TableModelListener
	{

		public void tableChanged(TableModelEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("TableModelEvent: {}", e);
			updateStatusText();
		}
	}

	private class FindTypeSelectionActionListener
		implements ActionListener
	{

		public void actionPerformed(ActionEvent e)
		{
			applyFilter();
		}
	}

	private class SplitPaneListener
		implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			if(logger.isDebugEnabled()) logger.debug("Splitpane change!");
			String propertyName = evt.getPropertyName();
			if("dividerLocation".equals(propertyName))
			{
				scrollToEvent();
			}
		}
	}

	private class MessageFocusListener
		implements FocusListener
	{

		public void focusGained(FocusEvent e)
		{
			messagePane.setBorder(focusedBorder);
		}

		public void focusLost(FocusEvent e)
		{
			messagePane.setBorder(unfocusedBorder);
		}
	}
}

