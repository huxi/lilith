package de.huxhorn.lilith.logback.encoder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import de.huxhorn.lilith.data.converter.Converter;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.logback.SameThreadLogbackLoggingConverter;
import de.huxhorn.lilith.data.logging.protobuf.CompressingLoggingEventWrapperProtobufCodec;
import de.huxhorn.sulky.codec.Codec;
import de.huxhorn.sulky.codec.Encoder;

public class WrappingClassicEncoder
	implements Encoder<ILoggingEvent>
{
	private Converter<de.huxhorn.lilith.data.logging.LoggingEvent> converter = new SameThreadLogbackLoggingConverter();
	private Codec<EventWrapper<de.huxhorn.lilith.data.logging.LoggingEvent>> codec = new CompressingLoggingEventWrapperProtobufCodec();
	private long id;

	public void reset()
	{
		id=0;
	}

	public byte[] encode(ILoggingEvent event)
	{
		de.huxhorn.lilith.data.logging.LoggingEvent lilithEvent = converter.convert(event);
		EventWrapper<de.huxhorn.lilith.data.logging.LoggingEvent> wrapped=new EventWrapper<de.huxhorn.lilith.data.logging.LoggingEvent>();
		wrapped.setEvent(lilithEvent);
		//wrapped.setEventIdentifier();
		id++;
		wrapped.setLocalId(id);
		//wrapped.setSourceIdentifier();

		return codec.encode(wrapped);
	}
}
