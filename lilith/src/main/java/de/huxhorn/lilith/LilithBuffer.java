package de.huxhorn.lilith;

import de.huxhorn.sulky.buffers.BlockingCircularBuffer;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.swing.ApplicationPreferences;

import java.util.List;
import java.io.Serializable;

public class LilithBuffer<T extends Serializable>
	extends BlockingCircularBuffer<EventWrapper<T>>
{
	private ApplicationPreferences applicationPreferences;

	public LilithBuffer(ApplicationPreferences applicationPreferences, int bufferSize, int congestionDelay)
	{
		super(bufferSize, congestionDelay);
		this.applicationPreferences=applicationPreferences;
	}

	public LilithBuffer(ApplicationPreferences applicationPreferences, int bufferSize)
	{
		super(bufferSize);
		this.applicationPreferences=applicationPreferences;
	}

	@Override
	public void add(EventWrapper<T> element)
	{
		T event=element.getEvent();
		if(event==null
			|| applicationPreferences.getSourceFiltering() == ApplicationPreferences.SourceFiltering.NONE)
		{
			// we *must* add null events so a closed connection is detected!
			super.add(element);
		}
		else
		{
			SourceIdentifier si = element.getSourceIdentifier();
			if(si!=null)
			{
				if(applicationPreferences.isValidSource(si.getIdentifier()))
				{
					super.add(element);
				}
			}
		}
	}

	@Override
	public void addAll(List<EventWrapper<T>> elements)
	{
		for(EventWrapper<T> current:elements)
		{
			add(current);
		}
	}

	@Override
	public void addAll(EventWrapper<T>[] elements)
	{
		for(EventWrapper<T> current:elements)
		{
			add(current);
		}
	}
}
