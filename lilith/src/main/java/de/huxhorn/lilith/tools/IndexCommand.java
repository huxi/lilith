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

import de.huxhorn.lilith.swing.callables.IndexingCallable;
import de.huxhorn.sulky.tasks.ProgressingCallable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexCommand
{
	static
	{
		new IndexCommand(); // stfu
	}

	private IndexCommand() {}

	public static boolean indexLogFile(String inputFileString)
	{
		return indexLogFile(new File(inputFileString));
	}

	static boolean indexLogFile(File inputFile)
	{
		final Logger logger = LoggerFactory.getLogger(IndexCommand.class);

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

		IndexingCallable callable = new IndexingCallable(inputDataFile, inputIndexFile);
		callable.addPropertyChangeListener(new IndexingChangeListener());
		try
		{
			long count = callable.call();
			if(logger.isInfoEnabled()) logger.info("Finished indexing {}. Number of events: {}", inputDataFileStr, count);
			return true;
		}
		catch(Exception e)
		{
			if(logger.isErrorEnabled()) logger.error("Exception while indexing '{}'!", inputDataFileStr, e);
		}
		return false;
	}

	private static class IndexingChangeListener
		implements PropertyChangeListener
	{
		private final Logger logger = LoggerFactory.getLogger(IndexingChangeListener.class);

		/**
		 * This method gets called when a bound property is changed.
		 *
		 * @param evt A PropertyChangeEvent object describing the event source
		 *            and the property that has changed.
		 */

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			if(ProgressingCallable.PROGRESS_PROPERTY_NAME.equals(evt.getPropertyName()))
			{
				if(logger.isInfoEnabled()) logger.info("Progress: {}%", evt.getNewValue()); // NOPMD
			}
		}
	}
}
