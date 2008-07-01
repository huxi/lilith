/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.sulky.stax.DateTimeFormatter;
import de.huxhorn.sulky.stax.GenericStreamReader;
import de.huxhorn.sulky.stax.StaxUtilities;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoggingEventReader
	implements GenericStreamReader<LoggingEvent>, LoggingEventSchemaConstants
{
	private DateTimeFormatter dateTimeFormatter;


	public LoggingEventReader()
	{
		dateTimeFormatter=new DateTimeFormatter();
	}

	public LoggingEvent read(XMLStreamReader reader) throws XMLStreamException
	{
		LoggingEvent result=null;
		String rootNamespace = NAMESPACE_URI;
		int type = reader.getEventType();

		if (XMLStreamConstants.START_DOCUMENT == type)
		{
			reader.nextTag();
			type = reader.getEventType();
			rootNamespace = null;
		}
		if (XMLStreamConstants.START_ELEMENT == type && LOGGING_EVENT_NODE.equals(reader.getLocalName()))
		{
			result = new LoggingEvent();
			result.setLogger(StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, LOGGER_ATTRIBUTE));
			result.setApplicationIdentifier(StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, APPLICATION_IDENTIFIER_ATTRIBUTE));
			result.setLevel(LoggingEvent.Level.valueOf(StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, LEVEL_ATTRIBUTE)));
			result.setThreadName(StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, THREAD_NAME_ATTRIBUTE));
			String timeStamp=StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, TIMESTAMP_ATTRIBUTE);
			try
			{
				result.setTimeStamp(dateTimeFormatter.parse(timeStamp));
			}
			catch (ParseException e)
			{
// TODO: change body of catch statement
				e.printStackTrace();
			}
			reader.nextTag();
			result.setMessage(StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, MESSAGE_NODE));
			readArguments(reader, result);
			readThrowable(reader, result);
			result.setMdc(readMdc(reader));
			readMarker(reader, result);
			readCallStack(reader, result);
			reader.require(XMLStreamConstants.END_ELEMENT, rootNamespace, LOGGING_EVENT_NODE);
		}
		return result;
	}

	private void readCallStack(XMLStreamReader reader, LoggingEvent event) throws XMLStreamException
	{
		event.setCallStack(readStackTraceNode(reader, CALLSTACK_NODE));
	}

	private StackTraceElement[] readStackTraceNode(XMLStreamReader reader, String nodeName) throws XMLStreamException
	{
		int type = reader.getEventType();
		ArrayList<StackTraceElement> ste=new ArrayList<StackTraceElement>();
		if (XMLStreamConstants.START_ELEMENT == type && nodeName.equals(reader.getLocalName()) && NAMESPACE_URI.equals(reader.getNamespaceURI()))
		{
			reader.nextTag();
			for(;;)
			{
				StackTraceElement elem=readStackTraceElement(reader);
				if(elem==null)
				{
					break;
				}
				ste.add(elem);
			}
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, nodeName);
			reader.nextTag();
			return ste.toArray(new StackTraceElement[ste.size()]);
		}
		return null;
	}

	private StackTraceElement readStackTraceElement(XMLStreamReader reader) throws XMLStreamException
	{
		int type = reader.getEventType();
		if (XMLStreamConstants.START_ELEMENT == type
				&& STACK_TRACE_ELEMENT_NODE.equals(reader.getLocalName())
				&& NAMESPACE_URI.equals(reader.getNamespaceURI()))
		{
			String className=StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, ST_CLASS_NAME_ATTRIBUTE);
			String methodName=StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, ST_METHOD_NAME_ATTRIBUTE);
			String fileName=StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, ST_FILE_NAME_ATTRIBUTE);
			reader.nextTag();
			int lineNumber=-1;
			String str = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, ST_LINE_NUMBER_NODE);
			if(str != null)
			{
				lineNumber=Integer.valueOf(str);
			}
			type = reader.getEventType();
			if (XMLStreamConstants.START_ELEMENT == type && ST_NATIVE_NODE.equals(reader.getLocalName()) && NAMESPACE_URI.equals(reader.getNamespaceURI()))
			{
				lineNumber = ThrowableInfo.MAGIC_NATIVE_LINE_NUMBER;
				reader.nextTag(); // close native
				reader.nextTag();
			}
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, STACK_TRACE_ELEMENT_NODE);
			reader.nextTag();
			return new StackTraceElement(className, methodName, fileName, lineNumber);
		}
		return null;
	}

	private void readMarker(XMLStreamReader reader, LoggingEvent event)
			throws XMLStreamException
	{
		int type = reader.getEventType();
		if (XMLStreamConstants.START_ELEMENT == type && MARKER_NODE.equals(reader.getLocalName()) && NAMESPACE_URI.equals(reader.getNamespaceURI()))
		{
			Map<String, Marker> markers=new HashMap<String, Marker>();
			Marker marker=recursiveReadMarker(reader, markers);
			event.setMarker(marker);
		}
	}

	private Marker recursiveReadMarker(XMLStreamReader reader, Map<String, Marker> markers) throws XMLStreamException
	{
		Marker marker=null;
		int type = reader.getEventType();
		if (XMLStreamConstants.START_ELEMENT == type && MARKER_NODE.equals(reader.getLocalName()) && NAMESPACE_URI.equals(reader.getNamespaceURI()))
		{
			String name=StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, MARKER_NAME_ATTRIBUTE);
			marker=new Marker(name);
			markers.put(name, marker);
			reader.nextTag();
			for(;;)
			{
				Marker child=recursiveReadMarker(reader, markers);
				if(child!=null)
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
		else if (XMLStreamConstants.START_ELEMENT == type && MARKER_REFERENCE_NODE.equals(reader.getLocalName()) && NAMESPACE_URI.equals(reader.getNamespaceURI()))
		{
			String ref=StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, MARKER_REFERENCE_ATTRIBUTE);
			marker=markers.get(ref);
			reader.nextTag();
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, MARKER_REFERENCE_NODE);
			reader.nextTag();
		}
		return marker;
	}

	private Map<String,String> readMdc(XMLStreamReader reader) throws XMLStreamException
	{
		int type = reader.getEventType();
		if (XMLStreamConstants.START_ELEMENT == type && MDC_NODE.equals(reader.getLocalName()) && NAMESPACE_URI.equals(reader.getNamespaceURI()))
		{
			Map<String,String> mdc=new HashMap<String, String>();
			reader.nextTag();
			for(;;)
			{
				MdcEntry entry=readMdcEntry(reader);
				if(entry==null)
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

	private MdcEntry readMdcEntry(XMLStreamReader reader) throws XMLStreamException
	{
		int type = reader.getEventType();
		if (XMLStreamConstants.START_ELEMENT == type && MDC_ENTRY_NODE.equals(reader.getLocalName()) && NAMESPACE_URI.equals(reader.getNamespaceURI()))
		{
			MdcEntry entry=new MdcEntry();
			entry.key=StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, MDC_ENTRY_KEY_ATTRIBUTE);
			entry.value=StaxUtilities.readText(reader);
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, MDC_ENTRY_NODE);
			reader.nextTag();
			return entry;
		}
		return null;
	}

	private void readThrowable(XMLStreamReader reader, LoggingEvent event) throws XMLStreamException
	{
		event.setThrowable(recursiveReadThrowable(reader, THROWABLE_NODE));
	}

	private ThrowableInfo recursiveReadThrowable(XMLStreamReader reader, String nodeName)
			throws XMLStreamException
	{
		int type = reader.getEventType();
		if (XMLStreamConstants.START_ELEMENT == type && nodeName.equals(reader.getLocalName()) && NAMESPACE_URI.equals(reader.getNamespaceURI()))
		{
			ThrowableInfo throwable=new ThrowableInfo();
			String name=StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, THROWABLE_CLASS_NAME_ATTRIBUTE);
			throwable.setName(name);
			reader.nextTag();

			throwable.setMessage(StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, THROWABLE_MESSAGE_NODE));
			throwable.setStackTrace(readStackTraceNode(reader, STACK_TRACE_NODE));

			throwable.setCause(recursiveReadThrowable(reader, CAUSE_NODE));
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, nodeName);
			reader.nextTag();
			return throwable;
		}
		return null;
	}
	private void readArguments(XMLStreamReader reader, LoggingEvent event) throws XMLStreamException
	{
		int type = reader.getEventType();
		if (XMLStreamConstants.START_ELEMENT == type && ARGUMENTS_NODE.equals(reader.getLocalName()) && NAMESPACE_URI.equals(reader.getNamespaceURI()))
		{
			reader.nextTag();
			List<String> args=new ArrayList<String>();
			for(;;)
			{
				type = reader.getEventType();
				if (XMLStreamConstants.END_ELEMENT == type && ARGUMENTS_NODE.equals(reader.getLocalName()) && NAMESPACE_URI.equals(reader.getNamespaceURI()))
				{
					reader.nextTag();
					break;
				}
				String arg=readArgument(reader);
				args.add(arg);
			}
			event.setArguments(args.toArray(new String[args.size()]));
		}
	}

	private String readArgument(XMLStreamReader reader) throws XMLStreamException
	{
		int type = reader.getEventType();
		if (XMLStreamConstants.START_ELEMENT == type && NULL_ARGUMENT_NODE.equals(reader.getLocalName()) && NAMESPACE_URI.equals(reader.getNamespaceURI()))
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

	private static class MdcEntry
	{
		public String key;
		public String value;
	}
}
