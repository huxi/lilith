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
package de.huxhorn.lilith.swing.preferences.table;

import de.huxhorn.lilith.swing.preferences.SavedCondition;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class ConditionActiveRenderer
	implements TableCellRenderer
{
	private JCheckBox renderer;

	public ConditionActiveRenderer()
	{
		super();
		renderer = new JCheckBox();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		renderer.setToolTipText(null);
		renderer.setIcon(null);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex)
	{
		boolean active = false;
		if(value instanceof SavedCondition)
		{
			SavedCondition wrapper = (SavedCondition) value;
			active = wrapper.isActive();
		}
		renderer.setSelected(active);
		if(isSelected)
		{
			renderer.setBackground(table.getSelectionBackground());
			renderer.setForeground(table.getSelectionForeground());
		}
		else
		{
			renderer.setBackground(table.getBackground());
			renderer.setForeground(table.getForeground());
		}

		return renderer;
	}

}
