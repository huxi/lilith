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
package de.huxhorn.lilith.log4j.xml;

import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.sulky.stax.GenericStreamReader;
import de.huxhorn.sulky.stax.StaxUtilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class LoggingEventReader
	implements GenericStreamReader<LoggingEvent>, LoggingEventSchemaConstants
{
	public LoggingEventReader()
	{
	}

	public LoggingEvent read(XMLStreamReader reader)
		throws XMLStreamException
	{
		LoggingEvent result = null;
		String rootNamespace = NAMESPACE_URI;
		int type = reader.getEventType();
		if(XMLStreamConstants.START_DOCUMENT == type)
		{
			do
			{
				reader.next();
				type = reader.getEventType();
			}
			while(type != XMLStreamConstants.START_ELEMENT);
			rootNamespace = null;
		}
		if(XMLStreamConstants.START_ELEMENT == type && LOGGING_EVENT_NODE.equals(reader.getLocalName()))
		{
			result = new LoggingEvent();
			result.setLogger(StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, LOGGER_ATTRIBUTE));
			// TODO: FATAL
			result
				.setLevel(LoggingEvent.Level.valueOf(StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, LEVEL_ATTRIBUTE)));
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

			String threadGroupName = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, THREAD_GROUP_NAME_ATTRIBUTE);
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


			if(threadName != null || threadId != null || threadGroupId != null ||threadGroupName != null)
			{
				result.setThreadInfo(new ThreadInfo(threadId, threadName, threadGroupId, threadGroupName));
			}
			String timeStamp = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, TIMESTAMP_ATTRIBUTE);
			try
			{
				long parsedTimeStamp = Long.parseLong(timeStamp);
				result.setTimeStamp(new Date(parsedTimeStamp));
			}
			catch(NumberFormatException e)
			{
// TODO: change body of catch statement
				e.printStackTrace();
			}
			reader.nextTag();
			String messagePattern = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, MESSAGE_NODE);
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
		return result;
	}

	private Map<String, String> readMdc(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && PROPERTIES_NODE.equals(reader.getLocalName()) && NAMESPACE_URI
			.equals(reader.getNamespaceURI()))
		{
			Map<String, String> mdc = new HashMap<String, String>();
			reader.nextTag();
			for(; ;)
			{
				MdcEntry entry = readMdcEntry(reader);
				if(entry == null)
				{
					break;
				}
				mdc.put(entry.key, entry.value);
			}
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, PROPERTIES_NODE);
			reader.nextTag();
			return mdc;
		}
		return null;
	}

	private MdcEntry readMdcEntry(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && DATA_NODE.equals(reader.getLocalName()) && NAMESPACE_URI
			.equals(reader.getNamespaceURI()))
		{
			MdcEntry entry = new MdcEntry();
			entry.key = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, NAME_ATTRIBUTE);
			entry.value = StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, VALUE_ATTRIBUTE);
			reader.nextTag();
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, DATA_NODE);
			reader.nextTag();
			return entry;
		}
		return null;
	}

	private ExtendedStackTraceElement[] readLocationInfo(XMLStreamReader reader)
		throws XMLStreamException
	{
		// <log4j:locationInfo class="de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass" method="execute" file="Log4jSandbox.java" line="18"/>
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && LOCATION_INFO_NODE.equals(reader.getLocalName()) && NAMESPACE_URI
			.equals(reader.getNamespaceURI()))
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
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, LOCATION_INFO_NODE);
			reader.nextTag();
			return new ExtendedStackTraceElement[]{ste};
		}
		return null;
	}

	private ThrowableInfo readThrowable(XMLStreamReader reader)
		throws XMLStreamException
	{
		String throwableString = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, THROWABLE_NODE);
		// TODO: parse Throwable string...
		return null;
	}

	private Message[] readNdc(XMLStreamReader reader)
		throws XMLStreamException
	{
		String ndcString = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, NDC_NODE);
		if(ndcString == null)
		{
			return null;
		}
		ArrayList<Message> ndcs = new ArrayList<Message>();
		StringTokenizer tok = new StringTokenizer(ndcString, " ", false); // *sigh*
		while(tok.hasMoreTokens())
		{
			ndcs.add(new Message(tok.nextToken()));
		}
		return ndcs.toArray(new Message[ndcs.size()]);
	}

	private static class MdcEntry
	{
		public String key;
		public String value;
	}
}
