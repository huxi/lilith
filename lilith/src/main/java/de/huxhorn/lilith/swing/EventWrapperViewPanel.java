/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2014 Joern Huxhorn
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

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.lilith.swing.callables.CallableMetaData;
import de.huxhorn.lilith.swing.callables.FindNextCallable;
import de.huxhorn.lilith.swing.callables.FindPreviousCallable;
import de.huxhorn.lilith.swing.linklistener.StackTraceElementLinkListener;
import de.huxhorn.lilith.swing.table.EventWrapperViewTable;
import de.huxhorn.lilith.swing.table.model.EventWrapperTableModel;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.DisposeOperation;
import de.huxhorn.sulky.buffers.Flush;
import de.huxhorn.sulky.buffers.FlushOperation;
import de.huxhorn.sulky.buffers.SoftReferenceCachingBuffer;
import de.huxhorn.sulky.buffers.filtering.FilteringBuffer;
import de.huxhorn.sulky.codec.filebuffer.CodecFileBuffer;
import de.huxhorn.sulky.conditions.And;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.formatting.HumanReadable;
import de.huxhorn.sulky.swing.KeyStrokes;
import de.huxhorn.sulky.tasks.ProgressingCallable;
import de.huxhorn.sulky.tasks.Task;
import de.huxhorn.sulky.tasks.TaskListener;
import de.huxhorn.sulky.tasks.TaskManager;

