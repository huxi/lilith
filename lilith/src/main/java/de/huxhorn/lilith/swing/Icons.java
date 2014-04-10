/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2013 Joern Huxhorn
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
import java.net.URL;

public class Icons
{
	public static final Icon EMPTY_16_ICON;

	public static final Icon CLEAR_MENU_ICON;
	public static final Icon ATTACH_MENU_ICON;
	public static final Icon DETACH_MENU_ICON;
	public static final Icon PAUSED_MENU_ICON;
	public static final Icon UNPAUSED_MENU_ICON;
	public static final Icon FIND_MENU_ITEM;
	public static final Icon STATISTICS_MENU_ICON;
	public static final Icon DISCONNECT_MENU_ICON;
	public static final Icon TAIL_MENU_ICON;
	public static final Icon FIND_NEXT_MENU_ICON;
	public static final Icon FIND_PREV_MENU_ICON;
	public static final Icon HELP_MENU_ICON;
	public static final Icon OPEN_INACTIVE_MENU_ICON;
	public static final Icon EXPORT_MENU_ICON;
	public static final Icon EXIT_MENU_ICON;
	public static final Icon PREFERENCES_MENU_ICON;
	public static final Icon LOVE_MENU_ICON;

	public static final Icon CLEAR_TOOLBAR_ICON;
	public static final Icon ATTACH_TOOLBAR_ICON;
	public static final Icon DETACH_TOOLBAR_ICON;
	public static final Icon PAUSED_TOOLBAR_ICON;
	public static final Icon UNPAUSED_TOOLBAR_ICON;
	public static final Icon FIND_TOOLBAR_ICON;
	public static final Icon DISCONNECT_TOOLBAR_ICON;
	public static final Icon TAIL_TOOLBAR_ICON;
	public static final Icon LOVE_TOOLBAR_ICON;

	public static final Icon PREFERENCES_TOOLBAR_ICON;

	public static final Icon CHECK_UPDATE_ICON;
	public static final Icon TOTD_ICON;
	public static final Icon WINDOW_16_ICON;

	// TODO: Resource classes
	// TODO: Separate Toolbar and Menu creation.
	// TODO: toolbar action, menu action => same action
	static
	{
		Icon icon;
		{
			URL url = ViewActions.class.getResource("/otherGraphics/empty16.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/actions/edit-clear.png");
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
			URL url = ViewActions.class.getResource("/tango/32x32/actions/edit-clear.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/actions/edit-undo.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/actions/edit-redo.png");
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
			URL url = ViewActions.class.getResource("/tango/32x32/actions/edit-undo.png");
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
			URL url = ViewActions.class.getResource("/tango/32x32/actions/edit-redo.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/actions/media-playback-start.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/actions/media-playback-pause.png");
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
			URL url = ViewActions.class.getResource("/tango/32x32/actions/media-playback-start.png");
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
			URL url = ViewActions.class.getResource("/tango/32x32/actions/media-playback-pause.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/actions/edit-find.png");
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
			URL url = ViewActions.class.getResource("/tango/32x32/actions/edit-find.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/apps/utilities-system-monitor.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/actions/media-eject.png");
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
			URL url = ViewActions.class.getResource("/tango/32x32/actions/media-eject.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/actions/go-down.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/actions/go-up.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/actions/go-bottom.png");
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
			URL url = ViewActions.class.getResource("/tango/32x32/actions/go-bottom.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/apps/help-browser.png");

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
			URL url = ViewActions.class.getResource("/tango/16x16/actions/document-open.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/actions/document-save.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/actions/system-log-out.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/categories/preferences-system.png");
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
			URL url = ViewActions.class.getResource("/tango/32x32/categories/preferences-system.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/emblems/emblem-favorite.png");
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
			URL url = ViewActions.class.getResource("/tango/32x32/emblems/emblem-favorite.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/status/software-update-available.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/status/dialog-information.png");
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
			URL url = ViewActions.class.getResource("/tango/16x16/mimetypes/text-x-generic.png");
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
