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

package de.huxhorn.lilith;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.prefs.LilithPreferences;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.sulky.buffers.BlockingCircularBuffer;
import java.io.Serializable;
import java.util.List;

public class LilithBuffer<T extends Serializable>
	extends BlockingCircularBuffer<EventWrapper<T>>
{
	private final ApplicationPreferences applicationPreferences;

	public LilithBuffer(ApplicationPreferences applicationPreferences, int bufferSize, int congestionDelay)
	{
		super(bufferSize, congestionDelay);
		this.applicationPreferences = applicationPreferences;
	}

	public LilithBuffer(ApplicationPreferences applicationPreferences, int bufferSize)
	{
		super(bufferSize);
		this.applicationPreferences = applicationPreferences;
	}

	@Override
	public void add(EventWrapper<T> element)
	{
		T event = element.getEvent();
		if(event == null
			|| applicationPreferences.getSourceFiltering() == LilithPreferences.SourceFiltering.NONE)
		{
			// we *must* add null events so a closed connection is detected!
			super.add(element);
		}
		else
		{
			SourceIdentifier si = element.getSourceIdentifier();
			if(si != null && applicationPreferences.isValidSource(si.getIdentifier()))
			{
				super.add(element);
			}
		}
	}

	@Override
	public void addAll(List<EventWrapper<T>> elements)
	{
		for(EventWrapper<T> current : elements)
		{
			add(current);
		}
	}

	@Override
	public void addAll(EventWrapper<T>[] elements)
	{
		for(EventWrapper<T> current : elements)
		{
			add(current);
		}
	}
}
