package de.huxhorn.lilith.data.logging.protobuf;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.codec.DelegatingCodecBase;
import de.huxhorn.sulky.codec.Decoder;
import de.huxhorn.sulky.codec.Encoder;

public class LoggingEventWrapperProtobufCodec
	extends DelegatingCodecBase<EventWrapper<LoggingEvent>>
{
	public LoggingEventWrapperProtobufCodec(boolean compressed)
	{
		super(new LoggingEventWrapperProtobufEncoder(compressed), new LoggingEventWrapperProtobufDecoder(compressed));
	}

	public void setCompressing(boolean compressing)
	{
		{
			Encoder<EventWrapper<LoggingEvent>> s = getEncoder();
			if(s instanceof LoggingEventWrapperProtobufEncoder)
			{
				LoggingEventWrapperProtobufEncoder ss= (LoggingEventWrapperProtobufEncoder) s;
				ss.setCompressing(compressing);
			}
		}
		{
			Decoder<EventWrapper<LoggingEvent>> d = getDecoder();
			if(d instanceof LoggingEventWrapperProtobufDecoder)
			{
				LoggingEventWrapperProtobufDecoder sd= (LoggingEventWrapperProtobufDecoder) d;
				sd.setCompressing(compressing);
			}
		}
	}

}
