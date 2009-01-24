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
package de.huxhorn.lilith.data.access;

import java.util.HashMap;
import java.util.Map;

public enum HttpStatus
{
	//Informational 1xx
	CONTINUE(100, Type.INFORMATIONAL, "Continue"),
	SWITCHING_PROTOCOLS(101, Type.INFORMATIONAL, "Switching Protocols"),

	//Successful 2xx
	OK(200, Type.SUCCESSFUL, "OK"),
	CREATED(201, Type.SUCCESSFUL, "Created"),
	ACCEPTED(202, Type.SUCCESSFUL, "Accepted"),
	NON_AUTHORITATIVE_INFORMATION(203, Type.SUCCESSFUL, "Non-Authoritative Information"),
	NO_CONTENT(204, Type.SUCCESSFUL, "No Content"),
	RESET_CONTENT(205, Type.SUCCESSFUL, "Reset Content"),
	PARTIAL_CONTENT(206, Type.SUCCESSFUL, "Partial Content"),

	//Redirection 3xx
	MULTIPLE_CHOICES(300, Type.REDIRECTION, "Multiple Choices"),
	MOVED_PERMANENTLY(301, Type.REDIRECTION, " Moved Permanently"),
	FOUND(302, Type.REDIRECTION, " Found"),
	SEE_OTHER(303, Type.REDIRECTION, " See Other"),
	NOT_MODIFIED(304, Type.REDIRECTION, " Not Modified"),
	USE_PROXY(305, Type.REDIRECTION, " Use Proxy"),
	UNUSED(306, Type.REDIRECTION, "(Unused)"),
	TEMPORARY_REDIRECT(307, Type.REDIRECTION, " Temporary Redirect"),

	//Client Error 4xx
	BAD_REQUEST(400, Type.CLIENT_ERROR, "Bad Request"),
	UNAUTHORIZED(401, Type.CLIENT_ERROR, "Unauthorized"),
	PAYMENT_REQUIRED(402, Type.CLIENT_ERROR, "Payment Required"),
	FORBIDDEN(403, Type.CLIENT_ERROR, "Forbidden"),
	NOT_FOUND(404, Type.CLIENT_ERROR, "Not Found"),
	METHOD_NOT_ALLOWED(405, Type.CLIENT_ERROR, "Method Not Allowed"),
	NOT_ACCEPTABLE(406, Type.CLIENT_ERROR, "Not Acceptable"),
	PROXY_AUTHENTICATION_REQUIRED(407, Type.CLIENT_ERROR, "Proxy Authentication Required"),
	REQUEST_TIMEOUT(408, Type.CLIENT_ERROR, "Request Timeout"),
	CONFLICT(409, Type.CLIENT_ERROR, "Conflict"),
	GONE(410, Type.CLIENT_ERROR, "Gone"),
	LENGTH_REQUIRED(411, Type.CLIENT_ERROR, "Length Required"),
	PRECONDITION_FAILED(412, Type.CLIENT_ERROR, "Precondition Failed"),
	REQUEST_ENTITY_TOO_LARGE(413, Type.CLIENT_ERROR, "Request Entity Too Large"),
	REQUEST_URI_TOO_LONG(414, Type.CLIENT_ERROR, "Request-URI Too Long"),
	UNSUPPORTED_MEDIA_TYPE(415, Type.CLIENT_ERROR, "Unsupported Media Type"),
	REQUESTED_RANGE_NOT_SATISFIABLE(416, Type.CLIENT_ERROR, "Requested Range Not Satisfiable"),
	EXPECTATION_FAILED(417, Type.CLIENT_ERROR, "Expectation Failed"),

	// Server Error 5xx
	INTERNAL_SERVER_ERROR(500, Type.SERVER_ERROR, "Internal Server Error"),
	NOT_IMPLEMENTED(501, Type.SERVER_ERROR, "Not Implemented"),
	BAD_GATEWAY(502, Type.SERVER_ERROR, "Bad Gateway"),
	SERVICE_UNAVAILABLE(503, Type.SERVER_ERROR, "Service Unavailable"),
	GATEWAY_TIMEOUT(504, Type.SERVER_ERROR, "Gateway Timeout"),
	HTTP_VERSION_NOT_SUPPORTED(505, Type.SERVER_ERROR, "HTTP Version Not Supported");

	private static final Map<Integer, HttpStatus> codeMap = new HashMap<Integer, HttpStatus>();

	static
	{
		for(HttpStatus code : HttpStatus.values())
		{
			codeMap.put(code.getCode(), code);
		}
	}

	public static HttpStatus getStatus(int code)
	{
		return codeMap.get(code);
	}

	private int code;
	private Type type;
	private String description;

	HttpStatus(int code, Type type, String description)
	{
		this.code = code;
		this.type = type;
		this.description = description;
	}

	public int getCode()
	{
		return code;
	}

	public Type getType()
	{
		return type;
	}

	public String getDescription()
	{
		return description;
	}

	public enum Type
	{
		INFORMATIONAL("Informational"),
		SUCCESSFUL("Successful"),
		REDIRECTION("Redirection"),
		CLIENT_ERROR("Client Error"),
		SERVER_ERROR("Server Error");

		private String description;

		Type(String description)
		{
			this.description = description;
		}

		@Override
		public String toString()
		{
			return description;
		}
	}

}
