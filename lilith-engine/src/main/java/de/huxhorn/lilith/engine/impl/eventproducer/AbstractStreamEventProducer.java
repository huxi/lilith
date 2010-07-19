/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright 2007-2010 Joern Huxhorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.huxhorn.lilith.engine.impl.eventproducer;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.sulky.buffers.AppendOperation;

import de.huxhorn.sulky.io.IOUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

public abstract class AbstractStreamEventProducer<T extends Serializable>
	extends AbstractEventProducer<T>
{
	final Logger logger = LoggerFactory.getLogger(AbstractStreamEventProducer.class);

	private ObjectInputStream dataInput;

	public AbstractStreamEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<T>> eventQueue, InputStream inputStream)
		throws IOException
	{
		super(sourceIdentifier, eventQueue);
		this.dataInput = new ObjectInputStream(new BufferedInputStream(inputStream));
	}

	public void start()
	{
		Thread t = new Thread(new ReceiverRunnable(), "" + getSourceIdentifier() + "-Receiver");
		t.setDaemon(false);
		t.start();
	}

	protected abstract T postprocessEvent(Object o);

	private class ReceiverRunnable
		implements Runnable
	{
		public void run()
		{
			for(; ;)
			{
				try
				{
					Object object = dataInput.readObject();

					T event = postprocessEvent(object);

					if(object == null)
					{
						if(logger.isInfoEnabled()) logger.info("Retrieved null!");
					}
					else
					{
						addEvent(event);
					}
				}
				catch(Throwable e)
				{
					if(logger.isInfoEnabled())
					{
						logger
							.info("Exception ({}: '{}') while reading events. Adding eventWrapper with empty event and stopping...", e
								.getClass().getName(), e.getMessage());
					}
					addEvent(null);
					IOUtilities.interruptIfNecessary(e);
					break;
				}
			}
		}
	}

	public void close()
	{
		IOUtilities.closeQuietly(dataInput);
	}
}
