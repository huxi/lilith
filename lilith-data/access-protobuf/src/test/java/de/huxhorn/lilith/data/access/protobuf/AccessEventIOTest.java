package de.huxhorn.lilith.data.access.protobuf;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.LoggerContext;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AccessEventIOTest
{
	private final Logger logger = LoggerFactory.getLogger(AccessEventIOTest.class);

	@Test
	public void minimal()
	{
		AccessEvent event = createMinimalEvent();
		check(event);
	}

	@Test
	public void loggerContext()
	{
		AccessEvent event = createMinimalEvent();
		LoggerContext value = new LoggerContext();
		value.setName("ContextName");
		value.setBirthTime(1234567890000L);
		Map<String, String> propperties = new HashMap<String, String>();
		propperties.put("foo", "bar");
		value.setProperties(propperties);
		event.setLoggerContext(value);
		check(event);
	}

	@Test
	public void method()
	{
		AccessEvent event = createMinimalEvent();
		String value = "value";
		event.setMethod(value);
		check(event);
	}

	@Test
	public void protocol()
	{
		AccessEvent event = createMinimalEvent();
		String value = "value";
		event.setProtocol(value);
		check(event);
	}

	@Test
	public void remoteAddress()
	{
		AccessEvent event = createMinimalEvent();
		String value = "value";
		event.setRemoteAddress(value);
		check(event);
	}

	@Test
	public void remoteHost()
	{
		AccessEvent event = createMinimalEvent();
		String value = "value";
		event.setRemoteHost(value);
		check(event);
	}

	@Test
	public void remoteUser()
	{
		AccessEvent event = createMinimalEvent();
		String value = "value";
		event.setRemoteUser(value);
		check(event);
	}

	@Test
	public void requestUri()
	{
		AccessEvent event = createMinimalEvent();
		String value = "value";
		event.setRequestURI(value);
		check(event);
	}

	@Test
	public void requestUrl()
	{
		AccessEvent event = createMinimalEvent();
		String value = "value";
		event.setRequestURL(value);
		check(event);
	}

	@Test
	public void serverName()
	{
		AccessEvent event = createMinimalEvent();
		String value = "value";
		event.setServerName(value);
		check(event);
	}

	@Test
	public void timeStamp()
	{
		AccessEvent event = createMinimalEvent();
		Long value = 1234567890000L;
		event.setTimeStamp(value);
		check(event);
	}

	@Test
	public void localPort()
	{
		AccessEvent event = createMinimalEvent();
		int value = 17;
		event.setLocalPort(value);
		check(event);
	}

	@Test
	public void statusCode()
	{
		AccessEvent event = createMinimalEvent();
		int value = 200;
		event.setStatusCode(value);
		check(event);
	}

	@Test
	public void requestHeaders()
	{
		AccessEvent event = createMinimalEvent();
		Map<String, String> value = new HashMap<String, String>();
		value.put("foo", "bar");
		event.setRequestHeaders(value);
		check(event);
	}

	@Test
	public void emptyRequestHeaders()
	{
		AccessEvent event = createMinimalEvent();
		Map<String, String> value = new HashMap<String, String>();
		event.setRequestHeaders(value);
		check(event);
	}

	@Test
	public void responseHeaders()
	{
		AccessEvent event = createMinimalEvent();
		Map<String, String> value = new HashMap<String, String>();
		value.put("foo", "bar");
		event.setResponseHeaders(value);
		check(event);
	}

	@Test
	public void emptyResponseHeaders()
	{
		AccessEvent event = createMinimalEvent();
		Map<String, String> value = new HashMap<String, String>();
		event.setResponseHeaders(value);
		check(event);
	}

	@Test
	public void requestParameters()
	{
		AccessEvent event = createMinimalEvent();
		Map<String, String[]> value = new HashMap<String, String[]>();
		value.put("foo", new String[]{"val1", "val2"});
		event.setRequestParameters(value);
		check(event);
	}

	@Test
	public void emptyRequestParameters()
	{
		AccessEvent event = createMinimalEvent();
		Map<String, String[]> value = new HashMap<String, String[]>();
		event.setRequestParameters(value);
		check(event);
	}

	@Test
	public void full()
	{
		AccessEvent event = createMinimalEvent();

		check(event);
	}

	public AccessEvent createMinimalEvent()
	{
		return new AccessEvent();
	}

	public void check(AccessEvent event)
	{
		if(logger.isDebugEnabled()) logger.debug("Processing AccessEvent:\n{}", event);
		byte[] bytes;
		AccessEvent readEvent;

		bytes = write(event, false);
		readEvent = read(bytes, false);
		if(logger.isDebugEnabled()) logger.debug("AccessEvent read uncompressed.");
		assertEquals(event, readEvent);

		bytes = write(event, true);
		readEvent = read(bytes, true);
		if(logger.isDebugEnabled()) logger.debug("AccessEvent read compressed.");
		assertEquals(event, readEvent);
	}

	public byte[] write(AccessEvent event, boolean compressing)
	{
		AccessEventProtobufEncoder ser = new AccessEventProtobufEncoder(compressing);
		return ser.encode(event);
	}

	public AccessEvent read(byte[] bytes, boolean compressing)
	{
		AccessEventProtobufDecoder des = new AccessEventProtobufDecoder(compressing);
		return des.decode(bytes);
	}
}
