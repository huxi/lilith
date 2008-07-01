/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
package de.huxhorn.lilith.data.logging;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Arrays;

public class LoggingEvent
	implements Serializable
{
	private static final long serialVersionUID = 8733590043979572340L;

	public enum Level
	{
		TRACE,
		DEBUG,
		INFO,
		WARN,
		ERROR
	}

	private String logger;
	private Level level;
	private String threadName;
	private Date timeStamp;
	private String message;
	private String[] arguments;
	private ThrowableInfo throwable;
	private Map<String,String> mdc;
	private Marker marker;
	private StackTraceElement[] callStack;
	private String applicationIdentifier;

	public String getLogger()
	{
		return logger;
	}

	public void setLogger(String logger)
	{
		this.logger = logger;
	}

	public String getThreadName()
	{
		return threadName;
	}

	public void setThreadName(String threadName)
	{
		this.threadName = threadName;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String[] getArguments()
	{
		return arguments;
	}

	public void setArguments(String[] arguments)
	{
		this.arguments = arguments;
	}

	public Date getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp)
	{
		this.timeStamp = timeStamp;
	}

	public Level getLevel()
	{
		return level;
	}

	public void setLevel(Level level)
	{
		this.level = level;
	}

	public ThrowableInfo getThrowable()
	{
		return throwable;
	}

	public void setThrowable(ThrowableInfo throwable)
	{
		this.throwable = throwable;
	}

	public Map<String, String> getMdc()
	{
		return mdc;
	}

	public void setMdc(Map<String, String> mdc)
	{
		this.mdc = mdc;
	}

	public Marker getMarker()
	{
		return marker;
	}

	public void setMarker(Marker marker)
	{
		this.marker = marker;
	}

	public StackTraceElement[] getCallStack()
	{
		return callStack;
	}

	public void setCallStack(StackTraceElement[] callStack)
	{
		this.callStack = callStack;
	}

	public String getApplicationIdentifier()
	{
		return applicationIdentifier;
	}

	public void setApplicationIdentifier(String applicationIdentifier)
	{
		this.applicationIdentifier = applicationIdentifier;
	}

	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LoggingEvent event = (LoggingEvent) o;

		if (applicationIdentifier != null ? !applicationIdentifier.equals(event.applicationIdentifier) : event.applicationIdentifier != null)
			return false;
		if (!Arrays.equals(arguments, event.arguments)) return false;
		if (!Arrays.equals(callStack, event.callStack)) return false;
		if (level != event.level) return false;
		if (logger != null ? !logger.equals(event.logger) : event.logger != null) return false;
		if (marker != null ? !marker.equals(event.marker) : event.marker != null) return false;
		if (mdc != null ? !mdc.equals(event.mdc) : event.mdc != null) return false;
		if (message != null ? !message.equals(event.message) : event.message != null) return false;
		if (threadName != null ? !threadName.equals(event.threadName) : event.threadName != null) return false;
		if (throwable != null ? !throwable.equals(event.throwable) : event.throwable != null) return false;
		if (timeStamp != null ? !timeStamp.equals(event.timeStamp) : event.timeStamp != null) return false;

		return true;
	}

	public int hashCode()
	{
		int result;
		result = (logger != null ? logger.hashCode() : 0);
		result = 31 * result + (level != null ? level.hashCode() : 0);
		result = 31 * result + (threadName != null ? threadName.hashCode() : 0);
		result = 31 * result + (timeStamp != null ? timeStamp.hashCode() : 0);
		result = 31 * result + (message != null ? message.hashCode() : 0);
		result = 31 * result + (arguments != null ? Arrays.hashCode(arguments) : 0);
		result = 31 * result + (throwable != null ? throwable.hashCode() : 0);
		result = 31 * result + (mdc != null ? mdc.hashCode() : 0);
		result = 31 * result + (marker != null ? marker.hashCode() : 0);
		result = 31 * result + (callStack != null ? Arrays.hashCode(callStack) : 0);
		result = 31 * result + (applicationIdentifier != null ? applicationIdentifier.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		StringBuffer result=new StringBuffer();
		result.append("LoggingEvent[");
		result.append("logger=").append(logger).append(", ");
		result.append("level=").append(level).append(", ");
		result.append("message=").append(message).append(", ");
		result.append("threadName=").append(threadName).append(", ");
		result.append("applicationIdentifier=").append(applicationIdentifier).append(", ");
		result.append("timeStamp=").append(timeStamp);

		result.append("]");
		return result.toString();
	}
}