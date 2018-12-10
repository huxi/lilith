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

package de.huxhorn.lilith.logback.appender.xml;

import ch.qos.logback.classic.spi.LoggingEvent;
import de.huxhorn.lilith.data.logging.logback.TransformingEncoder;
import de.huxhorn.lilith.data.logging.xml.codec.LoggingXmlEncoder;
import de.huxhorn.lilith.logback.appender.core.MultiplexSocketAppenderBase;
import de.huxhorn.lilith.sender.ZeroDelimitedWriteByteStrategy;

/**
 * This appender should not be used and is only implemented for symmetric reasons so both an appender
 * and a producer is available. It's only used to debug Lilith.
 *
 * The producer is only used for programming languages that are unable to count bytes, i.e. ActionScript... ;)
 */
public class ZeroDelimitedClassicXmlMultiplexSocketAppender
	extends MultiplexSocketAppenderBase<LoggingEvent>
{
	/**
	 * The default port number of remote logging server (11000).
	 */
	public static final int DEFAULT_PORT = 11_000;

	private boolean includeCallerData;
	private final TransformingEncoder transformingEncoder;

	public ZeroDelimitedClassicXmlMultiplexSocketAppender()
	{
		super(new ZeroDelimitedWriteByteStrategy());
		transformingEncoder =new TransformingEncoder();
		transformingEncoder.setLilithEncoder(new LoggingXmlEncoder(false));
		setEncoder(transformingEncoder);
		includeCallerData = true;
		setPort(DEFAULT_PORT);
	}

	@Override
	protected void applicationIdentifierChanged()
	{
		transformingEncoder.setApplicationIdentifier(getApplicationIdentifier());
	}

	@Override
	protected void uuidChanged()
	{
		transformingEncoder.setUUID(getUUID());
	}

	public void setIncludeCallerData(boolean includeCallerData)
	{
		this.includeCallerData = includeCallerData;
	}

	@Override
	protected void preProcess(LoggingEvent event)
	{
		if(event != null && includeCallerData)
		{
			event.getCallerData();
		}
	}
}
