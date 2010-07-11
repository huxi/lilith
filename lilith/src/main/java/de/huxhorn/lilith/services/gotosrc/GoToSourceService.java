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
package de.huxhorn.lilith.services.gotosrc;

import de.huxhorn.sulky.io.IOUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GoToSourceService
{
	private final Logger logger = LoggerFactory.getLogger(GoToSourceService.class);

	private Socket socket;
	private ObjectOutputStream oos;

	public void start()
	{
		try
		{
			socket = new Socket("localhost", 11111);
			oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		}
		catch(IOException e)
		{
			if(logger.isInfoEnabled()) logger.info("Exception while creating connection with IDE!", e);
			stop();
		}
	}

	public void goToSource(StackTraceElement ste)
	{
		if(logger.isInfoEnabled()) logger.info("Go to source of {}.", ste);
		if(ste == null)
		{
			return;
		}
		if(oos == null)
		{
			start();
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
				stop();
				error = true;
			}
		}
		if(error)
		{
			// try to send it again to work around lingering connections.
			start();
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
					stop();
				}
			}
		}

	}

	public void stop()
	{
		if(oos != null)
		{
			IOUtilities.closeQuietly(oos);
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
}
