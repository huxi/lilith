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

import de.huxhorn.sulky.tasks.AbstractProgressingCallable;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class CleanObsoleteCallable
	extends AbstractProgressingCallable<Long>

{
	private final Logger logger = LoggerFactory.getLogger(CleanObsoleteCallable.class);

	private File parentDirectory;

	public CleanObsoleteCallable(File parentDirectory)
	{
		this.parentDirectory = parentDirectory;
	}

	public Long call()
		throws Exception
	{
		Long result = 0L;

		if(parentDirectory.isDirectory())
		{
			setNumberOfSteps(1);
			result = 1L;
			FileUtils.deleteDirectory(parentDirectory);
			if(logger.isInfoEnabled())
			{
				boolean deleted = !parentDirectory.exists();
				if(deleted)
				{
					logger.info("Deleted obsolete directory '{}'.", parentDirectory);
				}
				else
				{
					if(logger.isWarnEnabled())
					{
						logger.warn("Failed to delete obsolete directory '{}'!", parentDirectory);
					}
				}
			}
			setCurrentStep(1);
		}

		return result;
	}
}
