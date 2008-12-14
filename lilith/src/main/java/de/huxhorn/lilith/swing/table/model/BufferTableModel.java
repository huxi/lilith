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
package de.huxhorn.lilith.swing.table.model;

import de.huxhorn.sulky.buffers.DisposeOperation;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.CircularBuffer;
import de.huxhorn.sulky.buffers.ResetOperation;
import de.huxhorn.lilith.swing.table.model.RowBasedTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.SwingUtilities;

public abstract class BufferTableModel<T>
		implements RowBasedTableModel<T>, DisposeOperation
{
	private final Logger logger = LoggerFactory.getLogger(BufferTableModel.class);

	private Buffer<T> buffer;
	private CircularBuffer<T> circularBuffer;
	private final EventListenerList eventListenerList;
	private boolean disposed;
	private boolean paused;

	private int pauseRowCount;
	private int lastRowCount;

	public BufferTableModel(Buffer<T> buffer)
	{
		eventListenerList = new EventListenerList();
		lastRowCount=0;
		disposed=false;
		setBuffer(buffer);

		Thread t=new Thread(new BufferTableModel.TableChangeDetectionRunnable(), "TableChangeDetection");
		t.setDaemon(true);
		t.setPriority(Thread.NORM_PRIORITY-1);
		t.start();
		setPaused(false);
	}

	public synchronized boolean isPaused()
	{
		return paused;
	}

	public synchronized void setPaused(boolean paused)
	{
		if(paused)
		{
			pauseRowCount=getRowCount();
		}
		this.paused = paused;
		notifyAll();
	}

	public Buffer<T> getBuffer()
	{
		return buffer;
	}

	public void setBuffer(Buffer<T> buffer)
	{
		this.buffer = buffer;
		if (buffer instanceof CircularBuffer)
		{
			this.circularBuffer = (CircularBuffer<T>) buffer;
		}
		else
		{
			this.circularBuffer = null;
		}
		setLastRowCount(0);
		this.pauseRowCount=0;
	}

	public boolean clear()
	{
		if(buffer instanceof ResetOperation)
		{
			ResetOperation op = (ResetOperation) buffer;
			op.reset();
			setLastRowCount(0);
			fireTableChange();
			return true;
		}
		return false;
	}

	public synchronized int getRowCount()
	{
		return lastRowCount;
	}

	private synchronized void setLastRowCount(int lastRowCount)
	{
		this.lastRowCount=lastRowCount;
	}

	public synchronized void dispose()
	{
		disposed=true;
		if(buffer instanceof DisposeOperation)
		{
			if(logger.isDebugEnabled()) logger.debug("Calling dispose on contained buffer...");
			DisposeOperation disposeable = (DisposeOperation) buffer;
			disposeable.dispose();
		}
		notifyAll();
	}

	public boolean isDisposed()
	{
		return disposed;
	}

	private int internalRowCount()
	{
		if(isPaused())
		{
			return pauseRowCount;
		}
		if (circularBuffer != null)
		{
			// special circular handling
			return circularBuffer.getAvailableElements();
		}

		long rows = buffer.getSize();
		if (rows > Integer.MAX_VALUE)
		{
			if (logger.isWarnEnabled())
				logger.warn("Swing can only handle " + Integer.MAX_VALUE + " rows instead of " + rows + "!");
			rows = Integer.MAX_VALUE;
		}
		return (int) rows;
	}

	public T getValueAt(int row)
	{
		if (circularBuffer != null)
		{
			// special circular handling
			return circularBuffer.getRelative(row);
		}
		return buffer.get(row);
	}

	public abstract int getColumnCount();

	public abstract String getColumnName(int columnIndex);

	public abstract Class<?> getColumnClass(int columnIndex);

	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		T value = getValueAt(rowIndex);
		if(logger.isDebugEnabled()) logger.debug("value: {}", value);
		return value;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

	private void fireTableChange()
	{
		TableModelEvent event = new TableModelEvent(this);
		fireTableChange(event);
	}

	private void fireTableChange(int prevValue, int currentValue)
	{
		TableModelEvent event = new TableModelEvent(this, prevValue, currentValue, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
		fireTableChange(event);
	}

	private void fireTableChange(TableModelEvent evt)
	{
		Runnable r=new BufferTableModel.FireTableChangeRunnable(evt);
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

	class TableChangeDetectionRunnable
		implements Runnable
	{
		private static final int UPDATE_INTERVAL = 500;

		public void run()
		{
			for(;;)
			{
				if(isDisposed())
				{
					if(logger.isDebugEnabled()) logger.debug("Stopping TableChangeDetectionRunnable...");
					return;
				}
				if(!isPaused())
				{
					int currentValue=internalRowCount();

					if(currentValue>-1)
					{
						int prevValue=getRowCount();
						if(prevValue!=0 && currentValue>prevValue)
						{
							int lastRow=currentValue-1;
							setLastRowCount(currentValue);
							fireTableChange(prevValue, lastRow);
						}
						else if(currentValue!=prevValue)
						{
							setLastRowCount(currentValue);
							fireTableChange();
						}
					}
					try
					{
						Thread.sleep(UPDATE_INTERVAL);
					}
					catch (InterruptedException e)
					{
						if(logger.isDebugEnabled()) logger.debug("Interrupted...", e);
						return;
					}
				}
				else
				{
					synchronized(BufferTableModel.this)
					{
						for(;;)
						{
							if(!isPaused())
							{
								break;
							}
							else
							{
								try
								{
									BufferTableModel.this.wait();
								}
								catch (InterruptedException e)
								{
									if(logger.isDebugEnabled()) logger.debug("Interrupted...", e);
									return;
								}
							}
						}
					}
				}
			}
		}
	}
}
