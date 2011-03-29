/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
 * Copyright 2007-2011 Joern Huxhorn
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

package de.huxhorn.lilith.data.logging.json;

import de.huxhorn.lilith.data.logging.LoggingEvent;

import de.huxhorn.lilith.data.logging.test.LoggingEventIOTestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLStreamException;

public class LoggingEventIOTest
	extends LoggingEventIOTestBase
{
	private final Logger logger = LoggerFactory.getLogger(LoggingEventIOTest.class);

	public LoggingEventIOTest(Boolean logging)
	{
		super(logging);
	}

	@Override
	protected void logUncompressedData(byte[] bytes)
	{
		if(logger.isDebugEnabled())
		{
			try
			{
				String data = new String(bytes, "UTF-8");
				logger.debug("Data: {}", data);
			}
			catch(UnsupportedEncodingException ex)
			{
				if(logger.isErrorEnabled()) logger.error("Exception while converting data to string!", ex);
			}
		}
	}

	public byte[] write(LoggingEvent event, boolean compressing)
		throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingJsonEncoder ser = new LoggingJsonEncoder(compressing);
		return ser.encode(event);
	}

	public LoggingEvent read(byte[] bytes, boolean compressing)
		throws XMLStreamException, UnsupportedEncodingException
	{
		LoggingJsonDecoder des = new LoggingJsonDecoder(compressing);
		return des.decode(bytes);
	}
}
