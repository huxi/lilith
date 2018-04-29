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

package de.huxhorn.lilith.swing.table.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;

/**
 * This is basically a mutable, simplified LineBorder-EmptyBorder combination.
 */
public final class ConditionalBorder
	extends AbstractBorder
{
	private static final long serialVersionUID = -2372658104457011019L;

	private int thickness;
	private int innerThickness;
	private Color borderColor;

	public ConditionalBorder(Color color, int thickness, int innerThickness)
	{
		setBorderColor(color);
		setThickness(thickness);
		setInnerThickness(innerThickness);
	}

	/**
	 * Paints the border for the specified component with the
	 * specified position and size.
	 *
	 * @param c      the component for which this border is being painted
	 * @param g      the paint graphics
	 * @param x      the x position of the painted border
	 * @param y      the y position of the painted border
	 * @param width  the width of the painted border
	 * @param height the height of the painted border
	 */
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		if(borderColor != null && thickness > 0)
		{
			Color oldColor = g.getColor();

			g.setColor(borderColor);

			for(int i = 0; i < thickness; i++)
			{
				g.drawRect(x + i, y + i, width - i - i - 1, height - i - i - 1);
			}

			g.setColor(oldColor);
		}
	}

	/**
	 * Returns the insets of the border.
	 *
	 * @param c the component for which this border insets value applies
	 */
	@Override
	public Insets getBorderInsets(Component c)
	{
		int actualThickness = thickness + innerThickness;
		return new Insets(actualThickness, actualThickness, actualThickness, actualThickness);
	}

	/**
	 * Reinitialize the insets parameter with this Border's current Insets.
	 *
	 * @param c      the component for which this border insets value applies
	 * @param insets the object to be reinitialized
	 */
	@Override
	public Insets getBorderInsets(Component c, Insets insets)
	{
		int actualThickness = thickness + innerThickness;

		insets.left = actualThickness;
		insets.top = actualThickness;
		insets.right = actualThickness;
		insets.bottom = actualThickness;

		return insets;
	}

	/**
	 * Returns the color of the border.
	 *
	 * @return the color of the border.
	 */
	public Color getBorderColor()
	{
		return borderColor;
	}

	/**
	 * Sets the color of the border.
	 *
	 * @param borderColor the color of the border.
	 */
	public void setBorderColor(Color borderColor)
	{
		this.borderColor = borderColor;
	}

	/**
	 * Returns the inner thickness of the border.
	 *
	 * @return Returns the inner thickness of the border.
	 */
	public int getInnerThickness()
	{
		return innerThickness;
	}

	/**
	 * Sets the inner thickness of the border.
	 *
	 * @param innerThickness Returns the inner thickness of the border.
	 */
	public void setInnerThickness(int innerThickness)
	{
		if(innerThickness < 0)
		{
			throw new IllegalArgumentException("innerThickness must not be negative!");
		}
		this.innerThickness = innerThickness;
	}


	/**
	 * Returns the thickness of the border.
	 *
	 * @return Returns the thickness of the border.
	 */
	public int getThickness()
	{
		return thickness;
	}

	/**
	 * Sets the thickness of the border.
	 *
	 * @param thickness the thickness of the border.
	 */
	public void setThickness(int thickness)
	{
		if(thickness < 0)
		{
			throw new IllegalArgumentException("thickness must not be negative!");
		}
		this.thickness = thickness;
	}

	/**
	 * Returns whether or not the border is opaque.
	 */
	@Override
	public boolean isBorderOpaque()
	{
		return innerThickness == 0 && thickness > 0 && borderColor != null;
	}
}
