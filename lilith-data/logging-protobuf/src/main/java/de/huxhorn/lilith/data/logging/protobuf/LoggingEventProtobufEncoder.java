/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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
 * Copyright 2007-2017 Joern Huxhorn
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

package de.huxhorn.lilith.data.logging.protobuf;

import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.lilith.data.logging.protobuf.generated.LoggingProto;
import de.huxhorn.sulky.codec.Encoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

class LoggingEventProtobufEncoder
	implements Encoder<LoggingEvent>
{
	private final boolean compressing;

	LoggingEventProtobufEncoder(boolean compressing)
	{
		this.compressing = compressing;
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	@Override
	@SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
	public byte[] encode(LoggingEvent event)
	{
		LoggingProto.LoggingEvent converted = convert(event);
		if(converted == null)
		{
			return null;
		}
		if(!compressing)
		{
			return converted.toByteArray();
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gos;
		try
		{
			gos = new GZIPOutputStream(out);
			converted.writeTo(gos);
			gos.flush();
			gos.close();
			return out.toByteArray();
		}
		catch(IOException e)
		{
			// ignore
		}
		return null;
	}

	public static LoggingProto.Marker convert(Marker marker)
	{
		if(marker == null)
		{
			return null;
		}
		List<String> handledMarkers = new ArrayList<>();
		return convert(marker, handledMarkers);
	}

	private static LoggingProto.Marker convert(Marker marker, List<String> handledMarkers)
	{
		String markerName = marker.getName();
		LoggingProto.Marker.Builder builder = LoggingProto.Marker.newBuilder().setName(markerName);
		if(handledMarkers.contains(markerName))
		{
			return builder.build();
		}
		handledMarkers.add(markerName);
		boolean hasReferences = marker.hasReferences();
		if(hasReferences)
		{
			Map<String, Marker> refs = marker.getReferences();
			for(Map.Entry<String, Marker> current : refs.entrySet())
			{
				builder.addReference(convert(current.getValue(), handledMarkers));
			}
		}

		return builder.build();
	}

	public static LoggingProto.StackTraceElement convert(ExtendedStackTraceElement ste)
	{
		if(ste == null)
		{
			return null;
		}
		LoggingProto.StackTraceElement.Builder builder = LoggingProto.StackTraceElement.newBuilder();

		{
			String classLoaderName = ste.getClassLoaderName();
			if(classLoaderName != null)
			{
				builder.setClassLoaderName(classLoaderName);
			}
		}

		{
			String moduleName = ste.getModuleName();
			if(moduleName != null)
			{
				builder.setModuleName(moduleName);
			}
		}


		{
			String moduleVersion = ste.getModuleVersion();
			if(moduleVersion != null)
			{
				builder.setModuleVersion(moduleVersion);
			}
		}

		{
			String methodName = ste.getMethodName();
			if(methodName != null)
			{
				builder.setMethodName(methodName);
			}
		}

		{
			String className = ste.getClassName();
			if(className != null)
			{
				builder.setClassName(className);
			}
		}

		{
			String fileName = ste.getFileName();
			if(fileName != null)
			{
				builder.setFileName(fileName);
			}
		}

		{
			int lineNumber = ste.getLineNumber();
			builder.setLineNumber(lineNumber);
		}

		{
			String codeLocation = ste.getCodeLocation();
			if(codeLocation != null)
			{
				builder.setCodeLocation(codeLocation);
			}
		}

		{
			String version = ste.getVersion();
			if(version != null)
			{
				builder.setVersion(version);
			}
		}

		builder.setExact(ste.isExact());

		return builder.build();
	}

	public static LoggingProto.Throwable convert(ThrowableInfo throwableInfo)
	{
		if(throwableInfo == null)
		{
			return null;
		}
		LoggingProto.Throwable.Builder builder = LoggingProto.Throwable.newBuilder();

		{
			String name = throwableInfo.getName();
			if(name != null)
			{
				builder.setThrowableClass(name);
			}
		}

		{
			String message = throwableInfo.getMessage();
			if(message != null)
			{
				builder.setMessage(message);
			}
		}

		{
			int omittedElements = throwableInfo.getOmittedElements();
			if(omittedElements > 0)
			{
				builder.setOmittedElements(omittedElements);
			}
		}

		{
			ThrowableInfo[] suppressed = throwableInfo.getSuppressed();
			if(suppressed != null)
			{
				for(ThrowableInfo current : suppressed)
				{
					if(current != null)
					{
						builder.addSuppressed(convert(current));
					}
				}
			}
		}

		{
			ThrowableInfo cause = throwableInfo.getCause();
			if(cause != null)
			{
				builder.setCause(convert(cause));
			}
		}

		{
			ExtendedStackTraceElement[] stackTrace = throwableInfo.getStackTrace();
			if(stackTrace != null)
			{
				for(ExtendedStackTraceElement current : stackTrace)
				{
					if(current != null)
					{
						builder.addStackTraceElement(convert(current));
					}
				}
			}
		}
		return builder.build();
	}

	public static LoggingProto.Message convert(Message message)
	{
		if(message == null)
		{
			return null;
		}
		LoggingProto.Message.Builder messageBuilder = LoggingProto.Message.newBuilder();
		String pattern = message.getMessagePattern();
		if(pattern != null)
		{
			messageBuilder.setMessagePattern(pattern);
		}

		String[] arguments = message.getArguments();
		if(arguments != null)
		{
			for(String current : arguments)
			{
				LoggingProto.MessageArgument.Builder argumentBuilder = LoggingProto.MessageArgument.newBuilder();
				if(current != null)
				{
					argumentBuilder.setValue(current);
				}
				messageBuilder.addArgument(argumentBuilder.build());
			}
		}
		return messageBuilder.build();
	}

	public static LoggingProto.ThreadInfo convert(ThreadInfo threadInfo)
	{
		if(threadInfo == null)
		{
			return null;
		}
		LoggingProto.ThreadInfo.Builder builder = LoggingProto.ThreadInfo.newBuilder();

		{
			String name = threadInfo.getName();
			if(name != null)
			{
				builder.setName(name);
			}
		}

		{
			Long id = threadInfo.getId();
			if(id != null)
			{
				builder.setId(id);
			}
		}

		{
			String name = threadInfo.getGroupName();
			if(name != null)
			{
				builder.setGroupName(name);
			}
		}

		{
			Long id = threadInfo.getGroupId();
			if(id != null)
			{
				builder.setGroupId(id);
			}
		}

		{
			Integer priority = threadInfo.getPriority();
			if(priority != null)
			{
				builder.setPriority(priority);
			}
		}
		return builder.build();
	}

	public static LoggingProto.LoggerContext convert(LoggerContext context)
	{
		if(context == null)
		{
			return null;
		}
		LoggingProto.LoggerContext.Builder builder = LoggingProto.LoggerContext.newBuilder();
		{
			String name = context.getName();
			if(name != null)
			{
				builder.setName(name);
			}
		}
		{
			Long birthTime = context.getBirthTime();
			if(birthTime != null)
			{
				builder.setBirthTime(birthTime);
			}
		}
		{
			Map<String, String> map = context.getProperties();
			if(map != null && !map.isEmpty())
			{
				builder.setProperties(convert(map));
			}
		}
		return builder.build();

	}

	public static LoggingProto.StringMap convert(Map<String, String> map)
	{
		if(map == null)
		{
			return null;
		}
		LoggingProto.StringMap.Builder builder = LoggingProto.StringMap.newBuilder();
		for(Map.Entry<String, String> current : map.entrySet())
		{
			LoggingProto.StringMapEntry.Builder entryBuilder = LoggingProto.StringMapEntry.newBuilder()
				.setKey(current.getKey());
			String value = current.getValue();
			if(value != null)
			{
				entryBuilder.setValue(value);
			}
			builder.addEntry(entryBuilder.build());
		}
		return builder.build();
	}

	public static LoggingProto.LoggingEvent convert(LoggingEvent event)
	{
		if(event == null)
		{
			return null;
		}
		LoggingProto.LoggingEvent.Builder eventBuilder = LoggingProto.LoggingEvent.newBuilder();

		// handling loggerName
		{
			String loggerName = event.getLogger();
			if(loggerName != null)
			{
				eventBuilder.setLoggerName(loggerName);
			}
		}

		// handling sequence number
		{
			Long sequence = event.getSequenceNumber();
			if(sequence != null)
			{
				eventBuilder.setSequenceNumber(sequence);
			}
		}

		// handling threadInfo
		{
			ThreadInfo threadInfo = event.getThreadInfo();
			if(threadInfo != null)
			{
				eventBuilder.setThreadInfo(convert(threadInfo));
			}
		}

		// handling level
		{
			LoggingEvent.Level level = event.getLevel();
			if(level != null)
			{
				switch(level)
				{
					case TRACE:
						eventBuilder.setLevel(LoggingProto.Level.TRACE);
						break;
					case DEBUG:
						eventBuilder.setLevel(LoggingProto.Level.DEBUG);
						break;
					case INFO:
						eventBuilder.setLevel(LoggingProto.Level.INFO);
						break;
					case WARN:
						eventBuilder.setLevel(LoggingProto.Level.WARN);
						break;
					default: // ERROR
						eventBuilder.setLevel(LoggingProto.Level.ERROR);
						break;
				}
			}
		}

		// handle LoggerContext
		{
			LoggerContext context = event.getLoggerContext();
			if(context != null)
			{
				eventBuilder.setLoggerContext(convert(context));
			}
		}

		// handle Throwable
		{
			ThrowableInfo throwable = event.getThrowable();
			if(throwable != null)
			{
				eventBuilder.setThrowable(convert(throwable));
			}
		}

		// handle Marker
		{
			Marker marker = event.getMarker();
			if(marker != null)
			{
				eventBuilder.setMarker(convert(marker));
			}
		}

		// handle CallStack
		{
			ExtendedStackTraceElement[] callStack = event.getCallStack();
			if(callStack != null)
			{
				for(ExtendedStackTraceElement current : callStack)
				{
					if(current != null)
					{
						eventBuilder.addCallStackElement(convert(current));
					}
				}
			}
		}

		// handling timestamp
		{
			Long timeStamp = event.getTimeStamp();
			if(timeStamp != null)
			{
				eventBuilder.setTimeStamp(timeStamp);
			}
		}

		// handling event message
		{
			Message message = event.getMessage();
			if(message != null)
			{
				eventBuilder.setMessage(convert(message));
			}
		}

		// handling MappedDiagnosticContext
		Map<String, String> mdc = event.getMdc();
		if(mdc != null && !mdc.isEmpty())
		{
			eventBuilder.setMappedDiagnosticContext(convert(mdc));
		}

		// handling NestedDiagnosticContext
		Message[] ndc = event.getNdc();
		if(ndc != null && ndc.length > 0)
		{
			LoggingProto.NestedDiagnosticContext.Builder ndcBuilder = LoggingProto.NestedDiagnosticContext.newBuilder();
			for(Message currentMessage : ndc)
			{
				ndcBuilder.addEntry(convert(currentMessage));
			}
			eventBuilder.setNestedDiagnosticContext(ndcBuilder.build());
		}

		return eventBuilder.build();
	}
}
