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
package de.huxhorn.lilith.consumers;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.engine.EventConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StdoutEventConsumer<T extends Serializable>
	implements EventConsumer<T>, Runnable
{
	final Logger logger = LoggerFactory.getLogger(StdoutEventConsumer.class);

	private List<EventWrapper> events;
	private Lock lock;
	private long writeInterval;

	public StdoutEventConsumer()
	{
		events = new LinkedList<EventWrapper>();
		lock = new ReentrantLock();
		writeInterval = 2000;
	}

	public long getWriteInterval()
	{
		return writeInterval;
	}

	public void setWriteInterval(long writeInterval)
	{
		this.writeInterval = writeInterval;
	}

	public void consume(List<EventWrapper<T>> events)
	{
		lock.lock();
		try
		{
			this.events.addAll(events);
		}
		finally
		{
			lock.unlock();
		}
	}

	public void run()
	{
		for(; ;)
		{
			lock.lock();
			int eventCount = 0;
			try
			{

				if(events.size() > 0)
				{
					EventWrapper event = events.remove(0);
					System.out.println("Event: " + event);
					eventCount = events.size();
				}
			}
			finally
			{
				lock.unlock();
			}

			if(eventCount == 0)
			{
				try
				{
					Thread.sleep(writeInterval);
				}
				catch(InterruptedException e)
				{
					if(logger.isDebugEnabled()) logger.debug("Interrupted...");
					break;
				}
			}
		}
	}
}
