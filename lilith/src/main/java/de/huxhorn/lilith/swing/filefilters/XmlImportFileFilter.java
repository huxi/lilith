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
package de.huxhorn.lilith.swing.filefilters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class XmlImportFileFilter
	extends FileFilter
{

	public boolean accept(File file)
	{
		if(file.isDirectory())
		{
			return true;
		}

		if(file.isFile())
		{
			String fileName = file.getName().toLowerCase();
			if(fileName.endsWith(".xml"))
			{
				return true;
			}
		}
		return false;
	}

	public String getDescription()
	{
		return "XML files";
	}
}
