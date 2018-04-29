/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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
 * Copyright 2007-2017 Joern Huxhorn
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

package de.huxhorn.lilith.log4j2.decoder;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.log4j2.converter.Log4j2LoggingConverter;
import de.huxhorn.sulky.codec.Decoder;
import org.apache.logging.log4j.core.parser.LogEventParser;
import org.apache.logging.log4j.core.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("PMD.ClassNamingConventions")
public class Log4j2DecoderBase
		implements Decoder<LoggingEvent>
{
	private final Logger logger = LoggerFactory.getLogger(Log4j2DecoderBase.class);

	private final LogEventParser parser;
	private final Log4j2LoggingConverter converter = new Log4j2LoggingConverter();

	Log4j2DecoderBase(LogEventParser parser)
	{
		this.parser = parser;
	}

	@Override
	public LoggingEvent decode(byte[] bytes)
	{
		if(bytes == null)
		{
			return null;
		}
		try
		{
			return converter.convert(parser.parseFrom(bytes));
		}
		catch (ParseException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while parsing event!", ex);
		}
		return null;
	}
}
