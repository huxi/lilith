/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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
 * Copyright 2007-2017 Joern Huxhorn
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

public interface LoggingEventSchemaConstants
{
	String NAMESPACE_URI = "http://lilith.sf.net/schema/logging/16";
	String NAMESPACE_LOCATION = "http://lilith.sf.net/schema/logging/16/LoggingEvent.xsd";

	String DEFAULT_NAMESPACE_PREFIX = "log";

	String LOGGING_EVENTS_NODE = "LoggingEvents";
	String START_INDEX_ATTRIBUTE = "startIndex";
	//String APPLICATION_IDENTIFIER_ATTRIBUTE = "applicationId";

	String LOGGING_EVENT_NODE = "LoggingEvent";
	String MESSAGE_NODE = "Message";
	String THROWABLE_NODE = "Throwable";
	String CALLSTACK_NODE = "CallStack";
	String LOGGER_ATTRIBUTE = "logger";
	String LEVEL_ATTRIBUTE = "level";
	String SEQUENCE_ATTRIBUTE = "sequence";
	String THREAD_NAME_ATTRIBUTE = "threadName";
	String THREAD_ID_ATTRIBUTE = "threadId";
	String THREAD_GROUP_NAME_ATTRIBUTE = "threadGroup";
	String THREAD_GROUP_ID_ATTRIBUTE = "threadGroupId";
	String THREAD_PRIORITY_ATTRIBUTE = "threadPriority";
	String TIMESTAMP_ATTRIBUTE = "timeStamp";
	String TIMESTAMP_MILLIS_ATTRIBUTE = "timeStampMillis";

	String ARGUMENTS_NODE = "Arguments";
	String ARGUMENT_NODE = "Argument";
	String NULL_ARGUMENT_NODE = "null";

	String THROWABLE_MESSAGE_NODE = "Message";
	String SUPPRESSED_NODE = "Suppressed";
	String CAUSE_NODE = "Cause";
	String THROWABLE_CLASS_NAME_ATTRIBUTE = "name";

	String MDC_NODE = "MDC";
	String STRING_MAP_ENTRY_NODE = "Entry";
	String STRING_MAP_ENTRY_KEY_ATTRIBUTE = "key";

	String NDC_NODE = "NDC";
	String NDC_ENTRY_NODE = "Entry";

	String MARKER_NODE = "Marker";
	String MARKER_NAME_ATTRIBUTE = "name";
	String MARKER_REFERENCE_NODE = "MarkerReference";
	String MARKER_REFERENCE_ATTRIBUTE = "ref";

	String STACK_TRACE_NODE = "StackTrace";
	String STACK_TRACE_ELEMENT_NODE = "StackTraceElement";
	String ST_CLASS_LOADER_NAME_ATTRIBUTE = "classLoaderName";
	String ST_MODULE_NAME_ATTRIBUTE = "moduleName";
	String ST_MODULE_VERSION_ATTRIBUTE = "moduleVersion";
	String ST_CLASS_NAME_ATTRIBUTE = "className";
	String ST_METHOD_NAME_ATTRIBUTE = "methodName";
	String ST_FILE_NAME_ATTRIBUTE = "fileName";
	String ST_LINE_NUMBER_NODE = "LineNumber";
	String ST_NATIVE_NODE = "Native";
	String ST_CODE_LOCATION_NODE = "CodeLocation";
	String ST_VERSION_NODE = "Version";
	String ST_EXACT_NODE = "Exact";

	String OMITTED_ELEMENTS_ATTRIBUTE = "omittedElements";

	String LOGGER_CONTEXT_NODE = "LoggerContext";
	String LOGGER_CONTEXT_NAME_ATTRIBUTE = "name";
	String LOGGER_CONTEXT_BIRTH_TIME_MILLIS_ATTRIBUTE = "birthTimeMillis";
	String LOGGER_CONTEXT_BIRTH_TIME_ATTRIBUTE = "birthTime";
	String LOGGER_CONTEXT_PROPERTIES_NODE = "Properties";
}
