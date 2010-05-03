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
package de.huxhorn.lilith.logback.encoder;

import ch.qos.logback.access.spi.AccessEvent;
import ch.qos.logback.core.recovery.ResilientFileOutputStream;
import de.huxhorn.lilith.api.FileConstants;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class AccessLilithEncoder
	extends LilithEncoderBase<AccessEvent>
{
	private WrappingAccessEncoder wrappingEncoder;

	public AccessLilithEncoder()
	{
		wrappingEncoder = new WrappingAccessEncoder();
		encoder=wrappingEncoder;
	}

	@Override
	public void init(OutputStream os) throws IOException
	{
		super.init(os);
		if(os instanceof ResilientFileOutputStream)
		{
			ResilientFileOutputStream rfos = (ResilientFileOutputStream) os;
			File file = rfos.getFile();
			if(file.length() == 0)
			{
				// write header
				Map<String, String> metaDataMap = new HashMap<String, String>();
				metaDataMap.put(FileConstants.CONTENT_TYPE_KEY, FileConstants.CONTENT_TYPE_VALUE_ACCESS);
				metaDataMap.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF);
				metaDataMap.put(FileConstants.COMPRESSION_KEY, FileConstants.COMPRESSION_VALUE_GZIP);
				writeHeader(metaDataMap);
			}
		}
		else
		{
			throw new IOException("OutputStream wasn't instanceof ResilientFileOutputStream! "+os);
		}
		wrappingEncoder.reset();
	}

	@Override
	protected void preProcess(AccessEvent event)
	{
	}
}
