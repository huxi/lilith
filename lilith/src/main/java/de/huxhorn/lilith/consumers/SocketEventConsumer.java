/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
package de.huxhorn.lilith.consumers;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.engine.EventConsumer;
import de.huxhorn.sulky.io.TimeoutOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

public class SocketEventConsumer<T extends Serializable>
	implements EventConsumer<T>, Runnable
{
	final Logger logger = LoggerFactory.getLogger(SocketEventConsumer.class);

	private static final int DEFAULT_RECONNECTION_DELAY = 60 * 1000;
	private static final int DEFAULT_CONNECTION_TIMEOUT = 10000;
	private static final int DEFAULT_WRITE_TIMEOUT = 1500;

	private ObjectOutputStream output;
	private long failTime;
	private String host;
	private int port;
	private int connectionTimeout;
	private int writeTimeout;
	private long reconnectionDelay;

	public SocketEventConsumer(String host, int port)
	{
		this();
		this.host = host;
		this.port = port;
	}

	public SocketEventConsumer()
	{
		output = null;
		failTime = 0;
		connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
		reconnectionDelay = DEFAULT_RECONNECTION_DELAY;
		writeTimeout = DEFAULT_WRITE_TIMEOUT;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public int getWriteTimeout()
	{
		return writeTimeout;
	}

	public void setWriteTimeout(int writeTimeout)
	{
		this.writeTimeout = writeTimeout;
	}

	public int getConnectionTimeout()
	{
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout)
	{
		this.connectionTimeout = connectionTimeout;
	}

	public long getReconnectionDelay()
	{
		return reconnectionDelay;
	}

	public void setReconnectionDelay(long reconnectionDelay)
	{
		this.reconnectionDelay = reconnectionDelay;
	}


	public void consume(List<EventWrapper<T>> events)
	{
		if(events == null)
		{
			return;
		}
		int eventCount = events.size();
		if(eventCount == 0)
		{
			return;
		}
		if(output == null)
		{
			int count = events.size();
			if(logger.isInfoEnabled()) logger.info("Dropping {} events.", count);
		}
		else
		{
			try
			{
				for(EventWrapper eventWrapper : events)
				{
					Object event = eventWrapper.getEvent();
					if(event != null)
					{
						output.writeObject(eventWrapper.getEvent());
						if(logger.isDebugEnabled()) logger.debug("Wrote event.");
					}
					else
					{
						if(logger.isInfoEnabled())
						{
							logger.info("Detected end of stream for source {}.", eventWrapper.getSourceIdentifier());
						}
					}
				}
				output.flush();
			}
			catch(IOException e)
			{
				if(logger.isInfoEnabled()) logger.info("Exception while writing event.", e);
				IOUtils.closeQuietly(output);
				output = null;
				failTime = System.currentTimeMillis();
			}
		}
	}

	public void run()
	{
		for(; ;)
		{
			if(output == null)
			{
				initObjectOutputStream();
			}
			try
			{
				Thread.sleep(reconnectionDelay);
			}
			catch(InterruptedException e)
			{
				if(logger.isDebugEnabled()) logger.debug("Interrupted...", e);
				break;
			}
		}
	}

	private void initObjectOutputStream()
	{
		if(output != null)
		{
			return;
		}
		long current = System.currentTimeMillis();
		if(current - failTime >= reconnectionDelay)
		{
			SocketAddress address = new InetSocketAddress(host, port);
			try
			{
				Socket socket = new Socket();
				socket.connect(address, connectionTimeout);
				socket.setSoTimeout(connectionTimeout);
				output = new ObjectOutputStream(new BufferedOutputStream(new TimeoutOutputStream(socket.getOutputStream(), writeTimeout)));
				if(logger.isInfoEnabled()) logger.info("Created connection to {}.", address);
			}
			catch(IOException e)
			{
				if(logger.isDebugEnabled()) logger.debug("Exception while creating connection to " + address + ".", e);
				IOUtils.closeQuietly(output);
				output = null;
				failTime = current;
			}
		}
	}
}
