/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2014 Joern Huxhorn
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

import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoggingViewStateIcons
{
	private static final ImageIcon activeGlobalIcon;
	private static final ImageIcon inactiveGlobalIcon;
	private static final Map<LoggingViewState, ImageIcon> activeIcons;
	private static final Map<LoggingViewState, ImageIcon> inactiveIcons;

	static
	{
		URL url = LoggingViewStateIcons.class.getResource("/tango/16x16/categories/applications-internet.png");
		if (url != null)
		{
			activeGlobalIcon = new ImageIcon(url);
			inactiveGlobalIcon = getDisabledIcon(activeGlobalIcon);
		}
		else
		{
			activeGlobalIcon = null;
			inactiveGlobalIcon = null;
		}
		activeIcons = new HashMap<>();
		inactiveIcons = new HashMap<>();

		url = LoggingViewStateIcons.class.getResource("/tango/16x16/status/network-receive.png");
		if (url != null)
		{
			activeIcons.put(LoggingViewState.ACTIVE, new ImageIcon(url));
		}

		url = LoggingViewStateIcons.class.getResource("/tango/16x16/status/network-offline.png");
		if (url != null)
		{
			activeIcons.put(LoggingViewState.INACTIVE, new ImageIcon(url));
		}

		url = LoggingViewStateIcons.class.getResource("/tango/16x16/emotes/face-grin.png");
		if (url != null)
		{
			activeIcons.put(LoggingViewState.UPDATING_FILE, new ImageIcon(url));
		}

		url = LoggingViewStateIcons.class.getResource("/tango/16x16/emotes/face-plain.png");
		if (url != null)
		{
			activeIcons.put(LoggingViewState.STALE_FILE, new ImageIcon(url));
		}

		for (Map.Entry<LoggingViewState, ImageIcon> current : activeIcons.entrySet())
		{
			LoggingViewState key = current.getKey();
			ImageIcon value = current.getValue();
			if(value != null)
			{
				inactiveIcons.put(key, getDisabledIcon(value));
			}
		}
	}

	public static ImageIcon resolveIconForState(LoggingViewState state)
	{
		return resolveIconForState(state, false);
	}

	public static ImageIcon resolveIconForState(LoggingViewState state, boolean disabled)
	{
		ImageIcon result;
		if(disabled)
		{
			result = inactiveGlobalIcon;
			if (state != null)
			{
				result = inactiveIcons.get(state);
			}
		}
		else
		{
			result = activeGlobalIcon;
			if (state != null)
			{
				result = activeIcons.get(state);
			}
		}
		return result;
	}


	private static ImageIcon getDisabledIcon(Icon icon)
	{
		final int w = icon.getIconWidth();
		final int h = icon.getIconHeight();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		BufferedImage image = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
		Graphics2D g2d = image.createGraphics();
		icon.paintIcon(null, g2d, 0, 0);
		Image gray = GrayFilter.createDisabledImage(image);
		return new ImageIcon(gray);
	}
}
