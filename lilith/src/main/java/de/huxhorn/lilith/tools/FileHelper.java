/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

package de.huxhorn.lilith.tools;

import de.huxhorn.lilith.api.FileConstants;
import java.io.File;
import java.util.Locale;

final class FileHelper
{
	static
	{
		new FileHelper(); // stfu
	}

	private FileHelper() {}

	static File resolveDataFile(File baseFile)
	{
		String fileStr = baseFile.getAbsolutePath();

		if(fileStr.toLowerCase(Locale.US).endsWith(FileConstants.FILE_EXTENSION))
		{
			return new File(fileStr);
		}
		if(fileStr.toLowerCase(Locale.US).endsWith(FileConstants.INDEX_FILE_EXTENSION))
		{
			fileStr = fileStr.substring(0, fileStr.length() - FileConstants.INDEX_FILE_EXTENSION.length());
		}
		return new File(fileStr + FileConstants.FILE_EXTENSION);
	}

	static File resolveIndexFile(File baseFile)
	{
		String fileStr = baseFile.getAbsolutePath();

		if(fileStr.toLowerCase(Locale.US).endsWith(FileConstants.INDEX_FILE_EXTENSION))
		{
			return new File(fileStr);
		}
		if(fileStr.toLowerCase(Locale.US).endsWith(FileConstants.FILE_EXTENSION))
		{
			fileStr = fileStr.substring(0, fileStr.length() - FileConstants.FILE_EXTENSION.length());
		}
		return new File(fileStr + FileConstants.INDEX_FILE_EXTENSION);
	}
}
