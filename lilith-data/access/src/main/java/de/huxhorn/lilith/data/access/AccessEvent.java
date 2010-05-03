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
package de.huxhorn.lilith.data.access;

import de.huxhorn.lilith.data.eventsource.LoggerContext;

import java.io.Serializable;
import java.util.Map;

public class AccessEvent
	implements Serializable
{
	private static final long serialVersionUID = 4290137484866338570L;

	private Long timeStamp;
	private LoggerContext loggerContext;
	private String requestURI;
	private String requestURL;
	private String remoteHost;
	private String remoteUser;
	private String protocol;
	private String method;
	private String serverName;
	private String remoteAddress;
	private Map<String, String> requestHeaders;
	private Map<String, String> responseHeaders;
	private Map<String, String[]> requestParameters;
	private int localPort;
	private int statusCode;

	public Long getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStamp(Long timeStamp)
	{
		this.timeStamp = timeStamp;
	}

	public LoggerContext getLoggerContext()
	{
		return loggerContext;
	}

	public void setLoggerContext(LoggerContext loggerContext)
	{
		this.loggerContext = loggerContext;
	}

	public String getRequestURI()
	{
		return requestURI;
	}

	public void setRequestURI(String requestURI)
	{
		this.requestURI = requestURI;
	}

	public String getRequestURL()
	{
		return requestURL;
	}

	public void setRequestURL(String requestURL)
	{
		this.requestURL = requestURL;
	}

	public String getRemoteHost()
	{
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost)
	{
		this.remoteHost = remoteHost;
	}

	public String getRemoteUser()
	{
		return remoteUser;
	}

	public void setRemoteUser(String remoteUser)
	{
		this.remoteUser = remoteUser;
	}

	public String getProtocol()
	{
		return protocol;
	}

	public void setProtocol(String protocol)
	{
		this.protocol = protocol;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public String getServerName()
	{
		return serverName;
	}

	public void setServerName(String serverName)
	{
		this.serverName = serverName;
	}

	public String getRemoteAddress()
	{
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress)
	{
		this.remoteAddress = remoteAddress;
	}

	public Map<String, String> getRequestHeaders()
	{
		return requestHeaders;
	}

	public void setRequestHeaders(Map<String, String> requestHeaders)
	{
		this.requestHeaders = requestHeaders;
	}

	public Map<String, String> getResponseHeaders()
	{
		return responseHeaders;
	}

	public void setResponseHeaders(Map<String, String> responseHeaders)
	{
		this.responseHeaders = responseHeaders;
	}

	public Map<String, String[]> getRequestParameters()
	{
		return requestParameters;
	}

	public void setRequestParameters(Map<String, String[]> requestParameters)
	{
		this.requestParameters = requestParameters;
	}

	public int getLocalPort()
	{
		return localPort;
	}

	public void setLocalPort(int localPort)
	{
		this.localPort = localPort;
	}

	public int getStatusCode()
	{
		return statusCode;
	}

	public void setStatusCode(int statusCode)
	{
		this.statusCode = statusCode;
	}

	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		AccessEvent event = (AccessEvent) o;

		if(localPort != event.localPort) return false;
		if(statusCode != event.statusCode) return false;
		if(loggerContext != null ? !loggerContext
			.equals(event.loggerContext) : event.loggerContext != null)
		{
			return false;
		}
		if(method != null ? !method.equals(event.method) : event.method != null) return false;
		if(protocol != null ? !protocol.equals(event.protocol) : event.protocol != null) return false;
		if(remoteAddress != null ? !remoteAddress.equals(event.remoteAddress) : event.remoteAddress != null)
		{
			return false;
		}
		if(remoteHost != null ? !remoteHost.equals(event.remoteHost) : event.remoteHost != null) return false;
		if(remoteUser != null ? !remoteUser.equals(event.remoteUser) : event.remoteUser != null) return false;
		if(requestHeaders != null ? !requestHeaders.equals(event.requestHeaders) : event.requestHeaders != null)
		{
			return false;
		}
//		if (requestParameters != null ? !requestParameters.equals(event.requestParameters) : event.requestParameters != null)
//			return false;
		if(requestURI != null ? !requestURI.equals(event.requestURI) : event.requestURI != null) return false;
		if(requestURL != null ? !requestURL.equals(event.requestURL) : event.requestURL != null) return false;
		if(responseHeaders != null ? !responseHeaders.equals(event.responseHeaders) : event.responseHeaders != null)
		{
			return false;
		}
		if(serverName != null ? !serverName.equals(event.serverName) : event.serverName != null) return false;

		return !(timeStamp != null ? !timeStamp.equals(event.timeStamp) : event.timeStamp != null);
	}

	public int hashCode()
	{
		int result;
		result = (timeStamp != null ? timeStamp.hashCode() : 0);
		result = 31 * result + (loggerContext != null ? loggerContext.hashCode() : 0);
		result = 31 * result + (requestURI != null ? requestURI.hashCode() : 0);
		result = 31 * result + (requestURL != null ? requestURL.hashCode() : 0);
		result = 31 * result + (remoteHost != null ? remoteHost.hashCode() : 0);
		result = 31 * result + (remoteUser != null ? remoteUser.hashCode() : 0);
		result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
		result = 31 * result + (method != null ? method.hashCode() : 0);
		result = 31 * result + (serverName != null ? serverName.hashCode() : 0);
		result = 31 * result + (remoteAddress != null ? remoteAddress.hashCode() : 0);
		result = 31 * result + localPort;
		result = 31 * result + statusCode;
		return result;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("AccessEvent[");
		result.append("loggerContext=").append(loggerContext).append(", ");
		result.append("timeStamp=").append(timeStamp);

		result.append("]");
		return result.toString();
	}


}
