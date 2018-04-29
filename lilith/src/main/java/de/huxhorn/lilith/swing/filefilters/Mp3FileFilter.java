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
package de.huxhorn.lilith.swing.filefilters;

import java.io.File;
import java.util.Locale;
import javax.swing.filechooser.FileFilter;

@SuppressWarnings("PMD.ClassNamingConventions")
public class Mp3FileFilter
	extends FileFilter
{
	@Override
	public boolean accept(File pathname)
	{
		return pathname.isDirectory() || pathname.isFile() && pathname.getName().toLowerCase(Locale.US).endsWith(".mp3");
	}

	@Override
	public String getDescription()
	{
		return "MP3 files";
	}
}
