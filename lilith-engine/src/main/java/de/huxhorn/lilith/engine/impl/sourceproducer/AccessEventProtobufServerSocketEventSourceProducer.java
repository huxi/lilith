package de.huxhorn.lilith.engine.impl.sourceproducer;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.engine.EventProducer;
import de.huxhorn.lilith.engine.impl.eventproducer.AccessEventProtobufMessageBasedEventProducer;
import de.huxhorn.sulky.buffers.AppendOperation;

import java.io.IOException;
import java.io.InputStream;

public class AccessEventProtobufServerSocketEventSourceProducer
	extends AbstractServerSocketEventSourceProducer<AccessEvent>
{
	private boolean compressing;

	public AccessEventProtobufServerSocketEventSourceProducer(int port, boolean compressing)
		throws IOException
	{
		super(port);
		this.compressing = compressing;
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	protected EventProducer<AccessEvent> createProducer(SourceIdentifier id, AppendOperation<EventWrapper<AccessEvent>> eventQueue, InputStream inputStream)
		throws IOException
	{
		return new AccessEventProtobufMessageBasedEventProducer(id, eventQueue, inputStream, compressing);
	}

	@Override
	public String toString()
	{
		return "AccessEventProtobufServerSocketEventSourceProducer[port=" + getPort() + ", compressing=" + compressing + "]";
	}
}
