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
package de.huxhorn.lilith.engine;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.sulky.buffers.ExtendedSerializingFileBuffer;
import de.huxhorn.sulky.buffers.FileBuffer;
import de.huxhorn.sulky.generics.io.Deserializer;
import de.huxhorn.sulky.generics.io.SerializableDeserializer;
import de.huxhorn.sulky.generics.io.SerializableSerializer;
import de.huxhorn.sulky.generics.io.Serializer;
import de.huxhorn.sulky.generics.io.XmlDeserializer;
import de.huxhorn.sulky.generics.io.XmlSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FileBufferFactory<T extends Serializable>
{
	private final Logger logger = LoggerFactory.getLogger(FileBufferFactory.class);

	private LogFileFactory logFileFactory;
	private Integer magicValue;
	private Map<String, String> metaData;

	public FileBufferFactory(LogFileFactory logFileFactory, Map<String, String> metaData)
	{
		this.logFileFactory = logFileFactory;
		this.magicValue = FileConstants.MAGIC_VALUE;
		if(metaData == null)
		{
			metaData = new HashMap<String, String>();
		}
		else
		{
			metaData = new HashMap<String, String>(metaData);
		}

		this.metaData = metaData;
	}

	public LogFileFactory getLogFileFactory()
	{
		return logFileFactory;
	}

	public FileBuffer<EventWrapper<T>> createBuffer(SourceIdentifier si)
	{
		File dataFile = logFileFactory.getDataFile(si);
		File indexFile = logFileFactory.getIndexFile(si);
		if(logger.isInfoEnabled()) logger.info("Creating buffer for dataFile '{}'.", dataFile.getAbsolutePath());

		Map<String, String> usedMetaData = new HashMap<String, String>(metaData);
		usedMetaData.put(FileConstants.IDENTIFIER_KEY, si.getIdentifier());
		if(si.getSecondaryIdentifier() != null)
		{
			usedMetaData.put(FileConstants.SECONDARY_IDENTIFIER_KEY, si.getSecondaryIdentifier());
		}


		ExtendedSerializingFileBuffer<EventWrapper<T>> result = new ExtendedSerializingFileBuffer<EventWrapper<T>>(magicValue, usedMetaData, null, null, dataFile, indexFile);

		Map<String, String> actualMetaData = result.getMetaData();

		boolean compressed = false;
		boolean useXml = false;
		boolean isLogging = true;

		if(actualMetaData != null)
		{
			compressed = Boolean.valueOf(actualMetaData.get(FileConstants.COMPRESSED_KEY));
			String format = actualMetaData.get(FileConstants.CONTENT_FORMAT_KEY);
			if(FileConstants.CONTENT_FORMAT_VALUE_XML.equals(format))
			{
				useXml = true;
			}
			String type = actualMetaData.get(FileConstants.CONTENT_TYPE_KEY);
			if(FileConstants.CONTENT_TYPE_VALUE_ACCESS.equals(type))
			{
				isLogging = false;
			}
		}
		Serializer<EventWrapper<T>> serializer;
		Deserializer<EventWrapper<T>> deserializer;
		if(useXml)
		{
			if(isLogging)
			{
				serializer = new XmlSerializer<EventWrapper<T>>(compressed, LoggingEvent.Level.class);
			}
			else
			{
				serializer = new XmlSerializer<EventWrapper<T>>(compressed);
			}
			deserializer = new XmlDeserializer<EventWrapper<T>>(compressed);
		}
		else
		{
			serializer = new SerializableSerializer<EventWrapper<T>>(compressed);
			deserializer = new SerializableDeserializer<EventWrapper<T>>(compressed);
		}
		result.setSerializer(serializer);
		result.setDeserializer(deserializer);

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

	public long getSizeOnDisk(SourceIdentifier sourceIdentifier)
	{
		File indexFile = logFileFactory.getIndexFile(sourceIdentifier);
		File dataFile = logFileFactory.getDataFile(sourceIdentifier);
		long indexSize = indexFile.length();
		long dataSize = dataFile.length();
		return indexSize + dataSize;
	}
}
