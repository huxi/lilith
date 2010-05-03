/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
 * Copyright 2007-2010 Joern Huxhorn
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


package de.huxhorn.lilith.logback.appender.jgroups;


import de.huxhorn.sulky.codec.Encoder;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.Message;
import org.jgroups.ChannelException;
import org.jgroups.ChannelNotConnectedException;
import org.jgroups.ChannelClosedException;

public abstract class JGroupsAppenderBase<E>
	extends UnsynchronizedAppenderBase<E>
{
	private Encoder<E> encoder;
	private String applicationIdentifier;
	private boolean debug;
	private JChannel channel;
	private String clusterName;

	public JGroupsAppenderBase()
	{
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public String getApplicationIdentifier()
	{
		return applicationIdentifier;
	}

	public void setApplicationIdentifier(String applicationIdentifier)
	{
		this.applicationIdentifier = applicationIdentifier;
		applicationIdentifierChanged();
	}

	public String getClusterName()
	{
		return clusterName;
	}

	protected void setClusterName(String clusterName)
	{
		this.clusterName = clusterName;
	}

	protected abstract void applicationIdentifierChanged();


	/**
	 * Start this appender.
	 */
	public void start()
	{
		if(!started)
		{
			int errorCount = 0;

			if(clusterName == null)
			{
				errorCount++;
				addError("No clusterName was configured for appender" + name + ".");
			}

			if(errorCount == 0)
			{
				initialize();
				this.started = true;
			}
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


	private void initialize()
	{
		try
		{
			channel = new JChannel();
			channel.setReceiver(new MyReceiver());
			channel.connect(clusterName);
		}
		catch(ChannelException e)
		{
			addError("Couldn't create channel!", e);
		}
	}

	/**
	 * Stop this appender.
	 * <p/>
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
		if(channel != null)
		{
			channel.close();
			channel=null;
		}
	}

	protected void sendBytes(byte[] bytes)
	{
		if(channel != null)
		{
			try
			{
				channel.send(new Message(null, null, bytes));
			}
			catch(ChannelNotConnectedException e)
			{
				e.printStackTrace();
			}
			catch(ChannelClosedException e)
			{
				e.printStackTrace();
			}
		}
	}

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

	protected Encoder<E> getEncoder()
	{
		return encoder;
	}

	protected void setEncoder(Encoder<E> encoder)
	{
		this.encoder = encoder;
	}

	private static class MyReceiver
		extends ReceiverAdapter
	{
		public void viewAccepted(View new_view)
		{
			System.out.println("** view: " + new_view);
		}

		public void receive(Message msg)
		{
		}
	}

}
