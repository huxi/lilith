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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleSendBytesService
	implements SendBytesService
{
	/**
	 * The default reconnection delay (30000 milliseconds or 30 seconds).
	 */
	public static final int DEFAULT_RECONNECTION_DELAY = 30_000;

	public static final int DEFAULT_QUEUE_SIZE = 1000;

	public static final int DEFAULT_POLL_INTERVAL = 100;

	private final Object lock = new Object();

	private final DataOutputStreamFactory dataOutputStreamFactory;
	private final WriteByteStrategy writeByteStrategy;
	private final int queueSize;
	private final long reconnectionDelay;
	private final int pollInterval;

	private final BlockingQueue<byte[]> localEventBytes;

	private final AtomicReference<ConnectionState> connectionState=new AtomicReference<>(ConnectionState.OFFLINE);
	private final AtomicBoolean shutdownIndicator = new AtomicBoolean(false);

	private SendBytesThread sendBytesThread;
	private boolean debug;

	public SimpleSendBytesService(DataOutputStreamFactory dataOutputStreamFactory, WriteByteStrategy writeByteStrategy)
	{
		this(dataOutputStreamFactory, writeByteStrategy, DEFAULT_QUEUE_SIZE, DEFAULT_RECONNECTION_DELAY, DEFAULT_POLL_INTERVAL);
	}

	public SimpleSendBytesService(DataOutputStreamFactory dataOutputStreamFactory, WriteByteStrategy writeByteStrategy, int queueSize, long reconnectionDelay, int pollInterval)
	{
		this.dataOutputStreamFactory = Objects.requireNonNull(dataOutputStreamFactory, "dataOutputStreamFactory must not be null!");
		this.writeByteStrategy = Objects.requireNonNull(writeByteStrategy, "writeByteStrategy must not be null!");

		if(queueSize <= 0)
		{
			throw new IllegalArgumentException("queueSize must be greater than zero but was "+queueSize+"!");
		}
		this.queueSize = queueSize;

		if(reconnectionDelay <= 0)
		{
			throw new IllegalArgumentException("reconnectionDelay must be greater than zero but was "+reconnectionDelay+"!");
		}
		this.reconnectionDelay = reconnectionDelay;

		if(pollInterval <= 0)
		{
			throw new IllegalArgumentException("pollInterval must be greater than zero but was "+pollInterval+"!");
		}
		this.pollInterval = pollInterval;

		this.localEventBytes = new ArrayBlockingQueue<>(queueSize, true);
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
		return connectionState.get();
	}

	@Override
	public void sendBytes(byte[] bytes)
	{
		if(connectionState.get() == ConnectionState.CONNECTED && sendBytesThread != null && bytes != null)
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

	@Override
	public void startUp()
	{
		synchronized(lock)
		{
			if(sendBytesThread == null)
			{
				shutdownIndicator.set(false);
				sendBytesThread = new SendBytesThread();
				sendBytesThread.start();
			}
		}
	}

	@Override
	public void shutDown()
	{
		shutdownIndicator.set(true);
		synchronized(lock)
		{
			connectionState.set(ConnectionState.CANCELED);
		}
		if(sendBytesThread != null)
		{
			sendBytesThread.interrupt();
			try
			{
				sendBytesThread.join();
			}
			catch(InterruptedException e)
			{
				// this is ok
			}
			sendBytesThread = null;
		}
		localEventBytes.clear();
	}

	private class SendBytesThread
		extends Thread
	{
		private DataOutputStream dataOutputStream;

		SendBytesThread()
		{
			super("SendBytes@" + dataOutputStreamFactory);
			setDaemon(true);
		}

		void closeConnection()
		{
			synchronized(lock)
			{
				if(dataOutputStream != null)
				{
					//IOUtilities.closeQuietly(dataOutputStream);
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
					if(connectionState.get() != ConnectionState.CANCELED)
					{
						connectionState.set(ConnectionState.OFFLINE);
					}
					if(debug)
					{
						System.err.println("Closed dataOutputStream."); // NOPMD
					}
				}
				lock.notifyAll();
			}
		}

		@Override
		public void run()
		{
			Thread reconnectionThread = new ReconnectionThread();
			reconnectionThread.start();

			List<byte[]> copy = new ArrayList<>(queueSize);
			for(;;)
			{
				try
				{
					localEventBytes.drainTo(copy);
					if(!copy.isEmpty())
					{
						DataOutputStream outputStream;
						synchronized(lock)
						{
							outputStream = dataOutputStream;
						}
						if(outputStream != null)
						{
							try
							{
								for(byte[] current : copy)
								{
									writeByteStrategy.writeBytes(outputStream, current);
								}
								outputStream.flush();
							}
							catch(Throwable e)
							{
								closeConnection();
							}
						}
						copy.clear();
					}
					if(shutdownIndicator.get())
					{
						break;
					}
					Thread.sleep(pollInterval);
				}
				catch(InterruptedException e)
				{
					break;
				}
			}
			reconnectionThread.interrupt();
			try
			{
				reconnectionThread.join();
			}
			catch(InterruptedException e)
			{
				// this is ok.
			}
			closeConnection();
		}

		private class ReconnectionThread
			extends Thread
		{
			ReconnectionThread()
			{
				super("Reconnection@" + dataOutputStreamFactory);
				setDaemon(true);
			}

			@Override
			public void run()
			{
				for(;;)
				{
					boolean connect = false;
					synchronized(lock)
					{
						if(dataOutputStream == null && connectionState.get() != ConnectionState.CANCELED)
						{
							connect = true;
							connectionState.set(ConnectionState.CONNECTING);
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

					synchronized(lock)
					{
						if(connect)
						{
							if(newStream != null)
							{
								if(connectionState.get() == ConnectionState.CANCELED)
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
									connectionState.set(ConnectionState.CONNECTED);
								}
							}
							else if(connectionState.get() != ConnectionState.CANCELED)
							{
								connectionState.set(ConnectionState.OFFLINE);
							}
						}
						try
						{
							lock.wait(reconnectionDelay);
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
