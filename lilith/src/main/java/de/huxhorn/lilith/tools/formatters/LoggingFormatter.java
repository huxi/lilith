/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.huxhorn.lilith.tools.formatters;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ClassPackagingData;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.lilith.logback.tools.ContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoggingFormatter
	implements Formatter<EventWrapper<LoggingEvent>>
{
	private final Logger logger = LoggerFactory.getLogger(LoggingFormatter.class);

	private static final String DEFAULT_PATTERN="%-5level [%thread]: %message%n";

	private ch.qos.logback.classic.PatternLayout layout;
	private String pattern;

	public String getPattern()
	{
		return pattern;
	}

	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	public String format(EventWrapper<LoggingEvent> wrapper)
	{
		initLayout();

		if(wrapper!=null)
		{
			LoggingEvent event = wrapper.getEvent();
			if(event != null)
			{
				LoggingFoo foo=new LoggingFoo(event);
				return layout.doLayout(foo);
			}
		}
		return null;
	}

	private void initLayout()
	{
		if(layout == null)
		{
			layout=new ch.qos.logback.classic.PatternLayout();
			Context context=new LoggerContext();
			layout.setContext(context);
			if(pattern != null)
			{
				layout.setPattern(pattern);
			}
			else
			{
				layout.setPattern(DEFAULT_PATTERN);
			}
			layout.start();
			StatusManager statusManager = context.getStatusManager();
			int statusLevel = ContextHelper.getHighestLevel(context);
			if(statusLevel > Status.INFO)
			{
				List<Status> stati = statusManager.getCopyOfStatusList();
				String msg="Error while initializing layout! " + stati;
				if(logger.isErrorEnabled()) logger.error(msg);
				throw new IllegalStateException(msg);
			}
		}

	}

	// suppress warning caused by implementing deprecated method
	@SuppressWarnings("deprecation")
	private static class LoggingFoo
		implements ILoggingEvent
	{
		private LoggingEvent event;

		public LoggingFoo(LoggingEvent event)
		{
			this.event=event;
		}

		public String getThreadName()
		{
			if(event != null)
			{
				ThreadInfo ti = event.getThreadInfo();
				if(ti != null)
				{
					return ti.getName();
				}
			}
			return null;
		}

		public Level getLevel()
		{
			if(event != null)
			{
				LoggingEvent.Level level = event.getLevel();
				if(level != null)
				{
					switch(level)
					{
						case TRACE:
							return ch.qos.logback.classic.Level.TRACE;
						case DEBUG:
							return ch.qos.logback.classic.Level.DEBUG;
						case INFO:
							return ch.qos.logback.classic.Level.INFO;
						case WARN:
							return ch.qos.logback.classic.Level.WARN;
						case ERROR:
							return ch.qos.logback.classic.Level.ERROR;
					}
				}
			}
			return null;
		}

		public String getMessage()
		{
			if(event != null)
			{
				Message message = event.getMessage();
				if(message != null)
				{
					return message.getMessagePattern();
				}
			}
			return null;
		}

		public Object[] getArgumentArray()
		{
			Message message = event.getMessage();
			if(message != null)
			{
				return message.getArguments();
			}
			return new Object[0];
		}

		public String getFormattedMessage()
		{
			if(event != null)
			{
				Message message = event.getMessage();
				if(message != null)
				{
					return message.getMessage();
				}
			}
			return null;
		}

		public String getLoggerName()
		{
			if(event != null)
			{
				return event.getLogger();
			}
			return null;
		}

		public LoggerContextVO getLoggerContextVO()
		{
			LoggerContextVO result=null;
			if(event != null)
			{
				de.huxhorn.lilith.data.eventsource.LoggerContext loggerContext = event.getLoggerContext();
				if(loggerContext != null)
				{
					result=new LoggerContextVO(loggerContext.getName(), loggerContext.getProperties(), loggerContext.getBirthTime());
				}
			}
			return result;
		}

		public IThrowableProxy getThrowableProxy()
		{
			if(event != null)
			{
				return ThrowableProxyFoo.convert(event.getThrowable());
			}
			return null;
		}

		public StackTraceElement[] getCallerData()
		{
			if(event != null)
			{
				ExtendedStackTraceElement[] callStack = event.getCallStack();
				if(callStack != null)
				{
					StackTraceElement[] result=new StackTraceElement[callStack.length];
					for(int i=0;i<callStack.length;i++)
					{
						result[i]=callStack[i].getStackTraceElement();
					}
					return result;
				}
			}
			return new StackTraceElement[0];
		}

		public boolean hasCallerData()
		{
			if(event != null)
			{
				ExtendedStackTraceElement[] callStack = event.getCallStack();

				return callStack!=null && callStack.length>0;
			}
			return false;
		}

		public Marker getMarker()
		{
			if(event != null)
			{
				Map<String, Marker> markerMap = new HashMap<>();
				return convert(event.getMarker(), markerMap);
			}
			return null;
		}

		public Map<String, String> getMDCPropertyMap()
		{
			if(event != null)
			{
				return event.getMdc();
			}
			return null;
		}

		public Map<String, String> getMdc()
		{
			if(event != null)
			{
				return event.getMdc();
			}
			return null;
		}

		public long getTimeStamp()
		{
			if(event != null)
			{
				Long timeStamp=event.getTimeStamp();
				if(timeStamp != null)
				{
					return timeStamp;
				}
			}
			return 0;
		}

		public void prepareForDeferredProcessing()
		{
		}

		private static Marker convert(de.huxhorn.lilith.data.logging.Marker originalMarker, Map<String, Marker> markerMap)
		{
			Marker result = null;
			if(originalMarker != null)
			{
				String originalName = originalMarker.getName();
				if(originalName != null)
				{
					result = markerMap.get(originalName);
					if(result == null)
					{
						result = MarkerFactory.getDetachedMarker(originalName);
						markerMap.put(originalName, result);

						Map<String,de.huxhorn.lilith.data.logging.Marker> references = originalMarker.getReferences();
						if(references != null)
						{
							for(Map.Entry<String, de.huxhorn.lilith.data.logging.Marker> current : references.entrySet())
							{
								String name = current.getKey();
								de.huxhorn.lilith.data.logging.Marker value = current.getValue();
								Marker marker = markerMap.get(name);
								if(marker == null)
								{
									marker = convert(value, markerMap);
								}
								result.add(marker);
							}
						}
					}
				}
			}
			return result;
		}
	}

	private static class ThrowableProxyFoo
		implements IThrowableProxy
	{
		private String message;
		private String className;
		private StackTraceElementProxy[] stackTraceElementProxyArray;
		private IThrowableProxy cause;
		private int commonFrames;
		private IThrowableProxy[] suppressed;

		public String getMessage()
		{
			return message;
		}

		public void setMessage(String message)
		{
			this.message = message;
		}

		public String getClassName()
		{
			return className;
		}

		public void setClassName(String className)
		{
			this.className = className;
		}

		public StackTraceElementProxy[] getStackTraceElementProxyArray()
		{
			return stackTraceElementProxyArray;
		}

		public void setStackTraceElementProxyArray(StackTraceElementProxy[] stackTraceElementProxyArray)
		{
			this.stackTraceElementProxyArray = stackTraceElementProxyArray;
		}

		public IThrowableProxy[] getSuppressed()
		{
			return suppressed;
		}

		public void setSuppressed(IThrowableProxy[] suppressed)
		{
			this.suppressed = suppressed;
		}

		public IThrowableProxy getCause()
		{
			return cause;
		}

		public void setCause(IThrowableProxy cause)
		{
			this.cause = cause;
		}

		public int getCommonFrames()
		{
			return commonFrames;
		}

		public void setCommonFrames(int commonFrames)
		{
			this.commonFrames = commonFrames;
		}

		private static ThrowableProxyFoo convert(ThrowableInfo throwableInfo)
		{
			ThrowableProxyFoo result=null;
			if(throwableInfo != null)
			{
				result = new ThrowableProxyFoo();
				result.setMessage(throwableInfo.getMessage());
				result.setClassName(throwableInfo.getName());
				result.setCommonFrames(throwableInfo.getOmittedElements());
				result.setStackTraceElementProxyArray(convert(throwableInfo.getStackTrace()));
				ThrowableInfo[] throwableInfoSuppressed = throwableInfo.getSuppressed();
				if(throwableInfoSuppressed != null)
				{
					IThrowableProxy[] throwableProxySuppressed=new IThrowableProxy[throwableInfoSuppressed.length];
					for(int i=0;i<throwableInfoSuppressed.length;i++)
					{
						throwableProxySuppressed[i] = convert(throwableInfoSuppressed[i]);
					}
					result.setSuppressed(throwableProxySuppressed);
				}
				result.setCause(convert(throwableInfo.getCause()));
			}
			return result;
		}

		private static StackTraceElementProxy[] convert(ExtendedStackTraceElement[] stackTrace)
		{
			StackTraceElementProxy[] result = null;
			if(stackTrace != null)
			{
				result = new StackTraceElementProxy[stackTrace.length];
				for(int i=0;i<stackTrace.length;i++)
				{
					ExtendedStackTraceElement current = stackTrace[i];
					if(current == null)
					{
						continue;
					}
					StackTraceElement ste = current.getStackTraceElement();
					if(ste == null)
					{
						continue;
					}
					result[i] = new StackTraceElementProxy(ste);
					String codeLocation=current.getCodeLocation();
					String version=current.getVersion();
					if(codeLocation != null || version != null)
					{
						boolean exact = current.isExact();
						ClassPackagingData cpd=new ClassPackagingData(codeLocation, version, exact);
						result[i].setClassPackagingData(cpd);
					}
				}
			}
			return result;
		}
	}
}
