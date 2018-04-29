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

import java.util.Map;

public abstract class AbstractMapCondition<V>
	implements LilithCondition, Cloneable
{
	private static final long serialVersionUID = 6645626846624677071L;

	private String key;
	private String value;

	AbstractMapCondition()
	{
		super();
	}

	AbstractMapCondition(String key, String value)
	{
		this.key = key;
		this.value = value;
	}

	public final String getKey()
	{
		return key;
	}

	public final void setKey(String key)
	{
		this.key = key;
	}

	public final String getValue()
	{
		return value;
	}

	public final void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public final boolean isTrue(Object object)
	{
		if(key == null)
		{
			return false;
		}
		Map<String, V> map = resolveMap(object);
		if(map == null || map.isEmpty())
		{
			return false;
		}
		if(value == null)
		{
			// no value means any value for the given key is true.
			return map.containsKey(key);
		}

		return isTrueForValue(map.get(key));
	}

	protected abstract Map<String, V> resolveMap(Object element);

	/**
	 * Only called if getValue() is not null.
	 *
	 * @param mapValue the value to compare against value.
	 * @return true, if this condition matches.
	 */
	protected abstract boolean isTrueForValue(V mapValue);

	@Override
	public AbstractMapCondition clone() throws CloneNotSupportedException {
		return (AbstractMapCondition) super.clone();
	}

	@Override
	public final String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append(getDescription()).append('(');
		if(key != null)
		{
			result.append('"').append(key).append('"');
		}
		else
		{
			result.append("null");
		}
		if(value != null)
		{
			result.append(",\"").append(value).append('"');
		}
		result.append(')');
		return result.toString();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AbstractMapCondition that = (AbstractMapCondition) o;

		return (key != null ? key.equals(that.key) : that.key == null)
				&& (value != null ? value.equals(that.value) : that.value == null);
	}

	@Override
	public int hashCode()
	{
		int result = key != null ? key.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}

	@Override
	public abstract String getDescription();
}
