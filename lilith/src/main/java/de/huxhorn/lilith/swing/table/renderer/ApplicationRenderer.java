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

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class ApplicationRenderer
	implements TableCellRenderer
{
	private final LabelCellRenderer renderer;

	public ApplicationRenderer()
	{
		super();
		renderer = new LabelCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.LEFT);
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

		Color foreground = Color.BLACK;
		String text = null;
		//String tooltip="";
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) value;
			Serializable evtObject = wrapper.getEvent();
			LoggerContext context = null;
			if(evtObject instanceof LoggingEvent)
			{
				context = ((LoggingEvent) evtObject).getLoggerContext();
			}
			else if(evtObject instanceof AccessEvent)
			{
				context = ((AccessEvent) evtObject).getLoggerContext();
			}
			if(context != null)
			{
				Map<String, String> props = context.getProperties();
				if(props!= null)
				{
					text=props.get(LoggerContext.APPLICATION_IDENTIFIER_PROPERTY_NAME);
				}
				if(text == null)
				{
					// using context name as a fallback
					text=context.getName();
				}
			}
		}
		if(text == null)
		{
			text="";
		}
		renderer.setText(text);
		boolean colorsInitialized = renderer.updateColors(isSelected, hasFocus, rowIndex, vColIndex, table, value);
		if(!colorsInitialized)
		{
			renderer.setForeground(foreground);
		}

		renderer.correctRowHeight(table);

		return renderer;
	}
}
