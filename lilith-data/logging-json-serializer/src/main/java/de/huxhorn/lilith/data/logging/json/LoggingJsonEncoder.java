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

package de.huxhorn.lilith.data.logging.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.sulky.codec.Encoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class LoggingJsonEncoder
	implements Encoder<LoggingEvent>
{
	private final boolean compressing;
	private final boolean indenting;
	private final boolean sortingProperties;
	private final ObjectMapper mapper;

	public LoggingJsonEncoder(boolean compressing)
	{
		this(compressing, false, false);
	}

	public LoggingJsonEncoder(boolean compressing, boolean indenting, boolean sortingProperties)
	{
		mapper = new ObjectMapper();
		mapper.registerModule(new LoggingModule());
		mapper.registerModule(new AfterburnerModule());

		this.compressing = compressing;
		this.indenting = indenting;
		this.sortingProperties = sortingProperties;

		mapper.configure(SerializationFeature.INDENT_OUTPUT, indenting);
		mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, sortingProperties);
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	public boolean isIndenting()
	{
		return indenting;
	}

	public boolean isSortingProperties()
	{
		return sortingProperties;
	}

	@Override
	@SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
	public byte[] encode(LoggingEvent event)
	{
		ByteArrayOutputStream output=new ByteArrayOutputStream();
		try
		{
			if(!compressing)
			{
				mapper.writeValue(output, event);
				return output.toByteArray();
			}
			GZIPOutputStream gzos=new GZIPOutputStream(output);
			mapper.writeValue(gzos, event);
			gzos.flush();
			gzos.close();
			return output.toByteArray();
		}
		catch(IOException ex)
		{
			ex.printStackTrace(); // NOPMD
		}
		return null;
	}
}
