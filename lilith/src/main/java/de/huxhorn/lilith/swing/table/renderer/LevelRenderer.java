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
package de.huxhorn.lilith.swing.table.renderer;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.swing.table.ColorScheme;
import de.huxhorn.lilith.swing.table.Colors;
import de.huxhorn.lilith.swing.table.ColorsProvider;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

public class LevelRenderer
		implements TableCellRenderer
{
	private LabelCellRenderer renderer;
	private static final Map<LoggingEvent.Level, ColorScheme> colors;

	static
	{
		colors=new HashMap<LoggingEvent.Level, ColorScheme>();

		colors.put(LoggingEvent.Level.TRACE, new ColorScheme(new Color(0x1F, 0x44, 0x58), new Color(0x80, 0xBA, 0xD9)));
		colors.put(LoggingEvent.Level.DEBUG, new ColorScheme(Color.BLACK, Color.GREEN));
		colors.put(LoggingEvent.Level.INFO, new ColorScheme(Color.BLACK, Color.WHITE));
		colors.put(LoggingEvent.Level.WARN, new ColorScheme(Color.BLACK, Color.YELLOW));
		colors.put(LoggingEvent.Level.ERROR, new ColorScheme(Color.YELLOW, Color.RED, Color.ORANGE));
	}

	public LevelRenderer()
	{
		super();

		renderer=new LabelCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		renderer.setToolTipText(null);
		renderer.setIcon(null);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex)
	{
		if(!isSelected)
		{
			isSelected=rowIndex == LabelCellRenderer.getSelectedRow(table);
		}
		if(!hasFocus && isSelected)
		{
			hasFocus=table.isFocusOwner();
		}
		renderer.setSelected(isSelected);
		renderer.setFocused(hasFocus);
		String text="";
		LoggingEvent.Level level=null;
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper=(EventWrapper)value;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof LoggingEvent)
			{
				LoggingEvent event=(LoggingEvent) eventObj;
				level=event.getLevel();
				text=""+level;
			}
		}
		renderer.setText(text);
		boolean colorsInitialized=false;
		if(!hasFocus && !isSelected)
		{
			if(table instanceof ColorsProvider)
			{
				if(value instanceof EventWrapper)
				{
					EventWrapper wrapper=(EventWrapper)value;
					ColorsProvider cp=(ColorsProvider) table;
					Colors colors=cp.resolveColors(wrapper, rowIndex, vColIndex);
					if(colors.isSticky())
					{
						colorsInitialized=renderer.updateColors(colors);
					}
				}
			}
		}
		if(!colorsInitialized && level != null)
		{
			renderer.setForeground(Color.BLACK);
			ColorScheme scheme=colors.get(level);
			if(scheme!=null)
			{
				{
					Color c=scheme.getBackgroundColor();
					if(c!=null)
					{
						renderer.setBackground(c);
					}
				}

				{
					Color c=scheme.getTextColor();
					if(c!=null)
					{
						renderer.setForeground(c);
					}
				}

				{
					Color c=scheme.getBorderColor();
					renderer.setBorderColor(c);
				}
			}
		}

		renderer.correctRowHeight(table);

		return renderer;
	}
}
