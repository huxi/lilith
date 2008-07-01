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
package de.huxhorn.lilith.engine;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.sulky.buffers.FileBuffer;
import de.huxhorn.sulky.buffers.SerializingFileBuffer;

import java.io.File;
import java.io.Serializable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileBufferFactory<T extends Serializable>
{
	private final Logger logger = LoggerFactory.getLogger(FileBufferFactory.class);

	private LogFileFactory logFileFactory;

	public FileBufferFactory(LogFileFactory logFileFactory)
	{
		this.logFileFactory = logFileFactory;
	}

	public LogFileFactory getLogFileFactory()
	{
		return logFileFactory;
	}

	public FileBuffer<EventWrapper<T>> createBuffer(SourceIdentifier si)
	{
		//String baseName=getBaseFileName(baseDirectory, si);
		//File dataFile = new File(baseName+DATA_FILE_EXTENSION);
		//File indexFile = new File(baseName+INDEX_FILE_EXTENSION);
		File dataFile = logFileFactory.getDataFile(si);
		File indexFile = logFileFactory.getIndexFile(si);
		if(logger.isInfoEnabled()) logger.info("Creating buffer for dataFile '{}'.", dataFile.getAbsolutePath());

		return new SerializingFileBuffer<EventWrapper<T>>(dataFile, indexFile);
	}

	public FileBuffer<EventWrapper<T>> createActiveBuffer(SourceIdentifier si)
	{
		//String baseName=getBaseFileName(baseDirectory, si);
		FileBuffer<EventWrapper<T>> result = createBuffer(si);
		//File activeFile=new File(baseName+ACTIVE_FILE_EXTENSION);
		File activeFile=logFileFactory.getActiveFile(si);
		try
		{
			activeFile.createNewFile();
			activeFile.deleteOnExit();
		}
		catch (IOException e)
		{
			if(logger.isWarnEnabled()) logger.warn("Couldn't create active-file.");
		}
		return result;
	}



	public long getSizeOnDisk(SourceIdentifier sourceIdentifier)
	{
		File indexFile=logFileFactory.getIndexFile(sourceIdentifier);
		File dataFile=logFileFactory.getDataFile(sourceIdentifier);
		long indexSize=indexFile.length();
		long dataSize=dataFile.length();
		return indexSize+dataSize;
	}
}
