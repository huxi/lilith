/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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

import java.io.Serializable;

public interface ClipboardFormatter
	extends Serializable
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

	/**
	 * Returns true, if this formatter is able to format the given object into a String.
	 *
	 * @param object the input to both isCompatible(...) and toString(...).
	 * @return true, if this formatter is able to format the given object.
	 */
	boolean isCompatible(Object object);

	/**
	 * Returns true, if this formatter is able to format the given object into a String.
	 *
	 * @param object the input to both isCompatible(...) and toString(...).
	 * @return object formatted into a String or null if this formatter is unable to format the object.
	 */
	String toString(Object object);
}
