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
package de.huxhorn.lilith.swing.callables;

import de.huxhorn.sulky.codec.filebuffer.CodecFileBuffer;
import de.huxhorn.sulky.codec.filebuffer.DefaultFileHeaderStrategy;
import de.huxhorn.sulky.codec.filebuffer.FileHeader;
import de.huxhorn.sulky.codec.filebuffer.FileHeaderStrategy;
import de.huxhorn.sulky.tasks.AbstractProgressingCallable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Should only be executed on inactive files.
 */
public class IndexingCallable
	extends AbstractProgressingCallable<Long>

{
	private final Logger logger = LoggerFactory.getLogger(IndexingCallable.class);

	private File dataFile;
	private File indexFile;

	public IndexingCallable(File dataFile, File indexFile)
	{
		this.dataFile = dataFile;
		this.indexFile = indexFile;
	}

	/**
	 * Computes a result, or throws an exception if unable to do so.
	 *
	 * @return computed result
	 * @throws Exception if unable to compute a result
	 */
	public Long call()
		throws Exception
	{
		if(!dataFile.exists())
		{
			throw new FileNotFoundException("File '" + dataFile.getAbsolutePath() + "' does not exist!");
		}
		if(!dataFile.isFile())
		{
			throw new FileNotFoundException("File '" + dataFile.getAbsolutePath() + "' is not a file!");
		}

		long fileSize = dataFile.length();
		setNumberOfSteps(fileSize);

		FileHeaderStrategy fhs = new DefaultFileHeaderStrategy();
		FileHeader fileHeader = fhs.readFileHeader(dataFile);
		if(fileHeader != null)
		{
			long offset = fileHeader.getDataOffset();

			RandomAccessFile dataRAFile = null;
			RandomAccessFile indexRAFile = null;
			Exception ex = null;
			long counter = 0;
			try
			{
				dataRAFile = new RandomAccessFile(dataFile, "r");
				indexRAFile = new RandomAccessFile(indexFile, "rw");
				indexRAFile.setLength(0);
				while(offset < fileSize)
				{
					dataRAFile.seek(offset);
					indexRAFile.writeLong(offset);
					counter++;
					int dataSize = dataRAFile.readInt();
					offset = offset + dataSize + CodecFileBuffer.DATA_LENGTH_SIZE;
					setCurrentStep(offset);
				}
			}
			catch(IOException e)
			{
				ex = e;
			}
			catch(InterruptedException e)
			{
				ex = e;
			}
			finally
			{
				closeQuietly(dataRAFile);
				closeQuietly(indexRAFile);
			}
			if(ex != null)
			{
				if(!indexFile.delete())
				{
					if(logger.isWarnEnabled())
					{
						logger.warn("Failed to delete index file '{}'!", indexFile.getAbsolutePath());
					}
				}
				throw ex; // rethrow
			}
			if(logger.isInfoEnabled()) logger.info("File '{}' has {} entries.", dataFile.getAbsolutePath(), counter);
			return counter;
		}
		else
		{
			throw new IllegalArgumentException("File '" + dataFile.getAbsolutePath() + "' is not a valid file!");
		}
	}

	private static void closeQuietly(RandomAccessFile file)
	{
		if(file != null)
		{
			try
			{
				file.close();
			}
			catch(IOException e)
			{
				// ignore
			}
		}
	}

}
