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
package de.huxhorn.lilith.data.logging.logback;

import de.huxhorn.sulky.codec.Encoder;
import de.huxhorn.lilith.data.eventsource.LoggerContext;

import ch.qos.logback.classic.spi.LoggingEvent;

import java.util.HashMap;
import java.util.Map;

public class TransformingEncoder
		implements Encoder<LoggingEvent>
{
	public static final String APPLICATION_IDENTIFIER_PROPERTY_NAME="applicationIdentifier";

	private LogbackLoggingAdapter adapter = new LogbackLoggingAdapter();
	private Encoder<de.huxhorn.lilith.data.logging.LoggingEvent> lilithEncoder;
	private String applicationIdentifier;
	private final boolean inSameThread;

	public TransformingEncoder(boolean inSameThread)
	{
		this.inSameThread = inSameThread;
	}

	public Encoder<de.huxhorn.lilith.data.logging.LoggingEvent> getLilithEncoder()
	{
		return lilithEncoder;
	}

	public void setLilithEncoder(Encoder<de.huxhorn.lilith.data.logging.LoggingEvent> lilithEncoder)
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

	public boolean isInSameThread()
	{
		return inSameThread;
	}

	public byte[] encode(LoggingEvent logbackEvent)
	{
		de.huxhorn.lilith.data.logging.LoggingEvent lilithEvent = adapter.convert(logbackEvent, inSameThread);
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
