/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2013 Joern Huxhorn
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
 * Copyright 2007-2013 Joern Huxhorn
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
import de.huxhorn.lilith.data.logging.*;
import de.huxhorn.lilith.data.logging.json.mixin.*;

public class LoggingModule
	extends SimpleModule
{
	public LoggingModule()
	{
		super("LilithLogging", new Version(1, 0, 0, null, "de.huxhorn.lilith", "de.huxhorn.lilith.data.logging-json-serializer"));
	}

	@Override
	public void setupModule(SetupContext context)
	{
		context.setMixInAnnotations(Message.class, MessageMixIn.class);
		context.setMixInAnnotations(ExtendedStackTraceElement.class, ExtendedStackTraceElementMixIn.class);
		context.setMixInAnnotations(LoggerContext.class, LoggerContextMixIn.class);
		context.setMixInAnnotations(Marker.class, MarkerMixIn.class);
		context.setMixInAnnotations(ThreadInfo.class, ThreadInfoMixIn.class);
		context.setMixInAnnotations(LoggingEvent.class, LoggingEventMixIn.class);
		context.setMixInAnnotations(ThrowableInfo.class, ThreadInfoMixIn.class);
	}

}
