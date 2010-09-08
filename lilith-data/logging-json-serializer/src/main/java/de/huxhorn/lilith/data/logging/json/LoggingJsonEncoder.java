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

package de.huxhorn.lilith.data.logging.json;

import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;

import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.json.mixin.ExtendedStackTraceElementMixIn;
import de.huxhorn.lilith.data.logging.json.mixin.MessageMixIn;
import de.huxhorn.sulky.codec.Encoder;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class LoggingJsonEncoder
	implements Encoder<LoggingEvent>
{
	private boolean compressing;
	private ObjectMapper mapper;

	public LoggingJsonEncoder(boolean compressing)
	{
		this.compressing = compressing;
		mapper = new ObjectMapper();
		mapper.getSerializationConfig().addMixInAnnotations(Message.class, MessageMixIn.class);
		mapper.getSerializationConfig().addMixInAnnotations(ExtendedStackTraceElement.class, ExtendedStackTraceElementMixIn.class);
	}

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
			ex.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return null;
	}
}
