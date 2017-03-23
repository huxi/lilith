/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icons
{
	public static final ImageIcon ADD_16_ICON = resolveImageIcon("/tango/16x16/actions/list-add.png");
	public static final ImageIcon ATTACH_MENU_ICON = resolveImageIcon("/tango/16x16/actions/edit-undo.png");
	public static final ImageIcon ATTACH_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/edit-undo.png");
	public static final ImageIcon CLOSE_16_ICON = resolveImageIcon("/tango/16x16/emblems/emblem-unreadable.png");
	public static final ImageIcon DETACH_MENU_ICON = resolveImageIcon("/tango/16x16/actions/edit-redo.png");
	public static final ImageIcon DETACH_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/edit-redo.png");
	public static final ImageIcon DIALOG_INFO_ICON = resolveImageIcon("/tango/32x32/status/dialog-information.png");
	public static final ImageIcon DIALOG_WARNING_ICON = resolveImageIcon("/tango/32x32/status/dialog-warning.png");
	public static final ImageIcon EMPTY_16_ICON = resolveImageIcon("/otherGraphics/empty16.png");
	public static final ImageIcon PAUSED_MENU_ICON = resolveImageIcon("/tango/16x16/actions/media-playback-start.png");
	public static final ImageIcon PAUSED_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/media-playback-start.png");
	public static final ImageIcon PROGRESS_ICON = resolveImageIcon("/otherGraphics/Progress16.gif");
	public static final ImageIcon REMOVE_16_ICON = resolveImageIcon("/tango/16x16/actions/list-remove.png");
	public static final ImageIcon STOP_16_ICON = resolveImageIcon("/tango/16x16/actions/process-stop.png");
	public static final ImageIcon UNPAUSED_MENU_ICON = resolveImageIcon("/tango/16x16/actions/media-playback-pause.png");
	public static final ImageIcon UNPAUSED_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/media-playback-pause.png");
	public static final ImageIcon UPDATE_AVAILABLE_32_ICON = resolveImageIcon("/tango/32x32/status/software-update-available.png");

	private static final ImageIcon CHECK_FOR_UPDATE_MENU_ICON = resolveImageIcon("/tango/16x16/status/software-update-available.png");
	private static final ImageIcon CLEAR_MENU_ICON = resolveImageIcon("/tango/16x16/actions/edit-clear.png");
	private static final ImageIcon CLEAR_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/edit-clear.png");
	private static final ImageIcon DISCONNECT_MENU_ICON = resolveImageIcon("/tango/16x16/actions/media-eject.png");
	private static final ImageIcon DISCONNECT_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/media-eject.png");
	private static final ImageIcon EXIT_MENU_ICON = resolveImageIcon("/tango/16x16/actions/system-log-out.png");
	private static final ImageIcon EXPORT_MENU_ICON = resolveImageIcon("/tango/16x16/actions/document-save.png");
	private static final ImageIcon FIND_MENU_ICON = resolveImageIcon("/tango/16x16/actions/edit-find.png");
	private static final ImageIcon FIND_NEXT_MENU_ICON = resolveImageIcon("/tango/16x16/actions/go-down.png");
	private static final ImageIcon FIND_PREV_MENU_ICON = resolveImageIcon("/tango/16x16/actions/go-up.png");
	private static final ImageIcon FIND_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/edit-find.png");
	private static final ImageIcon FRAME_ICON = resolveImageIcon("/otherGraphics/Lilith16.jpg");
	private static final ImageIcon HELP_MENU_ICON = resolveImageIcon("/tango/16x16/apps/help-browser.png");
	private static final ImageIcon LOVE_MENU_ICON = resolveImageIcon("/tango/16x16/emblems/emblem-favorite.png");
	private static final ImageIcon LOVE_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/emblems/emblem-favorite.png");
	private static final ImageIcon OPEN_MENU_ICON = resolveImageIcon("/tango/16x16/actions/document-open.png");
	private static final ImageIcon PREFERENCES_MENU_ICON = resolveImageIcon("/tango/16x16/categories/preferences-system.png");
	private static final ImageIcon PREFERENCES_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/categories/preferences-system.png");
	private static final ImageIcon TAIL_MENU_ICON = resolveImageIcon("/tango/16x16/actions/go-bottom.png");
	private static final ImageIcon TAIL_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/go-bottom.png");
	private static final ImageIcon TOTD_ICON = resolveImageIcon("/tango/16x16/status/dialog-information.png");

	private static final EnumMap<LilithActionId, Icon> MENU_ICONS = new EnumMap<>(LilithActionId.class);
	private static final EnumMap<LilithActionId, Icon> TOOLBAR_ICONS = new EnumMap<>(LilithActionId.class);
	private static final EnumMap<LilithFrameId, List<Image>> ICON_IMAGES = new EnumMap<>(LilithFrameId.class);

	static
	{
		registerMenuIcon(LilithActionId.FIND, FIND_MENU_ICON);
		registerMenuIcon(LilithActionId.DISCONNECT, DISCONNECT_MENU_ICON);
		registerMenuIcon(LilithActionId.PREFERENCES, PREFERENCES_MENU_ICON);
		registerMenuIcon(LilithActionId.SCROLL_TO_BOTTOM, TAIL_MENU_ICON);
		registerMenuIcon(LilithActionId.CLEAR, CLEAR_MENU_ICON);
		registerMenuIcon(LilithActionId.CLEAN_ALL_INACTIVE_LOGS, CLEAR_MENU_ICON);
		registerMenuIcon(LilithActionId.LOVE, LOVE_MENU_ICON);
		registerMenuIcon(LilithActionId.CHECK_FOR_UPDATE, CHECK_FOR_UPDATE_MENU_ICON);
		registerMenuIcon(LilithActionId.EXIT, EXIT_MENU_ICON);
		registerMenuIcon(LilithActionId.TIP_OF_THE_DAY, TOTD_ICON);
		registerMenuIcon(LilithActionId.HELP, HELP_MENU_ICON);
		registerMenuIcon(LilithActionId.EXPORT, EXPORT_MENU_ICON);
		registerMenuIcon(LilithActionId.OPEN, OPEN_MENU_ICON);
		registerMenuIcon(LilithActionId.OPEN_INACTIVE, OPEN_MENU_ICON);
		registerMenuIcon(LilithActionId.IMPORT, OPEN_MENU_ICON);
		registerMenuIcon(LilithActionId.FIND_PREVIOUS, FIND_PREV_MENU_ICON);
		registerMenuIcon(LilithActionId.FIND_PREVIOUS_ACTIVE, FIND_PREV_MENU_ICON);
		registerMenuIcon(LilithActionId.FIND_NEXT, FIND_NEXT_MENU_ICON);
		registerMenuIcon(LilithActionId.FIND_NEXT_ACTIVE, FIND_NEXT_MENU_ICON);

		registerToolbarIcon(LilithActionId.FIND, FIND_TOOLBAR_ICON);
		registerToolbarIcon(LilithActionId.DISCONNECT, DISCONNECT_TOOLBAR_ICON);
		registerToolbarIcon(LilithActionId.PREFERENCES, PREFERENCES_TOOLBAR_ICON);
		registerToolbarIcon(LilithActionId.SCROLL_TO_BOTTOM, TAIL_TOOLBAR_ICON);
		registerToolbarIcon(LilithActionId.CLEAR, CLEAR_TOOLBAR_ICON);
		registerToolbarIcon(LilithActionId.LOVE, LOVE_TOOLBAR_ICON);

		registerIconImage(LilithFrameId.HELP, HELP_MENU_ICON.getImage());
		registerIconImage(LilithFrameId.MAIN, FRAME_ICON.getImage());
		// TODO: ViewContainer / LoggingViewStateIcons.resolveIconForState
	}

	private static void registerIconImage(LilithFrameId id, Image image)
	{
		Objects.requireNonNull(id, "id must not be null!");
		Objects.requireNonNull(image, "image must not be null!");

		List<Image> list = ICON_IMAGES.get(id);
		if(list == null)
		{
			list = new ArrayList<>();
		}
		else
		{
			list = new ArrayList<>(list);
		}
		list.add(image);

		ICON_IMAGES.put(id, Collections.unmodifiableList(list));
	}

	public static List<? extends Image> resolveIconImages(LilithFrameId id)
	{
		return ICON_IMAGES.get(id);
	}

	public static Icon resolveMenuIcon(LilithActionId id)
	{
		Icon result = MENU_ICONS.get(Objects.requireNonNull(id, "id must not be null!"));
		return result == null ? EMPTY_16_ICON : result;
	}

	public static Icon resolveToolbarIcon(LilithActionId id)
	{
		return TOOLBAR_ICONS.get(Objects.requireNonNull(id, "id must not be null!"));
	}

	private static void registerMenuIcon(LilithActionId id, Icon icon)
	{
		MENU_ICONS.put(
				Objects.requireNonNull(id, "id must not be null!"),
				Objects.requireNonNull(icon, "menu icon for "+id+" must not be null!")
		);
	}

	private static void registerToolbarIcon(LilithActionId id, Icon icon)
	{
		TOOLBAR_ICONS.put(
				Objects.requireNonNull(id, "id must not be null!"),
				Objects.requireNonNull(icon, "toolbar icon for "+id+" must not be null!")
		);
	}

	private static ImageIcon resolveImageIcon(String resourcePath)
	{
		URL url = Icons.class.getResource(resourcePath);
		if (url == null)
		{
			throw new IllegalArgumentException("Failed to create ImageIcon from resource '" + resourcePath + "'!");
		}
		return new ImageIcon(url);
	}
}
