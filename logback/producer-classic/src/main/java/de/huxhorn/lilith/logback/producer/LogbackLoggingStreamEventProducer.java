/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
 * Copyright 2007-2010 Joern Huxhorn
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

package de.huxhorn.lilith.logback.producer;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.logback.LogbackLoggingAdapter;
import de.huxhorn.lilith.engine.impl.eventproducer.AbstractStreamEventProducer;
import de.huxhorn.sulky.buffers.AppendOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class LogbackLoggingStreamEventProducer
        extends AbstractStreamEventProducer<LoggingEvent>
{
	private final Logger logger = LoggerFactory.getLogger(LogbackLoggingStreamEventProducer.class);

	private LogbackLoggingAdapter adapter;

    public LogbackLoggingStreamEventProducer(SourceIdentifier sourceIdentifier, AppendOperation<EventWrapper<LoggingEvent>> eventQueue, InputStream inputStream)
            throws IOException {
        super(sourceIdentifier, eventQueue, inputStream);
        adapter = new LogbackLoggingAdapter();
    }

    protected LoggingEvent postprocessEvent(Object o) {
        if (o instanceof ch.qos.logback.classic.spi.ILoggingEvent) {
            ch.qos.logback.classic.spi.ILoggingEvent logbackEvent = (ch.qos.logback.classic.spi.ILoggingEvent) o;
            return adapter.convert(logbackEvent, false);
        }
        if (logger.isInfoEnabled()) {
            logger.info("Retrieved {} instead of ch.qos.logback.classic.spi.ILoggingEvent.", o == null ? null : o
                    .getClass().getName());
        }
        return null;
    }
}
