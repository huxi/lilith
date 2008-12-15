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
package de.huxhorn.lilith.swing.preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.TableModel;
import javax.swing.event.TableModelListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.SwingUtilities;
import java.util.*;

public class ConditionTableModel
	implements TableModel
{
	private final Logger logger = LoggerFactory.getLogger(ConditionTableModel.class);

	public static final int CONDITION_COLUMN = 0;

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
		fireTableChange();
	}

	public List<SavedCondition> getData()
	{
		return data;
	}

	public int getRowCount()
	{
		if(data==null)
		{
			return 0;
		}
		return data.size();
	}

	public int getColumnCount()
	{
		return 1;
	}

	public String getColumnName(int columnIndex)
	{
		switch(columnIndex)
		{
			case CONDITION_COLUMN:
				return "Condition";
		}
		return null;
	}

	public Class<?> getColumnClass(int columnIndex)
	{
		return SavedCondition.class;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		/*
		if(keys == null || rowIndex<0 || rowIndex>=keys.size())
		{
			return false;
		}
		return !(columnIndex < 0 || columnIndex > 1);
		*/
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(data == null || columnIndex>0 || rowIndex<0 || rowIndex>=data.size())
		{
			return null;
		}
		return data.get(rowIndex);
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		SavedCondition newValue=(SavedCondition) aValue;
		if(data == null || columnIndex>0 || rowIndex<0 || rowIndex>=data.size())
		{
			return;
		}
		data.set(rowIndex, newValue);
		fireTableChange();
	}

	private void fireTableChange()
	{
		TableModelEvent event = new TableModelEvent(this);
		fireTableChange(event);
	}

	private void fireTableChange(TableModelEvent evt)
	{
		Runnable r=new FireTableChangeRunnable(evt);
		if(SwingUtilities.isEventDispatchThread())
		{
			r.run();
		}
		else
		{
			SwingUtilities.invokeLater(r);
		}
	}

	private class FireTableChangeRunnable
		implements Runnable
	{
		private TableModelEvent event;

		public FireTableChangeRunnable(TableModelEvent event)
		{
			this.event = event;
		}

		public void run()
		{
			Object[] listeners;
			synchronized(eventListenerList)
			{
				listeners = eventListenerList.getListenerList();
			}
			// Process the listeners last to first, notifying
			// those that are interested in this event
			for (int i = listeners.length - 2; i >= 0; i -= 2)
			{
				if (listeners[i] == TableModelListener.class)
				{
					TableModelListener listener = ((TableModelListener) listeners[i + 1]);
					if(logger.isDebugEnabled()) logger.debug("Firing TableChange at {}.",listener.getClass().getName());
					try
					{
						listener.tableChanged(event);
					}
					catch(Throwable ex)
					{
						if(logger.isWarnEnabled()) logger.warn("Exception while firing change!",ex);
					}
				}
			}
		}

	}
	public void addTableModelListener(TableModelListener l)
	{
		synchronized(eventListenerList)
		{
			eventListenerList.add(TableModelListener.class, l);
		}
	}

	public void removeTableModelListener(TableModelListener l)
	{
		synchronized(eventListenerList)
		{
			eventListenerList.remove(TableModelListener.class, l);
		}
	}
}