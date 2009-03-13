package de.huxhorn.lilith.data.access.protobuf;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.sulky.codec.DelegatingCodecBase;

public class CompressingAccessEventWrapperProtobufCodec
	extends DelegatingCodecBase<EventWrapper<AccessEvent>>
{
	public CompressingAccessEventWrapperProtobufCodec()
	{
		super(new AccessEventWrapperProtobufEncoder(true), new AccessEventWrapperProtobufDecoder(true));
	}
}
