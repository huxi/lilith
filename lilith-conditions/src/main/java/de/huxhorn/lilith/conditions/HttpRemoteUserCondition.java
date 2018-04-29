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

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import java.io.ObjectStreamException;

public final class HttpRemoteUserCondition
	implements LilithCondition, SearchStringCondition, Cloneable
{
	private static final long serialVersionUID = -6203602189173332391L;

	public static final String DESCRIPTION = "HttpRemoteUser==";

	private static final String NA = "-"; // IAccessEvent.NA

	private String searchString;
	private transient String userName;

	public HttpRemoteUserCondition()
	{
		this(null);
	}

	public HttpRemoteUserCondition(String searchString)
	{
		setSearchString(searchString);
	}

	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
		userName = processRemoteUserNameString(searchString);
	}

	@Override
	public String getSearchString()
	{
		return searchString;
	}

	@Override
	public String getDescription()
	{
		return DESCRIPTION;
	}

	public String getUserName()
	{
		return userName;
	}

	@Override
	public boolean isTrue(Object value)
	{
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) value;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof AccessEvent)
			{
				AccessEvent event = (AccessEvent) eventObj;
				String remoteUser = processRemoteUserNameString(event.getRemoteUser());
				if(userName == null)
				{
					return remoteUser == null;
				}
				return userName.equals(remoteUser);
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		HttpRemoteUserCondition that = (HttpRemoteUserCondition) o;

		return searchString != null ? searchString.equals(that.searchString) : that.searchString == null;
	}

	@Override
	public int hashCode()
	{
		return searchString != null ? searchString.hashCode() : 0;
	}

	@Override
	public HttpRemoteUserCondition clone()
		throws CloneNotSupportedException
	{
		return (HttpRemoteUserCondition) super.clone();
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append(getDescription());
		if(userName != null)
		{
			result.append('"').append(userName).append('"');
		}
		else
		{
			result.append(NA);
		}
		return result.toString();
	}

	private Object readResolve()
			throws ObjectStreamException
	{
		setSearchString(searchString);
		return this;
	}

	private static String processRemoteUserNameString(String remoteUser)
	{
		if(remoteUser == null)
		{
			return null;
		}
		remoteUser = remoteUser.trim();
		if(NA.equals(remoteUser) || "".equals(remoteUser))
		{
			return null;
		}
		return remoteUser;
	}
}
