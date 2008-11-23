package de.huxhorn.lilith.swing.table.model;

import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableColumnModel;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Set;
import java.io.Serializable;

/**
 * This class is based on code and ideas from Stephen Kelvin (mail at StephenKelvin.de) and Thomas Darimont.
 */
public class PersistentTableColumnModel
		extends DefaultTableColumnModel
{
	protected List<TableColumn> allTableColumns = new ArrayList<TableColumn>();

	/**
	 * Creates an extended table column model.
	 */
	public PersistentTableColumnModel()
	{
		allTableColumns = new ArrayList<TableColumn>();
		initColumns();
	}

	/**
	 * Should be implemented by subclasses if needed.
	 */
	protected void initColumns()
	{

	}

	/**
	 * Sets the visibility of the specified TableColumn.
	 * The call is ignored if the TableColumn is not found in this column model
	 * or its visibility status did not change.
	 * <p/>
	 *
	 * @param column  the column to show/hide
	 * @param visible its new visibility status
	 */
	// listeners will receive columnAdded()/columnRemoved() event
	public void setColumnVisible(TableColumn column, boolean visible)
	{
		if (!visible)
		{
			super.removeColumn(column);
		}
		else
		{
			// find the visible index of the column:
			// iterate through both collections of visible and all columns, counting
			// visible columns up to the one that's about to be shown again
			int noVisibleColumns = tableColumns.size();
			int noInvisibleColumns = allTableColumns.size();
			int visibleIndex = 0;

			for (int invisibleIndex = 0; invisibleIndex < noInvisibleColumns; ++invisibleIndex)
			{
				TableColumn visibleColumn = (visibleIndex < noVisibleColumns ? tableColumns.get(visibleIndex) : null);
				TableColumn testColumn = allTableColumns.get(invisibleIndex);

				if (testColumn == column)
				{
					if (visibleColumn != column)
					{
						super.addColumn(column);
						super.moveColumn(tableColumns.size() - 1, visibleIndex);
					}
					return; // ####################
				}
				if (testColumn == visibleColumn)
				{
					++visibleIndex;
				}
			}
		}
	}

	/**
	 * Makes all columns in this model visible
	 */
	public void setAllColumnsVisible()
	{
		int noColumns = allTableColumns.size();

		for (int columnIndex = 0; columnIndex < noColumns; ++columnIndex)
		{
			TableColumn visibleColumn = (columnIndex < tableColumns.size() ? tableColumns.get(columnIndex) : null);
			TableColumn invisibleColumn = allTableColumns.get(columnIndex);

			if (visibleColumn != invisibleColumn)
			{
				super.addColumn(invisibleColumn);
				super.moveColumn(tableColumns.size() - 1, columnIndex);
			}
		}
	}

	/**
	 * Checks wether the specified column is currently visible.
	 *
	 * @param aColumn column to check
	 * @return visibility of specified column (false if there is no such column at all. [It's not visible, right?])
	 */
	public boolean isColumnVisible(TableColumn aColumn)
	{
		return (tableColumns.indexOf(aColumn) >= 0);
	}

	/**
	 * Append <code>column</code> to the right of exisiting columns.
	 * Posts <code>columnAdded</code> event.
	 *
	 * @param column The column to be added
	 * @throws IllegalArgumentException if <code>column</code> is <code>null</code>
	 * @see #removeColumn
	 */
	@Override
	public void addColumn(TableColumn column)
	{
		allTableColumns.add(column);
		super.addColumn(column);
	}

	/**
	 * Removes <code>column</code> from this column model.
	 * Posts <code>columnRemoved</code> event.
	 * Will do nothing if the column is not in this model.
	 *
	 * @param column the column to be added
	 * @see #addColumn
	 */
	@Override
	public void removeColumn(TableColumn column)
	{
		int allColumnsIndex = allTableColumns.indexOf(column);
		if (allColumnsIndex != -1)
		{
			allTableColumns.remove(allColumnsIndex);
		}
		super.removeColumn(column);
	}

	/**
	 * Moves the column from <code>oldIndex</code> to <code>newIndex</code>.
	 * Posts  <code>columnMoved</code> event.
	 * Will not move any columns if <code>oldIndex</code> equals <code>newIndex</code>.
	 *
	 * @throws IllegalArgumentException if either <code>oldIndex</code> or
	 *                                  <code>newIndex</code>
	 *                                  are not in [0, getColumnCount() - 1]
	 * @param	oldIndex			index of column to be moved
	 * @param	newIndex			new index of the column
	 */
	@Override
	public void moveColumn(int oldIndex, int newIndex)
	{
		if ((oldIndex < 0) || (oldIndex >= getColumnCount()) ||
				(newIndex < 0) || (newIndex >= getColumnCount()))
		{
			throw new IllegalArgumentException("moveColumn() - Index out of range");
		}

		TableColumn fromColumn = tableColumns.get(oldIndex);
		TableColumn toColumn = tableColumns.get(newIndex);

		int allColumnsOldIndex = allTableColumns.indexOf(fromColumn);
		int allColumnsNewIndex = allTableColumns.indexOf(toColumn);

		if (oldIndex != newIndex)
		{
			allTableColumns.remove(allColumnsOldIndex);
			allTableColumns.add(allColumnsNewIndex, fromColumn);
		}

		super.moveColumn(oldIndex, newIndex);
	}

	/**
	 * Returns the total number of columns in this model.
	 *
	 * @param onlyVisible if set only visible columns will be counted
	 * @return the number of columns in the <code>tableColumns</code> array
	 * @see	#getColumns
	 */
	public int getColumnCount(boolean onlyVisible)
	{
		return (onlyVisible ? tableColumns.size() : allTableColumns.size());
	}

	/**
	 * Returns an <code>Enumeration</code> of all the columns in the model.
	 *
	 * @param onlyVisible if set all invisible columns will be missing from the enumeration.
	 * @return an <code>Enumeration</code> of the columns in the model
	 */
	public Iterator<TableColumn> getColumns(boolean onlyVisible)
	{
		return (onlyVisible ? tableColumns.iterator() : allTableColumns.iterator());
	}

	/**
	 * Returns the position of the first column whose identifier equals <code>identifier</code>.
	 * Position is the the index in all visible columns if <code>onlyVisible</code> is true or
	 * else the index in all columns.
	 *
	 * @return the index of the first column whose identifier
	 *         equals <code>identifier</code>
	 * @throws IllegalArgumentException if <code>identifier</code>
	 *                                  is <code>null</code>, or if no
	 *                                  <code>TableColumn</code> has this
	 *                                  <code>identifier</code>
	 * @param	identifier the identifier object to search for
	 * @param	onlyVisible if set searches only visible columns
	 * @see		#getColumn
	 */
	public int getColumnIndex(Object identifier, boolean onlyVisible)
	{
		if (identifier == null)
		{
			throw new IllegalArgumentException("Identifier is null");
		}

		List<TableColumn> columns = (onlyVisible ? tableColumns : allTableColumns);
		int noColumns = columns.size();
		TableColumn column;

		for (int columnIndex = 0; columnIndex < noColumns; ++columnIndex)
		{
			column = columns.get(columnIndex);

			if (identifier.equals(column.getIdentifier()))
			{
				return columnIndex;
			}
		}

		throw new IllegalArgumentException("Identifier not found");
	}

	public Set<TableColumnLayoutInfo> xxx()
	{
		final Set<TableColumnLayoutInfo> tableColumnLayoutInfos = new TreeSet<TableColumnLayoutInfo>();

		for (int idx = 0; idx < allTableColumns.size(); idx++) {
			TableColumn tableColumn = allTableColumns.get(idx);
			boolean visible = tableColumns.contains(tableColumn);
			TableColumnLayoutInfo tableColumnLayoutInfo
					= new TableColumnLayoutInfo(tableColumn.getIdentifier().toString(),
						idx, tableColumn.getWidth(), visible);
			tableColumnLayoutInfos.add(tableColumnLayoutInfo);
		}

		return tableColumnLayoutInfos;
	}

	public void yyy(Set<TableColumnLayoutInfo> tableColumnLayoutInfos)
	{
		// TODO: implement!
/*
		for (TableColumnLayoutInfo tableColumnLayoutInfo : tableColumnLayoutInfos) {
			TableColumn tableColumn = table.getColumn(tableColumnLayoutInfo.getColumnName());
			tableColumn.setPreferredWidth(tableColumnLayoutInfo.getWidth());
			tableColumnModel.addColumn(tableColumn);
		}
*/
	}

	public static class TableColumnLayoutInfo
			implements Serializable, Comparable<TableColumnLayoutInfo>
	{
		private int order;
		private String columnName;
		private int width;
		private boolean visible;

		public TableColumnLayoutInfo()
		{
		}

		public TableColumnLayoutInfo(String columnName, int order, int width, boolean visible)
		{
			this.columnName = columnName;
			this.order = order;
			this.width = width;
			this.visible = visible;
		}

		public int getOrder()
		{
			return order;
		}

		public void setOrder(int order)
		{
			this.order = order;
		}

		public int getWidth()
		{
			return width;
		}

		public void setWidth(int width)
		{
			this.width = width;
		}

		public String getColumnName()
		{
			return columnName;
		}

		public void setColumnName(String columnName)
		{
			this.columnName = columnName;
		}

		public boolean isVisible()
		{
			return visible;
		}

		public void setVisible(boolean visible)
		{
			this.visible = visible;
		}

		public int compareTo(TableColumnLayoutInfo o)
		{
			return order - o.order;
		}
	}
}
