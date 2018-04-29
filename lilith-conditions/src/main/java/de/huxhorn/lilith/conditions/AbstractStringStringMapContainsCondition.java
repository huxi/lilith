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

public abstract class AbstractStringStringMapContainsCondition
	extends AbstractMapCondition<String>
	implements Cloneable
{
	private static final long serialVersionUID = -2160817812311122642L;

	AbstractStringStringMapContainsCondition()
	{
		super();
	}

	AbstractStringStringMapContainsCondition(String key, String value)
	{
		super(key, value);
	}

	@Override
	protected abstract Map<String, String> resolveMap(Object element);

	/**
	 * Only called if getValue() is not null.
	 *
	 * @param mapValue the value to compare against value.
	 * @return true, if this condition matches.
	 */
	@Override
	protected boolean isTrueForValue(String mapValue)
	{
		return getValue().equals(mapValue);
	}

	@Override
	public AbstractStringStringMapContainsCondition clone() throws CloneNotSupportedException {
		return (AbstractStringStringMapContainsCondition) super.clone();
	}

	@Override
	public abstract String getDescription();
}
