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
package de.huxhorn.lilith.swing.renderer;

import de.huxhorn.lilith.swing.ColorsProvider;
import de.huxhorn.lilith.swing.Colors;
import de.huxhorn.lilith.data.eventsource.EventWrapper;

import javax.swing.table.TableCellRenderer;
import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.text.SimpleDateFormat;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.access.AccessEvent;

public class TimestampRenderer
		implements TableCellRenderer
{
	private LabelCellRenderer renderer;
	private SimpleDateFormat timeFormat;

	public TimestampRenderer()
	{
		super();
		renderer=new LabelCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		renderer.setToolTipText(null);
		renderer.setIcon(null);

		timeFormat=new SimpleDateFormat("HH:mm:ss.SSS");
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

		Color foreground=Color.BLACK;
		String text="";
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper=(EventWrapper)value;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof LoggingEvent)
			{
				LoggingEvent event=(LoggingEvent) eventObj;
				Date timestamp=event.getTimeStamp();
				text=timeFormat.format(timestamp);
			}
			else if(eventObj instanceof AccessEvent)
			{
				AccessEvent event=(AccessEvent) eventObj;
				Date timestamp=event.getTimeStamp();
				if(timestamp!=null)
				{
					text=timeFormat.format(timestamp);
				}
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
					colorsInitialized=renderer.updateColors(colors);
				}
			}
		}
		if(!colorsInitialized)
		{
			renderer.setForeground(foreground);
		}

		return renderer;
	}
}
