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
package de.huxhorn.lilith.tray;

import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.MainFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.lang.reflect.Method;

public abstract class TraySupport
{
	private static final boolean AVAILABLE;

	private static final TraySupport INSTANCE;
	protected MainFrame mainFrame;

	/**
	 * Resembles TrayIcon.MessageType.
	 */
	public enum MessageType {
	    /** An error message */
	    ERROR,
	    /** A warning message */
	    WARNING,
	    /** An information message */
	    INFO,
	    /** Simple message */
	    NONE
	};

	static
	{
		final Logger logger = LoggerFactory.getLogger(TraySupport.class);

		boolean support = false;
		try
		{
			Class<?> clazz = Class.forName("java.awt.SystemTray");
			Method method = clazz.getMethod("isSupported");
			Object result = method.invoke(null);
			if(result instanceof Boolean)
			{
				support = (Boolean) result;
			}
		}
		catch(Exception e)
		{
			if(logger.isInfoEnabled()) logger.info("Exception while checking for SystemTray support. => not available", e);
		}
		AVAILABLE = support;

		TraySupport instance = null;
		if(AVAILABLE)
		{
			try
			{
				Class<?> clazz = Class.forName("de.huxhorn.lilith.tray.impl.SystemTrayImpl");
				Object o = clazz.newInstance();
				if(o instanceof TraySupport)
				{
					instance = (TraySupport) o;
				}
				else
				{
					if(logger.isErrorEnabled()) logger.error("Invalid TraySupport instance! "+o);
				}
			}
			catch(Exception e)
			{
				if(logger.isErrorEnabled()) logger.error("Exception while checking for SystemTray support. => not available", e);
			}
		}
		INSTANCE = instance;
	}

	public static boolean isAvailable()
	{
		return AVAILABLE;
	}

	public static TraySupport getInstance()
	{
		return INSTANCE;
	}

	public abstract void setIconImage(Image image);

	public abstract Image getDefaultIcon();

	public abstract void displayMessage(String message, MessageType messageType);

	public void displayMessage(String message)
	{
		displayMessage(message, MessageType.NONE);
	}

	public void setMainFrame(MainFrame mainFrame)
	{
		this.mainFrame = mainFrame;
		updateTrayIcon();
	}

	public void updateTrayIcon()
	{
		if(mainFrame == null)
		{
			setActive(false);
		}
		else
		{
			ApplicationPreferences prefs = mainFrame.getApplicationPreferences();
			setActive(prefs.isTrayActive());
		}
	}

	public abstract void setToolTip(String toolTip);
	public abstract void setActive(boolean active);
	public abstract boolean isActive();
}
