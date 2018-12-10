/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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
 * Copyright 2007-2018 Joern Huxhorn
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

package de.huxhorn.lilith.sender;

import de.huxhorn.sulky.io.TimeoutOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketDataOutputStreamFactory
	implements DataOutputStreamFactory
{
	public static final int DEFAULT_CONNECTION_TIMEOUT = 10_000;
	public static final int DEFAULT_WRITE_TIMEOUT = 5000;

	private final String hostName;
	private final int port;
	private int connectionTimeout;
	private int writeTimeout;

	public SocketDataOutputStreamFactory(String hostName, int port)
	{
		this(hostName, port, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_WRITE_TIMEOUT);
	}

	public SocketDataOutputStreamFactory(String hostName, int port, int connectionTimeout, int writeTimeout)
	{
		this.hostName = hostName;
		this.port = port;
		this.connectionTimeout = connectionTimeout;
		this.writeTimeout = writeTimeout;
	}

	@Override
	public DataOutputStream createDataOutputStream()
		throws IOException
	{
		InetAddress address = InetAddress.getByName(hostName);
		Socket socket = new Socket();
		SocketAddress socketAddress = new InetSocketAddress(address, port);

		socket.connect(socketAddress, connectionTimeout);

		OutputStream os = socket.getOutputStream();

		// BufferedOutputStream bos = new BufferedOutputStream(os);

		// We must not put SocketOutputStream into BufferedOutputStream since its close() method
		// (inherited from FilterOutputStream) calls flush() before actually closing the underlying
		// stream. While this is fine in most cases, it can cause a livelock in this case and prevents
		// the timeout of TimeoutOutputStream from working reliably.
		//
		// DataOutputStream is also a FilterOutputStream (calling flush() in close()) but since
		// SocketOutputStream extends FileOutputStream extends OutputStream, the flush() call is actually a no-op.
		TimeoutOutputStream tos = new TimeoutOutputStream(os, writeTimeout);
		return new DataOutputStream(tos);
	}

	@Override
	public String toString()
	{
		return "SocketDataOutputStreamFactory[hostName="+hostName+", port="+port+", connectionTimeout="+connectionTimeout+", writeTimeout="+writeTimeout+"]";
	}
}
