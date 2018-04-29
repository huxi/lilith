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

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.access.HttpStatus;
import de.huxhorn.lilith.swing.MainFrame;
import de.huxhorn.lilith.swing.table.model.EventWrapperTableModel;
import de.huxhorn.lilith.swing.table.renderer.ApplicationRenderer;
import de.huxhorn.lilith.swing.table.renderer.ElapsedTimeRenderer;
import de.huxhorn.lilith.swing.table.renderer.IdRenderer;
import de.huxhorn.lilith.swing.table.renderer.MethodRenderer;
import de.huxhorn.lilith.swing.table.renderer.ProtocolRenderer;
import de.huxhorn.lilith.swing.table.renderer.RemoteAddressRenderer;
import de.huxhorn.lilith.swing.table.renderer.RequestUriRenderer;
import de.huxhorn.lilith.swing.table.renderer.SourceRenderer;
import de.huxhorn.lilith.swing.table.renderer.StatusCodeRenderer;
import de.huxhorn.lilith.swing.table.renderer.TimestampRenderer;
import de.huxhorn.lilith.swing.table.tooltips.ApplicationTooltipGenerator;
import de.huxhorn.lilith.swing.table.tooltips.ElapsedTimeTooltipGenerator;
import de.huxhorn.lilith.swing.table.tooltips.RequestUrlTooltipGenerator;
import de.huxhorn.lilith.swing.table.tooltips.SourceTooltipGenerator;
import de.huxhorn.lilith.swing.table.tooltips.StatusCodeTooltipGenerator;
import de.huxhorn.lilith.swing.table.tooltips.TimestampTooltipGenerator;
import de.huxhorn.sulky.swing.PersistentTableColumnModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.table.TableColumn;

public class AccessEventViewTable
	extends EventWrapperViewTable<AccessEvent>
{
	private static final long serialVersionUID = -5675168001393911040L;

	private static final String DEFAULT_COLUMN_NAME_ID = "ID";
	private static final String DEFAULT_COLUMN_NAME_TIMESTAMP = "Timestamp";
	private static final String DEFAULT_COLUMN_NAME_ELAPSED_TIME = "Elapsed Time";
	private static final String DEFAULT_COLUMN_NAME_STATUS_CODE = "Status";
	private static final String DEFAULT_COLUMN_NAME_METHOD = "Method";
	private static final String DEFAULT_COLUMN_NAME_REQUEST_URI = "Request URI";
	private static final String DEFAULT_COLUMN_NAME_PROTOCOL = "Protocol";
	private static final String DEFAULT_COLUMN_NAME_REMOTE_ADDRESS = "Remote Address";
	private static final String DEFAULT_COLUMN_NAME_APPLICATION = "Application";
	private static final String DEFAULT_COLUMN_NAME_SOURCE = "Source";

	public AccessEventViewTable(MainFrame mainFrame, EventWrapperTableModel<AccessEvent> model, boolean global)
	{
		super(mainFrame, model, global);
	}

	@Override
	protected void initTooltipGenerators()
	{
		tooltipGenerators = new HashMap<>();
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_TIMESTAMP,
			new TimestampTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_REQUEST_URI,
			new RequestUrlTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_STATUS_CODE,
			new StatusCodeTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_APPLICATION,
			new ApplicationTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_ELAPSED_TIME,
			new ElapsedTimeTooltipGenerator());
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
			col.setHeaderValue(DEFAULT_COLUMN_NAME_STATUS_CODE);
			col.setCellRenderer(new StatusCodeRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_METHOD);
			col.setCellRenderer(new MethodRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_PROTOCOL);
			col.setCellRenderer(new ProtocolRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_REQUEST_URI);
			col.setCellRenderer(new RequestUriRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_REMOTE_ADDRESS);
			col.setCellRenderer(new RemoteAddressRenderer());
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
			col.setHeaderValue(DEFAULT_COLUMN_NAME_ELAPSED_TIME);
			col.setCellRenderer(new ElapsedTimeRenderer());
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
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_STATUS_CODE, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_METHOD, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_PROTOCOL, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_REQUEST_URI, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_REMOTE_ADDRESS, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_APPLICATION, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_ELAPSED_TIME, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_SOURCE, 75, isGlobal()));

		return result;
	}

	@Override
	public void saveLayout()
	{
		List<PersistentTableColumnModel.TableColumnLayoutInfo> layoutInfoList = tableColumnModel.getColumnLayoutInfos();
		mainFrame.getApplicationPreferences().writeAccessColumnLayout(isGlobal(), layoutInfoList);
	}

	@Override
	protected List<PersistentTableColumnModel.TableColumnLayoutInfo> loadLayout()
	{
		return mainFrame.getApplicationPreferences().readAccessColumnLayout(isGlobal());
	}

	@Override
	public Colors resolveColors(Object object, int row, int column)
	{
		if(object instanceof HttpStatus.Type)
		{
			return mainFrame.getColors((HttpStatus.Type) object);
		}
		return super.resolveColors(object, row, column);
	}

}
