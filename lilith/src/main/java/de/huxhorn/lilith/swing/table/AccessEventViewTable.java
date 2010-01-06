/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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
import de.huxhorn.sulky.swing.PersistentTableColumnModel;
import de.huxhorn.lilith.swing.table.renderer.*;
import de.huxhorn.lilith.swing.table.tooltips.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.table.TableColumn;

public class AccessEventViewTable
	extends EventWrapperViewTable<AccessEvent>
{
	public static final String DEFAULT_COLUMN_NAME_ID = "ID";
	public static final String DEFAULT_COLUMN_NAME_TIMESTAMP = "Timestamp";
	public static final String DEFAULT_COLUMN_NAME_STATUS_CODE = "Status";
	public static final String DEFAULT_COLUMN_NAME_METHOD = "Method";
	public static final String DEFAULT_COLUMN_NAME_REQUEST_URI = "Request URI";
	public static final String DEFAULT_COLUMN_NAME_PROTOCOL = "Protocol";
	public static final String DEFAULT_COLUMN_NAME_REMOTE_ADDR = "Remote Address";
	public static final String DEFAULT_COLUMN_NAME_APPLICATIION = "Application";
	//public static final String DEFAULT_COLUMN_NAME_CONTEXT = "Context";
	public static final String DEFAULT_COLUMN_NAME_SOURCE = "Source";

	public AccessEventViewTable(MainFrame mainFrame, EventWrapperTableModel<AccessEvent> model, boolean global)
	{
		super(mainFrame, model, global);
	}

	protected void initTooltipGenerators()
	{
		tooltipGenerators = new HashMap<Object, TooltipGenerator>();
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_TIMESTAMP,
			new TimestampTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_REQUEST_URI,
			new RequestUrlTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_STATUS_CODE,
			new StatusCodeTooltipGenerator());
		tooltipGenerators.put(DEFAULT_COLUMN_NAME_APPLICATIION,
			new ApplicationTooltipGenerator());
//		tooltipGenerators.put(DEFAULT_COLUMN_NAME_CONTEXT,
//			new ContextTooltipGenerator());
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
			col.setHeaderValue(DEFAULT_COLUMN_NAME_REMOTE_ADDR);
			col.setCellRenderer(new RemoteAddrRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_APPLICATIION);
			col.setCellRenderer(new ApplicationRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
/*
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(DEFAULT_COLUMN_NAME_CONTEXT);
			col.setCellRenderer(new ContextRenderer());
			tableColumns.put(col.getHeaderValue(), col);
		}
*/
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
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_STATUS_CODE, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_METHOD, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_PROTOCOL, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_REQUEST_URI, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_REMOTE_ADDR, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_APPLICATIION, 75, true));
//		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_CONTEXT, 75, true));
		result.add(new PersistentTableColumnModel.TableColumnLayoutInfo(DEFAULT_COLUMN_NAME_SOURCE, 75, isGlobal()));

		return result;
	}

	public void saveLayout()
	{
		List<PersistentTableColumnModel.TableColumnLayoutInfo> infos = tableColumnModel.getColumnLayoutInfos();
		mainFrame.getApplicationPreferences().writeAccessColumnLayout(isGlobal(), infos);
	}

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
