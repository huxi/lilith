/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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

import de.huxhorn.lilith.swing.preferences.SavedCondition;
import java.awt.EventQueue;
import java.util.List;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConditionTableModel
	implements TableModel
{
	private final Logger logger = LoggerFactory.getLogger(ConditionTableModel.class);

	private static final int CONDITION_COLUMN = 0;

	private List<SavedCondition> data;
	private final EventListenerList eventListenerList;

	public ConditionTableModel(List<SavedCondition> data)
	{
		eventListenerList = new EventListenerList();
		setData(data);
	}

	public void setData(List<SavedCondition> data)
	{
		this.data = data;
		fireTableChange(new TableModelEvent(this));
	}

	public List<SavedCondition> getData()
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
		if(CONDITION_COLUMN == columnIndex)
		{
			return "Condition";
		}
		return null;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		if(CONDITION_COLUMN == columnIndex)
		{
			return SavedCondition.class;
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
		if(CONDITION_COLUMN == columnIndex)
		{
			return data.get(rowIndex);
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if(data == null || columnIndex > 0 || rowIndex < 0 || rowIndex >= data.size())
		{
			return;
		}
		if(CONDITION_COLUMN == columnIndex)
		{
			SavedCondition newValue = (SavedCondition) aValue;
			data.set(rowIndex, newValue);
			fireTableChange(new TableModelEvent(this, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
		}
	}

	public void remove(int row)
	{
		if(row >= 0 && row < data.size())
		{
			data.remove(row);
			fireTableChange(new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
		}
	}

	public int moveUp(int row)
	{
		int result = -1;
		if(row >= 0)
		{
			int newRow = row - 1;
			if(row < data.size() && newRow >= 0 && newRow < data.size())
			{
				SavedCondition prev = data.set(newRow, data.get(row));
				data.set(row, prev);
				fireTableChange(new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
				fireTableChange(new TableModelEvent(this, newRow, newRow, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
				result = newRow;
			}
		}
		return result;
	}

	public int moveDown(int row)
	{
		int result = -1;
		if(row >= 0)
		{
			int newRow = row + 1;
			if(row < data.size() && newRow >= 0 && newRow < data.size())
			{
				SavedCondition prev = data.set(newRow, data.get(row));
				data.set(row, prev);
				fireTableChange(new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
				fireTableChange(new TableModelEvent(this, newRow, newRow, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
				result = newRow;
			}
		}
		return result;

	}

	private void fireTableChange(TableModelEvent evt)
	{
		Runnable r = new FireTableChangeRunnable(evt);
		if(EventQueue.isDispatchThread())
		{
			r.run();
		}
		else
		{
			EventQueue.invokeLater(r);
		}
	}

	public void set(int row, SavedCondition savedCondition)
	{
		if(row > -1 && row < data.size())
		{
			data.set(row, savedCondition);
			fireTableChange(new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
		}
	}

	public int add(SavedCondition savedCondition)
	{
		data.add(savedCondition);
		int row = data.size() - 1;
		fireTableChange(new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
		return row;
	}

	private class FireTableChangeRunnable
		implements Runnable
	{
		private final TableModelEvent event;

		FireTableChangeRunnable(TableModelEvent event)
		{
			this.event = event;
		}

		@Override
		public void run()
		{
			Object[] listeners;
			synchronized(eventListenerList)
			{
				listeners = eventListenerList.getListenerList();
			}
			// Process the listeners last to first, notifying
			// those that are interested in this event
			for(int i = listeners.length - 2; i >= 0; i -= 2)
			{
				if(listeners[i] == TableModelListener.class)
				{
					TableModelListener listener = ((TableModelListener) listeners[i + 1]);
					if(logger.isDebugEnabled())
					{
						logger.debug("Firing TableChange at {}.", listener.getClass().getName());
					}
					try
					{
						listener.tableChanged(event);
					}
					catch(Throwable ex)
					{
						if(logger.isWarnEnabled()) logger.warn("Exception while firing change!", ex);
					}
				}
			}
		}
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
