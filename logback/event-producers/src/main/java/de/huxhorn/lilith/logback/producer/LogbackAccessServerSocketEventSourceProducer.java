package de.huxhorn.lilith.logback.producer;

import de.huxhorn.lilith.engine.EventProducer;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.buffers.AppendOperation;

import java.io.InputStream;
import java.io.IOException;

import de.huxhorn.lilith.data.access.AccessEvent;

public class LogbackAccessServerSocketEventSourceProducer
	extends AbstractLogbackServerSocketEventSourceProducer<AccessEvent>
{
	public LogbackAccessServerSocketEventSourceProducer(int port) throws IOException
	{
		super(port);
	}

	protected EventProducer createProducer(SourceIdentifier id, AppendOperation<EventWrapper<AccessEvent>> eventQueue, InputStream inputStream) throws IOException
	{
		return new LogbackAccessStreamEventProducer(id, eventQueue, inputStream);
	}

	@Override
	public String toString()
	{
		return "LogbackAccessServerSocketEventSourceProducer[port="+getPort()+"]";
	}
}
