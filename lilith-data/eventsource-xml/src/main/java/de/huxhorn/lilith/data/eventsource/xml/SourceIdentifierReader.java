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
package de.huxhorn.lilith.data.eventsource.xml;

import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.sulky.stax.GenericStreamReader;
import de.huxhorn.sulky.stax.StaxUtilities;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class SourceIdentifierReader
	implements GenericStreamReader<SourceIdentifier>, EventSourceSchemaConstants
{
	public SourceIdentifier read(XMLStreamReader reader) throws XMLStreamException
	{
		SourceIdentifier result=null;
		String rootNamespace = NAMESPACE_URI;
		int type = reader.getEventType();

		if (XMLStreamConstants.START_DOCUMENT == type)
		{
			reader.nextTag();
			type = reader.getEventType();
			rootNamespace = null;
		}
		if (XMLStreamConstants.START_ELEMENT == type && SOURCE_IDENTIFIER_NODE.equals(reader.getLocalName()))
		{
			result = new SourceIdentifier();
			result.setIdentifier(StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, IDENTIFIER_ATTRIBUTE));
			result.setSecondaryIdentifier(StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, SECONDARY_IDENTIFIER_ATTRIBUTE));
			reader.nextTag();
			reader.require(XMLStreamConstants.END_ELEMENT, rootNamespace, SOURCE_IDENTIFIER_NODE);
		}
		return result;
	}
}
