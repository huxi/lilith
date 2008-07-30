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
package de.huxhorn.lilith.filters;

import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;

import java.io.ObjectInputStream;
import java.io.IOException;

public class MessageContainsCondition
	implements Condition
{
	private static final long serialVersionUID = 6162454733163822936L;

	private String searchString;
	private boolean ignoringCase;
	private transient String actualSearchString;

	public MessageContainsCondition()
	{
		this(null, false);
	}

	public MessageContainsCondition(String searchString, boolean ignoringCase)
	{
		setIgnoringCase(ignoringCase);
		setSearchString(searchString);
	}

	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
		if(ignoringCase && searchString!=null)
		{
			this.actualSearchString = searchString.toLowerCase();
		}
		else
		{
			this.actualSearchString = searchString;
		}
	}

	public String getSearchString()
	{
		return searchString;
	}

	public void setIgnoringCase(boolean ignoringCase)
	{
		if(this.ignoringCase!=ignoringCase)
		{
			this.ignoringCase = ignoringCase;
			setSearchString(searchString);
		}
	}

	public boolean isIgnoringCase()
	{
		return ignoringCase;
	}

	public boolean isTrue(Object value)
	{
		if(actualSearchString==null)
		{
			return false;
		}
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper=(EventWrapper)value;
			Object eventObj = wrapper.getEvent();
			if(actualSearchString.length()==0)
			{
				return true;
			}
			if(eventObj instanceof LoggingEvent)
			{
				LoggingEvent event=(LoggingEvent) eventObj;

				String message=event.getMessage();

				if(message==null)
				{
					return false;
				}

				if(ignoringCase)
				{
					message = message.toLowerCase();
				}
				return message.contains(actualSearchString);
			}
		}
		return false;
	}

	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final MessageContainsCondition that = (MessageContainsCondition) o;

		if (ignoringCase != that.ignoringCase) return false;
		return !(searchString != null ? !searchString.equals(that.searchString) : that.searchString != null);
	}

	public int hashCode()
	{
		int result;
		result = (searchString != null ? searchString.hashCode() : 0);
		result = 29 * result + (ignoringCase ? 1 : 0);
		return result;
	}

	private void readObject(ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		setSearchString(this.searchString);
	}

	public MessageContainsCondition clone() throws CloneNotSupportedException
	{
		return (MessageContainsCondition) super.clone();
	}

	public String toString()
	{
		StringBuffer result=new StringBuffer();
		result.append("message.");
		if(ignoringCase)
		{
			result.append("containsIgnoreCase(");
		}
		else
		{
			result.append("contains(");
		}
		if(searchString!=null)
		{
			result.append("\"");
			result.append(searchString);
			result.append("\"");
		}
		else
		{
			result.append("null");
		}
		result.append(")");
		return result.toString();
	}
}
