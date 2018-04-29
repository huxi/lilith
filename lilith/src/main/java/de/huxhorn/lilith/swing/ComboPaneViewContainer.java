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

package de.huxhorn.lilith.swing;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.lilith.swing.preferences.SavedCondition;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.Dispose;
import de.huxhorn.sulky.conditions.Condition;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ComboPaneViewContainer<T extends Serializable>
	extends ViewContainer<T>
{
	private static final long serialVersionUID = -399179541035021703L;

	private static final String UNFILTERED = "Unfiltered";

	private final Logger logger = LoggerFactory.getLogger(ComboPaneViewContainer.class);

	private final SourceChangeListener sourceChangeListener;
	private final JPanel contentPane;
	private final JComboBox<ViewHolder> comboBox;
	private final DefaultComboBoxModel<ViewHolder> comboBoxModel;
	private final CardLayout cardLayout;
	private final CloseAction closeAction;
	private final JPanel comboBoxPane;

	private int comboCounter;
	private boolean disposed;
	private EventWrapper<T> selectedEvent;

	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	ComboPaneViewContainer(MainFrame mainFrame, EventSource<T> eventSource)
	{
		super(mainFrame, eventSource);
		disposed = false;
		comboBoxPane = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		comboBoxModel = new DefaultComboBoxModel<>();
		comboBox = new JComboBox<>(comboBoxModel);
		comboBox.setRenderer(new MyComboBoxRenderer());
		comboBox.setEditable(false);
		comboBox.addItemListener(new ComboItemListener());

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

		comboBoxPane.add(comboBox, gbc);
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

		CloseAction()
		{
			super();
			putValue(Action.SMALL_ICON, Icons.CLOSE_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Close filtered view.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			closeCurrentFilter();
		}
	}

	private class ComboItemListener
		implements ItemListener
	{
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			ViewHolder holder = getSelectedItem();
			if(holder == null)
			{
				return;
			}
			Condition filter = null;
			EventWrapperViewPanel<T> view = holder.getView();
			if(view != null)
			{
				EventSource source = view.getEventSource();
				filter = source.getFilter();
			}
			if(filter == null)
			{
				comboBox.setToolTipText(UNFILTERED);
			}
			else
			{
				comboBox.setToolTipText(TextPreprocessor.preformattedTooltip(TextPreprocessor.cropTextBlock(TextPreprocessor.formatCondition(filter))));
			}
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

	/*
	 * Only used for non-generic identity check in ViewHolder.equals()
	 */
	private interface ViewHolderIdentity
	{
		String getId();
	}

	private class ViewHolder
		implements ViewHolderIdentity
	{
		private final EventWrapperViewPanel<T> view;
		private final String id;

		ViewHolder(EventWrapperViewPanel<T> view)
		{
			this.view = view;
			comboCounter++;
			this.id = Integer.toString(comboCounter);
		}

		public EventWrapperViewPanel<T> getView()
		{
			return view;
		}

		@Override
		public String getId()
		{
			return id;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (!(o instanceof ViewHolderIdentity)) return false;

			ViewHolderIdentity that = (ViewHolderIdentity) o;

			return id.equals(that.getId());
		}

		@Override
		public int hashCode()
		{
			return id.hashCode();
		}

		@Override
		public String toString()
		{
			return "ViewHolder[id=" + id + ", view=" + view + "]";
		}
	}

	@Override
	public final void addView(EventWrapperViewPanel<T> view)
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

	@Override
	public void updateViews()
	{
		if(comboBoxPane.isVisible())
		{
			comboBoxPane.repaint();
		}
		for(int i=0;i<comboBoxModel.getSize();i++)
		{
			ViewHolder holder = comboBoxModel.getElementAt(i);
			if(holder == null)
			{
				continue;
			}

			EventWrapperViewPanel<T> view = holder.getView();
			if(view == null)
			{
				continue;
			}
			view.updateView();
		}

		contentPane.repaint();
	}

	@Override
	public void updateViewScale(double scale)
	{
		for(int i = 0; i < comboBoxModel.getSize(); i++)
		{
			ViewHolder holder = comboBoxModel.getElementAt(i);
			if(holder == null)
			{
				continue;
			}

			EventWrapperViewPanel<T> view = holder.getView();
			if(view == null)
			{
				continue;
			}
			view.setScaleFactor(scale);
		}
	}

	@Override
	public void removeView(EventWrapperViewPanel<T> view, boolean dispose)
	{
		ViewHolder found = null;
		for(int i = 0; i < comboBoxModel.getSize(); i++)
		{
			ViewHolder holder = comboBoxModel.getElementAt(i);
			if(holder == null)
			{
				continue;
			}

			EventWrapperViewPanel<T> current = holder.getView();
			if(current == view) // NOPMD
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

	@Override
	public void showDefaultView()
	{
		if(comboBoxModel.getSize() > 0)
		{
			ViewHolder holder = comboBoxModel.getElementAt(0);
			if(holder != null)
			{
				comboBoxModel.setSelectedItem(holder);
				cardLayout.show(contentPane, holder.getId());
				selectedViewChanged();
			}
		}
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
		if(logger.isDebugEnabled()) logger.debug("addNotify - parent: {}", getParent());
	}

	@Override
	public void scrollToEvent()
	{
		EventWrapperViewPanel<T> selectedView = getSelectedView();
		if(selectedView != null)
		{
			selectedView.scrollToEvent();
			setSelectedEvent(selectedView.getSelectedEvent());
		}
	}

	@Override
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
		if(selectedView != null)
		{
			selectedView.focusTable();
		}
		fireChange();
	}

	@Override
	public void dispose()
	{
		super.dispose();
		disposed = true;
		List<ViewHolder> removedPanes = new ArrayList<>();
		for(int i = 0; i < comboBoxModel.getSize(); i++)
		{
			removedPanes.add(comboBoxModel.getElementAt(i));
		}

		for(ViewHolder current : removedPanes)
		{
			removeView(current.getView(), true);
		}
		fireChange();
	}

	@Override
	public boolean isDisposed()
	{
		return disposed;
	}

	private void setSelectedEvent(EventWrapper<T> selectedEvent)
	{
		Object oldValue = this.selectedEvent;
		this.selectedEvent = selectedEvent;
		Object newValue = this.selectedEvent;
		firePropertyChange(SELECTED_EVENT_PROPERTY_NAME, oldValue, newValue);
	}

	@Override
	public EventWrapper<T> getSelectedEvent()
	{
		return selectedEvent;
	}

	@Override
	public EventWrapperViewPanel<T> getViewAt(int index)
	{
		if(index >= 0 && index < comboBoxModel.getSize())
		{
			ViewHolder current = comboBoxModel.getElementAt(index);
			if(current != null)
			{
				return current.getView();
			}
		}
		return null;
	}

	@Override
	public EventWrapperViewPanel<T> getSelectedView()
	{
		ViewHolder holder = getSelectedItem();
		if(holder != null)
		{
			return holder.getView();
		}
		return null;
	}

	@Override
	public void setViewIndex(int index)
	{
		ViewHolder holder = comboBoxModel.getElementAt(index);
		comboBoxModel.setSelectedItem(holder);
		selectedViewChanged();
	}

	@Override
	public int getViewIndex()
	{
		return comboBoxModel.getIndexOf(comboBoxModel.getSelectedItem());
	}

	@Override
	public int getViewCount()
	{
		return comboBoxModel.getSize();
	}

	/*
	 * What the I don't even...
	 * set/getSelectedItem, Y U NO T??
	 */
	private ViewHolder getSelectedItem()
	{
		Object item = comboBoxModel.getSelectedItem();
		if(item == null)
		{
			return null;
		}
		int index = comboBoxModel.getIndexOf(item);
		if(index < 0)
		{
			return null;
		}
		return comboBoxModel.getElementAt(index);
	}

	@Override
	public void closeCurrentFilter()
	{
		ViewHolder holder = getSelectedItem();
		if(holder == null)
		{
			return;
		}
		int index = comboBoxModel.getIndexOf(holder);
		if(index > 0)
		{
			EventWrapperViewPanel<T> lvp = holder.getView();
			removeView(lvp, true);
			selectedViewChanged();
		}
	}

	@Override
	public void closeOtherFilters()
	{
		ViewHolder holder = getSelectedItem();
		int index = 0;
		if(holder != null)
		{
			index = comboBoxModel.getIndexOf(holder);
		}

		int tabCount = comboBoxModel.getSize();
		List<ViewHolder> removedPanes = new ArrayList<>();
		for(int i = 1; i < tabCount; i++)
		{
			if(i != index)
			{
				ViewHolder current = comboBoxModel.getElementAt(i);
				if(current == null)
				{
					continue;
				}
				removedPanes.add(current);
			}
		}

		for(ViewHolder current : removedPanes)
		{
			removeView(current.getView(), true);
		}
		selectedViewChanged();
	}

	@Override
	public void closeAllFilters()
	{
		List<ViewHolder> removedPanes = new ArrayList<>();
		for(int i = 1; i < comboBoxModel.getSize(); i++)
		{
			ViewHolder current = comboBoxModel.getElementAt(i);
			if(current == null)
			{
				continue;
			}
			removedPanes.add(current);
		}

		for(ViewHolder current : removedPanes)
		{
			removeView(current.getView(), true);
		}
		selectedViewChanged();
	}

	@Override
	public void setShowingStatusBar(boolean showingStatusBar)
	{
		int tabCount = comboBoxModel.getSize();
		for(int i = 0; i < tabCount; i++)
		{
			ViewHolder current = comboBoxModel.getElementAt(i);
			if(current == null)
			{
				continue;
			}
			EventWrapperViewPanel<T> view = current.getView();
			if(view == null)
			{
				continue;
			}
			view.setShowingStatusBar(showingStatusBar);
		}
	}

	private class SourceChangeListener
		implements PropertyChangeListener
	{

		@Override
		@SuppressWarnings({"unchecked"})
		public void propertyChange(PropertyChangeEvent evt)
		{
			String propName = evt.getPropertyName();
			switch (propName) {
				case EventWrapperViewPanel.EVENT_SOURCE_PROPERTY:
					if (logger.isDebugEnabled()) logger.debug("EventSource changed: {}", evt.getNewValue());
					EventWrapperViewPanel<T> lvp = (EventWrapperViewPanel<T>) evt.getSource();
					removeView(lvp, false);
					addView(lvp);
					break;
				case EventWrapperViewPanel.SELECTED_EVENT_PROPERTY:
					if (getSelectedView() == evt.getSource())
					{
						if (logger.isDebugEnabled()) logger.debug("EventSource changed: {}", evt.getNewValue());
						setSelectedEvent((EventWrapper<T>) evt.getNewValue());
					}
					break;
				default:
					if (logger.isDebugEnabled()) logger.debug("Other change: {}", propName);
					fireChange();
					break;
			}
		}
	}

	private class MyComboBoxRenderer
		implements ListCellRenderer<ViewHolder>
	{
		private final JLabel label;

		MyComboBoxRenderer()
		{
			label = new JLabel();
			label.setOpaque(true);
			label.setHorizontalAlignment(SwingConstants.LEFT);
			label.setVerticalAlignment(SwingConstants.CENTER);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends ViewHolder> list, ViewHolder value, int index, boolean isSelected, boolean cellHasFocus)
		{
			//noinspection Duplicates
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
			if(value != null)
			{
				EventWrapperViewPanel<T> view = value.getView();
				if (view != null)
				{
					EventSource source = view.getEventSource();
					Condition filter = source.getFilter();

					if (filter == null)
					{
						title = UNFILTERED;
						toolTip = title;
					}
					else
					{
						SavedCondition savedCondition = getMainFrame().getApplicationPreferences().resolveSavedCondition(filter);
						if (savedCondition != null)
						{
							title = savedCondition.getName();
						}
						else
						{
							title = filter.toString();
						}
						toolTip = TextPreprocessor.preformattedTooltip(TextPreprocessor.cropTextBlock(TextPreprocessor.formatCondition(filter)));
					}
				}
			}
			label.setText(title);
			label.setToolTipText(toolTip);

			return label;
		}
	}

}
