/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
package de.huxhorn.lilith.sender;

import de.huxhorn.sulky.io.TimeoutOutputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;

public class SocketDataOutputStreamFactory
	implements DataOutputStreamFactory
{
	public static final int DEFAULT_CONNECTION_TIMEOUT = 10000;
	public static final int DEFAULT_WRITE_TIMEOUT = 5000;

	private String hostName;
	private int port;
	private int connectionTimeout;
	private int writeTimeout;

	public SocketDataOutputStreamFactory(String hostName, int port)
	{
		this(hostName, port, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_WRITE_TIMEOUT);
		this.hostName = hostName;
		this.port = port;
	}

	public SocketDataOutputStreamFactory(String hostName, int port, int connectionTimeout, int writeTimeout)
	{
		this.hostName = hostName;
		this.port = port;
		this.connectionTimeout = connectionTimeout;
		this.writeTimeout = writeTimeout;
	}

	public DataOutputStream createDataOutputStream()
			throws IOException
	{
		InetAddress address = InetAddress.getByName(hostName);
		Socket socket = new Socket();
		SocketAddress socketAddress = new InetSocketAddress(address, port);

		socket.connect(socketAddress, connectionTimeout);

		OutputStream os = socket.getOutputStream();

		BufferedOutputStream bos=new BufferedOutputStream(os);
		TimeoutOutputStream tos = new TimeoutOutputStream(bos, writeTimeout);
		return new DataOutputStream(tos);
	}
}
