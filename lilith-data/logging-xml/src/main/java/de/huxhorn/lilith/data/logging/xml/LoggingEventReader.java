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

package de.huxhorn.lilith.data.logging.xml;

import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.sulky.stax.DateTimeFormatter;
import de.huxhorn.sulky.stax.GenericStreamReader;
import de.huxhorn.sulky.stax.StaxUtilities;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class LoggingEventReader
	implements GenericStreamReader<LoggingEvent>, LoggingEventSchemaConstants
{
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	private final DateTimeFormatter dateTimeFormatter;
	private final StackTraceElementReader steReader;

	public LoggingEventReader()
	{
		dateTimeFormatter = new DateTimeFormatter();
		steReader = new StackTraceElementReader();
	}

	@Override
	public LoggingEvent read(XMLStreamReader reader)
		throws XMLStreamException
	{
		LoggingEvent result = null;
		int type = reader.getEventType();

		if(XMLStreamConstants.START_DOCUMENT == type)
		{
			reader.nextTag();
			type = reader.getEventType();
		}
		if(XMLStreamConstants.START_ELEMENT == type && LOGGING_EVENT_NODE.equals(reader.getLocalName()))
		{
			result = new LoggingEvent();
			result.setLogger(StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, LOGGER_ATTRIBUTE));
			result
				.setLevel(LoggingEvent.Level.valueOf(StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, LEVEL_ATTRIBUTE)));

			{
				String sequence = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, SEQUENCE_ATTRIBUTE);
				if(sequence != null)
				{
					try
					{
						result.setSequenceNumber(Long.parseLong(sequence));
					}
					catch(NumberFormatException ex)
					{
						// ignore
					}
				}
			}

			{
				String threadName = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, THREAD_NAME_ATTRIBUTE);
				String threadIdStr = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, THREAD_ID_ATTRIBUTE);
				String threadGroupName = StaxUtilities
					.readAttributeValue(reader, NAMESPACE_URI, THREAD_GROUP_NAME_ATTRIBUTE);
				String threadGroupIdStr = StaxUtilities
					.readAttributeValue(reader, NAMESPACE_URI, THREAD_GROUP_ID_ATTRIBUTE);
				String threadPriorityStr = StaxUtilities
					.readAttributeValue(reader, NAMESPACE_URI, THREAD_PRIORITY_ATTRIBUTE);
				Long threadId = null;
				if(threadIdStr != null)
				{
					try
					{
						threadId = Long.valueOf(threadIdStr);
					}
					catch(NumberFormatException ex)
					{
						// ignore
					}
				}
				Long threadGroupId = null;
				if(threadGroupIdStr != null)
				{
					try
					{
						threadGroupId = Long.valueOf(threadGroupIdStr);
					}
					catch(NumberFormatException ex)
					{
						// ignore
					}
				}
				Integer threadPriority = null;
				if(threadPriorityStr != null)
				{
					try
					{
						threadPriority = Integer.valueOf(threadPriorityStr);
					}
					catch(NumberFormatException ex)
					{
						// ignore
					}
				}
				if(threadName != null || threadId != null || threadGroupName != null || threadGroupId != null || threadPriority != null)
				{
					ThreadInfo threadInfo = new ThreadInfo(threadId, threadName, threadGroupId, threadGroupName);
					threadInfo.setPriority(threadPriority);
					result.setThreadInfo(threadInfo);
				}
			}

			result.setTimeStamp(readTimeStamp(reader, TIMESTAMP_MILLIS_ATTRIBUTE, TIMESTAMP_ATTRIBUTE));

			reader.nextTag();
			Message message = null;
			{
				String messagePattern = StaxUtilities.readSimpleTextNodeIfAvailable(reader, null, MESSAGE_NODE);
				List<String> args = readArguments(reader);
				if(messagePattern != null || args != null)
				{
					if(args != null)
					{
						message = new Message(messagePattern, args.toArray(EMPTY_STRING_ARRAY));
					}
					else
					{
						message = new Message(messagePattern);
					}
				}
			}
			result.setMessage(message);

			readThrowable(reader, result);
			result.setMdc(readMdc(reader));
			result.setNdc(readNdc(reader));
			readMarker(reader, result);
			readCallStack(reader, result);
			result.setLoggerContext(readLoggerContext(reader));
			reader.require(XMLStreamConstants.END_ELEMENT, null, LOGGING_EVENT_NODE);
		}
		return result;
	}

	private Long readTimeStamp(XMLStreamReader reader, String millisName, String formattedName)
	{
		Long timeStamp = null;

		String timeStampMillis = StaxUtilities
			.readAttributeValue(reader, NAMESPACE_URI, millisName);
		if(timeStampMillis != null)
		{
			try
			{
				timeStamp = Long.parseLong(timeStampMillis);
			}
			catch(NumberFormatException ex)
			{
				// ignore
			}
		}
		if(timeStamp == null)
		{
			String timeStampStr = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, formattedName);
			if(timeStampStr != null)
			{
				try
				{
					timeStamp = dateTimeFormatter.parse(timeStampStr).getTime();
				}
				catch(ParseException e)
				{
					// ignore
				}
			}
		}
		return timeStamp;
	}

	private LoggerContext readLoggerContext(XMLStreamReader reader)
		throws XMLStreamException
	{
		LoggerContext result = null;
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && LOGGER_CONTEXT_NODE.equals(reader.getLocalName()))
		{
			result = new LoggerContext();

			result.setName(StaxUtilities
				.readAttributeValue(reader, NAMESPACE_URI, LOGGER_CONTEXT_NAME_ATTRIBUTE));

			result
				.setBirthTime(readTimeStamp(reader, LOGGER_CONTEXT_BIRTH_TIME_MILLIS_ATTRIBUTE, LOGGER_CONTEXT_BIRTH_TIME_ATTRIBUTE));
			reader.nextTag();
			result.setProperties(readLoggerContextProperties(reader));
			reader.require(XMLStreamConstants.END_ELEMENT, null, LOGGER_CONTEXT_NODE);
			reader.nextTag();
		}
		return result;
	}

	private Map<String, String> readLoggerContextProperties(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && LOGGER_CONTEXT_PROPERTIES_NODE.equals(reader.getLocalName()))
		{
			Map<String, String> map = new HashMap<>();
			reader.nextTag();
			for(;;)
			{
				StringMapEntry entry = readStringMapEntry(reader);
				if(entry == null)
				{
					break;
				}
				map.put(entry.key, entry.value);
			}
			reader.require(XMLStreamConstants.END_ELEMENT, null, LOGGER_CONTEXT_PROPERTIES_NODE);
			reader.nextTag();
			return map;
		}
		return null;
	}

	private void readCallStack(XMLStreamReader reader, LoggingEvent event)
		throws XMLStreamException
	{
		event.setCallStack(readStackTraceNode(reader, CALLSTACK_NODE));
	}

	@SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
	private ExtendedStackTraceElement[] readStackTraceNode(XMLStreamReader reader, String nodeName)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		ArrayList<ExtendedStackTraceElement> ste = new ArrayList<>();
		if(XMLStreamConstants.START_ELEMENT == type && nodeName.equals(reader.getLocalName()))
		{
			reader.nextTag();
			for(;;)
			{
				ExtendedStackTraceElement elem = steReader.read(reader);//readStackTraceElement(reader);
				if(elem == null)
				{
					break;
				}
				reader.nextTag();
				ste.add(elem);
			}
			reader.require(XMLStreamConstants.END_ELEMENT, null, nodeName);
			reader.nextTag();
			return ste.toArray(ExtendedStackTraceElement.ARRAY_PROTOTYPE);
		}
		return null;
	}

	private void readMarker(XMLStreamReader reader, LoggingEvent event)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && MARKER_NODE.equals(reader.getLocalName()))
		{
			Map<String, Marker> markers = new HashMap<>();
			Marker marker = recursiveReadMarker(reader, markers);
			event.setMarker(marker);
		}
	}

	private Marker recursiveReadMarker(XMLStreamReader reader, Map<String, Marker> markers)
		throws XMLStreamException
	{
		Marker marker = null;
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && MARKER_NODE.equals(reader.getLocalName()))
		{
			String name = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, MARKER_NAME_ATTRIBUTE);
			marker = new Marker(name);
			markers.put(name, marker);
			reader.nextTag();
			for(;;)
			{
				Marker child = recursiveReadMarker(reader, markers);
				if(child != null)
				{
					markers.put(child.getName(), child);
					marker.add(child);
				}
				else
				{
					break;
				}
			}
			reader.require(XMLStreamConstants.END_ELEMENT, null, MARKER_NODE);
			reader.nextTag();
		}
		else if(XMLStreamConstants.START_ELEMENT == type && MARKER_REFERENCE_NODE.equals(reader.getLocalName()))
		{
			String ref = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, MARKER_REFERENCE_ATTRIBUTE);
			marker = markers.get(ref);
			reader.nextTag();
			reader.require(XMLStreamConstants.END_ELEMENT, null, MARKER_REFERENCE_NODE);
			reader.nextTag();
		}
		return marker;
	}

	private Map<String, String> readMdc(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && MDC_NODE.equals(reader.getLocalName()))
		{
			Map<String, String> mdc = new HashMap<>();
			reader.nextTag();
			for(;;)
			{
				StringMapEntry entry = readStringMapEntry(reader);
				if(entry == null)
				{
					break;
				}
				mdc.put(entry.key, entry.value);
			}
			reader.require(XMLStreamConstants.END_ELEMENT, null, MDC_NODE);
			reader.nextTag();
			return mdc;
		}
		return null;
	}

	@SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
	private Message[] readNdc(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && NDC_NODE.equals(reader.getLocalName()))
		{
			List<Message> ndc = new ArrayList<>();
			reader.nextTag();
			for(;;)
			{
				Message entry = readNdcEntry(reader);
				if(entry == null)
				{
					break;
				}
				ndc.add(entry);
			}
			reader.require(XMLStreamConstants.END_ELEMENT, null, NDC_NODE);
			reader.nextTag();
			return ndc.toArray(Message.ARRAY_PROTOTYPE);
		}
		return null;
	}

	private StringMapEntry readStringMapEntry(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && STRING_MAP_ENTRY_NODE.equals(reader.getLocalName()))
		{
			StringMapEntry entry = new StringMapEntry();
			entry.key = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, STRING_MAP_ENTRY_KEY_ATTRIBUTE);
			entry.value = StaxUtilities.readText(reader);
			reader.require(XMLStreamConstants.END_ELEMENT, null, STRING_MAP_ENTRY_NODE);
			reader.nextTag();
			return entry;
		}
		return null;
	}

	private Message readNdcEntry(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && NDC_ENTRY_NODE.equals(reader.getLocalName()))
		{
			reader.nextTag();

			Message entry = new Message();
			entry.setMessagePattern(StaxUtilities.readSimpleTextNodeIfAvailable(reader, null, MESSAGE_NODE));

			List<String> args = readArguments(reader);
			if(args != null)
			{
				entry.setArguments(args.toArray(EMPTY_STRING_ARRAY));
			}

			reader.require(XMLStreamConstants.END_ELEMENT, null, NDC_ENTRY_NODE);
			reader.nextTag();
			return entry;
		}
		return null;
	}

	private void readThrowable(XMLStreamReader reader, LoggingEvent event)
		throws XMLStreamException
	{
		event.setThrowable(recursiveReadThrowable(reader, THROWABLE_NODE));
	}

	private ThrowableInfo recursiveReadThrowable(XMLStreamReader reader, String nodeName)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && nodeName.equals(reader.getLocalName()))
		{
			ThrowableInfo throwable = new ThrowableInfo();
			String name = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, THROWABLE_CLASS_NAME_ATTRIBUTE);
			throwable.setName(name);
			String omittedStr = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, OMITTED_ELEMENTS_ATTRIBUTE);
			if(omittedStr != null)
			{
				try
				{
					int omitted = Integer.parseInt(omittedStr);
					throwable.setOmittedElements(omitted);
				}
				catch(NumberFormatException ex)
				{
					// ignore
				}

			}
			reader.nextTag();

			throwable.setMessage(StaxUtilities.readSimpleTextNodeIfAvailable(reader, null, THROWABLE_MESSAGE_NODE));
			throwable.setStackTrace(readStackTraceNode(reader, STACK_TRACE_NODE));

			type = reader.getEventType();
			if(XMLStreamConstants.START_ELEMENT == type && SUPPRESSED_NODE.equals(reader.getLocalName()))
			{
				reader.nextTag();
				List<ThrowableInfo> suppressedList = new ArrayList<>();
				for(;;)
				{
					ThrowableInfo current = recursiveReadThrowable(reader, THROWABLE_NODE);
					if(current == null)
					{
						break;
					}
					suppressedList.add(current);
				}
				ThrowableInfo[] suppressed=new ThrowableInfo[suppressedList.size()];

				suppressed = suppressedList.toArray(suppressed);
				throwable.setSuppressed(suppressed);
				reader.require(XMLStreamConstants.END_ELEMENT, null, SUPPRESSED_NODE);
				reader.nextTag();
			}
			throwable.setCause(recursiveReadThrowable(reader, CAUSE_NODE));
			reader.require(XMLStreamConstants.END_ELEMENT, null, nodeName);
			reader.nextTag();
			return throwable;
		}
		return null;
	}

	private List<String> readArguments(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && ARGUMENTS_NODE.equals(reader.getLocalName()))
		{
			reader.nextTag();
			List<String> args = new ArrayList<>();
			for(;;)
			{
				type = reader.getEventType();
				if(XMLStreamConstants.END_ELEMENT == type && ARGUMENTS_NODE.equals(reader.getLocalName()))
				{
					reader.nextTag();
					break;
				}
				String arg = readArgument(reader);
				args.add(arg);
			}
			return args;
		}
		return null;
	}

	private String readArgument(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && NULL_ARGUMENT_NODE.equals(reader.getLocalName()))
		{
			reader.nextTag();
			reader.require(XMLStreamConstants.END_ELEMENT, null, NULL_ARGUMENT_NODE);
			reader.nextTag();
			return null;
		}
		else
		{
			return StaxUtilities.readSimpleTextNodeIfAvailable(reader, null, ARGUMENT_NODE);
		}
	}

	private static class StringMapEntry
	{
		public String key;
		public String value;
	}
}
