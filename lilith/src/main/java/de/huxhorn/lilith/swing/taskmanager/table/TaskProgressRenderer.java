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
package de.huxhorn.lilith.swing.taskmanager.table;

import de.huxhorn.sulky.tasks.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;


public class TaskProgressRenderer
	implements TableCellRenderer
{
	private final Logger logger = LoggerFactory.getLogger(TaskProgressRenderer.class);
	private JProgressBar progressBar;

	public TaskProgressRenderer()
	{
		super();
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex)
	{
		int progress = -1;
		if(logger.isInfoEnabled()) logger.info("Object: {}", value);

		if(value instanceof Integer)
		{
			progress = (Integer) value;
		}
		else if(value instanceof Task)
		{
			Task task = (Task) value;
			progress = task.getProgress();
		}
		if(logger.isInfoEnabled()) logger.info("Progress: {}", progress);

		if(progress < 0)
		{
			progressBar.setValue(0);
			progressBar.setString("Unknown");
		}
		else
		{
			progressBar.setValue(progress);
			progressBar.setString("" + progress + "%");
		}

		return progressBar;
	}
}