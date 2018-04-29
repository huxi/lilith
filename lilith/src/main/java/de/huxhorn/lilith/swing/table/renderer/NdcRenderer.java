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
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.swing.TextPreprocessor;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class NdcRenderer
	implements TableCellRenderer
{
	private static final String EMPTY_STRING = "";
	private final LabelCellRenderer renderer;

	public NdcRenderer()
	{
		super();
		renderer = new LabelCellRenderer();
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

		renderer.setText(resolveText(value));

		boolean colorsInitialized = renderer.updateColors(isSelected, hasFocus, rowIndex, vColIndex, table, value);
		if(!colorsInitialized)
		{
			renderer.setForeground(Color.BLACK);
		}

		renderer.correctRowHeight(table);

		return renderer;
	}



	private String resolveText(Object value)
	{
		if(!(value instanceof EventWrapper))
		{
			return EMPTY_STRING;
		}
		EventWrapper wrapper = (EventWrapper) value;
		Object eventObj = wrapper.getEvent();
		if(!(eventObj instanceof LoggingEvent))
		{
			return EMPTY_STRING;
		}

		LoggingEvent event = (LoggingEvent) eventObj;
		Message[] ndc = event.getNdc();

		if(ndc == null || ndc.length == 0)
		{
			return EMPTY_STRING;
		}

		Message message = ndc[ndc.length - 1];
		if(message == null)
		{
			return EMPTY_STRING;
		}

		return TextPreprocessor.cropLine(message.getMessage());
	}
}
