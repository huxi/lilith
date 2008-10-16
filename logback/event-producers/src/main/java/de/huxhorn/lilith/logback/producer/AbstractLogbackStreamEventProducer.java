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
package de.huxhorn.lilith.logback.producer;

import de.huxhorn.lilith.engine.impl.eventproducer.AbstractEventProducer;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.buffers.AppendOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

public abstract class AbstractLogbackStreamEventProducer<T extends Serializable>
	extends AbstractEventProducer<T>
{
	final Logger logger = LoggerFactory.getLogger(AbstractLogbackStreamEventProducer.class);

	private ObjectInputStream dataInput;

	public AbstractLogbackStreamEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<T>> eventQueue, InputStream inputStream) throws IOException
	{
		super(sourceIdentifier, eventQueue);
		this.dataInput=new ObjectInputStream(new BufferedInputStream(inputStream));
	}

	public void start()
	{
		Thread t=new Thread(new ReceiverRunnable(), ""+getSourceIdentifier()+"-Receiver");
		t.setDaemon(false);
		t.start();
	}

	protected abstract T postprocessEvent(Object o);

	private class ReceiverRunnable
		implements Runnable
	{
		public void run()
		{
//			long localIdCounter = 0;
			for(;;)
			{
				try
				{
					// TODO: obtain transfer size info
					Object object = dataInput.readObject();

					T event=postprocessEvent(object);

					if(object==null)
					{
						if(logger.isInfoEnabled()) logger.info("Retrieved null!");
					}
					else
					{
						addEvent(event);
/*
						localIdCounter++;
						EventWrapper<T> wrapper=new EventWrapper<T>(sourceIdentifier, localIdCounter, event);
						eventQueue.add(wrapper);
						if(logger.isDebugEnabled()) logger.debug("Added event-wrapper for {}.", event);
*/
					}
				}
				catch (Throwable e)
				{
					if(logger.isInfoEnabled()) logger.info("Exception ({}: '{}') while reading events. Adding eventWrapper with empty event and stopping...", e.getClass().getName(), e.getMessage());
					addEvent(null);
/*
					localIdCounter++;
					EventWrapper<T> wrapper=new EventWrapper<T>(sourceIdentifier, localIdCounter, null);
					eventQueue.add(wrapper);
					if(logger.isDebugEnabled()) logger.debug("Added event.");
*/
					break;
				}
			}
		}
	}

	public void close()
	{
		IOUtils.closeQuietly(dataInput);
	}
}
