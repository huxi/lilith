/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
package de.huxhorn.lilith.engine.impl.sourceproducer;

import de.huxhorn.lilith.engine.EventProducer;
import de.huxhorn.lilith.engine.EventSourceProducer;
import de.huxhorn.lilith.engine.SourceManager;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.sulky.buffers.AppendOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO: ServerSocket
// TODO: postprocess
public abstract class AbstractServerSocketEventSourceProducer<T extends Serializable>
	implements EventSourceProducer<T>, Runnable
{
	final Logger logger = LoggerFactory.getLogger(AbstractServerSocketEventSourceProducer.class);

	private ServerSocket serverSocket;
	private AppendOperation<EventWrapper<T>> queue;
	private SimpleDateFormat dateFormat;
	private SourceManager<T> sourceManager;
	private int port;

	public AbstractServerSocketEventSourceProducer(int port)
			throws IOException
	{
		this.port=port;
		serverSocket=new ServerSocket(port);
		dateFormat=new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");
	}

	public AppendOperation<EventWrapper<T>> getQueue()
	{
		return queue;
	}

	public void setQueue(AppendOperation<EventWrapper<T>> queue)
	{
		this.queue = queue;
	}

	public SourceManager<T> getSourceManager()
	{
		return sourceManager;
	}

	public void setSourceManager(SourceManager<T> sourceManager)
	{
		this.sourceManager = sourceManager;
	}

	public int getPort()
	{
		return port;
	}

	public void run()
	{
		for(;;)
		{
			Socket socket;
			try
			{
				socket=serverSocket.accept();
			}
			catch (IOException e)
			{
				if(logger.isInfoEnabled()) logger.info("Closing serverSocket because of exception.", e);
				try
				{
					if(serverSocket!=null)
					{
						serverSocket.close();
					}
				}
				catch (IOException e1)
				{
					if(logger.isInfoEnabled()) logger.info("Exception while closing serverSocket.");
				}
				break;
			}

			try
			{
				SourceIdentifier id=createSourceIdentifier(socket);
				EventProducer producer=createProducer(id, queue, socket.getInputStream());
				producer.start();
				sourceManager.addEventProducer(producer);
			}
			catch (Throwable e)
			{
				if(logger.isInfoEnabled()) logger.info("Exception while creating EventProducer.", e);
			}
		}
	}

	private SourceIdentifier createSourceIdentifier(Socket socket)
	{
		SocketAddress address = socket.getRemoteSocketAddress();
		String primary;
		if( address instanceof InetSocketAddress)
		{
			InetSocketAddress inetSocketAddress=(InetSocketAddress) address;
			InetAddress inetAddress = inetSocketAddress.getAddress();
			primary=inetAddress.getHostAddress();
		}
		else
		{
			primary=""+address;
		}
		String secondary=dateFormat.format(new Date());


		return new SourceIdentifier(primary, secondary);
	}

	protected abstract EventProducer createProducer(SourceIdentifier id,
													AppendOperation<EventWrapper<T>> eventQueue,
													InputStream inputStream)
			throws IOException;
}
