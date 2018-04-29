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

package de.huxhorn.lilith.swing.preferences.table;

import de.huxhorn.lilith.data.access.HttpStatus;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class AccessStatusTypeTableModel
	implements TableModel
{
	private static final int LEVEL_COLUMN = 0;

	private final EventListenerList eventListenerList;
	private final List<HttpStatus.Type> data;

	public AccessStatusTypeTableModel()
	{
		eventListenerList = new EventListenerList();
		HttpStatus.Type[] values = HttpStatus.Type.values();
		data = Collections.unmodifiableList(Arrays.asList(values));
	}

	public List<HttpStatus.Type> getData()
	{
		return data;
	}

	@Override
	public int getRowCount()
	{
		if(data == null)
		{
			return 0;
		}
		return data.size();
	}

	@Override
	public int getColumnCount()
	{
		return 1;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		if(LEVEL_COLUMN == columnIndex)
		{
			return "Condition";
		}
		return null;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		if(LEVEL_COLUMN == columnIndex)
		{
			return LoggingEvent.Level.class;
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(data == null || columnIndex > 0 || rowIndex < 0 || rowIndex >= data.size())
		{
			return null;
		}
		if(LEVEL_COLUMN == columnIndex)
		{
			return data.get(rowIndex);
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		// read-only
	}


	@Override
	public void addTableModelListener(TableModelListener l)
	{
		synchronized(eventListenerList)
		{
			eventListenerList.add(TableModelListener.class, l);
		}
	}

	@Override
	public void removeTableModelListener(TableModelListener l)
	{
		synchronized(eventListenerList)
		{
			eventListenerList.remove(TableModelListener.class, l);
		}
	}
}
