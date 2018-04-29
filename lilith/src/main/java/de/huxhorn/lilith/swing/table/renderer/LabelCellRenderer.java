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

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.swing.table.ColorScheme;
import de.huxhorn.lilith.swing.table.Colors;
import de.huxhorn.lilith.swing.table.ColorsProvider;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

public class LabelCellRenderer
	extends JLabel
{
	private static final long serialVersionUID = 3593164189779196002L;

	private static final Color FOCUSED_SELECTED_BACKGROUND = new Color(255, 255, 0);
	private static final Color FOCUSED_UNSELECTED_BACKGROUND = new Color(255, 255, 180);

	private final ConditionalBorder border;
	private boolean selected;
	private boolean focused;

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
	@Override
	public void validate()
	{
		// performance
	}

	@Override
	public void revalidate()
	{
		// performance
	}

	@Override
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
	{
		// performance
	}

	@Override
	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue)
	{
		// performance
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

	public boolean updateColors(boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex, JTable table, Object value)
	{
		return updateColors(isSelected, hasFocus, rowIndex, vColIndex, table, value, false);
	}

	public boolean updateColors(boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex, JTable table, Object value, boolean onlyIfSticky)
	{
		if(hasFocus || isSelected)
		{
			return false;
		}
		if(!(value instanceof EventWrapper) || !(table instanceof ColorsProvider))
		{
			return false;
		}

		EventWrapper wrapper = (EventWrapper) value;
		ColorsProvider cp = (ColorsProvider) table;
		Colors colors = cp.resolveColors(wrapper, rowIndex, vColIndex);
		if(onlyIfSticky && !colors.isSticky())
		{
			return false;
		}
		return updateColors(colors);
	}

	public void correctRowHeight(JTable table)
	{
		correctRowHeight(table, this);
	}

	public static void correctRowHeight(JTable table, JComponent component)
	{
		if(table != null)
		{
			int rowHeight = table.getRowHeight();
			int preferredHeight = component.getPreferredSize().height;
			if(rowHeight < preferredHeight)
			{
				table.setRowHeight(preferredHeight);
			}
		}
	}


	void updateColorsFromScheme(ColorScheme scheme)
	{
		if(scheme == null)
		{
			return;
		}

		{
			Color c = scheme.getBackgroundColor();
			if(c != null)
			{
				setBackground(c);
			}
		}

		{
			Color c = scheme.getTextColor();
			if(c != null)
			{
				setForeground(c);
			}
		}

		{
			Color c = scheme.getBorderColor();
			setBorderColor(c);
		}
	}

	private void setBorderColor(Color borderColor)
	{
		border.setBorderColor(borderColor);
	}
}
