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

import de.huxhorn.sulky.swing.KeyStrokes;
import java.util.HashMap;
import java.util.Map;
import javax.swing.KeyStroke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LilithKeyStrokes
{
	private static final Logger LOGGER = LoggerFactory.getLogger(LilithKeyStrokes.class);
	private static final Map<String, KeyStroke> ACTION_KEY_STROKES =new HashMap<>();
	private static final Map<KeyStroke, String> KEY_STROKE_ACTIONS =new HashMap<>();

	private static void addKeyStroke(String keyStrokeString, String actionName)
	{

		KeyStroke keyStroke = KeyStrokes.resolveAcceleratorKeyStroke(keyStrokeString);
		if(keyStroke == null)
		{
			if(LOGGER.isErrorEnabled()) LOGGER.error("KeyStroke '{}' for '{}' did not resolve to a KeyStroke!", keyStrokeString, actionName);
			return;
		}
		String existingActionName = KEY_STROKE_ACTIONS.get(keyStroke);
		if(existingActionName != null)
		{
			if(LOGGER.isWarnEnabled()) LOGGER.warn("KeyStroke '{}' is already used for '{}'! Ignoring '{}'.", keyStrokeString, existingActionName, actionName);
			return;
		}
		KeyStroke existingKeyStroke = ACTION_KEY_STROKES.get(actionName);
		if(existingKeyStroke != null)
		{
			if(LOGGER.isWarnEnabled()) LOGGER.warn("Duplicate entry for '{}'! Won't overwrite '{}' with '{}'.", actionName, existingKeyStroke, keyStroke);
			return;
		}
		ACTION_KEY_STROKES.put(actionName, keyStroke);
		KEY_STROKE_ACTIONS.put(keyStroke, actionName);
	}

	public static final String ATTACH_ACTION = "ATTACH_ACTION";
	public static final String CLEAN_ALL_INACTIVE_LOGS_ACTION = "CLEAN_ALL_INACTIVE_LOGS_ACTION";
	public static final String CLEAR_ACTION = "CLEAR_ACTION";
	public static final String CLOSE_ALL_ACTION = "CLOSE_ALL_ACTION";
	public static final String CLOSE_FILTER_ACTION = "CLOSE_FILTER_ACTION";
	public static final String CLOSE_OTHER_FILTERS_ACTION = "CLOSE_OTHER_FILTERS_ACTION";
	public static final String COPY_CALL_LOCATION_ACTION = "COPY_CALL_LOCATION_ACTION";
	public static final String COPY_CALL_STACK_ACTION = "COPY_CALL_STACK_ACTION";
	public static final String COPY_LOGGER_NAME_ACTION = "COPY_LOGGER_NAME_ACTION";
	public static final String COPY_MESSAGE_ACTION = "COPY_MESSAGE_ACTION";
	public static final String COPY_MESSAGE_PATTERN_ACTION = "COPY_MESSAGE_PATTERN_ACTION";
	public static final String COPY_SELECTION_ACTION = "COPY_SELECTION_ACTION";
	public static final String COPY_THROWABLE_ACTION = "COPY_THROWABLE_ACTION";
	public static final String COPY_THROWABLE_NAME_ACTION = "COPY_THROWABLE_NAME_ACTION";
	public static final String DISCONNECT_ACTION = "DISCONNECT_ACTION";
	public static final String EDIT_CONDITION_ACTION = "EDIT_CONDITION_ACTION";
	public static final String EDIT_SOURCE_NAME_ACTION = "EDIT_SOURCE_NAME_ACTION";
	public static final String ENTER = "ENTER";
	public static final String ESCAPE = "ESCAPE";
	public static final String EXIT_ACTION = "EXIT_ACTION";
	public static final String EXPORT_ACTION = "EXPORT_ACTION";
	public static final String FIND_ACTION = "FIND_ACTION";
	public static final String FIND_NEXT_ACTION = "FIND_NEXT_ACTION";
	public static final String FIND_NEXT_ACTIVE_ACTION = "FIND_NEXT_ACTIVE_ACTION";
	public static final String FIND_PREVIOUS_ACTION = "FIND_PREVIOUS_ACTION";
	public static final String FIND_PREVIOUS_ACTIVE_ACTION = "FIND_PREVIOUS_ACTIVE_ACTION";
	public static final String FOCUS_EVENTS_ACTION = "FOCUS_EVENTS_ACTION";
	public static final String FOCUS_MESSAGE_ACTION = "FOCUS_MESSAGE_ACTION";
	public static final String GO_TO_SOURCE_ACTION = "GO_TO_SOURCE_ACTION";
	public static final String HELP_ACTION = "HELP_ACTION";
	public static final String IMPORT_ACTION = "IMPORT_ACTION";
	public static final String NEXT_VIEW_ACTION = "NEXT_VIEW_ACTION";
	public static final String OPEN_ACTION = "OPEN_ACTION";
	public static final String OPEN_INACTIVE_ACTION = "OPEN_INACTIVE_ACTION";
	public static final String PASTE_STACK_TRACE_ELEMENT_ACTION = "PASTE_STACK_TRACE_ELEMENT_ACTION";
	public static final String PAUSE_ACTION = "PAUSE_ACTION";
	public static final String PREFERENCES_ACTION = "PREFERENCES_ACTION";
	public static final String PREVIOUS_VIEW_ACTION = "PREVIOUS_VIEW_ACTION";
	public static final String REMOVE_INACTIVE_ACTION = "REMOVE_INACTIVE_ACTION";
	public static final String REPLACE_FILTER_ACTION = "REPLACE_FILTER_ACTION";
	public static final String RESET_FIND_ACTION = "RESET_FIND_ACTION";
	public static final String SCROLL_TO_BOTTOM_ACTION = "SCROLL_TO_BOTTOM_ACTION";
	public static final String SHOW_UNFILTERED_EVENT_ACTION = "SHOW_UNFILTERED_EVENT_ACTION";
	public static final String VIEW_GLOBAL_ACCESS_LOGS_ACTION = "VIEW_GLOBAL_ACCESS_LOGS_ACTION";
	public static final String VIEW_GLOBAL_CLASSIC_LOGS_ACTION = "VIEW_GLOBAL_CLASSIC_LOGS_ACTION";
	public static final String VIEW_LILITH_LOGS_ACTION = "VIEW_LILITH_LOGS_ACTION";
	public static final String ZOOM_IN_ACTION = "ZOOM_IN_ACTION";
	public static final String ZOOM_OUT_ACTION = "ZOOM_OUT_ACTION";

	static
	{
		addKeyStroke("ENTER", ENTER);
		addKeyStroke("ESCAPE", ESCAPE);
		addKeyStroke("F1", HELP_ACTION);
		addKeyStroke("shift ENTER", REPLACE_FILTER_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " 0", VIEW_LILITH_LOGS_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " 1", VIEW_GLOBAL_CLASSIC_LOGS_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " 2", VIEW_GLOBAL_ACCESS_LOGS_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " B", EDIT_SOURCE_NAME_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " C", COPY_SELECTION_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " D", GO_TO_SOURCE_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " COMMA", PREFERENCES_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " E", FOCUS_EVENTS_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " F", FIND_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " G", FIND_PREVIOUS_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " I", EDIT_CONDITION_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " J", NEXT_VIEW_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " K", CLEAR_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " L", FIND_PREVIOUS_ACTIVE_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " M", FOCUS_MESSAGE_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " O", OPEN_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " P", PAUSE_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " PERIOD", ZOOM_IN_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " Q", EXIT_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " R", REMOVE_INACTIVE_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " T", SCROLL_TO_BOTTOM_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " U", SHOW_UNFILTERED_EVENT_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " W", CLOSE_FILTER_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift A", ATTACH_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift C", COPY_MESSAGE_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift D", DISCONNECT_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift E", EXPORT_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift F", RESET_FIND_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift G", FIND_NEXT_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift I", IMPORT_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift J", PREVIOUS_VIEW_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift L", FIND_NEXT_ACTIVE_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift N", COPY_LOGGER_NAME_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift O", OPEN_INACTIVE_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift PERIOD", ZOOM_OUT_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift S", COPY_CALL_LOCATION_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift T", COPY_THROWABLE_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift V", PASTE_STACK_TRACE_ELEMENT_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift W", CLOSE_OTHER_FILTERS_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift X", CLEAN_ALL_INACTIVE_LOGS_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift alt C", COPY_MESSAGE_PATTERN_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift alt S", COPY_CALL_STACK_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift alt T", COPY_THROWABLE_NAME_ACTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift alt W", CLOSE_ALL_ACTION);
	}

	public static KeyStroke getKeyStroke(String actionName)
	{
		return ACTION_KEY_STROKES.get(actionName);
	}

	public static String getActionName(KeyStroke keyStroke)
	{
		return KEY_STROKE_ACTIONS.get(keyStroke);
	}
}
