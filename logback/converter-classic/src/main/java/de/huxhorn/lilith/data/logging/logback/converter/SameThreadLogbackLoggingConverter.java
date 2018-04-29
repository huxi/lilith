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

package de.huxhorn.lilith.data.logging.logback.converter;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.logback.classic.NDC;

public class SameThreadLogbackLoggingConverter
	extends LogbackLoggingConverter
{
	@Override
	public LoggingEvent convert(Object o)
	{
		LoggingEvent result = super.convert(o);

		if(result == null)
		{
			return null;
		}

		// evaluate additional Thread info.
		ThreadInfo threadInfo = result.getThreadInfo();
		if(threadInfo == null)
		{
			threadInfo = new ThreadInfo();
		}

		// assuming this code is executed synchronously
		Thread t = Thread.currentThread();

		if(threadInfo.getName() == null)
		{
			threadInfo.setName(t.getName());
		}

		threadInfo.setId(t.getId());
		threadInfo.setPriority(t.getPriority());

		ThreadGroup tg = t.getThreadGroup();
		if(tg != null)
		{
			threadInfo.setGroupId((long) System.identityHashCode(tg));
			threadInfo.setGroupName(tg.getName());
		}

		result.setThreadInfo(threadInfo);

		// evaluate NDC
		if(!NDC.isEmpty())
		{
			// TODO: configurable NDC evaluation
			result.setNdc(NDC.getContextStack());
		}

		return result;
	}

	@Override
	public Class getSourceClass()
	{
		return ch.qos.logback.classic.spi.ILoggingEvent.class;
	}
}
