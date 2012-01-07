package de.huxhorn.lilith.swing;

import de.huxhorn.sulky.swing.KeyStrokes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.KeyStroke;
import java.util.HashMap;
import java.util.Map;

public class LilithKeyStrokes
{
	private static final Map<String, KeyStroke> actionKeyStrokes=new HashMap<String, KeyStroke>();
	private static final Map<KeyStroke, String> keyStrokeActions=new HashMap<KeyStroke, String>();

	private static void addKeyStroke(String actionName, String keyStrokeString)
	{
		final Logger logger = LoggerFactory.getLogger(LilithKeyStrokes.class);

		KeyStroke keyStroke = KeyStrokes.resolveAcceleratorKeyStroke(keyStrokeString);
		if(keyStroke == null)
		{
			if(logger.isErrorEnabled()) logger.error("KeyStroke '{}' for '{}' did not resolve to a KeyStroke!", keyStrokeString, actionName);
			return;
		}
		String existingActionName = keyStrokeActions.get(keyStroke);
		if(existingActionName != null)
		{
			if(logger.isWarnEnabled()) logger.warn("KeyStroke '{}' is already used for '{}'! Ignoring '{}'.", new Object[]{keyStrokeString, existingActionName, actionName});
			return;
		}
		KeyStroke existingKeyStroke = actionKeyStrokes.get(actionName);
		if(existingKeyStroke != null)
		{
			if(logger.isWarnEnabled()) logger.warn("Duplicate entry for '{}'! Won't overwrite '{}' with '{}'.", new Object[]{actionName, existingKeyStroke, keyStroke});
			return;
		}
		actionKeyStrokes.put(actionName, keyStroke);
		keyStrokeActions.put(keyStroke, actionName);
	}

	public static final String ATTACH_ACTION = "ATTACH_ACTION";
	public static final String CLEAN_ALL_INACTIVE_LOGS_ACTION = "CLEAN_ALL_INACTIVE_LOGS_ACTION";
	public static final String CLOSE_ALL_ACTION = "CLOSE_ALL_ACTION";
	public static final String CLOSE_FILTER_ACTION = "CLOSE_FILTER_ACTION";
	public static final String CLOSE_OTHER_FILTERS_ACTION = "CLOSE_OTHER_FILTERS_ACTION";
	public static final String COPY_SELECTION_ACTION = "COPY_SELECTION_ACTION";
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
	public static final String HELP_ACTION = "HELP_ACTION";
	public static final String IMPORT_ACTION = "IMPORT_ACTION";
	public static final String NEXT_TAB_ACTION = "NEXT_TAB_ACTION";
	public static final String OPEN_ACTION = "OPEN_ACTION";
	public static final String OPEN_INACTIVE_ACTION = "OPEN_INACTIVE_ACTION";
	public static final String PAUSE_ACTION = "PAUSE_ACTION";
	public static final String PREFERENCES_ACTION = "PREFERENCES_ACTION";
	public static final String PREVIOUS_TAB_ACTION = "PREVIOUS_TAB_ACTION";
	public static final String REMOVE_INACTIVE_ACTION = "REMOVE_INACTIVE_ACTION";
	public static final String REPLACE_FILTER_ACTION = "REPLACE_FILTER_ACTION";
	public static final String RESET_FIND_ACTION = "RESET_FIND_ACTION";
	public static final String SCROLL_TO_BOTTOM_ACTION = "SCROLL_TO_BOTTOM_ACTION";
	public static final String VIEW_GLOBAL_ACCESS_LOGS_ACTION = "VIEW_GLOBAL_ACCESS_LOGS_ACTION";
	public static final String VIEW_GLOBAL_CLASSIC_LOGS_ACTION = "VIEW_GLOBAL_CLASSIC_LOGS_ACTION";
	public static final String VIEW_LILITH_LOGS_ACTION = "VIEW_LILITH_LOGS_ACTION";
	public static final String ZOOM_IN_ACTION = "ZOOM_IN_ACTION";
	public static final String ZOOM_OUT_ACTION = "ZOOM_OUT_ACTION";

