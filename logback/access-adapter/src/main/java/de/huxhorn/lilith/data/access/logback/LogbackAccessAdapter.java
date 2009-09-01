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
package de.huxhorn.lilith.data.access.logback;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.LoggerContext;

import java.util.Map;
import java.util.HashMap;

public class LogbackAccessAdapter
{
	public AccessEvent convert(ch.qos.logback.access.spi.AccessEvent event)
	{
		if(event == null)
		{
			return null;
		}
		AccessEvent result = new AccessEvent();

		// TODO: add support for LoggerContext once available
		/*
		LoggerContextVO lcv = event.getLoggerContextVO();
		if(lcv != null)
		{
			String name = lcv.getName();
			Map<String, String> props = lcv.getPropertyMap();
			if(props != null)
			{
				// lcv property map leak? yes, indeed. See http://jira.qos.ch/browse/LBCLASSIC-115
				props = new HashMap<String, String>(props);
			}
			LoggerContext loggerContext = new LoggerContext();
			loggerContext.setName(name);
			loggerContext.setProperties(props);
			loggerContext.setBirthTime(lcv.getBirthTime());
			result.setLoggerContext(loggerContext);
		}
		*/

		result.setLocalPort(event.getLocalPort());
		result.setMethod(event.getMethod());
		result.setProtocol(event.getProtocol());
		result.setRemoteAddress(event.getRemoteAddr());
		result.setRemoteHost(event.getRemoteHost());
		result.setRemoteUser(event.getRemoteUser());
		result.setRequestHeaders(event.getRequestHeaderMap());
		result.setRequestParameters(event.getRequestParameterMap());
		result.setRequestURI(event.getRequestURI());
		result.setRequestURL(event.getRequestURL());
		result.setResponseHeaders(event.getResponseHeaderMap());
		result.setServerName(event.getServerName());
		result.setStatusCode(event.getStatusCode());
		result.setTimeStamp(event.getTimeStamp());
		return result;
	}
}
