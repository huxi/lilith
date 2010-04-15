/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

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
			if(statusManager.getLevel() == Status.ERROR)
			{
				List<Status> stati = statusManager.getCopyOfStatusList();
				String msg="Error while initializing layout! " + stati;
				if(logger.isErrorEnabled()) logger.error(msg);
				throw new IllegalStateException(msg);
			}
		}

	}

	private static class LoggingFoo
		implements ILoggingEvent
	{
		private LoggingEvent event;

		// TODO: finish
		
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
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		public IThrowableProxy getThrowableProxy()
		{
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		public StackTraceElement[] getCallerData()
		{
			return new StackTraceElement[0];  //To change body of implemented methods use File | Settings | File Templates.
		}

		public boolean hasCallerData()
		{
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		public Marker getMarker()
		{
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		public Map<String, String> getMDCPropertyMap()
		{
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		public long getTimeStamp()
		{
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		public void prepareForDeferredProcessing()
		{
			//To change body of implemented methods use File | Settings | File Templates.
		}
	}

}
