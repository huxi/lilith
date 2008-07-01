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
package de.huxhorn.lilith.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import java.awt.HeadlessException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ViewContainerFrame
	extends JFrame
	implements ViewWindow
{
	private final Logger logger = LoggerFactory.getLogger(ViewContainerFrame.class);

	private ViewActions viewActions;
	private MainFrame mainFrame;
	private ViewContainer viewContainer;

	public ViewContainerFrame(MainFrame mainFrame, ViewContainer viewContainer) throws HeadlessException
	{
		this.mainFrame=mainFrame;
		this.viewContainer=viewContainer;
		viewActions=new ViewActions(mainFrame, viewContainer);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		add(viewContainer, BorderLayout.CENTER);
		add(viewActions.getToolbar(), BorderLayout.NORTH);
		setJMenuBar(viewActions.getMenuBar());
		addWindowListener(new CleanupWindowChangeListener());
	}

	public ViewActions getViewActions()
	{
		return viewActions;
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

	class CleanupWindowChangeListener
			implements WindowListener
	{
		public void windowOpened(WindowEvent e)
		{
		}

		public void windowClosing(WindowEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Window closing.");
		}

		public void windowClosed(WindowEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Window closed.");
			viewContainer.cancelSearching();
			getContentPane().removeAll();
			viewActions.setViewContainer(null); // to remove listener from container
			mainFrame.updateWindowMenus();
		}

		public void windowIconified(WindowEvent e)
		{
		}

		public void windowDeiconified(WindowEvent e)
		{
		}

		public void windowActivated(WindowEvent e)
		{
		}

		public void windowDeactivated(WindowEvent e)
		{
		}
	}

}
