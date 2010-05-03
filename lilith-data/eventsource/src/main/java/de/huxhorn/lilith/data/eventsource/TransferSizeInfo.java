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

/*
 * Copyright 2007-2010 Joern Huxhorn
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

/**
 * This class is a simple datatype to hold informations about the size of an event while
 * "on the wire".
 */
public final class TransferSizeInfo
	implements Cloneable
{
	public Long transferSize;
	public Long uncompressedSize;

	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		TransferSizeInfo that = (TransferSizeInfo) o;

		if(transferSize != null ? !transferSize.equals(that.transferSize) : that.transferSize != null) return false;
		if(uncompressedSize != null ? !uncompressedSize.equals(that.uncompressedSize) : that.uncompressedSize != null)
		{
			return false;
		}

		return true;
	}

	public int hashCode()
	{
		int result;
		result = (transferSize != null ? transferSize.hashCode() : 0);
		result = 31 * result + (uncompressedSize != null ? uncompressedSize.hashCode() : 0);
		return result;
	}

	public TransferSizeInfo clone()
		throws CloneNotSupportedException
	{
		return (TransferSizeInfo) super.clone();
	}
}
