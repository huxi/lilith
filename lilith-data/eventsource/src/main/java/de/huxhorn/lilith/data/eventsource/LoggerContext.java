/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

/*
 * Copyright 2007-2017 Joern Huxhorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.huxhorn.lilith.data.eventsource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LoggerContext
	implements Cloneable, Serializable
{
	private static final long serialVersionUID = -1979182848053339299L;

	public static final String APPLICATION_IDENTIFIER_PROPERTY_NAME = "applicationIdentifier";
	public static final String APPLICATION_UUID_PROPERTY_NAME = "applicationUUID";

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
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LoggerContext that = (LoggerContext) o;

		return (birthTime != null ? birthTime.equals(that.birthTime) : that.birthTime == null)
				&& (name != null ? name.equals(that.name) : that.name == null)
				&& (properties != null ? properties.equals(that.properties) : that.properties == null);
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
	public LoggerContext clone()
		throws CloneNotSupportedException
	{
		LoggerContext result = (LoggerContext) super.clone();
		if(properties != null)
		{
			result.properties=new HashMap<>(properties);
		}
		return result;
	}

	@Override
	public String toString()
	{
		return "LoggerContext{" +
				"name='" + name + '\'' +
				", birthTime=" + birthTime +
				", properties=" + properties +
				'}';
	}
}
