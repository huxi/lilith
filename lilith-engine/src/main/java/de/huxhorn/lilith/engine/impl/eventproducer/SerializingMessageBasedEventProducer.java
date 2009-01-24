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
package de.huxhorn.lilith.engine.impl.eventproducer;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.sulky.buffers.AppendOperation;
import de.huxhorn.sulky.generics.io.Deserializer;
import de.huxhorn.sulky.generics.io.SerializableDeserializer;

import java.io.InputStream;
import java.io.Serializable;

public class SerializingMessageBasedEventProducer<T extends Serializable>
	extends AbstractMessageBasedEventProducer<T>
{
	public SerializingMessageBasedEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<T>> eventQueue, InputStream inputStream, boolean compressing)
	{
		super(sourceIdentifier, eventQueue, inputStream, compressing);
	}

	protected Deserializer<T> createDeserializer()
	{
		return new SerializableDeserializer<T>(isCompressing());
	}
}
