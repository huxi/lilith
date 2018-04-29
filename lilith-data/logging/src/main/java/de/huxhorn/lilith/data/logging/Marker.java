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

package de.huxhorn.lilith.data.logging;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Marker
	implements Serializable
{
	private static final long serialVersionUID = -4828769420328139691L;

	private String name;
	private Map<String, Marker> references;

	public Marker()
	{
		this("Marker");
	}

	public Marker(String name)
	{
		setName(name);
	}

	public void setName(String name)
	{
		if(name == null)
		{
			throw new IllegalArgumentException("Marker name must not be null!");
		}
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public Map<String, Marker> getReferences()
	{
		if(references == null)
		{
			return null;
		}
		return new HashMap<>(references);
	}

	public void remove(Marker marker)
	{
		if(references != null)
		{
			references.remove(marker.getName());
		}
	}

	public void add(Marker marker)
	{
		if(references == null)
		{
			references = new HashMap<>();
		}
		if(!references.containsKey(marker.getName()))
		{
			references.put(marker.getName(), marker);
		}
	}

	public void setReferences(Map<String, Marker> references)
	{
		this.references = references;
	}

	public boolean hasReferences()
	{
		return references != null && !references.isEmpty();
	}

	public boolean contains(Marker other)
	{
		if(other == null)
		{
			throw new IllegalArgumentException("Other cannot be null");
		}

		if(this.name.equals(other.name))
		{
			// see comment in c'tor
			return true;
		}

		if(hasReferences())
		{
			Set<String> collectedMarkers = collectMarkerNames(this, null);
			return collectedMarkers.contains(other.getName());
		}
		return false;
	}

	public boolean contains(String name)
	{
		if(name == null)
		{
			return false; // as documented in Marker interface.
		}

		if(this.name.equals(name))
		{
			return true;
		}

		if(hasReferences())
		{
			return collectMarkerNames().contains(name);
		}
		return false;
	}

	public Set<String> collectMarkerNames()
	{
		return collectMarkerNames(this, null);
	}

	private static Set<String> collectMarkerNames(Marker marker, Set<String> collectedMarkerNames)
	{
		if(collectedMarkerNames == null)
		{
			collectedMarkerNames = new HashSet<>();
		}
		if(!collectedMarkerNames.contains(marker.getName()))
		{
			collectedMarkerNames.add(marker.getName());
			if(marker.hasReferences())
			{
				for(Map.Entry<String, Marker> current : marker.getReferences().entrySet())
				{
					Marker child = current.getValue();
					if(!collectedMarkerNames.contains(child.getName()))
					{
						collectMarkerNames(child, collectedMarkerNames);
					}
				}
			}
		}

		return collectedMarkerNames;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		final Marker marker = (Marker) o;

		return !(name != null ? !name.equals(marker.name) : marker.name != null);
	}

	@Override
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
