package de.huxhorn.lilith.data.access.logback;

import de.huxhorn.lilith.data.access.AccessEvent;

import java.util.Date;

public class LogbackAccessAdapter
{
	public AccessEvent convert(ch.qos.logback.access.spi.AccessEvent event)
	{
		if(event == null)
		{
			return null;
		}
		AccessEvent result=new AccessEvent();
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
		result.setTimeStamp(new Date(event.getTimeStamp()));
		return result;
	}
}