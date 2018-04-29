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

package de.huxhorn.lilith.slf4j.impl;

import de.huxhorn.lilith.slf4j.Logger;
import java.io.IOException;
import java.io.Serializable;
import org.slf4j.Marker;

/**
 * This implementation assumes that http://bugzilla.slf4j.org/show_bug.cgi?id=70 and
 * http://bugzilla.slf4j.org/show_bug.cgi?id=112 have been implemented.
 */
public class LoggerImpl
	implements Logger, Serializable
{
	private static final long serialVersionUID = -6525699284282037869L;

	private final String loggerName;
	private transient org.slf4j.Logger logger;

	public LoggerImpl(String loggerName)
	{
		this.loggerName = loggerName;
		initLogger();
	}

	private void initLogger()
	{
		logger = org.slf4j.LoggerFactory.getLogger(loggerName);
	}

	@Override
	public String getName()
	{
		return loggerName;
	}

	@Override
	public Threshold getThreshold()
	{
		throw new UnsupportedOperationException("This isn't supported by slf4j, yet.");
	}

	@Override
	public boolean isLoggingEnabled(Level level)
	{
		switch(level)
		{
			case TRACE:
				return logger.isTraceEnabled();
			case DEBUG:
				return logger.isDebugEnabled();
			case INFO:
				return logger.isInfoEnabled();
			case WARN:
				return logger.isWarnEnabled();
			default: // ERROR
				return logger.isErrorEnabled();
		}
	}

	@Override
	public boolean isLoggingEnabled(Level level, Marker marker)
	{
		switch(level)
		{
			case TRACE:
				return logger.isTraceEnabled(marker);
			case DEBUG:
				return logger.isDebugEnabled(marker);
			case INFO:
				return logger.isInfoEnabled(marker);
			case WARN:
				return logger.isWarnEnabled(marker);
			default: // ERROR
				return logger.isErrorEnabled(marker);
		}
	}

	@Override
	public void log(Level level, String messagePattern, Object... args)
	{
		switch(level)
		{
			case TRACE:
				logger.trace(messagePattern, args);
				break;
			case DEBUG:
				logger.debug(messagePattern, args);
				break;
			case INFO:
				logger.info(messagePattern, args);
				break;
			case WARN:
				logger.warn(messagePattern, args);
				break;
			default: // ERROR
				logger.error(messagePattern, args);
				break;
		}
	}

	@Override
	public void log(Level level, Marker marker, String messagePattern, Object... args)
	{
		switch(level)
		{
			case TRACE:
				logger.trace(marker, messagePattern, args);
				break;
			case DEBUG:
				logger.debug(marker, messagePattern, args);
				break;
			case INFO:
				logger.info(marker, messagePattern, args);
				break;
			case WARN:
				logger.warn(marker, messagePattern, args);
				break;
			default: // ERROR
				logger.error(marker, messagePattern, args);
				break;
		}
	}

	@Override
	public boolean isTraceEnabled()
	{
		return logger.isTraceEnabled();
	}

	@Override
	public boolean isTraceEnabled(Marker marker)
	{
		return logger.isTraceEnabled(marker);
	}

	@Override
	public void trace(String messagePattern, Object... args)
	{
		logger.trace(messagePattern, args);
	}

	@Override
	public void trace(Marker marker, String messagePattern, Object... args)
	{
		logger.trace(marker, messagePattern, args);
	}

	@Override
	public boolean isDebugEnabled()
	{
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isDebugEnabled(Marker marker)
	{
		return logger.isDebugEnabled(marker);
	}

	@Override
	public void debug(String messagePattern, Object... args)
	{
		logger.debug(messagePattern, args);
	}

	@Override
	public void debug(Marker marker, String messagePattern, Object... args)
	{
		logger.debug(marker, messagePattern, args);
	}

	@Override
	public boolean isInfoEnabled()
	{
		return logger.isInfoEnabled();
	}

	@Override
	public boolean isInfoEnabled(Marker marker)
	{
		return logger.isInfoEnabled(marker);
	}

	@Override
	public void info(String messagePattern, Object... args)
	{
		logger.info(messagePattern, args);
	}

	@Override
	public void info(Marker marker, String messagePattern, Object... args)
	{
		logger.info(marker, messagePattern, args);
	}

	@Override
	public boolean isWarnEnabled()
	{
		return logger.isWarnEnabled();
	}

	@Override
	public boolean isWarnEnabled(Marker marker)
	{
		return logger.isWarnEnabled(marker);
	}

	@Override
	public void warn(String messagePattern, Object... args)
	{
		logger.warn(messagePattern, args);
	}

	@Override
	public void warn(Marker marker, String messagePattern, Object... args)
	{
		logger.warn(marker, messagePattern, args);
	}

	@Override
	public boolean isErrorEnabled()
	{
		return logger.isErrorEnabled();
	}

	@Override
	public boolean isErrorEnabled(Marker marker)
	{
		return logger.isErrorEnabled(marker);
	}

	@Override
	public void error(String messagePattern, Object... args)
	{
		logger.error(messagePattern, args);
	}

	@Override
	public void error(Marker marker, String messagePattern, Object... args)
	{
		logger.error(marker, messagePattern, args);
	}

	private void readObject(java.io.ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		initLogger();
	}

}
