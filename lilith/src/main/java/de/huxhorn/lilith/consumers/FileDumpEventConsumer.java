/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
package de.huxhorn.lilith.consumers;

import de.huxhorn.lilith.engine.EventConsumer;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.buffers.FileBuffer;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.SerializingFileBuffer;

import java.util.List;
import java.io.File;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDumpEventConsumer<T extends Serializable>
	implements EventConsumer<T>
{
	private final Logger logger = LoggerFactory.getLogger(FileDumpEventConsumer.class);

//	private ApplicationPreferences applicationPreferences;
	private FileBuffer<EventWrapper<T>> fileBuffer;

	public FileDumpEventConsumer(/*ApplicationPreferences applicationPreferences, */File dataFile, File indexFile)
	{
//		this.applicationPreferences=applicationPreferences;
		fileBuffer= new SerializingFileBuffer<EventWrapper<T>>(dataFile, indexFile);
	}

	public void consume(List<EventWrapper<T>> events)
	{
//		events=filterEvents(events);
		fileBuffer.addAll(events);
		if(logger.isInfoEnabled()) logger.info("Wrote {} events to file.", events.size());
	}

/*
	List<EventWrapper<T>> filterEvents(List<EventWrapper<T>> events)
	{
		if(applicationPreferences.getSourceFiltering() == ApplicationPreferences.SourceFiltering.NONE)
		{
			return events;
		}
		List<EventWrapper<T>> result=new ArrayList<EventWrapper<T>>();
		for(EventWrapper<T> event:events)
		{
			String id=event.getSourceIdentifier().getIdentifier();
			if(applicationPreferences.isValidSource(id))
			{
				result.add(event);
			}
		}
		return result;
	}
*/
	public Buffer<EventWrapper<T>> getBuffer()
	{
		return fileBuffer;
	}
}
