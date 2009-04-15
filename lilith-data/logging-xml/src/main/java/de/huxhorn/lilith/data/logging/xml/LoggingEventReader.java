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
package de.huxhorn.lilith.data.logging.xml;

import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggerContext;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class LoggingEventReader
	implements GenericStreamReader<LoggingEvent>, LoggingEventSchemaConstants
{
	private DateTimeFormatter dateTimeFormatter;
	private StackTraceElementReader steReader;

	public LoggingEventReader()
	{
		dateTimeFormatter = new DateTimeFormatter();
		steReader = new StackTraceElementReader();
	}

	public LoggingEvent read(XMLStreamReader reader)
		throws XMLStreamException
	{
		LoggingEvent result = null;
		String rootNamespace = NAMESPACE_URI;
		int type = reader.getEventType();

		if(XMLStreamConstants.START_DOCUMENT == type)
		{
			reader.nextTag();
			type = reader.getEventType();
			rootNamespace = null;
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
				if(threadName != null || threadId != null || threadGroupName != null || threadGroupId != null)
				{
					result.setThreadInfo(new ThreadInfo(threadId, threadName, threadGroupId, threadGroupName));
				}
			}

			result.setTimeStamp(readTimeStamp(reader, TIMESTAMP_MILLIS_ATTRIBUTE, TIMESTAMP_ATTRIBUTE));

			reader.nextTag();
			Message message = null;
			{
				String messagePattern = StaxUtilities
					.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, MESSAGE_NODE);
				List<String> args = readArguments(reader);
				if(messagePattern != null || args != null)
				{
					if(args != null)
					{
						message = new Message(messagePattern, args.toArray(new String[args.size()]));
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
			reader.require(XMLStreamConstants.END_ELEMENT, rootNamespace, LOGGING_EVENT_NODE);
		}
		return result;
	}

	private Date readTimeStamp(XMLStreamReader reader, String millisName, String formattedName)
	{
		Date timeStamp = null;

		String timeStampMillis = StaxUtilities
			.readAttributeValue(reader, NAMESPACE_URI, millisName);
		if(timeStampMillis != null)
		{
			try
			{
				timeStamp = new Date(Long.parseLong(timeStampMillis));
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
					timeStamp = dateTimeFormatter.parse(timeStampStr);
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
		if(XMLStreamConstants.START_ELEMENT == type && LOGGER_CONTEXT_NODE
			.equals(reader.getLocalName()) && NAMESPACE_URI
			.equals(reader.getNamespaceURI()))
		{
			result = new LoggerContext();

			result.setName(StaxUtilities
				.readAttributeValue(reader, NAMESPACE_URI, LOGGER_CONTEXT_NAME_ATTRIBUTE));

			result
				.setBirthTime(readTimeStamp(reader, LOGGER_CONTEXT_BIRTH_TIME_MILLIS_ATTRIBUTE, LOGGER_CONTEXT_BIRTH_TIME_ATTRIBUTE));
			reader.nextTag();
			result.setProperties(readLoggerContextProperties(reader));
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, LOGGER_CONTEXT_NODE);
			reader.nextTag();
		}
		return result;
	}

	private Map<String, String> readLoggerContextProperties(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && LOGGER_CONTEXT_PROPERTIES_NODE
			.equals(reader.getLocalName()) && NAMESPACE_URI
			.equals(reader.getNamespaceURI()))
		{
			Map<String, String> map = new HashMap<String, String>();
			reader.nextTag();
			for(; ;)
			{
				StringMapEntry entry = readStringMapEntry(reader);
				if(entry == null)
				{
					break;
				}
				map.put(entry.key, entry.value);
			}
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, LOGGER_CONTEXT_PROPERTIES_NODE);
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

	private ExtendedStackTraceElement[] readStackTraceNode(XMLStreamReader reader, String nodeName)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		ArrayList<ExtendedStackTraceElement> ste = new ArrayList<ExtendedStackTraceElement>();
		if(XMLStreamConstants.START_ELEMENT == type && nodeName.equals(reader.getLocalName()) && NAMESPACE_URI
			.equals(reader.getNamespaceURI()))
		{
			reader.nextTag();
			for(; ;)
			{
				ExtendedStackTraceElement elem = steReader.read(reader);//readStackTraceElement(reader);
				if(elem == null)
				{
					break;
				}
				reader.nextTag();
				ste.add(elem);
			}
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, nodeName);
			reader.nextTag();
			return ste.toArray(new ExtendedStackTraceElement[ste.size()]);
		}
		return null;
	}

	private void readMarker(XMLStreamReader reader, LoggingEvent event)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && MARKER_NODE.equals(reader.getLocalName()) && NAMESPACE_URI
			.equals(reader.getNamespaceURI()))
		{
			Map<String, Marker> markers = new HashMap<String, Marker>();
			Marker marker = recursiveReadMarker(reader, markers);
			event.setMarker(marker);
		}
	}

	private Marker recursiveReadMarker(XMLStreamReader reader, Map<String, Marker> markers)
		throws XMLStreamException
	{
		Marker marker = null;
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && MARKER_NODE.equals(reader.getLocalName()) && NAMESPACE_URI
			.equals(reader.getNamespaceURI()))
		{
			String name = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, MARKER_NAME_ATTRIBUTE);
			marker = new Marker(name);
			markers.put(name, marker);
			reader.nextTag();
			for(; ;)
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
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, MARKER_NODE);
			reader.nextTag();
		}
		else if(XMLStreamConstants.START_ELEMENT == type && MARKER_REFERENCE_NODE
			.equals(reader.getLocalName()) && NAMESPACE_URI.equals(reader.getNamespaceURI()))
		{
			String ref = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, MARKER_REFERENCE_ATTRIBUTE);
			marker = markers.get(ref);
			reader.nextTag();
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, MARKER_REFERENCE_NODE);
			reader.nextTag();
		}
		return marker;
	}

	private Map<String, String> readMdc(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && MDC_NODE.equals(reader.getLocalName()) && NAMESPACE_URI
			.equals(reader.getNamespaceURI()))
		{
			Map<String, String> mdc = new HashMap<String, String>();
			reader.nextTag();
			for(; ;)
			{
				StringMapEntry entry = readStringMapEntry(reader);
				if(entry == null)
				{
					break;
				}
				mdc.put(entry.key, entry.value);
			}
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, MDC_NODE);
			reader.nextTag();
			return mdc;
		}
		return null;
	}

	private Message[] readNdc(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && NDC_NODE.equals(reader.getLocalName()) && NAMESPACE_URI
			.equals(reader.getNamespaceURI()))
		{
			List<Message> ndc = new ArrayList<Message>();
			reader.nextTag();
			for(; ;)
			{
				Message entry = readNdcEntry(reader);
				if(entry == null)
				{
					break;
				}
				ndc.add(entry);
			}
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, NDC_NODE);
			reader.nextTag();
			return ndc.toArray(new Message[ndc.size()]);
		}
		return null;
	}

	private StringMapEntry readStringMapEntry(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && STRING_MAP_ENTRY_NODE
			.equals(reader.getLocalName()) && NAMESPACE_URI
			.equals(reader.getNamespaceURI()))
		{
			StringMapEntry entry = new StringMapEntry();
			entry.key = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, STRING_MAP_ENTRY_KEY_ATTRIBUTE);
			entry.value = StaxUtilities.readText(reader);
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, STRING_MAP_ENTRY_NODE);
			reader.nextTag();
			return entry;
		}
		return null;
	}

	private Message readNdcEntry(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && NDC_ENTRY_NODE.equals(reader.getLocalName()) && NAMESPACE_URI
			.equals(reader.getNamespaceURI()))
		{
			reader.nextTag();

			Message entry = new Message();
			entry.setMessagePattern(StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, MESSAGE_NODE));

			List<String> args = readArguments(reader);
			if(args != null)
			{
				entry.setArguments(args.toArray(new String[args.size()]));
			}

			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, NDC_ENTRY_NODE);
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
		if(XMLStreamConstants.START_ELEMENT == type && nodeName.equals(reader.getLocalName()) && NAMESPACE_URI
			.equals(reader.getNamespaceURI()))
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

			throwable
				.setMessage(StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, THROWABLE_MESSAGE_NODE));
			throwable.setStackTrace(readStackTraceNode(reader, STACK_TRACE_NODE));

			throwable.setCause(recursiveReadThrowable(reader, CAUSE_NODE));
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, nodeName);
			reader.nextTag();
			return throwable;
		}
		return null;
	}

	private List<String> readArguments(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && ARGUMENTS_NODE.equals(reader.getLocalName()) && NAMESPACE_URI
			.equals(reader.getNamespaceURI()))
		{
			reader.nextTag();
			List<String> args = new ArrayList<String>();
			for(; ;)
			{
				type = reader.getEventType();
				if(XMLStreamConstants.END_ELEMENT == type && ARGUMENTS_NODE
					.equals(reader.getLocalName()) && NAMESPACE_URI.equals(reader.getNamespaceURI()))
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
		if(XMLStreamConstants.START_ELEMENT == type && NULL_ARGUMENT_NODE.equals(reader.getLocalName()) && NAMESPACE_URI
			.equals(reader.getNamespaceURI()))
		{
			reader.nextTag();
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, NULL_ARGUMENT_NODE);
			reader.nextTag();
			return null;
		}
		else
		{
			return StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, ARGUMENT_NODE);
		}
	}

	private static class StringMapEntry
	{
		public String key;
		public String value;
	}
}
