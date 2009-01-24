/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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
package de.huxhorn.lilith.swing.table;

import java.awt.*;
import java.io.Serializable;

public final class ColorScheme
	implements Serializable, Cloneable
{
	private static final long serialVersionUID = 5979344161643301761L;

	private Color textColor;
	private Color backgroundColor;
	private Color borderColor;

	public ColorScheme()
	{
		this(Color.BLACK, Color.WHITE, Color.WHITE);
	}

	public ColorScheme(Color textColor, Color backgroundColor)
	{
		this(textColor, backgroundColor, backgroundColor);
	}

	public ColorScheme(Color textColor, Color backgroundColor, Color borderColor)
	{
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
	}

	public Color getTextColor()
	{
		return textColor;
	}

	public void setTextColor(Color textColor)
	{
		this.textColor = textColor;
	}

	public Color getBackgroundColor()
	{
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}

	public Color getBorderColor()
	{
		return borderColor;
	}

	public void setBorderColor(Color borderColor)
	{
		this.borderColor = borderColor;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		ColorScheme that = (ColorScheme) o;

		if(backgroundColor != null ? !backgroundColor.equals(that.backgroundColor) : that.backgroundColor != null)
		{
			return false;
		}
		if(borderColor != null ? !borderColor.equals(that.borderColor) : that.borderColor != null) return false;
		if(textColor != null ? !textColor.equals(that.textColor) : that.textColor != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = textColor != null ? textColor.hashCode() : 0;
		result = 31 * result + (backgroundColor != null ? backgroundColor.hashCode() : 0);
		result = 31 * result + (borderColor != null ? borderColor.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return "ColorScheme[textColor=" + textColor + ", backgroundColor=" + backgroundColor + ", borderColor=" + borderColor + "]";
	}

	@Override
	public ColorScheme clone()
		throws CloneNotSupportedException
	{
		ColorScheme result = (ColorScheme) super.clone();

		result.textColor = clone(textColor);
		result.backgroundColor = clone(backgroundColor);
		result.borderColor = clone(borderColor);

		return result;
	}

	private static Color clone(Color c)
	{
		if(c != null)
		{
			return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		}
		return null;
	}
}
