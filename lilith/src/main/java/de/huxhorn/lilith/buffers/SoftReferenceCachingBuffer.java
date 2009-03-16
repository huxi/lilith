package de.huxhorn.lilith.buffers;

import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.Dispose;
import de.huxhorn.sulky.buffers.DisposeOperation;
import de.huxhorn.sulky.buffers.Reset;
import de.huxhorn.sulky.buffers.ResetOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SoftReferenceCachingBuffer<E>
	implements Buffer<E>, ResetOperation, DisposeOperation
{
	private final Logger logger = LoggerFactory.getLogger(SoftReferenceCachingBuffer.class);

	private static final ReferenceQueue refQueue = new ReferenceQueue();

	static
	{
		Thread cleanupThread = new Thread(new ReferenceQueueRunnable(), "ReferenceQueue-Cleanup");
		cleanupThread.setDaemon(true);
		cleanupThread.start();
		final Logger logger = LoggerFactory.getLogger(SoftReferenceCachingBuffer.class);

		if(logger.isInfoEnabled()) logger.info("Started thread {}.", cleanupThread);
	}

	private Buffer<E> buffer;

	private Map<Long, MySoftReference<E>> cache;
	private boolean disposed;

	public SoftReferenceCachingBuffer(Buffer<E> buffer)
	{
		this.disposed = false;
		this.buffer = buffer;
		this.cache = new ConcurrentHashMap<Long, MySoftReference<E>>();
	}

	Buffer<E> getWrappedBuffer()
	{
		return buffer;
	}

	public E get(long index)
	{
		if(disposed)
		{
			return null;
		}
		SoftReference<E> softy = cache.get(index);
		E result;
		if(softy != null)
		{
			result = softy.get();
			if(result == null)
			{
				cache.remove(index);
			}
			else
			{
				// found in cache...
				if(logger.isDebugEnabled()) logger.debug("Retrieved {} from cache.", index);
				return result;
			}
		}

		result = buffer.get(index);
		if(result != null)
		{
			cache.put(index, new MySoftReference<E>(cache, index, result));
			if(logger.isDebugEnabled()) logger.debug("Added {} to cache.", index);
		}
		return result;
	}

	public long getSize()
	{
		return buffer.getSize();
	}

	public Iterator<E> iterator()
	{
		return buffer.iterator();
	}

	public void reset()
	{
		Reset.reset(buffer);
		cache.clear();
	}

	public void dispose()
	{
		disposed = true;
		cache.clear();
		Dispose.dispose(buffer);
	}

	public boolean isDisposed()
	{
		return disposed;
	}

	private static class MySoftReference<E>
		extends SoftReference<E>
	{
		private long index;
		private Map<Long, MySoftReference<E>> cache;

		public MySoftReference(Map<Long, MySoftReference<E>> cache, long index, E referent)
		{
			// the following cast is safe since we are not using the content in the reference queue......
			//noinspection unchecked
			super(referent, refQueue);
			this.index = index;
			this.cache = cache;
		}

		public long getIndex()
		{
			return index;
		}

		public void removeFromCache()
		{
			cache.remove(index);
			final Logger logger = LoggerFactory.getLogger(SoftReferenceCachingBuffer.class);
			if(logger.isDebugEnabled()) logger.debug("Removed {} from cache.", index);
		}
	}

	private static class ReferenceQueueRunnable
		implements Runnable
	{

		public void run()
		{

			for(; ;)
			{
				try
				{
					Reference ref = refQueue.remove();
					if(ref instanceof MySoftReference)
					{
						MySoftReference reference = (MySoftReference) ref;
						reference.removeFromCache();
					}
					else
					{
						final Logger logger = LoggerFactory.getLogger(SoftReferenceCachingBuffer.class);
						if(logger.isWarnEnabled()) logger.warn("Unexpected reference!! {}", ref);
					}
				}
				catch(InterruptedException e)
				{
					final Logger logger = LoggerFactory.getLogger(SoftReferenceCachingBuffer.class);
					if(logger.isInfoEnabled()) logger.info("Interrupted ReferenceQueueRunnable...");
					break;
				}
			}
		}
	}
}
