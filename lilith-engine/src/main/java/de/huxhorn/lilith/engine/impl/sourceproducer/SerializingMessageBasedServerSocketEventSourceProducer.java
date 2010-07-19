/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.engine.EventProducer;
import de.huxhorn.lilith.engine.impl.eventproducer.SerializingMessageBasedEventProducer;
import de.huxhorn.sulky.buffers.AppendOperation;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 *
 * @param <T> the type that is expected as serialized messages
 * @deprecated Sender should use protobuf implementations instead.
 */
public class SerializingMessageBasedServerSocketEventSourceProducer<T extends Serializable>
	extends AbstractServerSocketEventSourceProducer<T>
{
	private boolean compressing;


	public SerializingMessageBasedServerSocketEventSourceProducer(int port, boolean compressing)
		throws IOException
	{
		super(port);
		this.compressing = compressing;
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	protected EventProducer<T> createProducer(SourceIdentifier id, AppendOperation<EventWrapper<T>> eventQueue, InputStream inputStream)
		throws IOException
	{
		return new SerializingMessageBasedEventProducer<T>(id, eventQueue, inputStream, compressing);
	}

	@Override
	public String toString()
	{
		return "SerializingMessageBasedServerSocketEventSourceProducer[port=" + getPort() + ", compressing=" + compressing + "]";
	}
}
