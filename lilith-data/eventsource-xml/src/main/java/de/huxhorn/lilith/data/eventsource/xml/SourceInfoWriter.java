/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
 * Copyright 2007-2010 Joern Huxhorn
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
import de.huxhorn.sulky.stax.GenericStreamWriter;
import de.huxhorn.sulky.stax.StaxUtilities;

import java.util.Date;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class SourceInfoWriter
	implements GenericStreamWriter<SourceInfo>, EventSourceSchemaConstants
{
	private String prefix;
	private DateTimeFormatter dateTimeFormatter;
	private SourceIdentifierWriter sourceIdentifierWriter;
	private boolean writingSchemaLocation;
	private String preferredPrefix;

	public SourceInfoWriter()
	{
		dateTimeFormatter = new DateTimeFormatter();
		sourceIdentifierWriter = new SourceIdentifierWriter();
	}

	public String getPreferredPrefix()
	{
		return preferredPrefix;
	}

	public void setPreferredPrefix(String preferredPrefix)
	{
		this.preferredPrefix = preferredPrefix;
		sourceIdentifierWriter.setPreferredPrefix(prefix);
	}

	public boolean isWritingSchemaLocation()
	{
		return writingSchemaLocation;
	}

	public void setWritingSchemaLocation(boolean writingSchemaLocation)
	{
		this.writingSchemaLocation = writingSchemaLocation;
	}

	public void write(XMLStreamWriter writer, SourceInfo source, boolean isRoot)
		throws XMLStreamException
	{
		if(isRoot)
		{
			writer.writeStartDocument("utf-8", "1.0");
		}

		StaxUtilities.NamespaceInfo ni = StaxUtilities
			.setNamespace(writer, preferredPrefix, NAMESPACE_URI, DEFAULT_NAMESPACE_PREFIX);
		prefix = ni.getPrefix();
		StaxUtilities.writeStartElement(writer, prefix, NAMESPACE_URI, SOURCE_INFO_NODE);
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
		StaxUtilities.writeAttribute(writer, false, prefix, NAMESPACE_URI, NUMBER_OF_EVENTS_ATTRIBUTE, "" + source
			.getNumberOfEvents());

		Date ts = source.getOldestEventTimestamp();
		String dateTime = dateTimeFormatter.format(ts);
		StaxUtilities.writeAttribute(writer, false, prefix, NAMESPACE_URI, OLDEST_EVENT_TIMESTAMP_ATTRIBUTE, dateTime);

		if(source.isActive())
		{
			StaxUtilities.writeAttribute(writer, false, prefix, NAMESPACE_URI, ACTIVE_ATTRIBUTE, "true");
		}

		sourceIdentifierWriter.write(writer, source.getSource(), false);

		writer.writeEndElement();
		if(isRoot)
		{
			writer.writeEndDocument();
		}
	}
}
