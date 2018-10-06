/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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
 * Copyright 2007-2018 Joern Huxhorn
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

import de.huxhorn.lilith.data.converter.Converter;
import de.huxhorn.lilith.data.converter.ConverterRegistry;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.sulky.buffers.AppendOperation;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializableEventProducer<T extends Serializable>
	extends AbstractEventProducer<T>
{
	private final Logger logger = LoggerFactory.getLogger(SerializableEventProducer.class);

	private final ConverterRegistry<T> converterRegistry;
	private final ObjectInputStream dataInput;

	private Converter<T> converter;

	public SerializableEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<T>> eventQueue, SourceIdentifierUpdater<T> sourceIdentifierUpdater, ConverterRegistry<T> converterRegistry, InputStream inputStream)
		throws IOException
	{
		super(sourceIdentifier, eventQueue, sourceIdentifierUpdater);
		this.converterRegistry = Objects.requireNonNull(converterRegistry, "converterRegistry must not be null!");
		this.dataInput = new WhitelistObjectInputStream(new BufferedInputStream(Objects.requireNonNull(inputStream, "inputStream must not be null!")), SerializableWhitelist.WHITELIST, false /*, true*/);
	}

	@Override
	public void start()
	{
		Thread t = new Thread(new ReceiverRunnable(), getSourceIdentifier() + "-Receiver");
		t.setDaemon(false);
		t.start();
	}

	private T postProcessEvent(Object o)
	{
		if(o == null)
		{
			return null;
		}
		if(converter == null)
		{
			converter = converterRegistry.resolveConverter(o);
		}
		if(converter != null)
		{
			return converter.convert(o);
		}
		if(logger.isWarnEnabled()) logger.warn("Retrieved unsupported class {}.", o.getClass().getName());
		return null;
	}

	private class ReceiverRunnable
		implements Runnable
	{
		@SuppressWarnings("PMD.IdenticalCatchBranches")
		@Override
		public void run()
		{
			for(;;)
			{
				try
				{
					Object object = dataInput.readObject();

					T event = postProcessEvent(object);

					if(object == null)
					{
						if(logger.isInfoEnabled()) logger.info("Retrieved null!");
					}
					else
					{
						addEvent(event);
					}
				}
				catch(InvalidClassException e)
				{
					if(logger.isWarnEnabled()) logger.warn("Exception ({}: '{}') while reading events. Adding eventWrapper with empty event and stopping...", e.getClass().getName(), e.getMessage(), e);
					addEvent(null);
					break;
				}
				catch(IOException e)
				{
					if(logger.isDebugEnabled()) logger.debug("Exception ({}: '{}') while reading events. Adding eventWrapper with empty event and stopping...", e.getClass().getName(), e.getMessage(), e);
					addEvent(null);
					break;
				}
				catch(Throwable e)
				{
					if(logger.isWarnEnabled()) logger.warn("Exception ({}: '{}') while reading events. Adding eventWrapper with empty event and stopping...", e.getClass().getName(), e.getMessage(), e);
					addEvent(null);
					break;
				}
			}
		}
	}

	@Override
	public void close()
	{
		if(dataInput != null)
		{
			try
			{
				dataInput.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}
}
