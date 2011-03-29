/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
package de.huxhorn.lilith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionBundle
	implements Comparable<VersionBundle>
{
	private String version;
	private long timestamp;

	public VersionBundle(String version)
	{
		this(version, -1);
	}

	public VersionBundle(String version, long timestamp)
	{
		this.version = version;
		if(timestamp < 0)
		{
			timestamp = -1;
		}
		this.timestamp = timestamp;
	}

	public String getVersion()
	{
		return version;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public static VersionBundle fromString(String input)
	{
		final Logger logger = LoggerFactory.getLogger(VersionBundle.class);

		if(input == null)
		{
			return null;
		}

		input = input.trim();
		int hashIndex=input.indexOf('#');
		VersionBundle result;
		if(hashIndex < 0)
		{
			if(logger.isWarnEnabled()) logger.warn("Retrieved version without timestamp! '{}'", input);
			result=new VersionBundle(input);
		}
		else
		{
			String version = input.substring(0, hashIndex);
			String timestampStr = input.substring(hashIndex+1);

			try
			{
				result=new VersionBundle(version, Long.parseLong(timestampStr));
			}
			catch(NumberFormatException ex)
			{
				if(logger.isWarnEnabled()) logger.warn("Retrieved version with invalid timestamp! '"+input+"'", ex);
				result=new VersionBundle(input);
			}
		}
		if(logger.isDebugEnabled()) logger.debug("VersionBundle: {}", result);
		return result;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		VersionBundle that = (VersionBundle) o;

		if(timestamp != that.timestamp) return false;
		if(version != null ? !version.equals(that.version) : that.version != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = version != null ? version.hashCode() : 0;
		result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public String toString()
	{
		return version + '#' + timestamp;
	}

	/**
	 * Only the timestamps are compared by this method.
	 * It can also handle comparison with null in which case this is greater.
	 * @param o the other VersionBundle.
	 * @return  a negative integer, zero, or a positive integer as this object
	 *		is less than, equal to, or greater than the specified object.
	 */
	public int compareTo(VersionBundle o)
	{
		if(o == null)
		{
			return 1;
		}
		long dif=this.timestamp - o.timestamp;
		if(dif < 0)
		{
			return -1;
		}
		if(dif > 0)
		{
			return 1;
		}
		return 0;
	}
}
