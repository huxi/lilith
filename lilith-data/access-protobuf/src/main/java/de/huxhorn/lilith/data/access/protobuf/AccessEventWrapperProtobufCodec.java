package de.huxhorn.lilith.data.access.protobuf;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.sulky.codec.DelegatingCodecBase;

public class AccessEventWrapperProtobufCodec
	extends DelegatingCodecBase<EventWrapper<AccessEvent>>
{
	public AccessEventWrapperProtobufCodec()
	{
		super(new AccessEventWrapperProtobufEncoder(false), new AccessEventWrapperProtobufDecoder(false));
	}
}
