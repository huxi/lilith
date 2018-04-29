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

package de.huxhorn.lilith.swing.callables;

import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.sulky.tasks.AbstractProgressingCallable;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckFileChangeCallable
	extends AbstractProgressingCallable<Long>
{
	private final Logger logger = LoggerFactory.getLogger(CheckFileChangeCallable.class);

	private static final int POLL_INTERVAL = 1000;

	private final File dataFile;
	private final File indexFile;
	private final ViewContainer<?> viewContainer;
	private final FlushRunnable flushRunnable;

	public CheckFileChangeCallable(File dataFile, File indexFile, ViewContainer<?> viewContainer)
	{
		this.dataFile = dataFile;
		this.indexFile = indexFile;
		this.viewContainer = viewContainer;
		this.flushRunnable = new FlushRunnable();
	}


	@Override
	public Long call() throws Exception
	{
		setNumberOfSteps(-1);
		for(;;)
		{
			long dataModified=dataFile.lastModified();
			long indexModified=indexFile.lastModified();

			if(dataModified > indexModified)
			{
				try
				{
					IndexingCallable indexing=new IndexingCallable(dataFile, indexFile, true); // NOPMD - AvoidInstantiatingObjectsInLoops
					indexing.call();
					EventQueue.invokeAndWait(flushRunnable);
				}
				catch(IOException ex)
				{
					// this happens for example if the header hasn't been fully written, yet.
					// this can be safely ignored.
					// changed from EOFException to general IOException due to
					// http://sourceforge.net/apps/trac/lilith/ticket/97
					if(logger.isInfoEnabled()) logger.info("Exception while re-indexing log file. Ignoring it...", ex);
				}
			}
			try
			{
				Thread.sleep(POLL_INTERVAL);
			}
			catch(InterruptedException ex)
			{
				break;
			}
		}
		return 0L;
	}

	@Override
	public String toString()
	{
		return dataFile.getAbsolutePath();
	}

	private class FlushRunnable
		implements Runnable
	{

		@Override
		public void run()
		{
			viewContainer.flush();
		}
	}
}
