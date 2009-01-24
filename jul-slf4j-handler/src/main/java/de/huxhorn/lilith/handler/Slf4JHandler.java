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
package de.huxhorn.lilith.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * <p>A java.util.logging.Handler that simply forwards LogRecords to the respective
 * org.slf4j.Logger.</p>
 * <p/>
 * <p>Usage example:<br />
 * <tt><pre>
 * {
 * // initialize java.util.logging to use slf4j...
 * Handler handler = new Slf4JHandler();
 * java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
 * rootLogger.addHandler(handler);
 * rootLogger.setLevel(java.util.logging.Level.ALL);
 * }
 * </pre></tt></p>
 * <p/>
 * <p><b>Keep in mind that the above code enables all logging, even for <tt>java.*</tt>, <tt>javax.*</tt>
 * and <tt>sun.*</tt>!</b></p>
 * <p/>
 * <p>This means that you will most likely have to set the log level of those three packages to a sane value
 * like <tt>INFO</tt> or <tt>WARN</tt>.</p>
 * <p/>
 * <p>It's also possible to set those levels before the LogRecord reaches the Handler:<br/>
 * <tt><pre>
 * {
 * java.util.logging.Logger.getLogger("java").setLevel(java.util.logging.Level.WARNING);
 * java.util.logging.Logger.getLogger("javax").setLevel(java.util.logging.Level.WARNING);
 * java.util.logging.Logger.getLogger("sun").setLevel(java.util.logging.Level.WARNING);
 * }
 * </pre></tt></p>
 */
public class Slf4JHandler
	extends Handler
{
	private static final int TRACE_LEVEL_THRESHOLD = Level.FINEST.intValue();
	private static final int DEBUG_LEVEL_THRESHOLD = Level.FINE.intValue();
	private static final int INFO_LEVEL_THRESHOLD = Level.INFO.intValue();
	private static final int WARN_LEVEL_THRESHOLD = Level.WARNING.intValue();

	private static final String LEVEL_MDC_KEY = "JUL-Level";
	private static final String SOURCE_CLASS_NAME_MDC_KEY = "JUL-SourceClassName";
	private static final String SOURCE_METHOD_NAME_MDC_KEY = "JUL-SourceMethodName";

	/**
	 * Publish a <tt>LogRecord</tt>.
	 * <p/>
	 * The logging request was made initially to a <tt>Logger</tt> object,
	 * which initialized the <tt>LogRecord</tt> and forwarded it here.
	 * <p/>
	 * The <tt>Handler</tt>  is responsible for formatting the message, when and
	 * if necessary.  The formatting should include localization.
	 *
	 * @param record description of the log event. A null record is
	 *               silently ignored and is not published
	 */
	public void publish(LogRecord record)
	{
		if(record != null)
		{
			logRecord(record);
		}
	}


	/**
	 * Flush any buffered output.
	 */
	public void flush()
	{
	}

	/**
	 * Close the <tt>Handler</tt> and free all associated resources.
	 * <p/>
	 * The close method will perform a <tt>flush</tt> and then close the
	 * <tt>Handler</tt>.   After close has been called this <tt>Handler</tt>
	 * should no longer be used.  Method calls may either be silently
	 * ignored or may throw runtime exceptions.
	 *
	 * @throws SecurityException if a security manager exists and if
	 *                           the caller does not have <tt>LoggingPermission("control")</tt>.
	 */
	public void close()
		throws SecurityException
	{
	}

	private void logRecord(LogRecord record)
	{
		Level julLevel = record.getLevel();
		int julLevelValue = julLevel.intValue();
		if(julLevelValue <= TRACE_LEVEL_THRESHOLD)
		{
			logTrace(record);
		}
		else if(julLevelValue <= DEBUG_LEVEL_THRESHOLD)
		{
			logDebug(record);
		}
		else if(julLevelValue <= INFO_LEVEL_THRESHOLD)
		{
			logInfo(record);
		}
		else if(julLevelValue <= WARN_LEVEL_THRESHOLD)
		{
			logWarn(record);
		}
		else
		{
			logError(record);
		}
	}

	private void logTrace(LogRecord record)
	{
		String loggerName = record.getLoggerName();
		final Logger logger = LoggerFactory.getLogger(loggerName);
		if(logger.isTraceEnabled())
		{
			initMDC(record);
			String message = record.getMessage();
			Throwable throwable = record.getThrown();
			if(throwable != null)
			{
				logger.trace(message, throwable);
			}
			else
			{
				logger.trace(message);
			}
			clearMDC();
		}
	}

	private void logDebug(LogRecord record)
	{
		String loggerName = record.getLoggerName();
		final Logger logger = LoggerFactory.getLogger(loggerName);
		if(logger.isDebugEnabled())
		{
			initMDC(record);
			String message = record.getMessage();
			Throwable throwable = record.getThrown();
			if(throwable != null)
			{
				logger.debug(message, throwable);
			}
			else
			{
				logger.debug(message);
			}
			clearMDC();
		}
	}

	private void logInfo(LogRecord record)
	{
		String loggerName = record.getLoggerName();
		final Logger logger = LoggerFactory.getLogger(loggerName);
		if(logger.isInfoEnabled())
		{
			initMDC(record);
			String message = record.getMessage();
			Throwable throwable = record.getThrown();
			if(throwable != null)
			{
				logger.info(message, throwable);
			}
			else
			{
				logger.info(message);
			}
			clearMDC();
		}
	}

	private void logWarn(LogRecord record)
	{
		String loggerName = record.getLoggerName();
		final Logger logger = LoggerFactory.getLogger(loggerName);
		if(logger.isWarnEnabled())
		{
			initMDC(record);
			String message = record.getMessage();
			Throwable throwable = record.getThrown();
			if(throwable != null)
			{
				logger.warn(message, throwable);
			}
			else
			{
				logger.warn(message);
			}
			clearMDC();
		}
	}

	private void logError(LogRecord record)
	{
		String loggerName = record.getLoggerName();
		final Logger logger = LoggerFactory.getLogger(loggerName);
		if(logger.isErrorEnabled())
		{
			initMDC(record);
			String message = record.getMessage();
			Throwable throwable = record.getThrown();
			if(throwable != null)
			{
				logger.error(message, throwable);
			}
			else
			{
				logger.error(message);
			}
			clearMDC();
		}
	}


	private void initMDC(LogRecord record)
	{
		Level julLevel = record.getLevel();
		MDC.put(LEVEL_MDC_KEY, julLevel.getName());
		String sourceClassName = record.getSourceClassName();
		if(sourceClassName != null)
		{
			MDC.put(SOURCE_CLASS_NAME_MDC_KEY, sourceClassName);
		}
		String sourceMethodName = record.getSourceMethodName();
		if(sourceMethodName != null)
		{
			MDC.put(SOURCE_METHOD_NAME_MDC_KEY, sourceMethodName);
		}
	}

	private void clearMDC()
	{
		MDC.remove(LEVEL_MDC_KEY);
		MDC.remove(SOURCE_CLASS_NAME_MDC_KEY);
		MDC.remove(SOURCE_METHOD_NAME_MDC_KEY);
	}
}
