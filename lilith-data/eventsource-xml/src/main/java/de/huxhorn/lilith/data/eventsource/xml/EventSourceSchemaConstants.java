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
package de.huxhorn.lilith.data.eventsource.xml;

public interface EventSourceSchemaConstants
{
	String NAMESPACE_URI = "http://lilith.sf.net/schema/eventsource/10";
	String NAMESPACE_LOCATION = "http://lilith.sf.net/schema/eventsource/10/EventSource.xsd";

	String DEFAULT_NAMESPACE_PREFIX = "es";

	String SOURCE_IDENTIFIER_NODE = "SourceIdentifier";
	String IDENTIFIER_ATTRIBUTE = "identifier";
	String SECONDARY_IDENTIFIER_ATTRIBUTE = "secondaryIdentifier";

	String SOURCE_INFO_NODE = "EventSourceInfo";
	String NUMBER_OF_EVENTS_ATTRIBUTE = "numberOfEvents";
	String OLDEST_EVENT_TIMESTAMP_ATTRIBUTE = "oldestEventTimestamp";
	String ACTIVE_ATTRIBUTE = "active";
}
