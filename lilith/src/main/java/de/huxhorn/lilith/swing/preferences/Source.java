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

package de.huxhorn.lilith.swing.preferences;

import java.io.Serializable;

public class Source
	implements Serializable, Comparable<Source>
{
	private static final long serialVersionUID = -1855258442029284033L;

	private String name;
	private String identifier;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	String getIdentifier()
	{
		return identifier;
	}

	void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		final Source source = (Source) o;

		if(identifier != null ? !identifier.equals(source.identifier) : source.identifier != null) return false;
		return !(name != null ? !name.equals(source.name) : source.name != null);
	}

	@Override
	public int hashCode()
	{
		int result;
		result = (name != null ? name.hashCode() : 0);
		result = 29 * result + (identifier != null ? identifier.hashCode() : 0);
		return result;
	}

	@Override
	public int compareTo(Source other)
	{
		//noinspection StringEquality
		if(this.name == other.name)
		{
			return 0;
		}
		if(this.name == null)
		{
			return -1;
		}
		if(other.name == null)
		{
			return 1;
		}
		return this.name.compareToIgnoreCase(other.name);
	}

}
