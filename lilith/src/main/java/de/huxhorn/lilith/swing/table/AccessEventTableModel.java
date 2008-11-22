/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
package de.huxhorn.lilith.swing.table;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.buffers.Buffer;

public class AccessEventTableModel
		extends EventWrapperTableModelBase<AccessEvent>
		implements AccessEventTableModelConstants
{
	private final String[] columns =
			{
					DEFAULT_COLUMN_NAME_ID,
					DEFAULT_COLUMN_NAME_TIMESTAMP,
					DEFAULT_COLUMN_NAME_STATUS_CODE,
					DEFAULT_COLUMN_NAME_METHOD,
					DEFAULT_COLUMN_NAME_REQUEST_URI,
					DEFAULT_COLUMN_NAME_PROTOCOL,
					DEFAULT_COLUMN_NAME_REMOTE_ADDR,
					DEFAULT_COLUMN_NAME_APPLICATIION,
					DEFAULT_COLUMN_NAME_SOURCE,
			};

	public AccessEventTableModel(Buffer<EventWrapper<AccessEvent>> buffer, boolean global)
	{
		super(buffer, global);
	}

	public int getColumnCount()
	{
		if(!isGlobal())
		{
			return columns.length-1;
		}
		return columns.length;
	}

	public String getColumnName(int columnIndex)
	{
		if (columnIndex >= 0 && columnIndex < columns.length)
		{
			return columns[columnIndex];
		}
		return null;
	}

}
