/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
 * Copyright 2007-2010 Joern Huxhorn
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

package de.huxhorn.lilith.slf4j;

import org.slf4j.Marker;

public interface Logger
{
	/**
	 * The level of an event.
	 */
	enum Level
	{
		TRACE, DEBUG, INFO, WARN, ERROR
	}

	/**
	 * The threshold of a logger.
	 */
	enum Threshold
	{
		ALL(Level.TRACE), TRACE(Level.TRACE), DEBUG(Level.DEBUG), INFO(Level.INFO), WARN(Level.WARN), ERROR(Level.ERROR), OFF(null);

		private final Level level;

		Threshold(Level level)
		{
			this.level = level;
		}

		public boolean passes(Level level)
		{
			return this.level != null && this.level.compareTo(level) <= 0;
		}
	}

	String getName();

	/**
	 * @return the Threshold of this Logger.
	 */
	Threshold getThreshold();

	boolean isLoggingEnabled(Level level);

	boolean isLoggingEnabled(Level level, Marker marker);

	void log(Level level, String messagePattern, Object... args);

	void log(Level level, Marker marker, String messagePattern, Object... args);

	boolean isTraceEnabled();

	boolean isTraceEnabled(Marker marker);

	void trace(String messagePattern, Object... args);

	void trace(Marker marker, String messagePattern, Object... args);

	boolean isDebugEnabled();

	boolean isDebugEnabled(Marker marker);

	void debug(String messagePattern, Object... args);

	void debug(Marker marker, String messagePattern, Object... args);

	boolean isInfoEnabled();

	boolean isInfoEnabled(Marker marker);

	void info(String messagePattern, Object... args);

	void info(Marker marker, String messagePattern, Object... args);

	boolean isWarnEnabled();

	boolean isWarnEnabled(Marker marker);

	void warn(String messagePattern, Object... args);

	void warn(Marker marker, String messagePattern, Object... args);

	boolean isErrorEnabled();

	boolean isErrorEnabled(Marker marker);

	void error(String messagePattern, Object... args);

	void error(Marker marker, String messagePattern, Object... args);


}
