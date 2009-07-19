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
import de.huxhorn.lilith.logback.classic.NDC;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LogbackLoggingAdapter {
    public LoggingEvent convert(ch.qos.logback.classic.spi.ILoggingEvent event, boolean inSameThread) {
        if (event == null) {
            return null;
        }
        LoggingEvent result = new LoggingEvent();
        String messagePattern = event.getMessage();

        Object[] originalArguments = event.getArgumentArray();
        MessageFormatter.ArgumentResult argumentResult =
                MessageFormatter.evaluateArguments(messagePattern, originalArguments);

        String[] arguments = null;
        if (argumentResult != null) {
            arguments = argumentResult.getArguments();
            Throwable t = argumentResult.getThrowable();
            if (t != null && event.getThrowableProxy() == null && event instanceof ch.qos.logback.classic.spi.LoggingEvent) {
                ch.qos.logback.classic.spi.LoggingEvent le = (ch.qos.logback.classic.spi.LoggingEvent) event;
                le.setThrowableProxy(new ThrowableProxy(t));
            }
        }
        if (messagePattern != null || arguments != null) {
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
        if (lcv != null) {
            String name = lcv.getName();
            Map<String, String> props = lcv.getPropertyMap();
            if (props != null) {
                // TODO: lcv property map leak? yes, indeed. See http://jira.qos.ch/browse/LBCLASSIC-115
                props = new HashMap<String, String>(props);
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

        if (threadName != null) {
            Long threadId = null;
            String threadGroupName = null;
            Long threadGroupId = null;

            if (inSameThread) {
                // assuming this code is executed synchronously
                Thread t = Thread.currentThread();
                threadId = t.getId();

                ThreadGroup tg = t.getThreadGroup();
                if (tg != null) {
                    threadGroupName = tg.getName();
                    threadGroupId = (long) System.identityHashCode(tg);
                }
            }
            ThreadInfo threadInfo = new ThreadInfo(threadId, threadName, threadGroupId, threadGroupName);
            result.setThreadInfo(threadInfo);
        }
        result.setTimeStamp(event.getTimeStamp());

        if (inSameThread) {
            if (!NDC.isEmpty()) {
                // TODO: configurable NDC evaluation
                result.setNdc(NDC.getContextStack());
            }
        }

        return result;
    }


    private ExtendedStackTraceElement[] convert(StackTraceElement[] stackTrace) {
        if (stackTrace == null) {
            return null;
        }
        ExtendedStackTraceElement[] result = new ExtendedStackTraceElement[stackTrace.length];
        for (int i = 0; i < stackTrace.length; i++) {
            result[i] = new ExtendedStackTraceElement(stackTrace[i]);
        }
        return result;
    }

    ThrowableInfo initFromThrowableProxy(IThrowableProxy ti, boolean calculatePackagingData) {
        if (ti == null) {
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
        result.setCause(initFromThrowableProxy(ti.getCause(), calculatePackagingData));
        return result;
    }

    private ExtendedStackTraceElement[] initFromStackTraceElementProxyArray(StackTraceElementProxy[] stackTraceElementProxies) {
        if (stackTraceElementProxies == null) {
            return null;
        }
        int elementCount = stackTraceElementProxies.length;
        ExtendedStackTraceElement[] result = new ExtendedStackTraceElement[elementCount];
        for (int i = 0; i < elementCount; i++) {
            StackTraceElementProxy currentInput = stackTraceElementProxies[i];
            if (currentInput != null) {
                ExtendedStackTraceElement current = new ExtendedStackTraceElement(currentInput.getStackTraceElement());
                ClassPackagingData cpd = currentInput.getClassPackagingData();
                if (cpd != null) {
                    current.setCodeLocation(cpd.getCodeLocation());
                    current.setExact(cpd.isExact());
                    current.setVersion(cpd.getVersion());
                }
                result[i] = current;
            }
        }
        return result;
    }

    private void initMarker(ch.qos.logback.classic.spi.ILoggingEvent src, LoggingEvent dst) {
        org.slf4j.Marker origMarker = src.getMarker();
        if (origMarker == null) {
            return;
        }
        Map<String, Marker> markers = new HashMap<String, Marker>();
        dst.setMarker(initMarkerRecursive(origMarker, markers));
    }

    private Marker initMarkerRecursive(org.slf4j.Marker origMarker, Map<String, Marker> markers) {
        if (origMarker == null) {
            return null;
        }
        String name = origMarker.getName();
        if (markers.containsKey(name)) {
            return markers.get(name);
        }
        Marker newMarker = new Marker(name);
        markers.put(name, newMarker);
        if (origMarker.hasReferences()) {
            Iterator iter = origMarker.iterator();
            while (iter.hasNext()) {
                org.slf4j.Marker current = (org.slf4j.Marker) iter.next();
                newMarker.add(initMarkerRecursive(current, markers));
            }
        }
        return newMarker;
    }
}
