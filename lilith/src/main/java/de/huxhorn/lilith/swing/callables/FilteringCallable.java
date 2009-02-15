/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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
package de.huxhorn.lilith.swing.callables;

import de.huxhorn.lilith.buffers.FilteringBuffer;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.tasks.AbstractProgressingCallable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilteringCallable<E>
	extends AbstractProgressingCallable<Long>
{
	private final Logger logger = LoggerFactory.getLogger(FilteringCallable.class);

	private int filterDelay;
	private long lastFilteredElement;
	private FilteringBuffer<E> filteringBuffer;


	public FilteringCallable(FilteringBuffer<E> filteringBuffer, int filterDelay)
	{
		this.filterDelay = filterDelay;
		this.filteringBuffer = filteringBuffer;
	}

	public void run()
	{
		if(logger.isDebugEnabled()) logger.debug("Runnable finished.");
	}

	public Long call()
		throws Exception
	{
		for(; ;)
		{
			Buffer<E> sourceBuffer = filteringBuffer.getSourceBuffer();
			Condition condition = filteringBuffer.getCondition();
			boolean disposed = filteringBuffer.isDisposed();
			if(disposed)
			{
				break;
			}
			long currentSize = sourceBuffer.getSize();
			long filterStartIndex = lastFilteredElement;
			if(currentSize < lastFilteredElement)
			{
				filterStartIndex = 0;
				filteringBuffer.clearFilteredIndices();
			}

			setNumberOfSteps(currentSize);
			setCurrentStep(filterStartIndex);

			if(currentSize != lastFilteredElement + 1)
			{
				for(long i = filterStartIndex; i < currentSize; i++)
				{
					disposed = filteringBuffer.isDisposed();
					if(disposed)
					{
						break;
					}
					E current = sourceBuffer.get(i);
					if(current != null)
					{
						if(condition != null && condition.isTrue(current))
						{
							filteringBuffer.addFilteredIndex(i);
							if(logger.isDebugEnabled()) logger.debug("Added index: {}", i);
						}
					}
					setCurrentStep(i);
					lastFilteredElement = i;
				}
			}
			try
			{
				Thread.sleep(filterDelay);
			}
			catch(InterruptedException e)
			{
				if(logger.isDebugEnabled()) logger.debug("Interrupted...", e);
				return lastFilteredElement;
			}
		}
		if(logger.isDebugEnabled()) logger.debug("Callable finished.");
		return lastFilteredElement;
	}
}
