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

import de.huxhorn.lilith.swing.LilithActionId;

import static de.huxhorn.lilith.services.clipboard.FormatterTools.isNullOrEmpty;
import static de.huxhorn.lilith.services.clipboard.FormatterTools.resolveAccessEvent;
import static de.huxhorn.lilith.services.clipboard.FormatterTools.toStringOrNull;

public class AccessRequestUriFormatter
		extends AbstractNativeClipboardFormatter
{
	private static final long serialVersionUID = 6274663538364280458L;

	public AccessRequestUriFormatter()
	{
		super(LilithActionId.COPY_REQUEST_URI);
	}

	@Override
	public boolean isCompatible(Object object)
	{
		return resolveAccessEvent(object).map(it -> !isNullOrEmpty(it.getRequestURI())).orElse(false);
	}

	@Override
	public String toString(Object object)
	{
		return resolveAccessEvent(object).map(it -> toStringOrNull(it.getRequestURI())).orElse(null);
	}
}
