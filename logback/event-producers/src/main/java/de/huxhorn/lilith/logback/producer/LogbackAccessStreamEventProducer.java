package de.huxhorn.lilith.logback.producer;

import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.buffers.AppendOperation;

import java.io.InputStream;
import java.io.IOException;

import de.huxhorn.lilith.data.access.AccessEvent;

public class LogbackAccessStreamEventProducer
	extends AbstractLogbackStreamEventProducer<AccessEvent>
{
	public LogbackAccessStreamEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<AccessEvent>> eventQueue, InputStream inputStream) throws IOException
	{
		super(sourceIdentifier, eventQueue, inputStream);
	}

	protected AccessEvent postprocessEvent(Object o)
	{
		if(o instanceof AccessEvent)
		{
			return (AccessEvent) o;
		}
		if(logger.isInfoEnabled())
		{
			logger.info("Retrieved {} instead of ch.qos.logback.access.spi.AccessEvent.", o==null?null:o.getClass().getName());
		}
		return null;
	}
}