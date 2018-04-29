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

import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.swing.LilithActionId;

import static de.huxhorn.lilith.services.clipboard.FormatterTools.resolveCallStack;

public class LoggingCallStackFormatter
		extends AbstractNativeClipboardFormatter
{
	private static final long serialVersionUID = 7055240983247307195L;

	public LoggingCallStackFormatter()
	{
		super(LilithActionId.COPY_CALL_STACK);
	}

	@Override
	public boolean isCompatible(Object object)
	{
		return resolveCallStack(object).isPresent();
	}

	@Override
	public String toString(Object object)
	{
		return resolveCallStack(object).map(callStack -> toString(callStack)).orElse(null);
	}

	private static String toString(ExtendedStackTraceElement[] callStack)
	{
		StringBuilder text = new StringBuilder();
		boolean first = true;
		for (ExtendedStackTraceElement current : callStack)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				text.append('\n');
			}
			text.append("\tat ");
			if (current != null)
			{
				text.append(current.toString(true));
			}
			else
			{
				text.append((String) null);
			}
		}
		return text.toString();
	}
}
