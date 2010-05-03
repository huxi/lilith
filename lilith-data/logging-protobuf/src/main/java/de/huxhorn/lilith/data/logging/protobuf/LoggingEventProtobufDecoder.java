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
package de.huxhorn.lilith.data.logging.protobuf;

import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.lilith.data.logging.protobuf.generated.LoggingProto;
import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.sulky.codec.Decoder;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class LoggingEventProtobufDecoder
	implements Decoder<LoggingEvent>
{
	private boolean compressing;

	public LoggingEventProtobufDecoder(boolean compressing)
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

	public LoggingEvent decode(byte[] bytes)
	{
		if(bytes == null)
		{
			return null;
		}
		LoggingProto.LoggingEvent parsedEvent = null;
		if(!compressing)
		{
			try
			{
				parsedEvent = LoggingProto.LoggingEvent.parseFrom(bytes);
			}
			catch(InvalidProtocolBufferException e)
			{
				// ignore
			}
		}
		else
		{
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			try
			{
				GZIPInputStream gis = new GZIPInputStream(in);
				parsedEvent = LoggingProto.LoggingEvent.parseFrom(gis);
				gis.close();
			}
			catch(IOException e)
			{
				// ignore
			}
		}
		return convert(parsedEvent);
	}

	public static Marker convert(LoggingProto.Marker marker)
	{
		if(marker == null)
		{
			return null;
		}
		Map<String, Marker> markers = new HashMap<String, Marker>();
		return convert(marker, markers);
	}

	private static Marker convert(LoggingProto.Marker marker, Map<String, Marker> markers)
	{
		String markerName = marker.getName();

		Marker result = markers.get(markerName);
		if(result == null)
		{
			// new marker
			result = new Marker();
			result.setName(markerName);
			markers.put(markerName, result);
		}

		int refCount = marker.getReferenceCount();
		if(refCount > 0)
		{
			List<LoggingProto.Marker> refList = marker.getReferenceList();
			for(LoggingProto.Marker current : refList)
			{
				result.add(convert(current, markers));
			}
		}

		return result;
	}

	public static ExtendedStackTraceElement convert(LoggingProto.StackTraceElement ste)
	{
		if(ste == null)
		{
			return null;
		}

		ExtendedStackTraceElement result = new ExtendedStackTraceElement();
		if(ste.hasMethodName())
		{
			result.setMethodName(ste.getMethodName());
		}

		if(ste.hasClassName())
		{
			result.setClassName(ste.getClassName());
		}

		if(ste.hasFileName())
		{
			result.setFileName(ste.getFileName());
		}

		if(ste.hasLineNumber())
		{
			result.setLineNumber(ste.getLineNumber());
		}

		if(ste.hasCodeLocation())
		{
			result.setCodeLocation(ste.getCodeLocation());
		}

		if(ste.hasVersion())
		{
			result.setVersion(ste.getVersion());
		}

		if(ste.hasExact())
		{
			result.setExact(ste.getExact());
		}

		return result;

	}

	public static ThrowableInfo convert(LoggingProto.Throwable throwable)
	{
		if(throwable == null)
		{
			return null;
		}

		ThrowableInfo result = new ThrowableInfo();

		if(throwable.hasThrowableClass())
		{
			result.setName(throwable.getThrowableClass());
		}

		if(throwable.hasMessage())
		{
			result.setMessage(throwable.getMessage());
		}

		if(throwable.hasOmittedElements())
		{
			result.setOmittedElements(throwable.getOmittedElements());
		}

		if(throwable.hasCause())
		{
			result.setCause(convert(throwable.getCause()));
		}

		{
			int count = throwable.getStackTraceElementCount();
			if(count > 0)
			{
				ExtendedStackTraceElement[] stackTrace = new ExtendedStackTraceElement[count];
				List<LoggingProto.StackTraceElement> stackTraceElementList = throwable.getStackTraceElementList();
				for(int i = 0; i < count; i++)
				{
					stackTrace[i] = convert(stackTraceElementList.get(i));
				}
				result.setStackTrace(stackTrace);
			}
		}
		return result;
	}

	public static Message convert(LoggingProto.Message parsedMessage)
	{
		if(parsedMessage == null)
		{
			return null;
		}
		Message result = new Message();
		if(parsedMessage.hasMessagePattern())
		{
			result.setMessagePattern(parsedMessage.getMessagePattern());
		}
		int argumentCount = parsedMessage.getArgumentCount();
		if(argumentCount > 0)
		{
			String[] arguments = new String[argumentCount];
			List<LoggingProto.MessageArgument> argumentList = parsedMessage.getArgumentList();
			for(int i = 0; i < argumentCount; i++)
			{
				LoggingProto.MessageArgument current = argumentList.get(i);
				if(current.hasValue())
				{
					arguments[i] = current.getValue();
				}
			}
			result.setArguments(arguments);
		}
		return result;
	}

	public static ThreadInfo convert(LoggingProto.ThreadInfo parsedThreadInfo)
	{
		if(parsedThreadInfo == null)
		{
			return null;
		}
		Long threadId = null;
		if(parsedThreadInfo.hasId())
		{
			threadId = parsedThreadInfo.getId();
		}
		String threadName = null;
		if(parsedThreadInfo.hasName())
		{
			threadName = parsedThreadInfo.getName();
		}
		Long threadGroupId = null;
		if(parsedThreadInfo.hasGroupId())
		{
			threadGroupId = parsedThreadInfo.getGroupId();
		}
		String threadGroupName = null;
		if(parsedThreadInfo.hasGroupName())
		{
			threadGroupName = parsedThreadInfo.getGroupName();
		}
		return new ThreadInfo(threadId, threadName, threadGroupId, threadGroupName);
	}

	public static LoggerContext convert(LoggingProto.LoggerContext loggerContext)
	{
		if(loggerContext == null)
		{
			return null;
		}
		LoggerContext result = new LoggerContext();
		if(loggerContext.hasName())
		{
			result.setName(loggerContext.getName());
		}
		if(loggerContext.hasBirthTime())
		{
			result.setBirthTime(loggerContext.getBirthTime());
		}
		if(loggerContext.hasProperties())
		{
			result.setProperties(convert(loggerContext.getProperties()));
		}
		return result;
	}

	public static Map<String, String> convert(LoggingProto.StringMap stringMap)
	{
		if(stringMap == null)
		{
			return null;
		}
		if(stringMap.getEntryCount() > 0)
		{
			Map<String, String> result = new HashMap<String, String>();
			List<LoggingProto.StringMapEntry> mdcList = stringMap.getEntryList();
			for(LoggingProto.StringMapEntry current : mdcList)
			{
				String key = current.getKey();
				String value = null;
				if(current.hasValue())
				{
					value = current.getValue();
				}
				result.put(key, value);
			}
			return result;
		}

		return null;
	}

	public static LoggingEvent convert(LoggingProto.LoggingEvent parsedEvent)
	{
		if(parsedEvent == null)
		{
			return null;
		}

		LoggingEvent result = new LoggingEvent();

		// handling loggerName
		if(parsedEvent.hasLoggerName())
		{
			result.setLogger(parsedEvent.getLoggerName());
		}

		// handling sequence number
		if(parsedEvent.hasSequenceNumber())
		{
			result.setSequenceNumber(parsedEvent.getSequenceNumber());
		}

		// handling threadInfo
		if(parsedEvent.hasThreadInfo())
		{
			result.setThreadInfo(convert(parsedEvent.getThreadInfo()));
		}

		// handling level
		if(parsedEvent.hasLevel())
		{
			LoggingProto.Level level = parsedEvent.getLevel();
			switch(level)
			{
				case TRACE:
					result.setLevel(LoggingEvent.Level.TRACE);
					break;
				case DEBUG:
					result.setLevel(LoggingEvent.Level.DEBUG);
					break;
				case INFO:
					result.setLevel(LoggingEvent.Level.INFO);
					break;
				case WARN:
					result.setLevel(LoggingEvent.Level.WARN);
					break;
				case ERROR:
					result.setLevel(LoggingEvent.Level.ERROR);
					break;
			}
		}

		// handle LoggerContext
		if(parsedEvent.hasLoggerContext())
		{
			result.setLoggerContext(convert(parsedEvent.getLoggerContext()));
		}

		// handle Throwable
		if(parsedEvent.hasThrowable())
		{
			result.setThrowable(convert(parsedEvent.getThrowable()));
		}

		// handle Marker
		if(parsedEvent.hasMarker())
		{
			result.setMarker(convert(parsedEvent.getMarker()));

		}

		// handle CallStack
		{
			int count = parsedEvent.getCallStackElementCount();
			if(count > 0)
			{
				List<LoggingProto.StackTraceElement> callStackElements = parsedEvent.getCallStackElementList();
				ExtendedStackTraceElement[] callStack = new ExtendedStackTraceElement[count];
				for(int i = 0; i < count; i++)
				{
					LoggingProto.StackTraceElement current = callStackElements.get(i);
					callStack[i] = convert(current);
				}
				result.setCallStack(callStack);
			}
		}

		// handling timestamp
		if(parsedEvent.hasTimeStamp())
		{
			result.setTimeStamp(parsedEvent.getTimeStamp());
		}

		// handling event message
		if(parsedEvent.hasMessage())
		{
			result.setMessage(convert(parsedEvent.getMessage()));
		}

		// handling MappedDiagnosticContext
		if(parsedEvent.hasMappedDiagnosticContext())
		{
			result.setMdc(convert(parsedEvent.getMappedDiagnosticContext()));
		}

		// handling NestedDiagnosticContext
		if(parsedEvent.hasNestedDiagnosticContext())
		{
			LoggingProto.NestedDiagnosticContext parsedNdc = parsedEvent.getNestedDiagnosticContext();
			int entryCount = parsedNdc.getEntryCount();
			if(entryCount > 0)
			{
				List<LoggingProto.Message> entryList = parsedNdc.getEntryList();
				Message[] ndc = new Message[entryCount];
				for(int i = 0; i < entryCount; i++)
				{
					ndc[i] = convert(entryList.get(i));
				}
				result.setNdc(ndc);
			}
		}
		return result;
	}
}
