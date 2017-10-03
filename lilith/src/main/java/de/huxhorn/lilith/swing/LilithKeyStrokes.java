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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.swing.KeyStroke;

public final class LilithKeyStrokes
{
	private static final Map<String, String> UNPROCESSED_KEY_STROKE_STRINGS =new HashMap<>();
	private static final Map<String, KeyStroke> ACTION_KEY_STROKES =new HashMap<>();
	private static final Map<KeyStroke, String> KEY_STROKE_ACTIONS =new HashMap<>();

	public static final String ENTER = "ENTER";
	public static final String ESCAPE = "ESCAPE";

	private LilithKeyStrokes() {}

	static void addKeyStroke(String keyStrokeString, String actionName)
	{
		Objects.requireNonNull(keyStrokeString, "keyStrokeString must not be null!");
		Objects.requireNonNull(actionName, "actionName must not be null!");
		KeyStroke keyStroke = KeyStrokes.resolveAcceleratorKeyStroke(keyStrokeString);
		if(keyStroke == null)
		{
			throw new IllegalArgumentException("keyStrokeString '"+keyStrokeString+"' did not resolve to a KeyStroke!");
		}

		KeyStroke existingKeyStroke = ACTION_KEY_STROKES.get(actionName);
		if(existingKeyStroke != null)
		{
			throw new IllegalStateException("Duplicate action name entry '"+actionName+"'!");
		}

		String existingActionName = KEY_STROKE_ACTIONS.get(keyStroke);
		if(existingActionName != null)
		{
			throw new IllegalStateException("Duplicate action name entry for '"+keyStrokeString+"': '"+existingActionName+"' and '"+actionName+"'");
		}

		UNPROCESSED_KEY_STROKE_STRINGS.put(actionName, keyStrokeString);
		ACTION_KEY_STROKES.put(actionName, keyStroke);
		KEY_STROKE_ACTIONS.put(keyStroke, actionName);
	}

	private static void addKeyStroke(String keyStrokeString, LilithActionId id)
	{
		addKeyStroke(keyStrokeString, id.name());
	}

	static
	{
		new LilithKeyStrokes(); // coverage report shall stfu
		addKeyStroke("ENTER", ENTER);
		addKeyStroke("ESCAPE", ESCAPE);
		addKeyStroke("F1", LilithActionId.HELP_TOPICS);
		addKeyStroke("shift ENTER", LilithActionId.REPLACE_FILTER);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " 0", LilithActionId.VIEW_LILITH_LOGS);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " 1", LilithActionId.VIEW_GLOBAL_CLASSIC_LOGS);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " 2", LilithActionId.VIEW_GLOBAL_ACCESS_LOGS);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " B", LilithActionId.EDIT_SOURCE_NAME);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " C", LilithActionId.COPY_SELECTION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " D", LilithActionId.GO_TO_SOURCE);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " COMMA", LilithActionId.PREFERENCES);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " E", LilithActionId.FOCUS_EVENTS);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " F", LilithActionId.FIND);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " G", LilithActionId.FIND_PREVIOUS);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " I", LilithActionId.SAVE_CONDITION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " J", LilithActionId.NEXT_VIEW);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " K", LilithActionId.CLEAR);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " L", LilithActionId.FIND_PREVIOUS_ACTIVE);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " M", LilithActionId.FOCUS_MESSAGE);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " O", LilithActionId.OPEN);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " PERIOD", LilithActionId.ZOOM_IN);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " Q", LilithActionId.EXIT);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " R", LilithActionId.REMOVE_INACTIVE);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " T", LilithActionId.TAIL);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " U", LilithActionId.SHOW_UNFILTERED_EVENT);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " W", LilithActionId.CLOSE_FILTER);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift A", LilithActionId.ATTACH);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift C", LilithActionId.COPY_MESSAGE);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift D", LilithActionId.DISCONNECT);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift E", LilithActionId.EXPORT);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift F", LilithActionId.RESET_FIND);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift G", LilithActionId.FIND_NEXT);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift I", LilithActionId.IMPORT);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift J", LilithActionId.PREVIOUS_VIEW);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift L", LilithActionId.FIND_NEXT_ACTIVE);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift N", LilithActionId.COPY_LOGGER_NAME);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift O", LilithActionId.OPEN_INACTIVE);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift PERIOD", LilithActionId.ZOOM_OUT);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift S", LilithActionId.COPY_CALL_LOCATION);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift T", LilithActionId.COPY_THROWABLE);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift V", LilithActionId.PASTE_STACK_TRACE_ELEMENT);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift W", LilithActionId.CLOSE_OTHER_FILTERS);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift X", LilithActionId.CLEAN_ALL_INACTIVE_LOGS);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift alt C", LilithActionId.COPY_MESSAGE_PATTERN);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift alt S", LilithActionId.COPY_CALL_STACK);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift alt T", LilithActionId.COPY_THROWABLE_NAME);
		addKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift alt W", LilithActionId.CLOSE_ALL);

		new LilithKeyStrokes(); // stfu
	}

	static Set<String> getActionNames()
	{
		return Collections.unmodifiableSet(UNPROCESSED_KEY_STROKE_STRINGS.keySet());
	}

	public static String getUnprocessedKeyStrokeString(String actionName)
	{
		return UNPROCESSED_KEY_STROKE_STRINGS.get(actionName);
	}

	public static KeyStroke getKeyStroke(String actionName)
	{
		return ACTION_KEY_STROKES.get(actionName);
	}

	public static KeyStroke getKeyStroke(LilithActionId id)
	{
		return ACTION_KEY_STROKES.get(id.name());
	}

	public static String getKeyStrokeString(LilithActionId id)
	{
		KeyStroke result = ACTION_KEY_STROKES.get(id.name());
		if(result == null)
		{
			return null;
		}
		return result.toString();
	}

	public static String getActionName(KeyStroke keyStroke)
	{
		return KEY_STROKE_ACTIONS.get(keyStroke);
	}
}
