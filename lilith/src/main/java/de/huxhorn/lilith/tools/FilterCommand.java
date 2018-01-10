/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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
import de.huxhorn.lilith.conditions.GroovyCondition;
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.AccessFileBufferFactory;
import de.huxhorn.lilith.engine.LogFileFactory;
import de.huxhorn.lilith.engine.LoggingFileBufferFactory;
import de.huxhorn.lilith.engine.impl.LogFileFactoryImpl;
import de.huxhorn.lilith.swing.callables.IndexingCallable;
import de.huxhorn.lilith.tools.formatters.AccessFormatter;
import de.huxhorn.lilith.tools.formatters.Formatter;
import de.huxhorn.lilith.tools.formatters.LoggingFormatter;
import de.huxhorn.sulky.buffers.AppendOperation;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.FileBuffer;
import de.huxhorn.sulky.codec.filebuffer.DefaultFileHeaderStrategy;
import de.huxhorn.sulky.codec.filebuffer.FileHeader;
import de.huxhorn.sulky.codec.filebuffer.FileHeaderStrategy;
import de.huxhorn.sulky.codec.filebuffer.MetaData;
import de.huxhorn.sulky.codec.filebuffer.ReadOnlyExclusiveCodecFileBuffer;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FilterCommand
{
	static
	{
		new FilterCommand(); // stfu
	}

	private FilterCommand() {}

	public static boolean filterFile(File inputFile, File outputFile, File conditionFile, String searchString, String pattern, boolean overwrite, boolean keepRunning, boolean exclusive)
	{
		final Logger logger = LoggerFactory.getLogger(FilterCommand.class);

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
		if(exclusive)
		{
			keepRunning = false;
		}
		File inputIndexFile = FileHelper.resolveIndexFile(inputFile);
		//String inputIndexFileStr = inputIndexFile.getAbsolutePath();

		long inputDataModified = inputDataFile.lastModified();

		//File indexFile = new File(indexFileStr);
		if (!inputIndexFile.isFile())
		{
			// Index file does not exist.
			IndexCommand.indexLogFile(inputDataFile);
		}
		else
		{
			// Previous index file was found
			long inputIndexModified = inputIndexFile.lastModified();
			if (inputIndexModified < inputDataModified)
			{
				// Index file is outdated.
				IndexCommand.indexLogFile(inputDataFile);
			}
		}

		String scriptFileName = conditionFile.getAbsolutePath();
		GroovyCondition groovyCondition = new GroovyCondition(scriptFileName);
		if(searchString != null)
		{
			groovyCondition.setSearchString(searchString);
		}

		File outputDataFile = FileHelper.resolveDataFile(outputFile);
		String outputDataFileStr = outputDataFile.getAbsolutePath();
		File outputIndexFile = FileHelper.resolveIndexFile(outputFile);
		String outputIndexFileStr = outputIndexFile.getAbsolutePath();

		if(overwrite)
		{
			if(outputDataFile.delete())
			{
				if(logger.isDebugEnabled()) logger.debug("Deleted {}.", outputDataFileStr); // NOPMD
			}
			if(outputIndexFile.delete())
			{
				if(logger.isDebugEnabled()) logger.debug("Deleted {}.", outputIndexFileStr); // NOPMD
			}
		}

		if(outputDataFile.isFile())
		{
			long outputDataModified = outputDataFile.lastModified();
			if(!outputIndexFile.isFile())
			{
				// Index file does not exist.
				IndexCommand.indexLogFile(outputDataFile);
			}
			else
			{
				// Previous index file was found
				long outputIndexModified = outputIndexFile.lastModified();
				if (outputIndexModified < outputDataModified)
				{
					// Index file is outdated.
					IndexCommand.indexLogFile(outputDataFile);
				}
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
			if (metaData == null)
			{
				if (logger.isWarnEnabled()) logger.warn("Couldn't read meta data from '{}'!", inputDataFileStr);

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
				Buffer<EventWrapper<LoggingEvent>> inputBuffer;
				if(exclusive)
				{
					ReadOnlyExclusiveCodecFileBuffer<EventWrapper<LoggingEvent>> input = new ReadOnlyExclusiveCodecFileBuffer<>(inputDataFile, inputIndexFile);
					input.setCodec(fileBufferFactory.resolveCodec(metaData));
					inputBuffer = input;
				}
				else
				{
					inputBuffer = fileBufferFactory.createBuffer(inputDataFile, inputIndexFile, data);
				}
				FileBuffer<EventWrapper<LoggingEvent>> outputBuffer = fileBufferFactory.createBuffer(outputDataFile, outputIndexFile, data);

				LoggingFormatter formatter = null;
				if(pattern != null)
				{
					formatter = new LoggingFormatter();
					formatter.setPattern(pattern);
				}

				long firstUnfiltered=filterContent(inputBuffer, outputBuffer, groovyCondition, formatter);
				if(keepRunning)
				{
					pollFile(inputBuffer, outputBuffer, groovyCondition, formatter, inputDataFile, inputIndexFile, firstUnfiltered);
				}
				return true;
			}
			else if (FileConstants.CONTENT_TYPE_VALUE_ACCESS.equals(contentType))
			{
				Map<String, String> accessMetaData = new HashMap<>();
				accessMetaData.put(FileConstants.CONTENT_TYPE_KEY, FileConstants.CONTENT_TYPE_VALUE_ACCESS);
				accessMetaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF);
				accessMetaData.put(FileConstants.COMPRESSION_KEY, FileConstants.COMPRESSION_VALUE_GZIP);

				AccessFileBufferFactory fileBufferFactory = new AccessFileBufferFactory(logFileFactory, accessMetaData);
				Buffer<EventWrapper<AccessEvent>> inputBuffer;
				if(exclusive)
				{
					ReadOnlyExclusiveCodecFileBuffer<EventWrapper<AccessEvent>> input = new ReadOnlyExclusiveCodecFileBuffer<>(inputDataFile, inputIndexFile);
					input.setCodec(fileBufferFactory.resolveCodec(metaData));
					inputBuffer = input;
				}
				else
				{
					inputBuffer = fileBufferFactory.createBuffer(inputDataFile, inputIndexFile, data);
				}
				FileBuffer<EventWrapper<AccessEvent>> outputBuffer = fileBufferFactory.createBuffer(outputDataFile, outputIndexFile, data);

				AccessFormatter formatter = null;
				if(pattern != null)
				{
					formatter = new AccessFormatter();
					formatter.setPattern(pattern);
				}

				long firstUnfiltered=filterContent(inputBuffer, outputBuffer, groovyCondition, formatter);
				if(keepRunning)
				{
					pollFile(inputBuffer, outputBuffer, groovyCondition, formatter, inputDataFile, inputIndexFile, firstUnfiltered);
				}
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
				logger.warn("Exception while reading from file '" + inputDataFileStr + "'!", ex);
			}
		}
		return false;
	}

	@SuppressWarnings("PMD.SystemPrintln")
	private static <T extends Serializable> long filterContent(Buffer<EventWrapper<T>> inputBuffer, AppendOperation<EventWrapper<T>> outputBuffer, GroovyCondition groovyCondition, Formatter<EventWrapper<T>>  formatter)
	{
		long bufferSize=inputBuffer.getSize();
		long i;
		for (i = 0; i < bufferSize; i++)
		{
			EventWrapper<T> current = inputBuffer.get(i);
			if (current != null && groovyCondition.isTrue(current))
			{
				if(formatter != null)
				{
					String msg = formatter.format(current);
					if (msg != null)
					{
						System.out.print(msg);
						System.out.flush();
					}
				}
				outputBuffer.add(current);
			}
		}
		return i;
	}

	@SuppressWarnings("PMD.SystemPrintln")
	private static <T extends Serializable> void pollFile(Buffer<EventWrapper<T>> inputBuffer, AppendOperation<EventWrapper<T>> outputBuffer, GroovyCondition groovyCondition, Formatter<EventWrapper<T>>  formatter, File inputDataFile, File inputIndexFile, long index)
	{
		final Logger logger = LoggerFactory.getLogger(FilterCommand.class);

		for(;;)
		{
			long dataModified = inputDataFile.lastModified();
			long indexModified = inputIndexFile.lastModified();
			if (indexModified < dataModified)
			{
				// Index file is outdated.
				IndexingCallable callable=new IndexingCallable(inputDataFile, inputIndexFile, true); // NOPMD - AvoidInstantiatingObjectsInLoops
				try
				{
					callable.call();
				}
				catch(Exception e)
				{
					if(logger.isWarnEnabled()) logger.warn("Exception while reindexing!", e);
					break;
				}
				for(;index < inputBuffer.getSize();index++)
				{
					EventWrapper<T> current = inputBuffer.get(index);
					if (current != null && groovyCondition.isTrue(current))
					{
						if(formatter != null)
						{
							String msg = formatter.format(current);
							if (msg != null)
							{
								System.out.print(msg);
								System.out.flush();
							}
						}
						outputBuffer.add(current);
					}
				}
			}
			try
			{
				Thread.sleep(5000);
			}
			catch(InterruptedException e)
			{
				if(logger.isInfoEnabled()) logger.info("Interrupted...");
				break;
			}
		}
	}
}
