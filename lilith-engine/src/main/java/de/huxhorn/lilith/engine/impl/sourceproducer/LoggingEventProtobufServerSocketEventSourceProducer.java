package de.huxhorn.lilith.engine.impl.sourceproducer;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.EventProducer;
import de.huxhorn.lilith.engine.impl.eventproducer.LoggingEventProtobufMessageBasedEventProducer;
import de.huxhorn.sulky.buffers.AppendOperation;
import java.io.IOException;
import java.io.InputStream;

public class LoggingEventProtobufServerSocketEventSourceProducer
	extends AbstractServerSocketEventSourceProducer<LoggingEvent>
{
	private boolean compressing;

	public LoggingEventProtobufServerSocketEventSourceProducer(int port, boolean compressing)
		throws IOException
	{
		super(port);
		this.compressing = compressing;
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	protected EventProducer<LoggingEvent> createProducer(SourceIdentifier id, AppendOperation<EventWrapper<LoggingEvent>> eventQueue, InputStream inputStream)
		throws IOException
	{
		return new LoggingEventProtobufMessageBasedEventProducer(id, eventQueue, inputStream, compressing);
	}

	@Override
	public String toString()
	{
		return "LoggingEventProtobufServerSocketEventSourceProducer[port=" + getPort() + ", compressing=" + compressing + "]";
	}
}
