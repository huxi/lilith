/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2015 Joern Huxhorn
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
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ViewContainerFrame
	extends JFrame
	implements ViewWindow
{
	private static final long serialVersionUID = -3456616135178330081L;

	private final Logger logger = LoggerFactory.getLogger(ViewContainerFrame.class);

	private ViewActions viewActions;
	private MainFrame mainFrame;
	private ViewContainer viewContainer;
	private JToolBar toolbar;

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
		if(!isVisible())
		{
			setVisible(true);
		}
		if((getState() & Frame.ICONIFIED) != 0)
		{
			setState(Frame.NORMAL);
		}
		adjustBounds(this);
		toFront();
	}

	private void adjustBounds(Component component)
	{
		GraphicsConfiguration gc = component.getGraphicsConfiguration();
		if(gc == null)
		{
			if(logger.isWarnEnabled()) logger.warn("component.getGraphicsConfiguration() returned null!");
			return;
		}
		Rectangle componentBounds = component.getBounds();

		int componentX = (int) componentBounds.getX();
		int componentY = (int) componentBounds.getY();
		int componentWidth = (int) componentBounds.getWidth();
		int componentHeight = (int) componentBounds.getHeight();
		boolean adjusted = false;

		Rectangle desktopBounds = gc.getBounds();
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);

		int usableWidth = (int)(desktopBounds.getWidth()-screenInsets.left-screenInsets.right);
		if(componentWidth > usableWidth)
		{
			componentWidth = usableWidth;
			adjusted = true;
		}

		int usableHeight = (int)(desktopBounds.getHeight()-screenInsets.top-screenInsets.bottom);
		if(componentHeight > usableHeight)
		{
			componentHeight = usableHeight;
			adjusted = true;
		}

		int usableX = (int)(desktopBounds.getX()+screenInsets.left);
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


		int usableY = (int)(desktopBounds.getY()+screenInsets.top);
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

	public void setShowingStatusBar(boolean showingStatusBar)
	{
		if(viewContainer != null)
		{
			viewContainer.setShowingStatusBar(showingStatusBar);
		}
	}

	public void setShowingToolbar(boolean showingToolbar)
	{
		toolbar.setVisible(showingToolbar);
	}

//	public boolean isShowingToolbar()
//	{
//		return toolbar.isVisible();
//	}

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
