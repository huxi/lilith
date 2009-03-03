package de.huxhorn.lilith.data.logging.protobuf;

import de.huxhorn.lilith.data.logging.protobuf.generated.LoggingProto;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.protobuf.InvalidProtocolBufferException;

public class LoggingProtoTest
{
	private final Logger logger = LoggerFactory.getLogger(LoggingProtoTest.class);

	@Test
	public void check()
		throws InvalidProtocolBufferException
	{
		LoggingProto.LoggingEvent event=LoggingProto.LoggingEvent.newBuilder()
			.setLoggerName("loggerName")
			.setMessage(LoggingProto.Message.newBuilder()
			.setMessagePattern("MessagePattern"))
			.build();

		if(logger.isInfoEnabled()) logger.info("event: {}", event);
		byte[] messageBytes=event.toByteArray();
		if(logger.isInfoEnabled()) logger.info("messageBytes.length: {}", messageBytes.length);

		LoggingProto.LoggingEvent parsed=LoggingProto.LoggingEvent.parseFrom(messageBytes);
		assertEquals("loggerName", parsed.getLoggerName());
	}
}
