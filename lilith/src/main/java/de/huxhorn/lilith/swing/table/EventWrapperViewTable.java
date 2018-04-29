/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.swing.MainFrame;
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.lilith.swing.table.model.EventWrapperTableModel;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.swing.PersistentTableColumnModel;
import java.awt.Color;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EventWrapperViewTable<T extends Serializable>
	extends JTable
	implements ColorsProvider
{
	private static final long serialVersionUID = 7740275815213975505L;

	public static final String SCROLLING_TO_BOTTOM_PROPERTY = "scrollingToBottom";
	private static final String FILTER_CONDITION_PROPERTY = "filterCondition";

	private static final Colors NOT_MATCHING_COLORS =
			new Colors(new ColorScheme(new Color(192, 192, 192), new Color(245, 245, 245), new Color(245, 245, 245)), true);
	private static final ColorScheme EVEN_ROW_COLOR_SCHEME =
			new ColorScheme(new Color(0, 0, 0), new Color(255, 255, 255), new Color(255, 255, 255));
	private static final ColorScheme ODD_ROW_COLOR_SCHEME =
			new ColorScheme(new Color(0, 0, 0), new Color(0xE9, 0xED, 0xF2), new Color(0xE9, 0xED, 0xF2));

	private final Logger logger = LoggerFactory.getLogger(EventWrapperViewTable.class);

	Map<Object, TooltipGenerator> tooltipGenerators;
	Map<Object, TableColumn> tableColumns;
	PersistentTableColumnModel tableColumnModel;
	private boolean scrollingToBottom;
	private Condition filterCondition;

	// TODO: Move to ViewActions
	private final JPopupMenu popupMenu;
	private final JMenuItem columnsMenu;
	private final boolean global;
	protected final MainFrame mainFrame;
	private boolean scrollingSmoothly = true;

	EventWrapperViewTable(MainFrame mainFrame, EventWrapperTableModel<T> tableModel, boolean global)
	{
		super();
		this.mainFrame = mainFrame;
		this.global = global;
		tableModel.addTableModelListener(new ScrollToEventListener());
		setAutoCreateColumnsFromModel(false);
		setModel(tableModel);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		addFocusListener(new RepaintFocusListener());
		initTableColumns();
		tableColumnModel = new PersistentTableColumnModel();
		//initColumnModel();
		resetLayout();
		initTooltipGenerators();
		setShowHorizontalLines(false);
		setAutoResizeMode(AUTO_RESIZE_OFF);
		popupMenu = new JPopupMenu();
		columnsMenu = new JMenu("Columns");
		popupMenu.add(columnsMenu);
		popupMenu.addSeparator();
		popupMenu.add(new SaveLayoutAction());
		popupMenu.add(new ResetLayoutAction());
		getTableHeader().addMouseListener(new PopupListener());
	}

	public boolean isGlobal()
	{
		return global;
	}

	protected abstract void initTooltipGenerators();

	protected abstract void initTableColumns();

	// TODO: Move to ViewActions
	private void updatePopupMenu()
	{
		columnsMenu.removeAll();
		List<PersistentTableColumnModel.TableColumnLayoutInfo> cli = tableColumnModel.getColumnLayoutInfos();
		for(PersistentTableColumnModel.TableColumnLayoutInfo current : cli)
		{
			boolean visible = current.isVisible();
			JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(new ShowHideAction(current.getColumnName(), visible)); // NOPMD - AvoidInstantiatingObjectsInLoops
			cbmi.setSelected(visible);
			columnsMenu.add(cbmi);
		}
	}

	public PersistentTableColumnModel getTableColumnModel()
	{
		return tableColumnModel;
	}

	public Condition getFilterCondition()
	{
		return filterCondition;
	}

	public void setFilterCondition(Condition filterCondition)
	{
		Object oldValue = this.filterCondition;
		this.filterCondition = filterCondition;
		repaint();
		firePropertyChange(FILTER_CONDITION_PROPERTY, oldValue, filterCondition);
	}

	public void scrollToBottom()
	{
		//System.out.println("Scrolling to bottom...");
		//new Throwable().printStackTrace(System.out);

		int row = getRowCount();
		row--;

		selectRow(row);
	}

	public void scrollToFirst()
	{
		if(getRowCount() > 0)
		{
			selectRow(0);
		}
	}

	public void selectRow(int row)
	{
		if(row >= 0 && row < getRowCount())
		{
			if(logger.isDebugEnabled()) logger.debug("Selecting row {}.", row);
			getSelectionModel().setSelectionInterval(0, row);
			// scrollpane adjustment
			scrollRectToVisible(getCellRect(row, 0, true));
		}
	}

	public boolean isScrollingToBottom()
	{
		return scrollingToBottom;
	}

	public void setScrollingToBottom(boolean scrollingToBottom)
	{
		if(this.scrollingToBottom != scrollingToBottom)
		{
			Object oldValue = this.scrollingToBottom;
			this.scrollingToBottom = scrollingToBottom;
			if(scrollingToBottom)
			{
				scrollToBottom();
			}
			firePropertyChange(SCROLLING_TO_BOTTOM_PROPERTY, oldValue, scrollingToBottom);
		}
	}

	@Override
	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed)
	{
		if(logger.isDebugEnabled()) logger.debug("Processing KeyBinding:\n\tKeyStroke: {}\n\nEvent    : {}\n\tcondition: {}\n\tpressed  : {}", ks, e, condition, pressed);
		InputMap inputMap = getInputMap(condition);
		Object key = inputMap.get(ks);
		if(key != null)
		{
			String keyStr = key.toString();
			if(keyStr.startsWith("select"))
			{
				if(isScrollingToBottom())
				{
					setScrollingToBottom(false);
				}
				if(logger.isDebugEnabled()) logger.debug("select detected! {}", keyStr);
			}
			if(logger.isDebugEnabled()) logger.debug("ActionKey: {}", keyStr);
		}
		return super.processKeyBinding(ks, e, condition, pressed);
	}

	@Override
	public String getToolTipText(MouseEvent event)
	{
		if(tooltipGenerators == null)
		{
			return null;
		}
		Point p = event.getPoint();
		int row = rowAtPoint(p);
		int column = columnAtPoint(p); // This is the view column!
		if(column > -1)
		{
			TableColumn tableColumn = getColumnModel().getColumn(column);
			if(tableColumn != null)
			{
				TooltipGenerator generator = tooltipGenerators.get(tableColumn.getIdentifier());
				if(generator != null)
				{
					return generator.createTooltipText(this, row);
				}
			}
		}
		return null;
	}

	public abstract void saveLayout();

	protected abstract List<PersistentTableColumnModel.TableColumnLayoutInfo> getDefaultLayout();

	protected abstract List<PersistentTableColumnModel.TableColumnLayoutInfo> loadLayout();

	private void fireViewContainerChange()
	{
		ViewContainer viewContainer = resolveViewContainer();
		if(viewContainer != null)
		{
			viewContainer.fireChange();
			if(logger.isDebugEnabled()) logger.debug("Fired change on ViewContainer!");
		}
	}

	public final void resetLayout()
	{
		List<PersistentTableColumnModel.TableColumnLayoutInfo> loadedInfos = loadLayout();
		List<PersistentTableColumnModel.TableColumnLayoutInfo> defaults = getDefaultLayout();
		List<PersistentTableColumnModel.TableColumnLayoutInfo> infos;
		if(loadedInfos == null)
		{
			infos = getDefaultLayout();
		}
		else
		{
			infos = new ArrayList<>();

			// lets make sure that all columns exist.
			for(PersistentTableColumnModel.TableColumnLayoutInfo current : loadedInfos)
			{
				if(current != null)
				{
					String currentName = current.getColumnName();
					if(currentName != null)
					{
						for(PersistentTableColumnModel.TableColumnLayoutInfo other : defaults)
						{
							if(currentName.equals(other.getColumnName()))
							{
								infos.add(current);
								break;
							}
						}
					}
				}
			}

			// lets add missing columns
			for(PersistentTableColumnModel.TableColumnLayoutInfo current : defaults)
			{
				String currentName = current.getColumnName();
				if(currentName != null)
				{
					boolean found = false;
					for(PersistentTableColumnModel.TableColumnLayoutInfo other : infos)
					{
						String otherName = other.getColumnName();
						if(currentName.equals(otherName))
						{
							found = true;
						}
					}
					if(!found)
					{
						infos.add(current);
					}
				}
			}
		}
		PersistentTableColumnModel newModel = new PersistentTableColumnModel();
		for(PersistentTableColumnModel.TableColumnLayoutInfo current : infos)
		{
			String name = current.getColumnName();
			TableColumn col = tableColumns.get(name);
			if(col != null)
			{
				col.setPreferredWidth(current.getWidth());
				newModel.addColumn(col);
				newModel.setColumnVisible(col, current.isVisible());
			}
		}
		setColumnModel(newModel);
		tableColumnModel = newModel;
		fireViewContainerChange();
	}

	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend)
	{
		if(logger.isDebugEnabled())
		{
			logger.debug("changeSelection({}, {}, {}, {})", rowIndex, columnIndex, toggle, extend);
			//noinspection ThrowableInstanceNeverThrown
			logger.debug("changeSelection-Stacktrace", new Throwable());
		}
		if(isScrollingToBottom())
		{
			setScrollingToBottom(false);
		}
		super.changeSelection(rowIndex, columnIndex, toggle, extend);
	}

	@Override
	public Colors resolveColors(Object object, int row, int column)
	{
		if(object instanceof EventWrapper)
		{
			if(filterCondition != null && !filterCondition.isTrue(object))
			{
				return NOT_MATCHING_COLORS;
			}
			// check active conditions...
			Colors colors = mainFrame.getColors((EventWrapper) object);
			ColorScheme scheme=null;
			if(colors != null)
			{
				scheme=colors.getColorScheme();
				if(scheme != null && scheme.isAbsolute())
				{
					return colors;
				}
			}
			// if none match...
			if(row % 2 == 0)
			{
				if(scheme != null)
				{
					scheme.mergeWith(EVEN_ROW_COLOR_SCHEME);
				}
				else
				{
					try
					{
						scheme = EVEN_ROW_COLOR_SCHEME.clone();
					}
					catch(CloneNotSupportedException e)
					{
						if(logger.isErrorEnabled()) logger.error("Exception while cloning ColorScheme!!", e);
					}
				}

			}
			else
			{
				if(scheme != null)
				{
					scheme.mergeWith(ODD_ROW_COLOR_SCHEME);
				}
				else
				{
					try
					{
						scheme = ODD_ROW_COLOR_SCHEME.clone();
					}
					catch(CloneNotSupportedException e)
					{
						if(logger.isErrorEnabled()) logger.error("Exception while cloning ColorScheme!!", e);
					}
				}
			}
			if(scheme != null)
			{
				return new Colors(scheme, false);
			}
		}
		return null;
	}


	/**
	 * This is part one of "scroll to bottom" functionality and selects the first event otherwise if no event was
	 * previously selected.
	 * <p/>
	 * It selects the last row of the table.
	 * Be aware that this listener *must* be added to the table model *before* the
	 * model is assigned to a table!
	 */
	private class ScrollToEventListener
		implements TableModelListener
	{
		@Override
		public void tableChanged(TableModelEvent e)
		{
			if(scrollingToBottom)
			{
				scrollToBottom();
			}
			else if(getSelectedRow() < 0)
			{
				scrollToFirst();
			}
		}
	}

	private class RepaintFocusListener
		implements FocusListener
	{

		@Override
		public void focusGained(FocusEvent e)
		{
			repaint();
		}

		@Override
		public void focusLost(FocusEvent e)
		{
			repaint();
		}
	}

	private class PopupListener
		extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			if(e.isPopupTrigger())
			{
				showPopup(e.getPoint());
			}
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			if(e.isPopupTrigger())
			{
				showPopup(e.getPoint());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if(e.isPopupTrigger())
			{
				showPopup(e.getPoint());
			}
		}

		private void showPopup(Point p)
		{
			// TODO: call ViewActions
			if(logger.isDebugEnabled()) logger.debug("Showing popup at {}.", p);
			updatePopupMenu();
			popupMenu.show(EventWrapperViewTable.this.getTableHeader(), p.x, p.y);
		}
	}

	// TODO: Move to ViewActions
	private class SaveLayoutAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 1154654992206760884L;

		SaveLayoutAction()
		{
			super("Save layout");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Save layout");
			saveLayout();
		}
	}

	// TODO: Move to ViewActions
	private class ResetLayoutAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 8635210294474124660L;

		ResetLayoutAction()
		{
			super("Reset layout");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isDebugEnabled()) logger.debug("Reset layout");
			resetLayout();
		}
	}

	// TODO: Move to ViewActions
	private ViewContainer resolveViewContainer()
	{
		ViewContainer result = null;
		Container parentContainer = getParent();
		for(;;)
		{
			if(parentContainer instanceof ViewContainer)
			{
				result = (ViewContainer) parentContainer;
				break;
			}
			if(parentContainer == null)
			{
				break;
			}
			parentContainer = parentContainer.getParent();
		}
		return result;
	}

	// TODO: Move to ViewActions
	private class ShowHideAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 2845939134245819103L;

		private final String columnName;

		private boolean visible;

		ShowHideAction(String columnName, boolean visible)
		{
			super(columnName);
			this.columnName = columnName;
			this.visible = visible;
			//putValue(EventWrapperViewTable.SELECTED_KEY, visible);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			visible = !visible;
			Iterator<TableColumn> iter = tableColumnModel.getColumns(false);
			TableColumn found = null;
			while(iter.hasNext())
			{
				TableColumn current = iter.next();
				if(columnName.equals(current.getIdentifier()))
				{
					found = current;
					break;
				}
			}
			if(found != null)
			{
				tableColumnModel.setColumnVisible(found, visible);
				fireViewContainerChange();
			}
		}
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		if(orientation != SwingConstants.HORIZONTAL || !scrollingSmoothly)
		{
			return super.getScrollableUnitIncrement(visibleRect, orientation, direction);
		}
		return 5;
	}

	public void setScrollingSmoothly(boolean scrollingSmoothly)
	{
		this.scrollingSmoothly = scrollingSmoothly;
	}
}
