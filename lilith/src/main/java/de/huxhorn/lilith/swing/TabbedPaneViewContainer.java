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
package de.huxhorn.lilith.swing;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.lilith.swing.preferences.SavedCondition;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.Buffers;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.swing.KeyStrokes;
import de.huxhorn.sulky.tasks.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class TabbedPaneViewContainer<T extends Serializable>
	extends ViewContainer<T>
{
	private final Logger logger = LoggerFactory.getLogger(TabbedPaneViewContainer.class);

	private JTabbedPane pane;
	private JPopupMenu popup;
	private CloseFilterAction closeFilterAction;
	private CloseAllFiltersAction closeAllFiltersAction;
	private CloseOtherFiltersAction closeOtherFiltersAction;
	private SourceChangeListener sourceChangeListener;
	private boolean disposed;
	// TODO: probably move to container ;)
	private ProgressGlassPane progressPanel;
	private Component prevGlassPane;
	private boolean searching;
	private EventWrapper<T> selectedEvent;

	public TabbedPaneViewContainer(MainFrame mainFrame, EventSource<T> eventSource)
	{
		super(mainFrame, eventSource);
		progressPanel = new ProgressGlassPane();
		disposed = false;
		pane = new JTabbedPane(JTabbedPane.TOP);
		pane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		pane.addMouseListener(new PopupMouseListener());
		setLayout(new BorderLayout());
		add(pane, BorderLayout.CENTER);
		closeFilterAction = new CloseFilterAction();
		closeOtherFiltersAction = new CloseOtherFiltersAction();
		closeAllFiltersAction = new CloseAllFiltersAction();
		popup = new JPopupMenu();
		popup.add(closeFilterAction);
		popup.add(closeOtherFiltersAction);
		popup.add(closeAllFiltersAction);
		pane.addChangeListener(new TabChangeListener());
		sourceChangeListener = new SourceChangeListener();
		addView(getDefaultView());
	}

	public void addView(EventWrapperViewPanel<T> view)
	{
		EventSource source = view.getEventSource();
		if(logger.isInfoEnabled()) logger.info("Adding view for {}", source);
		Condition filter = source.getFilter();
		if(filter == null)
		{
			pane.insertTab("Unfiltered", null, view, null, 0);
		}
		else
		{
			String title;
			String toolTip;
			SavedCondition savedCondition = getMainFrame().getApplicationPreferences().resolveSavedCondition(filter);
			if(savedCondition != null)
			{
				title = savedCondition.getName();
				toolTip = filter.toString();
			}
			else
			{
				String text = filter.toString();
				title = text;
				toolTip = text;
			}
			pane.insertTab(title, null, view, toolTip, pane.getTabCount());
		}
		pane.setSelectedComponent(view);
		view.addPropertyChangeListener(sourceChangeListener);
		view.requestFocusInWindow();
		fireChange();
	}

	public void updateViews()
	{
		for(int i = 0; i < pane.getTabCount(); i++)
		{
			EventWrapperViewPanel<T> current = (EventWrapperViewPanel<T>) pane.getComponentAt(i);
			EventSource source = current.getEventSource();
			Condition condition = source.getFilter();
			if(logger.isDebugEnabled()) logger.debug("Condition: {}", condition);
			if(condition != null)
			{
				String title;
				String toolTip;
				SavedCondition savedCondition = getMainFrame().getApplicationPreferences()
					.resolveSavedCondition(condition);
				if(savedCondition != null)
				{
					title = savedCondition.getName();
					toolTip = condition.toString();
				}
				else
				{
					String text = condition.toString();
					title = text;
					toolTip = text;
				}
				pane.setTitleAt(i, title);
				pane.setToolTipTextAt(i, toolTip);
			}
			// trigger repaint of table
			pane.repaint();
		}
	}

	/**
	 * @param view
	 */
	public void removeView(EventWrapperViewPanel<T> view, boolean dispose)
	{
		pane.remove(view);
		view.removePropertyChangeListener(sourceChangeListener);
		if(logger.isDebugEnabled()) logger.debug("Removed view {}.", view);
		if(dispose)
		{
			view.dispose();
			Buffer<EventWrapper<T>> buffer = view.getEventSource().getBuffer();
			Buffers.dispose(buffer);
			if(logger.isDebugEnabled()) logger.debug("Disposed view {}.", view);
		}
		fireChange();
	}

	public void showDefaultView()
	{
		pane.setSelectedIndex(0);
		fireChange();
	}

	public void addNotify()
	{
		super.addNotify();
		if(logger.isDebugEnabled()) logger.debug("addNotify - parent: {}", getParent());
	}

	public void scrollToEvent()
	{
		EventWrapperViewPanel<T> selectedView = getSelectedView();
		if(selectedView != null)
		{
			selectedView.scrollToEvent();
			setSelectedEvent(selectedView.getSelectedEvent());
		}
	}

	public void removeNotify()
	{
		super.removeNotify();
		if(logger.isDebugEnabled()) logger.debug("removeNotify");
	}

	private void showPopup(MouseEvent evt)
	{
		Point p = evt.getPoint();
		if(logger.isInfoEnabled()) logger.info("Show popup at {}.", p);
		selectedViewChanged();
		popup.show(pane, p.x, p.y);
	}

	private void selectedViewChanged()
	{
		closeFilterAction.updateAction();
		closeOtherFiltersAction.updateAction();
		closeAllFiltersAction.updateAction();
		EventWrapperViewPanel<T> selectedView = getSelectedView();
		if(selectedView != null)
		{
			selectedView.scrollToEvent();
			setSelectedEvent(selectedView.getSelectedEvent());

		}
		else
		{
			setSelectedEvent(null);
		}
		fireChange();
	}

	public void dispose()
	{
		disposed = true;
		int tabCount = pane.getTabCount();
		List<Component> removedPanes = new ArrayList<Component>(tabCount - 1);
		for(int i = 0; i < tabCount; i++)
		{
			removedPanes.add(pane.getComponentAt(i));
		}

		for(Component comp : removedPanes)
		{
			if(comp instanceof EventWrapperViewPanel)
			{
				EventWrapperViewPanel<T> lvp = (EventWrapperViewPanel<T>) comp;
				removeView(lvp, true);
			}
		}
		fireChange();
	}

	public boolean isDisposed()
	{
		return disposed;
	}

	public void setSelectedEvent(EventWrapper<T> selectedEvent)
	{
		Object oldValue = this.selectedEvent;
		this.selectedEvent = selectedEvent;
		Object newValue = this.selectedEvent;
		firePropertyChange(SELECTED_EVENT_PROPERTY_NAME, oldValue, newValue);
	}

	public EventWrapper<T> getSelectedEvent()
	{
		return selectedEvent;
	}

	private class PopupMouseListener
		implements MouseListener
	{
		public void mouseClicked(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mousePressed(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mouseReleased(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mouseEntered(MouseEvent e)
		{
		}

		public void mouseExited(MouseEvent e)
		{
		}
	}

	// TODO: Move to ViewActions
	private class CloseFilterAction
		extends AbstractAction
	{
		public CloseFilterAction()
		{
			super("Close this filter");
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('c'));
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " W");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void updateAction()
		{
			int viewIndex = getViewIndex();
			if(viewIndex > 0)
			{
				setEnabled(true);
			}
			else
			{
				setEnabled(false);
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			closeCurrentFilter();
		}

	}

	// TODO: Move to ViewActions
	private class CloseOtherFiltersAction
		extends AbstractAction
	{
		public CloseOtherFiltersAction()
		{
			super("Close all other filters");
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('o'));
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift W");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void updateAction()
		{
			int viewIndex = getViewIndex();
			int viewCount = getViewCount();
			if(viewIndex > -1 && ((viewIndex == 0 && viewCount > 1) || viewCount > 2))
			{
				setEnabled(true);
			}
			else
			{
				setEnabled(false);
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			closeOtherFilters();
		}
	}

	// TODO: Move to ViewActions
	private class CloseAllFiltersAction
		extends AbstractAction
	{
		public CloseAllFiltersAction()
		{
			super("Close all filters");
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('a'));
		}

		public void updateAction()
		{
			int viewCount = getViewCount();
			if(viewCount > 1)
			{
				setEnabled(true);
			}
			else
			{
				setEnabled(false);
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			closeAllFilters();
		}
	}

	public EventWrapperViewPanel<T> getViewAt(int index)
	{
		if(index >= 0 && index < pane.getTabCount())
		{
			return (EventWrapperViewPanel<T>) pane.getComponentAt(index);
		}
		return null;
	}

	public EventWrapperViewPanel<T> getSelectedView()
	{
		return (EventWrapperViewPanel<T>) pane.getSelectedComponent();
	}

	public void setViewIndex(int index)
	{
		pane.setSelectedIndex(index);
		fireChange();
	}

	public int getViewIndex()
	{
		return pane.getSelectedIndex();
	}

	public int getViewCount()
	{
		return pane.getTabCount();
	}

	public void closeCurrentFilter()
	{
		int tabIndex = pane.getSelectedIndex();
		Component comp = pane.getComponentAt(tabIndex);
		if(comp != null)
		{
			if(comp instanceof EventWrapperViewPanel)
			{
				EventWrapperViewPanel<T> lvp = (EventWrapperViewPanel<T>) comp;
				removeView(lvp, true);
			}
		}
		fireChange();
	}

	public void closeOtherFilters()
	{
		int tabIndex = pane.getSelectedIndex();

		int tabCount = pane.getTabCount();
		List<Component> removedPanes = new ArrayList<Component>(tabCount - 1);
		for(int i = 1; i < tabCount; i++)
		{
			if(i != tabIndex)
			{
				removedPanes.add(pane.getComponentAt(i));
			}
		}

		for(Component comp : removedPanes)
		{
			if(comp instanceof EventWrapperViewPanel)
			{
				EventWrapperViewPanel<T> lvp = (EventWrapperViewPanel<T>) comp;
				removeView(lvp, true);
			}
		}
		fireChange();
	}

	public void closeAllFilters()
	{
		int tabCount = pane.getTabCount();
		List<Component> removedPanes = new ArrayList<Component>(tabCount - 1);
		for(int i = 1; i < tabCount; i++)
		{
			removedPanes.add(pane.getComponentAt(i));
		}

		for(Component comp : removedPanes)
		{
			if(comp instanceof EventWrapperViewPanel)
			{
				EventWrapperViewPanel<T> lvp = (EventWrapperViewPanel<T>) comp;
				removeView(lvp, true);
			}
		}
		fireChange();
	}

	public boolean isSearching()
	{
		return searching;
	}

	public void cancelSearching()
	{
		progressPanel.getFindCancelAction().actionPerformed(null);

	}

	public void hideSearchPanel()
	{
		if(searching)
		{
			searching = false;
			ViewWindow window = resolveViewWindow();
			if(window != null && prevGlassPane != null)
			{
				window.setGlassPane(prevGlassPane);
				prevGlassPane = null;
				fireChange();
			}
		}
	}

	/*
	public void reinstallSearchPanel()
	{
		if(searching)
		{
			ViewWindow window = resolveViewWindow();
			if(window != null)
			{
				window.setGlassPane(progressPanel);
				progressPanel.setVisible(true);
			}
			fireChange();
		}
	}
    */
	public void showSearchPanel(Task<Integer> task)
	{
		if(task != null)
		{
			searching = true;
			progressPanel.setProgress(0);
			progressPanel.getFindCancelAction().setTask(task);

			ViewWindow window = resolveViewWindow();
			if(window != null)
			{
				prevGlassPane = window.getGlassPane();
				window.setGlassPane(progressPanel);
				progressPanel.setVisible(true);
			}
/*
			if(window instanceof JInternalFrame)
			{
			}
			else if(window instanceof JFrame)
			{
				JFrame frame=(JFrame) window;
				iframeGlassPane=null;
				frameGlassPane=frame.getGlassPane();
				window.setGlassPane(progressPanel);
				progressPanel.setVisible(true);
			}
			*/
			fireChange();
		}
	}

	public ProgressGlassPane getProgressPanel()
	{
		return progressPanel;
	}

	private class TabChangeListener
		implements ChangeListener
	{

		public void stateChanged(ChangeEvent e)
		{
			int selected = pane.getSelectedIndex();
			if(logger.isDebugEnabled()) logger.debug("Selected tab: {}", selected);
			selectedViewChanged();
			fireChange();
		}
	}

	private class SourceChangeListener
		implements PropertyChangeListener
	{

		public void propertyChange(PropertyChangeEvent evt)
		{
			String propName = evt.getPropertyName();
			if(EventWrapperViewPanel.EVENT_SOURCE_PROPERTY.equals(propName))
			{
				if(logger.isDebugEnabled()) logger.debug("EventSource changed: {}", evt.getNewValue());
				EventWrapperViewPanel<T> lvp = (EventWrapperViewPanel<T>) evt.getSource();
				removeView(lvp, false);
				addView(lvp);
			}
			else if(EventWrapperViewPanel.SELECTED_EVENT_PROPERTY.equals(propName))
			{
				if(getSelectedView() == evt.getSource())
				{
					if(logger.isDebugEnabled()) logger.debug("EventSource changed: {}", evt.getNewValue());
					setSelectedEvent((EventWrapper<T>) evt.getNewValue());
				}
			}
			else
			{
				if(logger.isDebugEnabled()) logger.debug("Other change: {}", propName);
				fireChange();
			}
		}
	}
}
