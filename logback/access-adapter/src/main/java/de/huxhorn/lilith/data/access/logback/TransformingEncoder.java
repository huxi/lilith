/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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
package de.huxhorn.lilith.data.access.logback;

import de.huxhorn.sulky.codec.Encoder;
import de.huxhorn.lilith.data.eventsource.LoggerContext;

import ch.qos.logback.access.spi.AccessEvent;

import java.util.HashMap;
import java.util.Map;

public class TransformingEncoder
		implements Encoder<AccessEvent>
{
	public static final String APPLICATION_IDENTIFIER_PROPERTY_NAME="applicationIdentifier";

	private LogbackAccessAdapter adapter = new LogbackAccessAdapter();
	private Encoder<de.huxhorn.lilith.data.access.AccessEvent> lilithEncoder;
	private String applicationIdentifier;

	public Encoder<de.huxhorn.lilith.data.access.AccessEvent> getLilithEncoder()
	{
		return lilithEncoder;
	}

	public void setLilithEncoder(Encoder<de.huxhorn.lilith.data.access.AccessEvent> lilithEncoder)
	{
		this.lilithEncoder = lilithEncoder;
	}

	public String getApplicationIdentifier()
	{
		return applicationIdentifier;
	}

	public void setApplicationIdentifier(String applicationIdentifier)
	{
		this.applicationIdentifier = applicationIdentifier;
	}

	public byte[] encode(AccessEvent logbackEvent)
	{
		de.huxhorn.lilith.data.access.AccessEvent lilithEvent = adapter.convert(logbackEvent);
		if(applicationIdentifier !=null)
		{
			LoggerContext loggerContext = lilithEvent.getLoggerContext();
			if(loggerContext == null)
			{
				loggerContext=new LoggerContext();
			}
			Map<String, String> props = loggerContext.getProperties();
			if(props ==null)
			{
				props=new HashMap<String, String>();
			}
			props.put(APPLICATION_IDENTIFIER_PROPERTY_NAME, applicationIdentifier);
			loggerContext.setProperties(props);
			lilithEvent.setLoggerContext(loggerContext);
		}
		return lilithEncoder.encode(lilithEvent);
	}
}
