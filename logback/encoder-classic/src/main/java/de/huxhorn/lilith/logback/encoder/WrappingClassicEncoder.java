/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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
 * Copyright 2007-2017 Joern Huxhorn
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

package de.huxhorn.lilith.logback.encoder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import de.huxhorn.lilith.data.converter.Converter;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.logback.converter.SameThreadLogbackLoggingConverter;
import de.huxhorn.lilith.data.logging.protobuf.LoggingEventWrapperProtobufCodec;
import de.huxhorn.lilith.logback.encoder.core.ResettableEncoder;
import de.huxhorn.sulky.codec.Codec;
import java.util.concurrent.atomic.AtomicLong;

public class WrappingClassicEncoder
	implements ResettableEncoder<ILoggingEvent>
{
	private final Converter<de.huxhorn.lilith.data.logging.LoggingEvent> converter = new SameThreadLogbackLoggingConverter();
	private final Codec<EventWrapper<de.huxhorn.lilith.data.logging.LoggingEvent>> codec = new LoggingEventWrapperProtobufCodec(true);
	private final AtomicLong localId = new AtomicLong(0);

	@Override
	public void reset()
	{
		localId.set(0);
	}

	@Override
	public byte[] encode(ILoggingEvent event)
	{
		de.huxhorn.lilith.data.logging.LoggingEvent lilithEvent = converter.convert(event);
		EventWrapper<de.huxhorn.lilith.data.logging.LoggingEvent> wrapped=new EventWrapper<>();
		wrapped.setEvent(lilithEvent);
		long id = localId.incrementAndGet();
		wrapped.setLocalId(id);

		return codec.encode(wrapped);
	}
}
