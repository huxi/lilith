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
package de.huxhorn.lilith.swing.taskmanager;

import de.huxhorn.lilith.swing.taskmanager.table.TaskTableColumnModel;
import de.huxhorn.lilith.swing.taskmanager.table.TaskTableModel;
import de.huxhorn.sulky.swing.Tables;
import de.huxhorn.sulky.tasks.Task;
import de.huxhorn.sulky.tasks.TaskManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TaskManagerPanel<T>
	extends JPanel
{
	private final Logger logger = LoggerFactory.getLogger(TaskManagerPanel.class);

	private static final Icon CANCEL_TOOLBAR_ICON;

	static
	{
		ImageIcon icon;
		{
			URL url = TaskManagerPanel.class.getResource("/tango/16x16/actions/process-stop.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		CANCEL_TOOLBAR_ICON = icon;

	}

	private TaskTableModel<T> taskTableModel;
	private CancelTaskAction cancelAction;
	private JTable table;

	public TaskManagerPanel(TaskManager<T> taskManager)
	{
		taskTableModel = new TaskTableModel<T>(taskManager);
		setLayout(new BorderLayout());
		table = new JTable(taskTableModel);
		table.setColumnModel(new TaskTableColumnModel());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(new TaskSelectionListener());
		Tables.setAutoCreateRowSorter(table, true);

		cancelAction = new CancelTaskAction();

		JToolBar toolBar = new JToolBar();
		toolBar.add(cancelAction);

		JScrollPane tableScrollPane = new JScrollPane(table);
		add(toolBar, BorderLayout.NORTH);
		add(tableScrollPane, BorderLayout.CENTER);
	}

	public void setPaused(boolean paused)
	{
		taskTableModel.setPaused(paused);
	}

	public boolean isPaused()
	{
		return taskTableModel.isPaused();
	}

	private class TaskSelectionListener
		implements ListSelectionListener
	{

		public void valueChanged(ListSelectionEvent e)
		{
			if(e.getValueIsAdjusting()) return;

			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			if(lsm.isSelectionEmpty())
			{
				setSelectedTask(null);
			}
			else
			{
				int selectedRow = lsm.getMinSelectionIndex();

				selectedRow = Tables.convertRowIndexToModel(table, selectedRow);
				Task<T> task = taskTableModel.getValueAt(selectedRow);
				setSelectedTask(task);
			}
		}
	}

	private void setSelectedTask(Task<T> task)
	{
		if(logger.isInfoEnabled()) logger.info("Selected task {}.", task);
		cancelAction.setTask(task);
	}

	class CancelTaskAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -2004455657443480819L;

		private Task task;

		public CancelTaskAction()
		{
			super("Cancel task.");
			putValue(Action.SMALL_ICON, CANCEL_TOOLBAR_ICON);
			//KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift X");
			//if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			//putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('c'));
			setEnabled(false);
		}

		public Task getTask()
		{
			return task;
		}

		public void setTask(Task task)
		{
			this.task = task;
			setEnabled(this.task != null);
		}

		public void actionPerformed(ActionEvent e)
		{
			if(task != null)
			{
				if(logger.isInfoEnabled()) logger.info("Cancel task {}.", task);
				task.getFuture().cancel(true);
			}
		}
	}

}
