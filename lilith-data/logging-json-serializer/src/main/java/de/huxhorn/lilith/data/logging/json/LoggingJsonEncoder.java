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

import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;

import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.lilith.data.logging.json.mixin.ExtendedStackTraceElementMixIn;
import de.huxhorn.lilith.data.logging.json.mixin.LoggerContextMixIn;
import de.huxhorn.lilith.data.logging.json.mixin.LoggingEventMixIn;
import de.huxhorn.lilith.data.logging.json.mixin.MarkerMixIn;
import de.huxhorn.lilith.data.logging.json.mixin.MessageMixIn;
import de.huxhorn.lilith.data.logging.json.mixin.ThreadInfoMixIn;
import de.huxhorn.sulky.codec.Encoder;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class LoggingJsonEncoder
	implements Encoder<LoggingEvent>
{
	private boolean compressing;
	private boolean indenting;
	private boolean sortingProperties;
	private ObjectMapper mapper;

	public LoggingJsonEncoder(boolean compressing)
	{
		this(compressing, false, false);
	}

	public LoggingJsonEncoder(boolean compressing, boolean indenting, boolean sortingProperties)
	{
		mapper = new ObjectMapper();
		SerializationConfig serializationConfig = mapper.getSerializationConfig();
		serializationConfig.addMixInAnnotations(Message.class, MessageMixIn.class);
		serializationConfig.addMixInAnnotations(ExtendedStackTraceElement.class, ExtendedStackTraceElementMixIn.class);
		serializationConfig.addMixInAnnotations(LoggerContext.class, LoggerContextMixIn.class);
		serializationConfig.addMixInAnnotations(Marker.class, MarkerMixIn.class);
		serializationConfig.addMixInAnnotations(ThreadInfo.class, ThreadInfoMixIn.class);
		serializationConfig.addMixInAnnotations(LoggingEvent.class, LoggingEventMixIn.class);
		serializationConfig.addMixInAnnotations(ThrowableInfo.class, ThreadInfoMixIn.class);
		setCompressing(compressing);
		setIndenting(indenting);
		setSortingProperties(sortingProperties);
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	public void setCompressing(boolean compressing)
	{
		this.compressing = compressing;
	}

	public boolean isIndenting()
	{
		return indenting;
	}

	public void setIndenting(boolean indenting)
	{
		this.indenting = indenting;
		SerializationConfig config = mapper.getSerializationConfig();
		if(indenting)
		{
			config = config.with(SerializationConfig.Feature.INDENT_OUTPUT);
		}
		else
		{
			config = config.without(SerializationConfig.Feature.INDENT_OUTPUT);
		}
		mapper.setSerializationConfig(config);
	}

	public boolean isSortingProperties()
	{
		return sortingProperties;
	}

	public void setSortingProperties(boolean sortingProperties)
	{
		this.sortingProperties = sortingProperties;
		SerializationConfig config = mapper.getSerializationConfig();
		if(sortingProperties)
		{
			config = config.with(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY);
		}
		else
		{
			config = config.without(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY);
		}
		mapper.setSerializationConfig(config);
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
			ex.printStackTrace();
		}
		return null;
	}
}
