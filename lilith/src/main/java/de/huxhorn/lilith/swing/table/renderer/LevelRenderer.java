/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.swing.table.Colors;
import de.huxhorn.lilith.swing.table.ColorsProvider;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class LevelRenderer
	implements TableCellRenderer
{
	private final LabelCellRenderer renderer;

	public LevelRenderer()
	{
		super();

		renderer = new LabelCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		renderer.setToolTipText(null);
		renderer.setIcon(null);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex)
	{
		if(!isSelected)
		{
			isSelected = rowIndex == LabelCellRenderer.getSelectedRow(table);
		}
		if(!hasFocus && isSelected)
		{
			hasFocus = table.isFocusOwner();
		}
		renderer.setSelected(isSelected);
		renderer.setFocused(hasFocus);
		String text = "";
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) value;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof LoggingEvent)
			{
				LoggingEvent event = (LoggingEvent) eventObj;
				LoggingEvent.Level level = event.getLevel();
				if(level != null)
				{
					text = level.toString();
				}
			}
		}
		renderer.setText(text);

		boolean colorsInitialized = renderer.updateColors(isSelected, hasFocus, rowIndex, vColIndex, table, value, true);
		if(!colorsInitialized)
		{
			initializeColors(table, value, rowIndex, vColIndex);
		}

		renderer.correctRowHeight(table);

		return renderer;
	}

	private void initializeColors(JTable table, Object value, int rowIndex, int vColIndex)
	{
		renderer.setForeground(Color.BLACK);
		if(!(value instanceof EventWrapper))
		{
			return;
		}
		EventWrapper wrapper = (EventWrapper) value;
		Object eventObj = wrapper.getEvent();
		if(!(eventObj instanceof LoggingEvent))
		{
			return;
		}

		LoggingEvent event = (LoggingEvent) eventObj;
		LoggingEvent.Level level = event.getLevel();
		if(level == null)
		{
			return;
		}
		if(!(table instanceof ColorsProvider))
		{
			return;
		}

		ColorsProvider cp = (ColorsProvider) table;
		Colors colors = cp.resolveColors(level, rowIndex, vColIndex);

		renderer.updateColorsFromScheme(colors.getColorScheme());
	}
}
