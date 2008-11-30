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
package de.huxhorn.lilith.swing;

import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.lilith.engine.impl.EventSourceImpl;
import de.huxhorn.lilith.filters.EventContainsCondition;
import de.huxhorn.lilith.filters.GroovyFilter;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.swing.linklistener.StackTraceElementLinkListener;
import de.huxhorn.lilith.swing.table.EventWrapperViewTable;
import de.huxhorn.lilith.swing.table.model.EventWrapperTableModel;
import de.huxhorn.sulky.buffers.SoftReferenceCachingBuffer;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.FilteringBuffer;
import de.huxhorn.sulky.buffers.DisposeOperation;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.conditions.And;
import de.huxhorn.sulky.formatting.HumanReadable;
import de.huxhorn.sulky.swing.SwingWorkManager;
import de.huxhorn.sulky.swing.KeyStrokes;
import de.huxhorn.sulky.swing.ProgressingCallable;
import de.huxhorn.sulky.swing.ResultListener;
import de.huxhorn.sulky.swing.AbstractProgressingCallable;

import javax.swing.border.MatteBorder;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.JSplitPane;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Callable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.awt.Color;
import java.awt.FocusTraversalPolicy;
import java.awt.EventQueue;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.KeyboardFocusManager;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.simple.FSScrollPane;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;
import org.xhtmlrenderer.swing.LinkListener;
import org.apache.commons.io.IOUtils;

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
	private SwingWorkManager<Integer> workManager;

	private EventWrapperViewTable<T> table;
	private EventWrapperTableModel<T> tableModel;

	private FindNextAction findNextAction;
	private FindPreviousAction findPrevAction;
	private CloseFindAction closeFindAction;

	private JButton findPrevButton;
	private JButton findNextButton;

	private JToolBar findPanel;
	private JTextField findTextField;
	private JLabel statusLabel;
	private JScrollBar verticalLogScrollbar;

	private TableModelListener tableModelListener;
	private FocusTraversalPolicy focusTraversalPolicy;
	private MatteBorder focusedBorder;
	private MatteBorder unfocusedBorder;
	private DecimalFormat eventCountFormat;
	private FindResultListener findResultListener;
	private static final String GROOVY_IDENTIFIER = "#groovy#";
	private static final Color ERROR_COLOR = new Color(0xffaaaa);
	protected JMenu sendToMenuItem;

	private XHTMLPanel messagePane;
	private XhtmlNamespaceHandler xhtmlNamespaceHandler;
	private EventWrapper<T> selectedEvent;


	public EventWrapperViewPanel(MainFrame mainFrame, EventSource<T> eventSource)
	{
		super(true);
		eventCountFormat=new DecimalFormat("#,###");
		this.workManager=mainFrame.getIntegerWorkManager();
		findResultListener=new FindResultListener();
		workManager.addResultListener(findResultListener);
		this.mainFrame=mainFrame;
		this.eventSource=eventSource;
		showingFilters=false;

		tableModelListener=new StatusTableModelListener();
		initUi();
	}

	public EventWrapperViewTable<T> getTable()
	{
		return table;
	}

	public LoggingViewState getState()
	{
		if(!EventQueue.isDispatchThread())
		{
			if(logger.isWarnEnabled()) //noinspection ThrowableInstanceNeverThrown
				logger.warn("!DispatchThread - getState: state="+state, new Throwable());
		}
		return state;
	}

	public MainFrame getMainFrame()
	{
		return mainFrame;
	}

	public void setState(LoggingViewState state)
	{
		Object oldValue=this.state;
		this.state = state;
		Object newValue=this.state;
		firePropertyChange(STATE_PROPERTY, oldValue, newValue);
	}

	public boolean isShowingFilters()
	{
		return showingFilters;
	}

	public void setShowingFilters(boolean showingFilters)
	{
		this.showingFilters = showingFilters;
		findPanel.setVisible(showingFilters);
		if(showingFilters)
		{
			findTextField.requestFocusInWindow();
			findTextField.selectAll();
		}
		validate();
	}

	public void validate()
	{
		super.validate();
		if(table.isScrollingToBottom())
		{
			table.scrollToBottom();
			// since the view might have changed...
		}
		if(logger.isDebugEnabled()) logger.debug("Validate");
	}

	private SoftReferenceCachingBuffer<EventWrapper<T>> createCachedBuffer(Buffer<EventWrapper<T>> buffer)
	{
		return new SoftReferenceCachingBuffer<EventWrapper<T>>(buffer);
	}

	void setEventSource(EventSource<T> eventSource)
	{
		EventSource oldValue = this.eventSource;
		this.eventSource=eventSource;
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
		Object oldValue=table.isScrollingToBottom();
		table.setScrollingToBottom(scrollingToBottom);
		Object newValue=table.isScrollingToBottom();
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
		Object oldValue=tableModel.isPaused();
		tableModel.setPaused(paused);
		Object newValue=tableModel.isPaused();
		firePropertyChange(PAUSED_PROPERTY, oldValue, newValue);
	}

	public ViewContainer<T> resolveContainer()
	{
		Container parent=getParent();
		while(parent!= null && !(parent instanceof ViewContainer))
		{
			parent=parent.getParent();
		}
		// not 100% typesafe
		//noinspection unchecked
		return (ViewContainer<T>)parent;
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

	private void initUi()
	{
		Insets borderInsets=new Insets(2,2,2,2);
		focusedBorder=new MatteBorder(borderInsets, Color.YELLOW);
		unfocusedBorder=new MatteBorder(borderInsets, Color.WHITE);

		SoftReferenceCachingBuffer<EventWrapper<T>> cachedBuffer = createCachedBuffer(eventSource.getBuffer());
		tableModel = createTableModel(cachedBuffer);
		tableModel.addTableModelListener(tableModelListener);
		table = createTable(tableModel);
		table.getSelectionModel().addListSelectionListener(new TableRowSelectionListener());
		JScrollPane tableScrollPane = new JScrollPane(table);
		verticalLogScrollbar = tableScrollPane.getVerticalScrollBar();

		messagePane=new XHTMLPanel();
		messagePane.addMouseListener(new EventViewMouseListener());


		messagePane.addFocusListener(new MessageFocusListener());
		messagePane.setBorder(unfocusedBorder);

		{
			List mouseTrackingList = messagePane.getMouseTrackingListeners();
			if(mouseTrackingList!=null)
			{
				for(Object o: mouseTrackingList)
				{
					if(logger.isDebugEnabled()) logger.debug("Before MTL {}",o);
					if(o instanceof LinkListener)
					{
						messagePane.removeMouseTrackingListener((LinkListener) o);
					}
				}
			}
		}

		messagePane.addMouseTrackingListener(new StackTraceElementLinkListener(mainFrame));

		xhtmlNamespaceHandler=new XhtmlNamespaceHandler();
		FSScrollPane messageScrollPane = new FSScrollPane(messagePane);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, messageScrollPane);
		PropertyChangeListener splitPaneListener=new SplitPaneListener();
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

		FindTextFieldListener findTextFieldListener=new FindTextFieldListener();
		findTextField.addActionListener(findTextFieldListener);
		findTextField.getDocument().addDocumentListener(findTextFieldListener);
		findTextField.setBackground(Color.WHITE);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		JPanel statusPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		bottomPanel.add(findPanel, BorderLayout.CENTER);
		bottomPanel.add(statusPanel, BorderLayout.SOUTH);

		statusLabel=new JLabel();

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0,5,0,0);

		statusPanel.add(statusLabel, gbc);

		add(bottomPanel, BorderLayout.SOUTH);

		setScrollingToBottom(false);
		setPaused(false);
		setFocusTraversalPolicy(focusTraversalPolicy);
		setFocusCycleRoot(true);
		setFocusTraversalPolicyProvider(true);

		if(logger.isDebugEnabled()) logger.debug("table.isFocusCycleRoot()={}", table.isFocusCycleRoot());
		if(logger.isDebugEnabled()) logger.debug("table.isFocusTraversalPolicyProvider()={}", table.isFocusTraversalPolicyProvider());
		table.setFocusTraversalPolicy(focusTraversalPolicy);
		table.setFocusCycleRoot(true);

		// setting table traversal back to "normal"...
		table.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
		table.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));

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
		findTextField =new JTextField();
		findTextField.setColumns(15);
		findPanel.add(findTextField);

		findPrevAction=new FindPreviousAction();
		findPrevButton=new JButton(findPrevAction);
		findPanel.add(findPrevButton);

		findNextAction=new FindNextAction();
		findNextButton=new JButton(findNextAction);
		findPanel.add(findNextButton);
		enableFindComponents(true);
	}

	public void dispose()
	{
		tableModel.dispose();
		workManager.removeResultListener(findResultListener);
	}

	public boolean isDisposed()
	{
		return tableModel.isDisposed();
	}

	public void scrollToBottom()
	{
		table.scrollToBottom();
	}

	public void resetFind()
	{
		findTextField.setText("");
		setFilterCondition(null);
	}

	public void setSelectedEvent(EventWrapper<T> selectedEvent)
	{
		Object oldValue=this.selectedEvent;
		this.selectedEvent = selectedEvent;
		Object newValue=this.selectedEvent;
		firePropertyChange(SELECTED_EVENT_PROPERTY, oldValue, newValue);

		initMessage(this.selectedEvent);
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
					if(findTextField.isEnabled())
					{
						return findTextField;
					}
					if(findPrevButton.isEnabled())
					{
						return findPrevButton;
					}
					if(findNextButton.isEnabled())
					{
						return findNextButton;
					}
				}
				return table;
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

			// I guess focus was inside table so focus component after table.
			if(logger.isInfoEnabled()) logger.info("Moving focus forward to messagePane since it was not explicitly handled. (component={})", aComponent);
			return messagePane;
		}

		public Component getComponentBefore(Container aContainer, Component aComponent)
		{
			if(aComponent.equals(messagePane))
			{
				return table;
			}
			if(aComponent.equals(findTextField))
			{
				return messagePane;
			}
			if(aComponent.equals(findPrevButton))
			{
				if(findTextField.isEnabled())
				{
					return findTextField;
				}
				return messagePane;
			}
			if(aComponent.equals(findNextButton))
			{
				if(findPrevButton.isEnabled())
				{
					return findPrevButton;
				}
				if(findTextField.isEnabled())
				{
					return findTextField;
				}
				return messagePane;
			}

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
				if(findTextField.isEnabled())
				{
					return findTextField;
				}
			}
			// I guess focus was inside table so focus component before table.
			if(logger.isInfoEnabled()) logger.info("Moving focus backward to messagePane since it was not explicitly handled. (component={})", aComponent);
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


	public void initMessage(EventWrapper wrapper)
	{
		String message=mainFrame.createMessage(wrapper);
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
		SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd'T'HHmmssSSSZ");
		String filename=format.format(new Date());
		File errorFile=new File(errorPath, filename);
		FileOutputStream fos=null;
		try
		{
			fos=new FileOutputStream(errorFile);
			OutputStreamWriter osw=new OutputStreamWriter(fos, "UTF-8");
			osw.append(message);
			osw.flush();
			if(logger.isInfoEnabled()) logger.info("Faulty message written to '{}'.", errorFile.getAbsolutePath());
		}
		catch (Throwable e)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while writing faulty message to '"+errorFile.getAbsolutePath()+"'!", e);
		}
		finally
		{
			IOUtils.closeQuietly(fos);
		}

	}

	/**
	 * Does also disable scrollingToBottom and selects the clicked row, if any.
	 * @param p the point
	 * @return the EventWrapper at the given point.
	 */
	private EventWrapper<T> getEventWrapper(Point p)
	{
		int row = table.rowAtPoint(p);
		if (-1 == row)
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
		String text=findTextField.getText();
		Condition condition;
		boolean error=false;
		if(text!=null && text.length()>0)
		{
			if(text.startsWith(GROOVY_IDENTIFIER))
			{
				String scriptName=text.substring(GROOVY_IDENTIFIER.length());
				File resolvedScriptFile=mainFrame.resolveScriptFile(scriptName);
				if(resolvedScriptFile!=null)
				{
					// there is a file...
					condition = new GroovyFilter(resolvedScriptFile.getAbsolutePath());
				}
				else
				{
					error=true;
					condition=null;
				}
			}
			else
			{
				condition=new EventContainsCondition(text, false);
			}
		}
		else
		{
			condition=null;
		}
		if(error)
		{
			// problem with condition
			findTextField.setBackground(ERROR_COLOR);
		}
		else
		{
			findTextField.setBackground(Color.WHITE);
		}
		if(logger.isDebugEnabled()) logger.debug("Setting condition: {}", condition);
		setFilterCondition(condition);
	}

	public void setFilterCondition(Condition condition)
	{
		Object old=this.filterCondition;
		this.filterCondition=condition;
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
		if(condition!=null)
		{
			ProgressingCallable<Integer> callable = new FindPreviousCallable(currentRow, condition);
			executeFind(callable);
		}
	}

	public void findNext(int currentRow, Condition condition)
	{
		if(condition!=null)
		{
			ProgressingCallable<Integer> callable = new FindNextCallable(currentRow, condition);
			executeFind(callable);
		}
	}

	private class FindPreviousCallable
		extends AbstractProgressingCallable<Integer>
	{
		private final Logger logger = LoggerFactory.getLogger(FindPreviousCallable.class);

		private int currentRow;
		private Condition condition;

		public FindPreviousCallable(int currentRow, Condition condition)
		{
			super(200,1000);
			this.currentRow = currentRow;
			this.condition = condition;
		}

		public Integer call() throws Exception
		{
			int row=currentRow;
			if(row>-1)
			{
				row--;
				if(logger.isInfoEnabled()) logger.info("Searching previous starting at {}.", row);
				tableModel.getRowCount();
				int maxCount = row;
				int numberOfSteps=maxCount-1;
				if(numberOfSteps<1)
				{
					numberOfSteps=1;
				}
				setNumberOfSteps(numberOfSteps);
				for(int i=0; i<maxCount; i++)
				{
					setCurrentStep(i);
					int current=row-i;
					if(logger.isDebugEnabled()) logger.debug("Processing row {}", current);
					Object obj=tableModel.getValueAt(current,0);
					if(obj==null)
					{
						return -1;
					}
					if(obj instanceof EventWrapper)
					{
						if(condition.isTrue(obj))
						{
							if(logger.isInfoEnabled()) logger.info("Found previous at {}.",current);
							return current;
						}
					}
					else
					{
						if(logger.isWarnEnabled()) logger.warn("Unexpected class! {}", obj.getClass().getName());
					}
				}
			}
			if(logger.isInfoEnabled()) logger.info("Didn't find previous.");
			return -1;
		}
	}

	private class FindNextCallable
		extends AbstractProgressingCallable<Integer>
	{
		private final Logger logger = LoggerFactory.getLogger(FindNextCallable.class);

		private int currentRow;
		private Condition condition;

		public FindNextCallable(int currentRow, Condition condition)
		{
			super(200,1000);
			this.currentRow = currentRow;
			this.condition = condition;
		}

		public Integer call() throws Exception
		{
			int row=currentRow;
			if(row>-1)
			{
				row++;
				if(logger.isInfoEnabled()) logger.info("Searching next starting at {}.", row);

				int maxCount = tableModel.getRowCount() - row;
				int numberOfSteps=maxCount-1;
				if(numberOfSteps<1)
				{
					numberOfSteps=1;
				}
				setNumberOfSteps(numberOfSteps);
				for(int i=0; i<maxCount; i++)
				{
					setCurrentStep(i);
					int current=i+row;
					if(logger.isDebugEnabled()) logger.debug("Processing row {}", current);
					Object obj=tableModel.getValueAt(current,0);
					if(obj==null)
					{
						return -1;
					}
					if(obj instanceof EventWrapper)
					{
						if(condition.isTrue(obj))
						{
							if(logger.isInfoEnabled()) logger.info("Found next at {}.",current);
							return current;
						}
					}
					else
					{
						if(logger.isWarnEnabled()) logger.warn("Unexpected class! {}", obj.getClass().getName());
					}
				}
			}
			if(logger.isInfoEnabled()) logger.info("Didn't find next.");
			return -1;
		}
	}

	public void setSelectedRow(int row)
	{
		if(row>-1)
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
		StringBuffer statusText=new StringBuffer();
		if(eventCount<1)
		{
			statusText.append("No events.");
		}
		else
		{
			if(eventCount==1)
			{
				statusText.append("One event.");
			}
			else
			{
				statusText.append(eventCountFormat.format(eventCount)).append(" events.");
			}
			statusText.append("   Size on disk: ").append(HumanReadable.getHumanReadableSize(getSizeOnDisk(), true, false)).append("bytes");
		}


		statusLabel.setText(statusText.toString());
	}

	protected abstract long getSizeOnDisk();

	private EventSource<T> getFilteredSource()
	{
		Condition currentFilter = table.getFilterCondition();
		if(currentFilter!=null)
		{
			Buffer<EventWrapper<T>> buffer = eventSource.getBuffer();

			Condition previousCondition=null;
			Condition previousClone=null;
			if(buffer instanceof FilteringBuffer)
			{
				FilteringBuffer<EventWrapper<T>> filteringBuffer=(FilteringBuffer<EventWrapper<T>>) buffer;
				buffer=filteringBuffer.getSourceBuffer();
				previousCondition=filteringBuffer.getCondition();
				if(logger.isDebugEnabled()) logger.debug("Previous condition: {}",previousCondition);
			}

			Condition filter=currentFilter;
			if(previousCondition!=null)
			{
				try
				{
					previousCondition=previousCondition.clone(); // TODO: is this a bug? Check!
					previousClone=previousCondition.clone();
					And and;
					if(previousCondition instanceof And)
					{
						and=(And) previousCondition;
					}
					else
					{
						and=new And();
						ArrayList<Condition> conditions = new ArrayList<Condition>();
						conditions.add(previousCondition);
						and.setConditions(conditions);
					}
					List<Condition> conditions=and.getConditions();
					if(conditions==null)
					{
						conditions=new ArrayList<Condition>();
					}
					if(!conditions.contains(currentFilter))
					{
						// don't add duplicates
						conditions.add(currentFilter);
					}
					if(conditions.size()>1)
					{
						if(logger.isInfoEnabled()) logger.info("Setting and-conditions: {}", conditions);
						and.setConditions(conditions);
						filter=and;
					}
					else
					{
						filter=currentFilter;
					}
				}
				catch (CloneNotSupportedException ex)
				{
					if(logger.isWarnEnabled()) logger.warn("Exception while cloning!",ex);
				}
			}
			else
			{
				filter=currentFilter;
			}
			if(!filter.equals(previousClone))
			{
				Buffer<EventWrapper<T>> filteredBuffer=new FilteringBuffer<EventWrapper<T>>(buffer, filter);
				return new EventSourceImpl<T>(eventSource.getSourceIdentifier(),filteredBuffer, filter, eventSource.isGlobal());
			}
		}
		return null;
	}

	private void createFilteredView()
	{
		ViewContainer<T> container = resolveContainer();
		Condition currentFilter = table.getFilterCondition();
		if(container!=null && currentFilter!=null)
		{
			EventSource<T> newSource=getFilteredSource();
			if(newSource!=null)
			{
				EventWrapperViewPanel<T> view = container.createViewPanel(newSource);
				container.addView(view);
				if(logger.isInfoEnabled()) logger.info("Added source: {}",newSource);
			}
		}
	}

	/**
	 * If the currently selected event is in a filtered tab, the same event is displayed in the
	 * unfiltered view.
	 */
	public void showUnfilteredEvent()
	{
		int row=getSelectedRow();
		if(row>=0)
		{
			ViewContainer<T> container = resolveContainer();
			if(container!=null)
			{

				Buffer<EventWrapper<T>> buffer = eventSource.getBuffer();
				if(buffer instanceof FilteringBuffer)
				{
					FilteringBuffer<EventWrapper<T>> filteringBuffer=(FilteringBuffer<EventWrapper<T>>) buffer;
					long unfilteredRow=filteringBuffer.getSourceIndex(row);
					if(unfilteredRow>=0)
					{
						if(logger.isInfoEnabled()) logger.info("Show unfiltered event {} for filtered event {}...", unfilteredRow, row);
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
			if (evt.isPopupTrigger())
			{
				showPopup(evt);
			}
			else if(evt.getClickCount()>1 && evt.getButton()==MouseEvent.BUTTON1)
			{
				if((evt.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0)
				{
					Point p = evt.getPoint();
					EventWrapper<T> wrapper=getEventWrapper(p);
					if(wrapper!=null)
					{
						T event = wrapper.getEvent();
						if(event instanceof LoggingEvent)
						{
							LoggingEvent loggingEvent= (LoggingEvent) event;
							ExtendedStackTraceElement[] callStack = loggingEvent.getCallStack();
							if(callStack!=null && callStack.length>0)
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
			EventWrapper<T> wrapper=getEventWrapper(p); // To ensure that the event below the mouse is selected.
			if(logger.isDebugEnabled()) logger.debug("Show popup at {} for event {}.", p, wrapper);
			mainFrame.showPopup(table, p);
		}

		public void mousePressed(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mouseReleased(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
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
			if (evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mousePressed(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mouseReleased(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
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

	private class TableRowSelectionListener implements ListSelectionListener
	{
		private final Logger logger = LoggerFactory.getLogger(TableRowSelectionListener.class);

		public void valueChanged(ListSelectionEvent e)
		{
			int row=getSelectedRow();
			if(logger.isDebugEnabled()) logger.debug("Selected row: {}.", row);

			if(row>=0)
			{
				EventWrapper<T> event=tableModel.getValueAt(row);
				setSelectedEvent(event);
			}
			else
			{
				setSelectedEvent(null);
			}
		}
	}

	/**
	 * Disables "Scroll to Bottom" in case of adjusting, i.e. if the scrollbar is dragged.
	 */
	private class ScrollbarChangeListner implements ChangeListener
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
			String propertyName=event.getPropertyName();
			if(EventWrapperViewTable.SCROLLING_TO_BOTTOM_PROPERTY.equals(propertyName))
			{
				Object oldValue=event.getOldValue();
				Object newValue=event.getNewValue();
				firePropertyChange(SCROLLING_TO_BOTTOM_PROPERTY, oldValue, newValue);
			}
			else if(EventWrapperViewTable.FILTER_CONDITION_PROPERTY.equals(propertyName))
			{
				if(null==event.getNewValue())
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
		public ReplaceFilterAction()
		{
			super();
			putValue(Action.SHORT_DESCRIPTION, "Replace filter.");
			KeyStroke accelerator=KeyStrokes.resolveAcceleratorKeyStroke("shift ENTER");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			if(logger.isInfoEnabled()) logger.info("Replace filter.");
			Buffer<EventWrapper<T>> buffer = eventSource.getBuffer();

			if(buffer instanceof FilteringBuffer)
			{
				// replace
				EventSource<T> filteredSource = getFilteredSource();
				if(filteredSource!=null)
				{
					if(logger.isInfoEnabled()) logger.info("Replacing filter...");
					setEventSource(filteredSource);
				}
			}
			else
			{
				// create new
				createFilteredView();
			}
		}
	}

	private class FindTextFieldListener
		implements ActionListener, DocumentListener
	{

		public void actionPerformed(ActionEvent e)
		{
			applyFilter();
			if(logger.isDebugEnabled()) logger.debug("modifiers: "+e.getModifiers());
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


	private class FindNextAction
		extends AbstractAction
	{
		// TODO: Move to ViewActions
		public FindNextAction()
		{
			super();
			Icon icon;
			{
				URL url=EventWrapperViewPanel.class.getResource("/tango/16x16/actions/go-down.png");
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
			putValue(Action.SHORT_DESCRIPTION, "Find next.");
			KeyStroke accelerator=KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS+" shift G");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			findNext(getSelectedRow(), getFilterCondition());
		}
	}

	private class FindPreviousAction
		extends AbstractAction
	{
		// TODO: Move to ViewActions
		public FindPreviousAction()
		{
			super();
			Icon icon;
			{
				URL url=EventWrapperViewPanel.class.getResource("/tango/16x16/actions/go-up.png");
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
			putValue(Action.SHORT_DESCRIPTION, "Find previous.");
			KeyStroke accelerator=KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS+" G");
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
		public CloseFindAction()
		{
			super();
			Icon icon;
			{
				URL url=EventWrapperViewPanel.class.getResource("/tango/16x16/emblems/emblem-unreadable.png");
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
			putValue(Action.SHORT_DESCRIPTION, "Close");
			KeyStroke accelerator=KeyStrokes.resolveAcceleratorKeyStroke("ESCAPE");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			ViewContainer<T> container = resolveContainer();
			if(container!=null)
			{
				ProgressGlassPane progressPanel = container.getProgressPanel();
				progressPanel.getFindCancelAction().cancelSearch();
				setShowingFilters(false);
			}
		}
	}

	private void executeFind(ProgressingCallable<Integer> callable)
	{
		enableFindComponents(false);
		findResultListener.setCallable(callable);
		//progressPanel.setProgress(0);
		Future<Integer> future=workManager.add(callable);
		//progressPanel.getFindCancelAction().setFuture(future);
		ViewContainer<T> container = resolveContainer();
		if(container!=null)
		{
			container.showSearchPanel(future);
		}
	}

	class FindResultListener
		implements ResultListener<Integer>
	{
		private Callable<Integer> callable;

		public void executionFailed(Callable<Integer> callable, ExecutionException exception)
		{
			if(logger.isDebugEnabled()) logger.debug("in executionFailed:\n     callable: {}\nthis.callable: {}", callable, this.callable);
			if(this.callable==callable)
			{
				if(logger.isInfoEnabled()) logger.info("Find execution failed!",exception);
				finished();
			}
		}

		public void executionFinished(Callable<Integer> callable, Integer result)
		{
			if(logger.isDebugEnabled()) logger.debug("in executionFinished:\n     callable: {}\nthis.callable: {}", callable, this.callable);
			if(this.callable==callable)
			{
				if(logger.isInfoEnabled()) logger.info("Find execution finished: {}!",result);
				if(result>=0)
				{
					setSelectedRow(result);
				}
				finished();
			}
		}

		public void executionCanceled(Callable<Integer> callable)
		{
			if(logger.isDebugEnabled()) logger.debug("in executionCanceled:\n     callable: {}\nthis.callable: {}", callable, this.callable);
			if(this.callable==callable)
			{
				if(logger.isInfoEnabled()) logger.info("Find execution canceled.");
				finished();
			}
		}

		public void progressUpdated(ProgressingCallable progressingCallable, int progress)
		{
			if(logger.isDebugEnabled()) logger.debug("in progressUpdated:\nprogressingCallable: {}\n   this.callable: {}", progressingCallable, this.callable);
			if(this.callable==progressingCallable)
			{
				// to catch updates after cancel.
				if(logger.isDebugEnabled()) logger.debug("Progress update: {}", progress);
				ViewContainer<T> container = resolveContainer();
				if(container!=null)
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
			if(container!=null)
			{
				ProgressGlassPane progressPanel = container.getProgressPanel();
				progressPanel.getFindCancelAction().setFuture(null);
				setCallable(null);
				container.hideSearchPanel();
			}

			enableFindComponents(true);
		}

		public void setCallable(Callable<Integer> callable)
		{
			if(logger.isDebugEnabled()) //noinspection ThrowableInstanceNeverThrown
				logger.debug("Setting callable...\n     newFuture: "+callable+"\npreviousFuture: "+this.callable, new Throwable());
			this.callable=callable;
		}
	}

	void enableFindComponents(boolean enabled)
	{
		closeFindAction.setEnabled(enabled);
		findTextField.setEnabled(enabled);
		if(table.getFilterCondition()!=null)
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

	private class SplitPaneListener
		implements PropertyChangeListener
	{

		public void propertyChange(PropertyChangeEvent evt)
		{
			if(logger.isDebugEnabled()) logger.debug("Splitpane change!");
			validate();
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

