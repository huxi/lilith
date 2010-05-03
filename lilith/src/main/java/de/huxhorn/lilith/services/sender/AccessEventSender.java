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
package de.huxhorn.lilith.services.sender;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.sulky.codec.SerializableEncoder;

import javax.jmdns.JmDNS;

public class AccessEventSender
	extends AbstractEventSender<AccessEvent>
{
	public static final String SERVICE_TYPE = "_access._tcp.local.";

	private SerializableEncoder<AccessEvent> encoder;

	public AccessEventSender(JmDNS jmDns, String serviceName, String hostName, int port, boolean compressing)
	{
		super(jmDns, serviceName, hostName, port, compressing);
		encoder = new SerializableEncoder<AccessEvent>(compressing);
	}

	public void send(AccessEvent event)
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
