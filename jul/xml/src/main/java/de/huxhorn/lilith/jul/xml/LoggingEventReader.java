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

import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.sulky.stax.GenericStreamReader;
import de.huxhorn.sulky.stax.StaxUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class LoggingEventReader
	implements GenericStreamReader<LoggingEvent>, LoggingEventSchemaConstants
{
	private final Logger logger = LoggerFactory.getLogger(LoggingEventReader.class);

	private static final String MESSAGE_SEPARATOR = ": ";

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
			while(type != XMLStreamConstants.START_ELEMENT || !RECORD_NODE.equals(reader.getLocalName()));
			rootNamespace = null;
		}
		if(XMLStreamConstants.START_ELEMENT == type && RECORD_NODE.equals(reader.getLocalName()))
		{
			reader.nextTag();

			result = new LoggingEvent();

			String dateStr = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, DATE_NODE);
			String millisStr = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, MILLIS_NODE);
			Long timeStamp = null;
			if(millisStr != null)
			{
				try
				{
					timeStamp = Long.parseLong(millisStr);
				}
				catch(NumberFormatException ex)
				{
					// ignore
				}
			}
			if(timeStamp == null && dateStr != null)
			{
				// TODO: parse from string
				if(logger.isInfoEnabled())
				{
					logger.info("Parsing date hasn't been implemented since millis is mandatory in Schema.");
				}
			}
			result.setTimeStamp(timeStamp);

			String sequenceStr = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, SEQUENCE_NODE);
			try
			{
				result.setSequenceNumber(Long.parseLong(sequenceStr));
			}
			catch(NumberFormatException ex)
			{
				// ignore
			}

			String loggerStr = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, LOGGER_NODE);
			result.setLogger(loggerStr);

			result
				.setLevel(resolveLevel(StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, LEVEL_NODE)));

			String classStr = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, CLASS_NODE);
			String methodStr = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, METHOD_NODE);
			if(classStr != null || methodStr != null)
			{
				ExtendedStackTraceElement[] callStack = new ExtendedStackTraceElement[]
					{
						new ExtendedStackTraceElement(classStr, methodStr, null, -1)
					};
				result.setCallStack(callStack);
			}
			String threadStr = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, THREAD_NODE);
			if(threadStr != null)
			{
				try
				{
					long threadId = Long.parseLong(threadStr);
					result.setThreadInfo(new ThreadInfo(threadId, null, null, null));
				}
				catch(NumberFormatException ex)
				{
					// ignore
				}

			}
			String messageStr = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, MESSAGE_NODE);
			if(messageStr != null)
			{
				result.setMessage(new Message(messageStr));
			}
			// key?, catalog? param*
			{
				String keyStr = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, KEY_NODE);
				String catalogStr = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, CATALOG_NODE);
				List<String> paramList = new ArrayList<String>();
				for(; ;)
				{
					String paramStr = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, PARAM_NODE);
					if(paramStr == null)
					{
						break;
					}
					paramList.add(paramStr);
				}
				if(keyStr != null || catalogStr != null || paramList.size() > 0)
				{
					if(logger.isInfoEnabled())
					{
						logger
							.info("Ignoring the following message infos: key={}, catalog={}, params={}", new Object[]{keyStr, catalogStr, paramList});
					}
				}
			}
			result.setThrowable(readThrowableInfo(reader));
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, RECORD_NODE);
			for(; ;)
			{
				reader.next();
				type = reader.getEventType();
				if(type == XMLStreamConstants.END_DOCUMENT)
				{
					break;
				}
				if(type == XMLStreamConstants.START_ELEMENT)
				{
					break;
				}
			}
			return result;
		}
		return result;
	}


	private ThrowableInfo readThrowableInfo(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && EXCEPTION_NODE.equals(reader.getLocalName()))
		{
			reader.nextTag();
			ThrowableInfo result = new ThrowableInfo();
			String messageStr = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, MESSAGE_NODE);
			// parse message
			if(messageStr != null)
			{
				int separatorIndex = messageStr.indexOf(MESSAGE_SEPARATOR);
				String className;
				if(separatorIndex >= 0)
				{
					className = messageStr.substring(0, separatorIndex);
					messageStr = messageStr.substring(separatorIndex + MESSAGE_SEPARATOR.length());
				}
				else
				{
					className = messageStr;
					messageStr = null;
				}

				result.setMessage(messageStr);
				result.setName(className);
			}
			List<ExtendedStackTraceElement> stackTraceList = new ArrayList<ExtendedStackTraceElement>();
			for(; ;)
			{
				ExtendedStackTraceElement current = parseFrame(reader);
				if(current == null)
				{
					break;
				}
				stackTraceList.add(current);
			}
			result.setStackTrace(stackTraceList.toArray(new ExtendedStackTraceElement[stackTraceList.size()]));
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, EXCEPTION_NODE);
			reader.nextTag();
			return result;
		}
		return null;
	}

	private ExtendedStackTraceElement parseFrame(XMLStreamReader reader)
		throws XMLStreamException
	{
		int type = reader.getEventType();
		if(XMLStreamConstants.START_ELEMENT == type && FRAME_NODE.equals(reader.getLocalName()))
		{
			reader.nextTag();
			String classStr = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, CLASS_NODE);
			String methodStr = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, METHOD_NODE);
			String lineStr = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, LINE_NODE);
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
			reader.require(XMLStreamConstants.END_ELEMENT, NAMESPACE_URI, FRAME_NODE);
			reader.nextTag();
			return new ExtendedStackTraceElement(classStr, methodStr, null, line);
		}
		return null;
	}

	private LoggingEvent.Level resolveLevel(String levelStr)
	{
		if(levelStr == null)
		{
			return null;
		}

		int levelIntValue = 0;
		try
		{
			levelIntValue = Integer.parseInt(levelStr);
		}
		catch(NumberFormatException ex)
		{
			try
			{
				Level level = Level.parse(levelStr);
				levelIntValue = level.intValue();
			}
			catch(IllegalArgumentException ex2)
			{
				// ignore, shouldn't happen at this point
			}
		}

		if(levelIntValue <= Level.FINEST.intValue())
		{
			return LoggingEvent.Level.TRACE;
		}
		if(levelIntValue <= Level.FINE.intValue())
		{
			return LoggingEvent.Level.DEBUG;
		}
		if(levelIntValue <= Level.INFO.intValue())
		{
			return LoggingEvent.Level.INFO;
		}
		if(levelIntValue <= Level.WARNING.intValue())
		{
			return LoggingEvent.Level.WARN;
		}
		return LoggingEvent.Level.ERROR;
	}
}
