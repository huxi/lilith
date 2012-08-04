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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SourceManagerImpl<T extends Serializable>
	implements SourceManager<T>
{
	private final Logger logger = LoggerFactory.getLogger(SourceManagerImpl.class);

	private BlockingCircularBuffer<EventWrapper<T>> queue;
	private Set<EventSourceListener<T>> listeners;
	private PropertyChangeSupport changeSupport;
	private List<EventSource<T>> sources;
	private Set<EventProducer<T>> eventProducers;
	private EventPoller<T> eventPoller;

	private static final String NUMBER_OF_SOURCES = "numberOfSources";
	private List<EventSourceProducer<T>> eventSourceProducers;

	public SourceManagerImpl(BlockingCircularBuffer<EventWrapper<T>> queue)
	{
		this.queue = queue;
		this.eventPoller = new EventPoller<T>(queue);
		this.eventPoller.setPollDelay(100);
		eventProducers = new HashSet<EventProducer<T>>();
		eventSourceProducers = new ArrayList<EventSourceProducer<T>>();
		listeners = new HashSet<EventSourceListener<T>>();
		changeSupport = new PropertyChangeSupport(this);
		sources = new ArrayList<EventSource<T>>();
	}

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


	public void removeSource(SourceIdentifier source)
	{
		int oldSize = sources.size();
		List<EventSource<T>> removedSources = new ArrayList<EventSource<T>>();
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

	public List<EventSource<T>> getSources()
	{
		return new ArrayList<EventSource<T>>(sources);
	}


	public int getNumberOfSources()
	{
		return sources.size();
	}

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

		for(EventProducer<T> current : eventProducers)
		{
			if(id.equals(current.getSourceIdentifier()))
			{
				return current;
			}
		}
		return null;
	}

	public void addEventProducer(EventProducer<T> producer)
	{
		SourceIdentifier id = producer.getSourceIdentifier();
		EventProducer previous = findProducer(id);
		if(previous != null)
		{
			previous.close();
			eventProducers.remove(previous);
		}
		eventProducers.add(producer);

		if(logger.isDebugEnabled()) logger.debug("Started {}.", producer);
	}

	public void removeEventProducer(SourceIdentifier id)
	{
		EventProducer previous = findProducer(id);
		if(previous != null)
		{
			previous.close();
			eventProducers.remove(previous);
		}
	}

	public void setEventHandlers(List<EventHandler<T>> handlers)
	{
		eventPoller.setEventHandlers(handlers);
	}

	public List<EventHandler<T>> getEventHandlers()
	{
		return eventPoller.getEventHandlers();
	}


	public void addEventSourceListener(EventSourceListener<T> listener)
	{
		if(!listeners.contains(listener))
		{
			listeners.add(listener);
		}
	}

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

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		changeSupport.removePropertyChangeListener(listener);
	}

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
				Thread t = new Thread((Runnable) handler, "Consumer-Thread");
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
				Thread t = new Thread((Runnable) current, "Producer-Thread-" + current);
				t.setDaemon(true);
				t.start();
				if(logger.isInfoEnabled()) logger.info("Started {}.", t);
			}
		}

	}
}
