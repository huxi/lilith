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

package de.huxhorn.lilith.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDebugRunnable
	implements Runnable
{
	private final Logger logger = LoggerFactory.getLogger(AbstractDebugRunnable.class);

	private final int delay;

	public AbstractDebugRunnable(int delay)
	{
		this.delay = delay;
	}

	public void sleep()
		throws InterruptedException
	{
		if(delay > 0)
		{
			Thread.sleep(delay);
		}
	}

	@Override
	public final void run()
	{
		try
		{
			runIt();
		}
		catch(InterruptedException e)
		{
			if(logger.isInfoEnabled()) logger.info("Execution of DebugRunnable was interrupted!");
		}
	}

	public abstract void runIt()
		throws InterruptedException;
}
