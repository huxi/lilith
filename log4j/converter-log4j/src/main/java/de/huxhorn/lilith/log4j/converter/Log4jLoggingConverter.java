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

package de.huxhorn.lilith.log4j.converter;

import de.huxhorn.lilith.data.converter.Converter;
import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfoParser;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.ThrowableInformation;

@SuppressWarnings("PMD.ClassNamingConventions")
public class Log4jLoggingConverter
	implements Converter<LoggingEvent>
{
	private static final String LOG4J_LEVEL_KEY = "log4j.level";
	private static final String LOG4J_LEVEL_VALUE_FATAL = "FATAL";
	private static final String APPLICATION_MDC_KEY = "application";

	@Override
	public LoggingEvent convert(Object o)
	{
		if(o == null)
		{
			return null;
		}
		if(!(o instanceof org.apache.log4j.spi.LoggingEvent))
		{
			throw new IllegalArgumentException(o.toString()+" is not a "+getSourceClass()+"!");
		}
		org.apache.log4j.spi.LoggingEvent log4jEvent = (org.apache.log4j.spi.LoggingEvent) o;
		LoggingEvent result=new LoggingEvent();
		Map<String, String> mdc=new HashMap<>();

		// loggerName
		result.setLogger(log4jEvent.getLoggerName());

		// level
		{
			org.apache.log4j.Level log4jLevel = log4jEvent.getLevel();
			if(log4jLevel.equals(org.apache.log4j.Level.TRACE))
			{
				result.setLevel(LoggingEvent.Level.TRACE);
			}
			else if(log4jLevel.equals(org.apache.log4j.Level.DEBUG))
			{
				result.setLevel(LoggingEvent.Level.DEBUG);
			}
			else if(log4jLevel.equals(org.apache.log4j.Level.INFO))
			{
				result.setLevel(LoggingEvent.Level.INFO);
			}
			else if(log4jLevel.equals(org.apache.log4j.Level.WARN))
			{
				result.setLevel(LoggingEvent.Level.WARN);
			}
			else if(log4jLevel.equals(org.apache.log4j.Level.ERROR))
			{
				result.setLevel(LoggingEvent.Level.ERROR);
			}
			else if(log4jLevel.equals(org.apache.log4j.Level.FATAL))
			{
				mdc.put(LOG4J_LEVEL_KEY, LOG4J_LEVEL_VALUE_FATAL);
				result.setLevel(LoggingEvent.Level.ERROR);
			}
		}

		// timeStamp
		result.setTimeStamp(log4jEvent.getTimeStamp());

		// Message
		{
			String msg = log4jEvent.getRenderedMessage();
			if(msg != null)
			{
				result.setMessage(new Message(msg));
			}
		}

		// threadInfo
		{
			String threadName=log4jEvent.getThreadName();
			if(threadName != null)
			{
				ThreadInfo threadInfo=new ThreadInfo();
				threadInfo.setName(threadName);
				result.setThreadInfo(threadInfo);
			}
		}

		// MDC
		{
			Map props = log4jEvent.getProperties();
			if(props != null)
			{
				for(Object currentObj : props.entrySet())
				{
					Map.Entry current= (Map.Entry) currentObj;
					String keyStr=null;
					String valueStr=null;
					Object key=current.getKey();
					Object value=current.getValue();
					if(key != null)
					{
						keyStr=key.toString(); // use safe toString
					}
					if(value != null)
					{
						valueStr=value.toString(); // use safe toString
					}
					if(keyStr != null && valueStr != null)
					{
						mdc.put(keyStr, valueStr);
					}
				}
			}
		}
		if(!mdc.isEmpty())
		{
			result.setMdc(mdc);
		}

		// application / contextName
		if(mdc.containsKey(APPLICATION_MDC_KEY))
		{
			LoggerContext context=new LoggerContext();
			context.setName(mdc.get(APPLICATION_MDC_KEY));
			result.setLoggerContext(context);
		}

		// NDC
		{
			String ndc=log4jEvent.getNDC();
			if("".equals(ndc))
			{
				ndc = null;
			}
			if(ndc != null)
			{
				// TODO: tokenize?
				result.setNdc(new Message[]{new Message(ndc)});
			}
		}

		// location information
		{
			LocationInfo location = log4jEvent.getLocationInformation();
			if(location != null)
			{
				ExtendedStackTraceElement ste = new ExtendedStackTraceElement();
				// LOG4J_MODULE
				// ste.setClassLoaderName(location.getClassLoaderName());
				// ste.setModuleName(location.getModuleName());
				// ste.setModuleVersion(location.getModuleVersion());
				ste.setClassName(location.getClassName());
				ste.setMethodName(location.getMethodName());
				ste.setFileName(location.getFileName());
				String line = location.getLineNumber();
				if(line != null)
				{
					try
					{
						ste.setLineNumber(Integer.parseInt(line));
					}
					catch(NumberFormatException ex)
					{
						// ignore
					}
				}
				result.setCallStack(new ExtendedStackTraceElement[]{ste});
			}
		}

		// throwable information
		{
			// TODO: log4jEvent.getThrowableInformation();
			ThrowableInformation ti = log4jEvent.getThrowableInformation();
			if(ti != null)
			{
				String[] throwableStrRep = ti.getThrowableStrRep();
				if(throwableStrRep != null && throwableStrRep.length>0)
				{
					result.setThrowable(ThrowableInfoParser.parse(Arrays.asList(throwableStrRep)));
				}
			}
		}

		return result;
	}

	@Override
	public Class getSourceClass()
	{
		return org.apache.log4j.spi.LoggingEvent.class;
	}
}
