import de.huxhorn.lilith.data.logging.LoggingEvent
import de.huxhorn.lilith.data.logging.LoggingEvent.Level

/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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

/**
 * Returns true if the logging event has a level higher or equal to the
 * one provided with the searchString.
 */

if(searchString == null || '' == searchString)
{
	// so no string doesn't filter anything
	return true;
}

def event = input?.event;

if(event instanceof LoggingEvent)
{
	def level = event.level;

	if(level)
	{
		try
		{
			def threshold=LoggingEvent.Level.valueOf(searchString);
			if(level >= threshold)
			{
				return true;
			}
		}
		catch(IllegalArgumentException ex)
		{
			// ignore
		}
	}
}
return false;
