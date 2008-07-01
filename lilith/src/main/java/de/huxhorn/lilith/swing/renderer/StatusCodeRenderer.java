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
import de.huxhorn.lilith.data.access.HttpStatus;

import javax.swing.table.TableCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import java.awt.Component;
import java.awt.Color;
import java.util.Map;
import java.util.HashMap;

import de.huxhorn.lilith.data.access.AccessEvent;

public class StatusCodeRenderer
		implements TableCellRenderer
{
	private LabelCellRenderer renderer;
	private Map<HttpStatus.Type,Color> backgroundColors;
	private Map<HttpStatus.Type,Color> foregroundColors;


	public StatusCodeRenderer()
	{
		super();
		backgroundColors=new HashMap<HttpStatus.Type, Color>();
		backgroundColors.put(HttpStatus.Type.SUCCESSFUL, Color.GREEN);
		backgroundColors.put(HttpStatus.Type.INFORMATIONAL, Color.WHITE);
		backgroundColors.put(HttpStatus.Type.REDIRECTION, Color.YELLOW);
		backgroundColors.put(HttpStatus.Type.CLIENT_ERROR, Color.RED);
		backgroundColors.put(HttpStatus.Type.SERVER_ERROR, Color.RED);
		foregroundColors=new HashMap<HttpStatus.Type, Color>();
		foregroundColors.put(HttpStatus.Type.CLIENT_ERROR, Color.GREEN);
		foregroundColors.put(HttpStatus.Type.SERVER_ERROR, Color.YELLOW);
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
		Color foreground=Color.BLACK;
		String text="";
		//String tooltip="";
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper=(EventWrapper)value;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof AccessEvent)
			{
				AccessEvent event=(AccessEvent) eventObj;
				text=""+event.getStatusCode();
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
		if(!colorsInitialized)
		{
			renderer.setForeground(foreground);
			if(value instanceof EventWrapper)
			{
			    EventWrapper wrapper = (EventWrapper) value;
				Object eventObj=wrapper.getEvent();
				if(eventObj instanceof AccessEvent)
				{
					AccessEvent event=(AccessEvent) eventObj;
					int code=event.getStatusCode();
					HttpStatus status= HttpStatus.getStatus(code);
					if(status!=null)
					{
						HttpStatus.Type type = status.getType();
						if(backgroundColors!=null)
						{
							Color c=backgroundColors.get(type);
							if(c!=null)
							{
								renderer.setBackground(c);
							}
						}
						if(foregroundColors!=null)
						{
							Color c=foregroundColors.get(type);
							if(c!=null)
							{
								renderer.setForeground(c);
							}
						}
					}
				}
			}
		}

		return renderer;
	}
}
