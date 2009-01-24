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

public class HeartbeatRunnable
	implements Runnable
{
	/**
	 * The heartbeat rate. One heartbeat every 45 seconds.
	 */
	public static final int HEARTBEAT_RATE = 45000;

	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

	private final int heartbeatRate;
	private final SendBytesService sender;

	public HeartbeatRunnable(SendBytesService sender)
	{
		this(sender, HEARTBEAT_RATE);
	}

	public HeartbeatRunnable(SendBytesService sender, int heartbeatRate)
	{
		this.sender = sender;
		this.heartbeatRate = heartbeatRate;
	}

	public void run()
	{
		for(; ;)
		{
			try
			{
				Thread.sleep(heartbeatRate);
				sender.sendBytes(EMPTY_BYTE_ARRAY);
			}
			catch(InterruptedException e)
			{
				return;
			}
		}
	}
}
