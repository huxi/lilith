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

package de.huxhorn.lilith.services.clipboard;

import de.huxhorn.lilith.data.logging.json.LoggingJsonEncoder;
import de.huxhorn.lilith.swing.LilithActionId;

public class LoggingEventJsonFormatter
		extends AbstractLoggingEventEncoderFormatter
{
	private static final long serialVersionUID = -3491565980751250370L;

	public LoggingEventJsonFormatter()
	{
		super(LilithActionId.COPY_JSON, new LoggingJsonEncoder(false, true, true));
	}
}
