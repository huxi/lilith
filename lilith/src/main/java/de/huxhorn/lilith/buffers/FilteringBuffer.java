package de.huxhorn.lilith.buffers;

import de.huxhorn.sulky.buffers.BasicBufferIterator;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.DisposeOperation;
import de.huxhorn.sulky.conditions.Condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FilteringBuffer<E>
	implements Buffer<E>, DisposeOperation
{
	public static <E> Buffer<E> resolveSourceBuffer(Buffer<E> buffer)
	{
		for(; ;)
		{
			if(!(buffer instanceof FilteringBuffer))
			{
				return buffer;
			}
			buffer = ((FilteringBuffer<E>) buffer).getSourceBuffer();
		}
	}

	private Buffer<E> sourceBuffer;
	private Condition condition;
	private final List<Long> filteredIndices;
	private boolean disposed;

	public FilteringBuffer(Buffer<E> sourceBuffer, Condition condition)
	{
		this.sourceBuffer = sourceBuffer;
		this.condition = condition;
		this.filteredIndices = new ArrayList<Long>();
		this.disposed = false;
		Thread t = new Thread(new FilterUpdateRunnable(1000));
		t.setDaemon(true);
		t.start();
	}

	public E get(long index)
	{
		long realIndex = getSourceIndex(index);
		if(realIndex >= 0)
		{
			return sourceBuffer.get(realIndex);
		}
		return null;
	}

	public long getSourceIndex(long index)
	{
		long realIndex = -1;
		synchronized(filteredIndices)
		{
			if(index >= 0 && index < filteredIndices.size())
			{
				realIndex = filteredIndices.get((int) index);
			}
		}
		return realIndex;
	}

	public long getSize()
	{
		synchronized(filteredIndices)
		{
			return filteredIndices.size();
		}
	}

	public Iterator<E> iterator()
	{
		return new BasicBufferIterator<E>(this);
	}

	public Buffer<E> getSourceBuffer()
	{
		return sourceBuffer;
	}

	public Condition getCondition()
	{
		return condition;
	}

	public synchronized void dispose()
	{
		this.disposed = true;
	}

	public boolean isDisposed()
	{
		return disposed;
	}

	private class FilterUpdateRunnable
		implements Runnable
	{
		private final Logger logger = LoggerFactory.getLogger(FilterUpdateRunnable.class);

		private int filterDelay;
		private long lastFilteredElement;

		public FilterUpdateRunnable(int filterDelay)
		{
			this.filterDelay = filterDelay;
		}

		public void run()
		{
			for(; ;)
			{
				if(disposed)
				{
					break;
				}
				long currentSize = sourceBuffer.getSize();
				long filterStartIndex = lastFilteredElement;
				if(currentSize < lastFilteredElement)
				{
					filterStartIndex = 0;
					synchronized(filteredIndices)
					{
						filteredIndices.clear();
					}
				}

				if(currentSize != lastFilteredElement + 1)
				{
					//List<Long> newIndices=new ArrayList<Long>();
					for(long i = filterStartIndex; i < currentSize; i++)
					{
						if(disposed)
						{
							break;
						}
						E current = sourceBuffer.get(i);
						if(current != null)
						{
							if(condition != null && condition.isTrue(current))
							{
								synchronized(filteredIndices)
								{
									filteredIndices.add(i);
									if(logger.isDebugEnabled()) logger.debug("Added index: {}", i);
								}
							}
						}
						lastFilteredElement = i;
					}
					//if(logger.isInfoEnabled()) logger.info("Added {} indices.", newIndices.size());
				}
				try
				{
					Thread.sleep(filterDelay);
				}
				catch(InterruptedException e)
				{
					if(logger.isDebugEnabled()) logger.debug("Interrupted...", e);
					return;
				}
			}
			if(logger.isDebugEnabled()) logger.debug("Runnable finished.");
		}
	}
}
