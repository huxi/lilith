package de.huxhorn.lilith.swing.taskmanager;

import de.huxhorn.lilith.swing.table.model.RowBasedTableModel;
import de.huxhorn.sulky.tasks.Task;
import de.huxhorn.sulky.tasks.TaskListener;
import de.huxhorn.sulky.tasks.TaskManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class TaskManagerPanel<T>
	extends JPanel
{
	private final Logger logger = LoggerFactory.getLogger(TaskManagerPanel.class);
	private TaskManager<T> taskManager;
	private TaskListener<T> taskListener;
	private boolean paused;
	private TaskTableModel taskTableModel;

	public TaskManagerPanel(TaskManager<T> taskManager)
	{
		taskListener = new UpdateViewTaskListener();
		paused = true;
		taskTableModel = new TaskTableModel();
		setTaskManager(taskManager);
		setLayout(new GridLayout(1, 1));
		JTable table = new JTable(taskTableModel);
		JScrollPane tableScrollPane = new JScrollPane(table);
		add(tableScrollPane);
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
				taskTableModel.initTasks();
			}
			else
			{
				taskTableModel.clearTasks();
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
			taskTableModel.initTasks();
		}
		else
		{
			taskTableModel.clearTasks();
		}
	}

	private class UpdateViewTaskListener
		implements TaskListener<T>
	{
		public void taskCreated(Task<T> task)
		{
			if(!paused)
			{
				taskTableModel.addTask(task);
			}
		}

		public void executionFailed(Task<T> task, ExecutionException exception)
		{
			if(!paused)
			{
				taskTableModel.removeTask(task);
			}
		}

		public void executionFinished(Task<T> task, T result)
		{
			if(!paused)
			{
				taskTableModel.removeTask(task);
			}
		}

		public void executionCanceled(Task<T> task)
		{
			if(!paused)
			{
				taskTableModel.removeTask(task);
			}
		}

		public void progressUpdated(Task<T> task, int progress)
		{
			if(!paused)
			{
				taskTableModel.updateTask(task);
			}
		}
	}

	public class TaskTableModel
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

		public TaskTableModel()
		{
			eventListenerList = new EventListenerList();
			this.tasks = new ArrayList<Task<T>>();
		}

		public Class<?> getColumnClass(int columnIndex)
		{
			return String.class; // TODO: Task.class
		}

		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return false;
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if(columnIndex != 0)
			{
				return null;
			}
			return "" + getValueAt(rowIndex); //TODO:
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
			return 1;
		}

		public String getColumnName(int columnIndex)
		{
			if(columnIndex == 0)
			{
				return "Task";
			}
			return null;
		}

		public Task<T> getValueAt(int row)
		{
			logger.debug("getValueAt {}", row);
			if(row >= 0 && row <= tasks.size())
			{
				Task<T> result = tasks.get(row);
				logger.debug("getValueAt {} result={}", result);
				return result;
			}
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
			Map<Long, Task<T>> taskMap = taskManager.getTasks();
			if(logger.isDebugEnabled()) logger.debug("initTasks: {}", taskMap);
			tasks.clear();
			for(Map.Entry<Long, Task<T>> current : taskMap.entrySet())
			{
				tasks.add(current.getValue());
				Collections.sort(tasks, taskComparator);
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


	}
}
