package de.huxhorn.lilith.logback.producer;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.logback.LogbackLoggingAdapter;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.buffers.AppendOperation;

import java.io.InputStream;
import java.io.IOException;

public class LogbackLoggingStreamEventProducer
	extends AbstractLogbackStreamEventProducer<LoggingEvent>
{
	private LogbackLoggingAdapter adapter;

	public LogbackLoggingStreamEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<LoggingEvent>> eventQueue, InputStream inputStream) throws IOException
	{
		super(sourceIdentifier, eventQueue, inputStream);
		adapter=new LogbackLoggingAdapter();
	}

	protected LoggingEvent postprocessEvent(Object o)
	{
		if(o instanceof ch.qos.logback.classic.spi.LoggingEvent)
		{
			ch.qos.logback.classic.spi.LoggingEvent logbackEvent = (ch.qos.logback.classic.spi.LoggingEvent) o;
			return adapter.convert(logbackEvent);
		}
		if(logger.isInfoEnabled())
		{
			logger.info("Retrieved {} instead of ch.qos.logback.classic.spi.LoggingEvent.", o==null?null:o.getClass().getName());
		}
		return null;
	}
}
