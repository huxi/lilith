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

package de.huxhorn.lilith.tracing;

import de.huxhorn.sulky.formatting.SafeString;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TracingAspect
{
	public static final int DEFAULT_WARN_THRESHOLD_IN_SECONDS = 3;
	public static final int DEFAULT_ERROR_THRESHOLD_IN_SECONDS = 30;

	public static final String TRACED_CLASS_MDC_KEY = "tracedClass";
	public static final String TRACED_METHOD_MDC_KEY = "tracedMethod";

	private static final Marker ENTERING_MARKER = MarkerFactory.getDetachedMarker("ENTERING");
	private static final Marker EXITING_MARKER = MarkerFactory.getDetachedMarker("EXITING");
	private static final Marker THROWING_MARKER = MarkerFactory.getDetachedMarker("THROWING");
	private static final Marker TRACE_MARKER = MarkerFactory.getDetachedMarker("TRACE");
	private static final Marker PROFILE_MARKER = MarkerFactory.getDetachedMarker("PROFILE");

	static
	{
		ENTERING_MARKER.add(TRACE_MARKER);
		EXITING_MARKER.add(TRACE_MARKER);
		THROWING_MARKER.add(TRACE_MARKER);
	}

	private String loggerName;
	private boolean showingParameterValues;
	private boolean usingShortClassName;
	private boolean showingModifiers;

	private int warnThresholdInSeconds;
	private int errorThresholdInSeconds;

	public TracingAspect()
	{
		showingParameterValues = true;
		usingShortClassName = false;
		showingModifiers = false;
		warnThresholdInSeconds = DEFAULT_WARN_THRESHOLD_IN_SECONDS;
		errorThresholdInSeconds = DEFAULT_ERROR_THRESHOLD_IN_SECONDS;
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

	public String getLoggerName()
	{
		return loggerName;
	}

	public void setLoggerName(String loggerName)
	{
		this.loggerName = loggerName;
	}

	public int getWarnThresholdInSeconds()
	{
		return warnThresholdInSeconds;
	}

	public void setWarnThresholdInSeconds(int warnThresholdInSeconds)
	{
		this.warnThresholdInSeconds = warnThresholdInSeconds;
	}

	public int getErrorThresholdInSeconds()
	{
		return errorThresholdInSeconds;
	}

	public void setErrorThresholdInSeconds(int errorThresholdInSeconds)
	{
		this.errorThresholdInSeconds = errorThresholdInSeconds;
	}

	public String toString()
	{
		return "TracingAspect{loggerName="+loggerName+", showingParameterValues="+ showingParameterValues +", usingShortClassName="+ usingShortClassName +", showingModifiers="+showingModifiers+", warnThresholdInSeconds=" + warnThresholdInSeconds + ", errorThresholdInSeconds=" + errorThresholdInSeconds + "}";
	}

	public Object trace(ProceedingJoinPoint call) throws Throwable
	{
		final Logger logger;
		if(loggerName != null)
		{
			logger = LoggerFactory.getLogger(loggerName);
		}
		else
		{
			logger = LoggerFactory.getLogger(TracingAspect.class);
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
			msg.append(Modifier.toString(signature.getModifiers())).append(" ");
		}
		if(usingShortClassName)
		{
			msg.append(clazz.getSimpleName());
		}
		else
		{
			msg.append(fullClassName);
		}
		msg.append(".").append(methodName);
		if(signature instanceof MethodSignature)
		{
			MethodSignature methodSignature=(MethodSignature)signature;
			msg.append("(");
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
					msg.append(SafeString.toString(arg));
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
			msg.append(")");
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
			profile(logger, methodSignatureString, nanoSeconds);
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
			profile(logger, methodSignatureString, nanoSeconds);
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

	private void profile(Logger logger, String message, long nanoSeconds)
	{
		if(nanoSeconds < 1000000)
		{
			if(logger.isTraceEnabled(PROFILE_MARKER)) logger.trace(PROFILE_MARKER, "{}ns - {}", nanoSeconds, message);
			return;
		}
		long milliSeconds = nanoSeconds / 1000000;
		if(milliSeconds < 1000)
		{
			if(logger.isDebugEnabled(PROFILE_MARKER)) logger.debug(PROFILE_MARKER, "{}ms - {}", milliSeconds, message);
			return;
		}
		long seconds = milliSeconds / 1000;
		if(seconds > errorThresholdInSeconds)
		{
			if(logger.isErrorEnabled(PROFILE_MARKER)) logger.error(PROFILE_MARKER, "{}ms - {}", milliSeconds, message);
			return;
		}
		if(seconds > warnThresholdInSeconds)
		{
			if(logger.isWarnEnabled(PROFILE_MARKER)) logger.warn(PROFILE_MARKER, "{}ms - {}", milliSeconds, message);
			return;
		}
		if(logger.isInfoEnabled(PROFILE_MARKER)) logger.info(PROFILE_MARKER, "{}ms - {}", milliSeconds, message);
	}
}
