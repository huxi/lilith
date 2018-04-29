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

package de.huxhorn.lilith.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewContainerInternalFrame
	extends JInternalFrame
	implements ViewWindow
{
	private static final long serialVersionUID = 4881227991504896068L;

	private final Logger logger = LoggerFactory.getLogger(ViewContainerInternalFrame.class);

	private final MainFrame mainFrame;
	private final ViewContainer viewContainer;

	ViewContainerInternalFrame(MainFrame mainFrame, ViewContainer viewContainer)
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

	@Override
	public void setShowingStatusBar(boolean showingStatusBar)
	{
		if(viewContainer != null)
		{
			viewContainer.setShowingStatusBar(showingStatusBar);
		}
	}

	@Override
	public ViewActions getViewActions()
	{
		return mainFrame.getViewActions();
	}

	@Override
	public ViewContainer getViewContainer()
	{
		return viewContainer;
	}

	@Override
	public void focusWindow()
	{
		// move mainframe to front.
		if(!mainFrame.isVisible())
		{
			mainFrame.setVisible(true);
		}
		if((mainFrame.getState() & Frame.ICONIFIED) != 0)
		{
			mainFrame.setState(Frame.NORMAL);
		}
		mainFrame.toFront();
		adjustBounds(this);
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

	private void adjustBounds(JInternalFrame component)
	{
		if(component.isMaximum())
		{
			// don't adjust if maximized
			return;
		}

		Container parent = component.getParent();
		if(parent == null)
		{
			return;
		}

		Rectangle componentBounds = component.getBounds();
		int componentX = (int) componentBounds.getX();
		int componentY = (int) componentBounds.getY();
		int componentWidth = (int) componentBounds.getWidth();
		int componentHeight = (int) componentBounds.getHeight();
		boolean adjusted = false;

		Rectangle parentBounds = parent.getBounds();
		int usableWidth = (int)(parentBounds.getWidth());
		if(componentWidth > usableWidth)
		{
			componentWidth = usableWidth;
			adjusted = true;
		}

		int usableHeight = (int)(parentBounds.getHeight());
		if(componentHeight > usableHeight)
		{
			componentHeight = usableHeight;
			adjusted = true;
		}

		int usableX = 0;
		if(componentX < usableX)
		{
			componentX = usableX;
			adjusted = true;
		}
		else if(usableX + usableWidth < componentX + componentWidth)
		{
			componentX = usableX + usableWidth - componentWidth;
			adjusted = true;
		}


		int usableY = 0;
		if(componentY < usableY)
		{
			componentY = usableY;
			adjusted = true;
		}
		else if(usableY + usableHeight < componentY + componentHeight)
		{
			componentY = usableY + usableHeight - componentHeight;
			adjusted = true;
		}

		if(adjusted)
		{
			Rectangle newBounds = new Rectangle(componentX, componentY, componentWidth, componentHeight);
			component.setBounds(newBounds);
			if(logger.isDebugEnabled()) logger.debug("Adjusted bounds from {} to {}.", componentBounds, newBounds);
		}
	}


	@Override
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

	@Override
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
					result.append(current).append('\n');
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
					result.append(current).append('\n');
				}
			}

			logger.debug(result.toString());
		}
		if(logger.isInfoEnabled()) logger.info("Closed InternalFrame...");
	}

	private class CleanupWindowChangeListener
		implements InternalFrameListener
	{
		@Override
		public void internalFrameClosing(InternalFrameEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("internalFrameClosing: {}", e.getInternalFrame());
		}

		@Override
		public void internalFrameClosed(InternalFrameEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("internalFrameClosed: {}", e.getInternalFrame());
			viewContainer.cancelSearching();
			getContentPane().removeAll();

			mainFrame.updateWindowMenus();
		}

		@Override
		public void internalFrameOpened(InternalFrameEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("internalFrameOpened: {}", e.getInternalFrame());
		}

		@Override
		public void internalFrameIconified(InternalFrameEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("internalFrameIconified: {}", e.getInternalFrame());
		}

		@Override
		public void internalFrameDeiconified(InternalFrameEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("internalFrameDeiconified: {}", e.getInternalFrame());
		}

		@Override
		public void internalFrameActivated(InternalFrameEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("internalFrameActivated: {}", e.getInternalFrame());

			mainFrame.getViewActions().setViewContainer(viewContainer);
		}

		@Override
		public void internalFrameDeactivated(InternalFrameEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("internalFrameDeactivated: {}", e.getInternalFrame());
		}
	}
}
