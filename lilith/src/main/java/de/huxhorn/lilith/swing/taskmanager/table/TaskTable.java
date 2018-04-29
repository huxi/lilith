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

package de.huxhorn.lilith.swing.taskmanager.table;

import de.huxhorn.lilith.swing.TextPreprocessor;
import de.huxhorn.sulky.tasks.Task;
import de.huxhorn.sulky.tasks.TaskManager;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskTable<T>
	extends JTable
{
	private static final long serialVersionUID = -7788744990458100395L;

	private final Logger logger = LoggerFactory.getLogger(TaskTable.class);
	private final TaskTableModel<T> taskTableModel;

	public TaskTable(TaskManager<T> taskManager)
	{
		super();
		taskTableModel = new TaskTableModel<>(taskManager);
		// must be added before setting model to table
		taskTableModel.addTableModelListener(new SelectFirstListener());
		setModel(taskTableModel);
		setColumnModel(new TaskTableColumnModel());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setAutoCreateRowSorter(true);
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

	public TaskTableModel<T> getTaskTableModel()
	{
		return taskTableModel;
	}

	public Task<T> getTaskAt(Point p, boolean select)
	{
		int row = rowAtPoint(p);
		if(row > -1)
		{
			row = convertRowIndexToModel(row);

			Task<T> result = taskTableModel.getValueAt(row);
			if(result != null && select)
			{
				selectRow(row);
			}
			return result;
		}
		return null;
	}

	private void selectRow(int row)
	{
		if(row >= 0 && row < getRowCount())
		{
			if(logger.isDebugEnabled()) logger.debug("Selecting row {}.", row);
			getSelectionModel().setSelectionInterval(0, row);
			// scrollpane adjustment
			scrollRectToVisible(getCellRect(row, 0, true));
		}
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public String getToolTipText(MouseEvent event)
	{
		if(event == null)
		{
			return null;
		}
		Task<T> task = getTaskAt(event.getPoint(), false);
		if(task != null)
		{
			StringBuilder result = new StringBuilder();

			result.append("<html><body><p>").append(task.getName()).append(" (ID=").append(task.getId()).append(")</p>");
			String description = task.getDescription();
			if(description != null)
			{
				result.append("<p>").append(TextPreprocessor.wrapWithPre(TextPreprocessor.cropTextBlock(description))).append("</p>");
			}
			Map<String, String> metaData = task.getMetaData();
			if(metaData != null && !metaData.isEmpty())
			{
				result.append("<table border=\"1\"><tr><th>Key</th><th>Value</th></tr>");
				for(Map.Entry<String, String> current : metaData.entrySet())
				{
					result.append("<tr><td>")
							.append(TextPreprocessor.cropToSingleLine(current.getKey()))
							.append("</td><td>")
							.append(TextPreprocessor.cropToSingleLine(current.getValue()))
							.append("</td></tr>");
				}
				result.append("</table>");
			}
			result.append("</body></html>");

			return result.toString();
		}
		return null;
	}

	private class SelectFirstListener
		implements TableModelListener
	{

		@Override
		public void tableChanged(TableModelEvent tableModelEvent)
		{
			selectFirstTask();
		}
	}
}
