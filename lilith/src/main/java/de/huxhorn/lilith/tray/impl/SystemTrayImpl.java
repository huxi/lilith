/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
package de.huxhorn.lilith.tray.impl;

import de.huxhorn.lilith.tray.TraySupport;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemTrayImpl
	extends TraySupport
{
	private final Logger logger = LoggerFactory.getLogger(SystemTrayImpl.class);

	private TrayIcon trayIcon;
	private static final Image DEFAULT_ICON_IMAGE;
	private Menu sourcesMenu;
	private SystemTray tray;
	private boolean active=false;

	static
	{
		final Logger logger = LoggerFactory.getLogger(SystemTrayImpl.class);

		URL iconUrl = SystemTrayImpl.class.getResource("/Lilith.png");
		Image image = null;
		if(iconUrl == null)
		{
			if(logger.isErrorEnabled()) logger.error("Could not load default icon!");
		}
		else
		{
			image = Toolkit.getDefaultToolkit().createImage(iconUrl);
		}
		DEFAULT_ICON_IMAGE = image;
	}


	public SystemTrayImpl()
	{
		tray = SystemTray.getSystemTray();

		sourcesMenu = new Menu("Sources");

		PopupMenu popup = new PopupMenu();

		MenuItem defaultItem = new MenuItem("Show/Hide");
		defaultItem.addActionListener(new ShowHideActionListener());
		popup.add(defaultItem);

		//popup.add(sourcesMenu);

		MenuItem quitItem = new MenuItem("Quit");
		quitItem.addActionListener(new QuitActionListener());
		popup.add(quitItem);


		trayIcon = new TrayIcon(DEFAULT_ICON_IMAGE, "Lilith", popup);
		trayIcon.setImageAutoSize(true);

		trayIcon.addActionListener(new TrayIconActionListener());
		trayIcon.addMouseListener(new TrayIconMouseListener());
	}


	public void setActive(boolean active)
	{
		if(active)
		{
			addTrayIcon();
		}
		else
		{
			removeTrayIcon();
		}
	}

	private void addTrayIcon()
	{
		try
		{
			removeTrayIcon(); // this is safe and prevents IllegalArgumentException if already added
		    tray.add(trayIcon);
			active = true;
		}
		catch (AWTException e)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while adding tray icon!", e);
		}
	}

	private void removeTrayIcon()
	{
		tray.remove(trayIcon);
		active = false;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setIconImage(Image image)
	{
		trayIcon.setImage(image);
	}

	public void displayMessage(String message, MessageType messageType)
	{
		TrayIcon.MessageType type;
		switch(messageType)
		{
			case ERROR:
				type = TrayIcon.MessageType.ERROR;
				break;

			case WARNING:
				type = TrayIcon.MessageType.WARNING;
				break;

			case INFO:
				type = TrayIcon.MessageType.INFO;
				break;
			default:
				type = TrayIcon.MessageType.NONE;
		}
		trayIcon.displayMessage(null, message, type);
	}

	public void setToolTip(String toolTip)
	{
		trayIcon.setToolTip(toolTip);
	}

	public Image getDefaultIcon()
	{
		return DEFAULT_ICON_IMAGE;
	}

	private class QuitActionListener
		implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(mainFrame != null)
			{
				mainFrame.exit();
			}
		}
	}

	private class ShowHideActionListener
		implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isInfoEnabled()) logger.info("Show/Hide Action");
			if(mainFrame != null)
			{
				mainFrame.toggleVisible();
			}
		}
	}

	private class TrayIconActionListener
		implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isInfoEnabled()) logger.info("TrayIconAction");
		}
	}

	private class TrayIconMouseListener
		extends MouseAdapter
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseClicked(MouseEvent e)
		{
			if(e.getClickCount() >= 2)
			{
				if(mainFrame != null)
				{
					mainFrame.toggleVisible();
				}
			}
		}
	}
}
