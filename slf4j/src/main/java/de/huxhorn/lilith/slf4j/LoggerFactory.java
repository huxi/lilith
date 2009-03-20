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
package de.huxhorn.lilith.slf4j;

import de.huxhorn.lilith.slf4j.impl.LoggerImpl;

public class LoggerFactory
{
	public static Logger getLogger(String loggerName)
	{
		return new LoggerImpl(loggerName);
	}

	public static Logger getLogger(Class clazz)
	{
		String loggerName = clazz.getName();
		// the following is essentially an implementation of http://jira.qos.ch/browse/LBCLASSIC-102 but this could
		// be a very bad location for the fix :p
		loggerName = loggerName.replace('$', '.');

		return new LoggerImpl(loggerName);
	}
}
