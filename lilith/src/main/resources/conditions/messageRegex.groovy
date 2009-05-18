import de.huxhorn.lilith.data.logging.LoggingEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
 * Returns true if the logging events message matches the regex
 * given as searchString.
 */

if(searchString == null || '' == searchString)
{
	// so no string doesn't filter anything
	return true;
}

def event = input?.event;

if(event instanceof LoggingEvent)
{
	try
	{
		def pattern = Pattern.compile(searchString);
		def message = event.message.message;
		if(message)
		{
			return message ==~ pattern; // short for pattern.matcher(message).matches();
		}
	}
	catch(PatternSyntaxException ex)
	{
		// ignore, returns false
	}
}
return false;
