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
package de.huxhorn.lilith.swing.callables;

import de.huxhorn.sulky.swing.AbstractProgressingCallable;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.TreeSet;
import java.util.Set;

import de.huxhorn.lilith.data.logging.LoggingEvent;

public class IndexingCallable
		extends AbstractProgressingCallable<Integer>

{
	private final Logger logger = LoggerFactory.getLogger(IndexingCallable.class);
	private File logFile;
	private File indexFile;
	private Set<String> threadNames;
	private Set<String> loggerNames;

	public IndexingCallable(File logFile, File indexFile)
	{
		this.logFile=logFile;
		this.indexFile=indexFile;
		threadNames=new TreeSet<String>();
		loggerNames=new TreeSet<String>();
	}

	/**
	 * Computes a result, or throws an exception if unable to do so.
	 *
	 * @return computed result
	 * @throws Exception if unable to compute a result
	 */
	public Integer call() throws Exception
	{
		// ATTENTION! This method must be changed if SerializingFileBuffer implementation is changed!
		if(!logFile.exists())
		{
			throw new FileNotFoundException("File '"+logFile.getAbsolutePath()+"' does not exist!");
		}
		if(!logFile.isFile())
		{
			throw new FileNotFoundException("File '"+logFile.getAbsolutePath()+"' is not a file!");
		}

		long fileSize=logFile.length();
		setNumberOfSteps(fileSize);

		int counter=0;
		DataOutputStream dataOutputStream=null;
		BufferedInputStream bis=null;
		try
		{
			FileOutputStream fos=new FileOutputStream(indexFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			dataOutputStream = new DataOutputStream(bos);

			FileInputStream fis=new FileInputStream(logFile);
			bis=new BufferedInputStream(fis);
			CountingInputStream byteCounter =new CountingInputStream(bis);
			DataInputStream dis=new DataInputStream(byteCounter);
			long previousByteCount=0;
			for(;;)
			{
				Throwable error=null;
				try
				{
					int bufferSize=dis.readInt();
					byte[] buffer=new byte[bufferSize];
					dis.readFully(buffer);
					ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
					GZIPInputStream gis=new GZIPInputStream(bais);
					ObjectInputStream ois=new ObjectInputStream(gis);
					Object o = ois.readObject();
					performAdditionalWork(o);
					dataOutputStream.writeLong(previousByteCount);
					long byteCount=byteCounter.getByteCount();
					counter++;
					if(logger.isDebugEnabled()) logger.debug("counter={}, byteCounter={}, previousByteCount={}",
							new Object[]{counter, byteCount, previousByteCount});
					previousByteCount=byteCount;
					setCurrentStep(byteCount);
					if(byteCount == fileSize)
					{
						finish();
						break;
					}
				}
				catch (IOException e)
				{
					error=e;
				}
				catch (ClassNotFoundException e)
				{
					error=e;
				}
				if(error!=null)
				{
					if(logger.isWarnEnabled()) logger.warn("Error while indexing...", error);
					break;
				}
			}
		}
		finally
		{
			IOUtils.closeQuietly(dataOutputStream);
			IOUtils.closeQuietly(bis);
		}
		return counter;
	}

	private void finish()
	{
		if(logger.isDebugEnabled())
		{
			StringBuffer msg=new StringBuffer();
			msg.append("threadNames:\n");
			for(String name: threadNames)
			{
				msg.append("\t- ").append(name).append("\n");
			}
			msg.append("\n");
			msg.append("loggerNames:\n");
			for(String name: loggerNames)
			{
				msg.append("\t- ").append(name).append("\n");
			}
			logger.debug(msg.toString());
		}


	}

	private void performAdditionalWork(Object o)
	{
		if(o instanceof EventWrapper)
		{
			EventWrapper wrapper=(EventWrapper) o;
			Object eventObj = wrapper.getEvent();
			if (eventObj instanceof LoggingEvent)
			{
				LoggingEvent event=(LoggingEvent) eventObj;
				String loggerName=event.getLogger();
				if(!loggerNames.contains(loggerName))
				{
					loggerNames.add(loggerName);
				}
				String threadName=event.getThreadName();
				if(!threadNames.contains(threadName))
				{
					threadNames.add(threadName);
				}
			}
		}
	}
}
