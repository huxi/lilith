/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2012 Joern Huxhorn
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
package de.huxhorn.lilith.engine.impl.eventproducer;

import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import java.util.Map;

public class LoggingEventSourceIdentifierUpdater
		implements SourceIdentifierUpdater<LoggingEvent>
{
	@Override
	public void updateIdentifier(SourceIdentifier baseIdentifier, LoggingEvent event)
	{
		if(baseIdentifier == null)
		{
			return;
		}

		if(event == null)
		{
			return;
		}

		LoggerContext context = event.getLoggerContext();
		if(context == null)
		{
			return;
		}

		Map<String, String> properties = context.getProperties();
		if(properties == null)
		{
			return;
		}

		String uuid = properties.get(LoggerContext.APPLICATION_UUID_PROPERTY_NAME);
		if(uuid == null)
		{
			return;
		}

		baseIdentifier.setSecondaryIdentifier(uuid);
	}
}
