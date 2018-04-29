/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

import de.huxhorn.lilith.LilithSounds;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.EventHandler;
import de.huxhorn.sulky.sounds.Sounds;
import java.util.List;

public class AlarmSoundLoggingEventHandler
	implements EventHandler<LoggingEvent>
{
	private Sounds sounds;

	public void setSounds(Sounds sounds)
	{
		this.sounds = sounds;
	}

	@Override
	public void handle(List<EventWrapper<LoggingEvent>> events)
	{
		if(sounds == null)
		{
			return;
		}
		if(events == null || events.isEmpty())
		{
			return;
		}

		boolean errorDetected = false;
		boolean warnDetected = false;
		for(EventWrapper<LoggingEvent> current : events)
		{
			LoggingEvent event = current.getEvent();
			if(event == null)
			{
				continue;
			}
			if(LoggingEvent.Level.ERROR == event.getLevel())
			{
				errorDetected = true;
			}
			else if(LoggingEvent.Level.WARN == event.getLevel())
			{
				warnDetected = true;
			}
		}
		if(warnDetected)
		{
			sounds.play(LilithSounds.WARN_EVENT_ALARM);
		}
		if(errorDetected)
		{
			sounds.play(LilithSounds.ERROR_EVENT_ALARM);
		}
	}
}
