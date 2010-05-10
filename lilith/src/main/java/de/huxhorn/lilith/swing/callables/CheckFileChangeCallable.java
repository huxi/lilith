/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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

import de.huxhorn.sulky.tasks.AbstractProgressingCallable;

import java.io.File;

public class CheckFileChangeCallable
	extends AbstractProgressingCallable<Long>
{
	private File dataFile;
	private File indexFile;
	private static final int POLL_INTERVAL = 5000;

	public CheckFileChangeCallable(File dataFile, File indexFile)
	{
		this.dataFile = dataFile;
		this.indexFile = indexFile;
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
				IndexingCallable indexing=new IndexingCallable(dataFile, indexFile, true);
				indexing.call();
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
}
