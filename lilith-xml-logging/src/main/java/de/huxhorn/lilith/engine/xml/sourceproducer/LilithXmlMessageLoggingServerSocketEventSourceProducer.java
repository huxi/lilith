/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
package de.huxhorn.lilith.engine.xml.sourceproducer;

import de.huxhorn.lilith.engine.EventProducer;
import de.huxhorn.lilith.engine.xml.eventproducer.LilithXmlMessageLoggingEventProducer;
import de.huxhorn.lilith.engine.impl.sourceproducer.AbstractServerSocketEventSourceProducer;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.buffers.AppendOperation;
import de.huxhorn.lilith.data.logging.LoggingEvent;


import java.io.IOException;
import java.io.InputStream;

public class LilithXmlMessageLoggingServerSocketEventSourceProducer
		extends AbstractServerSocketEventSourceProducer<LoggingEvent>
{
	private boolean compressing;

	public LilithXmlMessageLoggingServerSocketEventSourceProducer(int port, boolean compressing)
			throws IOException
	{
		super(port);
		this.compressing=compressing;
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	protected EventProducer createProducer(SourceIdentifier id, AppendOperation<EventWrapper<LoggingEvent>> eventQueue, InputStream inputStream) throws IOException
	{
		return new LilithXmlMessageLoggingEventProducer(id, eventQueue, inputStream, compressing);
	}

	@Override
	public String toString()
	{
		return "LilithXmlMessageLoggingServerSocketEventSourceProducer[port="+getPort()+", compressing="+compressing+"]";
	}

}
