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
package de.huxhorn.lilith.swing;

import de.huxhorn.lilith.data.logging.LoggingEvent;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;

import de.huxhorn.lilith.swing.renderer.*;
import de.huxhorn.lilith.swing.tooltips.*;

public class LoggingEventViewTable
	extends EventWrapperViewTable<LoggingEvent>
{
	public LoggingEventViewTable(EventWrapperTableModelBase<LoggingEvent> model)
	{
		super(model);
	}

	protected void initTooltipGenerators()
	{
		tooltipGenerators=new TooltipGenerator[tableModel.getColumnCount()];
		tooltipGenerators[LoggingEventTableModelConstants.COLUMN_INDEX_LOGGER_NAME]=new LoggerNameTooltipGenerator();
		tooltipGenerators[LoggingEventTableModelConstants.COLUMN_INDEX_MARKER]=new MarkerTooltipGenerator();
		tooltipGenerators[LoggingEventTableModelConstants.COLUMN_INDEX_MESSAGE]=new MessageTooltipGenerator();
		tooltipGenerators[LoggingEventTableModelConstants.COLUMN_INDEX_THREAD]=new ThreadTooltipGenerator();
		tooltipGenerators[LoggingEventTableModelConstants.COLUMN_INDEX_THROWABLE]=new ThrowableTooltipGenerator();
		tooltipGenerators[LoggingEventTableModelConstants.COLUMN_INDEX_TIMESTAMP]=new TimestampTooltipGenerator();
		tooltipGenerators[LoggingEventTableModelConstants.COLUMN_INDEX_APPLICATION]=new ApplicationTooltipGenerator();

		if(tableModel.isGlobal())
		{
			tooltipGenerators[LoggingEventTableModelConstants.COLUMN_INDEX_SOURCE]=new SourceTooltipGenerator();
		}
	}

	protected void initColumnModel()
	{
		TableColumnModel columnModel = getColumnModel();
		{
			TableColumn col = columnModel.getColumn(LoggingEventTableModelConstants.COLUMN_INDEX_ID);
			col.setCellRenderer(new IdRenderer());
		}
		{
			TableColumn col = columnModel.getColumn(LoggingEventTableModelConstants.COLUMN_INDEX_TIMESTAMP);
			col.setCellRenderer(new TimestampRenderer());
		}
		{
			TableColumn col = columnModel.getColumn(LoggingEventTableModelConstants.COLUMN_INDEX_LEVEL);
			col.setCellRenderer(new LevelRenderer());
		}
		{
			TableColumn col = columnModel.getColumn(LoggingEventTableModelConstants.COLUMN_INDEX_LOGGER_NAME);
			col.setCellRenderer(new LoggerNameRenderer());
		}
		{
			TableColumn col = columnModel.getColumn(LoggingEventTableModelConstants.COLUMN_INDEX_MESSAGE);
			col.setCellRenderer(new MessageRenderer());
		}
		{
			TableColumn col = columnModel.getColumn(LoggingEventTableModelConstants.COLUMN_INDEX_THROWABLE);
			col.setCellRenderer(new ThrowableRenderer());
		}
		{
			TableColumn col = columnModel.getColumn(LoggingEventTableModelConstants.COLUMN_INDEX_THREAD);
			col.setCellRenderer(new ThreadRenderer());
		}
		{
			TableColumn col = columnModel.getColumn(LoggingEventTableModelConstants.COLUMN_INDEX_MARKER);
			col.setCellRenderer(new MarkerRenderer());
		}
		{
			TableColumn col = columnModel.getColumn(LoggingEventTableModelConstants.COLUMN_INDEX_APPLICATION);
			col.setCellRenderer(new ApplicationRenderer());
		}
		if(tableModel.isGlobal())
		{
			TableColumn col = columnModel.getColumn(LoggingEventTableModelConstants.COLUMN_INDEX_SOURCE);
			col.setCellRenderer(new SourceRenderer());
		}
	}
}
