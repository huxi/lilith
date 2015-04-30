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

import javax.swing.ImageIcon;
import java.net.URL;

public class Icons
{
	public static final ImageIcon EMPTY_16_ICON = resolveImageIcon("/otherGraphics/empty16.png");
	public static final ImageIcon CLEAR_MENU_ICON = resolveImageIcon("/tango/16x16/actions/edit-clear.png");
	public static final ImageIcon CLEAR_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/edit-clear.png");
	public static final ImageIcon ATTACH_MENU_ICON = resolveImageIcon("/tango/16x16/actions/edit-undo.png");
	public static final ImageIcon DETACH_MENU_ICON = resolveImageIcon("/tango/16x16/actions/edit-redo.png");
	public static final ImageIcon ATTACH_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/edit-undo.png");
	public static final ImageIcon DETACH_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/edit-redo.png");
	public static final ImageIcon PAUSED_MENU_ICON = resolveImageIcon("/tango/16x16/actions/media-playback-start.png");
	public static final ImageIcon UNPAUSED_MENU_ICON = resolveImageIcon("/tango/16x16/actions/media-playback-pause.png");
	public static final ImageIcon PAUSED_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/media-playback-start.png");
	public static final ImageIcon UNPAUSED_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/media-playback-pause.png");
	public static final ImageIcon FIND_MENU_ITEM = resolveImageIcon("/tango/16x16/actions/edit-find.png");
	public static final ImageIcon FIND_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/edit-find.png");
	public static final ImageIcon STATISTICS_MENU_ICON = resolveImageIcon("/tango/16x16/apps/utilities-system-monitor.png");
	public static final ImageIcon DISCONNECT_MENU_ICON = resolveImageIcon("/tango/16x16/actions/media-eject.png");
	public static final ImageIcon DISCONNECT_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/media-eject.png");
	public static final ImageIcon FIND_NEXT_MENU_ICON = resolveImageIcon("/tango/16x16/actions/go-down.png");
	public static final ImageIcon FIND_PREV_MENU_ICON = resolveImageIcon("/tango/16x16/actions/go-up.png");
	public static final ImageIcon TAIL_MENU_ICON = resolveImageIcon("/tango/16x16/actions/go-bottom.png");
	public static final ImageIcon TAIL_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/go-bottom.png");
	public static final ImageIcon HELP_MENU_ICON = resolveImageIcon("/tango/16x16/apps/help-browser.png");
	public static final ImageIcon OPEN_INACTIVE_MENU_ICON = resolveImageIcon("/tango/16x16/actions/document-open.png");
	public static final ImageIcon EXPORT_MENU_ICON = resolveImageIcon("/tango/16x16/actions/document-save.png");
	public static final ImageIcon EXIT_MENU_ICON = resolveImageIcon("/tango/16x16/actions/system-log-out.png");
	public static final ImageIcon PREFERENCES_MENU_ICON = resolveImageIcon("/tango/16x16/categories/preferences-system.png");
	public static final ImageIcon PREFERENCES_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/categories/preferences-system.png");
	public static final ImageIcon LOVE_MENU_ICON = resolveImageIcon("/tango/16x16/emblems/emblem-favorite.png");
	public static final ImageIcon LOVE_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/emblems/emblem-favorite.png");
	public static final ImageIcon CHECK_UPDATE_ICON = resolveImageIcon("/tango/16x16/status/software-update-available.png");
	public static final ImageIcon TOTD_ICON = resolveImageIcon("/tango/16x16/status/dialog-information.png");
	public static final ImageIcon DIALOG_WARNING_ICON = resolveImageIcon("/tango/32x32/status/dialog-warning.png");
	public static final ImageIcon DIALOG_INFO_ICON = resolveImageIcon("/tango/32x32/status/dialog-information.png");
	public static final ImageIcon ADD_16_ICON = resolveImageIcon("/tango/16x16/actions/list-add.png");
	public static final ImageIcon REMOVE_16_ICON = resolveImageIcon("/tango/16x16/actions/list-remove.png");
	public static final ImageIcon SAVE_AS_32_ICON = resolveImageIcon("/tango/32x32/actions/document-save-as.png");
	public static final ImageIcon STOP_16_ICON = resolveImageIcon("/tango/16x16/actions/process-stop.png");
	public static final ImageIcon CLOSE_16_ICON = resolveImageIcon("/tango/16x16/emblems/emblem-unreadable.png");
	public static final ImageIcon UPDATE_AVAILABLE_32_ICON = resolveImageIcon("/tango/32x32/status/software-update-available.png");
	public static final ImageIcon PROGRESS_ICON = resolveImageIcon("/otherGraphics/Progress16.gif");
	public static final ImageIcon FRAME_ICON = resolveImageIcon("/otherGraphics/Lilith16.jpg");

	private static final Logger logger = LoggerFactory.getLogger(Icons.class);

	private static ImageIcon resolveImageIcon(String resourcePath)
	{
		URL url = Icons.class.getResource(resourcePath);
		if (url != null)
		{
			return new ImageIcon(url);
		}
		if(logger.isWarnEnabled()) logger.warn("Failed to create ImageIcon from resource '{}'!", resourcePath);
		return null;
	}
}
