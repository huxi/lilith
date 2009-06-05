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


import de.huxhorn.lilith.sender.HeartbeatRunnable;
import de.huxhorn.lilith.sender.MessageWriteByteStrategy;
import de.huxhorn.lilith.sender.MultiplexSendBytesService;
import de.huxhorn.lilith.sender.WriteByteStrategy;
import de.huxhorn.sulky.codec.Encoder;

import ch.qos.logback.core.UnsynchronizedAppenderBase;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class MultiplexSocketAppenderBase<E>
	extends UnsynchronizedAppenderBase<E>
{
	private static final int DEFAULT_QUEUE_SIZE = 1000;

	private Encoder<E> encoder;
	private int port;
	private List<String> remoteHostsList;
	private String applicationIdentifier;
	private Thread heartbeatThread;
	private int reconnectionDelay;
	private WriteByteStrategy writeByteStrategy;
	private int queueSize;
	private MultiplexSendBytesService multiplexSendBytes;
	private boolean debug;

	public MultiplexSocketAppenderBase()
	{
		this(new MessageWriteByteStrategy());
	}

	public MultiplexSocketAppenderBase(WriteByteStrategy writeByteStrategy)
	{
		this(writeByteStrategy, DEFAULT_QUEUE_SIZE);
	}

	public MultiplexSocketAppenderBase(WriteByteStrategy writeByteStrategy, int queueSize)
	{
		this.writeByteStrategy = writeByteStrategy;
		setQueueSize(queueSize);
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public int getQueueSize()
	{
		return queueSize;
	}

	public void setQueueSize(int queueSize)
	{
		this.queueSize = queueSize;
	}

	public String getApplicationIdentifier()
	{
		return applicationIdentifier;
	}

	public void setApplicationIdentifier(String applicationIdentifier)
	{
		this.applicationIdentifier = applicationIdentifier;
		applicationIdentifierChanged();
	}

	protected abstract void applicationIdentifierChanged(); 

	public int getReconnectionDelay()
	{
		return reconnectionDelay;
	}

	public void setReconnectionDelay(int reconnectionDelay)
	{
		this.reconnectionDelay = reconnectionDelay;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public List<String> getRemoteHostsList()
	{
		return new ArrayList<String>(remoteHostsList);
	}

	/**
	 * Sets the remote host list by splitting the string remoteHosts. It is expected to be comma-separated.
	 *
	 * @param remoteHosts comma-seperated list of hosts.
	 */
	public void setRemoteHosts(String remoteHosts)
	{
		StringTokenizer tok = new StringTokenizer(remoteHosts, ",", false);
		List<String> hosts = new ArrayList<String>();
		while(tok.hasMoreTokens())
		{
			String current = tok.nextToken();
			current = current.trim();
			if(!"".equals(current) && !hosts.contains(current))
			{
				hosts.add(current);
			}
		}
		setRemoteHostsList(hosts);
	}

	/**
	 * Sets the list of remote hosts.
	 * <p/>
	 * This method should also be called setRemoteHosts but Joran explodes if it has the same name as the String version.
	 *
	 * @param remoteHostsList the list of remote hosts.
	 */
	public void setRemoteHostsList(List<String> remoteHostsList)
	{
		if(debug)
		{
			System.err.println("RemoteHosts: " + remoteHostsList);
		}
		this.remoteHostsList = remoteHostsList;
	}

	/**
	 * Start this appender.
	 */
	public void start()
	{
		if(!started)
		{
			int errorCount = 0;
			if(port == 0)
			{
				errorCount++;
				addError("No port was configured for appender" + name + ".");
			}

			if(remoteHostsList == null || remoteHostsList.size() == 0)
			{
				errorCount++;
				addError("No remote addresses were configured for appender" + name + ".");
			}

			if(queueSize < 1)
			{
				errorCount++;
				addError("Invalid queue size configured for appender" + name + ". Queue size must be at least 1!");
			}
			if(errorCount == 0)
			{
				initialize();
				this.started = true;
			}
			addInfo("Started " + this);
		}
	}


	private void initialize()
	{
		multiplexSendBytes = new MultiplexSendBytesService(name, remoteHostsList, port, writeByteStrategy, reconnectionDelay, queueSize);
		multiplexSendBytes.setDebug(debug);
		multiplexSendBytes.startUp();

		// TODO: add support for ip.ip.ip.ip:port
		heartbeatThread = new Thread(new HeartbeatRunnable(multiplexSendBytes), name + " Heartbeat");
		heartbeatThread.setDaemon(true);
		heartbeatThread.start();
	}

	/**
	 * Stop this appender.
	 * <p/>
	 * This will mark the appender as closed and calls the {@link #cleanUp}
	 * method.
	 */
	@Override
	public void stop()
	{
		if(!isStarted())
		{
			return;
		}

		this.started = false;
		cleanUp();
	}

	private void cleanUp()
	{
		addInfo("Cleaning up " + this + ".");
		heartbeatThread.interrupt();
		multiplexSendBytes.shutDown();
	}

	protected void sendBytes(byte[] bytes)
	{
		multiplexSendBytes.sendBytes(bytes);
	}

	protected void append(E e)
	{
		if(encoder != null)
		{
			preProcess(e);
			byte[] serialized = encoder.encode(e);
			if(serialized != null)
			{
				sendBytes(serialized);
			}
		}
	}

	protected abstract void preProcess(E e);

	protected Encoder<E> getEncoder()
	{
		return encoder;
	}

	protected void setEncoder(Encoder<E> encoder)
	{
		this.encoder = encoder;
	}
}
