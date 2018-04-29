/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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
 * Copyright 2007-2018 Joern Huxhorn
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

package de.huxhorn.lilith.log4j.xml;

import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfoParser;
import de.huxhorn.sulky.stax.GenericStreamReader;
import de.huxhorn.sulky.stax.StaxUtilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class LoggingEventReader
	implements GenericStreamReader<LoggingEvent>, LoggingEventSchemaConstants
{
	private static final String NEWLINE = "\n";

	@Override
	public LoggingEvent read(XMLStreamReader reader)
		throws XMLStreamException
	{

		int type = reader.getEventType();
		if(XMLStreamConstants.START_DOCUMENT == type)
		{
			do
			{
				reader.next();
				type = reader.getEventType();
			}
			while(type != XMLStreamConstants.START_ELEMENT);
		}
		if(XMLStreamConstants.START_ELEMENT == type && LOGGING_EVENT_NODE.equals(reader.getLocalName()))
		{
			LoggingEvent result = new LoggingEvent();
			result.setLogger(StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, LOGGER_ATTRIBUTE));

			String levelStr = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, LEVEL_ATTRIBUTE);
			if("FATAL".equals(levelStr))
			{
				levelStr = "ERROR";
			}
			try
			{
				result.setLevel(LoggingEvent.Level.valueOf(levelStr));
			}
			catch(IllegalArgumentException ex)
			{
				// ignore
			}
			String threadName = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, THREAD_NAME_ATTRIBUTE);
			Long threadId = null;
			try
			{
				String threadIdStr = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, THREAD_ID_ATTRIBUTE);
				if(threadIdStr != null)
				{
					threadId = Long.valueOf(threadIdStr);
				}
			}
			catch(NumberFormatException ex)
			{
				// ignore
			}

			String threadGroupName = StaxUtilities
				.readAttributeValue(reader, NAMESPACE_URI, THREAD_GROUP_NAME_ATTRIBUTE);
			Long threadGroupId = null;
			try
			{
				String idStr = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, THREAD_GROUP_ID_ATTRIBUTE);
				if(idStr != null)
				{
					threadGroupId = Long.valueOf(idStr);
				}
			}
			catch(NumberFormatException ex)
			{
				// ignore
			}


			if(threadName != null || threadId != null || threadGroupId != null || threadGroupName != null)
			{
				result.setThreadInfo(new ThreadInfo(threadId, threadName, threadGroupId, threadGroupName));
			}
			String timeStamp = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, TIMESTAMP_ATTRIBUTE);
			try
			{
				result.setTimeStamp(Long.parseLong(timeStamp));
			}
			catch(NumberFormatException e)
			{
				// ignore
			}
			reader.nextTag();
			String messagePattern = StaxUtilities.readSimpleTextNodeIfAvailable(reader, null, MESSAGE_NODE);
			if(messagePattern != null)
			{
				result.setMessage(new Message(messagePattern));
			}

			result.setNdc(readNdc(reader));
			result.setThrowable(readThrowable(reader));
			result.setCallStack(readLocationInfo(reader));
			result.setMdc(readMdc(reader));
			return result;
		}
		return null;
	}

	private Map<String, String> readMdc(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && PROPERTIES_NODE.equals(reader.getLocalName()))
		{
			Map<String, String> mdc = new HashMap<>();
			reader.nextTag();
			for(;;)
			{
				MdcEntry entry = readMdcEntry(reader);
				if(entry == null)
				{
					break;
				}
				mdc.put(entry.key, entry.value);
			}
			reader.require(XMLStreamConstants.END_ELEMENT, null, PROPERTIES_NODE);
			reader.nextTag();
			return mdc;
		}
		return null;
	}

	private MdcEntry readMdcEntry(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && DATA_NODE.equals(reader.getLocalName()))
		{
			MdcEntry entry = new MdcEntry();
			entry.key = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, NAME_ATTRIBUTE);
			entry.value = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, VALUE_ATTRIBUTE);
			reader.nextTag();
			reader.require(XMLStreamConstants.END_ELEMENT, null, DATA_NODE);
			reader.nextTag();
			return entry;
		}
		return null;
	}

	@SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
	private ExtendedStackTraceElement[] readLocationInfo(XMLStreamReader reader)
		throws XMLStreamException
	{
		// <log4j:locationInfo class="de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass" method="execute" file="Log4jSandbox.java" line="18"/>
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && LOCATION_INFO_NODE.equals(reader.getLocalName()))
		{
			String className = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, CLASS_ATTRIBUTE);
			String methodName = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, METHOD_ATTRIBUTE);
			String fileName = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, FILE_ATTRIBUTE);
			String lineStr = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, LINE_ATTRIBUTE);
			int line = -1;
			if(lineStr != null)
			{
				try
				{
					line = Integer.parseInt(lineStr);
				}
				catch(NumberFormatException ex)
				{
					// ignore
				}
			}
			ExtendedStackTraceElement ste = new ExtendedStackTraceElement(className, methodName, fileName, line);
			reader.nextTag();
			reader.require(XMLStreamConstants.END_ELEMENT, null, LOCATION_INFO_NODE);
			reader.nextTag();
			return new ExtendedStackTraceElement[]{ste};
		}
		return null;
	}

	private ThrowableInfo readThrowable(XMLStreamReader reader)
		throws XMLStreamException
	{
		String throwableString = StaxUtilities.readSimpleTextNodeIfAvailable(reader, null, THROWABLE_NODE);
		if(throwableString != null)
		{
			StringTokenizer tok = new StringTokenizer(throwableString, NEWLINE, true);
			List<String> lines = new ArrayList<>();
			boolean wasNewline=false;
			while(tok.hasMoreTokens())
			{
				String current = tok.nextToken();
				if(NEWLINE.equals(current))
				{
					if(wasNewline)
					{
						// support empty lines
						lines.add("");
						wasNewline=false;
					}
					else
					{
						wasNewline=true;
					}
				}
				else
				{
					wasNewline=false;
					lines.add(current);
				}
			}
			return ThrowableInfoParser.parse(lines);
		}
		return null;
	}

	private Message[] readNdc(XMLStreamReader reader)
		throws XMLStreamException
	{
		String ndcString = StaxUtilities.readSimpleTextNodeIfAvailable(reader, null, NDC_NODE);
		if(ndcString == null)
		{
			return null;
		}
		ArrayList<Message> ndcs = new ArrayList<>();
		StringTokenizer tok = new StringTokenizer(ndcString, " ", false); // *sigh*
		while(tok.hasMoreTokens())
		{
			ndcs.add(new Message(tok.nextToken())); // NOPMD - AvoidInstantiatingObjectsInLoops
		}
		return ndcs.toArray(Message.ARRAY_PROTOTYPE);
	}

	private static class MdcEntry
	{
		public String key;
		public String value;
	}
}
