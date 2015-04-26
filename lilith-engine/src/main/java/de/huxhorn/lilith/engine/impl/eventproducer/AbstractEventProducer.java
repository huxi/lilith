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
package de.huxhorn.lilith.engine.impl.eventproducer;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.engine.EventProducer;
import de.huxhorn.sulky.buffers.AppendOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public abstract class AbstractEventProducer<T extends Serializable>
	implements EventProducer<T>
{
	private final Logger logger = LoggerFactory.getLogger(AbstractEventProducer.class);

	private AppendOperation<EventWrapper<T>> eventQueue;
	private SourceIdentifier sourceIdentifier;
	private SourceIdentifierUpdater<T> sourceIdentifierUpdater;
	private long localIdCounter;

	protected AbstractEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<T>> eventQueue, SourceIdentifierUpdater<T> sourceIdentifierUpdater)
	{
		this.sourceIdentifier = sourceIdentifier;
		this.eventQueue = eventQueue;
		this.sourceIdentifierUpdater = sourceIdentifierUpdater;
		localIdCounter = 0;
	}

	public SourceIdentifier getSourceIdentifier()
	{
		if(sourceIdentifier == null)
		{
			return null;
		}
		try
		{
			return sourceIdentifier.clone();
		}
		catch(CloneNotSupportedException e)
		{
			// shouldn't be possible, ignore it.
			return sourceIdentifier;
		}
	}

	public AppendOperation<EventWrapper<T>> getEventQueue()
	{
		return eventQueue;
	}

	protected void addEvent(T event)
	{
		if(event == null && localIdCounter == 0)
		{
			// ignore null event if no event has previously been produced.
			// this is a good idea because sometimes exceptions are thrown while
			// establishing the connection/during first read.
			// this would otherwise produce views that contain only the connection closed event
			// which isn't very usefull :)
			return;
		}
		localIdCounter++;

		if(sourceIdentifierUpdater != null)
		{
			sourceIdentifierUpdater.updateIdentifier(sourceIdentifier, event);
		}

		EventWrapper<T> wrapper = new EventWrapper<>(getSourceIdentifier(), localIdCounter, event);
		eventQueue.add(wrapper);
		if(logger.isDebugEnabled()) logger.debug("Added event-wrapper for {}.", event);
	}
}
