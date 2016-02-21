/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
package de.huxhorn.lilith.services.sender;

import de.huxhorn.lilith.sender.ConnectionState;
import de.huxhorn.lilith.sender.HeartbeatRunnable;
import de.huxhorn.lilith.sender.MessageWriteByteStrategy;
import de.huxhorn.lilith.sender.SimpleSendBytesService;
import de.huxhorn.lilith.sender.SocketDataOutputStreamFactory;

import java.io.Serializable;

import javax.jmdns.JmDNS;

public abstract class AbstractEventSender<T extends Serializable>
	implements EventSender<T>
{
	public static final String COMPRESSED_MDNS_PROPERTY_NAME = "compressed";

	private boolean compressing;
	private String serviceName;
	private String hostName;
	private int port;
	protected SimpleSendBytesService sendBytesService;
	private Thread heartbeatThread;
	private JmDNS jmDns;

	public AbstractEventSender(JmDNS jmDns, String serviceName, String hostName, int port, boolean compressing)
	{
		this.jmDns = jmDns;
		this.serviceName = serviceName;
		this.hostName = hostName;
		this.port = port;
		this.compressing = compressing;

		sendBytesService = new SimpleSendBytesService(new SocketDataOutputStreamFactory(hostName, port), new MessageWriteByteStrategy());
		sendBytesService.startUp();
		heartbeatThread = new Thread(new HeartbeatRunnable(sendBytesService));
		heartbeatThread.setDaemon(true);
		heartbeatThread.start();
	}

	public JmDNS getJmDNS()
	{
		return jmDns;
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	public String getServiceName()
	{
		return serviceName;
	}

	public String getHostAddress()
	{
		return hostName;
	}

	public int getPort()
	{
		return port;
	}

	public void discard()
	{
		heartbeatThread.interrupt();
		sendBytesService.shutDown();
	}

	public boolean isInactive()
	{
		return sendBytesService.getConnectionState() != ConnectionState.Connected;
	}
}
