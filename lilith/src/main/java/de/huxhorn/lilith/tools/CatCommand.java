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
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CatCommand
{
	static
	{
		new CatCommand(); // stfu
	}

	private CatCommand() {}

	public static boolean catFile(File inputFile, String pattern, int amount)
	{
		final Logger logger = LoggerFactory.getLogger(CatCommand.class);

		File inputDataFile = FileHelper.resolveDataFile(inputFile);
		String inputDataFileStr = inputDataFile.getAbsolutePath();

		if (!inputDataFile.isFile())
		{
			if (logger.isErrorEnabled()) logger.error("'{}' is not a file!", inputDataFileStr);
			return false;
		}
		if (!inputDataFile.canRead())
		{
			if (logger.isErrorEnabled()) logger.error("Can't read '{}'!", inputDataFileStr);
			return false;
		}

		File inputIndexFile = FileHelper.resolveIndexFile(inputFile);

		//String inputIndexFileStr = inputIndexFile.getAbsolutePath();

		long dataModified = inputDataFile.lastModified();

		if (!inputIndexFile.isFile())
		{
			// Index file does not exist.
			IndexCommand.indexLogFile(inputDataFile);
		}
		else
		{
			// Previous index file was found
			long indexModified = inputIndexFile.lastModified();
			if (indexModified < dataModified)
			{
				// Index file is outdated.
				IndexCommand.indexLogFile(inputDataFile);
			}
		}

		FileHeaderStrategy fileHeaderStrategy = new DefaultFileHeaderStrategy();
		try
		{
			FileHeader header = fileHeaderStrategy.readFileHeader(inputDataFile);
			if (header == null)
			{
				if (logger.isWarnEnabled())
				{
					logger.warn("Couldn't read file header from '{}'!", inputDataFileStr);
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
					logger.warn("Couldn't read meta data from '{}'!", inputDataFileStr);
				}
				return false;
			}
			Map<String, String> data = metaData.getData();
			String contentType = data.get(FileConstants.CONTENT_TYPE_KEY);

			LogFileFactory logFileFactory = new LogFileFactoryImpl(new File("."));

			if (FileConstants.CONTENT_TYPE_VALUE_LOGGING.equals(contentType))
			{
				Map<String, String> loggingMetaData = new HashMap<>();
				loggingMetaData.put(FileConstants.CONTENT_TYPE_KEY, FileConstants.CONTENT_TYPE_VALUE_LOGGING);
				loggingMetaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF);
				loggingMetaData.put(FileConstants.COMPRESSION_KEY, FileConstants.COMPRESSION_VALUE_GZIP);

				LoggingFileBufferFactory fileBufferFactory = new LoggingFileBufferFactory(logFileFactory, loggingMetaData);
				FileBuffer<EventWrapper<LoggingEvent>> inputBuffer = fileBufferFactory.createBuffer(inputDataFile, inputIndexFile, data);
				LoggingFormatter formatter = new LoggingFormatter();
				formatter.setPattern(pattern);

				printContent(inputBuffer, formatter, amount);
				return true;
			}
			else if (FileConstants.CONTENT_TYPE_VALUE_ACCESS.equals(contentType))
			{
				Map<String, String> accessMetaData = new HashMap<>();
				accessMetaData.put(FileConstants.CONTENT_TYPE_KEY, FileConstants.CONTENT_TYPE_VALUE_ACCESS);
				accessMetaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF);
				accessMetaData.put(FileConstants.COMPRESSION_KEY, FileConstants.COMPRESSION_VALUE_GZIP);

				AccessFileBufferFactory fileBufferFactory = new AccessFileBufferFactory(logFileFactory, accessMetaData);
				FileBuffer<EventWrapper<AccessEvent>> buffer = fileBufferFactory.createBuffer(inputDataFile, inputIndexFile, data);
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
				logger.warn("Exception while reading from file '{}'!", inputDataFileStr, ex);
			}
		}
		return false;
	}

	@SuppressWarnings("PMD.SystemPrintln")
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
