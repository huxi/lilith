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

package de.huxhorn.lilith.log4j2.converter;

import de.huxhorn.lilith.data.converter.Converter;
import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

@SuppressWarnings("PMD.ClassNamingConventions")
public class Log4j2LoggingConverter
		implements Converter<LoggingEvent>
{
	public static final String LOG4J_LEVEL_KEY = "log4j.level";
	public static final String LOG4J_LEVEL_VALUE_FATAL = "FATAL";
	private static final String APPLICATION_MDC_KEY = "application";

	@Override
	public LoggingEvent convert(Object o)
	{
		if (o == null)
		{
			return null;
		}
		if (!(o instanceof org.apache.logging.log4j.core.LogEvent))
		{
			throw new IllegalArgumentException(o.toString() + " is not a " + getSourceClass() + "!");
		}
		org.apache.logging.log4j.core.LogEvent log4jEvent = (org.apache.logging.log4j.core.LogEvent) o;
		LoggingEvent result = new LoggingEvent();
		Map<String, String> mdc = new HashMap<>();

		// loggerName
		result.setLogger(log4jEvent.getLoggerName());

		// level
		{
			Level log4jLevel = log4jEvent.getLevel();
			if (log4jLevel == Level.TRACE)
			{
				result.setLevel(LoggingEvent.Level.TRACE);
			}
			else if (log4jLevel == Level.DEBUG)
			{
				result.setLevel(LoggingEvent.Level.DEBUG);
			}
			else if (log4jLevel == Level.INFO)
			{
				result.setLevel(LoggingEvent.Level.INFO);
			}
			else if (log4jLevel == Level.WARN)
			{
				result.setLevel(LoggingEvent.Level.WARN);
			}
			else if (log4jLevel == Level.ERROR)
			{
				result.setLevel(LoggingEvent.Level.ERROR);
			}
			else if (log4jLevel == Level.FATAL)
			{
				mdc.put(LOG4J_LEVEL_KEY, LOG4J_LEVEL_VALUE_FATAL);
				result.setLevel(LoggingEvent.Level.ERROR);
			}
		}

		// timeStamp
		result.setTimeStamp(log4jEvent.getTimeMillis());

		// Message
		{

			org.apache.logging.log4j.message.Message msg = log4jEvent.getMessage();
			if (msg != null)
			{
				result.setMessage(new Message(msg.getFormattedMessage()));
			}
		}

		// threadInfo
		{
			String threadName = log4jEvent.getThreadName();
			if (threadName != null)
			{
				ThreadInfo threadInfo = new ThreadInfo();
				threadInfo.setName(threadName);
				threadInfo.setId(log4jEvent.getThreadId());
				threadInfo.setPriority(log4jEvent.getThreadPriority());
				result.setThreadInfo(threadInfo);
			}
		}

		// MDC
		{
			ReadOnlyStringMap contextData = log4jEvent.getContextData();
			if (!contextData.isEmpty())
			{
				mdc.putAll(contextData.toMap());
			}
		}

		if (!mdc.isEmpty())
		{
			result.setMdc(mdc);

			// application / contextName
			if (mdc.containsKey(APPLICATION_MDC_KEY))
			{
				LoggerContext context = new LoggerContext();
				context.setName(mdc.get(APPLICATION_MDC_KEY));
				result.setLoggerContext(context);
			}
		}

		// NDC
		{
			org.apache.logging.log4j.ThreadContext.ContextStack ndc = log4jEvent.getContextStack();
			if (ndc != null)
			{
				List<String> list = ndc.asList();
				if (list != null && !list.isEmpty())
				{
					Message[] ndcResult = new Message[list.size()];
					for (int i = 0; i < list.size(); i++)
					{
						String current = list.get(i);
						if (current != null)
						{
							ndcResult[i] = new Message(current); // NOPMD - AvoidInstantiatingObjectsInLoops
						}
					}
					result.setNdc(ndcResult);
				}
			}
		}

		// location information
		{
			StackTraceElement location = log4jEvent.getSource();
			if (location != null)
			{
				ExtendedStackTraceElement ste = new ExtendedStackTraceElement();
				ste.setClassName(location.getClassName());
				ste.setMethodName(location.getMethodName());
				ste.setFileName(location.getFileName());
				ste.setLineNumber(location.getLineNumber());
				result.setCallStack(new ExtendedStackTraceElement[]{ste});
			}
		}

		// throwable information
		{
			result.setThrowable(convert(log4jEvent.getThrownProxy()));
		}

		initMarker(log4jEvent, result);

		return result;
	}

	private void initMarker(org.apache.logging.log4j.core.LogEvent src, LoggingEvent dst)
	{
		org.apache.logging.log4j.Marker origMarker = src.getMarker();
		if(origMarker == null)
		{
			return;
		}
		Map<String, Marker> markers = new HashMap<>();
		dst.setMarker(initMarkerRecursive(origMarker, markers));
	}

	private Marker initMarkerRecursive(org.apache.logging.log4j.Marker origMarker, Map<String, Marker> markers)
	{
		if(origMarker == null)
		{
			return null;
		}
		String name = origMarker.getName();
		if(markers.containsKey(name))
		{
			return markers.get(name);
		}
		Marker newMarker = new Marker(name);
		markers.put(name, newMarker);
		org.apache.logging.log4j.Marker[] parents = origMarker.getParents();
		if(parents != null)
		{
			for (org.apache.logging.log4j.Marker current : parents)
			{
				newMarker.add(initMarkerRecursive(current, markers));
			}
		}
		return newMarker;
	}

	private ThrowableInfo convert(org.apache.logging.log4j.core.impl.ThrowableProxy thrown)
	{
		if (thrown == null)
		{
			return null;
		}

		ThrowableInfo result = new ThrowableInfo();
		result.setCause(convert(thrown.getCauseProxy()));
		result.setMessage(thrown.getMessage());
		result.setName(thrown.getName());
		result.setStackTrace(convert(thrown.getExtendedStackTrace()));
		result.setOmittedElements(thrown.getCommonElementCount());
		result.setSuppressed(convert(thrown.getSuppressedProxies()));

		return result;
	}

	private ThrowableInfo[] convert(org.apache.logging.log4j.core.impl.ThrowableProxy[] array)
	{
	    if (array == null)
	    {
		    return null;
	    }

		ThrowableInfo[] result = new ThrowableInfo[array.length];
		for (int i=0; i<result.length; i++)
		{
			result[i]=convert(array[i]);
		}
		return result;
	}

	private ExtendedStackTraceElement[] convert(org.apache.logging.log4j.core.impl.ExtendedStackTraceElement[] array)
	{
		if(array == null)
		{
			return null;
		}

		ExtendedStackTraceElement[] result = new ExtendedStackTraceElement[array.length];
		for(int i=0; i<result.length; i++)
		{
			result[i] = convert(array[i]);
		}

		return result;
	}

	private ExtendedStackTraceElement convert(org.apache.logging.log4j.core.impl.ExtendedStackTraceElement ste)
	{
		if (ste == null)
		{
			return null;
		}

		ExtendedStackTraceElement result = new ExtendedStackTraceElement(ste.getStackTraceElement());
		result.setExact(ste.getExact());
		result.setVersion(ste.getVersion());
		result.setCodeLocation(ste.getLocation());

		return result;
	}

	@Override
	public Class getSourceClass()
	{
		return org.apache.logging.log4j.core.LogEvent.class;
	}
}
