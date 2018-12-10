/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright 2007-2018 Joern Huxhorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.huxhorn.lilith.data.access.protobuf;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.LoggerContext;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

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
		value.setBirthTime(1_234_567_890_000L);
		Map<String, String> propperties = new HashMap<>();
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
		Long value = 1_234_567_890_000L;
		event.setTimeStamp(value);
		check(event);
	}

	@Test
	public void elapsedTime()
	{
		AccessEvent event = createMinimalEvent();
		Long value = 1_234_567_890_000L;
		event.setElapsedTime(value);
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
		Map<String, String> value = new HashMap<>();
		value.put("foo", "bar");
		event.setRequestHeaders(value);
		check(event);
	}

	@Test
	public void emptyRequestHeaders()
	{
		AccessEvent event = createMinimalEvent();
		Map<String, String> value = new HashMap<>();
		event.setRequestHeaders(value);
		check(event);
	}

	@Test
	public void responseHeaders()
	{
		AccessEvent event = createMinimalEvent();
		Map<String, String> value = new HashMap<>();
		value.put("foo", "bar");
		event.setResponseHeaders(value);
		check(event);
	}

	@Test
	public void emptyResponseHeaders()
	{
		AccessEvent event = createMinimalEvent();
		Map<String, String> value = new HashMap<>();
		event.setResponseHeaders(value);
		check(event);
	}

	@Test
	public void requestParameters()
	{
		AccessEvent event = createMinimalEvent();
		Map<String, String[]> value = new HashMap<>();
		value.put("foo", new String[]{"val1", "val2"});
		event.setRequestParameters(value);
		check(event);
	}

	@Test
	public void emptyRequestParameters()
	{
		AccessEvent event = createMinimalEvent();
		Map<String, String[]> value = new HashMap<>();
		event.setRequestParameters(value);
		check(event);
	}

	@Test
	public void full()
	{
		AccessEvent event = createMinimalEvent();

		check(event);
	}

	private static AccessEvent createMinimalEvent()
	{
		return new AccessEvent();
	}

	private void check(AccessEvent event)
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

	private static byte[] write(AccessEvent event, boolean compressing)
	{
		AccessEventProtobufCodec ser = new AccessEventProtobufCodec(compressing);
		return ser.encode(event);
	}

	private static AccessEvent read(byte[] bytes, boolean compressing)
	{
		AccessEventProtobufCodec des = new AccessEventProtobufCodec(compressing);
		return des.decode(bytes);
	}
}
