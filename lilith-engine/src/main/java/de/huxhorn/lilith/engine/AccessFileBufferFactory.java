/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.huxhorn.lilith.engine;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.engine.impl.AccessEventWrapperXmlCodec;
import de.huxhorn.sulky.generics.io.Codec;
import de.huxhorn.sulky.generics.io.SerializableCodec;

import java.util.Map;

public class AccessFileBufferFactory
	extends FileBufferFactory<AccessEvent>
{
	public AccessFileBufferFactory(LogFileFactory logFileFactory, Map<String, String> metaData)
	{
		super(logFileFactory, metaData);
	}

	public Codec<EventWrapper<AccessEvent>> resolveCodec(Map<String, String> metaData)
	{
		boolean compressed = false;
		String format = null;


		if(metaData != null)
		{
			compressed = Boolean.valueOf(metaData.get(FileConstants.COMPRESSED_KEY));
			format = metaData.get(FileConstants.CONTENT_FORMAT_KEY);
		}
		
		Codec<EventWrapper<AccessEvent>> codec;

		if(FileConstants.CONTENT_FORMAT_VALUE_JAVA_BEANS_XML.equals(format))
		{
			codec = new AccessEventWrapperXmlCodec(compressed);
		}
		/*
		else if(FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF.equals(format))
		{
			// TODO: AccessEvent protobuf
		}
		*/
		else
		{
			codec = new SerializableCodec<EventWrapper<AccessEvent>>(compressed);
		}

		return codec;
	}
}
