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
import de.huxhorn.lilith.swing.table.model.EventWrapperTableModel;
import de.huxhorn.lilith.swing.table.model.PersistentTableColumnModel;
import de.huxhorn.lilith.swing.table.renderer.IdRenderer;
import de.huxhorn.lilith.swing.table.renderer.TimestampRenderer;
import de.huxhorn.lilith.swing.table.renderer.LevelRenderer;
import de.huxhorn.lilith.swing.table.renderer.LoggerNameRenderer;
import de.huxhorn.lilith.swing.table.renderer.MessageRenderer;
import de.huxhorn.lilith.swing.table.renderer.ThrowableRenderer;
import de.huxhorn.lilith.swing.table.renderer.ThreadRenderer;
import de.huxhorn.lilith.swing.table.renderer.MarkerRenderer;
import de.huxhorn.lilith.swing.table.renderer.ApplicationRenderer;
import de.huxhorn.lilith.swing.table.renderer.SourceRenderer;
import de.huxhorn.lilith.swing.MainFrame;

import javax.swing.table.TableColumn;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class LoggingEventViewTable
	extends EventWrapperViewTable<LoggingEvent>
{
	public static final String DEFAULT_COLUMN_NAME_ID = "ID";
	public static final String DEFAULT_COLUMN_NAME_TIMESTAMP = "Timestamp";
	public static final String DEFAULT_COLUMN_NAME_LEVEL = "Level";
	public static final String DEFAULT_COLUMN_NAME_LOGGER_NAME = "Logger";
	public static final String DEFAULT_COLUMN_NAME_MESSAGE = "Message";
	public static final String DEFAULT_COLUMN_NAME_THROWABLE = "Throwable";
	public static final String DEFAULT_COLUMN_NAME_THREAD = "Thread";
	public static final String DEFAULT_COLUMN_NAME_MARKER = "Marker";
	public static final String DEFAULT_COLUMN_NAME_APPLICATIION = "Application";
	public static final String DEFAULT_COLUMN_NAME_SOURCE = "Source";

	public LoggingEventViewTable(MainFrame mainFrame, EventWrapperTableModel<LoggingEvent> model, boolean global)
	{
		super(mainFrame, model, global);
	}

	protected void initTooltipGenerators()
	{
		tooltipGenerators=new HashMap<Object, TooltipGenerator>();
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_LOGGER_NAME,
				new LoggerNameTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_MARKER,
				new MarkerTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_MESSAGE,
				new MessageTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_THREAD,
				new ThreadTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_THROWABLE,
				new ThrowableTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_TIMESTAMP,
				new TimestampTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_APPLICATIION,
				new ApplicationTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_SOURCE,
				new SourceTooltipGenerator());
	}

	protected void initTableColumns()
	{
		tableColumns = new HashMap<Object, TableColumn>();
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_ID);
			col.setCellRenderer(new IdRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_TIMESTAMP);
			col.setCellRenderer(new TimestampRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_LEVEL);
			col.setCellRenderer(new LevelRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_LOGGER_NAME);
			col.setCellRenderer(new LoggerNameRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_MESSAGE);
			col.setCellRenderer(new MessageRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_THROWABLE);
			col.setCellRenderer(new ThrowableRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_THREAD);
			col.setCellRenderer(new ThreadRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_MARKER);
			col.setCellRenderer(new MarkerRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_APPLICATIION);
			col.setCellRenderer(new ApplicationRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_SOURCE);
			col.setCellRenderer(new SourceRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
	}

	protected List<PersistentTableColumnModel.TableColumnLayoutInfo> getDefaultLayout()
	{
		ArrayList<PersistentTableColumnModel.TableColumnLayoutInfo> result =
				new ArrayList<PersistentTableColumnModel.TableColumnLayoutInfo>();

		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_ID, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_TIMESTAMP, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_LEVEL, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_LOGGER_NAME, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_MESSAGE, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_THROWABLE, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_THREAD, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_MARKER, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_APPLICATIION, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_SOURCE, 75, isGlobal()));

		return result;
	}

	public void saveLayout()
	{
		List<PersistentTableColumnModel.TableColumnLayoutInfo> infos = tableColumnModel.getColumnLayoutInfos();
		mainFrame.getApplicationPreferences().writeLoggingColumnLayout(isGlobal(), infos);
	}

	protected List<PersistentTableColumnModel.TableColumnLayoutInfo> loadLayout()
	{
		return mainFrame.getApplicationPreferences().readLoggingColumnLayout(isGlobal());
	}
}
