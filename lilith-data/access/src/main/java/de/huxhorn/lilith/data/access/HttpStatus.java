/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
 * Copyright 2007-2011 Joern Huxhorn
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

import java.util.HashMap;
import java.util.Map;

public enum HttpStatus
{
	// Informational 1xx
	CONTINUE(100, Type.INFORMATIONAL, "Continue", Specification.RFC2616),
	SWITCHING_PROTOCOLS(101, Type.INFORMATIONAL, "Switching Protocols", Specification.RFC2616),
	PROCESSING(102, Type.INFORMATIONAL, "Processing (WebDAV)", Specification.RFC2518),


	// Successful 2xx
	OK(200, Type.SUCCESSFUL, "OK", Specification.RFC2616),
	CREATED(201, Type.SUCCESSFUL, "Created", Specification.RFC2616),
	ACCEPTED(202, Type.SUCCESSFUL, "Accepted", Specification.RFC2616),
	NON_AUTHORITATIVE_INFORMATION(203, Type.SUCCESSFUL, "Non-Authoritative Information", Specification.RFC2616),
	NO_CONTENT(204, Type.SUCCESSFUL, "No Content", Specification.RFC2616),
	RESET_CONTENT(205, Type.SUCCESSFUL, "Reset Content", Specification.RFC2616),
	PARTIAL_CONTENT(206, Type.SUCCESSFUL, "Partial Content", Specification.RFC2616),
	MULTI_STATUS(207, Type.SUCCESSFUL, "Multi-Status (WebDAV)", Specification.RFC4918),


	// Redirection 3xx
	MULTIPLE_CHOICES(300, Type.REDIRECTION, "Multiple Choices", Specification.RFC2616),
	MOVED_PERMANENTLY(301, Type.REDIRECTION, " Moved Permanently", Specification.RFC2616),
	FOUND(302, Type.REDIRECTION, " Found", Specification.RFC2616),
	SEE_OTHER(303, Type.REDIRECTION, " See Other", Specification.RFC2616),
	NOT_MODIFIED(304, Type.REDIRECTION, " Not Modified", Specification.RFC2616),
	USE_PROXY(305, Type.REDIRECTION, " Use Proxy", Specification.RFC2616),
	SWITCH_PROXY(306, Type.REDIRECTION, "Switch Proxy", Specification.RFC2616),
	TEMPORARY_REDIRECT(307, Type.REDIRECTION, " Temporary Redirect", Specification.RFC2616),


	// Client Error 4xx
	BAD_REQUEST(400, Type.CLIENT_ERROR, "Bad Request", Specification.RFC2616),
	UNAUTHORIZED(401, Type.CLIENT_ERROR, "Unauthorized", Specification.RFC2616),
	PAYMENT_REQUIRED(402, Type.CLIENT_ERROR, "Payment Required", Specification.RFC2616),
	FORBIDDEN(403, Type.CLIENT_ERROR, "Forbidden", Specification.RFC2616),
	NOT_FOUND(404, Type.CLIENT_ERROR, "Not Found", Specification.RFC2616),
	METHOD_NOT_ALLOWED(405, Type.CLIENT_ERROR, "Method Not Allowed", Specification.RFC2616),
	NOT_ACCEPTABLE(406, Type.CLIENT_ERROR, "Not Acceptable", Specification.RFC2616),
	PROXY_AUTHENTICATION_REQUIRED(407, Type.CLIENT_ERROR, "Proxy Authentication Required", Specification.RFC2616),
	REQUEST_TIMEOUT(408, Type.CLIENT_ERROR, "Request Timeout", Specification.RFC2616),
	CONFLICT(409, Type.CLIENT_ERROR, "Conflict", Specification.RFC2616),
	GONE(410, Type.CLIENT_ERROR, "Gone", Specification.RFC2616),
	LENGTH_REQUIRED(411, Type.CLIENT_ERROR, "Length Required", Specification.RFC2616),
	PRECONDITION_FAILED(412, Type.CLIENT_ERROR, "Precondition Failed", Specification.RFC2616),
	REQUEST_ENTITY_TOO_LARGE(413, Type.CLIENT_ERROR, "Request Entity Too Large", Specification.RFC2616),
	REQUEST_URI_TOO_LONG(414, Type.CLIENT_ERROR, "Request-URI Too Long", Specification.RFC2616),
	UNSUPPORTED_MEDIA_TYPE(415, Type.CLIENT_ERROR, "Unsupported Media Type", Specification.RFC2616),
	REQUESTED_RANGE_NOT_SATISFIABLE(416, Type.CLIENT_ERROR, "Requested Range Not Satisfiable", Specification.RFC2616),
	EXPECTATION_FAILED(417, Type.CLIENT_ERROR, "Expectation Failed", Specification.RFC2616),
	IM_A_TEAPOT(418, Type.CLIENT_ERROR, "I'm a teapot", Specification.RFC2324),
	UNPROCESSABLE_ENTITY(422, Type.CLIENT_ERROR, "Unprocessable Entity (WebDAV)", Specification.RFC4918),
	LOCKED(423, Type.CLIENT_ERROR, "Locked (WebDAV)", Specification.RFC4918),
	FAILED_DEPENDENCY(424, Type.CLIENT_ERROR, "Failed Dependency (WebDAV)", Specification.RFC4918),
	UNORDERED_COLLECTION(425, Type.CLIENT_ERROR, "Unordered Collection (WebDAV, draft)", Specification.RFC3648),
	UPGRADE_REQUIRED(426, Type.CLIENT_ERROR, "Upgrade Required", Specification.RFC2817),
	NO_RESPONSE(444, Type.CLIENT_ERROR, "No Response (Nginx)", Specification.NGINX),
	RETRY_WITH(449, Type.CLIENT_ERROR, "Retry With", Specification.MICROSOFT),
	BLOCKED_BY_WINDOWS_PARENTAL_CONTROLS(450, Type.CLIENT_ERROR, "Blocked by Windows Parental Controls", Specification.MICROSOFT),
	CLIENT_CLOSED_REQUEST(499, Type.CLIENT_ERROR, "Client Closed Request (Nginx)", Specification.NGINX),


