/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright 2007-2011 Joern Huxhorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.huxhorn.lilith.log4j.producer;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.engine.impl.eventproducer.AbstractStreamEventProducer;
import de.huxhorn.sulky.buffers.AppendOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class Log4jLoggingStreamEventProducer
        extends AbstractStreamEventProducer<LoggingEvent>
{
	private final Logger logger = LoggerFactory.getLogger(Log4jLoggingStreamEventProducer.class);

	private Log4jLoggingAdapter adapter;

    public Log4jLoggingStreamEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<LoggingEvent>> eventQueue, InputStream inputStream)
            throws IOException {
        super(sourceIdentifier, eventQueue, inputStream);
        adapter = new Log4jLoggingAdapter();
    }

    protected LoggingEvent postprocessEvent(Object o) {
        if (o instanceof org.apache.log4j.spi.LoggingEvent) {
            org.apache.log4j.spi.LoggingEvent logbackEvent = (org.apache.log4j.spi.LoggingEvent) o;
			try
			{
            	return adapter.convert(logbackEvent);
			}
			catch(Throwable t)
			{
				if(logger.isErrorEnabled()) logger.error("Exception while converting event!", t);
			}
        }
        if (logger.isInfoEnabled()) {
            logger.info("Retrieved {} instead of org.apache.log4j.spi.LoggingEvent.", o == null ? null : o
                    .getClass().getName());
        }
        return null;
    }
}
