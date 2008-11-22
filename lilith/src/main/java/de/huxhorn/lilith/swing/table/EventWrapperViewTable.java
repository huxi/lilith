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
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.Point;
import java.awt.Color;
import java.io.Serializable;

import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.swing.ColorsProvider;
import de.huxhorn.lilith.swing.Colors;

public abstract class EventWrapperViewTable<T extends Serializable>
	extends JTable
	implements ColorsProvider
{
	private final Logger logger = LoggerFactory.getLogger(EventWrapperViewTable.class);
	public static final String SCROLLING_TO_BOTTOM_PROPERTY = "scrollingToBottom";
	public static final String FILTER_CONDITION_PROPERTY = "filterCondition";

	protected TooltipGenerator[] tooltipGenerators;
	private boolean scrollingToBottom;
	private Condition filterCondition;
	protected EventWrapperTableModelBase<T> tableModel;

	public EventWrapperViewTable(EventWrapperTableModelBase<T> model)
	{
		super();
		this.tableModel=model;
		this.tableModel.addTableModelListener(new ScrollToBottomListener());
		setModel(tableModel);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		addFocusListener(new RepaintFocusListener());
		initColumnModel();
		initTooltipGenerators();
		setShowHorizontalLines(false);
		setAutoResizeMode(AUTO_RESIZE_OFF);
	}

	protected abstract void initTooltipGenerators();

	protected abstract void initColumnModel();


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
		Point p = event.getPoint();
		int row = rowAtPoint(p);
		int column = columnAtPoint(p); // This is the view column!
		if(logger.isDebugEnabled()) logger.debug("ColumnIndex before conversion: {}",column);
		column=convertColumnIndexToModel(column);
		if(logger.isDebugEnabled()) logger.debug("ColumnIndex after conversion: {}",column);
		if(column>=0 && column<tooltipGenerators.length)
		{
			TooltipGenerator generator=tooltipGenerators[column];
			if(generator!=null)
			{
				return generator.createTooltipText(this, row);
			}
		}
		return null;
		//return super.getToolTipText(event);
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
}
