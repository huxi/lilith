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
package de.huxhorn.lilith.consumers;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.engine.EventConsumer;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.access.HttpStatus;

import java.util.List;

import org.simplericity.macify.eawt.Application;

public class UserNotificationAccessEventConsumer
	implements EventConsumer<AccessEvent>
{
	private Application application;

	public UserNotificationAccessEventConsumer(Application application)
	{
		this.application = application;
	}

	public void consume(List<EventWrapper<AccessEvent>> events)
	{
		if(application!=null)
		{
			boolean errorDetected=false;
			for(EventWrapper<AccessEvent> current:events)
			{
				AccessEvent event = current.getEvent();
				if(event!=null)
				{
					HttpStatus status=HttpStatus.getStatus(event.getStatusCode());
					if(status.getType() == HttpStatus.Type.SERVER_ERROR)
					{
						errorDetected=true;
						break;
					}
				}
			}
			if(errorDetected)
			{
				//application.requestUserAttention(Application.REQUEST_USER_ATTENTION_TYPE_INFORMATIONAL);
				application.requestUserAttention(Application.REQUEST_USER_ATTENTION_TYPE_CRITICAL);
			}
		}
	}
}
