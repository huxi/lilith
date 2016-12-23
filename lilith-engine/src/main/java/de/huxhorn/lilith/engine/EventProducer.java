/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
package de.huxhorn.lilith.engine;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.sulky.buffers.AppendOperation;
import java.io.Serializable;

/**
 * The responsibility of EventProducers is the creation of events from
 * some kind of input, e.g. an InputStream.
 * Those events are wrapped into an EventWrapper and appended to the event queue of
 * the producer.
 */
public interface EventProducer<T extends Serializable>
{
	/**
	 *
	 * @return the source of the events.
	 */
	SourceIdentifier getSourceIdentifier();

	/**
	 *
	 * @return the event queue that this producer appends events to.
	 */
	AppendOperation<EventWrapper<T>> getEventQueue();

	void start();

	void close();
}
