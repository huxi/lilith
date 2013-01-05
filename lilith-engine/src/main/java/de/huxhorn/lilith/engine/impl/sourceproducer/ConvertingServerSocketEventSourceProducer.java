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

package de.huxhorn.lilith.engine.impl.sourceproducer;

import de.huxhorn.lilith.data.converter.ConverterRegistry;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.engine.EventProducer;
import de.huxhorn.lilith.engine.impl.eventproducer.ConvertingStreamEventProducer;
import de.huxhorn.lilith.engine.impl.eventproducer.SourceIdentifierUpdater;
import de.huxhorn.sulky.buffers.AppendOperation;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class ConvertingServerSocketEventSourceProducer<T extends Serializable>
	extends AbstractServerSocketEventSourceProducer<T>
{
	private ConverterRegistry<T> converterRegistry;
	private SourceIdentifierUpdater<T> sourceIdentifierUpdater;

	public ConvertingServerSocketEventSourceProducer(int port, ConverterRegistry<T> converterRegistry, SourceIdentifierUpdater<T> sourceIdentifierUpdater)
			throws IOException
	{
		super(port);

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

	public SourceIdentifierUpdater<T> getSourceIdentifierUpdater()
	{
		return sourceIdentifierUpdater;
	}

	public void setSourceIdentifierUpdater(SourceIdentifierUpdater<T> sourceIdentifierUpdater)
	{
		this.sourceIdentifierUpdater = sourceIdentifierUpdater;
	}

	@Override
	protected EventProducer<T> createProducer(SourceIdentifier id, AppendOperation<EventWrapper<T>> eventQueue, InputStream inputStream) throws IOException
	{
		return new ConvertingStreamEventProducer<T>(id, eventQueue, sourceIdentifierUpdater, inputStream, converterRegistry);
	}
}
