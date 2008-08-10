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
package de.huxhorn.lilith.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class LogAnonymousRunnable
	extends AbstractDebugRunnable
{
	private final Logger logger = LoggerFactory.getLogger(LogStuffRunnable.class);

	public LogAnonymousRunnable(int delay)
	{
		super(delay);
	}

	public interface StuffLogger
	{
		void logStuff();
	}

	public void runIt()
	{
		class MethodInternal
			implements StuffLogger
		{
			public void logStuff()
			{
				if(logger.isErrorEnabled()) logger.error("MethodInternalClass");

			}
		}

		StuffLogger stuffLogger=new StuffLogger()
		{
			class AnonymousInternalClass
				implements StuffLogger
			{
				public void logStuff()
				{
					if(logger.isErrorEnabled()) logger.error("AnonymousInternalClass");

				}
			}

			StuffLogger stuffLogger=new StuffLogger()
			{
				public void logStuff()
				{
					if(logger.isWarnEnabled()) logger.warn("SecondLevelAnonymous");
				}
			};

			public void logStuff()
			{
				if(logger.isInfoEnabled()) logger.info("FirstLevelAnonymous");
				stuffLogger.logStuff();
				new AnonymousInternalClass().logStuff();
				new MethodInternal().logStuff();
			}
		};
		stuffLogger.logStuff();
	}
}