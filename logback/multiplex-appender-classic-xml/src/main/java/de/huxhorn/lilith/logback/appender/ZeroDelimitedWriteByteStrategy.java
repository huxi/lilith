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
package de.huxhorn.lilith.logback.appender;

import de.huxhorn.lilith.sender.WriteByteStrategy;

import java.io.DataOutputStream;
import java.io.IOException;

public class ZeroDelimitedWriteByteStrategy
	implements WriteByteStrategy
{
	/**
	 * Writes the byte array if it contains any data, followed by a zero-byte.
	 *
	 * @param dataOutputStream the stream the bytes will be written to.
	 * @param bytes            the bytes that are written
	 * @throws java.io.IOException if an exception is thrown while writing the bytes.
	 */
	public void writeBytes(DataOutputStream dataOutputStream, byte[] bytes)
		throws IOException
	{
		if(bytes != null)
		{
			if(bytes.length > 0)
			{
				dataOutputStream.write(bytes);
			}
			dataOutputStream.write(0);
		}
	}
}
