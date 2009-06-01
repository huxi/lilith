package de.huxhorn.lilith.buffers;

import de.huxhorn.lilith.swing.callables.FilteringCallable;
import de.huxhorn.sulky.buffers.BasicBufferIterator;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.DisposeOperation;
import de.huxhorn.sulky.conditions.Condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FilteringBuffer<E>
	implements Buffer<E>, DisposeOperation
{
	private final Logger logger = LoggerFactory.getLogger(FilteringCallable.class);

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
	private final ReentrantReadWriteLock indicesLock;
	private final List<Long> filteredIndices;
	private boolean disposed;

	public FilteringBuffer(Buffer<E> sourceBuffer, Condition condition)
	{
		this.indicesLock = new ReentrantReadWriteLock(true);
		this.sourceBuffer = sourceBuffer;
		this.condition = condition;
		this.filteredIndices = new ArrayList<Long>();
		this.disposed = false;
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

		ReentrantReadWriteLock.ReadLock lock = indicesLock.readLock();
		lock.lock();
		try
		{
			if(index >= 0 && index < filteredIndices.size())
			{
				realIndex = filteredIndices.get((int) index);
			}
		}
		finally
		{
			lock.unlock();
		}
		return realIndex;
	}

	public long getSize()
	{
		ReentrantReadWriteLock.ReadLock lock = indicesLock.readLock();
		lock.lock();
		try
		{
			return filteredIndices.size();
		}
		finally
		{
			lock.unlock();
		}
	}

	public void addFilteredIndex(long index)
	{
		long size = sourceBuffer.getSize();
		if(index < 0 || index >= sourceBuffer.getSize())
		{
			if(logger.isInfoEnabled()) logger.info("Invalid filtered index {} (size={})!", index, size);
		}
		ReentrantReadWriteLock.WriteLock lock = indicesLock.writeLock();
		lock.lock();
		try
		{
			filteredIndices.add(index);
		}
		finally
		{
			lock.unlock();
		}
	}

	public void clearFilteredIndices()
	{
		ReentrantReadWriteLock.WriteLock lock = indicesLock.writeLock();
		lock.lock();
		try
		{
			filteredIndices.clear();
		}
		finally
		{
			lock.unlock();
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

	public void dispose()
	{
		this.disposed = true;
	}

	public boolean isDisposed()
	{
		return disposed;
	}
}
