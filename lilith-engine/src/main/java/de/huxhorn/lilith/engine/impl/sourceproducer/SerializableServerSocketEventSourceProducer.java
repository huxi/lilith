/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

package de.huxhorn.lilith.engine.impl.sourceproducer;

import de.huxhorn.lilith.data.converter.ConverterRegistry;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.engine.EventProducer;
import de.huxhorn.lilith.engine.impl.eventproducer.SerializableEventProducer;
import de.huxhorn.lilith.engine.impl.eventproducer.SourceIdentifierUpdater;
import de.huxhorn.sulky.buffers.AppendOperation;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Objects;

public class SerializableServerSocketEventSourceProducer<T extends Serializable>
	extends AbstractServerSocketEventSourceProducer<T>
{
	private final ConverterRegistry<T> converterRegistry;
	private final SourceIdentifierUpdater<T> sourceIdentifierUpdater;

	public SerializableServerSocketEventSourceProducer(int port, ConverterRegistry<T> converterRegistry, SourceIdentifierUpdater<T> sourceIdentifierUpdater)
			throws IOException
	{
		super(port);
		this.converterRegistry = Objects.requireNonNull(converterRegistry, "converterRegistry must not be null!");
		this.sourceIdentifierUpdater = sourceIdentifierUpdater;
	}

	@Override
	protected EventProducer<T> createProducer(SourceIdentifier id, AppendOperation<EventWrapper<T>> eventQueue, InputStream inputStream) throws IOException
	{
		return new SerializableEventProducer<>(id, eventQueue, sourceIdentifierUpdater, converterRegistry, inputStream);
	}

	@Override
	public String toString()
	{
		return "SerializableServerSocketEventSourceProducer[port=" + getPort() + "]";
	}
}
