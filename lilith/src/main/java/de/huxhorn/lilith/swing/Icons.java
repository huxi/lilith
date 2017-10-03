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
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public final class Icons
{
	public static final ImageIcon ADD_16_ICON = resolveImageIcon("/tango/16x16/actions/list-add.png");
	public static final ImageIcon ATTACH_MENU_ICON = resolveImageIcon("/tango/16x16/actions/edit-undo.png");
	public static final ImageIcon ATTACH_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/edit-undo.png");
	public static final ImageIcon CLOSE_16_ICON = resolveImageIcon("/tango/16x16/emblems/emblem-unreadable.png");
	public static final ImageIcon DETACH_MENU_ICON = resolveImageIcon("/tango/16x16/actions/edit-redo.png");
	public static final ImageIcon DETACH_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/edit-redo.png");
	public static final ImageIcon DIALOG_INFO_ICON = resolveImageIcon("/tango/32x32/status/dialog-information.png");
	public static final ImageIcon DIALOG_WARNING_ICON = resolveImageIcon("/tango/32x32/status/dialog-warning.png");
	public static final ImageIcon PAUSED_MENU_ICON = resolveImageIcon("/tango/16x16/actions/media-playback-start.png");
	public static final ImageIcon PAUSED_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/media-playback-start.png");
	public static final ImageIcon PROGRESS_ICON = resolveImageIcon("/otherGraphics/Progress16.gif");
	public static final ImageIcon REMOVE_16_ICON = resolveImageIcon("/tango/16x16/actions/list-remove.png");
	public static final ImageIcon STOP_16_ICON = resolveImageIcon("/tango/16x16/actions/process-stop.png");
	public static final ImageIcon UNPAUSED_MENU_ICON = resolveImageIcon("/tango/16x16/actions/media-playback-pause.png");
	public static final ImageIcon UNPAUSED_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/media-playback-pause.png");
	public static final ImageIcon UPDATE_AVAILABLE_32_ICON = resolveImageIcon("/tango/32x32/status/software-update-available.png");
	public static final ImageIcon LILITH_SYSTEM_TRAY_ICON = resolveImageIcon("/lilith-tray-icon.png");

	private static final ImageIcon CHECK_FOR_UPDATE_MENU_ICON = resolveImageIcon("/tango/16x16/status/software-update-available.png");
	private static final ImageIcon CLEAR_MENU_ICON = resolveImageIcon("/tango/16x16/actions/edit-clear.png");
	private static final ImageIcon CLEAR_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/edit-clear.png");
	private static final ImageIcon DISCONNECT_MENU_ICON = resolveImageIcon("/tango/16x16/actions/media-eject.png");
	private static final ImageIcon DISCONNECT_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/media-eject.png");
	private static final ImageIcon EMPTY_MENU_ICON = resolveImageIcon("/otherGraphics/empty16.png");
	private static final ImageIcon EXIT_MENU_ICON = resolveImageIcon("/tango/16x16/actions/system-log-out.png");
	private static final ImageIcon EXPORT_MENU_ICON = resolveImageIcon("/tango/16x16/actions/document-save.png");
	private static final ImageIcon FIND_MENU_ICON = resolveImageIcon("/tango/16x16/actions/edit-find.png");
	private static final ImageIcon FIND_NEXT_MENU_ICON = resolveImageIcon("/tango/16x16/actions/go-down.png");
	private static final ImageIcon FIND_NEXT_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/go-down.png");
	private static final ImageIcon FIND_PREVIOUS_MENU_ICON = resolveImageIcon("/tango/16x16/actions/go-up.png");
	private static final ImageIcon FIND_PREVIOUS_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/go-up.png");
	private static final ImageIcon FIND_NEXT_ACTIVE_MENU_ICON = resolveImageIcon("/tango/16x16/actions/go-down-active.png");
	private static final ImageIcon FIND_NEXT_ACTIVE_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/go-down-active.png");
	private static final ImageIcon FIND_PREVIOUS_ACTIVE_MENU_ICON = resolveImageIcon("/tango/16x16/actions/go-up-active.png");
	private static final ImageIcon FIND_PREVIOUS_ACTIVE_TOOLBAR_ICON = resolveImageIcon("/tango/32x32/actions/go-up-active.png");
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

	private static final ImageIcon VIEW_STATE_GLOBAL_ICON = resolveImageIcon("/tango/16x16/categories/applications-internet.png");
	private static final ImageIcon VIEW_STATE_ACTIVE_ICON = resolveImageIcon("/tango/16x16/status/network-receive.png");
	private static final ImageIcon VIEW_STATE_INACTIVE_ICON = resolveImageIcon("/tango/16x16/status/network-offline.png");
	private static final ImageIcon VIEW_STATE_UPDATING_FILE_ICON = resolveImageIcon("/tango/16x16/emotes/face-grin.png");
	private static final ImageIcon VIEW_STATE_STALE_FILE_ICON = resolveImageIcon("/tango/16x16/emotes/face-grin.png");

	private static final EnumMap<LilithActionId, Icon> MENU_ICONS = new EnumMap<>(LilithActionId.class);
	private static final EnumMap<LilithActionId, Icon> TOOLBAR_ICONS = new EnumMap<>(LilithActionId.class);
	private static final EnumMap<LilithFrameId, ImageIcon> FRAME_ICONS = new EnumMap<>(LilithFrameId.class);
	private static final EnumMap<LilithFrameId, List<Image>> FRAME_ICON_IMAGES = new EnumMap<>(LilithFrameId.class);

	static
	{
		new Icons(); // coverage report shall stfu
		registerMenuIcon(LilithActionId.CHECK_FOR_UPDATE, CHECK_FOR_UPDATE_MENU_ICON);
		registerMenuIcon(LilithActionId.CLEAN_ALL_INACTIVE_LOGS, CLEAR_MENU_ICON);
		registerMenuIcon(LilithActionId.CLEAR, CLEAR_MENU_ICON);
		registerMenuIcon(LilithActionId.DISCONNECT, DISCONNECT_MENU_ICON);
		registerMenuIcon(LilithActionId.EXIT, EXIT_MENU_ICON);
		registerMenuIcon(LilithActionId.EXPORT, EXPORT_MENU_ICON);
		registerMenuIcon(LilithActionId.FIND, FIND_MENU_ICON);
		registerMenuIcon(LilithActionId.FIND_NEXT, FIND_NEXT_MENU_ICON);
		registerMenuIcon(LilithActionId.FIND_NEXT_ACTIVE, FIND_NEXT_ACTIVE_MENU_ICON);
		registerMenuIcon(LilithActionId.FIND_PREVIOUS, FIND_PREVIOUS_MENU_ICON);
		registerMenuIcon(LilithActionId.FIND_PREVIOUS_ACTIVE, FIND_PREVIOUS_ACTIVE_MENU_ICON);
		registerMenuIcon(LilithActionId.HELP_TOPICS, HELP_MENU_ICON);
		registerMenuIcon(LilithActionId.IMPORT, OPEN_MENU_ICON);
		registerMenuIcon(LilithActionId.LOVE, LOVE_MENU_ICON);
		registerMenuIcon(LilithActionId.OPEN, OPEN_MENU_ICON);
		registerMenuIcon(LilithActionId.OPEN_INACTIVE, OPEN_MENU_ICON);
		registerMenuIcon(LilithActionId.PREFERENCES, PREFERENCES_MENU_ICON);
		registerMenuIcon(LilithActionId.TAIL, TAIL_MENU_ICON);
		registerMenuIcon(LilithActionId.TIP_OF_THE_DAY, TOTD_ICON);

		registerToolbarIcon(LilithActionId.CLEAR, CLEAR_TOOLBAR_ICON);
		registerToolbarIcon(LilithActionId.DISCONNECT, DISCONNECT_TOOLBAR_ICON);
		registerToolbarIcon(LilithActionId.FIND, FIND_TOOLBAR_ICON);
		registerToolbarIcon(LilithActionId.FIND_NEXT, FIND_NEXT_TOOLBAR_ICON);
		registerToolbarIcon(LilithActionId.FIND_PREVIOUS, FIND_PREVIOUS_TOOLBAR_ICON);
		registerToolbarIcon(LilithActionId.FIND_NEXT_ACTIVE, FIND_NEXT_ACTIVE_TOOLBAR_ICON);
		registerToolbarIcon(LilithActionId.FIND_PREVIOUS_ACTIVE, FIND_PREVIOUS_ACTIVE_TOOLBAR_ICON);
		registerToolbarIcon(LilithActionId.LOVE, LOVE_TOOLBAR_ICON);
		registerToolbarIcon(LilithActionId.PREFERENCES, PREFERENCES_TOOLBAR_ICON);
		registerToolbarIcon(LilithActionId.TAIL, TAIL_TOOLBAR_ICON);

		registerFrameIcon(LilithFrameId.HELP, HELP_MENU_ICON);
		registerFrameIcon(LilithFrameId.MAIN, FRAME_ICON);

		registerFrameIcon(LilithFrameId.VIEW_STATE_GLOBAL, VIEW_STATE_GLOBAL_ICON);
		registerFrameIcon(LilithFrameId.VIEW_STATE_ACTIVE, VIEW_STATE_ACTIVE_ICON);
		registerFrameIcon(LilithFrameId.VIEW_STATE_INACTIVE, VIEW_STATE_INACTIVE_ICON);
		registerFrameIcon(LilithFrameId.VIEW_STATE_UPDATING_FILE, VIEW_STATE_UPDATING_FILE_ICON);
		registerFrameIcon(LilithFrameId.VIEW_STATE_STALE_FILE, VIEW_STATE_STALE_FILE_ICON);

		registerFrameIcon(LilithFrameId.VIEW_STATE_GLOBAL_DISABLED, createDisabledImageIcon(VIEW_STATE_GLOBAL_ICON));
		registerFrameIcon(LilithFrameId.VIEW_STATE_ACTIVE_DISABLED, createDisabledImageIcon(VIEW_STATE_ACTIVE_ICON));
		registerFrameIcon(LilithFrameId.VIEW_STATE_INACTIVE_DISABLED, createDisabledImageIcon(VIEW_STATE_INACTIVE_ICON));
		registerFrameIcon(LilithFrameId.VIEW_STATE_UPDATING_FILE_DISABLED, createDisabledImageIcon(VIEW_STATE_UPDATING_FILE_ICON));
		registerFrameIcon(LilithFrameId.VIEW_STATE_STALE_FILE_DISABLED, createDisabledImageIcon(VIEW_STATE_STALE_FILE_ICON));

		createFrameIconImages();
		// add additional frame icon images manually if necessary

		new Icons(); // stfu
	}


	private Icons() {}

	private static void createFrameIconImages()
	{
		for (Map.Entry<LilithFrameId, ImageIcon> entry : FRAME_ICONS.entrySet())
		{
			LilithFrameId key = entry.getKey();
			ImageIcon value = entry.getValue();
			FRAME_ICON_IMAGES.put(key, Collections.singletonList(value.getImage()));
		}
	}

	private static void registerFrameIcon(LilithFrameId id, ImageIcon icon)
	{
		FRAME_ICONS.put(
				Objects.requireNonNull(id, "id must not be null!"),
				Objects.requireNonNull(icon, "frame icon for "+id+" must not be null!")
		);
	}

	public static List<? extends Image> resolveFrameIconImages(LilithFrameId id)
	{
		return FRAME_ICON_IMAGES.get(id);
	}

	public static Icon resolveFrameIcon(LilithFrameId id)
	{
		return FRAME_ICONS.get(id);
	}

	public static List<? extends Image> resolveFrameIconImages(LoggingViewState state, boolean disabled)
	{
		return resolveFrameIconImages(frameIdForViewState(state, disabled));
	}

	public static Icon resolveFrameIcon(LoggingViewState state, boolean disabled)
	{
		return resolveFrameIcon(frameIdForViewState(state, disabled));
	}

	public static Icon resolveMenuIcon(LilithActionId id)
	{
		Icon result = MENU_ICONS.get(Objects.requireNonNull(id, "id must not be null!"));
		return result == null ? EMPTY_MENU_ICON : result;
	}

	/**
	 *
	 * @return a transparent icon with the proper size for menu entries.
	 */
	public static Icon resolveEmptyMenuIcon()
	{
		return EMPTY_MENU_ICON;
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

	static ImageIcon resolveImageIcon(String resourcePath)
	{
		URL url = Icons.class.getResource(Objects.requireNonNull(resourcePath,
				"resourcePath must not be null!"));
		if (url == null)
		{
			throw new IllegalArgumentException("Failed to create ImageIcon from resource '" + resourcePath + "'!");
		}
		return new ImageIcon(url);
	}

	static LilithFrameId frameIdForViewState(LoggingViewState state, boolean disabled)
	{
		if (state == null)
		{
			if (disabled)
			{
				return LilithFrameId.VIEW_STATE_GLOBAL_DISABLED;
			}
			return LilithFrameId.VIEW_STATE_GLOBAL;
		}
		if(state == LoggingViewState.ACTIVE)
		{
			if (disabled)
			{
				return LilithFrameId.VIEW_STATE_ACTIVE_DISABLED;
			}
			return LilithFrameId.VIEW_STATE_ACTIVE;
		}
		if(state == LoggingViewState.INACTIVE)
		{
			if (disabled)
			{
				return LilithFrameId.VIEW_STATE_INACTIVE_DISABLED;
			}
			return LilithFrameId.VIEW_STATE_INACTIVE;
		}
		if(state == LoggingViewState.UPDATING_FILE)
		{
			if (disabled)
			{
				return LilithFrameId.VIEW_STATE_UPDATING_FILE_DISABLED;
			}
			return LilithFrameId.VIEW_STATE_UPDATING_FILE;
		}

		// must be STALE_FILE, otherwise tests would fail
		if (disabled)
		{
			return LilithFrameId.VIEW_STATE_STALE_FILE_DISABLED;
		}
		return LilithFrameId.VIEW_STATE_STALE_FILE;
	}

	private static ImageIcon createDisabledImageIcon(ImageIcon icon)
	{
		return new ImageIcon(GrayFilter.createDisabledImage(icon.getImage()));
	}
}
