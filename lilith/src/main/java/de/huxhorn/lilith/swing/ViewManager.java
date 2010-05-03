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

import de.huxhorn.lilith.engine.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ViewManager<T extends Serializable>
{
	private final Logger logger = LoggerFactory.getLogger(ViewManager.class);

	private final Map<EventSource<T>, ViewContainer<T>> views;
	private MainFrame mainFrame;

	public ViewManager(MainFrame mainFrame)
	{
		this.mainFrame = mainFrame;
		this.views = new HashMap<EventSource<T>, ViewContainer<T>>();
	}

	public MainFrame getMainFrame()
	{
		return mainFrame;
	}

	public ViewContainer<T> retrieveViewContainer(EventSource<T> eventSource)
	{
		ViewContainer<T> result;
		synchronized (views)
		{
			result = views.get(eventSource);
			if (result == null)
			{
				result = createViewContainer(eventSource);
				views.put(eventSource, result);
				if (logger.isInfoEnabled()) logger.info("Added view for eventSource {}.", eventSource);
			}
			return result;
		}
	}

	public Map<EventSource<T>, ViewContainer<T>> getViews()
	{
		synchronized (views)
		{
			return new HashMap<EventSource<T>, ViewContainer<T>>(views);
		}

	}

	protected abstract ViewContainer<T> createViewContainer(EventSource<T> eventSource);

	List<ViewContainer<T>> minimizeAllViews(ViewContainer beside)
	{
		List<ViewContainer<T>> result = new ArrayList<ViewContainer<T>>();
		synchronized (views)
		{
			for (Map.Entry<EventSource<T>, ViewContainer<T>> entry : views.entrySet())
			{
				ViewContainer<T> value = entry.getValue();

				if (value.resolveViewWindow() != null && value != beside)
				{
					result.add(value);
				}
			}
		}
		for (ViewContainer<T> current : result)
		{
			minimizeViewContainer(current);
		}
		return result;
	}

	List<ViewContainer<T>> closeAllViews(ViewContainer beside)
	{
		List<ViewContainer<T>> result = new ArrayList<ViewContainer<T>>();
		synchronized (views)
		{
			List<EventSource<T>> inactiveKeys = new ArrayList<EventSource<T>>();
			for (Map.Entry<EventSource<T>, ViewContainer<T>> entry : views.entrySet())
			{
				EventSource<T> key = entry.getKey();
				ViewContainer<T> value = entry.getValue();
				EventWrapperViewPanel panel = value.getDefaultView();

				if (!key.isGlobal() && LoggingViewState.INACTIVE == panel.getState() && value != beside)
				{
					inactiveKeys.add(key);
				}
				if (value.resolveViewWindow() != null && value != beside)
				{
					result.add(value);
				}
			}
			for (EventSource<T> current : inactiveKeys)
			{
				removeView(current);
			}
		}
		for (ViewContainer<T> current : result)
		{
			closeViewContainer(current);
		}
		return result;
	}

	List<ViewContainer<T>> removeInactiveViews(boolean onlyClosed)
	{
		List<ViewContainer<T>> result = new ArrayList<ViewContainer<T>>();
		List<EventSource<T>> inactiveKeys = new ArrayList<EventSource<T>>();
		synchronized (views)
		{
			for (Map.Entry<EventSource<T>, ViewContainer<T>> entry : views.entrySet())
			{
				EventSource<T> key = entry.getKey();
				ViewContainer<T> value = entry.getValue();
				EventWrapperViewPanel panel = value.getDefaultView();

				if (!key.isGlobal() && LoggingViewState.INACTIVE == panel.getState())
				{
					if (onlyClosed)
					{
						//if(value.resolveInternalFrame()==null && value.resolveFrame()==null)
						if (value.resolveViewWindow() == null)
						{
							result.add(value);
							inactiveKeys.add(key);
						}
					}
					else
					{
						result.add(value);
						inactiveKeys.add(key);
					}
				}
			}
		}
		for (EventSource<T> current : inactiveKeys)
		{
			removeView(current);
		}
		for (ViewContainer<T> current : result)
		{
			closeViewContainer(current);
		}
		return result;
	}

	private void minimizeViewContainer(ViewContainer<T> lvc)
	{
		ViewWindow window = lvc.resolveViewWindow();
		if (window != null)
		{
			window.minimizeWindow();
		}
	}

	void closeViewContainer(ViewContainer<T> lvc)
	{
		ViewWindow window = lvc.resolveViewWindow();
		if (window != null)
		{
			window.closeWindow();
		}
	}

	private void removeView(EventSource<T> source)
	{
		ViewContainer previous;
		synchronized (views)
		{
			previous = views.remove(source);
		}
		if (previous != null)
		{
			previous.dispose();
			if (logger.isDebugEnabled()) logger.debug("Removed view for {}.", source);
		}
	}
}
