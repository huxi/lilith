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

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.swing.LilithActionId;

import static de.huxhorn.lilith.services.clipboard.FormatterTools.isNullOrEmpty;
import static de.huxhorn.lilith.services.clipboard.FormatterTools.resolveLoggingEvent;

public class LoggingNdcFormatter
		extends AbstractNativeClipboardFormatter
{
	private static final long serialVersionUID = 8250337181083260675L;

	public LoggingNdcFormatter()
	{
		super(LilithActionId.COPY_NDC);
	}

	@Override
	public boolean isCompatible(Object object)
	{
		return resolveLoggingEvent(object).map(it -> !isNullOrEmpty(it.getNdc())).orElse(false);
	}

	@Override
	public String toString(Object object)
	{
		return resolveLoggingEvent(object).map(LoggingNdcFormatter::toStringOrNull).orElse(null);
	}

	private static String toStringOrNull(LoggingEvent value)
	{
		Message[] ndc = value.getNdc();
		if (!isNullOrEmpty(ndc))
		{
			StringBuilder text = new StringBuilder();
			for (Message current : ndc)
			{
				if (text.length() != 0)
				{
					text.append('\n');
				}
				if (current != null)
				{
					text.append(current.getMessage());
				}
				else
				{
					text.append((String) null);
				}
			}
			return text.toString();
		}
		return null;
	}
}
