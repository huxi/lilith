/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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

import de.huxhorn.lilith.swing.preferences.ColorScheme;
import de.huxhorn.lilith.swing.preferences.SavedCondition;

import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;
import javax.swing.JLabel;
import java.awt.Component;

public class ConditionPreviewRenderer
		implements TableCellRenderer

{
	private JLabel renderer;
	private static final ColorScheme DEFAULT_SCHEME = new ColorScheme();

	public ConditionPreviewRenderer()
	{
		this.renderer = new JLabel();
		renderer.setOpaque(true);
		renderer.setText("Example");
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		ColorScheme scheme=null;

		if(value instanceof SavedCondition)
		{
			SavedCondition condition= (SavedCondition) value;
			scheme=condition.getColorScheme();
		}

		if(scheme == null)
		{
			scheme=DEFAULT_SCHEME;
		}

		renderer.setForeground(scheme.getTextColor());
		renderer.setBackground(scheme.getBackgroundColor());
		// TODO: renderer.setBorderColor(scheme.getBorderColor());
		return renderer;
	}
}
