/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

package de.huxhorn.lilith.tools.formatters;

import ch.qos.logback.access.spi.AccessContext;
import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.access.spi.ServerAdapter;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.logback.tools.ContextHelper;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessFormatter
		implements Formatter<EventWrapper<AccessEvent>>
{
	private final Logger logger = LoggerFactory.getLogger(AccessFormatter.class);

	private static final String DEFAULT_PATTERN="common";

	private ch.qos.logback.access.PatternLayout layout;
	private String pattern;

	public String getPattern()
	{
		return pattern;
	}

	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	@Override
	public String format(EventWrapper<AccessEvent> wrapper)
	{
		initLayout();

		if(wrapper!=null)
		{
			AccessEvent event = wrapper.getEvent();
			if(event != null)
			{
				return layout.doLayout(convert(event));
			}
		}
		return null;
	}

	private IAccessEvent convert(AccessEvent event)
	{
		AccessEventFoo result = null;
		if(event != null)
		{
			result=new AccessEventFoo();
			Long ts = event.getTimeStamp();
			if(ts != null)
			{
				result.setTimeStamp(ts);
			}
			Long et = event.getElapsedTime();
			if(et != null)
			{
				result.setElapsedTime(et);
			}
			// loggerContext
			result.setRequestURI(event.getRequestURI());
			result.setRequestURL(event.getRequestURL());
			result.setRemoteHost(event.getRemoteHost());
			result.setRemoteUser(event.getRemoteUser());
			result.setProtocol(event.getProtocol());
			result.setMethod(event.getMethod());
			result.setServerName(event.getServerName());
			result.setRemoteAddr(event.getRemoteAddress());
			result.setRequestHeaderMap(event.getRequestHeaders());
			result.setResponseHeaderMap(event.getResponseHeaders());
			result.setRequestParameterMap(event.getRequestParameters());
			result.setLocalPort(event.getLocalPort());
			result.setStatusCode(event.getStatusCode());
		}

		return result;
	}

	private void initLayout()
	{
		if(layout == null)
		{
			layout=new ch.qos.logback.access.PatternLayout();
			Context context=new AccessContext();
			layout.setContext(context);
			if(pattern != null)
			{
				layout.setPattern(pattern);
			}
			else
			{
				layout.setPattern(DEFAULT_PATTERN);
			}
			layout.start();

			int statusLevel = ContextHelper.getHighestLevel(context);
			StatusManager statusManager = context.getStatusManager();
			if(statusLevel > Status.INFO)
			{
				List<Status> stati = statusManager.getCopyOfStatusList();
				String msg="Error while initializing layout! " + stati;
				if(logger.isErrorEnabled()) logger.error(msg);
				throw new IllegalStateException(msg);
			}
		}
	}

	private static class AccessEventFoo
		implements IAccessEvent
	{
		private String requestURI;
		private String requestURL;
		private String remoteHost;
		private String remoteUser;
		private String remoteAddr;
		private String protocol;
		private String method;
		private String serverName;

		private Map<String, String> requestHeaderMap;
		private Map<String, String[]> requestParameterMap;
		private Map<String, String> responseHeaderMap;

		private int statusCode = SENTINEL;
		private int localPort = SENTINEL;

		private long timeStamp = 0;
		private long elapsedTime;
		private static final String[] NA_STRING_ARRAY = new String[0];
		private static final String EMPTY = "";

		@Override
		public HttpServletRequest getRequest()
		{
			return null;
		}

		@Override
		public HttpServletResponse getResponse()
		{
			return null;
		}

		@Override
		public long getTimeStamp()
		{
			return timeStamp;
		}

		private void setElapsedTime(long elapsedTime)
		{
			this.elapsedTime = elapsedTime;
		}

		@Override
		public long getElapsedTime()
		{
			return elapsedTime;
		}

		@Override
		public long getElapsedSeconds()
		{
			return 0;
		}

		public void setTimeStamp(long timeStamp)
		{
			this.timeStamp = timeStamp;
		}

		public void setRequestURI(String requestURI)
		{
			this.requestURI = requestURI;
		}

		@Override
		public String getRequestURI()
		{
			if(requestURI == null)
			{
				requestURI = NA;
			}
			return requestURI;
		}

		public void setRequestURL(String requestURL)
		{
			this.requestURL = requestURL;
		}

		@Override
		public String getRequestURL()
		{
			if(requestURL == null)
			{
				requestURL = NA;
			}
			return requestURL;
		}

		public void setRemoteHost(String remoteHost)
		{
			this.remoteHost = remoteHost;
		}

		@Override
		public String getRemoteHost()
		{
			if(remoteHost == null)
			{
				remoteHost = NA;
			}
			return remoteHost;
		}

		public void setRemoteUser(String remoteUser)
		{
			this.remoteUser = remoteUser;
		}

		@Override
		public String getRemoteUser()
		{
			if(remoteUser == null)
			{
				remoteUser = NA;
			}
			return remoteUser;
		}

		public void setProtocol(String protocol)
		{
			this.protocol = protocol;
		}

		@Override
		public String getProtocol()
		{
			if(protocol == null)
			{
				protocol = NA;
			}
			return protocol;
		}

		public void setMethod(String method)
		{
			this.method = method;
		}

		@Override
		public String getMethod()
		{
			if(method == null)
			{
				method = NA;
			}
			return method;
		}

		public void setServerName(String serverName)
		{
			this.serverName = serverName;
		}

		@Override
		public String getServerName()
		{
			if(serverName == null)
			{
				serverName = NA;
			}
			return serverName;
		}

		@Override
		public String getSessionID() {
			return null;
		}

		@Override
		public void setThreadName(String threadName) {
			// no-op
		}

		@Override
		public String getThreadName() {
			return null;
		}

		@Override
		public String getQueryString() {
			return null;
		}

		public void setRemoteAddr(String remoteAddr)
		{
			this.remoteAddr = remoteAddr;
		}

		@Override
		public String getRemoteAddr()
		{
			if(remoteAddr == null)
			{
				remoteAddr = NA;
			}
			return remoteAddr;
		}

		public void setRequestHeaderMap(Map<String, String> requestHeaderMap)
		{
			this.requestHeaderMap = requestHeaderMap;
		}

		@Override
		public String getRequestHeader(String key)
		{
			String result = null;
			key = key.toLowerCase(Locale.US);
			if(requestHeaderMap != null)
			{
				result = requestHeaderMap.get(key);
			}

			if(result != null)
			{
				return result;
			}
			return NA;
		}

		@Override
		public Enumeration<String> getRequestHeaderNames()
		{
			if(requestHeaderMap == null)
			{
				requestHeaderMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
			}
			Vector<String> list = new Vector<>(requestHeaderMap.keySet()); // NOPMD
			return list.elements();
		}

		@Override
		public Map<String, String> getRequestHeaderMap()
		{
			if(requestHeaderMap == null)
			{
				requestHeaderMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
			}
			return requestHeaderMap;
		}

		public void setRequestParameterMap(Map<String, String[]> requestParameterMap)
		{
			this.requestParameterMap = requestParameterMap;
		}

		@Override
		public Map<String, String[]> getRequestParameterMap()
		{
			if(requestParameterMap == null)
			{
				requestParameterMap = new HashMap<>();
			}
			return requestParameterMap;
		}

		@Override
		public String getAttribute(String key)
		{
			return NA;
		}

		@Override
		public String[] getRequestParameter(String key)
		{
			if(requestParameterMap == null)
			{
				requestParameterMap = new HashMap<>();
			}
			String[] value = requestParameterMap.get(key);
			if(value == null)
			{
				return NA_STRING_ARRAY;
			}
			else
			{
				return value;
			}
		}

		@Override
		public String getCookie(String key)
		{
			return NA;
		}

		@Override
		public long getContentLength()
		{
			return SENTINEL;
		}

		public void setStatusCode(int statusCode)
		{
			this.statusCode = statusCode;
		}

		@Override
		public int getStatusCode()
		{
			return statusCode;
		}

		@Override
		public String getRequestContent()
		{
			return EMPTY;
		}

		@Override
		public String getResponseContent()
		{
			return EMPTY;
		}

		public void setLocalPort(int localPort)
		{
			this.localPort = localPort;
		}

		@Override
		public int getLocalPort()
		{
			return localPort;
		}

		@Override
		public ServerAdapter getServerAdapter()
		{
			return null;
		}

		public void setResponseHeaderMap(Map<String, String> responseHeaderMap)
		{
			this.responseHeaderMap = responseHeaderMap;
		}

		@Override
		public String getResponseHeader(String key)
		{
			if(responseHeaderMap == null)
			{
				responseHeaderMap = new HashMap<>();
			}
			return responseHeaderMap.get(key);
		}

		@Override
		public Map<String, String> getResponseHeaderMap()
		{
			if(responseHeaderMap == null)
			{
				responseHeaderMap = new HashMap<>();
			}
			return responseHeaderMap;
		}

		@Override
		public List<String> getResponseHeaderNameList()
		{
			if(responseHeaderMap == null)
			{
				responseHeaderMap = new HashMap<>();
			}
			return new ArrayList<>(responseHeaderMap.keySet());
		}

		@Override
		public void prepareForDeferredProcessing()
		{
			// no-op
		}
	}
}
