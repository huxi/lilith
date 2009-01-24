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
package de.huxhorn.lilith.data.logging;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>Replacement for ch.qos.logback.classic.spi.LoggingEvent</p>
 * <p/>
 * <p>This class has been implemented for optimized serialization (size) and creation (speed).</p>
 * <p/>
 * <p>Only the unformatted message pattern and the argument array are serialized. The formatted message is (re)created
 * lazily on demand.</p>
 * <p/>
 * <p>Additionally, a LoggingEvent can contain an optional applicationId so it's possible to recognize the application
 * the events are originating from. This is extremely useful if more than one application is running on the same
 * host.</p>
 */
public class LoggingEvent
	implements Serializable
{
	private static final long serialVersionUID = 2176672034350830417L;

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

	private ThrowableInfo throwable;
	private Map<String, String> mdc;
	private List<Message> ndc;
	private Marker marker;
	private ExtendedStackTraceElement[] callStack;
	private String applicationIdentifier;
	private Message message;

	public LoggingEvent()
	{
		message = new Message();
	}

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

	public String getMessagePattern()
	{
		return message.getMessagePattern();
	}

	public void setMessagePattern(String messagePattern)
	{
		message.setMessagePattern(messagePattern);
	}

	public String getMessage()
	{
		return message.getMessage();
	}

	public String[] getArguments()
	{
		return message.getArguments();
	}

	public void setArguments(String[] arguments)
	{
		message.setArguments(arguments);
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

	public List<Message> getNdc()
	{
		return ndc;
	}

	public void setNdc(List<Message> ndc)
	{
		this.ndc = ndc;
	}

	public Marker getMarker()
	{
		return marker;
	}

	public void setMarker(Marker marker)
	{
		this.marker = marker;
	}

	public ExtendedStackTraceElement[] getCallStack()
	{
		return callStack;
	}

	public void setCallStack(ExtendedStackTraceElement[] callStack)
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
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		LoggingEvent event = (LoggingEvent) o;

		if(level != event.level) return false;
		if(timeStamp != null ? !timeStamp.equals(event.timeStamp) : event.timeStamp != null) return false;
		if(logger != null ? !logger.equals(event.logger) : event.logger != null) return false;
		if(applicationIdentifier != null ? !applicationIdentifier
			.equals(event.applicationIdentifier) : event.applicationIdentifier != null)
		{
			return false;
		}
		if(message != null ? !message.equals(event.message) : event.message != null) return false;
		if(threadName != null ? !threadName.equals(event.threadName) : event.threadName != null) return false;
		if(!Arrays.equals(callStack, event.callStack)) return false;
		if(marker != null ? !marker.equals(event.marker) : event.marker != null) return false;
		if(mdc != null ? !mdc.equals(event.mdc) : event.mdc != null) return false;
		if(ndc != null ? !ndc.equals(event.ndc) : event.ndc != null) return false;
		if(throwable != null ? !throwable.equals(event.throwable) : event.throwable != null) return false;

		return true;
	}

	public int hashCode()
	{
		int result;
		result = (logger != null ? logger.hashCode() : 0);
		result = 31 * result + (level != null ? level.hashCode() : 0);
		result = 31 * result + (message != null ? message.hashCode() : 0);
		result = 31 * result + (timeStamp != null ? timeStamp.hashCode() : 0);
		result = 31 * result + (threadName != null ? threadName.hashCode() : 0);
		result = 31 * result + (applicationIdentifier != null ? applicationIdentifier.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
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