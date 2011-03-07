/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
import de.huxhorn.sulky.io.IOUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AccessStatusTypeTableModel
	implements TableModel
{
	private final Logger logger = LoggerFactory.getLogger(AccessStatusTypeTableModel.class);

	public static final int LEVEL_COLUMN = 0;

	private List<HttpStatus.Type> data;
	private final EventListenerList eventListenerList;

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

	public int getRowCount()
	{
		if(data == null)
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
			case LEVEL_COLUMN:
				return "Condition";
		}
		return null;
	}

	public Class<?> getColumnClass(int columnIndex)
	{
		switch(columnIndex)
		{
			case LEVEL_COLUMN:
				return LoggingEvent.Level.class;
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(data == null || columnIndex > 0 || rowIndex < 0 || rowIndex >= data.size())
		{
			return null;
		}
		switch(columnIndex)
		{
			case LEVEL_COLUMN:
			{
				return data.get(rowIndex);
			}
		}
		return null;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
	}

	public void refresh()
	{
		fireTableChange(new TableModelEvent(this));
	}

	private void fireTableChange(TableModelEvent evt)
	{
		Runnable r = new FireTableChangeRunnable(evt);
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
						IOUtilities.interruptIfNecessary(ex);
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
