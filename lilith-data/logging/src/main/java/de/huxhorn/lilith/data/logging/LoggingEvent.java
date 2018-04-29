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

package de.huxhorn.lilith.data.logging;

import de.huxhorn.lilith.data.eventsource.LoggerContext;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 * <p>Replacement for ch.qos.logback.classic.spi.LoggingEvent</p>
 *
 * <p>This class has been implemented for optimized serialization (size) and creation (speed).</p>
 *
 * <p>Only the unformatted message pattern and the argument array are serialized. The formatted message is (re)created
 * lazily on demand.</p>
 *
 * <p>Additionally, a LoggingEvent can contain an optional applicationId so it's possible to recognize the application
 * the events are originating from. This is extremely useful if more than one application is running on the same
 * host.</p>
 */
@SuppressWarnings({"PMD.MethodReturnsInternalArray", "PMD.ArrayIsStoredDirectly"})
public class LoggingEvent
	implements Serializable
{
	private static final long serialVersionUID = -2135999771611827603L;

	public enum Level
	{
		TRACE,
		DEBUG,
		INFO,
		WARN,
		ERROR
	}

	private Level level;
	private Message message;
	private String logger;
	private ThrowableInfo throwable;
	private ExtendedStackTraceElement[] callStack;
	private Map<String, String> mdc;
	private Message[] ndc;
	private Marker marker;
	private ThreadInfo threadInfo;
	private LoggerContext loggerContext;
	private Long sequenceNumber;
	private Long timeStamp;

	public String getLogger()
	{
		return logger;
	}

	public void setLogger(String logger)
	{
		this.logger = logger;
	}

	public ThreadInfo getThreadInfo()
	{
		return threadInfo;
	}

	public void setThreadInfo(ThreadInfo threadInfo)
	{
		this.threadInfo = threadInfo;
	}

	public Long getSequenceNumber()
	{
		return sequenceNumber;
	}

	public void setSequenceNumber(Long sequenceNumber)
	{
		this.sequenceNumber = sequenceNumber;
	}

	public LoggerContext getLoggerContext()
	{
		return loggerContext;
	}

	public void setLoggerContext(LoggerContext loggerContext)
	{
		this.loggerContext = loggerContext;
	}

	public Message getMessage()
	{
		return message;
	}

	public void setMessage(Message message)
	{
		this.message = message;
	}

	public Long getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStamp(Long timeStamp)
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

	public Message[] getNdc()
	{
		return ndc;
	}

	public void setNdc(Message[] ndc)
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

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LoggingEvent event = (LoggingEvent) o;

		return level == event.level
				&& (sequenceNumber != null ? sequenceNumber.equals(event.sequenceNumber) : event.sequenceNumber == null)
				&& (timeStamp != null ? timeStamp.equals(event.timeStamp) : event.timeStamp == null)
				&& (logger != null ? logger.equals(event.logger) : event.logger == null)
				&& (loggerContext != null ? loggerContext.equals(event.loggerContext) : event.loggerContext == null)
				&& (message != null ? message.equals(event.message) : event.message == null)
				&& (threadInfo != null ? threadInfo.equals(event.threadInfo) : event.threadInfo == null)
				&& Arrays.equals(callStack, event.callStack)
				&& (marker != null ? marker.equals(event.marker) : event.marker == null)
				&& (mdc != null ? mdc.equals(event.mdc) : event.mdc == null)
				&& Arrays.equals(ndc, event.ndc)
				&& (throwable != null ? throwable.equals(event.throwable) : event.throwable == null);
	}

	@Override
	public int hashCode()
	{
		int result;
		result = (logger != null ? logger.hashCode() : 0);
		result = 31 * result + (sequenceNumber != null ? sequenceNumber.hashCode() : 0);
		result = 31 * result + (level != null ? level.hashCode() : 0);
		result = 31 * result + (message != null ? message.hashCode() : 0);
		result = 31 * result + (timeStamp != null ? timeStamp.hashCode() : 0);
		result = 31 * result + (threadInfo != null ? threadInfo.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return "LoggingEvent{" +
				"logger='" + logger + '\'' +
				", level=" + level +
				", message=" + message +
				", throwable=" + throwable +
				", callStack=" + Arrays.toString(callStack) +
				", mdc=" + mdc +
				", ndc=" + Arrays.toString(ndc) +
				", marker=" + marker +
				", threadInfo=" + threadInfo +
				", loggerContext=" + loggerContext +
				", sequenceNumber=" + sequenceNumber +
				", timeStamp=" + timeStamp +
				'}';
	}
}
