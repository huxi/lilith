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

package de.huxhorn.lilith.jul.xml;

public interface LoggingEventSchemaConstants
{
	/*
	From http://java.sun.com/j2se/1.4.2/docs/guide/util/logging/overview.html#3.0

	<!-- DTD used by the java.util.logging.XMLFormatter -->
	<!-- This provides an XML formatted log message. -->

	<!-- The document type is "log" which consists of a sequence
	of record elements -->
	<!ELEMENT log (record*)>

	<!-- Each logging call is described by a record element. -->
	<!ELEMENT record (date, millis, sequence, logger?, level,
	class?, method?, thread?, message, key?, catalog?, param*, exception?)>

	<!-- Date and time when LogRecord was created in ISO 8601 format -->
	<!ELEMENT date (#PCDATA)>

	<!-- Time when LogRecord was created in milliseconds since
	midnight January 1st, 1970, UTC. -->
	<!ELEMENT millis (#PCDATA)>

	<!-- Unique sequence number within source VM. -->
	<!ELEMENT sequence (#PCDATA)>

	<!-- Name of source Logger object. -->
	<!ELEMENT logger (#PCDATA)>

	<!-- Logging level, may be either one of the constant
	names from java.util.logging.Constants (such as "SEVERE"
	or "WARNING") or an integer value such as "20". -->
	<!ELEMENT level (#PCDATA)>

	<!-- Fully qualified name of class that issued
	logging call, e.g. "javax.marsupial.Wombat". -->
	<!ELEMENT class (#PCDATA)>

	<!-- Name of method that issued logging call.
	It may be either an unqualified method name such as
	"fred" or it may include argument type information
	in parenthesis, for example "fred(int,String)". -->
	<!ELEMENT method (#PCDATA)>

	<!-- Integer thread ID. -->
	<!ELEMENT thread (#PCDATA)>

	<!-- The message element contains the text string of a log message. -->
	<!ELEMENT message (#PCDATA)>

	<!-- If the message string was localized, the key element provides
	the original localization message key. -->
	<!ELEMENT key (#PCDATA)>

	<!-- If the message string was localized, the catalog element provides
	the logger's localization resource bundle name. -->
	<!ELEMENT catalog (#PCDATA)>

	<!-- If the message string was localized, each of the param elements
	provides the String value (obtained using Object.toString())
	of the corresponding LogRecord parameter. -->
	<!ELEMENT param (#PCDATA)>

	<!-- An exception consists of an optional message string followed
	by a series of StackFrames. Exception elements are used
	for Java exceptions and other java Throwables. -->
	<!ELEMENT exception (message?, frame+)>

	<!-- A frame describes one line in a Throwable backtrace. -->
	<!ELEMENT frame (class, method, line?)>

	<!-- an integer line number within a class's source file. -->
	<!ELEMENT line (#PCDATA)>
	 */

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

	String KEY_NODE = "key";
	String CATALOG_NODE = "catalog";
	String PARAM_NODE = "param";

	String EXCEPTION_NODE = "exception";
	String FRAME_NODE = "frame";
	String LINE_NODE = "line";
}
