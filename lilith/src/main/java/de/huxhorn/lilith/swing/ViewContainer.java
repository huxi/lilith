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
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.lilith.engine.impl.EventSourceImpl;
import de.huxhorn.lilith.swing.callables.CallableMetaData;
import de.huxhorn.lilith.swing.callables.FilteringCallable;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.DisposeOperation;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.tasks.Task;
import de.huxhorn.sulky.tasks.TaskListener;
import de.huxhorn.sulky.tasks.TaskManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class ViewContainer<T extends Serializable>
		extends JPanel
		implements DisposeOperation
{
	// TODO: property change instead of change?
	public static final String SELECTED_EVENT_PROPERTY_NAME = "selectedEvent";

	private static final ImageIcon globalFrameImageIcon;
	private static final Map<LoggingViewState, ImageIcon> frameIconImages;

	static
	{
		URL url = EventWrapperViewPanel.class.getResource("/tango/16x16/categories/applications-internet.png");
		if (url != null)
		{
			globalFrameImageIcon = new ImageIcon(url);
		}
		else
		{
			globalFrameImageIcon = null;
		}
		frameIconImages = new HashMap<LoggingViewState, ImageIcon>();
		url = EventWrapperViewPanel.class.getResource("/tango/16x16/status/network-receive.png");
		if (url != null)
		{
			frameIconImages.put(LoggingViewState.ACTIVE, new ImageIcon(url));
		}
		url = EventWrapperViewPanel.class.getResource("/tango/16x16/status/network-offline.png");
		if (url != null)
		{
			frameIconImages.put(LoggingViewState.INACTIVE, new ImageIcon(url));
		}
	}

	private final List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	private EventWrapperViewPanel<T> defaultView;
	private MainFrame mainFrame;
	private TaskManager<Long> taskManager;
	private Map<Callable<Long>, EventWrapperViewPanel<T>> filterMapping;
	private FilterTaskListener filterTaskListener;
	private EventSource<T> eventSource;
	private ProgressGlassPane progressPanel;
	private Component prevGlassPane;
	private boolean searching;

	public ViewContainer(MainFrame mainFrame, EventSource<T> eventSource)
	{
		this.mainFrame = mainFrame;
		this.eventSource = eventSource;
		taskManager = mainFrame.getLongWorkManager();
		progressPanel = new ProgressGlassPane();
		filterMapping = new HashMap<Callable<Long>, EventWrapperViewPanel<T>>();
		filterTaskListener = new FilterTaskListener();
		taskManager.addTaskListener(filterTaskListener);
		this.defaultView = createViewPanel(eventSource);
		defaultView.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (EventWrapperViewPanel.STATE_PROPERTY.equals(evt.getPropertyName()))
				{
					updateContainerIcon();
				}
			}
		});

	}

	public EventSource<T> getEventSource()
	{
		return eventSource;
	}

	public MainFrame getMainFrame()
	{
		return mainFrame;
	}

	protected abstract EventWrapperViewPanel<T> createViewPanel(EventSource<T> eventSource);

	public abstract EventWrapperViewPanel<T> getViewAt(int index);

	public abstract EventWrapperViewPanel<T> getSelectedView();

	public abstract void addView(EventWrapperViewPanel<T> view);

	public abstract void removeView(EventWrapperViewPanel<T> view, boolean dispose);

	public abstract void showDefaultView();

	public abstract Class getWrappedClass();

	public EventWrapperViewPanel<T> getDefaultView()
	{
		return defaultView;
	}

	public void dispose()
	{
		taskManager.removeTaskListener(filterTaskListener);
	}

	/**
	 * Returns a new combined condition of the given view and its table if it differs from the current buffer condition.
	 * Otherwise, null is returned.
	 *
	 * @param original the original view
	 * @return the combined condition
	 */
	protected Condition resolveCombinedCondition(EventWrapperViewPanel<T> original)
	{
		Condition currentFilter = original.getTable().getFilterCondition();
		if (currentFilter == null)
		{
			return null;
		}

		Condition previousClone = original.getBufferCondition();

		Condition filter = original.getCombinedCondition();
		if (filter == null || filter.equals(previousClone))
		{
			return null;
		}
		return filter;
	}

	public void addFilteredView(EventWrapperViewPanel<T> original)
	{
		Condition filter = resolveCombinedCondition(original);
		if (filter == null)
		{
			return;
		}
		Buffer<EventWrapper<T>> originalBuffer = original.getSourceBuffer();
		FilteringBuffer<EventWrapper<T>> filteredBuffer = new FilteringBuffer<EventWrapper<T>>(originalBuffer, filter);
		FilteringCallable<EventWrapper<T>> callable = new FilteringCallable<EventWrapper<T>>(filteredBuffer, 500);
		EventSource<T> originalEventSource = original.getEventSource();
		Map<String, String> metaData = CallableMetaData.createFilteringMetaData(filter, originalEventSource);

		EventSourceImpl<T> newEventSource = new EventSourceImpl<T>(originalEventSource.getSourceIdentifier(), filteredBuffer, filter, originalEventSource.isGlobal());
		EventWrapperViewPanel<T> newViewPanel = createViewPanel(newEventSource);
		filterMapping.put(callable, newViewPanel);
		addView(newViewPanel);
		taskManager.startTask(callable, "Filtering", "Filtering " + metaData
				.get(CallableMetaData.FIND_TASK_META_SOURCE_IDENTIFIER)
				+ " on condition " + metaData.get(CallableMetaData.FIND_TASK_META_CONDITION) + ".", metaData);
	}

	public void replaceFilteredView(EventWrapperViewPanel<T> original)
	{
		Condition filter = resolveCombinedCondition(original);
		if (filter == null)
		{
			return;
		}

		EventSource<T> eventSource = original.getEventSource();

		Buffer<EventWrapper<T>> buffer = eventSource.getBuffer();

		if (buffer instanceof FilteringBuffer)
		{
			// replace
			Callable<Long> found = null;
			for (Map.Entry<Callable<Long>, EventWrapperViewPanel<T>> current : filterMapping.entrySet())
			{
				if (current.getValue() == original)
				{
					found = current.getKey();
					break;
				}
			}
			if (found != null)
			{
				// remove previous and cancel the task
				filterMapping.remove(found);
				Task<Long> task = taskManager.getTaskByCallable(found);
				if (task != null)
				{
					task.getFuture().cancel(true);
				}
				// create new EventSource
				Buffer<EventWrapper<T>> originalBuffer = original.getSourceBuffer();
				FilteringBuffer<EventWrapper<T>> filteredBuffer = new FilteringBuffer<EventWrapper<T>>(originalBuffer, filter);
				FilteringCallable<EventWrapper<T>> callable = new FilteringCallable<EventWrapper<T>>(filteredBuffer, 500);
				EventSource<T> originalEventSource = original.getEventSource();
				Map<String, String> metaData = CallableMetaData.createFilteringMetaData(filter, originalEventSource);

				EventSourceImpl<T> newEventSource = new EventSourceImpl<T>(originalEventSource.getSourceIdentifier(), filteredBuffer, filter, originalEventSource.isGlobal());
				original.setEventSource(newEventSource);
				// restore mapping of original view, this time with the new callable
				filterMapping.put(callable, original);
				// start the new task.
				taskManager.startTask(callable, "Filtering", "Filtering " + metaData
						.get(CallableMetaData.FIND_TASK_META_SOURCE_IDENTIFIER)
						+ " on condition " + metaData.get(CallableMetaData.FIND_TASK_META_CONDITION) + ".", metaData);
			}
		}
		else
		{
			// create new
			addFilteredView(original);
		}

	}

	public ViewWindow resolveViewWindow()
	{
		Container parent = getParent();
		while (parent != null && !(parent instanceof ViewWindow))
		{
			parent = parent.getParent();
		}
		return (ViewWindow) parent;
	}

	private void updateContainerIcon()
	{
		ViewWindow window = resolveViewWindow();
		if (window instanceof JFrame)
		{
			JFrame frame = (JFrame) window;
			updateFrameIcon(frame);
		}
		else if (window instanceof JInternalFrame)
		{
			JInternalFrame frame = (JInternalFrame) window;
			updateInternalFrameIcon(frame);
		}
	}

	private static ImageIcon resolveIconForState(LoggingViewState state)
	{
		ImageIcon result = globalFrameImageIcon;
		if (state != null)
		{
			result = frameIconImages.get(state);
		}
		return result;
	}

	private void updateFrameIcon(JFrame frame)
	{
		ImageIcon frameImageIcon = resolveIconForState(defaultView.getState());

		if (frameImageIcon != null)
		{
			frame.setIconImage(frameImageIcon.getImage());
		}
	}

	private void updateInternalFrameIcon(JInternalFrame iframe)
	{
		ImageIcon frameImageIcon = resolveIconForState(defaultView.getState());

		if (frameImageIcon != null)
		{
			iframe.setFrameIcon(frameImageIcon);
			iframe.repaint(); // Apple L&F Bug workaround
		}
	}

	public void addNotify()
	{
		super.addNotify();
		updateContainerIcon();
	}

	public void addChangeListener(ChangeListener listener)
	{
		boolean changed = false;
		synchronized (changeListeners)
		{
			if (!changeListeners.contains(listener))
			{
				changeListeners.add(listener);
				changed = true;
			}
		}
		if (changed)
		{
			fireChange();
		}
	}

	public void removeChangeListener(ChangeListener listener)
	{
		boolean changed = false;
		synchronized (changeListeners)
		{
			if (changeListeners.contains(listener))
			{
				changeListeners.remove(listener);
				changed = true;
			}
		}
		if (changed)
		{
			fireChange();
		}
	}

	public void fireChange()
	{
		ArrayList<ChangeListener> clone;
		synchronized (changeListeners)
		{
			clone = new ArrayList<ChangeListener>(changeListeners);
		}
		ChangeEvent event = new ChangeEvent(this);
		for (ChangeListener listener : clone)
		{
			listener.stateChanged(event);
		}
	}

	public abstract void updateViewScale(double scale);

	class FilterTaskListener
			implements TaskListener<Long>
	{
		private final Logger logger = LoggerFactory.getLogger(FilterTaskListener.class);

		public void taskCreated(Task<Long> longTask)
		{

		}

		public void executionFailed(Task<Long> task, ExecutionException exception)
		{
			EventWrapperViewPanel<T> view = filterMapping.get(task.getCallable());
			if (view != null)
			{
				if (logger.isInfoEnabled()) logger.info("Filter execution failed!", exception);
				finished(view);
			}
		}

		public void executionFinished(Task<Long> task, Long result)
		{
			EventWrapperViewPanel<T> view = filterMapping.get(task.getCallable());
			if (view != null)
			{
				if (logger.isInfoEnabled()) logger.info("Filter execution finished: {}!", result);
				finished(view);
			}
		}

		public void executionCanceled(Task<Long> task)
		{
			EventWrapperViewPanel<T> view = filterMapping.get(task.getCallable());
			if (view != null)
			{
				if (logger.isInfoEnabled()) logger.info("Filter execution canceled.");
				finished(view);
			}
		}

		public void progressUpdated(Task<Long> task, int progress)
		{
		}

		private void finished(EventWrapperViewPanel<T> view)
		{
			if (logger.isDebugEnabled()) logger.debug("Executing FilterTaskListener.finished().");

			removeView(view, true);
		}
	}

	public abstract void closeCurrentFilter();

	public abstract void closeOtherFilters();

	public abstract void closeAllFilters();

	public abstract int getViewCount();

	public abstract void setViewIndex(int newView);

	public abstract int getViewIndex();

	public boolean isSearching()
	{
		return searching;
	}

	public void cancelSearching()
	{
		progressPanel.getFindCancelAction().actionPerformed(null);

	}

	public void hideSearchPanel()
	{
		if(searching)
		{
			searching = false;
			ViewWindow window = resolveViewWindow();
			if(window != null && prevGlassPane != null)
			{
				window.setGlassPane(prevGlassPane);
				prevGlassPane = null;
				fireChange();
			}
		}
	}

	public void showSearchPanel(Task<Long> task)
	{
		if(task != null)
		{
			searching = true;
			progressPanel.setProgress(0);
			progressPanel.getFindCancelAction().setTask(task);

			ViewWindow window = resolveViewWindow();
			if(window != null)
			{
				prevGlassPane = window.getGlassPane();
				window.setGlassPane(progressPanel);
				progressPanel.setVisible(true);
			}

			fireChange();
		}
	}

	public ProgressGlassPane getProgressPanel()
	{
		return progressPanel;
	}

	public abstract EventWrapper<T> getSelectedEvent();

	public abstract void updateViews();

	public abstract void scrollToEvent();
}
