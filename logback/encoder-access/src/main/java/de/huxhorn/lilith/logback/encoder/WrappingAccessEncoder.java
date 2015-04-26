package de.huxhorn.lilith.logback.encoder;

import ch.qos.logback.access.spi.AccessEvent;
import de.huxhorn.lilith.data.access.logback.LogbackAccessConverter;
import de.huxhorn.lilith.data.access.protobuf.CompressingAccessEventWrapperProtobufCodec;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.codec.Codec;
import de.huxhorn.sulky.codec.Encoder;

public class WrappingAccessEncoder
	implements Encoder<AccessEvent>
{
	private LogbackAccessConverter converter = new LogbackAccessConverter();
	private Codec<EventWrapper<de.huxhorn.lilith.data.access.AccessEvent>> codec = new CompressingAccessEventWrapperProtobufCodec();
	private long id;

	public void reset()
	{
		id=0;
	}

	public byte[] encode(AccessEvent event)
	{
		de.huxhorn.lilith.data.access.AccessEvent lilithEvent = converter.convert(event);
		EventWrapper<de.huxhorn.lilith.data.access.AccessEvent> wrapped=new EventWrapper<>();
		wrapped.setEvent(lilithEvent);
		//wrapped.setEventIdentifier();
		id++;
		wrapped.setLocalId(id);
		//wrapped.setSourceIdentifier();

		return codec.encode(wrapped);
	}
}
