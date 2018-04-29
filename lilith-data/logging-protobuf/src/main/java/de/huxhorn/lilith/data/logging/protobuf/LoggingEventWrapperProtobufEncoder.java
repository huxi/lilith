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

package de.huxhorn.lilith.data.logging.protobuf;

import de.huxhorn.lilith.data.eventsource.EventIdentifier;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.protobuf.generated.LoggingProto;
import de.huxhorn.sulky.codec.Encoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

class LoggingEventWrapperProtobufEncoder
	implements Encoder<EventWrapper<LoggingEvent>>
{
	private final boolean compressing;

	LoggingEventWrapperProtobufEncoder(boolean compressing)
	{
		this.compressing = compressing;
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	@Override
	@SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
	public byte[] encode(EventWrapper<LoggingEvent> wrapper)
	{
		LoggingProto.EventWrapper converted = convert(wrapper);
		if(converted == null)
		{
			return null;
		}
		if(!compressing)
		{
			return converted.toByteArray();
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gos;
		try
		{
			gos = new GZIPOutputStream(out);
			converted.writeTo(gos);
			gos.flush();
			gos.close();
			return out.toByteArray();
		}
		catch(IOException e)
		{
			// ignore
		}
		return null;
	}

	public static LoggingProto.EventWrapper convert(EventWrapper<LoggingEvent> wrapper)
	{
		if(wrapper == null)
		{
			return null;
		}

		LoggingProto.EventWrapper.Builder builder = LoggingProto.EventWrapper.newBuilder();

		{
			EventIdentifier eventId = wrapper.getEventIdentifier();
			if(eventId != null)
			{
				builder.setEventIdentifier(convert(eventId));
			}
		}

		{
			LoggingEvent event = wrapper.getEvent();
			if(event != null)
			{
				builder.setEvent(LoggingEventProtobufEncoder.convert(event));
			}
		}

		return builder.build();
	}

	public static LoggingProto.EventIdentifier convert(EventIdentifier eventId)
	{
		if(eventId == null)
		{
			return null;
		}
		LoggingProto.EventIdentifier.Builder builder = LoggingProto.EventIdentifier.newBuilder();

		{
			SourceIdentifier sourceId = eventId.getSourceIdentifier();
			if(sourceId != null)
			{
				builder.setSourceIdentifier(convert(sourceId));
			}
		}

		builder.setLocalId(eventId.getLocalId());

		return builder.build();
	}

	public static LoggingProto.SourceIdentifier convert(SourceIdentifier sourceId)
	{
		if(sourceId == null)
		{
			return null;
		}
		LoggingProto.SourceIdentifier.Builder builder = LoggingProto.SourceIdentifier.newBuilder();

		{
			String identifier = sourceId.getIdentifier();
			if(identifier != null)
			{
				builder.setIdentifier(identifier);
			}
		}

		{
			String identifier = sourceId.getSecondaryIdentifier();
			if(identifier != null)
			{
				builder.setSecondaryIdentifier(identifier);
			}
		}

		return builder.build();
	}
}
