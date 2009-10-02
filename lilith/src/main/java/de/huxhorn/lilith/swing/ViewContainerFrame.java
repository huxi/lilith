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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

public class ViewContainerFrame
	extends JFrame
	implements ViewWindow
{
	private final Logger logger = LoggerFactory.getLogger(ViewContainerFrame.class);

	private ViewActions viewActions;
	private MainFrame mainFrame;
	private ViewContainer viewContainer;
	private JToolBar toolbar;
	private boolean showingToolbar;
	private boolean showingStatusbar;

	public ViewContainerFrame(MainFrame mainFrame, ViewContainer viewContainer)
		throws HeadlessException
	{
		this.mainFrame = mainFrame;
		this.viewContainer = viewContainer;
		viewActions = new ViewActions(mainFrame, viewContainer);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		add(viewContainer, BorderLayout.CENTER);
		toolbar=viewActions.getToolbar();
		add(toolbar, BorderLayout.NORTH);
		setJMenuBar(viewActions.getMenuBar());
		addWindowListener(new CleanupWindowChangeListener());
	}

	public ViewActions getViewActions()
	{
		return viewActions;
	}

	public ViewContainer getViewContainer()
	{
		return viewContainer;
	}

	public void focusWindow()
	{
		if((getState() & Frame.ICONIFIED) != 0)
		{
			setState(Frame.NORMAL);
		}
		toFront();

	}

	public void minimizeWindow()
	{
		setExtendedState(Frame.ICONIFIED);
	}

	public void closeWindow()
	{
		setVisible(false);
		dispose();
		if(logger.isInfoEnabled()) logger.info("Closed Frame...");
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

	public void setShowingToolbar(boolean showingToolbar)
	{
		this.showingToolbar=showingToolbar;
		toolbar.setVisible(showingToolbar);
	}

	public boolean isShowingToolbar()
	{
		return showingToolbar;
	}

	class CleanupWindowChangeListener
		implements WindowListener
	{
		public void windowOpened(WindowEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("windowOpened: {}", e.getWindow());
		}

		public void windowClosing(WindowEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("windowClosing: {}", e.getWindow());
		}

		public void windowClosed(WindowEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("windowClosed: {}", e.getWindow());
			viewContainer.cancelSearching();
			getContentPane().removeAll();
			viewActions.setViewContainer(null); // to remove listener from container
			mainFrame.updateWindowMenus();
		}

		public void windowIconified(WindowEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("windowIconified: {}", e.getWindow());

		}

		public void windowDeiconified(WindowEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("windowDeiconified: {}", e.getWindow());
		}

		public void windowActivated(WindowEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("windowActivated: {}", e.getWindow());
		}

		public void windowDeactivated(WindowEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("windowDeactivated: {}", e.getWindow());
		}
	}

}
