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
package de.huxhorn.lilith.engine.impl;

import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.api.FileConstants;
import de.huxhorn.lilith.engine.LogFileFactory;

import java.io.File;

public class LogFileFactoryImpl
	implements LogFileFactory
{
	private File baseDir;
	private String dataFileExtension;

	public LogFileFactoryImpl(File baseDir)
	{
		this.baseDir = baseDir;
		this.dataFileExtension = FileConstants.FILE_EXTENSION;
		if(!dataFileExtension.startsWith("."))
		{
			this.dataFileExtension = "." + this.dataFileExtension;
		}
	}

	private String getBaseFileName(SourceIdentifier si)
	{
		String primaryName = si.getIdentifier();
		String secondaryName = si.getSecondaryIdentifier();
		primaryName = prepareName(primaryName);
		if(secondaryName != null)
		{
			secondaryName = prepareName(secondaryName);
			File parent = new File(baseDir, primaryName);
			parent.mkdirs();
			File baseFile = new File(parent, secondaryName);
			return baseFile.getAbsolutePath();
		}
		else
		{
			baseDir.mkdirs();
			File baseFile = new File(baseDir, primaryName);
			return baseFile.getAbsolutePath();
		}

	}

	private static String prepareName(String name)
	{
		name = name.replace(':', '_');
		name = name.replace('/', '_');
		name = name.replace('\\', '_');
		name = name.replace('#', '_');
		return name;
	}

	public File getBaseDir()
	{
		return baseDir;
	}

	public File getIndexFile(SourceIdentifier sourceIdentifier)
	{
		String baseName = getBaseFileName(sourceIdentifier);
		return new File(baseName + FileConstants.INDEX_FILE_EXTENSION);
	}

	public File getDataFile(SourceIdentifier sourceIdentifier)
	{
		String baseName = getBaseFileName(sourceIdentifier);
		return new File(baseName + dataFileExtension);
	}

	public File getActiveFile(SourceIdentifier sourceIdentifier)
	{
		String baseName = getBaseFileName(sourceIdentifier);
		return new File(baseName + FileConstants.ACTIVE_FILE_EXTENSION);
	}

	public String getDataFileExtension()
	{
		return dataFileExtension;
	}

	public long getSizeOnDisk(SourceIdentifier sourceIdentifier)
	{
		File indexFile = getIndexFile(sourceIdentifier);
		File dataFile = getDataFile(sourceIdentifier);
		long indexSize = indexFile.length();
		long dataSize = dataFile.length();
		return indexSize + dataSize;
	}

	public long getNumberOfEvents(SourceIdentifier sourceIdentifier)
	{
		File indexFile = getIndexFile(sourceIdentifier);
		long indexSize = indexFile.length();
		return indexSize / 8;
	}
}
