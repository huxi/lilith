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

import org.apache.commons.io.IOUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.List;
import java.util.ArrayList;
import java.io.DataOutputStream;
import java.io.IOException;

public class SimpleSendBytesService
	implements SendBytesService
{
	/**
	 * The default reconnection delay (30000 milliseconds or 30 seconds).
	 */
	public static final int DEFAULT_RECONNECTION_DELAY = 30000;

	public static final int DEFAULT_QUEUE_SIZE = 1000;

	public static final int DEFAULT_POLL_INTERVALL = 100;

	private BlockingQueue<byte[]> localEventBytes;

	private WriteByteStrategy writeByteStrategy;
	private DataOutputStreamFactory dataOutputStreamFactory;
	//private boolean shutdown;
	private final int reconnectionDelay;
	private final int queueSize;
	private final int pollIntervall;
	private ConnectionState connectionState;
	private SendBytesThread sendBytesThread;

	public SimpleSendBytesService(DataOutputStreamFactory dataOutputStreamFactory, WriteByteStrategy writeByteStrategy)
	{
		this(dataOutputStreamFactory, writeByteStrategy, DEFAULT_QUEUE_SIZE, DEFAULT_RECONNECTION_DELAY, DEFAULT_POLL_INTERVALL);
	}

	public SimpleSendBytesService(DataOutputStreamFactory dataOutputStreamFactory, WriteByteStrategy writeByteStrategy, int queueSize, int reconnectionDelay, int pollIntervall)
	{
		if(dataOutputStreamFactory==null)
		{
			throw new IllegalArgumentException("dataOutputStreamFactory must not be null!");
		}
		if(writeByteStrategy==null)
		{
			throw new IllegalArgumentException("writeByteStrategy must not be null!");
		}
		if(queueSize<=0)
		{
			throw new IllegalArgumentException("queueSize must be greater than zero!");
		}
		if(reconnectionDelay<=0)
		{
			throw new IllegalArgumentException("reconnectionDelay must be greater than zero!");
		}
		if(pollIntervall<=0)
		{
			throw new IllegalArgumentException("pollIntervall must be greater than zero!");
		}
		this.connectionState= ConnectionState.Offline;
		this.localEventBytes=new ArrayBlockingQueue<byte[]>(queueSize);
		this.dataOutputStreamFactory = dataOutputStreamFactory;
		this.writeByteStrategy=writeByteStrategy;
		this.queueSize=queueSize;
		this.reconnectionDelay=reconnectionDelay;
		this.pollIntervall=pollIntervall;
	}

	public ConnectionState getConnectionState()
	{
		return connectionState;
	}

	public void sendBytes(byte[] bytes)
	{
		if (sendBytesThread!=null && bytes != null) // just to make sure...
		{
			try
			{
				localEventBytes.put(bytes);
			}
			catch (InterruptedException e)
			{
				// ignore
			}
		}
	}

	public synchronized void startUp()
	{
		if(sendBytesThread==null)
		{
			sendBytesThread=new SendBytesThread();
			sendBytesThread.start();
		}
	}

	public synchronized void shutDown()
	{
		if(sendBytesThread!=null)
		{
			sendBytesThread.interrupt();
			sendBytesThread=null;
			localEventBytes.clear();
		}
	}

	private class SendBytesThread
		extends Thread
	{
		private DataOutputStream dataOutputStream;

		public SendBytesThread()
		{
			super("SendBytes@"+dataOutputStreamFactory);
			setDaemon(true);
		}

		public synchronized void closeConnection()
		{
			IOUtils.closeQuietly(dataOutputStream);
			dataOutputStream = null;
			connectionState = ConnectionState.Offline;
			notifyAll();
		}

		public void run()
		{
			Thread reconnectionThread=new ReconnectionThread();
			reconnectionThread.start();

			List<byte[]> copy=new ArrayList<byte[]>(queueSize);
			for (; ;)
			{
				try
				{
					localEventBytes.drainTo(copy);
					if(copy.size()>0)
					{
						if (dataOutputStream != null)
						{
//							System.out.println(this+" - about to write "+copy.size()+" events...");
							try
							{
								for(byte[] current:copy)
								{
									writeByteStrategy.writeBytes(dataOutputStream, current);
								}
								dataOutputStream.flush();
//								System.out.println(this+" wrote "+copy.size()+" events.");
							}
							catch (IOException e)
							{
								closeConnection();
							}
							catch(Throwable e)
							{
								closeConnection();
							}
						}
//						else
//						{
//							System.out.println(this+" ignored "+copy.size()+" events because of missing connection.");
//						}
						copy.clear();
					}
					Thread.sleep(pollIntervall);
				}
				catch (InterruptedException e)
				{
					reconnectionThread.interrupt();
					shutDown();
					return;
					//e.printStackTrace();
				}
			}
		}

		private class ReconnectionThread extends Thread
		{
			public ReconnectionThread()
			{
				super("Reconnection@"+dataOutputStreamFactory);
				setDaemon(true);
			}

			public void run()
			{
				for(;;)
				{
					synchronized(SendBytesThread.this)
					{
						if(dataOutputStream==null)
						{
							try
							{
								connectionState =ConnectionState.Connecting;
								dataOutputStream = dataOutputStreamFactory.createDataOutputStream();
								connectionState = ConnectionState.Connected;
							}
							catch (IOException e)
							{
								connectionState = ConnectionState.Offline;
							}
						}
						try
						{
							SendBytesThread.this.wait(reconnectionDelay);
						}
						catch (InterruptedException e)
						{
							return;
						}
					}
				}
			}
		}
	}
}
