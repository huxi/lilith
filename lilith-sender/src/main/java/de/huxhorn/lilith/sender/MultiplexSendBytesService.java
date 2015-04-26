/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
 * Copyright 2007-2011 Joern Huxhorn
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MultiplexSendBytesService
	implements SendBytesService
{
	private int queueSize;

	private Set<SimpleSendBytesService> senderServices;
	private final BlockingQueue<byte[]> eventBytes;
	private List<String> remoteHostsList;
	private Thread dispatcherThread;
	private String name;
	private WriteByteStrategy writeByteStrategy;
	private int port;
	private int reconnectionDelay;
	private boolean debug;

	public MultiplexSendBytesService(String name, List<String> remoteHostsList, int port, WriteByteStrategy writeByteStrategy, int reconnectionDelay, int queueSize)
	{
		this.name = name;
		this.queueSize = queueSize;
		this.remoteHostsList = remoteHostsList;
		this.senderServices = new HashSet<>();
		this.eventBytes = new ArrayBlockingQueue<>(queueSize, true);
		this.writeByteStrategy = writeByteStrategy;
		this.port = port;
		this.reconnectionDelay = reconnectionDelay;
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public void startUp()
	{
		if(dispatcherThread == null)
		{
			for(String hostName : remoteHostsList)
			{
				DataOutputStreamFactory dataOutputStreamFactory = new SocketDataOutputStreamFactory(hostName, port);
				//SimpleSendBytesService(DataOutputStreamFactory dataOutputStreamFactory, WriteByteStrategy writeByteStrategy, int queueSize, int reconnectionDelay, int pollIntervall)
				SimpleSendBytesService service = new SimpleSendBytesService(
					dataOutputStreamFactory,
					writeByteStrategy,
					queueSize,
					reconnectionDelay,
					SimpleSendBytesService.DEFAULT_POLL_INTERVALL);
				service.setDebug(debug);
				senderServices.add(service);
				service.startUp();
			}

			dispatcherThread = new Thread(new DispatcherRunnable(), name + " Dispatcher");
			dispatcherThread.setDaemon(true);
			dispatcherThread.start();
		}
	}

	public void shutDown()
	{
		if(dispatcherThread != null)
		{
			dispatcherThread.interrupt();
			for(SimpleSendBytesService current : senderServices)
			{
				current.shutDown();
			}
			senderServices.clear();
			eventBytes.clear();
			try
			{
				dispatcherThread.join();
			}
			catch(InterruptedException e)
			{
				// this is ok.
			}
			dispatcherThread = null;
		}
	}

	public void sendBytes(byte[] serialized)
	{
		try
		{
			eventBytes.put(serialized);
		}
		catch(InterruptedException e)
		{
			// ignore
		}
	}

	private class DispatcherRunnable
		implements Runnable
	{
		public void run()
		{
			for(; ;)
			{
				try
				{
					byte[] bytes = eventBytes.take();
					for(SimpleSendBytesService current : senderServices)
					{
						current.sendBytes(bytes);
					}
				}
				catch(InterruptedException e)
				{
					return;
				}
			}
		}
	}

}
