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
package de.huxhorn.lilith.logback.appender;

import de.huxhorn.lilith.data.access.logback.TransformingEncoder;
import de.huxhorn.lilith.data.access.protobuf.AccessEventProtobufEncoder;

import ch.qos.logback.access.spi.AccessEvent;

public class AccessMultiplexSocketAppender
	extends MultiplexSocketAppenderBase<AccessEvent>
{
	/**
	 * The default port number of compressed new-style remote logging server (10010).
	 */
	public static final int COMRESSED_DEFAULT_PORT = 10010;

	/**
	 * The default port number of uncompressed new-style remote logging server (10011).
	 */
	public static final int UNCOMPRESSED_DEFAULT_PORT = 10011;

	private boolean compressing;
	private boolean usingDefaultPort;
	private TransformingEncoder transformingEncoder;

	public AccessMultiplexSocketAppender()
	{
		this(true);
	}

	public AccessMultiplexSocketAppender(boolean compressing)
	{
		super();
		usingDefaultPort = true;
		transformingEncoder = new TransformingEncoder();
		setEncoder(transformingEncoder);
		setCompressing(compressing);
	}

	protected void applicationIdentifierChanged()
	{
		transformingEncoder.setApplicationIdentifier(getApplicationIdentifier());
	}

	@Override
	public void setPort(int port)
	{
		super.setPort(port);
		usingDefaultPort = false;
	}

	/**
	 * GZIPs the event if set to true.
	 * <p/>
	 * Automatically chooses the correct default port if it was not previously set manually.
	 *
	 * @param compressing if events will be gzipped or not.
	 */
	public void setCompressing(boolean compressing)
	{
		this.compressing = compressing;
		if(usingDefaultPort)
		{
			if(compressing)
			{
				setPort(COMRESSED_DEFAULT_PORT);
			}
			else
			{
				setPort(UNCOMPRESSED_DEFAULT_PORT);
			}
			usingDefaultPort = true;
		}
		transformingEncoder.setLilithEncoder(new AccessEventProtobufEncoder(compressing));
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	protected void preProcess(AccessEvent e)
	{
		if(e != null)
		{
			e.prepareForDeferredProcessing();
		}
	}
}
