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
package de.huxhorn.lilith.swing.table.renderer;

import de.huxhorn.lilith.swing.table.ColorScheme;
import de.huxhorn.lilith.swing.table.Colors;

import java.awt.*;

import javax.swing.*;

public class LabelCellRenderer
	extends JLabel
{
	private ConditionalBorder border;
	private boolean selected;
	private boolean focused;
	private static final Color FOCUSED_SELECTED_BACKGROUND = new Color(255, 255, 0);
	private static final Color FOCUSED_UNSELECTED_BACKGROUND = new Color(255, 255, 180);

	public LabelCellRenderer()
	{
		super();
		Font font = getFont();
		font = font.deriveFont(Font.PLAIN);
		setFont(font);
		border = new ConditionalBorder(Color.WHITE, 3, 3);
		setBorder(border);
	}

	public static int getSelectedRow(JTable table)
	{
		ListSelectionModel rsm = table.getSelectionModel();
		return rsm.getLeadSelectionIndex();
	}

	public void setSelected(boolean selected)
	{
		this.selected = selected;
		initCellProperties();
	}

	public void setFocused(boolean focused)
	{
		this.focused = focused;
		initCellProperties();
	}

	private void initCellProperties()
	{
		/*
		if(!focused)
		{
			if (selected)
			{
				setBackground(UIManager.getColor("Table.selectionBackground"));
				setForeground(UIManager.getColor("Table.selectionForeground"));
			}
			else
			{
				setBackground(UIManager.getColor("Table.background"));
				setForeground(UIManager.getColor("Table.foreground"));
			}
		}
		else
		{
			if (selected)
			{
				setBackground(FOCUSED_SELECTED_BACKGROUND);
				setForeground(UIManager.getColor("Table.selectionForeground"));
			}
			else
			{
				setBackground(FOCUSED_UNSELECTED_BACKGROUND);
				setForeground(UIManager.getColor("Table.foreground"));
			}
		}
		*/
		if(selected)
		{
			if(focused)
			{
				setBackground(FOCUSED_SELECTED_BACKGROUND);
				border.setBorderColor(null);
				setForeground(UIManager.getColor("Table.selectionForeground"));
			}
			else
			{
				setBackground(FOCUSED_UNSELECTED_BACKGROUND);
				border.setBorderColor(null);
				setForeground(UIManager.getColor("Table.foreground"));
			}
		}
		else
		{
			Color bgColor = UIManager.getColor("Table.background");
			setBackground(bgColor);
			border.setBorderColor(null);
			setForeground(UIManager.getColor("Table.foreground"));
		}
		setOpaque(true);
	}

	// The following methods override the defaults for performance reasons
	public void validate()
	{
	}

	public void revalidate()
	{
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
	{
	}

	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue)
	{
	}

	public boolean updateColors(Colors colors)
	{
		boolean result = false;
		if(colors != null)
		{
			ColorScheme scheme = colors.getColorScheme();
			if(scheme != null)
			{
				Color fg = scheme.getTextColor();
				if(fg != null)
				{
					setForeground(fg);
					result = true;
				}
				Color bg = scheme.getBackgroundColor();
				if(bg != null)
				{
					result = true;
					setBackground(bg);
				}
				Color borderColor = scheme.getBorderColor();
				if(borderColor != null)
				{
					result = true;
					border.setBorderColor(borderColor);
				}
			}
		}
		return result;
	}

	public void correctRowHeight(JTable table)
	{
		if(table != null)
		{
			int rowHeight = table.getRowHeight();
			int preferredHeight = getPreferredSize().height;
			if(rowHeight < preferredHeight)
			{
				table.setRowHeight(preferredHeight);
			}
		}
	}

	public void setBorderColor(Color borderColor)
	{
		border.setBorderColor(borderColor);
	}
}
