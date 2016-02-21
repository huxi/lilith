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

package de.huxhorn.lilith.data.eventsource.xml;

import de.huxhorn.lilith.data.eventsource.SourceInfo;
import de.huxhorn.sulky.stax.DateTimeFormatter;
import de.huxhorn.sulky.stax.GenericStreamReader;
import de.huxhorn.sulky.stax.StaxUtilities;

import java.text.ParseException;
import java.util.Date;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class SourceInfoReader
	implements GenericStreamReader<SourceInfo>, EventSourceSchemaConstants
{
	private DateTimeFormatter dateTimeFormatter;
	private SourceIdentifierReader sourceIdentifierReader;
	private static final String TRUE = "true";
	private static final String ONE = "1";

	public SourceInfoReader()
	{
		dateTimeFormatter = new DateTimeFormatter();
		sourceIdentifierReader = new SourceIdentifierReader();
	}

	public SourceInfo read(XMLStreamReader reader)
		throws XMLStreamException
	{
		SourceInfo result = null;
		String rootNamespace = NAMESPACE_URI;
		int type = reader.getEventType();

		if(XMLStreamConstants.START_DOCUMENT == type)
		{
			reader.nextTag();
			type = reader.getEventType();
			rootNamespace = null;
		}
		if(XMLStreamConstants.START_ELEMENT == type && SOURCE_INFO_NODE.equals(reader.getLocalName()))
		{
			result = new SourceInfo();
			result
				.setNumberOfEvents(Long.parseLong(StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, NUMBER_OF_EVENTS_ATTRIBUTE)));

			String dateTime = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, OLDEST_EVENT_TIMESTAMP_ATTRIBUTE);
			if(dateTime != null && !"".equals(dateTime.trim()))
			{
				try
				{
					Date ts = dateTimeFormatter.parse(dateTime);
					result.setOldestEventTimestamp(ts);
				}
				catch(ParseException e)
				{
					e.printStackTrace();
				}
			}
			String activeStr = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, ACTIVE_ATTRIBUTE);
			boolean active = false;
			if(TRUE.equals(activeStr) || ONE.equals(activeStr))
			{
				active = true;
			}
			result.setActive(active);
			reader.nextTag();

			result.setSource(sourceIdentifierReader.read(reader));
			reader.require(XMLStreamConstants.END_ELEMENT, null, SOURCE_IDENTIFIER_NODE);
			reader.nextTag();

			reader.require(XMLStreamConstants.END_ELEMENT, null, SOURCE_INFO_NODE);
		}
		return result;
	}
}
