/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2014 Joern Huxhorn
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
 * Copyright 2007-2014 Joern Huxhorn
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

package de.huxhorn.lilith.data.access;

import de.huxhorn.lilith.data.eventsource.LoggerContext;
import java.io.Serializable;
import java.util.Map;

public class AccessEvent
	implements Serializable
{
	private static final long serialVersionUID = -942687545417047646L;

	private Long timeStamp;
	private Long elapsedTime;
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

	public Long getElapsedTime()
	{
		return elapsedTime;
	}

	public void setElapsedTime(Long elapsedTime)
	{
		this.elapsedTime = elapsedTime;
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

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AccessEvent that = (AccessEvent) o;

		if (localPort != that.localPort) return false;
		if (statusCode != that.statusCode) return false;
		if (elapsedTime != null ? !elapsedTime.equals(that.elapsedTime) : that.elapsedTime != null) return false;
		if (loggerContext != null ? !loggerContext.equals(that.loggerContext) : that.loggerContext != null)
			return false;
		if (method != null ? !method.equals(that.method) : that.method != null) return false;
		if (protocol != null ? !protocol.equals(that.protocol) : that.protocol != null) return false;
		if (remoteAddress != null ? !remoteAddress.equals(that.remoteAddress) : that.remoteAddress != null)
			return false;
		if (remoteHost != null ? !remoteHost.equals(that.remoteHost) : that.remoteHost != null) return false;
		if (remoteUser != null ? !remoteUser.equals(that.remoteUser) : that.remoteUser != null) return false;
		if (requestHeaders != null ? !requestHeaders.equals(that.requestHeaders) : that.requestHeaders != null)
			return false;
		// unusable, map.equals does not work with array values.
		//if (requestParameters != null ? !requestParameters.equals(that.requestParameters) : that.requestParameters != null) return false;
		if (requestURI != null ? !requestURI.equals(that.requestURI) : that.requestURI != null) return false;
		if (requestURL != null ? !requestURL.equals(that.requestURL) : that.requestURL != null) return false;
		if (responseHeaders != null ? !responseHeaders.equals(that.responseHeaders) : that.responseHeaders != null)
			return false;
		if (serverName != null ? !serverName.equals(that.serverName) : that.serverName != null) return false;

		return !(timeStamp != null ? !timeStamp.equals(that.timeStamp) : that.timeStamp != null);
	}

	@Override
	public int hashCode()
	{
		int result = timeStamp != null ? timeStamp.hashCode() : 0;
		result = 31 * result + (elapsedTime != null ? elapsedTime.hashCode() : 0);
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
		return "AccessEvent{" +
				"timeStamp=" + timeStamp +
				", elapsedTime=" + elapsedTime +
				", loggerContext=" + loggerContext +
				", requestURI='" + requestURI + '\'' +
				", requestURL='" + requestURL + '\'' +
				", remoteHost='" + remoteHost + '\'' +
				", remoteUser='" + remoteUser + '\'' +
				", protocol='" + protocol + '\'' +
				", method='" + method + '\'' +
				", serverName='" + serverName + '\'' +
				", remoteAddress='" + remoteAddress + '\'' +
				", requestHeaders=" + requestHeaders +
				", responseHeaders=" + responseHeaders +
				", requestParameters=" + requestParameters +
				", localPort=" + localPort +
				", statusCode=" + statusCode +
				'}';
	}
}
