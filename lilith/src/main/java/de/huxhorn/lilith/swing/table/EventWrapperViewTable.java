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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.InputMap;
import javax.swing.ListSelectionModel;
import javax.swing.JPopupMenu;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.Action;
import javax.swing.table.TableColumn;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.Point;
import java.awt.Color;
import java.io.Serializable;
import java.util.Map;
import java.util.List;
import java.util.Iterator;

import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.swing.ColorsProvider;
import de.huxhorn.lilith.swing.Colors;
import de.huxhorn.lilith.swing.MainFrame;
import de.huxhorn.lilith.swing.table.model.EventWrapperTableModel;
import de.huxhorn.lilith.swing.table.model.PersistentTableColumnModel;

public abstract class EventWrapperViewTable<T extends Serializable>
	extends JTable
	implements ColorsProvider
{
	public static final String SCROLLING_TO_BOTTOM_PROPERTY = "scrollingToBottom";
	public static final String FILTER_CONDITION_PROPERTY = "filterCondition";

	private final Logger logger = LoggerFactory.getLogger(EventWrapperViewTable.class);

	protected EventWrapperTableModel<T> tableModel;
	protected Map<Object, TooltipGenerator> tooltipGenerators;
	protected Map<Object, TableColumn> tableColumns;
	protected PersistentTableColumnModel tableColumnModel;
	private boolean scrollingToBottom;
	private Condition filterCondition;
	private JPopupMenu popupMenu;
	private JMenuItem showHideMenu;
	private boolean global;
	protected MainFrame mainFrame;

	public EventWrapperViewTable(MainFrame mainFrame, EventWrapperTableModel<T> model, boolean global)
	{
		super();
		this.mainFrame=mainFrame;
		this.global=global;
		this.tableModel=model;
		this.tableModel.addTableModelListener(new ScrollToBottomListener());
		setAutoCreateColumnsFromModel(false);
		setModel(tableModel);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		addFocusListener(new RepaintFocusListener());
		initTableColumns();
		tableColumnModel=new PersistentTableColumnModel();
		//initColumnModel();
		resetLayout();
		initTooltipGenerators();
		setShowHorizontalLines(false);
		setAutoResizeMode(AUTO_RESIZE_OFF);
		popupMenu=new JPopupMenu();
		popupMenu.add(new SaveLayoutAction());
		popupMenu.add(new ResetLayoutAction());
		showHideMenu=new JMenu("Show/Hide");
		popupMenu.add(showHideMenu);
		getTableHeader().addMouseListener(new PopupListener());
	}

	public boolean isGlobal()
	{
		return global;
	}

	protected abstract void initTooltipGenerators();
	protected abstract void initTableColumns();
//	protected abstract void initColumnModel();

	private void updatePopupMenu()
	{
		showHideMenu.removeAll();
		List<PersistentTableColumnModel.TableColumnLayoutInfo> cli = tableColumnModel.getColumnLayoutInfos();
		for(PersistentTableColumnModel.TableColumnLayoutInfo current : cli)
		{
			JCheckBoxMenuItem cbmi=new JCheckBoxMenuItem(new ShowHideAction(current.getColumnName(), current.isVisible()));
			showHideMenu.add(cbmi);
		}
	}

	public Condition getFilterCondition()
	{
		return filterCondition;
	}

	public void setFilterCondition(Condition filterCondition)
	{
		Object oldValue=this.filterCondition;
		this.filterCondition = filterCondition;
		repaint();
		firePropertyChange(FILTER_CONDITION_PROPERTY, oldValue, filterCondition);
	}

	public void scrollToBottom()
	{
		int row=getRowCount();
		row--;

		selectRow(row);
	}

	public void selectRow(int row)
	{
		if(row >= 0 && row<getRowCount())
		{
			if(logger.isDebugEnabled()) logger.debug("Selecting row {}.", row);
			getSelectionModel().setSelectionInterval(0,row);
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
			Object oldValue=this.scrollingToBottom;
			this.scrollingToBottom = scrollingToBottom;
			if(scrollingToBottom)
			{
				scrollToBottom();
			}
			firePropertyChange(SCROLLING_TO_BOTTOM_PROPERTY, oldValue, scrollingToBottom);
		}
	}

	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed)
	{
		if(logger.isDebugEnabled())
		{
			Object[] args = new Object[]{ks, e, condition, pressed};
			logger.debug("Processing KeyBinding:\n" +
					"\tKeyStroke: {}\n" +
					"\nEvent    : {}\n" +
					"\tcondition: {}\n" +
					"\tpressed  : {}\n", args);
		}
		InputMap inputMap=getInputMap(condition);
		Object key=inputMap.get(ks);
		if(key!=null)
		{
			String keyStr=""+key;
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

	public String getToolTipText(MouseEvent event)
	{
		if(tooltipGenerators==null)
		{
			return null;
		}
		Point p = event.getPoint();
		int row = rowAtPoint(p);
		int column = columnAtPoint(p); // This is the view column!
		if(column > -1)
		{
			TableColumn tableColumn=getColumnModel().getColumn(column);
			if(tableColumn!=null)
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
	
	public void resetLayout()
	{
		List<PersistentTableColumnModel.TableColumnLayoutInfo> infos = loadLayout();
		if(infos==null)
		{
			infos=getDefaultLayout();
		}
		PersistentTableColumnModel newModel=new PersistentTableColumnModel();
		for(PersistentTableColumnModel.TableColumnLayoutInfo current: infos)
		{
			String name=current.getColumnName();
			TableColumn col=tableColumns.get(name);
			if(col!=null)
			{
				col.setPreferredWidth(current.getWidth());
			}
			newModel.addColumn(col);
			newModel.setColumnVisible(col, current.isVisible());
		}
		setColumnModel(newModel);
		tableColumnModel=newModel;
	}

	public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend)
	{
		if(logger.isDebugEnabled())
		{
			if(logger.isDebugEnabled()) logger.debug("changeSelection({}, {}, {}, {})", new Object[]{rowIndex, columnIndex, toggle, extend});
			if(logger.isDebugEnabled()) //noinspection ThrowableInstanceNeverThrown
				logger.debug("changeSelection-Stacktrace", new Throwable());
		}
		if(isScrollingToBottom())
		{
			setScrollingToBottom(false);
		}
		super.changeSelection(rowIndex, columnIndex, toggle, extend);
	}

	private static final Colors NOT_MATCHING_COLORS =new Colors(new Color(192,192,192), new Color(245,245,245), true);
	private static final Colors EVEN_ROW_COLORS =new Colors(new Color(0,0,0), new Color(255,255,255));
	private static final Colors ODD_ROW_COLORS =new Colors(new Color(0,0,0), new Color(0xE9,0xED,0xF2));
	
	public Colors resolveColors(EventWrapper object, int row, int column)
	{
		if(filterCondition!=null && !filterCondition.isTrue(object))
		{
			return NOT_MATCHING_COLORS;
		}
		if(row%2 == 0)
		{
			return EVEN_ROW_COLORS;
		}
		return ODD_ROW_COLORS;
	}


	/**
	 * This is part one of "scroll to bottom" functionality.
	 *
	 * It selects the last row of the table.
	 * Be aware that this listener *must* be added to the table model *before* the
	 * model is assigned to a table!
	 */
	private class ScrollToBottomListener implements TableModelListener
	{
		public void tableChanged(TableModelEvent e)
		{
			if(scrollingToBottom)
			{
				scrollToBottom();
			}
		}
	}

	private class RepaintFocusListener implements FocusListener
	{

		public void focusGained(FocusEvent e)
		{
			repaint();
		}

		public void focusLost(FocusEvent e)
		{
			repaint();
		}
	}

	private class PopupListener implements MouseListener
	{

		public void mouseClicked(MouseEvent e)
		{
			if(e.isPopupTrigger())
			{
				showPopup(e.getPoint());
			}
		}

		public void mousePressed(MouseEvent e)
		{
			if(e.isPopupTrigger())
			{
				showPopup(e.getPoint());
			}
		}

		public void mouseReleased(MouseEvent e)
		{
			if(e.isPopupTrigger())
			{
				showPopup(e.getPoint());
			}
		}

		public void mouseEntered(MouseEvent e)
		{
		}

		public void mouseExited(MouseEvent e)
		{
		}

		private void showPopup(Point p)
		{
			System.out.println("Showing popup at "+p);
			updatePopupMenu();
			popupMenu.show(EventWrapperViewTable.this.getTableHeader(), p.x,  p.y);
		}
	}

	private class SaveLayoutAction
		extends AbstractAction
	{
		private SaveLayoutAction()
		{
			super("Save layout");
		}

		public void actionPerformed(ActionEvent e)
		{
			System.out.println("Save layout");
			saveLayout();
		}
	}

	private class ResetLayoutAction
		extends AbstractAction
	{
		private ResetLayoutAction()
		{
			super("Reset layout");
		}

		public void actionPerformed(ActionEvent e)
		{
			System.out.println("Reset layout");
			resetLayout();
		}
	}

	private class ShowHideAction
		extends AbstractAction
	{
		private boolean visible;
		private String columnName;

		private ShowHideAction(String columnName, boolean visible)
		{
			super(columnName);
			this.columnName=columnName;
			this.visible=visible;
			putValue(Action.SELECTED_KEY, visible);
		}

		public void actionPerformed(ActionEvent e)
		{
			Iterator<TableColumn> iter = tableColumnModel.getColumns(false);
			TableColumn found=null;
			while(iter.hasNext())
			{
				TableColumn current = iter.next();
				if(columnName.equals(current.getIdentifier()))
				{
					found=current;
					break;
				}
			}
			if(found!=null)
			{
				tableColumnModel.setColumnVisible(found, !visible);
			}
		}
	}

}
