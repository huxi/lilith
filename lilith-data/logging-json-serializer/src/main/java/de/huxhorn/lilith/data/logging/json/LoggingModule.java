/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2015 Joern Huxhorn
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
 * Copyright 2007-2015 Joern Huxhorn
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

package de.huxhorn.lilith.data.logging.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.lilith.data.logging.json.mixin.ExtendedStackTraceElementMixIn;
import de.huxhorn.lilith.data.logging.json.mixin.LoggerContextMixIn;
import de.huxhorn.lilith.data.logging.json.mixin.LoggingEventMixIn;
import de.huxhorn.lilith.data.logging.json.mixin.MarkerMixIn;
import de.huxhorn.lilith.data.logging.json.mixin.MessageMixIn;
import de.huxhorn.lilith.data.logging.json.mixin.ThreadInfoMixIn;
import de.huxhorn.lilith.data.logging.json.mixin.ThrowableInfoMixIn;

public class LoggingModule
	extends SimpleModule
{
	private static final long serialVersionUID = 1811966736888686433L;

	public LoggingModule()
	{
		super("LilithLogging", new Version(1, 0, 0, null, "de.huxhorn.lilith", "de.huxhorn.lilith.data.logging-json-serializer"));
		setMixInAnnotation(Message.class, MessageMixIn.class);
		setMixInAnnotation(ExtendedStackTraceElement.class, ExtendedStackTraceElementMixIn.class);
		setMixInAnnotation(LoggerContext.class, LoggerContextMixIn.class);
		setMixInAnnotation(Marker.class, MarkerMixIn.class);
		setMixInAnnotation(ThreadInfo.class, ThreadInfoMixIn.class);
		setMixInAnnotation(LoggingEvent.class, LoggingEventMixIn.class);
		setMixInAnnotation(ThrowableInfo.class, ThrowableInfoMixIn.class);
	}
}
