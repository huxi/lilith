/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2016 Joern Huxhorn
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

package de.huxhorn.lilith.services.clipboard;

import de.huxhorn.lilith.data.logging.xml.LoggingXmlEncoder;

public class LoggingEventXmlFormatter
		extends AbstractLoggingEventEncoderFormatter
{
	private static final long serialVersionUID = 2263706767713579277L;

	public LoggingEventXmlFormatter()
	{
		super(new LoggingXmlEncoder(false, true));
	}

	public String getName()
	{
		return "Copy event as XML";
	}

	public String getDescription()
	{
		return "Copies the XML representation of the event to the clipboard.";
	}

	public String getAccelerator()
	{
		return null;
	}

	//Not performing this hardcore check for the sake of performance.
	//@Override
	//public boolean isCompatible(Object object)
	//{
	//	return toString(object) != null;
	//}

	public boolean isNative()
	{
		return true;
	}
}
