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

package de.huxhorn.lilith.swing.preferences;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SourceNameTableModel
	implements TableModel
{
	private final Logger logger = LoggerFactory.getLogger(SourceNameTableModel.class);

	static final int SOURCE_IDENTIFIER_COLUMN = 0;
	private static final int NAME_COLUMN = 1;
	private Map<String, String> data;
	private List<String> keys;
	private final EventListenerList eventListenerList;

	SourceNameTableModel(Map<String, String> data)
	{
		eventListenerList = new EventListenerList();
		setData(data);
	}

	public void setData(Map<String, String> data)
	{
		this.data = data;
		if(data != null)
		{
			this.keys = new ArrayList<>(data.keySet());
			Collections.sort(this.keys);
		}
		else
		{
			keys = null;
		}
		fireTableChange();
	}

	public Map<String, String> getData()
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
		return 2;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		switch(columnIndex)
		{
			case SOURCE_IDENTIFIER_COLUMN:
				return "Source Identifier";
			case NAME_COLUMN:
				return "Name";
			default:
				return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return String.class;
	}

	@Override
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

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(keys == null || rowIndex < 0 || rowIndex >= keys.size())
		{
			return null;
		}
		String key = keys.get(rowIndex);
		switch(columnIndex)
		{
			case SOURCE_IDENTIFIER_COLUMN:
				return key;
			case NAME_COLUMN:
				return data.get(key);
			default:
				return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if(keys == null || rowIndex < 0 || rowIndex >= keys.size())
		{
			return;
		}

		String newValue = (String) aValue;
		switch(columnIndex)
		{
			case SOURCE_IDENTIFIER_COLUMN:
			{
				String key = keys.remove(rowIndex);
				String value = data.remove(key);
				if(newValue != null && !"".equals(newValue.trim()))
				{
					data.put(newValue, value);
					keys.add(newValue);
					Collections.sort(keys);
				}
				else
				{
					if(logger.isDebugEnabled()) logger.debug("Removed source name for {}.", key);
				}
				fireTableChange();
			}
			break;
			case NAME_COLUMN:
			{
				String key = keys.get(rowIndex);
				if(newValue != null && !"".equals(newValue.trim()))
				{
					data.put(key, newValue);
				}
				else
				{
					keys.remove(rowIndex);
					data.remove(key);
					if(logger.isDebugEnabled()) logger.debug("Removed source name for {}.", key);
				}
				fireTableChange();
			}
			break;
			default: // nothing
			break;
		}
	}

	private void fireTableChange()
	{
		TableModelEvent event = new TableModelEvent(this);
		fireTableChange(event);
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
