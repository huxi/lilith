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
package de.huxhorn.lilith.eventhandlers;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.EventHandler;

import org.simplericity.macify.eawt.Application;

import java.util.List;

public class UserNotificationLoggingEventHandler
	implements EventHandler<LoggingEvent>
{
	private Application application;

	public UserNotificationLoggingEventHandler(Application application)
	{
		this.application = application;
	}

	public void handle(List<EventWrapper<LoggingEvent>> events)
	{
		if(application != null)
		{
			boolean errorDetected = false;
			for(EventWrapper<LoggingEvent> current : events)
			{
				LoggingEvent event = current.getEvent();
				if(event != null && LoggingEvent.Level.ERROR == event.getLevel())
				{
					errorDetected = true;
					break;
				}
			}
			if(errorDetected)
			{
				application.requestUserAttention(Application.REQUEST_USER_ATTENTION_TYPE_INFORMATIONAL);
			}
		}
	}
}
