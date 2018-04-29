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

package de.huxhorn.lilith.data.logging.logback.converter;

import ch.qos.logback.classic.spi.ClassPackagingData;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import de.huxhorn.lilith.data.converter.Converter;
import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.MessageFormatter;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LogbackLoggingConverter
	implements Converter<LoggingEvent>
{
	private static final Method GET_SUPPRESSED_METHOD;

	static
	{
		Method m = null;
		try
		{
			m = IThrowableProxy.class.getMethod("getSuppressed");
		}
		catch(NoSuchMethodException e)
		{
			// ignore
		}
		GET_SUPPRESSED_METHOD = m;
	}

	@SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
	private static IThrowableProxy[] getSuppressed(IThrowableProxy proxy)
	{
		if(GET_SUPPRESSED_METHOD == null || proxy == null)
		{
			return null;
		}
		try
		{
			Object result = GET_SUPPRESSED_METHOD.invoke(proxy);
			if(result instanceof IThrowableProxy[])
			{
				return (IThrowableProxy[]) result;
			}
		}
		catch(IllegalAccessException | InvocationTargetException e)
		{
			// ignore
		}
		return null;
	}

	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	private ExtendedStackTraceElement[] convert(StackTraceElement[] stackTrace)
	{
		if(stackTrace == null)
		{
			return null;
		}
		ExtendedStackTraceElement[] result = new ExtendedStackTraceElement[stackTrace.length];
		for(int i = 0; i < stackTrace.length; i++)
		{
			result[i] = new ExtendedStackTraceElement(stackTrace[i]);
		}
		return result;
	}

	ThrowableInfo initFromThrowableProxy(IThrowableProxy ti, boolean calculatePackagingData)
	{
		if(ti == null)
		{
			return null;
		}
		/* CHECK: java.lang.IllegalStateException: Packaging data has been already set
		if(calculatePackagingData && ti instanceof ThrowableProxy)
		{
		    ThrowableProxy tp= (ThrowableProxy) ti;
		    tp.calculatePackagingData();
		}
		*/
		ThrowableInfo result = new ThrowableInfo();
		result.setName(ti.getClassName());
		result.setOmittedElements(ti.getCommonFrames());
		result.setMessage(ti.getMessage());
		result.setStackTrace(initFromStackTraceElementProxyArray(ti.getStackTraceElementProxyArray()));

		IThrowableProxy[] suppressedThrowableProxies = getSuppressed(ti);
		if(suppressedThrowableProxies != null)
		{
			ThrowableInfo[] suppressed = new ThrowableInfo[suppressedThrowableProxies.length];
			for(int i=0;i<suppressedThrowableProxies.length;i++)
			{
				suppressed[i] = initFromThrowableProxy(suppressedThrowableProxies[i], calculatePackagingData);
			}
			result.setSuppressed(suppressed);
		}
		result.setCause(initFromThrowableProxy(ti.getCause(), calculatePackagingData));
		return result;
	}

	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	private static ExtendedStackTraceElement[] initFromStackTraceElementProxyArray(StackTraceElementProxy[] stackTraceElementProxies)
	{
		if(stackTraceElementProxies == null)
		{
			return null;
		}
		int elementCount = stackTraceElementProxies.length;
		ExtendedStackTraceElement[] result = new ExtendedStackTraceElement[elementCount];
		for(int i = 0; i < elementCount; i++)
		{
			StackTraceElementProxy currentInput = stackTraceElementProxies[i];
			if(currentInput != null)
			{
				ExtendedStackTraceElement current = new ExtendedStackTraceElement(currentInput.getStackTraceElement());
				ClassPackagingData cpd = currentInput.getClassPackagingData();
				if(cpd != null)
				{
					current.setCodeLocation(cpd.getCodeLocation());
					current.setExact(cpd.isExact());
					current.setVersion(cpd.getVersion());
				}
				result[i] = current;
			}
		}
		return result;
	}

	private void initMarker(ch.qos.logback.classic.spi.ILoggingEvent src, LoggingEvent dst)
	{
		org.slf4j.Marker origMarker = src.getMarker();
		if(origMarker == null)
		{
			return;
		}
		Map<String, Marker> markers = new HashMap<>();
		dst.setMarker(initMarkerRecursive(origMarker, markers));
	}

	private Marker initMarkerRecursive(org.slf4j.Marker origMarker, Map<String, Marker> markers)
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
		if(origMarker.hasReferences())
		{
			Iterator iter = origMarker.iterator();
			while(iter.hasNext())
			{
				org.slf4j.Marker current = (org.slf4j.Marker) iter.next();
				newMarker.add(initMarkerRecursive(current, markers));
			}
		}
		return newMarker;
	}

	@Override
	public LoggingEvent convert(Object o)
	{
		if(o == null)
		{
			return null;
		}
		if(!(o instanceof ch.qos.logback.classic.spi.ILoggingEvent))
		{
			throw new IllegalArgumentException(o.toString() + " is not a "+getSourceClass()+"!");
		}
		ch.qos.logback.classic.spi.ILoggingEvent event = (ch.qos.logback.classic.spi.ILoggingEvent) o;

		LoggingEvent result = new LoggingEvent();
		String messagePattern = event.getMessage();

		Object[] originalArguments = event.getArgumentArray();
		MessageFormatter.ArgumentResult argumentResult = MessageFormatter.evaluateArguments(messagePattern, originalArguments);

		String[] arguments = null;
		if(argumentResult != null)
		{
			arguments = argumentResult.getArguments();
			Throwable t = argumentResult.getThrowable();
			if(t != null
					&& event.getThrowableProxy() == null
					&& event instanceof ch.qos.logback.classic.spi.LoggingEvent)
			{
				ch.qos.logback.classic.spi.LoggingEvent le = (ch.qos.logback.classic.spi.LoggingEvent) event;
				le.setThrowableProxy(new ThrowableProxy(t));
			}
		}
		if(messagePattern != null || arguments != null)
		{
			Message message = new Message(messagePattern, arguments);
			result.setMessage(message);
		}
		event.prepareForDeferredProcessing();
		// TODO: configurable calculation of packaging data?
		result.setThrowable(initFromThrowableProxy(event.getThrowableProxy(), true));


		// TODO: configurable init of call stack, i.e. don't execute next line.
		result.setCallStack(convert(event.getCallerData()));

		result.setLogger(event.getLoggerName());

		result.setLevel(LoggingEvent.Level.valueOf(event.getLevel().toString()));
		LoggerContextVO lcv = event.getLoggerContextVO();
		if(lcv != null)
		{
			String name = lcv.getName();
			Map<String, String> props = lcv.getPropertyMap();
			if(props != null)
			{
				// lcv property map leak? yes, indeed. See http://jira.qos.ch/browse/LBCLASSIC-115
				props = new HashMap<>(props);
			}
			LoggerContext loggerContext = new LoggerContext();
			loggerContext.setName(name);
			loggerContext.setProperties(props);
			loggerContext.setBirthTime(lcv.getBirthTime());
			result.setLoggerContext(loggerContext);
		}
		initMarker(event, result);
		result.setMdc(event.getMDCPropertyMap());
		String threadName = event.getThreadName();

		if(threadName != null)
		{
			ThreadInfo threadInfo = new ThreadInfo();
			threadInfo.setName(threadName);
			result.setThreadInfo(threadInfo);
		}
		result.setTimeStamp(event.getTimeStamp());

		return result;
	}

	@Override
	public Class getSourceClass()
	{
		return ch.qos.logback.classic.spi.ILoggingEvent.class;
	}
}
