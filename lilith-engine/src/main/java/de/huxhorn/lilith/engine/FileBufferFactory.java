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

package de.huxhorn.lilith.engine;

import de.huxhorn.lilith.api.FileConstants;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.sulky.buffers.FileBuffer;
import de.huxhorn.sulky.codec.Codec;
import de.huxhorn.sulky.codec.filebuffer.CodecFileBuffer;
import de.huxhorn.sulky.codec.filebuffer.FileHeader;
import de.huxhorn.sulky.codec.filebuffer.MetaData;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileBufferFactory<T extends Serializable>
{
	private final Logger logger = LoggerFactory.getLogger(FileBufferFactory.class);

	private final LogFileFactory logFileFactory;
	private final int magicValue;
	private final Map<String, String> metaData;

	public FileBufferFactory(LogFileFactory logFileFactory, Map<String, String> metaData)
	{
		this.logFileFactory = Objects.requireNonNull(logFileFactory, "logFileFactory must not be null!");
		this.magicValue = FileConstants.MAGIC_VALUE;
		if(metaData == null)
		{
			metaData = new HashMap<>();
		}
		else
		{
			metaData = new HashMap<>(metaData);
		}

		this.metaData = metaData;
	}

	public LogFileFactory getLogFileFactory()
	{
		return logFileFactory;
	}

	public abstract Codec<EventWrapper<T>> resolveCodec(MetaData metaData);

	public FileBuffer<EventWrapper<T>> createBuffer(SourceIdentifier si)
	{
		File dataFile = logFileFactory.getDataFile(si);
		File indexFile = logFileFactory.getIndexFile(si);

		Map<String, String> usedMetaData = new HashMap<>(metaData);
		usedMetaData.put(FileConstants.IDENTIFIER_KEY, si.getIdentifier());
		if(si.getSecondaryIdentifier() != null)
		{
			usedMetaData.put(FileConstants.SECONDARY_IDENTIFIER_KEY, si.getSecondaryIdentifier());
		}

		return createBuffer(dataFile, indexFile, usedMetaData);
	}

	public FileBuffer<EventWrapper<T>> createBuffer(File dataFile, File indexFile, Map<String, String> usedMetaData)
	{
		if(logger.isInfoEnabled()) logger.info("Creating buffer for dataFile '{}'.", dataFile.getAbsolutePath());

		CodecFileBuffer<EventWrapper<T>> result = new CodecFileBuffer<>(magicValue, false, usedMetaData, null, dataFile, indexFile);

		FileHeader fileHeader = result.getFileHeader();
		MetaData actualMetaData = fileHeader.getMetaData();

		result.setCodec(resolveCodec(actualMetaData));
		if(logger.isDebugEnabled()) logger.debug("Created file buffer: {}", result);

		return result;
	}

	public FileBuffer<EventWrapper<T>> createActiveBuffer(SourceIdentifier si)
	{
		FileBuffer<EventWrapper<T>> result = createBuffer(si);
		File activeFile = logFileFactory.getActiveFile(si);
		try
		{
			activeFile.createNewFile();
			activeFile.deleteOnExit();
		}
		catch(IOException e)
		{
			if(logger.isWarnEnabled()) logger.warn("Couldn't create active-file.");
		}
		return result;
	}
}
