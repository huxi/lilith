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
import de.huxhorn.sulky.codec.Decoder;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class LoggingEventWrapperProtobufDecoder
	implements Decoder<EventWrapper<LoggingEvent>>
{
	private boolean compressing;

	public LoggingEventWrapperProtobufDecoder(boolean compressing)
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

	public EventWrapper<LoggingEvent> decode(byte[] bytes)
	{
		if(bytes == null)
		{
			return null;
		}
		LoggingProto.EventWrapper parsedEvent = null;
		if(!compressing)
		{
			try
			{
				parsedEvent = LoggingProto.EventWrapper.parseFrom(bytes);
			}
			catch(InvalidProtocolBufferException e)
			{
				// ignore
			}
		}
		else
		{
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			try
			{
				GZIPInputStream gis = new GZIPInputStream(in);
				parsedEvent = LoggingProto.EventWrapper.parseFrom(gis);
				gis.close();
			}
			catch(IOException e)
			{
				// ignore
			}
		}
		return convert(parsedEvent);
	}

	public static EventWrapper<LoggingEvent> convert(LoggingProto.EventWrapper parsedEvent)
	{
		if(parsedEvent == null)
		{
			return null;
		}

		EventWrapper<LoggingEvent> result = new EventWrapper<LoggingEvent>();
		if(parsedEvent.hasEventIdentifier())
		{
			result.setEventIdentifier(convert(parsedEvent.getEventIdentifier()));
		}
		if(parsedEvent.hasEvent())
		{
			result.setEvent(LoggingEventProtobufDecoder.convert(parsedEvent.getEvent()));
		}

		return result;
	}

	public static EventIdentifier convert(LoggingProto.EventIdentifier eventIdentifier)
	{
		if(eventIdentifier == null)
		{
			return null;
		}

		EventIdentifier result = new EventIdentifier();
		if(eventIdentifier.hasSourceIdentifier())
		{
			result.setSourceIdentifier(convert(eventIdentifier.getSourceIdentifier()));
		}
		if(eventIdentifier.hasLocalId())
		{
			result.setLocalId(eventIdentifier.getLocalId());
		}
		return result;
	}

	public static SourceIdentifier convert(LoggingProto.SourceIdentifier sourceIdentifier)
	{
		if(sourceIdentifier == null)
		{
			return null;
		}
		SourceIdentifier result = new SourceIdentifier();
		if(sourceIdentifier.hasIdentifier())
		{
			result.setIdentifier(sourceIdentifier.getIdentifier());
		}
		if(sourceIdentifier.hasSecondaryIdentifier())
		{
			result.setSecondaryIdentifier(sourceIdentifier.getSecondaryIdentifier());
		}
		return result;

	}
}
