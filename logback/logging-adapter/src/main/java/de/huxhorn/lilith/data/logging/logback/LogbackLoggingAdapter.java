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
package de.huxhorn.lilith.data.logging.logback;

import ch.qos.logback.classic.spi.*;
import de.huxhorn.lilith.data.logging.*;
import de.huxhorn.lilith.data.logging.LoggingEvent;

import java.util.*;

public class LogbackLoggingAdapter
{
	private static final String CLASSNAME_MESSAGE_SEPARATOR = ": ";
    private static final String COMMON_FRAMES_PREFIX = "\t... ";
	private static final String COMMON_FRAMES_OMITTED = " common frames omitted";
	private static final String NATIVE_METHOD = "Native Method";
	private static final String UNKNOWN_SOURCE = "Unknown Source";
    private static final String CAUSED_BY = "Caused by: ";

    /*
	private static final Field throwableField;

	static
	{
		Field field=null;
		try
		{
			Class clazz=Class.forName("ch.qos.logback.classic.spi.ThrowableInformation");
			field=clazz.getDeclaredField("throwable");
			field.setAccessible(true);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		throwableField=field;
	}
    */

	public LoggingEvent convert(ch.qos.logback.classic.spi.LoggingEvent event)
	{
		if(event == null)
		{
			return null;
		}
		LoggingEvent result=new LoggingEvent();
		String messagePattern=event.getMessage();
		result.setMessagePattern(messagePattern);
		Object[] originalArguments = event.getArgumentArray();
		MessageFormatter.ArgumentResult argumentResult = 
				MessageFormatter.evaluateArguments(messagePattern, originalArguments);

		if(argumentResult!=null)
		{
			result.setArguments(argumentResult.getArguments());
			Throwable t=argumentResult.getThrowable();
			if(t!=null && event.getThrowableProxy() == null)
			{
                event.setThrowableProxy(new ThrowableProxy(t));
			}
		}
        initThrowableFromEvent(event, result);

		initCallStack(event, result);
		result.setLevel(LoggingEvent.Level.valueOf(event.getLevel().toString()));
		result.setLogger(event.getLoggerRemoteView().getName());
		initMarker(event, result);
		result.setMdc(event.getMDCPropertyMap());

		result.setThreadName(event.getThreadName());
		result.setTimeStamp(new Date(event.getTimeStamp()));
		return result;
	}


	private void initThrowableFromEvent(ch.qos.logback.classic.spi.LoggingEvent src, LoggingEvent dst)
	{
		ThrowableProxy ti = src.getThrowableProxy();
		if(ti==null)
		{
			return;
		}
        ti.calculatePackagingData(); // TODO: configurable

		initFromThrowableDataPoints(ti, dst);
	}

	private ExtendedStackTraceElement[] convert(StackTraceElement[] stackTrace)
	{
		if(stackTrace==null)
		{
			return null;
		}
		ExtendedStackTraceElement[] result=new ExtendedStackTraceElement[stackTrace.length];
		for(int i=0;i<stackTrace.length;i++)
		{
			result[i]=new ExtendedStackTraceElement(stackTrace[i]);
		}
		return result;
	}

	void initFromThrowableDataPoints(ThrowableProxy ti, LoggingEvent dst)
	{
		ThrowableDataPoint[] throwStrRep = ti.getThrowableDataPointArray();
		if(throwStrRep==null)
		{
			return;
		}
		dst.setThrowable(initFromThrowableDataPointsRecursive(throwStrRep, 0));
	}

	ThrowableInfo initFromThrowableDataPointsRecursive(ThrowableDataPoint[] throwStrRep, int index)
	{
		if(index >= throwStrRep.length)
		{
			return null;
		}
		ThrowableDataPoint currentDataPoint=throwStrRep[index];

        String current=currentDataPoint.toString();
		if(current.startsWith(CAUSED_BY))
		{
			current=current.substring(CAUSED_BY.length());
		}
		int colonIdx=current.indexOf(CLASSNAME_MESSAGE_SEPARATOR);
		ThrowableInfo result=new ThrowableInfo();
		if(colonIdx==-1)
		{
			result.setName(current);
		}
		else
		{
			result.setName(current.substring(0,colonIdx));
			result.setMessage(current.substring(colonIdx+CLASSNAME_MESSAGE_SEPARATOR.length()));
		}
		index++;

		ArrayList<ExtendedStackTraceElement> stackElements= new ArrayList<ExtendedStackTraceElement>();
		for(int i=index;i<throwStrRep.length;i++)
		{
            ThrowableDataPoint dataPoint = throwStrRep[i];
            ThrowableDataPoint.ThrowableDataPointType type=dataPoint.getType();
            if(type == ThrowableDataPoint.ThrowableDataPointType.RAW)
            {
                String raw=throwStrRep[i].toString();
                if(raw.startsWith(CAUSED_BY))
                {
                    result.setStackTrace(stackElements.toArray(new ExtendedStackTraceElement[stackElements.size()]));
                    stackElements.clear();
                    result.setCause(initFromThrowableDataPointsRecursive(throwStrRep, i));
                    break;
                }
                // else
                if(raw.endsWith(COMMON_FRAMES_OMITTED))
                {
                    // we ignore this...
                    String omittedElementsStr = raw.substring(COMMON_FRAMES_PREFIX.length(), raw.length() - COMMON_FRAMES_OMITTED.length());

                    int omittedElements=0;
                    try
                    {
                        omittedElements=Integer.parseInt(omittedElementsStr);
                    }
                    catch(NumberFormatException ex)
                    {
                        // ignore
                    }
                    result.setOmittedElements(omittedElements);
                    continue;
                }
            }

			// else
			stackElements.add(parseStac(dataPoint.getStackTraceElementProxy()));
		}
		return result;
	}

