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
package de.huxhorn.lilith.tools.formatters;

import ch.qos.logback.access.spi.AccessContext;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AccessFormatter
		implements Formatter<EventWrapper<AccessEvent>>
{
	private final Logger logger = LoggerFactory.getLogger(AccessFormatter.class);

	private static final String DEFAULT_PATTERN="common";
	
	private ch.qos.logback.access.PatternLayout layout;
	private String pattern;

	public String getPattern()
	{
		return pattern;
	}

	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	public String format(EventWrapper<AccessEvent> wrapper)
	{
		initLayout();

		if(wrapper!=null)
		{
			AccessEvent event = wrapper.getEvent();
			if(event != null)
			{
				// TODO: implement! http://jira.qos.ch/browse/LBACCESS-12
				//ch.qos.logback.access.spi.AccessEvent foo=new ch.qos.logback.access.spi.AccessEvent(null, null, null);
				//
				//return layout.doLayout(foo);
				throw new UnsupportedOperationException("AccessEvent isn't supported yet!");
			}
		}
		return null;
	}

	private void initLayout()
	{
		if(layout == null)
		{
			layout=new ch.qos.logback.access.PatternLayout();
			Context context=new AccessContext();
			layout.setContext(context);
			if(pattern != null)
			{
				layout.setPattern(pattern);
			}
			else
			{
				layout.setPattern(DEFAULT_PATTERN);
			}
			layout.start();
			StatusManager statusManager = context.getStatusManager();
			if(statusManager.getLevel() == Status.ERROR)
			{
				List<Status> stati = statusManager.getCopyOfStatusList();
				String msg="Error while initializing layout! " + stati;
				if(logger.isErrorEnabled()) logger.error(msg);
				throw new IllegalStateException(msg);
			}
		}
	}
}