	static
	{
		addKeyStroke(ATTACH_ACTION, KeyStrokes.COMMAND_ALIAS + " shift A");
		addKeyStroke(CLEAN_ALL_INACTIVE_LOGS_ACTION, KeyStrokes.COMMAND_ALIAS + " shift X");
		addKeyStroke(CLOSE_ALL_ACTION, KeyStrokes.COMMAND_ALIAS+" shift alt W");
		addKeyStroke(CLOSE_FILTER_ACTION, KeyStrokes.COMMAND_ALIAS + " W");
		addKeyStroke(CLOSE_OTHER_FILTERS_ACTION, KeyStrokes.COMMAND_ALIAS + " shift W");
		addKeyStroke(COPY_SELECTION_ACTION, KeyStrokes.COMMAND_ALIAS + " C");
		addKeyStroke(DISCONNECT_ACTION, KeyStrokes.COMMAND_ALIAS + " shift D");
		addKeyStroke(EDIT_CONDITION_ACTION, KeyStrokes.COMMAND_ALIAS + " I");
		addKeyStroke(EDIT_SOURCE_NAME_ACTION, KeyStrokes.COMMAND_ALIAS + " B");
		addKeyStroke(ENTER, "ENTER");
		addKeyStroke(ESCAPE, "ESCAPE");
		addKeyStroke(EXIT_ACTION, KeyStrokes.COMMAND_ALIAS + " Q");
		addKeyStroke(EXPORT_ACTION, KeyStrokes.COMMAND_ALIAS + " shift E");
		addKeyStroke(FIND_ACTION, KeyStrokes.COMMAND_ALIAS + " F");
		addKeyStroke(FIND_NEXT_ACTION, KeyStrokes.COMMAND_ALIAS + " shift G");
		addKeyStroke(FIND_NEXT_ACTIVE_ACTION, KeyStrokes.COMMAND_ALIAS + " shift L");
		addKeyStroke(FIND_PREVIOUS_ACTION, KeyStrokes.COMMAND_ALIAS + " G");
		addKeyStroke(FIND_PREVIOUS_ACTIVE_ACTION, KeyStrokes.COMMAND_ALIAS + " L");
		addKeyStroke(FOCUS_EVENTS_ACTION, KeyStrokes.COMMAND_ALIAS + " E");
		addKeyStroke(FOCUS_MESSAGE_ACTION, KeyStrokes.COMMAND_ALIAS + " M");
		addKeyStroke(HELP_ACTION, "F1");
		addKeyStroke(IMPORT_ACTION, KeyStrokes.COMMAND_ALIAS + " shift I");
		addKeyStroke(NEXT_TAB_ACTION, KeyStrokes.COMMAND_ALIAS + " K");
		addKeyStroke(OPEN_ACTION, KeyStrokes.COMMAND_ALIAS + " O");
		addKeyStroke(OPEN_INACTIVE_ACTION, KeyStrokes.COMMAND_ALIAS + " shift O");
		addKeyStroke(PAUSE_ACTION, KeyStrokes.COMMAND_ALIAS + " P");
		addKeyStroke(PREFERENCES_ACTION, KeyStrokes.COMMAND_ALIAS + " COMMA");
		addKeyStroke(PREVIOUS_TAB_ACTION, KeyStrokes.COMMAND_ALIAS + " J");
		addKeyStroke(REMOVE_INACTIVE_ACTION, KeyStrokes.COMMAND_ALIAS + " R");
		addKeyStroke(REPLACE_FILTER_ACTION, "shift ENTER");
		addKeyStroke(RESET_FIND_ACTION, KeyStrokes.COMMAND_ALIAS + " shift F");
		addKeyStroke(SCROLL_TO_BOTTOM_ACTION, KeyStrokes.COMMAND_ALIAS + " T");
		addKeyStroke(VIEW_GLOBAL_ACCESS_LOGS_ACTION, KeyStrokes.COMMAND_ALIAS + " 2");
		addKeyStroke(VIEW_GLOBAL_CLASSIC_LOGS_ACTION, KeyStrokes.COMMAND_ALIAS + " 1");
		addKeyStroke(VIEW_LILITH_LOGS_ACTION, KeyStrokes.COMMAND_ALIAS + " 0");
		addKeyStroke(ZOOM_IN_ACTION, KeyStrokes.COMMAND_ALIAS + " PERIOD");
		addKeyStroke(ZOOM_OUT_ACTION, KeyStrokes.COMMAND_ALIAS + " shift PERIOD");
	}

	public static KeyStroke getKeyStroke(String actionName)
	{
		return actionKeyStrokes.get(actionName);
	}

	public static String getActionName(KeyStroke keyStroke)
	{
		return keyStrokeActions.get(keyStroke);
	}
}
