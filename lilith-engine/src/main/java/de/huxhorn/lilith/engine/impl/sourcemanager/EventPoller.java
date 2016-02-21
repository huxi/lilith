/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2015 Joern Huxhorn
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
package de.huxhorn.lilith.engine.impl.sourcemanager;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.engine.EventHandler;
import de.huxhorn.sulky.buffers.CircularBuffer;
import de.huxhorn.sulky.buffers.RemoveOperation;

import de.huxhorn.sulky.io.IOUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

public class EventPoller<T extends Serializable>
	implements Runnable
{
	final Logger logger = LoggerFactory.getLogger(EventPoller.class);

	private RemoveOperation<EventWrapper<T>> queue;
	private int pollDelay;
	private List<EventHandler<T>> handlers;
	private static final int DEFAULT_POLL_INTERVAL = 1000;

	public EventPoller(RemoveOperation<EventWrapper<T>> queue)
	{
		this.queue = queue;
		this.pollDelay = DEFAULT_POLL_INTERVAL;
	}

	public int getPollDelay()
	{
		return pollDelay;
	}

	public void setPollDelay(int pollDelay)
	{
		this.pollDelay = pollDelay;
	}

	public List<EventHandler<T>> getEventHandlers()
	{
		return handlers;
	}

	public void setEventHandlers(List<EventHandler<T>> handlers)
	{
		this.handlers = handlers;
	}

	public RemoveOperation<EventWrapper<T>> getQueue()
	{
		return queue;
	}

	public void setQueue(CircularBuffer<EventWrapper<T>> queue)
	{
		this.queue = queue;
	}

	public void run()
	{
		for(; ;)
		{
			long pollTime = System.currentTimeMillis();
			List<EventWrapper<T>> events = queue.removeAll();
			if(events != null)
			{
				int eventCount = events.size();
				if(eventCount > 0)
				{
					if(logger.isInfoEnabled()) logger.info("Consuming {} events.", eventCount);
					long time = System.currentTimeMillis();
					if(handlers != null)
					{
						for(EventHandler<T> handler : handlers)
						{
							try
							{
								handler.handle(events);
								if(logger.isDebugEnabled()) logger.debug("Executed handler {}.", handler);
							}
							catch(Throwable t)
							{
								if(logger.isWarnEnabled()) logger.warn("Exception while executing event handler!", t);
								IOUtilities.interruptIfNecessary(t);
							}
						}
					}
					time = System.currentTimeMillis() - time;
					time = time / 1000;
					if(time == 0)
					{
						time = 1;
					}
					int eventsPerSecond = (int) (eventCount / time);
					if(logger.isInfoEnabled())
					{
						logger.info("Finished consuming {} events ({} events/sec).", eventCount, eventsPerSecond);
					}
				}
			}

			pollTime = System.currentTimeMillis() - pollTime;
			long sleepTime = pollDelay - pollTime;
			if(sleepTime > 0)
			{
				if(logger.isInfoEnabled()) logger.info("Sleeping {} milliseconds.", sleepTime);
				try
				{
					Thread.sleep(pollDelay);
				}
				catch(InterruptedException e)
				{
					if(logger.isDebugEnabled()) logger.debug("Interrupted...");
					IOUtilities.interruptIfNecessary(e);
					break;
				}
			}
		}
	}
}
