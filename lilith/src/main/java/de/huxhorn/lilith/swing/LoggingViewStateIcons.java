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
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class LoggingViewStateIcons
{
	private static final ImageIcon ACTIVE_GLOBAL_ICON;
	private static final ImageIcon INACTIVE_GLOBAL_ICON;
	private static final Map<LoggingViewState, ImageIcon> ACTIVE_ICONS;
	private static final Map<LoggingViewState, ImageIcon> INACTIVE_ICONS;

	static
	{
		URL url = LoggingViewStateIcons.class.getResource("/tango/16x16/categories/applications-internet.png");
		if (url != null)
		{
			ACTIVE_GLOBAL_ICON = new ImageIcon(url);
			INACTIVE_GLOBAL_ICON = getDisabledIcon(ACTIVE_GLOBAL_ICON);
		}
		else
		{
			ACTIVE_GLOBAL_ICON = null;
			INACTIVE_GLOBAL_ICON = null;
		}
		ACTIVE_ICONS = new HashMap<>();
		INACTIVE_ICONS = new HashMap<>();

		url = LoggingViewStateIcons.class.getResource("/tango/16x16/status/network-receive.png");
		if (url != null)
		{
			ACTIVE_ICONS.put(LoggingViewState.ACTIVE, new ImageIcon(url));
		}

		url = LoggingViewStateIcons.class.getResource("/tango/16x16/status/network-offline.png");
		if (url != null)
		{
			ACTIVE_ICONS.put(LoggingViewState.INACTIVE, new ImageIcon(url));
		}

		url = LoggingViewStateIcons.class.getResource("/tango/16x16/emotes/face-grin.png");
		if (url != null)
		{
			ACTIVE_ICONS.put(LoggingViewState.UPDATING_FILE, new ImageIcon(url));
		}

		url = LoggingViewStateIcons.class.getResource("/tango/16x16/emotes/face-plain.png");
		if (url != null)
		{
			ACTIVE_ICONS.put(LoggingViewState.STALE_FILE, new ImageIcon(url));
		}

		for (Map.Entry<LoggingViewState, ImageIcon> current : ACTIVE_ICONS.entrySet())
		{
			LoggingViewState key = current.getKey();
			ImageIcon value = current.getValue();
			if(value != null)
			{
				INACTIVE_ICONS.put(key, getDisabledIcon(value));
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
			result = INACTIVE_GLOBAL_ICON;
			if (state != null)
			{
				result = INACTIVE_ICONS.get(state);
			}
		}
		else
		{
			result = ACTIVE_GLOBAL_ICON;
			if (state != null)
			{
				result = ACTIVE_ICONS.get(state);
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
