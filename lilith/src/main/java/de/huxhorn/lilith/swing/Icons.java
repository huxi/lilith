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

import javax.swing.ImageIcon;
import java.net.URL;

public class Icons
{
	public static final ImageIcon EMPTY_16_ICON;

	public static final ImageIcon CLEAR_MENU_ICON;
	public static final ImageIcon ATTACH_MENU_ICON;
	public static final ImageIcon DETACH_MENU_ICON;
	public static final ImageIcon PAUSED_MENU_ICON;
	public static final ImageIcon UNPAUSED_MENU_ICON;
	public static final ImageIcon FIND_MENU_ITEM;
	public static final ImageIcon STATISTICS_MENU_ICON;
	public static final ImageIcon DISCONNECT_MENU_ICON;
	public static final ImageIcon TAIL_MENU_ICON;
	public static final ImageIcon FIND_NEXT_MENU_ICON;
	public static final ImageIcon FIND_PREV_MENU_ICON;
	public static final ImageIcon HELP_MENU_ICON;
	public static final ImageIcon OPEN_INACTIVE_MENU_ICON;
	public static final ImageIcon EXPORT_MENU_ICON;
	public static final ImageIcon EXIT_MENU_ICON;
	public static final ImageIcon PREFERENCES_MENU_ICON;
	public static final ImageIcon LOVE_MENU_ICON;

	public static final ImageIcon CLEAR_TOOLBAR_ICON;
	public static final ImageIcon ATTACH_TOOLBAR_ICON;
	public static final ImageIcon DETACH_TOOLBAR_ICON;
	public static final ImageIcon PAUSED_TOOLBAR_ICON;
	public static final ImageIcon UNPAUSED_TOOLBAR_ICON;
	public static final ImageIcon FIND_TOOLBAR_ICON;
	public static final ImageIcon DISCONNECT_TOOLBAR_ICON;
	public static final ImageIcon TAIL_TOOLBAR_ICON;
	public static final ImageIcon LOVE_TOOLBAR_ICON;

	public static final ImageIcon PREFERENCES_TOOLBAR_ICON;

	public static final ImageIcon CHECK_UPDATE_ICON;
	public static final ImageIcon TOTD_ICON;
	public static final ImageIcon WINDOW_16_ICON; // TODO change

	// TODO: Resource classes
	// TODO: Separate Toolbar and Menu creation.
	// TODO: toolbar action, menu action => same action
	static
	{
		ImageIcon icon;
		{
			URL url = Icons.class.getResource("/otherGraphics/empty16.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		EMPTY_16_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/actions/edit-clear.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		CLEAR_MENU_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/32x32/actions/edit-clear.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		CLEAR_TOOLBAR_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/actions/edit-undo.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		ATTACH_MENU_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/actions/edit-redo.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		DETACH_MENU_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/32x32/actions/edit-undo.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		ATTACH_TOOLBAR_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/32x32/actions/edit-redo.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		DETACH_TOOLBAR_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/actions/media-playback-start.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		PAUSED_MENU_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/actions/media-playback-pause.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		UNPAUSED_MENU_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/32x32/actions/media-playback-start.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		PAUSED_TOOLBAR_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/32x32/actions/media-playback-pause.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		UNPAUSED_TOOLBAR_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/actions/edit-find.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		FIND_MENU_ITEM = icon;

		{
			URL url = Icons.class.getResource("/tango/32x32/actions/edit-find.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		FIND_TOOLBAR_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/apps/utilities-system-monitor.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		STATISTICS_MENU_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/actions/media-eject.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		DISCONNECT_MENU_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/32x32/actions/media-eject.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		DISCONNECT_TOOLBAR_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/actions/go-down.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		FIND_NEXT_MENU_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/actions/go-up.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		FIND_PREV_MENU_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/actions/go-bottom.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}

		}
		TAIL_MENU_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/32x32/actions/go-bottom.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}

		}
		TAIL_TOOLBAR_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/apps/help-browser.png");

			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		HELP_MENU_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/actions/document-open.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		OPEN_INACTIVE_MENU_ICON = icon;


		{
			URL url = Icons.class.getResource("/tango/16x16/actions/document-save.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		EXPORT_MENU_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/actions/system-log-out.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		EXIT_MENU_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/categories/preferences-system.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		PREFERENCES_MENU_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/32x32/categories/preferences-system.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		PREFERENCES_TOOLBAR_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/emblems/emblem-favorite.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		LOVE_MENU_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/32x32/emblems/emblem-favorite.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		LOVE_TOOLBAR_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/status/software-update-available.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		CHECK_UPDATE_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/status/dialog-information.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		TOTD_ICON = icon;

		{
			URL url = Icons.class.getResource("/tango/16x16/mimetypes/text-x-generic.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		WINDOW_16_ICON = icon;
	}
}
