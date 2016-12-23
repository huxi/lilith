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
package de.huxhorn.lilith.tray;

import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.MainFrame;
import de.huxhorn.lilith.tray.impl.SystemTrayImpl;
import java.awt.Image;
import java.awt.SystemTray;


public abstract class TraySupport
{
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
	}

	static
	{
		TraySupport instance = null;
		if(SystemTray.isSupported())
		{
			instance = new SystemTrayImpl();
		}
		INSTANCE = instance;
	}

	public static boolean isAvailable()
	{
		return SystemTray.isSupported();
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
