/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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
package de.huxhorn.lilith.engine.impl.sourcemanager;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.engine.EventHandler;
import de.huxhorn.lilith.engine.EventProducer;
import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.lilith.engine.EventSourceListener;
import de.huxhorn.lilith.engine.EventSourceProducer;
import de.huxhorn.lilith.engine.SourceManager;
import de.huxhorn.sulky.buffers.BlockingCircularBuffer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceManagerImpl<T extends Serializable>
	implements SourceManager<T>
{
	private final Logger logger = LoggerFactory.getLogger(SourceManagerImpl.class);

	private static final String NUMBER_OF_SOURCES = "numberOfSources";

	private final BlockingCircularBuffer<EventWrapper<T>> queue;
	private final Set<EventSourceListener<T>> listeners;
	private final PropertyChangeSupport changeSupport;
	private final List<EventSource<T>> sources;
	private final Set<EventProducer<T>> eventProducers;
	private final Lock eventProducersLock;
	private final EventPoller<T> eventPoller;

	private final List<EventSourceProducer<T>> eventSourceProducers;

	public SourceManagerImpl(BlockingCircularBuffer<EventWrapper<T>> queue)
	{
		this.queue = Objects.requireNonNull(queue, "queue must not be null!");
		eventPoller = new EventPoller<>(queue, 100);
		eventProducersLock = new ReentrantLock();
		eventProducers = new HashSet<>();
		eventSourceProducers = new ArrayList<>();
		listeners = new HashSet<>();
		changeSupport = new PropertyChangeSupport(this);
		sources = new ArrayList<>();
	}

	@Override
	public void addSource(EventSource<T> source)
	{
		//SourceIdentifier si=source.getSourceIdentifier();
		int oldSize = sources.size();
		if(!sources.contains(source))
		{
			sources.add(source);
			int newSize = sources.size();
			changeSupport.firePropertyChange(NUMBER_OF_SOURCES, oldSize, newSize);
			fireAddSource(source);
			if(logger.isInfoEnabled()) logger.info("Added source {}.", source);
		}
	}


	@Override
	public void removeSource(SourceIdentifier source)
	{
		int oldSize = sources.size();
		List<EventSource<T>> removedSources = new ArrayList<>();
		for(EventSource<T> src : sources)
		{
			if(source.equals(src.getSourceIdentifier()))
			{
				removedSources.add(src);
			}
		}
		sources.removeAll(removedSources);
		int newSize = sources.size();
		changeSupport.firePropertyChange(NUMBER_OF_SOURCES, oldSize, newSize);
		for(EventSource<T> src : removedSources)
		{
			fireRemoveSource(src);
			if(logger.isInfoEnabled()) logger.info("Removed source {}.", src);
		}

	}

	private void fireAddSource(EventSource<T> source)
	{
		for(EventSourceListener<T> listener : listeners)
		{
			listener.eventSourceAdded(source);
		}
	}

	private void fireRemoveSource(EventSource<T> source)
	{
		for(EventSourceListener<T> listener : listeners)
		{
			listener.eventSourceRemoved(source);
		}
	}

	@Override
	public List<EventSource<T>> getSources()
	{
		return new ArrayList<>(sources);
	}


	@Override
	public int getNumberOfSources()
	{
		return sources.size();
	}

	@Override
	public void addEventSourceProducer(EventSourceProducer<T> producer)
	{
		producer.setQueue(queue);
		producer.setSourceManager(this);
		eventSourceProducers.add(producer);
	}

	private EventProducer<T> findProducer(SourceIdentifier id)
	{
		if(id == null)
		{
			return null;
		}
		eventProducersLock.lock();
		try
		{
			// The SourceIdentifier of an EventProducer can change so it is not
			// suitable as the key of a Map.
			// See SourceIdentifierUpdater and its implementations.
			for(EventProducer<T> current : eventProducers)
			{
				if(id.equals(current.getSourceIdentifier()))
				{
					return current;
				}
			}
			return null;
		}
		finally
		{
			eventProducersLock.unlock();
		}
	}

	@Override
	public void addEventProducer(EventProducer<T> producer)
	{
		SourceIdentifier id = producer.getSourceIdentifier();
		removeEventProducer(id);

		eventProducersLock.lock();
		try
		{
			eventProducers.add(producer);
		}
		finally
		{
			eventProducersLock.unlock();
		}

		if(logger.isDebugEnabled()) logger.debug("Started {}.", producer);
	}

	@Override
	public void removeEventProducer(SourceIdentifier id)
	{
		EventProducer previous = findProducer(id);
		if(previous != null)
		{
			previous.close();
			eventProducersLock.lock();
			try
			{
				eventProducers.remove(previous);
			}
			finally
			{
				eventProducersLock.unlock();
			}
		}
	}

	@Override
	public void setEventHandlers(List<EventHandler<T>> handlers)
	{
		eventPoller.setEventHandlers(handlers);
	}

	@Override
	public List<EventHandler<T>> getEventHandlers()
	{
		return eventPoller.getEventHandlers();
	}


	@Override
	public void addEventSourceListener(EventSourceListener<T> listener)
	{
		if(!listeners.contains(listener))
		{
			listeners.add(listener);
		}
	}

	@Override
	public void removeEventSourceListener(EventSourceListener<T> listener)
	{
		if(listeners.contains(listener))
		{
			listeners.remove(listener);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		changeSupport.addPropertyChangeListener(listener);
	}

	@SuppressWarnings("unused")
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		changeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void start()
	{
		// start poller...
		{
			Thread t = new Thread(eventPoller, "EventPoller");
			t.setDaemon(true);
			t.start();
		}

		// start handlers if necessary...
		for(EventHandler<T> handler : getEventHandlers())
		{
			if(handler instanceof Runnable)
			{
				Thread t = new Thread((Runnable) handler, "Consumer-Thread"); // NOPMD - AvoidInstantiatingObjectsInLoops
				t.setDaemon(true);
				t.start();
				if(logger.isInfoEnabled()) logger.info("Started {}.", t);
			}
		}

		// start event source producers if necessary...
		for(EventSourceProducer current : eventSourceProducers)
		{
			if(current instanceof Runnable)
			{
				Thread t = new Thread((Runnable) current, "Producer-Thread-" + current); // NOPMD - AvoidInstantiatingObjectsInLoops
				t.setDaemon(true);
				t.start();
				if(logger.isInfoEnabled()) logger.info("Started {}.", t);
			}
		}

	}
}
