/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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

import javax.swing.AbstractListModel;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GenericSortedListModel<T extends Comparable>
	extends AbstractListModel
{
	private final List<T> data;
	private final Comparator<T> comparator;

	public GenericSortedListModel()
	{
		this(null);
	}

	public GenericSortedListModel(Comparator<T> comparator)
	{
		this.data = new ArrayList<T>();
		this.comparator = comparator;
	}

	public void setData(List<T> data)
	{
		if(!this.data.equals(data))
		{
			int size = this.data.size();
			if(size > 0)
			{
				this.data.clear();
				fireIntervalRemoved(this, 0, size - 1);
			}
			size = data.size();
			if(size > 0)
			{
				this.data.addAll(data);
				if(comparator != null)
				{
					Collections.sort(this.data, comparator);
				}
				else
				{
					Collections.sort(this.data);
				}
				fireIntervalAdded(this, 0, size - 1);
			}
		}
	}

	public List<T> getData()
	{
		return new ArrayList<T>(data);
	}

	public void add(T element)
	{
		if(!data.contains(element))
		{
			data.add(element);
			if(comparator != null)
			{
				Collections.sort(this.data, comparator);
			}
			else
			{
				Collections.sort(this.data);
			}
			int size=data.size();
			fireContentsChanged(this, 0, size - 1);
		}
	}

	public void remove(T element)
	{
		int index=data.indexOf(element);
		if(index >= 0)
		{
			data.remove(index);
			fireIntervalRemoved(this, index, index);
		}
	}

	/**
	 * Returns the length of the list.
	 *
	 * @return the length of the list
	 */
	public int getSize()
	{
		return data.size();
	}

	/**
	 * Returns the value at the specified index.
	 *
	 * @param index the requested index
	 * @return the value at <code>index</code>
	 */
	public T getElementAt(int index)
	{
		return data.get(index);
	}
}
