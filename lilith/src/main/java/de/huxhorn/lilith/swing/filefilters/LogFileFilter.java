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
package de.huxhorn.lilith.swing.filefilters;

import de.huxhorn.lilith.engine.LogFileFactory;

import java.io.FileFilter;
import java.io.File;

public class LogFileFilter
	implements FileFilter
{
	private LogFileFactory logFileFactory;

	public LogFileFilter(LogFileFactory logFileFactory)
	{
		this.logFileFactory = logFileFactory;
	}

	public boolean accept(File file)
	{
		return file.getName().toLowerCase().endsWith(logFileFactory.getDataFileExtension());
	}
}

