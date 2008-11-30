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
package de.huxhorn.lilith.swing;

import de.huxhorn.sulky.buffers.DisposeOperation;
import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.lilith.data.eventsource.EventWrapper;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.Container;
import java.util.*;
import java.util.concurrent.Future;
import java.net.URL;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.Serializable;

public abstract class ViewContainer<T extends Serializable>
	extends JPanel
	implements DisposeOperation
{
	// TODO: property change instead of change?
	//public static final String VIEW_INDEX_PROPERTY_NAME="viewIndex";
	//public static final String VIEW_COUNT_PROPERTY_NAME="viewCount";
	//public static final String SELECTED_VIEW_PROPERTY_NAME="selectedView";
	public static final String SELECTED_EVENT_PROPERTY_NAME="selectedEvent";

	private static final ImageIcon globalFrameImageIcon;
	private static final Map<LoggingViewState, ImageIcon> frameIconImages;
	static
	{
		URL url=EventWrapperViewPanel.class.getResource("/tango/16x16/categories/applications-internet.png");
		if(url!=null)
		{
			globalFrameImageIcon=new ImageIcon(url);
		}
		else
		{
			globalFrameImageIcon=null;
		}
		frameIconImages=new HashMap<LoggingViewState, ImageIcon>();
		url=EventWrapperViewPanel.class.getResource("/tango/16x16/status/network-receive.png");
		if(url!=null)
		{
			frameIconImages.put(LoggingViewState.ACTIVE, new ImageIcon(url));
		}
		url=EventWrapperViewPanel.class.getResource("/tango/16x16/status/network-offline.png");
		if(url!=null)
		{
			frameIconImages.put(LoggingViewState.INACTIVE, new ImageIcon(url));
		}
	}

	private final List<ChangeListener> changeListeners=new LinkedList<ChangeListener>();
	private EventWrapperViewPanel<T> defaultView;
	private MainFrame mainFrame;
//	protected PropertyChangeSupport propertyChangeSuppoert;

	public ViewContainer(MainFrame mainFrame, EventSource<T> eventSource)
	{
		this.mainFrame=mainFrame;
		this.defaultView=createViewPanel(eventSource);
		defaultView.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				if(EventWrapperViewPanel.STATE_PROPERTY.equals(evt.getPropertyName()))
				{
					updateContainerIcon();
				}
			}
		});

	}

	public MainFrame getMainFrame()
	{
		return mainFrame;
	}

	protected abstract EventWrapperViewPanel<T> createViewPanel(EventSource<T> eventSource);

	public abstract EventWrapperViewPanel<T> getViewAt(int index);
	public abstract EventWrapperViewPanel<T> getSelectedView();
	public abstract void addView(EventWrapperViewPanel<T> view);
	public abstract void removeView(EventWrapperViewPanel<T> view, boolean dispose);
	public abstract void showDefaultView();

	public abstract Class getWrappedClass();

	public EventWrapperViewPanel<T> getDefaultView()
	{
		return defaultView;
	}

	public ViewWindow resolveViewWindow()
	{
		Container parent=getParent();
		while(parent!= null && !(parent instanceof ViewWindow))
		{
			parent=parent.getParent();
		}
		return (ViewWindow)parent;
	}

	private void updateContainerIcon()
	{
		ViewWindow window=resolveViewWindow();
		if(window instanceof JFrame)
		{
			JFrame frame=(JFrame) window;
			updateFrameIcon(frame);
		}
		else if(window instanceof JInternalFrame)
		{
			JInternalFrame frame=(JInternalFrame) window;
			updateInternalFrameIcon(frame);
		}
	}

	private static ImageIcon resolveIconForState(LoggingViewState state)
	{
		ImageIcon result = globalFrameImageIcon;
		if(state!=null)
		{
			result = frameIconImages.get(state);
		}
		return result;
	}

	private void updateFrameIcon(JFrame frame)
	{
		ImageIcon frameImageIcon = resolveIconForState(defaultView.getState());

		if(frameImageIcon!=null)
		{
			frame.setIconImage(frameImageIcon.getImage());
		}
	}

	private void updateInternalFrameIcon(JInternalFrame iframe)
	{
		ImageIcon frameImageIcon = resolveIconForState(defaultView.getState());

		if(frameImageIcon!=null)
		{
			iframe.setFrameIcon(frameImageIcon);
			iframe.repaint(); // Apple L&F Bug workaround
		}
	}

	public void addNotify()
	{
		super.addNotify();
		updateContainerIcon();
	}

	public void addChangeListener(ChangeListener listener)
	{
		boolean changed=false;
		synchronized(changeListeners)
		{
			if(!changeListeners.contains(listener))
			{
				changeListeners.add(listener);
				changed=true;
			}
		}
		if(changed)
		{
			fireChange();
		}
	}

	public void removeChangeListener(ChangeListener listener)
	{
		boolean changed=false;
		synchronized(changeListeners)
		{
			if(changeListeners.contains(listener))
			{
				changeListeners.remove(listener);
				changed=true;
			}
		}
		if(changed)
		{
			fireChange();
		}
	}

	public void fireChange()
	{
		ArrayList<ChangeListener> clone;
		synchronized(changeListeners)
		{
			clone = new ArrayList<ChangeListener>(changeListeners);
		}
		ChangeEvent event=new ChangeEvent(this);
		for(ChangeListener listener:clone)
		{
			listener.stateChanged(event);
		}
	}

	public abstract void closeCurrentFilter();
	public abstract void closeOtherFilters();
	public abstract void closeAllFilters();

	public abstract int getViewCount();
	public abstract void setViewIndex(int newView);
	public abstract int getViewIndex();
	public abstract void hideSearchPanel();
	public abstract void showSearchPanel(Future<Integer> future);
	public abstract boolean isSearching();
	public abstract void cancelSearching();
	public abstract ProgressGlassPane getProgressPanel();

	public abstract EventWrapper<T> getSelectedEvent();
}
