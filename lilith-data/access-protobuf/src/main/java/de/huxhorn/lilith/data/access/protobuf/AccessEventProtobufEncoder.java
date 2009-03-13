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
package de.huxhorn.lilith.data.access.protobuf;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.access.protobuf.generated.AccessProto;
import de.huxhorn.sulky.codec.Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import java.util.Date;
import java.util.Map;

public class AccessEventProtobufEncoder
	implements Encoder<AccessEvent>
{
	private boolean compressing;

	public AccessEventProtobufEncoder(boolean compressing)
	{
		this.compressing = compressing;
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	public void setCompressing(boolean compressing)
	{
		this.compressing = compressing;
	}

	public byte[] encode(AccessEvent event)
	{
		AccessProto.AccessEvent converted = convert(event);
		if(converted == null)
		{
			return null;
		}
		if(!compressing)
		{
			return converted.toByteArray();
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gos;
		try
		{
			gos = new GZIPOutputStream(out);
			converted.writeTo(gos);
			gos.flush();
			gos.close();
			return out.toByteArray();
		}
		catch(IOException e)
		{
			// ignore
		}
		return null;
	}

	public static AccessProto.AccessEvent convert(AccessEvent event)
	{
		if(event == null)
		{
			return null;
		}
		AccessProto.AccessEvent.Builder eventBuilder = AccessProto.AccessEvent.newBuilder();

		// handling method
		{
			String method = event.getMethod();
			if(method!=null)
			{
				eventBuilder.setMethod(method);
			}
		}

		// handling protocol
		{
			String protocol = event.getProtocol();
			if(protocol!=null)
			{
				eventBuilder.setProtocol(protocol);
			}
		}

		// handling remote address
		{
			String address = event.getRemoteAddress();
			if(address!=null)
			{
				eventBuilder.setRemoteAddress(address);
			}
		}

		// handling remote host
		{
			String host = event.getRemoteHost();
			if(host!=null)
			{
				eventBuilder.setRemoteHost(host);
			}
		}

		// handling remote user
		{
			String user = event.getRemoteUser();
			if(user!=null)
			{
				eventBuilder.setRemoteUser(user);
			}
		}

		// handling request uri
		{
			String uri = event.getRequestURI();
			if(uri!=null)
			{
				eventBuilder.setRequestUri(uri);
			}
		}

		// handling request url
		{
			String url = event.getRequestURL();
			if(url!=null)
			{
				eventBuilder.setRequestUrl(url);
			}
		}

		// handling request url
		{
			String url = event.getRequestURL();
			if(url!=null)
			{
				eventBuilder.setRequestUrl(url);
			}
		}

		// handling server name
		{
			String name = event.getServerName();
			if(name!=null)
			{
				eventBuilder.setServerName(name);
			}
		}

		// handling timestamp
		{
			Date ts = event.getTimeStamp();
			if(ts!=null)
			{
				eventBuilder.setTimeStamp(ts.getTime());
			}
		}

		// handling local port
		{
			int port = event.getLocalPort();
			eventBuilder.setLocalPort(port);
		}

		// handling status code
		{
			int status = event.getStatusCode();
			eventBuilder.setStatusCode(status);
		}

		// handling request headers
		{
			AccessProto.StringMap data = convertStringMap(event.getRequestHeaders());
			if(data != null)
			{
				eventBuilder.setRequestHeaders(data);
			}
		}

		// handling response headers
		{
			AccessProto.StringMap data = convertStringMap(event.getResponseHeaders());
			if(data != null)
			{
				eventBuilder.setResponseHeaders(data);
			}
		}

		// handling request parameters
		{
			AccessProto.StringArrayMap data = convertStringArrayMap(event.getRequestParameters());
			if(data != null)
			{
				eventBuilder.setRequestParameters(data);
			}
		}

		// handling application id
		{
			String id = event.getApplicationIdentifier();
			if(id!=null)
			{
				eventBuilder.setApplicationIdentifier(id);
			}
		}
		return eventBuilder.build();
	}

	public static AccessProto.StringMap convertStringMap(Map<String, String> data)
	{
		if(data == null)
		{
			return null;
		}
		AccessProto.StringMap.Builder builder = AccessProto.StringMap.newBuilder();
		for(Map.Entry<String, String> current : data.entrySet())
		{
			AccessProto.StringMapEntry.Builder entryBuilder = AccessProto.StringMapEntry.newBuilder()
				.setKey(current.getKey());
			String value = current.getValue();
			if(value != null)
			{
				entryBuilder.setValue(value);
			}
			builder.addEntry(entryBuilder.build());
		}
		return builder.build();
	}

	public static AccessProto.StringArrayMap convertStringArrayMap(Map<String, String[]> data)
	{
		if(data == null)
		{
			return null;
		}
		AccessProto.StringArrayMap.Builder builder = AccessProto.StringArrayMap.newBuilder();
		for(Map.Entry<String,String[]> current : data.entrySet())
		{
			AccessProto.StringArrayMapEntry.Builder entryBuilder = AccessProto.StringArrayMapEntry.newBuilder()
				.setKey(current.getKey());
			String[] value = current.getValue();
			if(value != null && value.length>0)
			{
				for(String cur:value)
				{
					entryBuilder.addValue(cur);
				}
			}
			builder.addEntry(entryBuilder.build());
		}
		return builder.build();
	}
}
