/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
 * Copyright 2007-2010 Joern Huxhorn
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
package de.huxhorn.lilith.data.access;

import static de.huxhorn.sulky.junit.JUnitTools.testSerialization;
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization;
import de.huxhorn.lilith.data.eventsource.LoggerContext;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AccessEventTest
{
	private AccessEvent fresh;

	@Before
	public void initFresh()
	{
		fresh = new AccessEvent();
	}

	@Test
	public void defaultConstructor()
		throws ClassNotFoundException, IOException
	{
		AccessEvent instance = new AccessEvent();

		testSerialization(instance);
		testXmlSerialization(instance);
	}

	@Test
	public void loggerContext()
		throws ClassNotFoundException, IOException
	{
		AccessEvent instance = new AccessEvent();

		LoggerContext value = new LoggerContext();
		value.setBirthTime(1234567890000L);
		value.setName("contextName");
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("foo", "bar");
		value.setProperties(properties);
		instance.setLoggerContext(value);

		{
			AccessEvent obj = testSerialization(instance);
			assertEquals(value, obj.getLoggerContext());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getLoggerContext());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void method()
		throws ClassNotFoundException, IOException
	{
		AccessEvent instance = new AccessEvent();

		String value = "value";
		instance.setMethod(value);

		{
			AccessEvent obj = testSerialization(instance);
			assertEquals(value, obj.getMethod());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getMethod());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void protocol()
		throws ClassNotFoundException, IOException
	{
		AccessEvent instance = new AccessEvent();

		String value = "value";
		instance.setProtocol(value);

		{
			AccessEvent obj = testSerialization(instance);
			assertEquals(value, obj.getProtocol());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getProtocol());
			assertFalse(fresh.equals(obj));
		}
	}


	@Test
	public void remoteAddress()
		throws ClassNotFoundException, IOException
	{
		AccessEvent instance = new AccessEvent();

		String value = "value";
		instance.setRemoteAddress(value);

		{
			AccessEvent obj = testSerialization(instance);
			assertEquals(value, obj.getRemoteAddress());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getRemoteAddress());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void remoteHost()
		throws ClassNotFoundException, IOException
	{
		AccessEvent instance = new AccessEvent();

		String value = "value";
		instance.setRemoteHost(value);

		{
			AccessEvent obj = testSerialization(instance);
			assertEquals(value, obj.getRemoteHost());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getRemoteHost());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void remoteUser()
		throws ClassNotFoundException, IOException
	{
		AccessEvent instance = new AccessEvent();

		String value = "value";
		instance.setRemoteUser(value);

		{
			AccessEvent obj = testSerialization(instance);
			assertEquals(value, obj.getRemoteUser());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getRemoteUser());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void requestUri()
		throws ClassNotFoundException, IOException
	{
		AccessEvent instance = new AccessEvent();

		String value = "value";
		instance.setRequestURI(value);

		{
			AccessEvent obj = testSerialization(instance);
			assertEquals(value, obj.getRequestURI());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getRequestURI());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void requestUrl()
		throws ClassNotFoundException, IOException
	{
		AccessEvent instance = new AccessEvent();

		String value = "value";
		instance.setRequestURL(value);

		{
			AccessEvent obj = testSerialization(instance);
			assertEquals(value, obj.getRequestURL());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getRequestURL());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void serverName()
		throws ClassNotFoundException, IOException
	{
		AccessEvent instance = new AccessEvent();

		String value = "value";
		instance.setServerName(value);

		{
			AccessEvent obj = testSerialization(instance);
			assertEquals(value, obj.getServerName());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getServerName());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void requestHeaders()
		throws ClassNotFoundException, IOException
	{
		AccessEvent instance = new AccessEvent();

		Map<String, String> value = new HashMap<String, String>();
		value.put("foo", "bar");
		instance.setRequestHeaders(value);

		{
			AccessEvent obj = testSerialization(instance);
			assertEquals(value, obj.getRequestHeaders());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getRequestHeaders());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void requestParameters()
		throws ClassNotFoundException, IOException
	{
		AccessEvent instance = new AccessEvent();

		Map<String, String[]> value = new HashMap<String, String[]>();
		String[] array = new String[]{"bar"};
		value.put("foo", array);
		instance.setRequestParameters(value);

		{
			AccessEvent obj = testSerialization(instance);
			assertArrayEquals(array, obj.getRequestParameters().get("foo"));
			//assertEquals(value, obj.getRequestParameters());
			//assertFalse(fresh.equals(obj));
		}
		{
			AccessEvent obj = testXmlSerialization(instance);
			assertArrayEquals(array, obj.getRequestParameters().get("foo"));
			//assertEquals(value, obj.getRequestParameters());
			//assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void responseHeaders()
		throws ClassNotFoundException, IOException
	{
		AccessEvent instance = new AccessEvent();

		Map<String, String> value = new HashMap<String, String>();
		value.put("foo", "bar");
		instance.setResponseHeaders(value);

		{
			AccessEvent obj = testSerialization(instance);
			assertEquals(value, obj.getResponseHeaders());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getResponseHeaders());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void timeStamp()
		throws ClassNotFoundException, IOException
	{
		AccessEvent instance = new AccessEvent();

		Long value = 1234567890000L;
		instance.setTimeStamp(value);

		{
			AccessEvent obj = testSerialization(instance);
			assertEquals(value, obj.getTimeStamp());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getTimeStamp());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void localPort()
		throws ClassNotFoundException, IOException
	{
		AccessEvent instance = new AccessEvent();

		int value = 17;
		instance.setLocalPort(value);

		{
			AccessEvent obj = testSerialization(instance);
			assertEquals(value, obj.getLocalPort());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getLocalPort());
			assertFalse(fresh.equals(obj));
		}
	}

	@Test
	public void statusCode()
		throws ClassNotFoundException, IOException
	{
		AccessEvent instance = new AccessEvent();

		int value = 17;
		instance.setStatusCode(value);

		{
			AccessEvent obj = testSerialization(instance);
			assertEquals(value, obj.getStatusCode());
			assertFalse(fresh.equals(obj));
		}
		{
			AccessEvent obj = testXmlSerialization(instance);
			assertEquals(value, obj.getStatusCode());
			assertFalse(fresh.equals(obj));
		}
	}
}
