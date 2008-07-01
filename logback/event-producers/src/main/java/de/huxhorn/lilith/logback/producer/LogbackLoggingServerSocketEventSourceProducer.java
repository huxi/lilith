package de.huxhorn.lilith.logback.producer;

import de.huxhorn.lilith.engine.EventProducer;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.sulky.buffers.AppendOperation;

import java.io.InputStream;
import java.io.IOException;

public class LogbackLoggingServerSocketEventSourceProducer
	extends AbstractLogbackServerSocketEventSourceProducer<LoggingEvent>
{
	public LogbackLoggingServerSocketEventSourceProducer(int port) throws IOException
	{
		super(port);
	}

	protected EventProducer createProducer(SourceIdentifier id, AppendOperation<EventWrapper<LoggingEvent>> eventQueue, InputStream inputStream) throws IOException
	{
		return new LogbackLoggingStreamEventProducer(id, eventQueue, inputStream);
	}

	@Override
	public String toString()
	{
		return "LogbackLoggingServerSocketEventSourceProducer[port="+getPort()+"]";
	}
}