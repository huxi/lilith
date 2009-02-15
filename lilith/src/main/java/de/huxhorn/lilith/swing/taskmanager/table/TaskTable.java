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

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.*;

public class TaskTable<T>
	extends JTable
{
	private TaskTableModel<T> taskTableModel;

	public TaskTable(TaskManager<T> taskManager)
	{
		super();
		taskTableModel = new TaskTableModel<T>(taskManager);
		setModel(taskTableModel);
		setColumnModel(new TaskTableColumnModel());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Tables.setAutoCreateRowSorter(this, true);


	}

	public void setPaused(boolean paused)
	{
		taskTableModel.setPaused(paused);
	}

	public boolean isPaused()
	{
		return taskTableModel.isPaused();
	}

	public TaskTableModel<T> getTaskTableModel()
	{
		return taskTableModel;
	}

	public String getToolTipText(MouseEvent event)
	{
		Point p = event.getPoint();
		int row = rowAtPoint(p);
		if(row > -1)
		{
			row = Tables.convertRowIndexToModel(this, row);
			Task<T> task = taskTableModel.getValueAt(row);
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
		}
		return null;
	}
}
