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

import de.huxhorn.sulky.swing.Tables;
import de.huxhorn.sulky.tasks.Task;
import de.huxhorn.sulky.tasks.TaskManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class TaskTable<T>
	extends JTable
{
	private final Logger logger = LoggerFactory.getLogger(TaskTable.class);
	private TaskTableModel<T> taskTableModel;

	public TaskTable(TaskManager<T> taskManager)
	{
		super();
		taskTableModel = new TaskTableModel<T>(taskManager);
		// must be added before setting model to table
		taskTableModel.addTableModelListener(new SelectFirstListener());
		setModel(taskTableModel);
		setColumnModel(new TaskTableColumnModel());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Tables.setAutoCreateRowSorter(this, true);

	}

	public void setPaused(boolean paused)
	{
		taskTableModel.setPaused(paused);
		if(!paused)
		{
			selectFirstTask();
		}
	}

	/**
	 * Selects first task if no task was previously selected.
	 */
	private void selectFirstTask()
	{
		if(getSelectedRow() < 0 && getRowCount() > 0)
		{
			selectRow(0);
		}
	}

	public boolean isPaused()
	{
		return taskTableModel.isPaused();
	}

	public TaskTableModel<T> getTaskTableModel()
	{
		return taskTableModel;
	}

	public Task<T> getTaskAt(Point p, boolean select)
	{
		int row = rowAtPoint(p);
		if(row > -1)
		{
			row = Tables.convertRowIndexToModel(this, row);

			Task<T> result = taskTableModel.getValueAt(row);
			if(result != null && select)
			{
				selectRow(row);
			}
			return result;
		}
		return null;
	}

	public void selectRow(int row)
	{
		if(row >= 0 && row < getRowCount())
		{
			if(logger.isDebugEnabled()) logger.debug("Selecting row {}.", row);
			getSelectionModel().setSelectionInterval(0, row);
			// scrollpane adjustment
			scrollRectToVisible(getCellRect(row, 0, true));
		}
	}

	public String getToolTipText(MouseEvent event)
	{
		Task<T> task = getTaskAt(event.getPoint(), false);
		if(task != null)
		{
			StringBuilder result = new StringBuilder();

			result.append("<HTML><BODY>");
			result.append("<P>").append(task.getName()).append(" (ID=").append(task.getId()).append(")</P>");
			String description = task.getDescription();
			if(description != null)
			{
				result.append("<P>").append(description).append("</P>");
			}
			Map<String, String> metaData = task.getMetaData();
			if(metaData != null && metaData.size() > 0)
			{
				result.append("<TABLE border=\"1\">");
				result.append("<TR><TH>Key</TH><TH>Value</TH></TR>");
				for(Map.Entry<String, String> current : metaData.entrySet())
				{
					result.append("<TR>");
					result.append("<TD>");
					result.append(current.getKey());
					result.append("</TD>");
					result.append("<TD>");
					result.append(current.getValue());
					result.append("</TD>");
					result.append("</TR>");
				}
				result.append("</TABLE>");
			}
			result.append("</BODY></HTML>");

			return result.toString();
		}
		return null;
	}

	private class SelectFirstListener
		implements TableModelListener
	{

		public void tableChanged(TableModelEvent tableModelEvent)
		{
			selectFirstTask();
		}
	}
}
