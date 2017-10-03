/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

import de.huxhorn.lilith.appender.InternalLilithAppender;
import de.huxhorn.lilith.engine.EventSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ViewManager<T extends Serializable>
{
	private final Logger logger = LoggerFactory.getLogger(ViewManager.class);

	private final MainFrame mainFrame;
	private final Map<EventSource<T>, ViewContainer<T>> views;

	ViewManager(MainFrame mainFrame)
	{
		this.mainFrame = mainFrame;
		this.views = new HashMap<>();
	}

	public MainFrame getMainFrame()
	{
		return mainFrame;
	}

	ViewContainer<T> retrieveViewContainer(EventSource<T> eventSource)
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

	Map<EventSource<T>, ViewContainer<T>> getViews()
	{
		synchronized (views)
		{
			return new HashMap<>(views);
		}

	}

	protected abstract ViewContainer<T> createViewContainer(EventSource<T> eventSource);

	@SuppressWarnings("UnusedReturnValue")
	List<ViewContainer<T>> minimizeAllViews(ViewContainer beside)
	{
		List<ViewContainer<T>> result = new ArrayList<>();
		synchronized (views)
		{
			for (Map.Entry<EventSource<T>, ViewContainer<T>> entry : views.entrySet())
			{
				ViewContainer<T> value = entry.getValue();

				if (value.resolveViewWindow() != null && value != beside) // NOPMD
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

	@SuppressWarnings("UnusedReturnValue")
	List<ViewContainer<T>> closeAllViews(ViewContainer beside)
	{
		List<ViewContainer<T>> result = new ArrayList<>();
		synchronized (views)
		{
			List<EventSource<T>> inactiveKeys = new ArrayList<>();
			for (Map.Entry<EventSource<T>, ViewContainer<T>> entry : views.entrySet())
			{
				EventSource<T> key = entry.getKey();
				ViewContainer<T> value = entry.getValue();
				EventWrapperViewPanel panel = value.getDefaultView();

				if (!key.isGlobal() && LoggingViewState.INACTIVE == panel.getState() && value != beside) // NOPMD
				{
					inactiveKeys.add(key);
				}
				if (value.resolveViewWindow() != null && value != beside) // NOPMD
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

	@SuppressWarnings("UnusedReturnValue")
	List<ViewContainer<T>> removeInactiveViews(boolean onlyClosed)
	{
		List<ViewContainer<T>> result = new ArrayList<>();
		List<EventSource<T>> inactiveKeys = new ArrayList<>();
		synchronized (views)
		{
			for (Map.Entry<EventSource<T>, ViewContainer<T>> entry : views.entrySet())
			{
				EventSource<T> key = entry.getKey();
				ViewContainer<T> value = entry.getValue();
				EventWrapperViewPanel panel = value.getDefaultView();

				if (!key.isGlobal()
						&& LoggingViewState.ACTIVE != panel.getState()
						&& !key.getSourceIdentifier().equals(InternalLilithAppender.getSourceIdentifier()))
				{
					if (onlyClosed)
					{
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
