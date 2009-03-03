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
package de.huxhorn.lilith.data.logging.protobuf;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.protobuf.generated.LoggingProto;
import de.huxhorn.sulky.generics.io.Deserializer;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class LoggingEventDeserializer
	implements Deserializer<LoggingEvent>
{
	public static Message convert(LoggingProto.Message parsedMessage)
	{
		if(parsedMessage==null)
		{
			return null;
		}
		Message result=new Message();
		if(parsedMessage.hasMessagePattern())
		{
			result.setMessagePattern(parsedMessage.getMessagePattern());
		}
		int argumentCount = parsedMessage.getArgumentCount();
		if(argumentCount>0)
		{
			List<String> argumentList = parsedMessage.getArgumentList();
			String[] arguments = argumentList.toArray(new String[argumentCount]);
			result.setArguments(arguments);
		}
		return result;
	}

	public static LoggingEvent convert(LoggingProto.LoggingEvent parsedEvent)
	{
		if(parsedEvent == null)
		{
			return null;
		}

		LoggingEvent result=new LoggingEvent();

		// handling loggerName
		if(parsedEvent.hasLoggerName())
		{
			result.setLogger(parsedEvent.getLoggerName());
		}

		// handling threadName
		if(parsedEvent.hasThreadName())
		{
			result.setThreadName(parsedEvent.getThreadName());
		}

		// TODO: handling threadId

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

		// TODO: handle Throwable
		// TODO: handle Marker
		// TODO: handle CallStack
		// TODO: handle ApplicationIdentifier

		// handling timestamp
		if(parsedEvent.hasTimeStamp())
		{
			result.setTimeStamp(new Date(parsedEvent.getTimeStamp()));
		}

		// handling event message
		if(parsedEvent.hasMessage())
		{
			Message message = convert(parsedEvent.getMessage());
			if(message != null)
			{
				result.setMessagePattern(message.getMessagePattern());
				result.setArguments(message.getArguments());
			}
		}

		// handling MappedDiagnosticContext
		if(parsedEvent.hasMappedDiagnosticContext())
		{
			LoggingProto.MappedDiagnosticContext parsedMdc = parsedEvent.getMappedDiagnosticContext();
			if(parsedMdc.getEntryCount()>0)
			{
				Map<String, String> mdc=new HashMap<String, String>();
				List<LoggingProto.MapEntry> mdcList = parsedMdc.getEntryList();
				for(LoggingProto.MapEntry current: mdcList)
				{
					String key=current.getKey();
					String value=null;
					if(current.hasValue())
					{
						value=current.getValue();
					}
					mdc.put(key, value);
				}
				result.setMdc(mdc);
			}
		}

		// handling NestedDiagnosticContext
		if(parsedEvent.hasNestedDiagnosticContext())
		{
			LoggingProto.NestedDiagnosticContext parsedNdc = parsedEvent.getNestedDiagnosticContext();
			int entryCount = parsedNdc.getEntryCount();
			if(entryCount>0)
			{
				List<LoggingProto.Message> entryList = parsedNdc.getEntryList();
				Message[] ndc = new Message[entryCount];
				for(int i=0;i<entryCount;i++)
				{
					ndc[i]=convert(entryList.get(i));
				}
				result.setNdc(ndc);
			}
		}
		return result;
	}

	public LoggingEvent deserialize(byte[] bytes)
	{
		if(bytes==null)
		{
			return null;
		}
		try
		{
			LoggingProto.LoggingEvent parsedEvent = LoggingProto.LoggingEvent.parseFrom(bytes);
			return convert(parsedEvent);
		}
		catch(InvalidProtocolBufferException e)
		{
			// ignore
		}
		return null;
	}
}
