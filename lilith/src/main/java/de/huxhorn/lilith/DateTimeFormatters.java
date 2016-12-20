/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2016 Joern Huxhorn
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
package de.huxhorn.lilith;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeFormatters
{
	public static final DateTimeFormatter DATETIME_IN_SYSTEM_ZONE_SPACE =
			DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss.SSS", Locale.US)
					.withZone(ZoneId.systemDefault());

	public static final DateTimeFormatter TIME_IN_SYSTEM_ZONE =
			DateTimeFormatter.ofPattern("HH:mm:ss.SSS", Locale.US)
					.withZone(ZoneId.systemDefault());

	public static final DateTimeFormatter COMPACT_DATETIME_IN_SYSTEM_ZONE_T =
			DateTimeFormatter.ofPattern("yyyyMMdd\'T\'HHmmssSSS", Locale.US)
					.withZone(ZoneId.systemDefault());
}
