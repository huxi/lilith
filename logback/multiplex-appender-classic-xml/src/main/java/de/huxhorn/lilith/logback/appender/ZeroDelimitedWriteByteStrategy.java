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
	 * @param bytes			the bytes that are written
	 * @throws java.io.IOException if an exception is thrown while writing the bytes.
	 */
	public void writeBytes(DataOutputStream dataOutputStream, byte[] bytes)
			throws IOException
	{
		if(bytes!=null)
		{
			if(bytes.length>0)
			{
				dataOutputStream.write(bytes);
			}
			dataOutputStream.write(0);
		}
	}
}
