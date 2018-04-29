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

package de.huxhorn.lilith.eventhandlers;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.engine.EventHandler;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.FileBuffer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDumpEventHandler<T extends Serializable>
	implements EventHandler<T>
{
	private final Logger logger = LoggerFactory.getLogger(FileDumpEventHandler.class);

	private final FileBuffer<EventWrapper<T>> fileBuffer;
	private boolean enabled;

	public FileDumpEventHandler(FileBuffer<EventWrapper<T>> fileBuffer)
	{
		this.fileBuffer = Objects.requireNonNull(fileBuffer, "fileBuffer must not be null!");
		enabled = true;
	}

	@Override
	public void handle(List<EventWrapper<T>> events)
	{
		if(!enabled)
		{
			return;
		}

		if(events == null || events.isEmpty())
		{
			return;
		}

		List<EventWrapper<T>> sanitized = new ArrayList<>(events.size());
		for (EventWrapper<T> eventWrapper : events)
		{
			if (eventWrapper != null)
			{
				T event = eventWrapper.getEvent();
				if (event != null)
				{
					sanitized.add(eventWrapper);
				}
			}
		}
		if (!sanitized.isEmpty())
		{
			fileBuffer.addAll(sanitized);
			if (logger.isInfoEnabled()) logger.info("Wrote {} events to file.", events.size());
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
