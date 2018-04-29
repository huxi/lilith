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

package de.huxhorn.lilith.logback.appender.core;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import de.huxhorn.lilith.sender.HeartbeatRunnable;
import de.huxhorn.lilith.sender.MessageWriteByteStrategy;
import de.huxhorn.lilith.sender.MultiplexSendBytesService;
import de.huxhorn.lilith.sender.WriteByteStrategy;
import de.huxhorn.sulky.codec.Encoder;
import de.huxhorn.sulky.ulid.ULID;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class MultiplexSocketAppenderBase<E>
	extends UnsynchronizedAppenderBase<E>
{
	private static final int DEFAULT_QUEUE_SIZE = 1000;

	private Encoder<E> encoder;
	private int port;
	private final List<String> remoteHostsList;
	private String applicationIdentifier;
	private Thread heartbeatThread;
	private long reconnectionDelay;
	private WriteByteStrategy writeByteStrategy;
	private int queueSize;
	private MultiplexSendBytesService multiplexSendBytes;
	private boolean debug;
	private boolean creatingUUID=true;
	private String uuid;

	public MultiplexSocketAppenderBase()
	{
		this(new MessageWriteByteStrategy());
	}

	public MultiplexSocketAppenderBase(WriteByteStrategy writeByteStrategy)
	{
		this(writeByteStrategy, DEFAULT_QUEUE_SIZE);
	}

	private MultiplexSocketAppenderBase(WriteByteStrategy writeByteStrategy, int queueSize)
	{
		this.writeByteStrategy = writeByteStrategy;
		this.queueSize = queueSize;
		remoteHostsList=new ArrayList<>();
	}

	public void setCreatingUUID(boolean creatingUUID)
	{
		this.creatingUUID = creatingUUID;
	}

	protected String getUUID()
	{
		return uuid;
	}

	private void setUUID(String uuid)
	{
		this.uuid = uuid;
		uuidChanged();
	}

	protected abstract void uuidChanged();

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	protected String getApplicationIdentifier()
	{
		return applicationIdentifier;
	}

	public void setApplicationIdentifier(String applicationIdentifier)
	{
		this.applicationIdentifier = applicationIdentifier;
		applicationIdentifierChanged();
	}

	protected abstract void applicationIdentifierChanged();

	public void setReconnectionDelay(long reconnectionDelay)
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

	/**
	 * Sets the remote host list by splitting the string remoteHosts. It is expected to be comma-separated.
	 *
	 * @param remoteHosts comma-separated list of hosts.
	 */
	@SuppressWarnings("unused")
	public void setRemoteHosts(String remoteHosts)
	{
		StringTokenizer tok = new StringTokenizer(remoteHosts, ",", false);
		List<String> hosts = new ArrayList<>();
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
	 *
	 * This method should also be called setRemoteHosts but Joran explodes if it has the same name as the String version.
	 *
	 * @param remoteHostsList the list of remote hosts.
	 */
	@SuppressWarnings("WeakerAccess")
	public void setRemoteHostsList(List<String> remoteHostsList)
	{
		if(debug)
		{
			System.err.println("RemoteHosts: " + remoteHostsList); // NOPMD
		}
		this.remoteHostsList.clear();
		this.remoteHostsList.addAll(remoteHostsList);
	}


	@SuppressWarnings("unused")
	public void addRemoteHost(String remoteHost)
	{
		remoteHost=remoteHost.trim();
		if(!"".equals(remoteHost) && !this.remoteHostsList.contains(remoteHost))
		{
			this.remoteHostsList.add(remoteHost);
		}
	}

	/**
	 * Start this appender.
	 */
	@Override
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

			if(remoteHostsList == null || remoteHostsList.isEmpty())
			{
				errorCount++;
				addError("No remote addresses were configured for appender" + name + ".");
			}

			if(queueSize < 1)
			{
				errorCount++;
				addError("Invalid queue size configured for appender" + name + ". Queue size must be at least 1!");
			}

			if(creatingUUID)
			{
				//setUUID(UUID.randomUUID().toString());
				setUUID(ULIDHolder.ULID.nextULID());
			}
			else
			{
				setUUID(null);
			}

			if(errorCount == 0)
			{
				initialize();
				this.started = true;
				addInfo("Waiting 1s to establish connections.");
				try
				{
					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{
					// ignore
				}
				addInfo("Started " + this);
			}
		}
	}


	private void initialize()
	{
		if(multiplexSendBytes != null)
		{
			multiplexSendBytes.shutDown();
		}
		multiplexSendBytes = new MultiplexSendBytesService(name, remoteHostsList, port, writeByteStrategy, reconnectionDelay, queueSize);
		multiplexSendBytes.setDebug(debug);
		multiplexSendBytes.startUp();

		// TODO: add support for ip.ip.ip.ip:port
		if(heartbeatThread != null)
		{
			heartbeatThread.interrupt();
			try
			{
				heartbeatThread.join();
			}
			catch(InterruptedException e)
			{
				// this is ok.
			}
		}
		heartbeatThread = new Thread(new HeartbeatRunnable(multiplexSendBytes), name + " Heartbeat");
		heartbeatThread.setDaemon(true);
		heartbeatThread.start();
	}

	/**
	 * Stop this appender.
	 *
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
		try
		{
			heartbeatThread.join();
		}
		catch(InterruptedException e)
		{
			// this is ok.
		}
		heartbeatThread = null;
		multiplexSendBytes.shutDown();
		multiplexSendBytes = null;
	}

	private void sendBytes(byte[] bytes)
	{
		if(multiplexSendBytes != null)
		{
			multiplexSendBytes.sendBytes(bytes);
		}
	}

	@Override
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

	protected void setEncoder(Encoder<E> encoder)
	{
		this.encoder = encoder;
	}

	// lazily initialized ULID instance
	private static class ULIDHolder
	{
		static final ULID ULID = new ULID();
	}
}
