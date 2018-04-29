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

package de.huxhorn.lilith.tracing;

import de.huxhorn.sulky.formatting.SafeString;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class TracingAspect
{
	public static final String TRACED_CLASS_MDC_KEY = "tracedClass";
	public static final String TRACED_METHOD_MDC_KEY = "tracedMethod";

	public static final String ENTERING_MARKER_NAME = "ENTERING";
	public static final String EXITING_MARKER_NAME = "EXITING";
	public static final String THROWING_MARKER_NAME = "THROWING";
	public static final String TRACE_MARKER_NAME = "TRACE";

	private static final Marker ENTERING_MARKER = MarkerFactory.getDetachedMarker(ENTERING_MARKER_NAME);
	private static final Marker EXITING_MARKER = MarkerFactory.getDetachedMarker(EXITING_MARKER_NAME);
	private static final Marker THROWING_MARKER = MarkerFactory.getDetachedMarker(THROWING_MARKER_NAME);
	private static final Marker TRACE_MARKER = MarkerFactory.getDetachedMarker(TRACE_MARKER_NAME);

	static
	{
		ENTERING_MARKER.add(TRACE_MARKER);
		EXITING_MARKER.add(TRACE_MARKER);
		THROWING_MARKER.add(TRACE_MARKER);
	}

	private String loggerName;
	private Logger logger;
	private boolean showingParameterValues;
	private boolean usingShortClassName;
	private boolean showingModifiers;
	private ProfilingHandler profilingHandler;

	public TracingAspect()
	{
		showingParameterValues = true;
		usingShortClassName = false;
		showingModifiers = false;
		profilingHandler = new BasicProfilingHandler();
	}

	public boolean isShowingParameterValues()
	{
		return showingParameterValues;
	}

	public void setShowingParameterValues(boolean showingParameterValues)
	{
		this.showingParameterValues = showingParameterValues;
	}

	public boolean isUsingShortClassName()
	{
		return usingShortClassName;
	}

	public void setUsingShortClassName(boolean usingShortClassName)
	{
		this.usingShortClassName = usingShortClassName;
	}

	public boolean isShowingModifiers()
	{
		return showingModifiers;
	}

	public void setShowingModifiers(boolean showingModifiers)
	{
		this.showingModifiers = showingModifiers;
	}

	public ProfilingHandler getProfilingHandler()
	{
		return profilingHandler;
	}

	public void setProfilingHandler(ProfilingHandler profilingHandler)
	{
		this.profilingHandler = profilingHandler;
	}

	public String getLoggerName()
	{
		return loggerName;
	}

	public void setLoggerName(String loggerName)
	{
		this.loggerName = loggerName;
		if(loggerName != null)
		{
			logger = LoggerFactory.getLogger(loggerName);
		}
		else
		{
			logger = LoggerFactory.getLogger(TracingAspect.class);
		}
	}

	@Override
	public String toString()
	{
		return "TracingAspect{loggerName="+loggerName+", showingParameterValues="+ showingParameterValues +", usingShortClassName="+ usingShortClassName +", showingModifiers="+showingModifiers+", profilingHandler="+profilingHandler+"}";
	}

	public Object trace(ProceedingJoinPoint call) throws Throwable
	{
		if(logger == null)
		{
			setLoggerName(null);
			// this initializes the logger
		}
		Signature signature=call.getSignature();
		Class<?> clazz = signature.getDeclaringType();
		Object theTarget = call.getTarget();
		if(theTarget != null)
		{
			clazz = theTarget.getClass();
		}
		String fullClassName = clazz.getName();
		String methodName = signature.getName();
		StringBuilder msg=new StringBuilder();
		if(showingModifiers)
		{
			msg.append(Modifier.toString(signature.getModifiers())).append(' ');
		}
		if(usingShortClassName)
		{
			msg.append(clazz.getSimpleName());
		}
		else
		{
			msg.append(fullClassName);
		}
		msg.append('.').append(methodName);
		String methodBaseName = msg.toString();
		if(signature instanceof MethodSignature)
		{
			MethodSignature methodSignature=(MethodSignature)signature;
			msg.append('(');
			if(showingParameterValues)
			{
				Object[] args=call.getArgs();
				boolean first=true;
				for(Object arg:args)
				{
					if(first)
					{
						first=false;
					}
					else
					{
						msg.append(", ");
					}
					msg.append(SafeString.toString(arg, SafeString.StringWrapping.ALL, SafeString.StringStyle.GROOVY, SafeString.MapStyle.GROOVY));
				}
			}
			else
			{
				Method method=methodSignature.getMethod();
				Class<?>[] parameterTypes = method.getParameterTypes();
				boolean first=true;
				for(Class<?> param : parameterTypes)
				{
					if(first)
					{
						first=false;
					}
					else
					{
						msg.append(", ");
					}
					msg.append(param.getSimpleName());
				}
				if(method.isVarArgs())
				{
					int length=msg.length();
					msg.delete(length-2, length); // cut of existing []
					msg.append("...");
				}
			}
			msg.append(')');
		}
		String methodSignatureString=msg.toString();

		String previousClass = MDC.get(TRACED_CLASS_MDC_KEY);
		String previousMethod = MDC.get(TRACED_METHOD_MDC_KEY);
		long nanoSeconds=0;
		try
		{
			MDC.put(TRACED_CLASS_MDC_KEY, fullClassName);
			MDC.put(TRACED_METHOD_MDC_KEY, methodName);
			if(logger.isInfoEnabled(ENTERING_MARKER)) logger.info(ENTERING_MARKER, "{} entered.", methodSignatureString);
			Object result;
			nanoSeconds=System.nanoTime();
			result=call.proceed();
			nanoSeconds=System.nanoTime()-nanoSeconds;
			profile(methodBaseName, methodSignatureString, nanoSeconds);
			if(result == null || !showingParameterValues)
			{
				if(logger.isInfoEnabled(EXITING_MARKER)) logger.info(EXITING_MARKER, "{} returned.", methodSignatureString);
			}
			else
			{
				if(logger.isInfoEnabled(EXITING_MARKER)) logger.info(EXITING_MARKER, "{} returned {}.", methodSignatureString, result);
			}
			return result;
		}
		catch(Throwable t)
		{
			nanoSeconds=System.nanoTime()-nanoSeconds;
			profile(methodBaseName, methodSignatureString, nanoSeconds);
			if(logger.isInfoEnabled(THROWING_MARKER)) logger.info(THROWING_MARKER, "{} failed.", methodSignatureString, t);
			throw t; // rethrow
		}
		finally
		{
			if(previousClass == null)
			{
				MDC.remove(TRACED_CLASS_MDC_KEY);
			}
			else
			{
				MDC.put(TRACED_CLASS_MDC_KEY, previousClass);
			}
			if(previousMethod == null)
			{
				MDC.remove(TRACED_METHOD_MDC_KEY);
			}
			else
			{
				MDC.put(TRACED_METHOD_MDC_KEY, previousMethod);
			}
		}
	}

	private void profile(String methodBaseName, String fullMethodSignature, long nanoSeconds)
	{
		if(profilingHandler != null)
		{
			logger.info("{}", profilingHandler);
			profilingHandler.profile(logger, methodBaseName, fullMethodSignature, nanoSeconds);
		}
	}
}
