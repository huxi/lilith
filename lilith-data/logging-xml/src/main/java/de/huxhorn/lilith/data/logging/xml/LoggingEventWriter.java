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
import de.huxhorn.sulky.stax.GenericStreamWriter;
import de.huxhorn.sulky.stax.StaxUtilities;
import de.huxhorn.sulky.stax.WhiteSpaceHandling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class LoggingEventWriter
	implements GenericStreamWriter<LoggingEvent>, LoggingEventSchemaConstants
{
	private String preferredPrefix;
	private String prefix;
	private boolean sortingMdcValues;
	private DateTimeFormatter dateTimeFormatter;
	private boolean writingSchemaLocation;
	private TimeStampType timeStampType;

	public enum TimeStampType
	{
		ONLY_TIMESTAMP,
		ONLY_MILLIS,
		BOTH
	}


	public LoggingEventWriter()
	{
		dateTimeFormatter = new DateTimeFormatter();
		timeStampType = TimeStampType.BOTH;
	}

	public TimeStampType getTimeStampType()
	{
		return timeStampType;
	}

	public void setTimeStampType(TimeStampType timeStampType)
	{
		this.timeStampType = timeStampType;
	}

	public boolean isSortingMdcValues()
	{
		return sortingMdcValues;
	}

	public void setSortingMdcValues(boolean sortingMdcValues)
	{
		this.sortingMdcValues = sortingMdcValues;
	}

	public boolean isWritingSchemaLocation()
	{
		return writingSchemaLocation;
	}

	public void setWritingSchemaLocation(boolean writingSchemaLocation)
	{
		this.writingSchemaLocation = writingSchemaLocation;
	}

	public String getPreferredPrefix()
	{
		return preferredPrefix;
	}

	public void setPreferredPrefix(String prefix)
	{
		this.preferredPrefix = prefix;
	}

	public void write(XMLStreamWriter writer, LoggingEvent event, boolean isRoot)
		throws XMLStreamException
	{
		if(isRoot)
		{
			writer.writeStartDocument("utf-8", "1.0");
		}

		StaxUtilities.NamespaceInfo ni = StaxUtilities
			.setNamespace(writer, preferredPrefix, NAMESPACE_URI, DEFAULT_NAMESPACE_PREFIX);
		prefix = ni.getPrefix();
		StaxUtilities.writeStartElement(writer, prefix, NAMESPACE_URI, LOGGING_EVENT_NODE);
		if(ni.isCreated())
		{
			StaxUtilities.writeNamespace(writer, prefix, NAMESPACE_URI);
		}

		if(isRoot && writingSchemaLocation)
		{
			ni = StaxUtilities
				.setNamespace(writer, StaxUtilities.XML_SCHEMA_INSTANCE_PREFIX, StaxUtilities.XML_SCHEMA_INSTANCE_NAMESPACE_URI, StaxUtilities.XML_SCHEMA_INSTANCE_PREFIX);
			if(ni.isCreated())
			{
				StaxUtilities.writeNamespace(writer, ni.getPrefix(), StaxUtilities.XML_SCHEMA_INSTANCE_NAMESPACE_URI);
			}
			StaxUtilities.writeAttribute(writer,
				true,
				ni.getPrefix(),
				StaxUtilities.XML_SCHEMA_INSTANCE_NAMESPACE_URI,
				StaxUtilities.XML_SCHEMA_INSTANCE_SCHEMA_LOCATION_ATTRIBUTE,
				NAMESPACE_URI + " " + NAMESPACE_LOCATION);
		}
		StaxUtilities.writeAttribute(writer, false, prefix, NAMESPACE_URI, LOGGER_ATTRIBUTE, event.getLogger());
		StaxUtilities.writeAttribute(writer, false, prefix, NAMESPACE_URI, LEVEL_ATTRIBUTE, "" + event.getLevel());
		Long sequence = event.getSequenceNumber();
		if(sequence != null)
		{
			StaxUtilities.writeAttribute(writer, false, prefix, NAMESPACE_URI, SEQUENCE_ATTRIBUTE, sequence.toString());
		}
		ThreadInfo threadInfo = event.getThreadInfo();
		if(threadInfo != null)
		{
			Long id = threadInfo.getId();
			String name = threadInfo.getName();
			Long groupId = threadInfo.getGroupId();
			String groupName = threadInfo.getGroupName();
			if(name != null)
			{
				StaxUtilities
					.writeAttributeIfNotNull(writer, false, prefix, NAMESPACE_URI, THREAD_NAME_ATTRIBUTE, name);
			}
			if(id != null)
			{
				StaxUtilities
					.writeAttributeIfNotNull(writer, false, prefix, NAMESPACE_URI, THREAD_ID_ATTRIBUTE, "" + id);
			}
			if(groupName != null)
			{
				StaxUtilities
					.writeAttributeIfNotNull(writer, false, prefix, NAMESPACE_URI, THREAD_GROUP_NAME_ATTRIBUTE, groupName);
			}
			if(groupId != null)
			{
				StaxUtilities
					.writeAttributeIfNotNull(writer, false, prefix, NAMESPACE_URI, THREAD_GROUP_ID_ATTRIBUTE, "" + groupId);
			}
		}

		Long timeStamp = event.getTimeStamp();
		if(timeStamp != null)
		{
			if(timeStampType == TimeStampType.ONLY_TIMESTAMP || timeStampType == TimeStampType.BOTH)
			{
				StaxUtilities
					.writeAttribute(writer, false, prefix, NAMESPACE_URI, TIMESTAMP_ATTRIBUTE, dateTimeFormatter.format(new Date(timeStamp)));
			}
			if(timeStampType == TimeStampType.ONLY_MILLIS || timeStampType == TimeStampType.BOTH)
			{
				StaxUtilities
					.writeAttribute(writer, false, prefix, NAMESPACE_URI, TIMESTAMP_MILLIS_ATTRIBUTE, "" + timeStamp);
			}
		}
		Message message = event.getMessage();
		if(message != null)
		{
			String messagePattern = message.getMessagePattern();
			if(messagePattern != null)
			{
				StaxUtilities.writeSimpleTextNode(writer, prefix, NAMESPACE_URI, MESSAGE_NODE, messagePattern);
			}
			writeArguments(writer, message.getArguments());
		}
		writeThrowable(writer, event);
		writeStringMap(writer, event.getMdc(), MDC_NODE);
		writeNdc(writer, event);
		writeMarker(writer, event);
		writeCallStack(writer, event);
		writeLoggerContext(writer, event);
		writer.writeEndElement();
		if(isRoot)
		{
			writer.writeEndDocument();
		}
	}

	private void writeLoggerContext(XMLStreamWriter writer, LoggingEvent event)
		throws XMLStreamException
	{
		LoggerContext context = event.getLoggerContext();
		if(context == null)
		{
			return;
		}
		StaxUtilities.writeStartElement(writer, prefix, NAMESPACE_URI, LOGGER_CONTEXT_NODE);
		String name = context.getName();
		if(name != null)
		{
			StaxUtilities.writeAttribute(writer, false, prefix, NAMESPACE_URI, LOGGER_CONTEXT_NAME_ATTRIBUTE, name);
		}
		Long timeStamp = context.getBirthTime();
		if(timeStamp != null)
		{
			if(timeStampType == TimeStampType.ONLY_TIMESTAMP || timeStampType == TimeStampType.BOTH)
			{
				StaxUtilities
					.writeAttribute(writer, false, prefix, NAMESPACE_URI, LOGGER_CONTEXT_BIRTH_TIME_ATTRIBUTE, dateTimeFormatter.format(new Date(timeStamp)));
			}
			if(timeStampType == TimeStampType.ONLY_MILLIS || timeStampType == TimeStampType.BOTH)
			{
				StaxUtilities
					.writeAttribute(writer, false, prefix, NAMESPACE_URI, LOGGER_CONTEXT_BIRTH_TIME_MILLIS_ATTRIBUTE, "" + timeStamp);
			}
		}
		writeStringMap(writer, context.getProperties(), LOGGER_CONTEXT_PROPERTIES_NODE);
		writer.writeEndElement();
	}

	private void writeCallStack(XMLStreamWriter writer, LoggingEvent event)
		throws XMLStreamException
	{
		writeStackTraceNode(writer, event.getCallStack(), CALLSTACK_NODE);
	}

	private void writeMarker(XMLStreamWriter writer, LoggingEvent event)
		throws XMLStreamException
	{
		Marker marker = event.getMarker();
		if(marker != null)
		{
			List<String> handledMarkers = new ArrayList<String>();
			writeMarkerNode(writer, marker, handledMarkers);
		}
	}

	private void writeMarkerNode(XMLStreamWriter writer, Marker marker, List<String> handledMarkers)
		throws XMLStreamException
	{
		String markerName = marker.getName();
		if(handledMarkers.contains(markerName))
		{
			StaxUtilities.writeEmptyElement(writer, prefix, NAMESPACE_URI, MARKER_REFERENCE_NODE);
			StaxUtilities.writeAttribute(writer, false, prefix, NAMESPACE_URI, MARKER_REFERENCE_ATTRIBUTE, markerName);
		}
		else
		{
			handledMarkers.add(markerName);
			boolean hasChildren = marker.hasReferences();
			if(hasChildren)
			{
				StaxUtilities.writeStartElement(writer, prefix, NAMESPACE_URI, MARKER_NODE);
			}
			else
			{
				StaxUtilities.writeEmptyElement(writer, prefix, NAMESPACE_URI, MARKER_NODE);
			}
			StaxUtilities.writeAttribute(writer, false, prefix, NAMESPACE_URI, MARKER_NAME_ATTRIBUTE, markerName);
			if(hasChildren)
			{
				Map<String, Marker> children = marker.getReferences();
				for(Map.Entry<String, Marker> current : children.entrySet())
				{
					writeMarkerNode(writer, current.getValue(), handledMarkers);
				}
				writer.writeEndElement();
			}
		}

	}

	private void writeStringMap(XMLStreamWriter writer, Map<String, String> map, String nodeName)
		throws XMLStreamException
	{
		if(map != null)
		{
			if(sortingMdcValues)
			{
				map = new TreeMap<String, String>(map);
			}

			StaxUtilities.writeStartElement(writer, prefix, NAMESPACE_URI, nodeName);
			for(Map.Entry<String, String> entry : map.entrySet())
			{

				StaxUtilities.writeStartElement(writer, prefix, NAMESPACE_URI, STRING_MAP_ENTRY_NODE);
				StaxUtilities
					.writeAttribute(writer, false, prefix, NAMESPACE_URI, STRING_MAP_ENTRY_KEY_ATTRIBUTE, entry.getKey(), WhiteSpaceHandling.PRESERVE_NORMALIZE_NEWLINE);
				StaxUtilities.writeText(writer, entry.getValue());
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}
	}

	private void writeNdc(XMLStreamWriter writer, LoggingEvent event)
		throws XMLStreamException
	{
		Message[] ndc = event.getNdc();
		if(ndc != null)
		{
			StaxUtilities.writeStartElement(writer, prefix, NAMESPACE_URI, NDC_NODE);
			for(Message entry : ndc)
			{

				StaxUtilities.writeStartElement(writer, prefix, NAMESPACE_URI, NDC_ENTRY_NODE);
				StaxUtilities
					.writeSimpleTextNode(writer, prefix, NAMESPACE_URI, MESSAGE_NODE, entry.getMessagePattern());
				writeArguments(writer, entry.getArguments());
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}
	}

	private void writeArguments(XMLStreamWriter writer, String[] arguments)
		throws XMLStreamException
	{
		if(arguments != null)
		{
			StaxUtilities.writeStartElement(writer, prefix, NAMESPACE_URI, ARGUMENTS_NODE);
			for(String current : arguments)
			{
				if(current == null)
				{
					StaxUtilities.writeEmptyElement(writer, prefix, NAMESPACE_URI, NULL_ARGUMENT_NODE);
				}
				else
				{
					StaxUtilities.writeSimpleTextNode(writer, prefix, NAMESPACE_URI, ARGUMENT_NODE, current);
				}
			}
			writer.writeEndElement();
		}
	}

	private void writeThrowable(XMLStreamWriter writer, LoggingEvent event)
		throws XMLStreamException
	{
		ThrowableInfo throwable = event.getThrowable();
		writeThrowableNode(writer, throwable, THROWABLE_NODE);
	}

	private void writeThrowableNode(XMLStreamWriter writer, ThrowableInfo throwable, String nodeName)
		throws XMLStreamException
	{
		if(throwable != null)
		{
			StaxUtilities.writeStartElement(writer, prefix, NAMESPACE_URI, nodeName);
			StaxUtilities
				.writeAttribute(writer, false, prefix, NAMESPACE_URI, THROWABLE_CLASS_NAME_ATTRIBUTE, throwable.getName());
			int omitted = throwable.getOmittedElements();
			if(omitted != 0)
			{
				StaxUtilities
					.writeAttribute(writer, false, prefix, NAMESPACE_URI, OMITTED_ELEMENTS_ATTRIBUTE, "" + throwable
						.getOmittedElements());
			}
			// TODO: can message be null?
			StaxUtilities
				.writeSimpleTextNode(writer, prefix, NAMESPACE_URI, THROWABLE_MESSAGE_NODE, throwable.getMessage());
			writeStackTraceNode(writer, throwable.getStackTrace(), STACK_TRACE_NODE);
			writeThrowableNode(writer, throwable.getCause(), CAUSE_NODE);
			writer.writeEndElement();
		}
	}

	private void writeStackTraceNode(XMLStreamWriter writer, ExtendedStackTraceElement[] ste, String nodeName)
		throws XMLStreamException
	{
		if(ste != null)
		{
			StaxUtilities.writeStartElement(writer, prefix, NAMESPACE_URI, nodeName);
			StackTraceElementWriter steWriter = new StackTraceElementWriter();
			steWriter.setPreferredPrefix(prefix);
			for(ExtendedStackTraceElement elem : ste)
			{
				steWriter.write(writer, elem, false);
			}
			writer.writeEndElement();
		}
	}
}
