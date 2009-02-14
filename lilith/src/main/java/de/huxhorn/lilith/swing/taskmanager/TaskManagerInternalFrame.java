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
package de.huxhorn.lilith.swing.taskmanager;

import de.huxhorn.lilith.swing.MainFrame;

import java.awt.*;

import javax.swing.*;

public class TaskManagerInternalFrame
	extends JInternalFrame
{
	private TaskManagerPanel<Long> taskManagerPanel;

	public TaskManagerInternalFrame(MainFrame mainFrame)
	{
		super("Task Manager", true, true, true, true);
		taskManagerPanel = new TaskManagerPanel<Long>(mainFrame.getLongWorkManager());
		setLayout(new GridLayout(1, 1));
		add(taskManagerPanel);
	}

	@Override
	public void setVisible(boolean visible)
	{
		if(taskManagerPanel != null)
		{
			taskManagerPanel.setPaused(!visible);
		}
		super.setVisible(visible);
	}
}
