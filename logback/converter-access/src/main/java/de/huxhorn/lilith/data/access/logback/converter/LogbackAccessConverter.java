/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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
 * Copyright 2007-2018 Joern Huxhorn
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

package de.huxhorn.lilith.data.access.logback.converter;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.converter.Converter;

public class LogbackAccessConverter
	implements Converter<AccessEvent>
{
	@Override
	public AccessEvent convert(Object o)
	{
		if(o == null)
		{
			return null;
		}
		if(!(o instanceof ch.qos.logback.access.spi.IAccessEvent))
		{
			throw new IllegalArgumentException(o.toString()+" is not a "+getSourceClass()+"!");
		}
		ch.qos.logback.access.spi.IAccessEvent event = (ch.qos.logback.access.spi.IAccessEvent) o;
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
		result.setElapsedTime(event.getElapsedTime());
		return result;
	}

	@Override
	public Class getSourceClass()
	{
		return ch.qos.logback.access.spi.AccessEvent.class;
	}
}
