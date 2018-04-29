/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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
import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.lilith.engine.impl.EventSourceImpl;
import de.huxhorn.lilith.swing.callables.CallableMetaData;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.DisposeOperation;
import de.huxhorn.sulky.buffers.Flush;
import de.huxhorn.sulky.buffers.FlushOperation;
import de.huxhorn.sulky.buffers.filtering.FilteringBuffer;
import de.huxhorn.sulky.buffers.filtering.FilteringCallable;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.tasks.ProgressingCallable;
import de.huxhorn.sulky.tasks.Task;
import de.huxhorn.sulky.tasks.TaskListener;
import de.huxhorn.sulky.tasks.TaskManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ViewContainer<T extends Serializable>
		extends JPanel
		implements DisposeOperation, FlushOperation
{
	private static final long serialVersionUID = 4834209079953596930L;

	// TODO: property change instead of change?
	static final String SELECTED_EVENT_PROPERTY_NAME = "selectedEvent";


	private final List<ChangeListener> changeListeners = new LinkedList<>();
	private final MainFrame mainFrame;
	private final EventSource<T> eventSource;
	private final EventWrapperViewPanel<T> defaultView;
	private final TaskManager<Long> taskManager;
	private final Map<Callable<Long>, EventWrapperViewPanel<T>> filterMapping;
	private final FilterTaskListener filterTaskListener;
	private final ProgressGlassPane progressPanel;
	private Component prevGlassPane;
	private boolean searching;
	private ProgressingCallable<Long> updateCallable;

	public ViewContainer(MainFrame mainFrame, EventSource<T> eventSource)
	{
		this.mainFrame = Objects.requireNonNull(mainFrame, "mainFrame must not be null!");
		this.eventSource = Objects.requireNonNull(eventSource, "eventSource must not be null!");
		taskManager = mainFrame.getLongWorkManager();
		progressPanel = new ProgressGlassPane();
		filterMapping = new HashMap<>();
		filterTaskListener = new FilterTaskListener();
		taskManager.addTaskListener(filterTaskListener);
		this.defaultView = createViewPanel(eventSource);
		defaultView.addPropertyChangeListener(evt -> {
			if (EventWrapperViewPanel.STATE_PROPERTY.equals(evt.getPropertyName()))
			{
				updateContainerIcon();
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

	EventWrapperViewPanel<T> getDefaultView()
	{
		return defaultView;
	}

	void setScrollingSmoothly(boolean scrollingSmoothly)
	{
		defaultView.setScrollingSmoothly(scrollingSmoothly);
		for (EventWrapperViewPanel<T> viewPanel : filterMapping.values())
		{
			viewPanel.setScrollingSmoothly(scrollingSmoothly);
		}
	}

	LoggingViewState getState()
	{
		if(defaultView != null)
		{
			return defaultView.getState();
		}
		return null;
	}

	@Override
	public void dispose()
	{
		taskManager.removeTaskListener(filterTaskListener);
		cancelUpdateTask();
	}

	@Override
	public void flush()
	{
		for(int i=0;i<getViewCount();i++)
		{
			Flush.flush(getViewAt(i));
		}
	}

	public void applyCondition(Condition condition, ActionEvent e)
	{
		if(condition == null)
		{
			// simply do nothing
			return;
		}
		EventWrapperViewPanel<T> selectedView = getSelectedView();
		if(selectedView == null)
		{
			// simply do nothing
			return;
		}

		Condition previousCondition = selectedView.getBufferCondition();

		Condition filter = selectedView.getCombinedCondition(condition);
		if (filter == null || filter.equals(previousCondition))
		{
			return;
		}

		ApplicationPreferences applicationPreferences = getMainFrame().getApplicationPreferences();
		if(applicationPreferences.isReplacingOnApply(e))
		{
			replaceFilteredView(selectedView, filter);
		}
		else
		{
			addFilteredView(selectedView, filter);
		}
	}

	void addFilteredView(EventWrapperViewPanel<T> original, Condition filter)
	{
		Buffer<EventWrapper<T>> originalBuffer = original.getSourceBuffer();
		FilteringBuffer<EventWrapper<T>> filteredBuffer = new FilteringBuffer<>(originalBuffer, filter);
		FilteringCallable<EventWrapper<T>> callable = new FilteringCallable<>(filteredBuffer, 500);
		EventSource<T> originalEventSource = original.getEventSource();
		Map<String, String> metaData = CallableMetaData.createFilteringMetaData(filter, originalEventSource);

		EventSourceImpl<T> newEventSource = new EventSourceImpl<>(originalEventSource.getSourceIdentifier(), filteredBuffer, filter, originalEventSource.isGlobal());
		EventWrapperViewPanel<T> newViewPanel = createViewPanel(newEventSource);
		filterMapping.put(callable, newViewPanel);
		addView(newViewPanel);
		taskManager.startTask(callable, "Filtering", createFilteringMessage(metaData), metaData);
	}

	void replaceFilteredView(EventWrapperViewPanel<T> original, Condition filter)
	{
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
				FilteringBuffer<EventWrapper<T>> filteredBuffer = new FilteringBuffer<>(originalBuffer, filter);
				FilteringCallable<EventWrapper<T>> callable = new FilteringCallable<>(filteredBuffer, 500);
				EventSource<T> originalEventSource = original.getEventSource();
				Map<String, String> metaData = CallableMetaData.createFilteringMetaData(filter, originalEventSource);

				EventSourceImpl<T> newEventSource = new EventSourceImpl<>(originalEventSource.getSourceIdentifier(), filteredBuffer, filter, originalEventSource.isGlobal());
				original.setEventSource(newEventSource);
				// restore mapping of original view, this time with the new callable
				filterMapping.put(callable, original);
				// start the new task.
				taskManager.startTask(callable, "Filtering", createFilteringMessage(metaData), metaData);
			}
		}
		else
		{
			// create new
			addFilteredView(original, filter);
		}

	}

	private static String createFilteringMessage(Map<String, String> metaData)
	{
		return "Filtering " + metaData.get(CallableMetaData.FIND_TASK_META_SOURCE_IDENTIFIER) + ".\n\n" +
				metaData.get(CallableMetaData.FIND_TASK_META_CONDITION);
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


	private void updateFrameIcon(JFrame frame)
	{
		frame.setIconImages(Icons.resolveFrameIconImages(defaultView.getState(), false));
	}

	private void updateInternalFrameIcon(JInternalFrame internalFrame)
	{
		internalFrame.setFrameIcon(Icons.resolveFrameIcon(defaultView.getState(), false));
		internalFrame.repaint(); // Apple L&F Bug workaround
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
		updateContainerIcon();
	}

	void addChangeListener(ChangeListener listener)
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

	void removeChangeListener(ChangeListener listener)
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
			clone = new ArrayList<>(changeListeners);
		}
		ChangeEvent event = new ChangeEvent(this);
		for (ChangeListener listener : clone)
		{
			listener.stateChanged(event);
		}
	}

	public void setPreviousSearchStrings(List<String> previousSearchStrings)
	{
		for(int i=0;i<getViewCount();i++)
		{
			EventWrapperViewPanel<T> view = getViewAt(i);
			view.setPreviousSearchStrings(previousSearchStrings);
		}
	}

	public void setConditionNames(List<String> conditionNames)
	{
		for(int i=0;i<getViewCount();i++)
		{
			EventWrapperViewPanel<T> view = getViewAt(i);
			view.setConditionNames(conditionNames);
		}
	}

	public abstract void updateViewScale(double scale);

	public abstract void setShowingStatusBar(boolean showingStatusBar);

	void setUpdateCallable(ProgressingCallable<Long> updateCallable)
	{
		cancelUpdateTask();
		this.updateCallable=updateCallable;
		if(this.updateCallable != null)
		{
			taskManager.startTask(this.updateCallable, "Updating: "+this.updateCallable);
			getDefaultView().setState(LoggingViewState.UPDATING_FILE);
		}
	}

	private void cancelUpdateTask()
	{
		if(this.updateCallable != null)
		{
			Task<Long> task = taskManager.getTaskByCallable(this.updateCallable);
			if(task != null)
			{
				task.getFuture().cancel(true);
			}
			this.updateCallable=null;
			getDefaultView().setState(LoggingViewState.STALE_FILE);
		}
	}

	class FilterTaskListener
			implements TaskListener<Long>
	{
		private final Logger logger = LoggerFactory.getLogger(FilterTaskListener.class);

		@Override
		public void taskCreated(Task<Long> longTask)
		{
			// no-op
		}

		@Override
		public void executionFailed(Task<Long> task, ExecutionException exception)
		{
			EventWrapperViewPanel<T> view = filterMapping.get(task.getCallable());
			if (view != null)
			{
				if (logger.isInfoEnabled()) logger.info("Filter execution failed!", exception);
				finished(view);
			}
			if(task.getCallable() == ViewContainer.this.updateCallable)
			{
				cancelUpdateTask();
			}
		}

		@Override
		public void executionFinished(Task<Long> task, Long result)
		{
			EventWrapperViewPanel<T> view = filterMapping.get(task.getCallable());
			if (view != null)
			{
				if (logger.isInfoEnabled()) logger.info("Filter execution finished: {}!", result);
				finished(view);
			}
			if(task.getCallable() == ViewContainer.this.updateCallable)
			{
				cancelUpdateTask();
			}
		}

		@Override
		public void executionCanceled(Task<Long> task)
		{
			EventWrapperViewPanel<T> view = filterMapping.get(task.getCallable());
			if (view != null)
			{
				if (logger.isInfoEnabled()) logger.info("Filter execution canceled.");
				finished(view);
			}
			if(task.getCallable() == ViewContainer.this.updateCallable)
			{
				cancelUpdateTask();
			}
		}

		@Override
		public void progressUpdated(Task<Long> task, int progress)
		{
			// no-op
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

	boolean isSearching()
	{
		return searching;
	}

	void cancelSearching()
	{
		progressPanel.getFindCancelAction().actionPerformed(null);

	}

	void hideSearchPanel()
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

	void showSearchPanel(Task<Long> task)
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

	ProgressGlassPane getProgressPanel()
	{
		return progressPanel;
	}

	public abstract EventWrapper<T> getSelectedEvent();

	public abstract void updateViews();

	public abstract void scrollToEvent();
}
