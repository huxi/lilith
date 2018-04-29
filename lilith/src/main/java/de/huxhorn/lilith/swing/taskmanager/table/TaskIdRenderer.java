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

package de.huxhorn.lilith.swing.taskmanager.table;

import de.huxhorn.sulky.tasks.Task;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class TaskIdRenderer
	implements TableCellRenderer
{
	private final Logger logger = LoggerFactory.getLogger(TaskIdRenderer.class);
	private final DefaultTableCellRenderer renderer;

	TaskIdRenderer()
	{
		super();
		renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		renderer.setToolTipText(null);
		renderer.setIcon(null);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex)
	{
		String text = "";
		if(value instanceof Long)
		{
			text = value.toString();
		}
		else if(value instanceof Task)
		{
			Task task = (Task) value;
			text = Long.toString(task.getId());
		}
		if(logger.isInfoEnabled()) logger.info("Text: {}", text);

		return renderer.getTableCellRendererComponent(table, text, isSelected, false, rowIndex, vColIndex);
	}
}
