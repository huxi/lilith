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

import java.util.logging.Logger;

public class LogJulRunnable
	extends AbstractDebugRunnable
{
	private final Logger logger = Logger.getLogger(LogJulRunnable.class.getName());

	public LogJulRunnable(int delay)
	{
		super(delay);
	}

	@Override
	public void runIt()
		throws InterruptedException
	{
		logger.finest("finest");
		sleep();
		logger.finer("finer");
		sleep();
		logger.fine("fine");
		sleep();
		logger.info("info");
		sleep();
		logger.warning("warning");
		sleep();
		logger.severe("severe");

		logger.config("config");
		sleep();

		logger.entering("SourceClass", "sourceMethod", new Object[]{"param1", "param2"});
		sleep();
		logger.exiting("SourceClass", "sourceMethod", "result");
		sleep();
		logger.throwing("SourceClass", "sourceMethod", new RuntimeException("meh."));
	}
}
