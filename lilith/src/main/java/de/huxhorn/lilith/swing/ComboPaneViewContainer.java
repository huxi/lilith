/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
import de.huxhorn.sulky.buffers.Dispose;
import de.huxhorn.sulky.conditions.Condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public abstract class ComboPaneViewContainer<T extends Serializable>
	extends ViewContainer<T>
{
	private final Logger logger = LoggerFactory.getLogger(ComboPaneViewContainer.class);

	private SourceChangeListener sourceChangeListener;
	private boolean disposed;
	private EventWrapper<T> selectedEvent;
	private JPanel contentPane;
	private DefaultComboBoxModel comboBoxModel;
	private int comboCounter;
	private CardLayout cardLayout;
	private CloseAction closeAction;
	private JPanel comboBoxPane;

	public ComboPaneViewContainer(MainFrame mainFrame, EventSource<T> eventSource)
	{
		super(mainFrame, eventSource);
		disposed = false;
		comboBoxPane = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		comboBoxModel = new DefaultComboBoxModel();
		JComboBox cb = new JComboBox(comboBoxModel);
		cb.setRenderer(new MyComboBoxRenderer());
		cb.setEditable(false);
		cb.addItemListener(new ComboItemListener());

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;

		closeAction = new CloseAction();
		JButton closeButton = new JButton(closeAction);
		closeButton.setMargin(new Insets(0, 0, 0, 0));
		comboBoxPane.add(closeButton, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;

		comboBoxPane.add(cb, gbc);
		cardLayout = new CardLayout();
		contentPane = new JPanel(cardLayout);
		setLayout(new BorderLayout());
		add(comboBoxPane, BorderLayout.NORTH);
		add(contentPane, BorderLayout.CENTER);
		sourceChangeListener = new SourceChangeListener();
		addView(getDefaultView());
	}

	private class CloseAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 7687142682378711767L;

		private CloseAction()
		{
			super();
			Icon icon;
			{
				URL url = EventWrapperViewPanel.class.getResource("/tango/16x16/emblems/emblem-unreadable.png");
				if(url != null)
				{
					icon = new ImageIcon(url);
				}
				else
				{
					icon = null;
				}
			}
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.SHORT_DESCRIPTION, "Close filtered view.");
		}

		public void actionPerformed(ActionEvent e)
		{
			closeCurrentFilter();
		}
	}

	private class ComboItemListener
		implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{
			ViewHolder holder = (ViewHolder) comboBoxModel.getSelectedItem();
			if(holder != null)
			{
				cardLayout.show(contentPane, holder.getId());
				if(getViewIndex() > 0)
				{
					closeAction.setEnabled(true);
				}
				else
				{
					closeAction.setEnabled(false);
				}
				selectedViewChanged();
			}
		}
	}

	private class ViewHolder
	{
		private final EventWrapperViewPanel<T> view;
		private final String id;

		private ViewHolder(EventWrapperViewPanel<T> view)
		{
			this.view = view;
			comboCounter++;
			this.id = "" + comboCounter;
		}

		public EventWrapperViewPanel<T> getView()
		{
			return view;
		}

		public String getId()
		{
			return id;
		}

		public boolean equals(Object o)
		{
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;

			ViewHolder that = (ViewHolder) o;

			if(id != null ? !id.equals(that.id) : that.id != null) return false;
			if(view != null ? !view.equals(that.view) : that.view != null) return false;

			return true;
		}

		public int hashCode()
		{
			int result;
			result = (view != null ? view.hashCode() : 0);
			result = 31 * result + (id != null ? id.hashCode() : 0);
			return result;
		}

		public String toString()
		{
			return "ViewHolder[id=" + id + ", view=" + view + "]";
		}
	}

	public void addView(EventWrapperViewPanel<T> view)
	{
		EventSource source = view.getEventSource();
		if(logger.isInfoEnabled()) logger.info("Adding view for {}", source);
		ViewHolder holder = new ViewHolder(view);
		Condition filter = source.getFilter();
		if(filter == null)
		{
			comboBoxModel.insertElementAt(holder, 0);
		}
		else
		{
			comboBoxModel.addElement(holder);
		}
		comboBoxModel.setSelectedItem(holder);
		contentPane.add(holder.getView(), holder.getId());
		cardLayout.show(contentPane, holder.getId());
		view.addPropertyChangeListener(sourceChangeListener);
		view.requestFocusInWindow();
		selectedViewChanged();
	}

	public void updateViews()
	{
		if(comboBoxPane.isVisible())
		{
			comboBoxPane.repaint();
		}
		contentPane.repaint();
	}

	public void updateViewScale(double scale)
	{
		for(int i = 0; i < comboBoxModel.getSize(); i++)
		{
			ViewHolder holder = (ViewHolder) comboBoxModel.getElementAt(i);

			EventWrapperViewPanel<T> current = holder.getView();
			current.setScaleFactor(scale);
		}
	}

	public void removeView(EventWrapperViewPanel<T> view, boolean dispose)
	{
		ViewHolder found = null;
		for(int i = 0; i < comboBoxModel.getSize(); i++)
		{
			ViewHolder holder = (ViewHolder) comboBoxModel.getElementAt(i);

			EventWrapperViewPanel<T> current = holder.getView();
			if(current == view)
			{
				found = holder;
				break;
			}
		}

		if(found != null)
		{
			comboBoxModel.removeElement(found);
			contentPane.remove(found.getView());
			view.removePropertyChangeListener(sourceChangeListener);
			if(logger.isDebugEnabled()) logger.debug("Removed view {}.", view);
			if(dispose)
			{
				view.dispose();
				Buffer<EventWrapper<T>> buffer = view.getEventSource().getBuffer();
				Dispose.dispose(buffer);
				if(logger.isDebugEnabled()) logger.debug("Disposed view {}.", view);
			}
			selectedViewChanged();
		}
	}

	public void showDefaultView()
	{
		if(comboBoxModel.getSize() > 0)
		{
			ViewHolder holder = (ViewHolder) comboBoxModel.getElementAt(0);
			if(holder != null)
			{
				comboBoxModel.setSelectedItem(holder);
				cardLayout.show(contentPane, holder.getId());
				selectedViewChanged();
			}
		}
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

	private void selectedViewChanged()
	{
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

		int count = comboBoxModel.getSize();
		if(count > 1)
		{
			comboBoxPane.setVisible(true);
		}
		else
		{
			comboBoxPane.setVisible(false);
		}
		fireChange();
	}

	public void dispose()
	{
		super.dispose();
		disposed = true;
		List<ViewHolder> removedPanes = new ArrayList<ViewHolder>();
		for(int i = 0; i < comboBoxModel.getSize(); i++)
		{
			removedPanes.add((ViewHolder) comboBoxModel.getElementAt(i));
		}

		for(ViewHolder current : removedPanes)
		{
			removeView(current.getView(), true);
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

	public EventWrapperViewPanel<T> getViewAt(int index)
	{
		if(index >= 0 && index < comboBoxModel.getSize())
		{
			return ((ViewHolder) comboBoxModel.getElementAt(index)).getView();
		}
		return null;
	}

	public EventWrapperViewPanel<T> getSelectedView()
	{
		ViewHolder holder = (ViewHolder) comboBoxModel.getSelectedItem();
		if(holder != null)
		{
			return holder.getView();
		}
		return null;
	}

	public void setViewIndex(int index)
	{
		ViewHolder holder = (ViewHolder) comboBoxModel.getElementAt(index);
		comboBoxModel.setSelectedItem(holder);
		selectedViewChanged();
	}

	public int getViewIndex()
	{
		return comboBoxModel.getIndexOf(comboBoxModel.getSelectedItem());
	}

	public int getViewCount()
	{
		return comboBoxModel.getSize();
	}

	public void closeCurrentFilter()
	{
		ViewHolder holder = (ViewHolder) comboBoxModel.getSelectedItem();
		int index = comboBoxModel.getIndexOf(holder);
		if(holder != null && index > 0)
		{
			EventWrapperViewPanel<T> lvp = holder.getView();
			removeView(lvp, true);
			selectedViewChanged();
		}
	}

	public void closeOtherFilters()
	{
		ViewHolder holder = (ViewHolder) comboBoxModel.getSelectedItem();
		int index = comboBoxModel.getIndexOf(holder);

		int tabCount = comboBoxModel.getSize();
		List<ViewHolder> removedPanes = new ArrayList<ViewHolder>();
		for(int i = 1; i < tabCount; i++)
		{
			if(i != index)
			{
				removedPanes.add((ViewHolder) comboBoxModel.getElementAt(i));
			}
		}

		for(ViewHolder current : removedPanes)
		{
			removeView(current.getView(), true);
		}
		selectedViewChanged();
	}

	public void closeAllFilters()
	{
		List<ViewHolder> removedPanes = new ArrayList<ViewHolder>();
		for(int i = 1; i < comboBoxModel.getSize(); i++)
		{
			removedPanes.add((ViewHolder) comboBoxModel.getElementAt(i));
		}

		for(ViewHolder current : removedPanes)
		{
			removeView(current.getView(), true);
		}
		selectedViewChanged();
	}

	public void setShowingStatusbar(boolean showingStatusbar)
	{
		int tabCount = comboBoxModel.getSize();
		for(int i = 0; i < tabCount; i++)
		{
			ViewHolder current = (ViewHolder) comboBoxModel.getElementAt(i);
			current.getView().setShowingStatusBar(showingStatusbar);
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

	private class MyComboBoxRenderer
		implements ListCellRenderer
	{
		private JLabel label;

		public MyComboBoxRenderer()
		{
			label = new JLabel();
			label.setOpaque(true);
			label.setHorizontalAlignment(SwingConstants.LEFT);
			label.setVerticalAlignment(SwingConstants.CENTER);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			if(isSelected)
			{
				label.setBackground(list.getSelectionBackground());
				label.setForeground(list.getSelectionForeground());
			}
			else
			{
				label.setBackground(list.getBackground());
				label.setForeground(list.getForeground());
			}

			String title = null;
			String toolTip = null;

			if(value != null && value.getClass() == ViewHolder.class)
			{
				ViewHolder holder = (ViewHolder) value;
				EventWrapperViewPanel<T> view = holder.getView();
				if(view != null)
				{
					EventSource source = view.getEventSource();
					Condition filter = source.getFilter();

					if(filter == null)
					{
						title = "Unfiltered";
						toolTip = title;
					}
					else
					{
						SavedCondition savedCondition = getMainFrame().getApplicationPreferences()
							.resolveSavedCondition(filter);
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
					}
				}

			}
			label.setText(title);
			label.setToolTipText(toolTip);

			return label;
		}
	}

}
