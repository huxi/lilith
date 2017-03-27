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
import de.huxhorn.lilith.swing.LilithKeyStrokes;
import java.util.Objects;

public abstract class AbstractNativeClipboardFormatter
	implements ClipboardFormatter
{
	private static final long serialVersionUID = -124752554703671002L;

	private final LilithActionId id;

	protected AbstractNativeClipboardFormatter(LilithActionId id)
	{
		this.id = Objects.requireNonNull(id, "id must not be null!");
	}

	@Override
	public final String getName()
	{
		return id.getText();
	}

	@Override
	public final String getDescription()
	{
		return id.getDescription();
	}

	@Override
	public final String getAccelerator()
	{
		return LilithKeyStrokes.getKeyStrokeString(id);
	}

	@Override
	public final Integer getMnemonic()
	{
		return id.getMnemonic();
	}

	@Override
	public final boolean isNative()
	{
		return true;
	}
}
