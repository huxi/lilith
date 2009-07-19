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

import com.google.protobuf.InvalidProtocolBufferException;
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.access.LoggerContext;
import de.huxhorn.lilith.data.access.protobuf.generated.AccessProto;
import de.huxhorn.sulky.codec.Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class AccessEventProtobufDecoder
        implements Decoder<AccessEvent> {
    private boolean compressing;

    public AccessEventProtobufDecoder(boolean compressing) {
        this.compressing = compressing;
    }

    public boolean isCompressing() {
        return compressing;
    }

    public void setCompressing(boolean compressing) {
        this.compressing = compressing;
    }

    public AccessEvent decode(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        AccessProto.AccessEvent parsedEvent = null;
        if (!compressing) {
            try {
                parsedEvent = AccessProto.AccessEvent.parseFrom(bytes);
            }
            catch (InvalidProtocolBufferException e) {
                // ignore
            }
        } else {
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            try {
                GZIPInputStream gis = new GZIPInputStream(in);
                parsedEvent = AccessProto.AccessEvent.parseFrom(gis);
                gis.close();
            }
            catch (IOException e) {
                // ignore
            }
        }
        return convert(parsedEvent);
    }

    public static AccessEvent convert(AccessProto.AccessEvent parsedEvent) {
        if (parsedEvent == null) {
            return null;
        }
        AccessEvent result = new AccessEvent();

        // handling method
        if (parsedEvent.hasMethod()) {
            result.setMethod(parsedEvent.getMethod());
        }

        // handling protocol
        if (parsedEvent.hasProtocol()) {
            result.setProtocol(parsedEvent.getProtocol());
        }

        // handling remote address
        if (parsedEvent.hasRemoteAddress()) {
            result.setRemoteAddress(parsedEvent.getRemoteAddress());
        }

        // handling remote host
        if (parsedEvent.hasRemoteHost()) {
            result.setRemoteHost(parsedEvent.getRemoteHost());
        }

        // handling remote user
        if (parsedEvent.hasRemoteUser()) {
            result.setRemoteUser(parsedEvent.getRemoteUser());
        }

        // handling request uri
        if (parsedEvent.hasRequestUri()) {
            result.setRequestURI(parsedEvent.getRequestUri());
        }

        // handling request url
        if (parsedEvent.hasRequestUrl()) {
            result.setRequestURL(parsedEvent.getRequestUrl());
        }

        // handling server name
        if (parsedEvent.hasServerName()) {
            result.setServerName(parsedEvent.getServerName());
        }

        // handling timestamp
        if (parsedEvent.hasTimeStamp()) {
            result.setTimeStamp(parsedEvent.getTimeStamp());
        }

        // handling local port
        if (parsedEvent.hasLocalPort()) {
            result.setLocalPort(parsedEvent.getLocalPort());
        }

        // handling status code
        if (parsedEvent.hasStatusCode()) {
            result.setStatusCode(parsedEvent.getStatusCode());
        }

        // handling request headers
        if (parsedEvent.hasRequestHeaders()) {
            result.setRequestHeaders(convertStringMap(parsedEvent.getRequestHeaders()));
        }

        // handling response headers
        if (parsedEvent.hasResponseHeaders()) {
            result.setResponseHeaders(convertStringMap(parsedEvent.getResponseHeaders()));
        }

        // handling request parameters
        if (parsedEvent.hasRequestParameters()) {
            result.setRequestParameters(convertStringArrayMap(parsedEvent.getRequestParameters()));
        }

        // handling logger context
        if (parsedEvent.hasLoggerContext()) {
            result.setLoggerContext(convert(parsedEvent.getLoggerContext()));
        }

        return result;
    }

    public static LoggerContext convert(AccessProto.LoggerContext loggerContext) {
        if (loggerContext == null) {
            return null;
        }
        LoggerContext result = new LoggerContext();
        if (loggerContext.hasName()) {
            result.setName(loggerContext.getName());
        }
        if (loggerContext.hasBirthTime()) {
            result.setBirthTime(new Date(loggerContext.getBirthTime()));
        }
        if (loggerContext.hasProperties()) {
            result.setProperties(convertStringMap(loggerContext.getProperties()));
        }
        return result;
    }

    public static Map<String, String> convertStringMap(AccessProto.StringMap data) {
        if (data == null) {
            return null;
        }
        Map<String, String> result = new HashMap<String, String>();
        List<AccessProto.StringMapEntry> entries = data.getEntryList();
        for (AccessProto.StringMapEntry current : entries) {
            String key = null;
            String value = null;
            if (current.hasKey()) {
                key = current.getKey();
            }
            if (current.hasValue()) {
                value = current.getValue();
            }
            if (key != null) {
                result.put(key, value);
            }
        }
        return result;
    }

    public static Map<String, String[]> convertStringArrayMap(AccessProto.StringArrayMap data) {
        if (data == null) {
            return null;
        }
        Map<String, String[]> result = new HashMap<String, String[]>();
        List<AccessProto.StringArrayMapEntry> entries = data.getEntryList();
        for (AccessProto.StringArrayMapEntry current : entries) {
            String key = null;
            String[] values = null;
            if (current.hasKey()) {
                key = current.getKey();
            }
            int count = current.getValueCount();
            if (count > 0) {
                List<String> valueList = new ArrayList<String>(count);
                for (AccessProto.StringArrayValue curVal : current.getValueList()) {
                    if (curVal.hasValue()) {
                        valueList.add(curVal.getValue());
                    } else {
                        valueList.add(null);
                    }
                }

                values = valueList.toArray(new String[count]);
            }
            if (key != null) {
                result.put(key, values);
            }
        }
        return result;
    }
}
