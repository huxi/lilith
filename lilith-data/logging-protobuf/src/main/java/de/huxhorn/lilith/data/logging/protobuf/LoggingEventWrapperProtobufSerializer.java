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
package de.huxhorn.lilith.data.logging.protobuf;

import de.huxhorn.lilith.data.eventsource.EventIdentifier;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.protobuf.generated.LoggingProto;
import de.huxhorn.sulky.generics.io.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class LoggingEventWrapperProtobufSerializer
	implements Serializer<EventWrapper<LoggingEvent>>
{
	private boolean compressing;

	public LoggingEventWrapperProtobufSerializer(boolean compressing)
	{
		this.compressing = compressing;
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	public void setCompressing(boolean compressing)
	{
		this.compressing = compressing;
	}

	public byte[] serialize(EventWrapper<LoggingEvent> wrapper)
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
				builder.setEvent(LoggingEventProtobufSerializer.convert(event));
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
