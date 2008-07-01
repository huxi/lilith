/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Marker
		implements Serializable
{
	private String name;
	private Map<String, Marker> children;

	public Marker(String name)
	{
		if (name == null)
		{
			throw new IllegalArgumentException("Markername must not be null!");
		}
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public Map<String, Marker> getChildren()
	{
		if (children == null)
		{
			return null;
		}
		return Collections.unmodifiableMap(children);
	}

	public void remove(Marker marker)
	{
		if (children != null)
		{
			children.remove(marker.getName());
		}
	}

	public void add(Marker marker)
	{
		if (children == null)
		{
			children = new HashMap<String, Marker>();
		}
		if (!children.containsKey(marker.getName()))
		{
			children.put(marker.getName(), marker);
		}
	}

	public void setChildren(Map<String, Marker> children)
	{
		this.children = children;
	}

	public boolean hasChildren()
	{
		return children != null && children.size() != 0;
	}

	public boolean contains(Marker other)
	{
		if (other == null)
		{
			throw new IllegalArgumentException("Other cannot be null");
		}

		if (this.name.equals(other.name))
		{
			// see comment in c'tor
			return true;
		}

		if (hasChildren())
		{
			Set<String> collectedMarkers = collectChildMarkerNames(this, null);
			return collectedMarkers.contains(other.getName());
		}
		return false;
	}

	public boolean contains(String name)
	{
		if (name == null)
		{
			return false; // as documented in Marker interface.
		}

		if (this.name.equals(name))
		{
			return true;
		}

		if (hasChildren())
		{
			Set<String> collectedMarkerNames = collectChildMarkerNames(this, null);
			return collectedMarkerNames.contains(name);
		}
		return false;
	}

	private Set<String> collectChildMarkerNames(Marker marker, Set<String> collectedMarkerNames)
	{
		if (collectedMarkerNames == null)
		{
			collectedMarkerNames = new HashSet<String>();
		}
		if (!collectedMarkerNames.contains(marker.getName()))
		{
			collectedMarkerNames.add(marker.getName());
			if (marker.hasChildren())
			{
				for(Map.Entry<String, Marker> current: marker.getChildren().entrySet())
				{
					Marker child = current.getValue();
					if (!collectedMarkerNames.contains(child.getName()))
					{
						collectChildMarkerNames(child, collectedMarkerNames);
					}
				}
			}
		}

		return collectedMarkerNames;
	}

	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final Marker marker = (Marker) o;

		return !(name != null ? !name.equals(marker.name) : marker.name != null);
	}

	public int hashCode()
	{
		return (name != null ? name.hashCode() : 0);
	}

	@Override
	public String toString()
	{
		return name;
	}
}
