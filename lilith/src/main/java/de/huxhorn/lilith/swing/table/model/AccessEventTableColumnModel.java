package de.huxhorn.lilith.swing.table.model;

import de.huxhorn.lilith.swing.table.renderer.IdRenderer;
import de.huxhorn.lilith.swing.table.renderer.TimestampRenderer;
import de.huxhorn.lilith.swing.table.renderer.ApplicationRenderer;
import de.huxhorn.lilith.swing.table.renderer.SourceRenderer;
import de.huxhorn.lilith.swing.table.renderer.StatusCodeRenderer;
import de.huxhorn.lilith.swing.table.renderer.MethodRenderer;
import de.huxhorn.lilith.swing.table.renderer.ProtocolRenderer;
import de.huxhorn.lilith.swing.table.renderer.RequestUriRenderer;
import de.huxhorn.lilith.swing.table.renderer.RemoteAddrRenderer;

import javax.swing.table.TableColumn;

public class AccessEventTableColumnModel
	extends PersistentTableColumnModel
{
	@Override
	protected void initColumns()
	{
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(AccessEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_ID);
			col.setCellRenderer(new IdRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(AccessEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_TIMESTAMP);
			col.setCellRenderer(new TimestampRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(AccessEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_STATUS_CODE);
			col.setCellRenderer(new StatusCodeRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(AccessEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_METHOD);
			col.setCellRenderer(new MethodRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(AccessEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_PROTOCOL);
			col.setCellRenderer(new ProtocolRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(AccessEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_REQUEST_URI);
			col.setCellRenderer(new RequestUriRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(AccessEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_REMOTE_ADDR);
			col.setCellRenderer(new RemoteAddrRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(AccessEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_APPLICATIION);
			col.setCellRenderer(new ApplicationRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(AccessEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_SOURCE);
			col.setCellRenderer(new SourceRenderer());
			addColumn(col);
		}
	}
}