    public static ExtendedStackTraceElement parseStac(StackTraceElementProxy proxy)
    {
        if(proxy == null)
        {
            return null;
        }
        ExtendedStackTraceElement result=new ExtendedStackTraceElement();
        StackTraceElement ste = proxy.getStackTraceElement();
        result.setClassName(ste.getClassName());
        result.setMethodName(ste.getMethodName());
        result.setLineNumber(ste.getLineNumber());
        result.setFileName(ste.getFileName());

        ClassPackagingData cpd = proxy.getClassPackagingData();
        if(cpd!=null)
        {
            result.setCodeLocation(cpd.getCodeLocation());
            result.setVersion(cpd.getVersion());
            result.setExact(cpd.isExact());
        }
        return result;
    }

	public static ExtendedStackTraceElement parseStackTraceElement(String current)
	{
		int idx=current.lastIndexOf("(");
		String classAndMethod=current.substring(0, idx);
		//System.out.println("classAndMethod:"+ classAndMethod);
		String source=current.substring(idx+1, current.length()-1);
		//System.out.println("source:"+ source);
		idx=classAndMethod.lastIndexOf(".");
		String clazz=classAndMethod.substring(0, idx);
		String method=classAndMethod.substring(idx+1, classAndMethod.length());
		//System.out.println("clazz:"+ clazz);
		//System.out.println("method:"+ method);
		idx=source.lastIndexOf(":");
		String file=null;
		int lineNumber=-1;
		if(idx!=-1)
		{
			file=source.substring(0, idx);
			lineNumber=Integer.parseInt(source.substring(idx+1, source.length()));
		}
		else
		{
			if(source.equals(NATIVE_METHOD))
			{
				lineNumber = ExtendedStackTraceElement.NATIVE_METHOD;
			}
			else if(!source.equals(UNKNOWN_SOURCE))
			{
				file=source;
			}
		}
        // TODO: add support for codeLocation, version and exact as soon as logback 0.9.10 is released.
		return new ExtendedStackTraceElement(clazz, method, file, lineNumber);
	}

	private void initCallStack(ch.qos.logback.classic.spi.LoggingEvent src, LoggingEvent dst)
	{
		CallerData[] cd = src.getCallerData();
		if(cd==null)
		{
			return;
		}
		StackTraceElement[] callStack=new StackTraceElement[cd.length];
		for(int i=0;i<cd.length;i++)
		{
			CallerData current=cd[i];
			int lineNumber=current.getLineNumber();
			if(current.isNativeMethod())
			{
				lineNumber = ExtendedStackTraceElement.NATIVE_METHOD;
			}
			callStack[i]=new StackTraceElement(current.getClassName(),current.getMethodName(), current.getFileName(), lineNumber);
		}
		dst.setCallStack(convert(callStack));
	}

	private void initMarker(ch.qos.logback.classic.spi.LoggingEvent src, LoggingEvent dst)
	{
		org.slf4j.Marker origMarker = src.getMarker();
		if(origMarker==null)
		{
			return;
		}
		Map<String, Marker> markers=new HashMap<String, Marker>();
		dst.setMarker(initMarkerRecursive(origMarker, markers));
	}

	private Marker initMarkerRecursive(org.slf4j.Marker origMarker, Map<String, Marker> markers)
	{
		if(origMarker==null)
		{
			return null;
		}
		String name=origMarker.getName();
		if(markers.containsKey(name))
		{
			return markers.get(name);
		}
		Marker newMarker=new Marker(name);
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
}