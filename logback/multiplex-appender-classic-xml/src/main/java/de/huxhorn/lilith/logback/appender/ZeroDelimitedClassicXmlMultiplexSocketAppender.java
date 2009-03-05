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
package de.huxhorn.lilith.logback.appender;

import de.huxhorn.lilith.data.logging.logback.LogbackLoggingAdapter;
import de.huxhorn.lilith.data.logging.xml.LoggingXmlEncoder;
import de.huxhorn.sulky.codec.Encoder;

import ch.qos.logback.classic.spi.LoggingEvent;

/**
 * This appender should not be used and is only implemented for symmetric reasons so both an appender
 * and a producer is available. It's only used to debug Lilith.
 * <p/>
 * The producer is only used for programming languages that are unable to count bytes, i.e. ActionScript... ;)
 */
public class ZeroDelimitedClassicXmlMultiplexSocketAppender
	extends MultiplexSocketAppenderBase<LoggingEvent>
{
	/**
	 * The default port number of remote logging server (11000).
	 */
	public static final int DEFAULT_PORT = 11000;

	private boolean includeCallerData;

	public ZeroDelimitedClassicXmlMultiplexSocketAppender()
	{
		super(new ZeroDelimitedWriteByteStrategy());
		includeCallerData = true;
		setEncoder(new TransformingSerializer());
		setPort(DEFAULT_PORT);
	}

//	@Override
//	public void setPort(int port)
//	{
//		super.setPort(port);
//	}

	public boolean isIncludeCallerData()
	{
		return includeCallerData;
	}

	public void setIncludeCallerData(boolean includeCallerData)
	{
		this.includeCallerData = includeCallerData;
	}

	protected void preProcess(LoggingEvent event)
	{
		if(event != null)
		{
			if(includeCallerData)
			{
				event.getCallerData();
			}
		}
	}

	private class TransformingSerializer
		implements Encoder<LoggingEvent>
	{
		LogbackLoggingAdapter adapter = new LogbackLoggingAdapter();
		Encoder<de.huxhorn.lilith.data.logging.LoggingEvent> internalSerializer;

		private TransformingSerializer()
		{
			internalSerializer = new LoggingXmlEncoder(false);
		}

		public byte[] encode(LoggingEvent logbackEvent)
		{
			de.huxhorn.lilith.data.logging.LoggingEvent lilithEvent = adapter.convert(logbackEvent);
			lilithEvent.setApplicationIdentifier(getApplicationIdentifier());
			return internalSerializer.encode(lilithEvent);
		}
	}
}
