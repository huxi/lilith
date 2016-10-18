/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2013 Joern Huxhorn
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
package de.huxhorn.lilith.swing.actions;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.conditions.Not;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

public class NegateFilterAction
	implements FilterAction, Serializable
{
	private static final long serialVersionUID = -71987935355756555L;

	private BasicFilterAction wrapped;

	public NegateFilterAction()
	{
		this(null);
	}

	public NegateFilterAction(BasicFilterAction wrapped)
	{
		this.wrapped = wrapped;
	}

	public BasicFilterAction getWrapped()
	{
		return wrapped;
	}

	public void setWrapped(BasicFilterAction wrapped)
	{
		this.wrapped = wrapped;
	}

	@Override
	public Condition resolveCondition(ActionEvent e)
	{
		if(wrapped == null)
		{
			return null;
		}
		Condition condition = wrapped.resolveCondition(e);
		if(condition != null)
		{
			return new Not(condition);
		}
		return null;
	}

	@Override
	public Object getValue(String key)
	{
		if(wrapped == null)
		{
			return null;
		}
		return wrapped.getValue(key);
	}

	@Override
	public void putValue(String key, Object value)
	{
		if(wrapped == null)
		{
			return;
		}
		wrapped.putValue(key, value);
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		if(wrapped == null)
		{
			return;
		}
		wrapped.setEnabled(enabled);
	}

	@Override
	public boolean isEnabled()
	{
		return wrapped != null && wrapped.isEnabled();
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		if(wrapped == null)
		{
			return;
		}
		wrapped.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		if(wrapped == null)
		{
			return;
		}
		wrapped.removePropertyChangeListener(listener);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(wrapped == null)
		{
			return;
		}
		ViewContainer viewContainer = wrapped.getViewContainer();
		if(viewContainer == null)
		{
			return;
		}
		Condition condition=resolveCondition(e);
		if(condition == null)
		{
			return;
		}
		viewContainer.applyCondition(condition, e);
	}

	@Override
	public void setEventWrapper(EventWrapper eventWrapper)
	{
		if(wrapped == null)
		{
			return;
		}
		if(wrapped instanceof FilterAction)
		{
			((FilterAction)wrapped).setEventWrapper(eventWrapper);
		}
	}

	@Override
	public void setViewContainer(ViewContainer viewContainer)
	{
		if(wrapped == null)
		{
			return;
		}
		wrapped.setViewContainer(viewContainer);
	}

	@Override
	public ViewContainer getViewContainer()
	{
		if(wrapped == null)
		{
			return null;
		}
		return wrapped.getViewContainer();
	}
}
