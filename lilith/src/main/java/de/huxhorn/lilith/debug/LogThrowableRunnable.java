/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

package de.huxhorn.lilith.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LogThrowableRunnable
	extends AbstractDebugRunnable
{
	private final Logger logger = LoggerFactory.getLogger(LogThrowableRunnable.class);

	private final Throwable throwable;

	LogThrowableRunnable(int delay, Throwable throwable)
	{
		super(delay);
		this.throwable = throwable;
	}

	@Override
	public void runIt()
		throws InterruptedException
	{
		MDC.put("type", "param");
		if(logger.isTraceEnabled()) logger.trace("A trace message.", throwable);
		sleep();
		if(logger.isDebugEnabled()) logger.debug("A debug message.", throwable);
		sleep();
		if(logger.isInfoEnabled()) logger.info("A info message.", throwable);
		sleep();
		if(logger.isWarnEnabled()) logger.warn("A warn message.", throwable);
		sleep();
		if(logger.isErrorEnabled()) logger.error("A error message.", throwable);
		sleep();
		MDC.remove("type");
	}
}
