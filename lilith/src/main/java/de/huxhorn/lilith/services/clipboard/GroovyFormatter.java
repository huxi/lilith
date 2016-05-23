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

import de.huxhorn.sulky.groovy.GroovyInstance;

import java.io.File;

public class GroovyFormatter
		implements ClipboardFormatter
{
	private static final long serialVersionUID = -2815978833786033048L;
	private GroovyInstance groovyInstance;

	public GroovyFormatter()
	{
		this(null);
	}

	public GroovyFormatter(String fileName)
	{
		groovyInstance = new GroovyInstance();
		setGroovyFileName(fileName);
	}

	public void setGroovyFileName(String fileName)
	{
		groovyInstance.setGroovyFileName(fileName);
	}

	private ClipboardFormatter getFormatter()
	{
		return groovyInstance.getInstanceAs(ClipboardFormatter.class);
	}

	public String getName()
	{
		ClipboardFormatter formatter = getFormatter();
		if (formatter != null)
		{
			return formatter.getName();
		}
		String fileName = groovyInstance.getGroovyFileName();
		if (fileName != null)
		{
			File file = new File(fileName);
			return file.getName();
		}
		return "Missing file!";
	}

	public String getDescription()
	{
		ClipboardFormatter formatter = getFormatter();
		if (formatter != null)
		{
			return formatter.getDescription();
		}
		String fileName = groovyInstance.getGroovyFileName();
		String shortName = "Missing file!";
		if (fileName != null)
		{
			File file = new File(fileName);
			shortName = file.getName();
		}

		Class instanceClass = groovyInstance.getInstanceClass();
		if (instanceClass != null)
		{
			return shortName + " - Expected ClipboardFormatter but received " + instanceClass.getName() + "!";
		}

		return shortName + " - " + groovyInstance.getErrorMessage();
	}

	public String getAccelerator()
	{
		ClipboardFormatter formatter = getFormatter();
		return formatter == null ? null : formatter.getAccelerator();
	}

	public boolean isCompatible(Object object)
	{
		ClipboardFormatter formatter = getFormatter();
		return formatter != null && formatter.isCompatible(object);
	}

	public String toString(Object object)
	{
		ClipboardFormatter formatter = getFormatter();
		if (formatter == null)
		{
			return null;
		}
		return formatter.toString(object);
	}
}