import de.huxhorn.sulky.io.IOUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.context.AWTFontResolver;
import org.xhtmlrenderer.extend.FontResolver;
import org.xhtmlrenderer.extend.TextRenderer;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.simple.FSScrollPane;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;
import org.xhtmlrenderer.swing.LinkListener;
import org.xhtmlrenderer.swing.ScalableXHTMLPanel;
import org.xhtmlrenderer.swing.SelectionHighlighter;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public abstract class EventWrapperViewPanel<T extends Serializable>
	extends JPanel
	implements DisposeOperation, FlushOperation
{
	private static final long serialVersionUID = 7987088820464790207L;

	private final Logger logger = LoggerFactory.getLogger(EventWrapperViewPanel.class);

	public static final String STATE_PROPERTY = "state";
	public static final String FILTER_CONDITION_PROPERTY = "filterCondition";
	public static final String EVENT_SOURCE_PROPERTY = "eventSource";
	public static final String SCROLLING_TO_BOTTOM_PROPERTY = "scrollingToBottom";
	public static final String PAUSED_PROPERTY = "paused";
	public static final String SELECTED_EVENT_PROPERTY = "selectedEvent";


	private EventSource<T> eventSource;
	private LoggingViewState state;
	private MainFrame mainFrame;
	private boolean showingFilters;
	private Condition filterCondition;
	private TaskManager<Long> taskManager;

	private EventWrapperViewTable<T> table;
	private EventWrapperTableModel<T> tableModel;

	private JLabel statusLabel;
	private JScrollBar verticalLogScrollbar;

	private StatusTableModelListener tableModelListener;
	private MatteBorder focusedBorder;
	private MatteBorder unfocusedBorder;
	private DecimalFormat eventCountFormat;
	private FindResultListener findResultListener;

	private ScalableXHTMLPanel messagePane;
	private XhtmlNamespaceHandler xhtmlNamespaceHandler;
	private EventWrapper<T> selectedEvent;
	private SelectionHighlighter.CopyAction copyAction;
	private double scale;
	private JScrollPane tableScrollPane;
	private FindPanel<T> findPanel;
	private SoftReferenceCachingBuffer<EventWrapper<T>> cachedBuffer;

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
		scale = mainFrame.getApplicationPreferences().getScaleFactor();
		initUi();
	}

	private void initUi()
	{
		Insets borderInsets = new Insets(2, 2, 2, 2);
		focusedBorder = new MatteBorder(borderInsets, Color.YELLOW);
		unfocusedBorder = new MatteBorder(borderInsets, Color.WHITE);

		cachedBuffer = createCachedBuffer(eventSource.getBuffer());
		tableModel = createTableModel(cachedBuffer);
		tableModel.addTableModelListener(tableModelListener);
		table = createTable(tableModel);
		table.getSelectionModel().addListSelectionListener(new TableRowSelectionListener());
		tableScrollPane = new JScrollPane(table);
		table.addMouseListener(new TableMouseListener());
		tableScrollPane.addMouseListener(new ScrollPaneMouseListener());
		tableScrollPane.setPreferredSize(new Dimension(400, 400));

		verticalLogScrollbar = tableScrollPane.getVerticalScrollBar();

		messagePane = new ScalableXHTMLPanel();

		{
			SharedContext sharedContext = messagePane.getSharedContext();
			TextRenderer textRenderer = sharedContext.getTextRenderer();
			textRenderer.setSmoothingThreshold(RendererConstants.SMOOTHING_THRESHOLD);
			FontResolver fontResolver = sharedContext.getFontResolver();
			if(fontResolver instanceof AWTFontResolver && RendererConstants.MENSCH_FONT != null)
			{
				AWTFontResolver awtFontResolver = (AWTFontResolver) fontResolver;
				awtFontResolver.setFontMapping(RendererConstants.MONOSPACED_FAMILY, RendererConstants.MENSCH_FONT);
				if(logger.isInfoEnabled()) logger.info("Installed '{}' font.", RendererConstants.MONOSPACED_FAMILY);
			}
		}

		messagePane.setScale(mainFrame.getApplicationPreferences().getScaleFactor());
		SelectionHighlighter messagePaneCaret = new SelectionHighlighter();
		messagePaneCaret.install(messagePane);

		copyAction = new SelectionHighlighter.CopyAction();
		copyAction.install(messagePaneCaret);

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
		messageScrollPane.setPreferredSize(new Dimension(400, 400));

		MouseWheelListener[] mwl = messageScrollPane.getMouseWheelListeners();
		if(mwl != null)
		{
			for(MouseWheelListener current : mwl)
			{
				messageScrollPane.removeMouseWheelListener(current);
			}
		}
		messageScrollPane.addMouseWheelListener(new WrappingMouseWheelListener(mwl));

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, messageScrollPane);
		PropertyChangeListener splitPaneListener = new SplitPaneListener();
		splitPane.addPropertyChangeListener(splitPaneListener);
		splitPane.setResizeWeight(0.5); // divide space equally in case of resize.
		splitPane.setOneTouchExpandable(true);
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
		ScrollbarChangeListner scrollBarChangeListener = new ScrollbarChangeListner();
		verticalLogScrollbar.getModel().addChangeListener(scrollBarChangeListener);


		table.addPropertyChangeListener(new EventWrapperViewChangeListener());


		JPanel bottomPanel = new JPanel(new BorderLayout());
		JPanel statusPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		findPanel = new FindPanel<T>(this);
		findPanel.addPropertyChangeListener(new FindPanelChangeListener());
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

		FocusTraversalPolicy focusTraversalPolicy = new MyFocusTraversalPolicy();
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

		splitPane.setDividerLocation(0.5d);
		setShowingStatusBar(mainFrame.getApplicationPreferences().isShowingStatusbar());
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
		if(logger.isDebugEnabled()) logger.debug("ShowingFilters: {}", showingFilters);
		this.showingFilters = showingFilters;
		if(showingFilters)
		{
			findPanel.updateUi();
		}
		findPanel.setVisible(showingFilters);
		if(showingFilters)
		{
			findPanel.requestComboFocus();
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

	public void setScaleFactor(double scale)
	{
		this.scale = scale;
		messagePane.setScale(scale);
	}

	public void updateView()
	{
		// update HTML detailsview
		EventWrapper<T> selected = getSelectedEvent();
		if(selected != null)
		{
			initMessage(selected);
		}
		else
		{
			resetMessage();
		}
	}

	public void setShowingStatusBar(boolean showingStatusbar)
	{
		statusLabel.setVisible(showingStatusbar);
	}

	public void setPreviousSearchStrings(List<String> previousSearchStrings)
	{
		findPanel.setPreviousSearchStrings(previousSearchStrings);
	}

	public void setConditionNames(List<String> conditionNames)
	{
		findPanel.setConditionNames(conditionNames);
	}

	private class ScrollToBottomRunnable
		implements Runnable
	{
		public void run()
		{
			// recheck because this is executed with invokeLater.
			if(table.isScrollingToBottom())
			{
				table.scrollToBottom();
			}
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

	public void dispose()
	{
		tableModel.dispose();
		taskManager.removeTaskListener(findResultListener);
	}

	public void flush()
	{
		Flush.flush(cachedBuffer);
		resetFind();
	}

	public boolean isDisposed()
	{
		return tableModel.isDisposed();
	}

	public void resetFind()
	{
		findPanel.resetFind();
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

	/**
	 * Alright... The code in this class sucks big time. I'm fully aware of this fact.
	 * One would expect that Focus Traversal would play well in an hierarchy, too, but it doesn't.
	 * It's only supporting up- and down-cycling which isn't what I want/need.
	 * I'd like to traverse into the findPanel and back out of it....
	 * buuuuuut.....
	 * if I implement that in a sophisticated, i.e. non-sucking, way I'd risk to infringe the
	 * software patent "US Patent 6606106 - Hierarchical model for expressing focus traversal"
	 * which you may review here:
	 * http://www.patentstorm.us/patents/6606106/fulltext.html
	 *
	 * This is a hilarious example for the epic fail inherent to all software patents.
	 * It's also a really good reason why you should vote for your local Pirate Party, or,
	 * if your country doesn't have a Pirate Party yet, a reason to start one.
	 *
	 * If this is out of question then please try to vote for a party that opposes software patents,
	 * assuming there is one in your country.
	 *
	 * kthxbai, Joern. 
	 */
	class MyFocusTraversalPolicy
		extends FocusTraversalPolicy
	{
		private Component resolveComponent(Component component)
		{
			Container container = component.getParent();
			while(container != null)
			{
				if(container == table)
				{
					return table;
				}
				if(container == messagePane)
				{
					return messagePane;
				}
				container = container.getParent();
			}
			return null;
		}

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
					/*
					Attention, sucking code below!
					*/
					if(logger.isDebugEnabled()) logger.debug("Performing FocusTraversal-Voodoo...");

					FocusTraversalPolicy policy = findPanel.getFocusTraversalPolicy();
					return policy.getDefaultComponent(aContainer);
					/*
					Attention, sucking code above!
					*/
				}
				return table;
			}
			if(aComponent.equals(findPanel))
			{
				return table;
			}

			/*
			Attention, sucking code below!
			*/
			if(logger.isDebugEnabled()) logger.debug("Performing FocusTraversal-Yaddayadda...");

			Component c = resolveComponent(aComponent);
			if(table.equals(c))
			{
				return messagePane;
			}
			if(messagePane.equals(c))
			{
				if(isShowingFilters())
				{
					return findPanel;
				}
				return table;
			}

			FocusTraversalPolicy policy = findPanel.getFocusTraversalPolicy();
			Component result = policy.getComponentAfter(aContainer, aComponent);
			if(result == policy.getFirstComponent(aContainer))
			{
				return table;
			}
			else if (result != null)
			{
				return result;
			}

			if(aContainer == aComponent)
			{
				// prevent useless warning
				return null;
			}
			/*
			Attention, sucking code above!
			*/

			if(logger.isWarnEnabled()) logger.warn("Moving focus forward was not explicitly handled.\ncontainer={}\ncomponent={}", aContainer, aComponent);

			return null;
		}

		public Component getComponentBefore(Container aContainer, Component aComponent)
		{
			if(aComponent.equals(messagePane))
			{
				return table;
			}
			if(aComponent.equals(findPanel))
			{
				return messagePane;
			}
			if(aComponent.equals(table))
			{
				if(isShowingFilters())
				{
					/*
					Attention, sucking code below!
					*/
					if(logger.isDebugEnabled()) logger.debug("Performing FocusTraversal-Voodoo...");

					FocusTraversalPolicy policy = findPanel.getFocusTraversalPolicy();
					return policy.getDefaultComponent(aContainer);
					/*
					Attention, sucking code above!
					*/
				}
				return messagePane;
			}

			/*
			Attention, sucking code below!
			*/
			if(logger.isDebugEnabled()) logger.debug("Performing FocusTraversal-Yaddayadda...");

			Component c = resolveComponent(aComponent);
			if(table.equals(c))
			{
				if(isShowingFilters())
				{
					return findPanel;
				}
				return messagePane;
			}
			if(messagePane.equals(c))
			{
				return table;
			}

			FocusTraversalPolicy policy = findPanel.getFocusTraversalPolicy();
			Component result = policy.getComponentBefore(aContainer, aComponent);
			if(result == policy.getLastComponent(aContainer))
			{
				return messagePane;
			}
			else if (result != null)
			{
				return result;
			}

			if(aContainer == aComponent)
			{
				// prevent useless warning
				return null;
			}
			/*
			Attention, sucking code above!
			*/

			if(logger.isWarnEnabled()) logger.warn("Moving focus backward was not explicitly handled.\ncontainer={}\ncomponent={}", aContainer, aComponent);

			return null;
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
			messagePane.setScale(scale); // this fixes a bug
		}
		catch(Throwable t)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while setting message {}!", message, t);
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
			messagePane.setScale(scale); // this fixes a bug
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
			IOUtilities.closeQuietly(fos);
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
		Condition condition=findPanel.getCondition();
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

	public void findPrevious()
	{
		findPrevious(getSelectedRow(), getFilterCondition());
	}

	public void findPrevious(int currentRow, Condition condition)
	{
		if(condition != null)
		{
			ProgressingCallable<Long> callable = new FindPreviousCallable<T>(this, currentRow, condition);
			executeFind(callable, "Find previous", currentRow, condition);
		}
	}

	public void findNext()
	{
		findNext(getSelectedRow(), getFilterCondition());
	}

	public void findNext(int currentRow, Condition condition)
	{
		if(condition != null)
		{
			ProgressingCallable<Long> callable = new FindNextCallable<T>(this, currentRow, condition);
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
	 * This method creates a new condition that is a combination of the current buffer condition and the given condition.
	 * The conditions are combined using "and". Duplicate condition entries are prevented.
	 *
	 * @param condition the condition to be combined with the current buffer condition.
	 * @return the combination of the given condition and the previous buffer condition.
	 */
	public Condition getCombinedCondition(Condition condition)
	{
		Condition previousCondition = getBufferCondition();

		if(previousCondition == null)
		{
			return condition;
		}

		try
		{
			// clone the previous condition so we don't change it while active
			Condition previousClone = previousCondition.clone();
			if(condition == null)
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
			if(!conditions.contains(condition))
			{
				// don't add duplicates
				conditions.add(condition);
			}
			if(conditions.size() > 1)
			{
				if(logger.isInfoEnabled()) logger.info("Setting and-conditions: {}", conditions);
				and.setConditions(conditions);
				return and;
			}
			else
			{
				return condition;
			}
		}
		catch(CloneNotSupportedException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while cloning {}!", previousCondition, ex);
		}
		return null;
	}

	void createFilteredView()
	{
		ViewContainer<T> container = resolveContainer();
		if(container == null)
		{
			return;
		}
		Condition condition = resolveCombinedCondition();
		if(condition == null)
		{
			return;
		}
		container.addFilteredView(this, condition);
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
					else if(logger.isWarnEnabled())
					{
						logger.info("Can't show unfiltered event {} for filtered event {}...", unfilteredRow, row);
					}
				}
			}
		}
	}


	private void showPopup(Component component, Point p)
	{
		ViewContainer<T> container = resolveContainer();
		if(container != null)
		{
			ViewWindow viewWindow = container.resolveViewWindow();
			if(viewWindow != null)
			{
				ViewActions viewActions = viewWindow.getViewActions();
				JPopupMenu popup = viewActions.getPopupMenu();
				if(logger.isDebugEnabled()) logger.debug("Show popup at {}.", p);
				popup.show(component, p.x, p.y);
			}
			else
			{
				if(logger.isWarnEnabled()) logger.warn("Couldn't resolve viewWindow of viewContainer {}!", container);
			}
		}
		else
		{
			if(logger.isWarnEnabled()) logger.warn("Couldn't resolve viewContainer of viewPanel {}!", this);
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
					Point p = evt.getPoint();
					EventWrapper<T> wrapper = getEventWrapper(p); // To ensure that the event below the mouse is selected.
					if(logger.isDebugEnabled()) logger.debug("Show unfiltered event {}.", wrapper);
					showUnfilteredEvent();
				}
			}
		}


		private void showPopup(MouseEvent evt)
		{
			Point p = evt.getPoint();
			EventWrapper<T> wrapper = getEventWrapper(p); // To ensure that the event below the mouse is selected.
			if(logger.isDebugEnabled()) logger.debug("Show popup at {} for event {}.", p, wrapper);
			EventWrapperViewPanel.this.showPopup(table, p);
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
			EventWrapperViewPanel.this.showPopup(messagePane, p);
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

	private class ScrollPaneMouseListener
		implements MouseListener
	{
		public ScrollPaneMouseListener()
		{
		}

		private void showPopup(MouseEvent evt)
		{
			Point p = evt.getPoint();
			EventWrapperViewPanel.this.showPopup(tableScrollPane, p);
		}

		public void mouseClicked(MouseEvent evt)
		{
			tableScrollPane.requestFocusInWindow();
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
		}
	}

	class FindPanelChangeListener
		implements PropertyChangeListener
	{
		final Logger logger = LoggerFactory.getLogger(FindPanelChangeListener.class);

		public void propertyChange(PropertyChangeEvent event)
		{
			String propertyName = event.getPropertyName();
			if(FindPanel.CONDITION_PROPERTY.equals(propertyName))
			{
				applyFilter();
			}
		}
	}

	private void executeFind(Callable<Long> callable, String name, int currentRow, Condition condition)
	{
		ViewContainer<T> container = resolveContainer();
		if(container != null)
		{
			if(container.isSearching())
			{
				return; // prevent scheduling of multiple searches...
			}
		}
		Map<String, String> metaData = CallableMetaData.createFindMetaData(condition, eventSource, currentRow);

		String description = "Executing '" + name + "'  on " +
				metaData.get(CallableMetaData.FIND_TASK_META_SOURCE_IDENTIFIER) +
				" starting at row " + currentRow + ".\n\n" +
				metaData.get(CallableMetaData.FIND_TASK_META_CONDITION);

		findPanel.enableFindComponents(false, condition);
		findResultListener.setCallable(callable);
		Task<Long> task = taskManager.startTask(callable, name, description, metaData);
		if(container != null)
		{
			container.showSearchPanel(task);
		}
	}

	private class FindResultListener
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

			findPanel.enableFindComponents(true, table.getFilterCondition());
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

	protected abstract void closeConnection(SourceIdentifier sourceIdentifier);

	/**
	 * Returns a new combined condition of this view and the current condition of its table if it differs and the table has a condition.
	 * Otherwise, null is returned.
	 *
	 * @return the combined condition
	 */
	public Condition resolveCombinedCondition()
	{
		Condition currentFilter = getTable().getFilterCondition();
		if (currentFilter == null)
		{
			return null;
		}

		Condition originalBufferCondition = getBufferCondition();

		Condition filter = getCombinedCondition(currentFilter);
		if (filter == null || filter.equals(originalBufferCondition))
		{
			return null;
		}
		return filter;
	}

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

	private class WrappingMouseWheelListener
		implements MouseWheelListener
	{

		private MouseWheelListener[] wrapped;

		private WrappingMouseWheelListener(MouseWheelListener[] wrapped)
		{
			this.wrapped = wrapped;
		}

		public void mouseWheelMoved(MouseWheelEvent e)
		{
			//noinspection MagicConstant
			if(e.getModifiers() == KeyStrokes.COMMAND_KEYMASK)
			{
				// special handling, i.e. zoom in, zoomm out
				int rotation = e.getWheelRotation();
				boolean up = false;
				if(rotation < 0)
				{
					up = true;
					rotation = -rotation;
				}
				for(int i = 0; i < rotation; i++)
				{
					if(up)
					{
						mainFrame.zoomIn();
					}
					else
					{
						mainFrame.zoomOut();
					}
				}
			}
			else
			{
				if(wrapped != null)
				{
					for(MouseWheelListener current : wrapped)
					{
						current.mouseWheelMoved(e);
					}
				}
			}
		}
	}
}

