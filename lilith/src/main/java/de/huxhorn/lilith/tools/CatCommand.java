/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.AccessFileBufferFactory;
import de.huxhorn.lilith.engine.LogFileFactory;
import de.huxhorn.lilith.engine.LoggingFileBufferFactory;
import de.huxhorn.lilith.engine.impl.LogFileFactoryImpl;
import de.huxhorn.lilith.tools.formatters.AccessFormatter;
import de.huxhorn.lilith.tools.formatters.Formatter;
import de.huxhorn.lilith.tools.formatters.LoggingFormatter;
import de.huxhorn.sulky.buffers.FileBuffer;
import de.huxhorn.sulky.codec.filebuffer.DefaultFileHeaderStrategy;
import de.huxhorn.sulky.codec.filebuffer.FileHeader;
import de.huxhorn.sulky.codec.filebuffer.FileHeaderStrategy;
import de.huxhorn.sulky.codec.filebuffer.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CatCommand
{
	public static boolean catFile(File dataFile, String pattern, int amount)
	{
		final Logger logger = LoggerFactory.getLogger(CatCommand.class);

		String logFileStr = dataFile.getAbsolutePath();
		if (!dataFile.isFile())
		{
			if (logger.isErrorEnabled()) logger.error("'" + logFileStr + "' is not a file!");
			return false;
		}
		if (!dataFile.canRead())
		{
			if (logger.isErrorEnabled()) logger.error("Can't read '" + logFileStr + "'!");
			return false;
		}

		String indexFileStr;
		if (logFileStr.toLowerCase().endsWith(FileConstants.FILE_EXTENSION))
		{
			indexFileStr = logFileStr.substring(0, logFileStr.length() - FileConstants.FILE_EXTENSION.length());
		}
		else
		{
			indexFileStr = logFileStr;
		}
		indexFileStr = indexFileStr + FileConstants.INDEX_FILE_EXTENSION;

		long dataModified = dataFile.lastModified();

		File indexFile = new File(indexFileStr);
		if (!indexFile.isFile())
		{
			// Index file does not exist.
			IndexCommand.indexLogFile(logFileStr, indexFileStr);
		}
		else
		{
			// Previous index file was found
			long indexModified = indexFile.lastModified();
			if (indexModified < dataModified)
			{
				// Index file is outdated.
				IndexCommand.indexLogFile(logFileStr, indexFileStr);
			}
		}

		boolean isAccess = false;
		FileHeaderStrategy fileHeaderStrategy = new DefaultFileHeaderStrategy();
		try
		{
			FileHeader header = fileHeaderStrategy.readFileHeader(dataFile);
			if (header == null)
			{
				if (logger.isWarnEnabled())
				{
					logger.warn("Couldn't read file header from '{}'!", dataFile.getAbsolutePath());
				}
				return false;
			}
			if (header.getMagicValue() != FileConstants.MAGIC_VALUE)
			{
				if (logger.isWarnEnabled())
				{
					logger.warn("Invalid magic value! ", Integer.toHexString(header.getMagicValue()));
				}
				return false;
			}
			MetaData metaData = header.getMetaData();
			if (metaData == null || metaData.getData() == null)
			{
				if (logger.isWarnEnabled())
				{
					logger.warn("Couldn't read meta data from '{}'!", dataFile.getAbsolutePath());
				}
				return false;
			}
			Map<String, String> data = metaData.getData();
			String contentType = data.get(FileConstants.CONTENT_TYPE_KEY);

			LogFileFactory logFileFactory = new LogFileFactoryImpl(new File("."));


			if (FileConstants.CONTENT_TYPE_VALUE_LOGGING.equals(contentType))
			{
				isAccess = false;
				Map<String, String> loggingMetaData = new HashMap<String, String>();
				loggingMetaData.put(FileConstants.CONTENT_TYPE_KEY, FileConstants.CONTENT_TYPE_VALUE_LOGGING);
				loggingMetaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF);
				loggingMetaData.put(FileConstants.COMPRESSION_KEY, FileConstants.COMPRESSION_VALUE_GZIP);
				// TODO: configurable format and compressed

				LoggingFileBufferFactory fileBufferFactory = new LoggingFileBufferFactory(logFileFactory, loggingMetaData);
				FileBuffer<EventWrapper<LoggingEvent>> buffer = fileBufferFactory.createBuffer(dataFile, indexFile, data);
				LoggingFormatter formatter = new LoggingFormatter();
				formatter.setPattern(pattern);

				printContent(buffer, formatter, amount);
				return true;
			}
			else if (FileConstants.CONTENT_TYPE_VALUE_ACCESS.equals(contentType))
			{
				isAccess = true;
				Map<String, String> accessMetaData = new HashMap<String, String>();
				accessMetaData.put(FileConstants.CONTENT_TYPE_KEY, FileConstants.CONTENT_TYPE_VALUE_ACCESS);
				accessMetaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF);
				accessMetaData.put(FileConstants.COMPRESSION_KEY, FileConstants.COMPRESSION_VALUE_GZIP);
				// TODO: configurable format and compressed

				AccessFileBufferFactory fileBufferFactory = new AccessFileBufferFactory(logFileFactory, accessMetaData);
				FileBuffer<EventWrapper<AccessEvent>> buffer = fileBufferFactory.createBuffer(dataFile, indexFile, data);
				AccessFormatter formatter = new AccessFormatter();
				formatter.setPattern(pattern);

				printContent(buffer, formatter, amount);
				return true;
			}
			else
			{
				if (logger.isWarnEnabled()) logger.warn("Unexpected content type {}.", contentType);
				return false;
			}
		}
		catch (IOException ex)
		{
			if (logger.isWarnEnabled())
			{
				logger.warn("Exception while reading from file '" + dataFile.getAbsolutePath() + "'!", ex);
			}
		}
		return false;
	}

	private static <T extends Serializable> void printContent(FileBuffer<EventWrapper<T>> buffer, Formatter<EventWrapper<T>> formatter, long amount)
	{
		long bufferSize=buffer.getSize();
		if(amount < 1 || amount > bufferSize)
		{
			amount = bufferSize;
		}
		for (long i = 0; i < amount; i++)
		{
			EventWrapper<T> current = buffer.get(i);
			if (current != null)
			{
				String msg = formatter.format(current);
				if (msg != null)
				{
					System.out.print(msg);
					System.out.flush();
				}
			}
		}
	}
}
