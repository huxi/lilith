package de.huxhorn.lilith.swing.preferences;

import de.huxhorn.sulky.conditions.Condition;

import java.awt.Color;
import java.io.Serializable;

public class SavedCondition
	implements Cloneable, Serializable
{
	private static final long serialVersionUID = -3481175802321027303L;

	private String name;
	private Condition condition;
	private boolean active;
	private Color textColor;
	private Color backgroundColor;
	private Color borderColor;

	public SavedCondition()
	{
		// do not *EVER* forget the default c'tor if you want to save instances using XMLEncoder!!!
		this(null);
	}

	public SavedCondition(Condition condition)
	{
		this("", condition, Color.BLACK, Color.WHITE, Color.WHITE, false);
	}

	public SavedCondition(String name, Condition condition, Color textColor, Color backgroundColor)
	{
		this(name, condition, textColor, backgroundColor, backgroundColor, false);
	}

	public SavedCondition(String name, Condition condition, Color textColor, Color backgroundColor, Color borderColor)
	{
		this(name, condition, textColor, backgroundColor, borderColor, false);
	}

	public SavedCondition(String name, Condition condition, Color textColor, Color backgroundColor, Color borderColor, boolean active)
	{
		this.name = name;
		this.condition = condition;
		this.active = active;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
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
		if (this == o) return true;
		if (!(o instanceof SavedCondition)) return false;

		SavedCondition that = (SavedCondition) o;

		if (active != that.active) return false;
		if (backgroundColor != null ? !backgroundColor.equals(that.backgroundColor) : that.backgroundColor != null)
		{
			return false;
		}
		if (borderColor != null ? !borderColor.equals(that.borderColor) : that.borderColor != null) return false;
		if (condition != null ? !condition.equals(that.condition) : that.condition != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (textColor != null ? !textColor.equals(that.textColor) : that.textColor != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (condition != null ? condition.hashCode() : 0);
		result = 31 * result + (active ? 1 : 0);
		result = 31 * result + (textColor != null ? textColor.hashCode() : 0);
		result = 31 * result + (backgroundColor != null ? backgroundColor.hashCode() : 0);
		result = 31 * result + (borderColor != null ? borderColor.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return "SavedCondition[name="+name+", condition="+condition+", textColor="+textColor+", backgroundColor="+backgroundColor+", borderColor="+borderColor+", active="+active+"]";
	}

	public SavedCondition clone()
			throws CloneNotSupportedException
	{
		SavedCondition result = (SavedCondition) super.clone();
		if(condition!=null)
		{
			result.condition=condition.clone();
		}
		result.textColor=clone(textColor);
		result.backgroundColor=clone(backgroundColor);
		result.borderColor=clone(borderColor);
		return result;
	}

	private static Color clone(Color c)
	{
		if(c!=null)
		{
			return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		}
		return null;
	}
}
