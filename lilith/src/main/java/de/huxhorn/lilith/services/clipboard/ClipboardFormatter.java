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

import de.huxhorn.lilith.services.BasicFormatter;
import java.io.Serializable;

public interface ClipboardFormatter
	extends BasicFormatter,Serializable
{
	/**
	 * The name of this formatter.
	 * It is used as the name of the menu item.
	 *
	 * @return the name of this formatter.
	 */
	String getName();

	/**
	 * The description of this formatter.
	 * It is used as the tooltip in the menu.
	 *
	 * @return the description of this formatter.
	 */
	String getDescription();

	/**
	 *
	 * @return the accelerator of this formatter, can be null.
	 */
	String getAccelerator();

	default Integer getMnemonic()
	{
		return null;
	}

	/**
	 * Returns true, if this is a native Lilith formatter, i.e. the formatter is part
	 * of Lilith itself so a match against Lilith accelerator keystrokes should not be
	 * considered a problem.
	 *
	 * Default implementation returns false.
	 *
	 * @return true, if this is a native Lilith formatter. false otherwise
	 */
	default boolean isNative()
	{
		return false;
	}
}
