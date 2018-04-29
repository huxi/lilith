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

package de.huxhorn.lilith.swing.table;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.swing.MainFrame;
import de.huxhorn.lilith.swing.table.model.EventWrapperTableModel;
import de.huxhorn.lilith.swing.table.renderer.ApplicationRenderer;
import de.huxhorn.lilith.swing.table.renderer.ContextRenderer;
import de.huxhorn.lilith.swing.table.renderer.IdRenderer;
import de.huxhorn.lilith.swing.table.renderer.LevelRenderer;
import de.huxhorn.lilith.swing.table.renderer.LoggerNameRenderer;
import de.huxhorn.lilith.swing.table.renderer.MarkerRenderer;
import de.huxhorn.lilith.swing.table.renderer.MessageRenderer;
import de.huxhorn.lilith.swing.table.renderer.NdcRenderer;
import de.huxhorn.lilith.swing.table.renderer.SourceRenderer;
import de.huxhorn.lilith.swing.table.renderer.ThreadRenderer;
import de.huxhorn.lilith.swing.table.renderer.ThrowableRenderer;
import de.huxhorn.lilith.swing.table.renderer.TimestampRenderer;
import de.huxhorn.lilith.swing.table.tooltips.ApplicationTooltipGenerator;
import de.huxhorn.lilith.swing.table.tooltips.ContextTooltipGenerator;
import de.huxhorn.lilith.swing.table.tooltips.LoggerNameTooltipGenerator;
import de.huxhorn.lilith.swing.table.tooltips.MarkerTooltipGenerator;
import de.huxhorn.lilith.swing.table.tooltips.MessageTooltipGenerator;
import de.huxhorn.lilith.swing.table.tooltips.NdcTooltipGenerator;
import de.huxhorn.lilith.swing.table.tooltips.SourceTooltipGenerator;
import de.huxhorn.lilith.swing.table.tooltips.ThreadTooltipGenerator;
import de.huxhorn.lilith.swing.table.tooltips.ThrowableTooltipGenerator;
import de.huxhorn.lilith.swing.table.tooltips.TimestampTooltipGenerator;
import de.huxhorn.sulky.swing.PersistentTableColumnModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.table.TableColumn;

public class LoggingEventViewTable
	extends EventWrapperViewTable<LoggingEvent>
{
	private static final long serialVersionUID = 2969867582530249593L;

	private static final String DEFAULT_COLUMN_NAME_ID = "ID";
	private static final String DEFAULT_COLUMN_NAME_TIMESTAMP = "Timestamp";
	private static final String DEFAULT_COLUMN_NAME_LEVEL = "Level";
	private static final String DEFAULT_COLUMN_NAME_LOGGER_NAME = "Logger";
	private static final String DEFAULT_COLUMN_NAME_MESSAGE = "Message";
	private static final String DEFAULT_COLUMN_NAME_THROWABLE = "Throwable";
	private static final String DEFAULT_COLUMN_NAME_THREAD = "Thread";
	private static final String DEFAULT_COLUMN_NAME_MARKER = "Marker";
	private static final String DEFAULT_COLUMN_NAME_NDC = "NDC";
	private static final String DEFAULT_COLUMN_NAME_APPLICATION = "Application";
	private static final String DEFAULT_COLUMN_NAME_CONTEXT = "Context";
	private static final String DEFAULT_COLUMN_NAME_SOURCE = "Source";

	public LoggingEventViewTable(MainFrame mainFrame, EventWrapperTableModel<LoggingEvent> model, boolean global)
	{
		super(mainFrame, model, global);
	}

	@Override
	protected void initTooltipGenerators()
	{
		tooltipGenerators = new HashMap<>();
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_LOGGER_NAME,
			new LoggerNameTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_MARKER,
			new MarkerTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_NDC,
			new NdcTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_MESSAGE,
			new MessageTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_THREAD,
			new ThreadTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_THROWABLE,
			new ThrowableTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_TIMESTAMP,
			new TimestampTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_APPLICATION,
			new ApplicationTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_CONTEXT,
			new ContextTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_SOURCE,
			new SourceTooltipGenerator());
	}

	@Override
	protected void initTableColumns()
	{
		tableColumns = new HashMap<>();
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
			col.setHeaderValue(DEFAULT_COLUMN_NAME_NDC);
			col.setCellRenderer(new NdcRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_APPLICATION);
			col.setCellRenderer(new ApplicationRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_CONTEXT);
			col.setCellRenderer(new ContextRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_SOURCE);
			col.setCellRenderer(new SourceRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
	}

	@Override
	protected List<PersistentTableColumnModel.TableColumnLayoutInfo> getDefaultLayout()
	{
		ArrayList<PersistentTableColumnModel.TableColumnLayoutInfo> result =
			new ArrayList<>();

		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_ID, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_TIMESTAMP, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_LEVEL, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_LOGGER_NAME, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_MESSAGE, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_THROWABLE, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_THREAD, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_MARKER, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_NDC, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_APPLICATION, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_CONTEXT, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_SOURCE, 75, isGlobal()));

		return result;
	}

	@Override
	public void saveLayout()
	{
		List<PersistentTableColumnModel.TableColumnLayoutInfo> layoutInfoList = tableColumnModel.getColumnLayoutInfos();
		mainFrame.getApplicationPreferences().writeLoggingColumnLayout(isGlobal(), layoutInfoList);
	}

	@Override
	protected List<PersistentTableColumnModel.TableColumnLayoutInfo> loadLayout()
	{
		return mainFrame.getApplicationPreferences().readLoggingColumnLayout(isGlobal());
	}

	@Override
	public Colors resolveColors(Object object, int row, int column)
	{
		if(object instanceof LoggingEvent.Level)
		{
			return mainFrame.getColors((LoggingEvent.Level) object);
		}
		return super.resolveColors(object, row, column);
	}
}
