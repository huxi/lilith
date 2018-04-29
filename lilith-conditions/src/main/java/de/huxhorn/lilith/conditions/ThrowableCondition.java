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

package de.huxhorn.lilith.conditions;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

public final class ThrowableCondition
	implements LilithCondition, SearchStringCondition, Cloneable
{
	private static final long serialVersionUID = -6937557692850490570L;

	public static final String DESCRIPTION = "Throwable";

	private String searchString;

	public ThrowableCondition()
	{
		this(null);
	}

	public ThrowableCondition(String searchString)
	{
		setSearchString(searchString);
	}

	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
	}

	@Override
	public String getSearchString()
	{
		return searchString;
	}

	@Override
	public boolean isTrue(Object value)
	{
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) value;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof LoggingEvent)
			{
				LoggingEvent event = (LoggingEvent) eventObj;

				ThrowableInfo throwable = event.getThrowable();
				if(throwable == null)
				{
					return false;
				}
				if(searchString == null || "".equals(searchString))
				{
					// no search string means "match any Throwable"
					return true;
				}

				if(collectThrowableNames(throwable).contains(searchString))
				{
					// otherwise match if any exception name matches
					return true;
				}
			}
		}
		return false;
	}

	public static Set<String> collectThrowableNames(ThrowableInfo throwable)
	{
		Set<String> result=new HashSet<>();
		IdentityHashMap<ThrowableInfo, String> dejaVu=new IdentityHashMap<>();
		collectThrowableNamesRecursive(throwable, dejaVu);
		for (String name : dejaVu.values())
		{
			if(name != null)
			{
				result.add(name);
			}
		}
		return result;
	}

	private static void collectThrowableNamesRecursive(ThrowableInfo throwable, IdentityHashMap<ThrowableInfo, String> dejaVu)
	{
		if(throwable == null)
		{
			return;
		}

		if(dejaVu.containsKey(throwable))
		{
			return;
		}

		dejaVu.put(throwable, throwable.getName());

		collectThrowableNamesRecursive(throwable.getCause(), dejaVu);

		ThrowableInfo[] suppressed = throwable.getSuppressed();
		if(suppressed == null)
		{
			return;
		}

		for (ThrowableInfo current : suppressed)
		{
			collectThrowableNamesRecursive(current, dejaVu);
		}
	}

	@Override
	public ThrowableCondition clone()
		throws CloneNotSupportedException
	{
		ThrowableCondition result = (ThrowableCondition) super.clone();
		result.setSearchString(searchString);
		return result;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ThrowableCondition that = (ThrowableCondition) o;

		return searchString != null ? searchString.equals(that.searchString) : that.searchString == null;

	}

	@Override
	public int hashCode()
	{
		return searchString != null ? searchString.hashCode() : 0;
	}

	@Override
	public String getDescription()
	{
		return DESCRIPTION;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append(getDescription()).append('(');
		if(searchString != null)
		{
			result.append(searchString);
		}
		result.append(')');
		return result.toString();
	}
}
