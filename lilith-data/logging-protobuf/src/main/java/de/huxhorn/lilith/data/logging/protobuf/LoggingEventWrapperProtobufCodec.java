package de.huxhorn.lilith.data.logging.protobuf;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.generics.io.DelegatingCodecBase;
import de.huxhorn.sulky.generics.io.Serializer;
import de.huxhorn.sulky.generics.io.Deserializer;

public class LoggingEventWrapperProtobufCodec
	extends DelegatingCodecBase<EventWrapper<LoggingEvent>>
{
	public LoggingEventWrapperProtobufCodec(boolean compressed)
	{
		super(new LoggingEventWrapperProtobufSerializer(compressed), new LoggingEventWrapperProtobufDeserializer(compressed));
	}

	public void setCompressing(boolean compressing)
	{
		{
			Serializer<EventWrapper<LoggingEvent>> s = getSerializer();
			if(s instanceof LoggingEventWrapperProtobufSerializer)
			{
				LoggingEventWrapperProtobufSerializer ss= (LoggingEventWrapperProtobufSerializer) s;
				ss.setCompressing(compressing);
			}
		}
		{
			Deserializer<EventWrapper<LoggingEvent>> d = getDeserializer();
			if(d instanceof LoggingEventWrapperProtobufDeserializer)
			{
				LoggingEventWrapperProtobufDeserializer sd= (LoggingEventWrapperProtobufDeserializer) d;
				sd.setCompressing(compressing);
			}
		}
	}

}
