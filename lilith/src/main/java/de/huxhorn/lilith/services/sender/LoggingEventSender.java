/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
package de.huxhorn.lilith.services.sender;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.sulky.codec.SerializableEncoder;

import javax.jmdns.JmDNS;

public class LoggingEventSender
	extends AbstractEventSender<LoggingEvent>
{
	public static final String SERVICE_TYPE = "_logging._tcp.local.";
	private SerializableEncoder<LoggingEvent> encoder;

	public LoggingEventSender(JmDNS jmDns, String serviceName, String hostName, int port, boolean compressing)
	{
		super(jmDns, serviceName, hostName, port, compressing);
		encoder = new SerializableEncoder<LoggingEvent>(compressing);
	}

	public void send(LoggingEvent event)
	{
		if(encoder != null)
		{
			byte[] serialized = encoder.encode(event);
			if(serialized != null)
			{
				sendBytesService.sendBytes(serialized);
			}
		}
	}
}
