/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.huxhorn.lilith.data.eventsource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LoggerContext
	implements Cloneable, Serializable
{
	private static final long serialVersionUID = -1979182848053339299L;

	private String name;
	private Long birthTime;
	private Map<String, String> properties;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Long getBirthTime()
	{
		return birthTime;
	}

	public void setBirthTime(Long birthTime)
	{
		this.birthTime = birthTime;
	}

	public Map<String, String> getProperties()
	{
		return properties;
	}

	public void setProperties(Map<String, String> properties)
	{
		this.properties = properties;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		LoggerContext that = (LoggerContext) o;

		if(birthTime != null ? !birthTime.equals(that.birthTime) : that.birthTime != null) return false;
		if(name != null ? !name.equals(that.name) : that.name != null) return false;
		if(properties != null ? !properties.equals(that.properties) : that.properties != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (birthTime != null ? birthTime.hashCode() : 0);
		result = 31 * result + (properties != null ? properties.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return "LoggerContext[name="+name+", birthTime="+birthTime+", properties="+properties+"]";
	}

	@Override
	public LoggerContext clone()
		throws CloneNotSupportedException
	{
		LoggerContext result = (LoggerContext) super.clone();
		if(properties != null)
		{
			result.properties=new HashMap<String, String>(properties);
		}
		return result;
	}
}