package de.huxhorn.lilith.data.logging.logback;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.ThrowableInfo;

import java.util.Date;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.reflect.Field;

import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ThrowableInformation;
import ch.qos.logback.classic.ClassicGlobal;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.MessageFormatter;

public class LogbackLoggingAdapter
{
	private static final String CLASSNAME_MESSAGE_SEPARATOR = ": ";
	private static final String COMMON_FRAMES_OMITTED = " common frames omitted";
	private static final String NATIVE_METHOD = "Native Method";
	private static final String UNKNOWN_SOURCE = "Unknown Source";

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

		boolean throwableInitialized=false;
		if(argumentResult!=null)
		{
			result.setArguments(argumentResult.getArguments());
			Throwable t=argumentResult.getThrowable();
			if(t!=null)
			{
				result.setThrowable(initFromThrowableRecursive(t));
				throwableInitialized=true;
			}
		}
		if(!throwableInitialized)
		{
			// to catch exceptions given using special methods in logback.
			initThrowable(event, result);
		}

		initCallStack(event, result);
		result.setLevel(LoggingEvent.Level.valueOf(event.getLevel().toString()));
		result.setLogger(event.getLoggerRemoteView().getName());
		initMarker(event, result);
		result.setMdc(event.getMDCPropertyMap());

		result.setThreadName(event.getThreadName());
		result.setTimeStamp(new Date(event.getTimeStamp()));
		return result;
	}


	/*
	private void initArguments(ch.qos.logback.classic.spi.LoggingEvent src, LoggingEvent dst)
	{
		Object[] origArgs=src.getArgumentArray();
		if(origArgs==null)
		{
			return;
		}
		String[] args=new String[origArgs.length];
		for(int i=0;i<origArgs.length;i++)
		{
			if(origArgs[i]!=null)
			{
				if(origArgs[i] instanceof String)
				{
					args[i]=(String) origArgs[i];
				}
				else
				{
					args[i]=origArgs[i].toString();
				}
			}
		}

		dst.setArguments(args);
	}
    */

	private void initThrowable(ch.qos.logback.classic.spi.LoggingEvent src, LoggingEvent dst)
	{
		ThrowableInformation ti = src.getThrowableInformation();
		if(ti==null)
		{
			return;
		}
		if(!initFromThrowable(ti, dst))
		{
			initFromThrowableStrRep(ti, dst);
		}
	}

	// not private because of testcase
	Throwable getThrowable(ThrowableInformation ti)
	{
		if(throwableField==null)
		{
			return null;
		}
		try
		{
			return (Throwable) throwableField.get(ti);
		}
		catch (IllegalAccessException e)
		{
			// ignore
		}
		return null;
	}

	private boolean initFromThrowable(ThrowableInformation ti, LoggingEvent dst)
	{
		Throwable t=getThrowable(ti);
		if(t==null)
		{
			return false;
		}
		dst.setThrowable(initFromThrowableRecursive(t));
		return true;
	}

	private ThrowableInfo initFromThrowableRecursive(Throwable t)
	{
		if(t==null)
		{
			return null;
		}
		ThrowableInfo info=new ThrowableInfo();
		info.setName(t.getClass().getName());
		info.setMessage(t.getMessage());
		info.setStackTrace(t.getStackTrace());
		info.setCause(initFromThrowableRecursive(t.getCause()));

		return info;
	}

	void initFromThrowableStrRep(ThrowableInformation ti, LoggingEvent dst)
	{
		String[] throwStrRep = ti.getThrowableStrRep();
		if(throwStrRep==null)
		{
			return;
		}
		dst.setThrowable(initFromThrowableStrRepRecursive(throwStrRep, 0));
	}

	ThrowableInfo initFromThrowableStrRepRecursive(String[] throwStrRep, int index)
	{
		if(index >= throwStrRep.length)
		{
			return null;
		}
		String current=throwStrRep[index];
		if(current.startsWith(ClassicGlobal.CAUSED_BY))
		{
			current=current.substring(ClassicGlobal.CAUSED_BY.length());
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
		ArrayList<StackTraceElement> stackElements=new ArrayList<StackTraceElement>();
		for(int i=index;i<throwStrRep.length;i++)
		{
			current=throwStrRep[i];
			if(current.startsWith(ClassicGlobal.CAUSED_BY))
			{
				result.setStackTrace(stackElements.toArray(new StackTraceElement[stackElements.size()]));
				stackElements.clear();
				result.setCause(initFromThrowableStrRepRecursive(throwStrRep, i));
				break;
			}
			// else
			if(current.endsWith(COMMON_FRAMES_OMITTED))
			{
				// we ignore this...
				continue;
			}
			// else
			/*
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
					lineNumber = ThrowableInfo.MAGIC_NATIVE_LINE_NUMBER;
				}
				else if(!source.equals(UNKNOWN_SOURCE))
				{
					file=source;
				}
			}
			StackTraceElement newSTE = new StackTraceElement(clazz, method, file, lineNumber);
			*/
			stackElements.add(parseStackTraceElement(current));
			//System.out.println("Original: "+current);
			//System.out.println("Parsed  : "+newSTE);
		}
		return result;
	}

	public static StackTraceElement parseStackTraceElement(String current)
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
				lineNumber = ThrowableInfo.MAGIC_NATIVE_LINE_NUMBER;
			}
			else if(!source.equals(UNKNOWN_SOURCE))
			{
				file=source;
			}
		}
		return new StackTraceElement(clazz, method, file, lineNumber);
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
				lineNumber = ThrowableInfo.MAGIC_NATIVE_LINE_NUMBER;
			}
			callStack[i]=new StackTraceElement(current.getClassName(),current.getMethodName(), current.getFileName(), lineNumber);
		}
		dst.setCallStack(callStack);
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
		if(origMarker.hasChildren())
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