	// Server Error 5xx
	INTERNAL_SERVER_ERROR(500, Type.SERVER_ERROR, "Internal Server Error", Specification.RFC2616),
	NOT_IMPLEMENTED(501, Type.SERVER_ERROR, "Not Implemented", Specification.RFC2616),
	BAD_GATEWAY(502, Type.SERVER_ERROR, "Bad Gateway", Specification.RFC2616),
	SERVICE_UNAVAILABLE(503, Type.SERVER_ERROR, "Service Unavailable", Specification.RFC2616),
	GATEWAY_TIMEOUT(504, Type.SERVER_ERROR, "Gateway Timeout", Specification.RFC2616),
	HTTP_VERSION_NOT_SUPPORTED(505, Type.SERVER_ERROR, "HTTP Version Not Supported", Specification.RFC2616),
	VARIANT_ALSO_NEGOTIATES(506, Type.SERVER_ERROR, "Variant Also Negotiates", Specification.RFC2295),
	INSUFFICIENT_STORAGE(507, Type.SERVER_ERROR, "Insufficient Storage (WebDAV)", Specification.RFC4918),
	BANDWIDTH_LIMIT_EXCEEDED(509, Type.SERVER_ERROR, "Bandwidth Limit Exceeded", Specification.APACHE),
	NOT_EXTENDED(510, Type.SERVER_ERROR, "Not Extended", Specification.RFC2774);


	private static final Map<Integer, HttpStatus> codeMap = new HashMap<Integer, HttpStatus>();

	static
	{
		for(HttpStatus code : HttpStatus.values())
		{
			HttpStatus previous = codeMap.put(code.getCode(), code);
			if(previous != null)
			{
				throw new RuntimeException("Duplicate entry for HttpStatus "+code.getCode()+"!");
			}
		}
	}

	public static HttpStatus getStatus(int code)
	{
		return codeMap.get(code);
	}

	public static HttpStatus.Type getType(int code)
	{
		if(code >= 100 && code < 200)
		{
			return Type.INFORMATIONAL;
		}
		if(code >= 200 && code < 300)
		{
			return Type.SUCCESSFUL;
		}
		if(code >= 300 && code < 400)
		{
			return Type.REDIRECTION;
		}
		if(code >= 400 && code < 500)
		{
			return Type.CLIENT_ERROR;
		}
		if(code >= 500 && code < 600)
		{
			return Type.SERVER_ERROR;
		}
		return null;
	}

	private int code;
	private Type type;
	private Specification specification;
	private String description;

	HttpStatus(int code, Type type, String description, Specification specification)
	{
		this.code = code;
		this.type = type;
		this.description = description;
		this.specification = specification;
	}

	public int getCode()
	{
		return code;
	}

	public Type getType()
	{
		return type;
	}

	public Specification getSpecification()
	{
		return specification;
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

	public enum Specification
	{
		RFC2295("RFC 2295", "Transparent Content Negotiation in HTTP"),
		RFC2324("RFC 2324", "Hyper Text Coffee Pot Control Protocol"),
		RFC2518("RFC 2518", "HTTP Extensions for Distributed Authoring -- WEBDAV"),
		RFC2616("RFC 2616", "Hypertext Transfer Protocol -- HTTP/1.1"),
		RFC2774("RFC 2774", "An HTTP Extension Framework"),
		RFC2817("RFC 2817", "Upgrading to TLS Within HTTP/1.1"),
		RFC3648("RFC 3648", "Web Distributed Authoring and Versioning (WebDAV) Ordered Collections Protocol"),
		RFC4918("RFC 4918", "HTTP Extensions for Web Distributed Authoring and Versioning (WebDAV)"),
		NGINX("Nginx", "Nginx HTTP server extensions"),
		APACHE("Apache", "Apache extensions"),
		MICROSOFT("Microsoft", "Microsoft extensions");

		private String identifier;
		private String description;

		Specification(String identifier, String description)
		{
			this.identifier = identifier;
			this.description = description;
		}

		@Override
		public String toString()
		{
			return identifier+" - "+description;
		}
	}
}
