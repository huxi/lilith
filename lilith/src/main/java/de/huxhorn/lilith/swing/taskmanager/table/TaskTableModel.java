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
package de.huxhorn.lilith.swing.taskmanager.table;

import de.huxhorn.lilith.swing.table.model.RowBasedTableModel;
import de.huxhorn.sulky.tasks.Task;
import de.huxhorn.sulky.tasks.TaskListener;
import de.huxhorn.sulky.tasks.TaskManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class TaskTableModel<T>
	implements RowBasedTableModel<Task<T>>
{
	private final Logger logger = LoggerFactory.getLogger(TaskTableModel.class);

	private final List<Task<T>> tasks;
	private final EventListenerList eventListenerList;
	private Comparator<Task<T>> taskComparator = new Comparator<Task<T>>()
	{
		public int compare(Task<T> task1, Task<T> task2)
		{
			return (int) (task1.getId() - task2.getId());
		}
	};

	public static final int ID_INDEX = 0;
	public static final int NAME_INDEX = 1;
	public static final int PROGRESS_INDEX = 2;

	private static final Class[] COLUMN_CLASSES =
		{
			Long.class,
			String.class,
			Integer.class
		};

	private static final String[] COLUMN_NAMES =
		{
			"ID",
			"Name",
			"Progress"
		};

	private TaskManager<T> taskManager;
	private boolean paused;
	private TaskListener<T> taskListener;


	public TaskTableModel(TaskManager<T> taskManager)
	{
		this.tasks = new ArrayList<Task<T>>();
		this.paused = true;
		this.eventListenerList = new EventListenerList();
		this.taskListener = new UpdateViewTaskListener();

		setTaskManager(taskManager);
	}

	public Class<?> getColumnClass(int columnIndex)
	{
		if(columnIndex >= 0 && columnIndex < COLUMN_CLASSES.length)
		{
			return COLUMN_CLASSES[columnIndex];
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Task<T> task = getValueAt(rowIndex);
		if(task == null)
		{
			return null;
		}
		switch(columnIndex)
		{
			case ID_INDEX:
				return task.getId();
			case NAME_INDEX:
				return task.getName();
			case PROGRESS_INDEX:
				return task.getProgress();
		}
		return null;
	}

	public void setValueAt(Object o, int rowIndex, int columnIndex)
	{
	}

	public void addTableModelListener(TableModelListener l)
	{
		synchronized(eventListenerList)
		{
			eventListenerList.add(TableModelListener.class, l);
		}
	}

	public void removeTableModelListener(TableModelListener l)
	{
		synchronized(eventListenerList)
		{
			eventListenerList.remove(TableModelListener.class, l);
		}
	}

	public int getRowCount()
	{
		return tasks.size();
	}

	public int getColumnCount()
	{
		return COLUMN_CLASSES.length;
	}

	public String getColumnName(int columnIndex)
	{
		if(columnIndex >= 0 && columnIndex < COLUMN_NAMES.length)
		{
			return COLUMN_NAMES[columnIndex];
		}
		return null;
	}

	public Task<T> getValueAt(int row)
	{
		logger.debug("getValueAt {}", row);
		if(row >= 0 && row < tasks.size())
		{
			Task<T> result = tasks.get(row);
			logger.debug("getValueAt {} result={}", result);
			return result;
		}
		logger.debug("getValueAt {} is null!", row);
		return null;
	}

	private void fireTableChanged(TableModelEvent event)
	{
		Object[] listeners;
		synchronized(eventListenerList)
		{
			listeners = eventListenerList.getListenerList();
		}
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for(int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if(listeners[i] == TableModelListener.class)
			{
				TableModelListener listener = ((TableModelListener) listeners[i + 1]);
				if(logger.isDebugEnabled())
				{
					logger.debug("Firing TableChange at {}.", listener.getClass().getName());
				}
				try
				{
					listener.tableChanged(event);
				}
				catch(Throwable ex)
				{
					if(logger.isWarnEnabled()) logger.warn("Exception while firing change!", ex);
				}
			}
		}
	}

	private void initTasks()
	{
		tasks.clear();
		if(taskManager != null)
		{
			Map<Long, Task<T>> taskMap = taskManager.getTasks();
			if(logger.isDebugEnabled()) logger.debug("initTasks: {}", taskMap);
			for(Map.Entry<Long, Task<T>> current : taskMap.entrySet())
			{
				tasks.add(current.getValue());
				Collections.sort(tasks, taskComparator);
			}
		}
		fireTableChanged(new TableModelEvent(this));
	}

	private void clearTasks()
	{
		if(logger.isDebugEnabled()) logger.debug("clearTasks");
		tasks.clear();
		fireTableChanged(new TableModelEvent(this));
	}

	private void addTask(Task<T> task)
	{
		tasks.add(task);
		int index = tasks.size() - 1;
		fireTableChanged(new TableModelEvent(this, index, index, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

	private void removeTask(Task<T> task)
	{
		int index = tasks.indexOf(task);
		tasks.remove(index);
		fireTableChanged(new TableModelEvent(this, index, index, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
	}

	private void updateTask(Task<T> task)
	{
		int index = tasks.indexOf(task);
		fireTableChanged(new TableModelEvent(this, index, index, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
	}

	public boolean isPaused()
	{
		return paused;
	}

	public void setPaused(boolean paused)
	{
		if(this.paused != paused)
		{
			if(!paused)
			{
				initTasks();
			}
			else
			{
				clearTasks();
			}
			this.paused = paused;
		}

	}

	public TaskManager<?> getTaskManager()
	{
		return taskManager;
	}

	public void setTaskManager(TaskManager<T> taskManager)
	{
		if(this.taskManager != null)
		{
			this.taskManager.removeTaskListener(taskListener);
		}
		this.taskManager = taskManager;
		if(this.taskManager != null)
		{
			this.taskManager.addTaskListener(taskListener);
		}
		if(!paused)
		{
			initTasks();
		}
		else
		{
			clearTasks();
		}
	}

	private class UpdateViewTaskListener
		implements TaskListener<T>
	{
		public void taskCreated(Task<T> task)
		{
			if(!paused)
			{
				addTask(task);
			}
		}

		public void executionFailed(Task<T> task, ExecutionException exception)
		{
			if(!paused)
			{
				removeTask(task);
			}
		}

		public void executionFinished(Task<T> task, T result)
		{
			if(!paused)
			{
				removeTask(task);
			}
		}

		public void executionCanceled(Task<T> task)
		{
			if(!paused)
			{
				removeTask(task);
			}
		}

		public void progressUpdated(Task<T> task, int progress)
		{
			if(!paused)
			{
				updateTask(task);
			}
		}
	}
}
