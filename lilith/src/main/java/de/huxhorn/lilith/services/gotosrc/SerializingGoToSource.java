/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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

package de.huxhorn.lilith.services.gotosrc;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class sends serialized StackTraceElements to a server.
 * By default, this server is expected to be running on port 11111.
 */
public class SerializingGoToSource
	implements GoToSource
{
	public static final int DEFAULT_PORT = 11_111;

	private final BlockingQueue<StackTraceElement> queue=new LinkedBlockingQueue<>();
	private final GoToSourceRunnable goToSourceRunnable;
	private Thread goToSourceThread=null;

	public SerializingGoToSource(int port)
	{
		goToSourceRunnable = new GoToSourceRunnable();
		setPort(port);
	}

	public SerializingGoToSource()
	{
		this(DEFAULT_PORT);
	}

	public int getPort()
	{
		return goToSourceRunnable.getPort();
	}

	public void setPort(int port)
	{
		goToSourceRunnable.setPort(port);
	}

	@Override
	public void goToSource(StackTraceElement ste)
	{
		if(goToSourceThread == null)
		{
			goToSourceThread=new Thread(goToSourceRunnable);
			goToSourceThread.setDaemon(true);
			goToSourceThread.start();
		}
		if(ste == null)
		{
			return;
		}
		try
		{
			queue.put(ste);
		}
		catch(InterruptedException e)
		{
			stop();
		}
	}

	@Override
	public void stop()
	{
		if(goToSourceThread != null)
		{
			goToSourceThread.interrupt();
			goToSourceThread=null;
		}
	}

	private class GoToSourceRunnable
		implements Runnable
	{
		private final Logger logger = LoggerFactory.getLogger(GoToSourceRunnable.class);

		private int port;
		private Socket socket;
		private ObjectOutputStream oos;

		public int getPort()
		{
			return port;
		}

		public void setPort(int port)
		{
			this.port = port;
			closeConnection();
		}

		private void openConnection()
		{
			try
			{
				socket = new Socket("127.0.0.1", port); // NOPMD
				oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			}
			catch(IOException e)
			{
				if(logger.isInfoEnabled()) logger.info("Exception while creating connection with IDE!", e);
				closeConnection();
			}
		}

		private void closeConnection()
		{
			if(oos != null)
			{
				try
				{
					oos.close();
				}
				catch (IOException e)
				{
					// ignore
				}
				oos = null;
			}
			if(socket != null)
			{
				try
				{
					socket.close();
				}
				catch(IOException e)
				{
					// ignore
				}
				socket = null;
			}
		}

		@Override
		public void run()
		{
			for(;;)
			{
				StackTraceElement ste;
				try
				{
					ste=queue.take();
				}
				catch(InterruptedException e)
				{
					break;
				}
				if(logger.isInfoEnabled()) logger.info("Go to source of {}.", ste);
				if(oos == null)
				{
					openConnection();
				}
				boolean error = false;
				if(oos != null)
				{
					try
					{
						oos.writeObject(ste);
						oos.flush();
					}
					catch(IOException e)
					{
						if(logger.isDebugEnabled()) logger.debug("Exception on first try, probably lingering connection.", e);
						closeConnection();
						error = true;
					}
				}
				if(error)
				{
					// try to send it again to work around lingering connections.
					openConnection();
					if(oos != null)
					{
						try
						{
							oos.writeObject(ste);
							oos.flush();
						}
						catch(IOException e)
						{
							if(logger.isWarnEnabled()) logger.warn("Exception on second try!", e);
							closeConnection();
						}
					}
				}
			}
		}
	}

}
