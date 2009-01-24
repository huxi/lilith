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
package de.huxhorn.lilith.swing.preferences.table;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class ConditionTableColumnModel
	extends DefaultTableColumnModel
{
	public static final int DEFAULT_COLUMN_INDEX_INDEX = 0;
	public static final int DEFAULT_COLUMN_INDEX_NAME = 1;
	public static final int DEFAULT_COLUMN_INDEX_PREVIEW = 2;
	public static final int DEFAULT_COLUMN_INDEX_ACTIVE = 3;

	private static final String DEFAULT_COLUMN_NAME_INDEX = "#";
	private static final String DEFAULT_COLUMN_NAME_NAME = "Name";
	private static final String DEFAULT_COLUMN_NAME_PREVIEW = "Preview";
	private static final String DEFAULT_COLUMN_NAME_ACTIVE = "Active";

	public ConditionTableColumnModel()
	{
		super();

		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_INDEX);
			col.setCellRenderer(new ConditionIndexRenderer());
			addColumn(col);
		}

		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_NAME);
			col.setCellRenderer(new ConditionNameRenderer());
			addColumn(col);
		}

		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_PREVIEW);
			col.setCellRenderer(new ConditionPreviewRenderer());
			addColumn(col);
		}

		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_ACTIVE);
			col.setCellRenderer(new ConditionActiveRenderer());
			addColumn(col);
		}

	}
}
