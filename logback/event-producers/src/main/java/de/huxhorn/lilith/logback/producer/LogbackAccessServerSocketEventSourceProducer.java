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
package de.huxhorn.lilith.logback.producer;

import de.huxhorn.lilith.engine.EventProducer;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.buffers.AppendOperation;

import java.io.InputStream;
import java.io.IOException;

import de.huxhorn.lilith.data.access.AccessEvent;

public class LogbackAccessServerSocketEventSourceProducer
	extends AbstractLogbackServerSocketEventSourceProducer<AccessEvent>
{
	public LogbackAccessServerSocketEventSourceProducer(int port) throws IOException
	{
		super(port);
	}

	protected EventProducer createProducer(SourceIdentifier id, AppendOperation<EventWrapper<AccessEvent>> eventQueue, InputStream inputStream) throws IOException
	{
		return new LogbackAccessStreamEventProducer(id, eventQueue, inputStream);
	}

	@Override
	public String toString()
	{
		return "LogbackAccessServerSocketEventSourceProducer[port="+getPort()+"]";
	}
}
