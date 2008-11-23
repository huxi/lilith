/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
package de.huxhorn.lilith.swing.table;

import de.huxhorn.lilith.data.logging.LoggingEvent;

import de.huxhorn.lilith.swing.table.tooltips.*;
import de.huxhorn.lilith.swing.table.model.LoggingEventTableColumnModel;
import de.huxhorn.lilith.swing.table.model.EventWrapperTableModel;
import de.huxhorn.lilith.swing.table.model.LoggingEventTableColumnModelConstants;

import java.util.HashMap;

public class LoggingEventViewTable
	extends EventWrapperViewTable<LoggingEvent>
{
	public LoggingEventViewTable(EventWrapperTableModel<LoggingEvent> model)
	{
		super(model);
	}

	protected void initTooltipGenerators()
	{
		tooltipGenerators=new HashMap<Object, TooltipGenerator>();
		tooltipGenerators.put(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_LOGGER_NAME,
				new LoggerNameTooltipGenerator());
		tooltipGenerators.put(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_MARKER,
				new MarkerTooltipGenerator());
		tooltipGenerators.put(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_MESSAGE,
				new MessageTooltipGenerator());
		tooltipGenerators.put(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_THREAD,
				new ThreadTooltipGenerator());
		tooltipGenerators.put(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_THROWABLE,
				new ThrowableTooltipGenerator());
		tooltipGenerators.put(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_TIMESTAMP,
				new TimestampTooltipGenerator());
		tooltipGenerators.put(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_APPLICATIION,
				new ApplicationTooltipGenerator());
		tooltipGenerators.put(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_SOURCE,
				new SourceTooltipGenerator());
	}

	protected void initColumnModel()
	{
		setColumnModel(new LoggingEventTableColumnModel());
	}
}
