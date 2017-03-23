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

public enum LilithActionId
{
	ABOUT("About…"),
	ATTACH("Attach/Detach", "Attach or detach the current view."),
	CHECK_FOR_UPDATE("Check for Update…"),
	CLEAN_ALL_INACTIVE_LOGS("Clean all inactive logs", (int) 'c', null),
	CLEAR("Clear", "Clear this view."),
	CLEAR_RECENT_FILES("Clear Recent Files", (int) 'c', null),
	CLOSE_ALL("Close all"),
	CLOSE_ALL_FILTERS("Close all filters", (int) 'a', null),
	CLOSE_ALL_OTHER("Close all other"),
	CLOSE_FILTER("Close this filter", (int) 'c', null),
	CLOSE_OTHER_FILTERS("Close all other filters", (int) 'o', null),
	COPY_CALL_LOCATION("Copy call location", "Copies the call location, i.e. the first element of the logging events call stack, to the clipboard."),
	COPY_CALL_STACK("Copy call stack", "Copies the call stack of the logging event to the clipboard."),
	COPY_HTML("Copy event as HTML", "Copies the HTML code of this events details view to the clipboard."),
	COPY_JSON("Copy event as JSON", "Copies the JSON representation of the event to the clipboard."),
	COPY_LOGGER_NAME("Copy logger name", "Copies the logger name of the logging event to the clipboard."),
	COPY_MARKER("Copy Marker", "Copies the Marker hierarchy of the logging event to the clipboard."),
	COPY_MDC("Copy MDC", "Copies the Mapped Diagnostic Context of the logging event to the clipboard."),
	COPY_MESSAGE("Copy message", "Copies the message of the logging event to the clipboard."),
	COPY_MESSAGE_PATTERN("Copy message pattern", "Copies the message pattern of the logging event to the clipboard."),
	COPY_NDC("Copy NDC", "Copies the Nested Diagnostic Context of the logging event to the clipboard."),
	COPY_RESPONSE_HEADERS("Copy response headers", "Copies the response headers of the access event to the clipboard."),
	COPY_REQUEST_HEADERS("Copy request headers", "Copies the request headers of the access event to the clipboard."),
	COPY_REQUEST_PARAMETERS("Copy request parameters", "Copies the request parameters of the access event to the clipboard."),
	COPY_REQUEST_URI("Copy request URI", "Copies the request URI of the access event to the clipboard."),
	COPY_REQUEST_URL("Copy request URL", "Copies the request URL of the access event to the clipboard."),
	COPY_SELECTION("Copy selection", "Copies the selection to the clipboard."),
	COPY_THREAD_GROUP_NAME("Copy thread group name", "Copies the thread group name of the logging event to the clipboard."),
	COPY_THREAD_NAME("Copy thread name", "Copies the thread name of the logging event to the clipboard."),
	COPY_THROWABLE("Copy Throwable", "Copies the Throwable of the logging event to the clipboard."),
	COPY_THROWABLE_NAME("Copy Throwable name", "Copies the Throwable class name of the logging event to the clipboard."),
	COPY_XML("Copy event as XML", "Copies the XML representation of the event to the clipboard."),
	DEBUG("Debug…", "Create various events and send them to all available receivers. Useful during development."),
	DISCONNECT("Disconnect", "Terminates this connection."),
	SAVE_CONDITION("Save condition…", "Save the condition of the current view."),
	EDIT_SOURCE_NAME("Edit source name…", "Edit the source name of the current view."),
	EXIT("Exit", (int) 'x', "Exit Lilith."),
	EXPORT("Export…", (int) 'e', null),
	FIND("Find", "Opens the Find panel."),
	FIND_NEXT("Find next", "Find next match of the current filter."),
	FIND_NEXT_ACTIVE("Find next active", "Find next match of any active condition."),
	FIND_PREVIOUS("Find previous", "Find previous match of the current filter."),
	FIND_PREVIOUS_ACTIVE("Find previous active", "Find previous match of any active condition."),
	FOCUS_EVENTS("Focus events", "Focus the table containing the events."),
	FOCUS_MESSAGE("Focus message", "Focus detailed message view."),
	GO_TO_SOURCE("Go to source", "Show source in IDE if Lilith plugin is installed."),
	HELP("Help Topics…"),
	IMPORT("Import…", (int) 'i', null),
	LOVE("Show some Love…"),
	MINIMIZE_ALL("Minimize all"),
	MINIMIZE_OTHER("Minimize all other"),
	NEXT_VIEW("Next view"),
	OPEN("Open…", (int) 'o', null),
	OPEN_INACTIVE("Open inactive log…", (int) 'n', null),
	PASTE_STACK_TRACE_ELEMENT("Paste StackTraceElement", "Paste StackTraceElement from clipboard and open code in IDE if Lilith plugin is installed."),
	PAUSE("Pause", "Pause or resume receiving of events."),
	PREFERENCES("Preferences…", (int) 'p', "Open Preferences."),
	PREVIOUS_VIEW("Previous view"),
	REMOVE_INACTIVE("Remove inactive", (int) 'r', null),
	REPLACE_FILTER(null, "Replace filter."),
	RESET_FIND("Reset find"),
	RESET_LAYOUT("Reset layout"),
	RESET_ZOOM("Reset Zoom", "Reset Zoom of the details view."),
	SAVE_LAYOUT("Save layout"),
	SCROLL_TO_BOTTOM("Tail", "Tail (\"scroll to bottom\")"),
	SHOW_UNFILTERED_EVENT("Show unfiltered", "Show selected event in unfiltered view."),
	TASK_MANAGER("Task Manager…"),
	TIP_OF_THE_DAY("Tip of the Day…", "Shows you a Tip of the Day."),
	TROUBLESHOOTING("Troubleshooting…"),
	VIEW_GLOBAL_ACCESS_LOGS(),
	VIEW_GLOBAL_CLASSIC_LOGS(),
	VIEW_LILITH_LOGS(),
	ZOOM_IN("Zoom in", "Zoom in on the details view."),
	ZOOM_OUT("Zoom out", "Zoom out on the details view.");

	private final String text;
	private final Integer mnemonic;
	private final String description;

	LilithActionId()
	{
		this(null);
	}

	LilithActionId(String text)
	{
		this(text, null, null);
	}

	LilithActionId(String text, String description)
	{
		this(text, null, description);
	}

	LilithActionId(String text, Integer mnemonic, String description)
	{
		this.text = text;
		this.mnemonic = mnemonic;
		this.description = description;
	}

	public String getText()
	{
		return text;
	}

	public String getDescription()
	{
		return description;
	}

	public Integer getMnemonic()
	{
		return mnemonic;
	}
}
