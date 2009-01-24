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

public class SourceIdentifier
	implements Serializable, Comparable<SourceIdentifier>, Cloneable
{
	private static final long serialVersionUID = -3221347884837534650L;

	private String identifier;
	private String secondaryIdentifier;

	public SourceIdentifier()
	{
	}

	public SourceIdentifier(String identifier)
	{
		this(identifier, null);
	}

	public SourceIdentifier(String identifier, String secondaryIdentifier)
	{
		this.identifier = identifier;
		this.secondaryIdentifier = secondaryIdentifier;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}

	public String getSecondaryIdentifier()
	{
		return secondaryIdentifier;
	}

	public void setSecondaryIdentifier(String secondaryIdentifier)
	{
		this.secondaryIdentifier = secondaryIdentifier;
	}

	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		final SourceIdentifier that = (SourceIdentifier) o;

		if(identifier != null ? !identifier.equals(that.identifier) : that.identifier != null) return false;
		return !(secondaryIdentifier != null ? !secondaryIdentifier
			.equals(that.secondaryIdentifier) : that.secondaryIdentifier != null);
	}

	public int hashCode()
	{
		int result;
		result = (identifier != null ? identifier.hashCode() : 0);
		result = 29 * result + (secondaryIdentifier != null ? secondaryIdentifier.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append(identifier);
		if(secondaryIdentifier != null)
		{
			result.append("-");
			result.append(secondaryIdentifier);
		}
		return result.toString();
	}

	public int compareTo(SourceIdentifier o)
	{
		if(this.identifier == null)
		{
			if(o.identifier != null)
			{
				return -1;
			}
		}
		else if(o.identifier == null)
		{
			return 1;
		}
		else
		{
			int compare = this.identifier.compareTo(o.identifier);
			if(compare != 0)
			{
				return compare;
			}
		}

		if(this.secondaryIdentifier == null)
		{
			if(o.secondaryIdentifier != null)
			{
				return -1;
			}
		}
		else if(o.secondaryIdentifier == null)
		{
			return 1;
		}
		else
		{
			int compare = this.secondaryIdentifier.compareTo(o.secondaryIdentifier);
			if(compare != 0)
			{
				return compare;
			}
		}

		return 0;
	}

	public SourceIdentifier clone()
		throws CloneNotSupportedException
	{
		return (SourceIdentifier) super.clone();
	}
}