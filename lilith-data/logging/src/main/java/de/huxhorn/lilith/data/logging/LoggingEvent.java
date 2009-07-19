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
        implements Serializable {
    private static final long serialVersionUID = -5298580552977499507L;

    public enum Level {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    private String logger;
    private Level level;
    private ThreadInfo threadInfo;
    private LoggerContext loggerContext;
    private Long timeStamp;
    private Long sequenceNumber;

    private ThrowableInfo throwable;
    private Map<String, String> mdc;
    private Message[] ndc;
    private Marker marker;
    private ExtendedStackTraceElement[] callStack;
    private Message message;

    public LoggingEvent() {
    }

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public ThreadInfo getThreadInfo() {
        return threadInfo;
    }

    public void setThreadInfo(ThreadInfo threadInfo) {
        this.threadInfo = threadInfo;
    }

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public LoggerContext getLoggerContext() {
        return loggerContext;
    }

    public void setLoggerContext(LoggerContext loggerContext) {
        this.loggerContext = loggerContext;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public ThrowableInfo getThrowable() {
        return throwable;
    }

    public void setThrowable(ThrowableInfo throwable) {
        this.throwable = throwable;
    }

    public Map<String, String> getMdc() {
        return mdc;
    }

    public void setMdc(Map<String, String> mdc) {
        this.mdc = mdc;
    }

    public Message[] getNdc() {
        return ndc;
    }

    public void setNdc(Message[] ndc) {
        this.ndc = ndc;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public ExtendedStackTraceElement[] getCallStack() {
        return callStack;
    }

    public void setCallStack(ExtendedStackTraceElement[] callStack) {
        this.callStack = callStack;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoggingEvent event = (LoggingEvent) o;

        if (level != event.level) return false;
        if (sequenceNumber != null ? !sequenceNumber.equals(event.sequenceNumber) : event.sequenceNumber != null) {
            return false;
        }
        if (timeStamp != null ? !timeStamp.equals(event.timeStamp) : event.timeStamp != null) return false;
        if (logger != null ? !logger.equals(event.logger) : event.logger != null) return false;
        if (loggerContext != null ? !loggerContext
                .equals(event.loggerContext) : event.loggerContext != null) {
            return false;
        }
        if (message != null ? !message.equals(event.message) : event.message != null) return false;
        if (threadInfo != null ? !threadInfo.equals(event.threadInfo) : event.threadInfo != null) return false;
        if (!Arrays.equals(callStack, event.callStack)) return false;
        if (marker != null ? !marker.equals(event.marker) : event.marker != null) return false;
        if (mdc != null ? !mdc.equals(event.mdc) : event.mdc != null) return false;
        if (!Arrays.equals(ndc, event.ndc)) return false;
        if (throwable != null ? !throwable.equals(event.throwable) : event.throwable != null) return false;

        return true;
    }

    public int hashCode() {
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
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("LoggingEvent[");
        result.append("logger=").append(logger).append(", ");
        result.append("level=").append(level).append(", ");
        result.append("message=").append(message).append(", ");
        result.append("threadInfo=").append(threadInfo).append(", ");
        result.append("loggerContext=").append(loggerContext).append(", ");
        result.append("sequenceNumber=").append(sequenceNumber).append(", ");
        result.append("timeStamp=").append(timeStamp);

        result.append("]");
        return result.toString();
    }
}
