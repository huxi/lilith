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
	ABOUT("About…", 'a', null),
	ATTACH("Attach/Detach", 'a', "Attach or detach the current view."),
	CHECK_FOR_UPDATE("Check for Update…", 'u', null),
	CLEAN_ALL_INACTIVE_LOGS("Clean all inactive logs", 'c', null),
	CLEAR("Clear", 'c', "Clear this view."),
	CLEAR_RECENT_FILES("Clear Recent Files", 'c', null),
	CLOSE_ALL("Close all", 'c', null),
	CLOSE_ALL_FILTERS("Close all filters", 'l', null),
	CLOSE_ALL_OTHER("Close all other", 'o', null),
	CLOSE_FILTER("Close this filter", 'f', null),
	CLOSE_OTHER_FILTERS("Close all other filters", 'h', null),
	COLUMNS("Columns", 'c', null),
	COPY_CALL_LOCATION("Copy call location", 'l', "Copies the call location, i.e. the first element of the logging events call stack, to the clipboard."),
	COPY_CALL_STACK("Copy call stack", 'k', "Copies the call stack of the logging event to the clipboard."),
	COPY_HTML("Copy event as HTML", 'h', "Copies the HTML code of this events details view to the clipboard."),
	COPY_JSON("Copy event as JSON", 'j', "Copies the JSON representation of the event to the clipboard."),
	COPY_LOGGER_NAME("Copy logger name", 'n', "Copies the logger name of the logging event to the clipboard."),
	COPY_MARKER("Copy Marker", 'r', "Copies the Marker hierarchy of the logging event to the clipboard."),
	COPY_MDC("Copy Mapped Diagnostic Context", 'd', "Copies the Mapped Diagnostic Context (MDC) of the logging event to the clipboard."),
	COPY_MESSAGE("Copy message", 'm', "Copies the message of the logging event to the clipboard."),
	COPY_MESSAGE_PATTERN("Copy message pattern", 'p', "Copies the message pattern of the logging event to the clipboard."),
	COPY_NDC("Copy Nested Diagnostic Context", 'e', "Copies the Nested Diagnostic Context (NDC) of the logging event to the clipboard."),
	COPY_RESPONSE_HEADERS("Copy response headers", 'o', "Copies the response headers of the access event to the clipboard."),
	COPY_REQUEST_HEADERS("Copy request headers", 'q', "Copies the request headers of the access event to the clipboard."),
	COPY_REQUEST_PARAMETERS("Copy request parameters", 'p', "Copies the request parameters of the access event to the clipboard."),
	COPY_REQUEST_URI("Copy request URI", 'i', "Copies the request URI of the access event to the clipboard."),
	COPY_REQUEST_URL("Copy request URL", 'l', "Copies the request URL of the access event to the clipboard."),
	COPY_SELECTION("Copy selection", 's', "Copies the selection to the clipboard."),
	COPY_THREAD_GROUP_NAME("Copy thread group name", 'u', "Copies the thread group name of the logging event to the clipboard."),
	COPY_THREAD_NAME("Copy thread name", 't', "Copies the thread name of the logging event to the clipboard."),
	COPY_THROWABLE("Copy Throwable", 'w', "Copies the Throwable of the logging event to the clipboard."),
	COPY_THROWABLE_NAME("Copy Throwable name", 'o', "Copies the Throwable class name of the logging event to the clipboard."),
	COPY_XML("Copy event as XML", 'x', "Copies the XML representation of the event to the clipboard."),
	CUSTOM_COPY("Custom copy", 'c', null),
	DEBUG("Debug…", 'd', "Create various events and send them to all available receivers. Useful during development."),
	DISCONNECT("Disconnect", 'd', "Terminates this connection."),
	EDIT("Edit", 'e', null),
	EDIT_SOURCE_NAME("Edit source name…", 'i', "Edit the source name of the current view."),
	EXIT("Exit", 'x', "Exit Lilith."),
	EXCLUDE("Exclude", 'x', null),
	EXPORT("Export…", 'e', null),
	FILE("File", 'f', null),
	FIND("Find", 'f', "Opens the Find panel."),
	FIND_NEXT("Find next", 'n', "Find next match of the current filter."),
	FIND_NEXT_ACTIVE("Find next active", 'e', "Find next match of any active condition."),
	FIND_PREVIOUS("Find previous", 'p', "Find previous match of the current filter."),
	FIND_PREVIOUS_ACTIVE("Find previous active", 'v', "Find previous match of any active condition."),
	FOCUS("Focus", 'o', null),
	FOCUS_EVENTS("Focus events", 'e', "Focus the table containing the events."),
	FOCUS_MESSAGE("Focus message", 'm', "Focus detailed message view."),
	GO_TO_SOURCE("Go to source", 'g', "Show source in IDE if Lilith plugin is installed."),
	HELP("Help", 'h', null),
	HELP_TOPICS("Help Topics…", 'h', null),
	IMPORT("Import…", 'i', null),
	LAYOUT("Layout", 'y', null),
	LOVE("Show some Love…", 'l', "Show some Love… You love Lilith, right?"),
	MINIMIZE_ALL("Minimize all", 'm', null),
	MINIMIZE_OTHER("Minimize all other", 'n', null),
	NEXT_VIEW("Next view", 'n', null),
	OPEN("Open…", 'o', null),
	OPEN_INACTIVE("Open inactive log…", 'n', null),
	PASTE_STACK_TRACE_ELEMENT("Paste StackTraceElement", 'a', "Paste StackTraceElement from clipboard and open code in IDE if Lilith plugin is installed."),
	PAUSE("Pause", 'u', "Pause or resume receiving of events."),
	PREFERENCES("Preferences…", 'p', "Open Preferences."),
	PREVIOUS_VIEW("Previous view", 'p', null),
	RECENT_FILES("Recent files", 'r', null),
	REMOVE_INACTIVE("Remove inactive", 'r', null),
	REPLACE_FILTER("Replace filter", 'r' /* for the sake of test correctness */, null),
	RESET_FIND("Reset find", 'r', null),
	RESET_LAYOUT("Reset layout", 'r', null),
	RESET_ZOOM("Reset Zoom", 'r', "Reset Zoom of the details view."),
	SAVE_CONDITION("Save condition…", 's', "Save the condition of the current view."),
	SAVE_LAYOUT("Save layout", 's', null),
	SEARCH("Search", 's', null),
	SHOW_UNFILTERED_EVENT("Show unfiltered", 'u', "Show selected event in unfiltered view."),
	TAIL("Tail", 't', "Tail (\"scroll to bottom\")"),
	TASK_MANAGER("Task Manager…", 't', null),
	TIP_OF_THE_DAY("Tip of the Day…", 't', "Shows you a Tip of the Day."),
	TROUBLESHOOTING("Troubleshooting…", 'o', null),
	VIEW("View", 'v', null),
	VIEW_GLOBAL_ACCESS_LOGS("Global access events", 'a', "Opens the global access events."),
	VIEW_GLOBAL_CLASSIC_LOGS("Global logging events", 'l', "Opens the global logging events."),
	VIEW_LILITH_LOGS("Lilith events", 'i', "Opens the internal Lilith logging events. Take a look here if you have mysterious problems."),
	WINDOW("Window", 'w', null),
	ZOOM_IN("Zoom in", 'z', "Zoom in on the details view."),
	ZOOM_OUT("Zoom out", 'o', "Zoom out on the details view.");

	private final String text;
	private final Character mnemonic;
	private final String description;

	LilithActionId(String text, Character mnemonic, String description)
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
		return (int) mnemonic;
	}
}
