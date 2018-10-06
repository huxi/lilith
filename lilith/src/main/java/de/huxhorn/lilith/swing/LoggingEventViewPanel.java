/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.lilith.swing.table.EventWrapperViewTable;
import de.huxhorn.lilith.swing.table.LoggingEventViewTable;
import de.huxhorn.lilith.swing.table.model.EventWrapperTableModel;
import de.huxhorn.sulky.buffers.Buffer;

public class LoggingEventViewPanel
	extends EventWrapperViewPanel<LoggingEvent>
{
	private static final long serialVersionUID = -4953548772213895981L;

	public LoggingEventViewPanel(MainFrame mainFrame, EventSource<LoggingEvent> eventSource)
	{
		super(mainFrame, eventSource);
	}

	@Override
	protected EventWrapperTableModel<LoggingEvent> createTableModel(Buffer<EventWrapper<LoggingEvent>> buffer)
	{
		return new EventWrapperTableModel<>(buffer);
	}

	@Override
	protected EventWrapperViewTable<LoggingEvent> createTable(EventWrapperTableModel<LoggingEvent> tableModel)
	{
		return new LoggingEventViewTable(getMainFrame(), tableModel, getEventSource().isGlobal());
	}

	@Override
	protected void closeConnection(SourceIdentifier sourceIdentifier)
	{
		getMainFrame().closeLoggingConnection(sourceIdentifier);
	}
}
