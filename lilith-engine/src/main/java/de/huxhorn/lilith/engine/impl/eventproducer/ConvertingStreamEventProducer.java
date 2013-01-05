/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2013 Joern Huxhorn
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
 * Copyright 2007-2013 Joern Huxhorn
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class ConvertingStreamEventProducer<T extends Serializable>
	extends AbstractStreamEventProducer<T>
{
	private ConverterRegistry<T> converterRegistry;
	private Converter<T> converter;

	public ConvertingStreamEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<T>> eventQueue, SourceIdentifierUpdater<T> sourceIdentifierUpdater, InputStream inputStream, ConverterRegistry<T> converterRegistry)
			throws IOException
	{
		super(sourceIdentifier, eventQueue, sourceIdentifierUpdater, inputStream);
		setConverterRegistry(converterRegistry);
	}

	public ConverterRegistry<T> getConverterRegistry()
	{
		return converterRegistry;
	}

	public void setConverterRegistry(ConverterRegistry<T> converterRegistry)
	{
		if(converterRegistry == null)
		{
			throw new IllegalArgumentException("converterRegistry must not be null!");
		}
		this.converterRegistry = converterRegistry;
	}

	@Override
	protected T postProcessEvent(Object o)
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
}
