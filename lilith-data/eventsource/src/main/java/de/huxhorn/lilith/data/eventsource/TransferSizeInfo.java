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
