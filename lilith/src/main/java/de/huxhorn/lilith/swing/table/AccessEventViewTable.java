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

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.swing.table.tooltips.*;
import de.huxhorn.lilith.swing.table.renderer.*;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;

public class AccessEventViewTable
	extends EventWrapperViewTable<AccessEvent>
{
	public AccessEventViewTable(EventWrapperTableModelBase<AccessEvent> model)
	{
		super(model);
	}

	protected void initTooltipGenerators()
	{
		tooltipGenerators=new TooltipGenerator[tableModel.getColumnCount()];
		tooltipGenerators[AccessEventTableModelConstants.COLUMN_INDEX_TIMESTAMP]=new TimestampTooltipGenerator();
		tooltipGenerators[AccessEventTableModelConstants.COLUMN_INDEX_REQUEST_URI]=new RequestUrlTooltipGenerator();
		tooltipGenerators[AccessEventTableModelConstants.COLUMN_INDEX_STATUS_CODE]=new StatusCodeTooltipGenerator();
		tooltipGenerators[AccessEventTableModelConstants.COLUMN_INDEX_APPLICATION]=new ApplicationTooltipGenerator();

		if(tableModel.isGlobal())
		{
			tooltipGenerators[AccessEventTableModelConstants.COLUMN_INDEX_SOURCE]=new SourceTooltipGenerator();
		}
	}

	protected void initColumnModel()
	{
		TableColumnModel columnModel = getColumnModel();
		{
			TableColumn col = columnModel.getColumn(AccessEventTableModelConstants.COLUMN_INDEX_ID);
			col.setCellRenderer(new IdRenderer());
		}
		{
			TableColumn col = columnModel.getColumn(AccessEventTableModelConstants.COLUMN_INDEX_TIMESTAMP);
			col.setCellRenderer(new TimestampRenderer());
		}
		{
			TableColumn col = columnModel.getColumn(AccessEventTableModelConstants.COLUMN_INDEX_STATUS_CODE);
			col.setCellRenderer(new StatusCodeRenderer());
		}
		{
			TableColumn col = columnModel.getColumn(AccessEventTableModelConstants.COLUMN_INDEX_METHOD);
			col.setCellRenderer(new MethodRenderer());
		}
		{
			TableColumn col = columnModel.getColumn(AccessEventTableModelConstants.COLUMN_INDEX_PROTOCOL);
			col.setCellRenderer(new ProtocolRenderer());
		}
		{
			TableColumn col = columnModel.getColumn(AccessEventTableModelConstants.COLUMN_INDEX_REQUEST_URI);
			col.setCellRenderer(new RequestUriRenderer());
		}
		{
			TableColumn col = columnModel.getColumn(AccessEventTableModelConstants.COLUMN_INDEX_REMOTE_ADDR);
			col.setCellRenderer(new RemoteAddrRenderer());
		}
		{
			TableColumn col = columnModel.getColumn(AccessEventTableModelConstants.COLUMN_INDEX_APPLICATION);
			col.setCellRenderer(new ApplicationRenderer());
		}
		if(tableModel.isGlobal())
		{
			TableColumn col = columnModel.getColumn(AccessEventTableModelConstants.COLUMN_INDEX_SOURCE);
			col.setCellRenderer(new SourceRenderer());
		}
	}
}
