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
import de.huxhorn.sulky.generics.io.Serializer;

import java.util.Map;
import java.util.Date;

public class LoggingEventSerializer
	implements Serializer<LoggingEvent>
{
	public static LoggingProto.Message convert(Message message)
	{
		if(message==null)
		{
			return null;
		}
		LoggingProto.Message.Builder messageBuilder = LoggingProto.Message.newBuilder();
		messageBuilder.setMessagePattern(message.getMessagePattern());

		String[] arguments = message.getArguments();
		if(arguments!=null)
		{
			for(String current:arguments)
			{
				LoggingProto.MessageArgument.Builder argumentBuilder = LoggingProto.MessageArgument.newBuilder();
				if(current!=null)
				{
					argumentBuilder.setValue(current);
				}
				messageBuilder.addArgument(argumentBuilder.build());
			}
		}
		return messageBuilder.build();
	}

	public static LoggingProto.LoggingEvent convert(LoggingEvent event)
	{
		if(event == null)
		{
			return null;
		}
		LoggingProto.LoggingEvent.Builder eventBuilder = LoggingProto.LoggingEvent.newBuilder();

		// handling loggerName
		eventBuilder.setLoggerName(event.getLogger());

		// handling threadName
		{
			String threadName = event.getThreadName();
			if(threadName != null)
			{
				eventBuilder.setThreadName(threadName);
			}
		}

		// TODO: handling threadId

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
					case ERROR:
							eventBuilder.setLevel(LoggingProto.Level.ERROR);
						break;
				}
			}
		}

		// handle ApplicationIdentifier
		if(event.getApplicationIdentifier() != null)
		{
			eventBuilder.setApplicationIdentifier(event.getApplicationIdentifier());
		}

		// TODO: handle Throwable
		// TODO: handle Marker
		// TODO: handle CallStack

		// handling timestamp
		{
			Date timeStamp = event.getTimeStamp();
			if(timeStamp!=null)
			{
				eventBuilder.setTimeStamp(timeStamp.getTime());
			}
		}

		// handling event message
		{
			Message message=new Message();
			message.setMessagePattern(event.getMessagePattern());
			message.setArguments(event.getArguments());
			eventBuilder.setMessage(convert(message));
		}

		// handling MappedDiagnosticContext
		Map<String, String> mdc = event.getMdc();
		if(mdc != null && mdc.size()>0)
		{
			LoggingProto.MappedDiagnosticContext.Builder mdcBuilder = LoggingProto.MappedDiagnosticContext.newBuilder();
			for(Map.Entry<String,String> current:mdc.entrySet())
			{
				LoggingProto.MapEntry.Builder entryBuilder = LoggingProto.MapEntry.newBuilder()
					.setKey(current.getKey());
				String value=current.getValue();
				if(value!=null)
				{
					entryBuilder.setValue(value);
				}
				mdcBuilder.addEntry(entryBuilder.build());
			}
			eventBuilder.setMappedDiagnosticContext(mdcBuilder.build());
		}

		// handling NestedDiagnosticContext
		Message[] ndc = event.getNdc();
		if(ndc!=null && ndc.length>0)
		{
			LoggingProto.NestedDiagnosticContext.Builder ndcBuilder = LoggingProto.NestedDiagnosticContext.newBuilder();
			for(Message currentMessage: ndc)
			{
				ndcBuilder.addEntry(convert(currentMessage));
			}
			eventBuilder.setNestedDiagnosticContext(ndcBuilder.build());
		}
		
		return eventBuilder.build();
	}

	public byte[] serialize(LoggingEvent event)
	{
		LoggingProto.LoggingEvent converted = convert(event);
		if(converted==null)
		{
			return null;
		}
		return converted.toByteArray();
	}
}
