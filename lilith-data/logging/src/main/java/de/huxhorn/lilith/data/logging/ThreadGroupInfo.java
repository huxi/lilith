/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
package de.huxhorn.lilith.data.logging;

import java.io.Serializable;

/**
 * Not yet used and/or finished.
 */
public class ThreadGroupInfo
	implements Serializable, Cloneable
{
	private String name;
	private ThreadGroupInfo parent;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ThreadGroupInfo getParent()
	{
		return parent;
	}

	public void setParent(ThreadGroupInfo parent)
	{
		this.parent = parent;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		ThreadGroupInfo that = (ThreadGroupInfo) o;

		if(name != null ? !name.equals(that.name) : that.name != null) return false;
		if(parent != null ? !parent.equals(that.parent) : that.parent != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (parent != null ? parent.hashCode() : 0);
		return result;
	}
}
