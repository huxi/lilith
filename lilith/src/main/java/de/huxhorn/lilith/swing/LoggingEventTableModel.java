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
package de.huxhorn.lilith.swing;

import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.lilith.data.eventsource.EventWrapper;

import de.huxhorn.lilith.data.logging.LoggingEvent;

public class LoggingEventTableModel
		extends EventWrapperTableModelBase<LoggingEvent>
		implements LoggingEventTableModelConstants
{
	private final String[] columns =
			{
					DEFAULT_COLUMN_NAME_ID,
					DEFAULT_COLUMN_NAME_TIMESTAMP,
					DEFAULT_COLUMN_NAME_LEVEL,
					DEFAULT_COLUMN_NAME_LOGGER_NAME,
					DEFAULT_COLUMN_NAME_MESSAGE,
					DEFAULT_COLUMN_NAME_THROWABLE,
					DEFAULT_COLUMN_NAME_THREAD,
					DEFAULT_COLUMN_NAME_MARKER,
					DEFAULT_COLUMN_NAME_APPLICATIION,
					DEFAULT_COLUMN_NAME_SOURCE,
			};

	public LoggingEventTableModel(Buffer<EventWrapper<LoggingEvent>> buffer, boolean global)
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
