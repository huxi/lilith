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

package de.huxhorn.lilith.swing.taskmanager;

import de.huxhorn.lilith.swing.MainFrame;
import java.awt.GridLayout;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskManagerInternalFrame
	extends JInternalFrame
{
	private static final long serialVersionUID = 7919725126794950187L;

	private final Logger logger = LoggerFactory.getLogger(TaskManagerInternalFrame.class);

	private final MainFrame mainFrame;

	public TaskManagerInternalFrame(MainFrame mainFrame)
	{
		super("Task Manager", true, true, true, true);
		this.mainFrame=mainFrame;
		TaskManagerPanel<Long> taskManagerPanel = new TaskManagerPanel<>(mainFrame.getLongWorkManager());
		setLayout(new GridLayout(1, 1));
		add(taskManagerPanel);
		taskManagerPanel.setPaused(false);
		addInternalFrameListener(new CleanupWindowChangeListener());
	}

	class CleanupWindowChangeListener
		implements InternalFrameListener
	{
		@Override
		public void internalFrameClosing(InternalFrameEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("internalFrameClosing {}", e.getInternalFrame());
		}

		@Override
		public void internalFrameClosed(InternalFrameEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("internalFrameClosed {}", e.getInternalFrame());

			mainFrame.updateWindowMenus();
		}

		@Override
		public void internalFrameOpened(InternalFrameEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("internalFrameOpened {}", e.getInternalFrame());
		}


		@Override
		public void internalFrameIconified(InternalFrameEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("internalFrameIconified {}", e.getInternalFrame());
		}

		@Override
		public void internalFrameDeiconified(InternalFrameEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("internalFrameDeiconified {}", e.getInternalFrame());
		}

		@Override
		public void internalFrameActivated(InternalFrameEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("internalFrameActivated {}", e.getInternalFrame());

			mainFrame.getViewActions().setViewContainer(null);
		}

		@Override
		public void internalFrameDeactivated(InternalFrameEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("internalFrameDeactivated {}", e.getInternalFrame());
		}
	}

}
