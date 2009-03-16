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
package de.huxhorn.lilith.elementprocessors;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.sulky.buffers.ElementProcessor;
import de.huxhorn.sulky.buffers.ResetOperation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoggingEventThreadNameProcessor
	implements ElementProcessor<EventWrapper<LoggingEvent>>, ResetOperation
{
	private Set<String> threadNames;

	public LoggingEventThreadNameProcessor()
	{
		threadNames = new HashSet<String>();
	}

	public void processElement(EventWrapper<LoggingEvent> element)
	{
		if(element == null)
		{
			return;
		}
		LoggingEvent event = element.getEvent();
		if(event == null)
		{
			return;
		}
		ThreadInfo ti = event.getThreadInfo();
		if(ti == null)
		{
			return;
		}
		String name = ti.getName();
		if(name == null)
		{
			return;
		}
		if(!threadNames.contains(name))
		{
			threadNames.add(name);
			changed();
		}
	}

	public void processElements(List<EventWrapper<LoggingEvent>> elements)
	{
		if(elements == null || elements.size() == 0)
		{
			return;
		}

		boolean changed = false;
		for(EventWrapper<LoggingEvent> element : elements)
		{
			if(element == null)
			{
				continue;
			}
			LoggingEvent event = element.getEvent();
			if(event == null)
			{
				continue;
			}
			ThreadInfo ti = event.getThreadInfo();
			if(ti == null)
			{
				continue;
			}
			String name = ti.getName();
			if(name == null)
			{
				continue;
			}
			if(!threadNames.contains(name))
			{
				threadNames.add(name);
				changed = true;
			}

		}
		if(changed)
		{
			changed();
		}
	}

	public void reset()
	{
		threadNames.clear();
		changed();
	}

	private void changed()
	{
		// TODO: handle change
	}

}
