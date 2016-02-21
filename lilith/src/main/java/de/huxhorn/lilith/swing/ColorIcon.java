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
package de.huxhorn.lilith.swing;

import java.awt.*;

import javax.swing.*;

public class ColorIcon
	implements Icon
{
	private Color color;
	private Dimension size;

	public ColorIcon(Color color)
	{
		this(color, new Dimension(16, 16));
	}

	public ColorIcon(Color color, Dimension size)
	{
		this.color = color;
		this.size = size;
	}

	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		g.setColor(color);
		g.fillRect(x, y, size.width, size.height);
	}

	public int getIconWidth()
	{
		return size.width;
	}

	public int getIconHeight()
	{
		return size.height;
	}
}
