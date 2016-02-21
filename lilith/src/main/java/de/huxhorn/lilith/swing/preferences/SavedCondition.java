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
package de.huxhorn.lilith.swing.preferences;

import de.huxhorn.lilith.swing.table.ColorScheme;
import de.huxhorn.sulky.conditions.Condition;

import java.awt.*;
import java.io.Serializable;

public class SavedCondition
	implements Cloneable, Serializable
{
	private static final long serialVersionUID = -3481175802321027303L;

	private String name;
	private Condition condition;
	private boolean active;
	private ColorScheme colorScheme;

	public SavedCondition()
	{
		// do not *EVER* forget the default c'tor if you want to save instances using XMLEncoder!!!
		this(null);
	}

	public SavedCondition(Condition condition)
	{
		this("", condition, new ColorScheme().initDefaults(), false);
	}

	public SavedCondition(String name, Condition condition, ColorScheme colorScheme, boolean active)
	{
		this.name = name;
		this.condition = condition;
		this.active = active;
		this.colorScheme = colorScheme;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Condition getCondition()
	{
		return condition;
	}

	public void setCondition(Condition condition)
	{
		this.condition = condition;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public Color getTextColor()
	{
		if(colorScheme != null)
		{
			return colorScheme.getTextColor();
		}
		return null;
	}

	public Color getBackgroundColor()
	{
		if(colorScheme != null)
		{
			return colorScheme.getBackgroundColor();
		}
		return null;
	}

	public Color getBorderColor()
	{
		if(colorScheme != null)
		{
			return colorScheme.getBorderColor();
		}
		return null;
	}

	public ColorScheme getColorScheme()
	{
		return colorScheme;
	}

	public void setColorScheme(ColorScheme colorScheme)
	{
		this.colorScheme = colorScheme;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		SavedCondition that = (SavedCondition) o;

		if(active != that.active) return false;
		if(colorScheme != null ? !colorScheme.equals(that.colorScheme) : that.colorScheme != null) return false;
		if(condition != null ? !condition.equals(that.condition) : that.condition != null) return false;
		if(name != null ? !name.equals(that.name) : that.name != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (condition != null ? condition.hashCode() : 0);
		result = 31 * result + (active ? 1 : 0);
		result = 31 * result + (colorScheme != null ? colorScheme.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return "SavedCondition[name=" + name + ", condition=" + condition + ", colorScheme=" + colorScheme + ", active=" + active + "]";
	}

	public SavedCondition clone()
		throws CloneNotSupportedException
	{
		SavedCondition result = (SavedCondition) super.clone();
		if(condition != null)
		{
			result.condition = condition.clone();
		}
		if(colorScheme != null)
		{
			result.colorScheme = colorScheme.clone();
		}
		return result;
	}

}
