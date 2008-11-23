package de.huxhorn.lilith.swing.table.model;

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

import javax.swing.table.TableColumn;

public class LoggingEventTableColumnModel
	extends PersistentTableColumnModel
{
	@Override
	protected void initColumns()
	{
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_ID);
			col.setCellRenderer(new IdRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_TIMESTAMP);
			col.setCellRenderer(new TimestampRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_LEVEL);
			col.setCellRenderer(new LevelRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_LOGGER_NAME);
			col.setCellRenderer(new LoggerNameRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_MESSAGE);
			col.setCellRenderer(new MessageRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_THROWABLE);
			col.setCellRenderer(new ThrowableRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_THREAD);
			col.setCellRenderer(new ThreadRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_MARKER);
			col.setCellRenderer(new MarkerRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_APPLICATIION);
			col.setCellRenderer(new ApplicationRenderer());
			addColumn(col);
		}
		{
			TableColumn col = new TableColumn(0);
			col.setHeaderValue(LoggingEventTableColumnModelConstants.DEFAULT_COLUMN_NAME_SOURCE);
			col.setCellRenderer(new SourceRenderer());
			addColumn(col);
		}
	}
}
