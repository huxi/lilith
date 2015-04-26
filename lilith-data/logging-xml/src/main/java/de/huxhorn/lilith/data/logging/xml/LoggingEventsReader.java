/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
 * Copyright 2007-2011 Joern Huxhorn
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

import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.eventsource.xml.EventSourceSchemaConstants;
import de.huxhorn.lilith.data.eventsource.xml.SourceIdentifierReader;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.LoggingEvents;
import de.huxhorn.sulky.stax.GenericStreamReader;
import de.huxhorn.sulky.stax.StaxUtilities;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class LoggingEventsReader
	implements GenericStreamReader<LoggingEvents>, LoggingEventSchemaConstants
{
	private SourceIdentifierReader sourceIdentifierReader;
	private LoggingEventReader loggingEventReader;

	public LoggingEventsReader()
	{
		sourceIdentifierReader = new SourceIdentifierReader();
		loggingEventReader = new LoggingEventReader();
	}

	public LoggingEvents read(XMLStreamReader reader)
		throws XMLStreamException
	{
		LoggingEvents result = null;
		String rootNamespace = NAMESPACE_URI;
		int type = reader.getEventType();

		if(XMLStreamConstants.START_DOCUMENT == type)
		{
			reader.nextTag();
			type = reader.getEventType();
			rootNamespace = null;
		}
		if(XMLStreamConstants.START_ELEMENT == type && LOGGING_EVENTS_NODE.equals(reader.getLocalName()))
		{
			result = new LoggingEvents();
			String idxStr = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, START_INDEX_ATTRIBUTE);
			long idx = 0;
			if(idxStr != null)
			{
				idx = Long.parseLong(idxStr);
			}
			result.setStartIndex(idx);
			reader.nextTag();
			SourceIdentifier sourceId = sourceIdentifierReader.read(reader);
			if(sourceId != null)
			{
				result.setSource(sourceId);
				reader.require(XMLStreamConstants.END_ELEMENT, null, EventSourceSchemaConstants.SOURCE_IDENTIFIER_NODE);
				reader.nextTag();
			}

			List<LoggingEvent> events = null;
			for(; ;)
			{
				LoggingEvent event = loggingEventReader.read(reader);
				if(event == null)
				{
					break;
				}
				reader.require(XMLStreamConstants.END_ELEMENT, null, LOGGING_EVENT_NODE);
				reader.nextTag();
				if(events == null)
				{
					events = new ArrayList<>();
				}
				events.add(event);
			}
			result.setEvents(events);
			reader.require(XMLStreamConstants.END_ELEMENT, null, LOGGING_EVENTS_NODE);
		}
		return result;
	}
}
