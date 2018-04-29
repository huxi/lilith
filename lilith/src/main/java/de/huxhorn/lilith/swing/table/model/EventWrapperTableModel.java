/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
package de.huxhorn.lilith.swing.table.model;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.table.BufferTableModel;
import java.io.Serializable;

public class EventWrapperTableModel<T extends Serializable>
	extends BufferTableModel<EventWrapper<T>>
{
	public EventWrapperTableModel(Buffer<EventWrapper<T>> buffer)
	{
		super(buffer);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return EventWrapper.class;
	}

	@Override
	public int getColumnCount()
	{
		return 1;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		if(columnIndex == 0)
		{
			return "EventWrapper";
		}
		return null;
	}
}
