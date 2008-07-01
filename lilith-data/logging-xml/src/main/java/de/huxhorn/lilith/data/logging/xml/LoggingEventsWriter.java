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

import de.huxhorn.lilith.data.eventsource.xml.SourceIdentifierWriter;
import de.huxhorn.lilith.data.eventsource.xml.EventSourceSchemaConstants;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.LoggingEvents;
import de.huxhorn.sulky.stax.GenericStreamWriter;
import de.huxhorn.sulky.stax.StaxUtilities;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

public class LoggingEventsWriter
	implements GenericStreamWriter<LoggingEvents>, LoggingEventSchemaConstants
{
	private String prefix;
	private boolean writingSchemaLocation;
	private LoggingEventWriter loggingEventWriter;
	private SourceIdentifierWriter sourceIdentifierWriter;
	private String preferredPrefix;

	public LoggingEventsWriter()
	{
		sourceIdentifierWriter =new SourceIdentifierWriter();
		sourceIdentifierWriter.setPreferredPrefix(EventSourceSchemaConstants.DEFAULT_NAMESPACE_PREFIX);
		loggingEventWriter=new LoggingEventWriter();
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

	public void setPreferredPrefix(String preferredPrefix)
	{
		this.preferredPrefix = preferredPrefix;
		loggingEventWriter.setPreferredPrefix(prefix);
	}

	public void setEventSourcePrefix(String prefix)
	{
		sourceIdentifierWriter.setPreferredPrefix(prefix);
	}

	public String getEventSourcePrefix()
	{
		return sourceIdentifierWriter.getPreferredPrefix();
	}

	public void write(XMLStreamWriter writer, LoggingEvents events, boolean isRoot)
			throws XMLStreamException
	{
		if(isRoot)
		{
			writer.writeStartDocument("utf-8","1.0");
		}
		StaxUtilities.NamespaceInfo ni = StaxUtilities.setNamespace(writer, preferredPrefix, NAMESPACE_URI, DEFAULT_NAMESPACE_PREFIX);
		prefix=ni.getPrefix();

		//StaxUtilities.writeStartElement(writer, null, null, LOGGING_EVENTS_NODE);
		StaxUtilities.writeStartElement(writer, prefix, NAMESPACE_URI, LOGGING_EVENTS_NODE);
		if(ni.isCreated())
		{
			StaxUtilities.writeNamespace(writer, prefix, NAMESPACE_URI);
		}
		/*
			String eventSourcePrefix=sourceIdentifierWriter.getPrefix();
			if(eventSourcePrefix==null)
			{
				writer.writeDefaultNamespace(EventSourceSchemaConstants.NAMESPACE_URI);
			}
			else
			{
				writer.writeNamespace(eventSourcePrefix, EventSourceSchemaConstants.NAMESPACE_URI);
			}
        */
		if(isRoot && writingSchemaLocation)
		{
			ni = StaxUtilities.setNamespace(writer,
					StaxUtilities.XML_SCHEMA_INSTANCE_PREFIX,
					StaxUtilities.XML_SCHEMA_INSTANCE_NAMESPACE_URI,
					StaxUtilities.XML_SCHEMA_INSTANCE_PREFIX);
			if(ni.isCreated())
			{
				writer.writeNamespace(ni.getPrefix(), StaxUtilities.XML_SCHEMA_INSTANCE_NAMESPACE_URI);
			}
			StaxUtilities.writeAttribute(writer,
					true,
					StaxUtilities.XML_SCHEMA_INSTANCE_PREFIX,
					StaxUtilities.XML_SCHEMA_INSTANCE_NAMESPACE_URI,
					StaxUtilities.XML_SCHEMA_INSTANCE_SCHEMA_LOCATION_ATTRIBUTE,
					NAMESPACE_URI+" "+NAMESPACE_LOCATION
					+" "+ EventSourceSchemaConstants.NAMESPACE_URI+" "+EventSourceSchemaConstants.NAMESPACE_LOCATION);
		}
		long idx=events.getStartIndex();
		if(idx>0)
		{
			StaxUtilities.writeAttribute(writer, false, prefix, NAMESPACE_URI, START_INDEX_ATTRIBUTE, ""+idx);
		}
		sourceIdentifierWriter.write(writer, events.getSource(), false);

		List<LoggingEvent> eventList = events.getEvents();
		if(eventList!=null)
		{
			for(LoggingEvent event: eventList)
			{
				loggingEventWriter.write(writer, event, false);
			}
		}

		writer.writeEndElement();
		if(isRoot)
		{
			writer.writeEndDocument();
		}
	}
}
