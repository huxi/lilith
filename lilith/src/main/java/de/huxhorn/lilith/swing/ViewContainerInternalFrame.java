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
package de.huxhorn.lilith.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.beans.PropertyVetoException;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class ViewContainerInternalFrame
	extends JInternalFrame
	implements ViewWindow
{
	private final Logger logger = LoggerFactory.getLogger(ViewContainerInternalFrame.class);
	private MainFrame mainFrame;
	private ViewContainer viewContainer;
	private boolean showingToolbar;
	private boolean showingStatusbar;

	public ViewContainerInternalFrame(MainFrame mainFrame, ViewContainer viewContainer)
	{
		super();
		this.mainFrame = mainFrame;
		this.viewContainer = viewContainer;
		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		add(viewContainer, BorderLayout.CENTER);
		setClosable(true);
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		addInternalFrameListener(new CleanupWindowChangeListener());
	}

	@Override
	public void setGlassPane(Component glassPane)
	{
		Component prev = getGlassPane();
		super.setGlassPane(glassPane);
		if(logger.isDebugEnabled()) logger.debug("Glasspane\nprev: {}\n new: {}", prev, glassPane);
	}

	public void setShowingStatusbar(boolean showingStatusbar)
	{
		this.showingStatusbar=showingStatusbar;
		if(viewContainer != null)
		{
			viewContainer.setShowingStatusbar(showingStatusbar);
		}
	}

	public ViewActions getViewActions()
	{
		return mainFrame.getViewActions();
	}

	public ViewContainer getViewContainer()
	{
		return viewContainer;
	}

	public void focusWindow()
	{
		try
		{
			setIcon(false);
			toFront();
			setSelected(true);
		}
		catch(PropertyVetoException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Veto!!", ex);
		}
	}

	public void minimizeWindow()
	{
		try
		{
			setIcon(true);
		}
		catch(PropertyVetoException e)
		{
			// ignore
		}
	}

	public void closeWindow()
	{
		if(logger.isDebugEnabled()) logger.debug("Closing InternalFrame...");
		JDesktopPane desktop = mainFrame.getDesktop();
		if(logger.isDebugEnabled())
		{
			JInternalFrame[] frames = desktop.getAllFrames();
			StringBuilder result = new StringBuilder();
			result.append("before closing:\n");
			if(frames != null)
			{
				for(JInternalFrame current : frames)
				{
					result.append(current).append("\n");
				}
			}

			logger.debug(result.toString());
		}
		setVisible(false);
		try
		{
			setClosed(true);
		}
		catch(PropertyVetoException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Couldn't close InternalFrame!", ex);
		}
		if(logger.isDebugEnabled())
		{
			JInternalFrame[] frames = desktop.getAllFrames();
			StringBuilder result = new StringBuilder();
			result.append("after closing:\n");
			if(frames != null)
			{
				for(JInternalFrame current : frames)
				{
					result.append(current).append("\n");
				}
			}

			logger.debug(result.toString());
		}
		if(logger.isInfoEnabled()) logger.info("Closed InternalFrame...");
	}

	class CleanupWindowChangeListener
		implements InternalFrameListener
	{
		public void internalFrameClosing(InternalFrameEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("internalFrameClosing");
		}

		public void internalFrameClosed(InternalFrameEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("internalFrameClosed");
			viewContainer.cancelSearching();
			getContentPane().removeAll();

			mainFrame.updateWindowMenus();
		}

		public void internalFrameOpened(InternalFrameEvent e)
		{
		}


		public void internalFrameIconified(InternalFrameEvent e)
		{
		}

		public void internalFrameDeiconified(InternalFrameEvent e)
		{
		}

		public void internalFrameActivated(InternalFrameEvent e)
		{
			mainFrame.getViewActions().setViewContainer(viewContainer);
		}

		public void internalFrameDeactivated(InternalFrameEvent e)
		{
			mainFrame.getViewActions().setViewContainer(null);
		}
	}

}
