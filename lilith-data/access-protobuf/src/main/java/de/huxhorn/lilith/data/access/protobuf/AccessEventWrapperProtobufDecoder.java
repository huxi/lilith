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

package de.huxhorn.lilith.data.access.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.access.protobuf.generated.AccessProto;
import de.huxhorn.lilith.data.eventsource.EventIdentifier;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.sulky.codec.Decoder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class AccessEventWrapperProtobufDecoder
	implements Decoder<EventWrapper<AccessEvent>>
{
	private final boolean compressing;

	public AccessEventWrapperProtobufDecoder(boolean compressing)
	{
		this.compressing = compressing;
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	@Override
	public EventWrapper<AccessEvent> decode(byte[] bytes)
	{
		if(bytes == null)
		{
			return null;
		}
		AccessProto.EventWrapper parsedEvent = null;
		if(!compressing)
		{
			try
			{
				parsedEvent = AccessProto.EventWrapper.parseFrom(bytes);
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
				parsedEvent = AccessProto.EventWrapper.parseFrom(gis);
				gis.close();
			}
			catch(IOException e)
			{
				// ignore
			}
		}
		return convert(parsedEvent);
	}

	public static EventWrapper<AccessEvent> convert(AccessProto.EventWrapper parsedEvent)
	{
		if(parsedEvent == null)
		{
			return null;
		}

		EventWrapper<AccessEvent> result = new EventWrapper<>();
		if(parsedEvent.hasEventIdentifier())
		{
			result.setEventIdentifier(convert(parsedEvent.getEventIdentifier()));
		}
		if(parsedEvent.hasEvent())
		{
			result.setEvent(AccessEventProtobufDecoder.convert(parsedEvent.getEvent()));
		}

		return result;
	}

	public static EventIdentifier convert(AccessProto.EventIdentifier eventIdentifier)
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

	public static SourceIdentifier convert(AccessProto.SourceIdentifier sourceIdentifier)
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
