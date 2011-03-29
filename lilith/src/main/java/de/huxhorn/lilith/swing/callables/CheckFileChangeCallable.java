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
package de.huxhorn.lilith.swing.callables;

import de.huxhorn.lilith.swing.ViewContainer;
import de.huxhorn.sulky.tasks.AbstractProgressingCallable;

import javax.swing.SwingUtilities;
import java.io.EOFException;
import java.io.File;

public class CheckFileChangeCallable
	extends AbstractProgressingCallable<Long>
{
	private File dataFile;
	private File indexFile;
	private static final int POLL_INTERVAL = 1000;
	private ViewContainer<?> viewContainer;
	private FlushRunnable flushRunnable;

	public CheckFileChangeCallable(File dataFile, File indexFile, ViewContainer<?> viewContainer)
	{
		this.dataFile = dataFile;
		this.indexFile = indexFile;
		this.viewContainer = viewContainer;
		this.flushRunnable = new FlushRunnable();
	}


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
					IndexingCallable indexing=new IndexingCallable(dataFile, indexFile, true);
					indexing.call();
					SwingUtilities.invokeAndWait(flushRunnable);
				}
				catch(EOFException ex)
				{
					// this happens for example if the header hasn't been fully written, yet.
					// this can be safely ignored.
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

		public void run()
		{
			viewContainer.flush();
		}
	}
}
