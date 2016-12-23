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
package de.huxhorn.lilith.eventhandlers;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.engine.EventHandler;
import de.huxhorn.lilith.engine.FileBufferFactory;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.FileBuffer;
import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDumpEventHandler<T extends Serializable>
	implements EventHandler<T>
{
	private final Logger logger = LoggerFactory.getLogger(FileDumpEventHandler.class);

	private FileBuffer<EventWrapper<T>> fileBuffer;
	private boolean enabled;

	public FileDumpEventHandler(SourceIdentifier sourceIdentifier, FileBufferFactory<T> fileBufferFactory)
	{
		enabled = true;
		fileBuffer = fileBufferFactory.createActiveBuffer(sourceIdentifier);
	}

	public void handle(List<EventWrapper<T>> events)
	{
		if(enabled)
		{
			fileBuffer.addAll(events);
			if(logger.isInfoEnabled()) logger.info("Wrote {} events to file.", events.size());
		}
	}

	public Buffer<EventWrapper<T>> getBuffer()
	{
		return fileBuffer;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public boolean isEnabled()
	{
		return enabled;
	}
}
