package de.huxhorn.lilith.swing.taskmanager;

import de.huxhorn.lilith.swing.table.model.RowBasedTableModel;
import de.huxhorn.sulky.tasks.Task;
import de.huxhorn.sulky.tasks.TaskListener;
import de.huxhorn.sulky.tasks.TaskManager;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelListener;

public class TaskManagerPanel
	extends JPanel
{
	private TaskManager<Long> taskManager;
	private TaskListener<Long> taskListener;

	public TaskManagerPanel(TaskManager<Long> taskManager)
	{
		taskListener = new UpdateViewTaskListener();
		this.taskManager = taskManager;

	}

	public TaskManager<?> getTaskManager()
	{
		return taskManager;
	}

	public void setTaskManager(TaskManager<Long> taskManager)
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
		initTasks();
	}

	private void addTask(Task<Long> task)
	{
		//To change body of created methods use File | Settings | File Templates.
	}

	private void removeTask(Task<Long> task)
	{
		//To change body of created methods use File | Settings | File Templates.
	}

	private void updateTask(Task<Long> task)
	{
		//To change body of created methods use File | Settings | File Templates.
	}

	private void initTasks()
	{
		//To change body of created methods use File | Settings | File Templates.
	}

	private class UpdateViewTaskListener
		implements TaskListener<Long>
	{
		public void taskCreated(Task<Long> task)
		{
			addTask(task);
		}

		public void executionFailed(Task<Long> task, ExecutionException exception)
		{
			removeTask(task);
		}

		public void executionFinished(Task<Long> task, Long result)
		{
			removeTask(task);
		}

		public void executionCanceled(Task<Long> task)
		{
			removeTask(task);
		}

		public void progressUpdated(Task<Long> task, int progress)
		{
			updateTask(task);
		}
	}

	public class TaskTableModel
		implements RowBasedTableModel<Task>
	{
		private List<Task> tasks;
		private final EventListenerList eventListenerList;


		public TaskTableModel(List<Task> tasks)
		{
			eventListenerList = new EventListenerList();
			this.tasks = tasks;
		}

		public Class<?> getColumnClass(int columnIndex)
		{
			return Task.class;
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
			return getValueAt(rowIndex);
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
			if(tasks == null)
			{
				return 0;
			}
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

		public Task getValueAt(int row)
		{
			if(tasks != null && row >= 0 && row <= tasks.size())
			{
				return tasks.get(row);
			}
			return null;
		}
	}
}
