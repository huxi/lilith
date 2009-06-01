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
package de.huxhorn.lilith.sender;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SimpleSendBytesService
	implements SendBytesService
{
	/**
	 * The default reconnection delay (30000 milliseconds or 30 seconds).
	 */
	public static final int DEFAULT_RECONNECTION_DELAY = 30000;

	public static final int DEFAULT_QUEUE_SIZE = 1000;

	public static final int DEFAULT_POLL_INTERVALL = 100;

	private final BlockingQueue<byte[]> localEventBytes;

	private WriteByteStrategy writeByteStrategy;
	private DataOutputStreamFactory dataOutputStreamFactory;
	//private boolean shutdown;
	private final int reconnectionDelay;
	private final int queueSize;
	private final int pollIntervall;
	private ConnectionState connectionState;
	private SendBytesThread sendBytesThread;
	private boolean debug;

	public SimpleSendBytesService(DataOutputStreamFactory dataOutputStreamFactory, WriteByteStrategy writeByteStrategy)
	{
		this(dataOutputStreamFactory, writeByteStrategy, DEFAULT_QUEUE_SIZE, DEFAULT_RECONNECTION_DELAY, DEFAULT_POLL_INTERVALL);
	}

	public SimpleSendBytesService(DataOutputStreamFactory dataOutputStreamFactory, WriteByteStrategy writeByteStrategy, int queueSize, int reconnectionDelay, int pollIntervall)
	{
		if(dataOutputStreamFactory == null)
		{
			throw new IllegalArgumentException("dataOutputStreamFactory must not be null!");
		}
		if(writeByteStrategy == null)
		{
			throw new IllegalArgumentException("writeByteStrategy must not be null!");
		}
		if(queueSize <= 0)
		{
			throw new IllegalArgumentException("queueSize must be greater than zero!");
		}
		if(reconnectionDelay <= 0)
		{
			throw new IllegalArgumentException("reconnectionDelay must be greater than zero!");
		}
		if(pollIntervall <= 0)
		{
			throw new IllegalArgumentException("pollIntervall must be greater than zero!");
		}
		this.connectionState = ConnectionState.Offline;
		this.localEventBytes = new ArrayBlockingQueue<byte[]>(queueSize, true);
		this.dataOutputStreamFactory = dataOutputStreamFactory;
		this.writeByteStrategy = writeByteStrategy;
		this.queueSize = queueSize;
		this.reconnectionDelay = reconnectionDelay;
		this.pollIntervall = pollIntervall;
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public ConnectionState getConnectionState()
	{
		return connectionState;
	}

	public void sendBytes(byte[] bytes)
	{
		if(sendBytesThread != null && bytes != null) // just to make sure...
		{
			try
			{
				localEventBytes.put(bytes);
			}
			catch(InterruptedException e)
			{
				// ignore
			}
		}
	}

	public synchronized void startUp()
	{
		if(sendBytesThread == null)
		{
			sendBytesThread = new SendBytesThread();
			sendBytesThread.start();
		}
	}

	public synchronized void shutDown()
	{
		connectionState = ConnectionState.Canceled;
		if(sendBytesThread != null)
		{
			sendBytesThread.interrupt();
			sendBytesThread = null;
			localEventBytes.clear();
		}
	}

	private class SendBytesThread
		extends Thread
	{
		private DataOutputStream dataOutputStream;

		public SendBytesThread()
		{
			super("SendBytes@" + dataOutputStreamFactory);
			setDaemon(true);
		}

		public void closeConnection()
		{
			synchronized(SimpleSendBytesService.this)
			{
				if(dataOutputStream != null)
				{
					//IOUtils.closeQuietly(dataOutputStream);
					// the above call can result in a ClassNotFoundException if a
					// webapp is already unloaded!!!
					try
					{
						dataOutputStream.close();
					}
					catch(IOException e)
					{
						// ignore
					}
					dataOutputStream = null;
					if(connectionState != ConnectionState.Canceled)
					{
						connectionState = ConnectionState.Offline;
					}
					if(debug)
					{
						System.err.println("Closed dataOutputStream.");
					}
				}
				SimpleSendBytesService.this.notifyAll();
			}
		}

		public void run()
		{
			Thread reconnectionThread = new ReconnectionThread();
			reconnectionThread.start();

			List<byte[]> copy = new ArrayList<byte[]>(queueSize);
			for(; ;)
			{
				try
				{
					localEventBytes.drainTo(copy);
					if(copy.size() > 0)
					{
						DataOutputStream outputStream;
						synchronized(SimpleSendBytesService.this)
						{
							outputStream = dataOutputStream;
						}
						if(outputStream != null)
						{
//								System.out.println(this+" - about to write "+copy.size()+" events...");
							try
							{
								for(byte[] current : copy)
								{
									writeByteStrategy.writeBytes(outputStream, current);
								}
								outputStream.flush();
//									System.out.println(this+" wrote "+copy.size()+" events.");
							}
							catch(IOException e)
							{
								closeConnection();
							}
							catch(Throwable e)
							{
								closeConnection();
							}
						}
						copy.clear();
					}
//						else
//						{
//							System.out.println(this+" ignored "+copy.size()+" events because of missing connection.");
//						}
					Thread.sleep(pollIntervall);
				}
				catch(InterruptedException e)
				{
					reconnectionThread.interrupt();
					closeConnection();
					shutDown();
					return;
					//e.printStackTrace();
				}
			}
		}

		private class ReconnectionThread
			extends Thread
		{
			public ReconnectionThread()
			{
				super("Reconnection@" + dataOutputStreamFactory);
				setDaemon(true);
			}

			public void run()
			{
				for(; ;)
				{
					boolean connect = false;
					synchronized(SimpleSendBytesService.this)
					{
						if(dataOutputStream == null && connectionState != ConnectionState.Canceled)
						{
							connect = true;
							connectionState = ConnectionState.Connecting;
						}
					}
					DataOutputStream newStream = null;
					if(connect)
					{
						try
						{
							newStream = dataOutputStreamFactory.createDataOutputStream();
						}
						catch(IOException e)
						{
							// ignore
						}
					}

					synchronized(SimpleSendBytesService.this)
					{
						if(connect)
						{
							if(newStream != null)
							{
								if(connectionState == ConnectionState.Canceled)
								{
									// cleanup
									try
									{
										newStream.close();
									}
									catch(IOException e)
									{
										// ignore
									}
								}
								else
								{
									dataOutputStream = newStream;
									connectionState = ConnectionState.Connected;
								}
							}
							else if(connectionState != ConnectionState.Canceled)
							{
								connectionState = ConnectionState.Offline;
							}
						}
						try
						{
							SimpleSendBytesService.this.wait(reconnectionDelay);
						}
						catch(InterruptedException e)
						{
							return;
						}
					}
				}
			}
		}
	}
}
