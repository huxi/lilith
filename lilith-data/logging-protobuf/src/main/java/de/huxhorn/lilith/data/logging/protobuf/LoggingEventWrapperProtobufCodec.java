package de.huxhorn.lilith.data.logging.protobuf;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.codec.DelegatingCodecBase;
import de.huxhorn.sulky.codec.Decoder;
import de.huxhorn.sulky.codec.Encoder;

public class LoggingEventWrapperProtobufCodec
	extends DelegatingCodecBase<EventWrapper<LoggingEvent>>
{
	public LoggingEventWrapperProtobufCodec()
	{
		super(new LoggingEventWrapperProtobufEncoder(false), new LoggingEventWrapperProtobufDecoder(false));
	}
}
