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
package de.huxhorn.lilith.jul.xml;

public interface LoggingEventSchemaConstants
{
	String DEFAULT_NAMESPACE_PREFIX = null;
	String NAMESPACE_URI = null;


	String LOG_NODE = "log";

	String RECORD_NODE = "record";
	String DATE_NODE = "date";
	String MILLIS_NODE = "millis";
	String SEQUENCE_NODE = "sequence";
	String LOGGER_NODE = "logger";
	String LEVEL_NODE = "level";
	String CLASS_NODE = "class";
	String METHOD_NODE = "method";
	String THREAD_NODE = "thread";
	String MESSAGE_NODE = "message";

	String EXCEPTION_NODE = "exception";
	String FRAME_NODE = "frame";
	String LINE_NODE = "line";


}
