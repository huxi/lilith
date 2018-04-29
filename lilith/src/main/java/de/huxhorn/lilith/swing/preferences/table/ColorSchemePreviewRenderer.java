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

package de.huxhorn.lilith.swing.preferences.table;

import de.huxhorn.lilith.swing.table.ColorScheme;
import de.huxhorn.lilith.swing.table.renderer.ConditionalBorder;
import de.huxhorn.lilith.swing.table.renderer.LabelCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public abstract class ColorSchemePreviewRenderer
	implements TableCellRenderer

{
	private static final ColorScheme DEFAULT_SCHEME = new ColorScheme().initDefaults();

	private final ConditionalBorder border;
	protected final JLabel renderer;

	ColorSchemePreviewRenderer()
	{
		renderer = new JLabel();
		Font font = renderer.getFont();
		font = font.deriveFont(Font.PLAIN);
		renderer.setFont(font);
		renderer.setOpaque(true);
		renderer.setText("X");
		border = new ConditionalBorder(Color.WHITE, 3, 3);
		renderer.setBorder(border);
	}

	public abstract ColorScheme resolveColorScheme(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column);

	public abstract void updateText(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column);

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		ColorScheme scheme = resolveColorScheme(table, value, isSelected, hasFocus, row, column);

		if(scheme == null)
		{
			scheme = DEFAULT_SCHEME;
		}

		border.setBorderColor(scheme.getBorderColor());
		renderer.setForeground(scheme.getTextColor());
		renderer.setBackground(scheme.getBackgroundColor());
		renderer.setBorder(null); // so it actually changes...
		renderer.setBorder(border);

		updateText(table, value, isSelected, hasFocus, row, column);

		LabelCellRenderer.correctRowHeight(table, renderer);

		return renderer;
	}
}
