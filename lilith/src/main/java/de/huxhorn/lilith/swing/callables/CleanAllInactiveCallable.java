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

import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.engine.LogFileFactory;
import de.huxhorn.lilith.swing.MainFrame;
import de.huxhorn.sulky.tasks.AbstractProgressingCallable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class CleanAllInactiveCallable
	extends AbstractProgressingCallable<Long>
{
	private final Logger logger = LoggerFactory.getLogger(CleanAllInactiveCallable.class);

	private MainFrame mainFrame;

	public CleanAllInactiveCallable(MainFrame mainFrame)
	{
		this.mainFrame = mainFrame;
	}

	public Long call()
		throws Exception
	{
		LogFileFactory accessFileFactory = mainFrame.getAccessFileFactory();
		LogFileFactory loggingFileFactory = mainFrame.getLoggingFileFactory();
		List<SourceIdentifier> inactiveAccess = mainFrame.collectInactiveLogs(accessFileFactory);
		List<SourceIdentifier> inactiveLogging = mainFrame.collectInactiveLogs(loggingFileFactory);
		setNumberOfSteps(inactiveAccess.size() + inactiveLogging.size());
		long currentStep = 0;
		for(SourceIdentifier si : inactiveAccess)
		{
			delete(accessFileFactory, si);
			currentStep++;
			setCurrentStep(currentStep);
		}
		for(SourceIdentifier si : inactiveLogging)
		{
			delete(loggingFileFactory, si);
			currentStep++;
			setCurrentStep(currentStep);
		}
		return currentStep;
	}

	private void delete(LogFileFactory fileFactory, SourceIdentifier si)
	{
		File dataFile = fileFactory.getDataFile(si);
		File indexFile = fileFactory.getIndexFile(si);
		if(dataFile.delete())
		{
			if(logger.isInfoEnabled()) logger.info("Deleted {}", dataFile);
		}
		if(indexFile.delete())
		{
			if(logger.isInfoEnabled()) logger.info("Deleted {}", indexFile);
		}
	}
}
