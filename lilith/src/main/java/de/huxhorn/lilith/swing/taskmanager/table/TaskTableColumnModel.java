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

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class TaskTableColumnModel
	extends DefaultTableColumnModel
{
	private static final long serialVersionUID = 1077359385881404953L;

	private static final String DEFAULT_COLUMN_NAME_ID = "ID";
	private static final String DEFAULT_COLUMN_NAME_NAME = "Name";
	private static final String DEFAULT_COLUMN_NAME_PROGRESS = "Progress";

	public TaskTableColumnModel()
	{
		super();

		{
			TableColumn col = new TableColumn(TaskTableModel.ID_INDEX);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_ID);
			col.setCellRenderer(new TaskIdRenderer());
			addColumn(col);
		}

		{
			TableColumn col = new TableColumn(TaskTableModel.NAME_INDEX);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_NAME);
			col.setCellRenderer(new TaskNameRenderer());
			addColumn(col);
		}

		{
			TableColumn col = new TableColumn(TaskTableModel.PROGRESS_INDEX);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_PROGRESS);
			col.setCellRenderer(new TaskProgressRenderer());
			addColumn(col);
		}
	}
}