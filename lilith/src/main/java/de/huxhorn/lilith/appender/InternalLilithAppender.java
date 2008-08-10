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
package de.huxhorn.lilith.appender;

import ch.qos.logback.core.AppenderBase;
import de.huxhorn.lilith.data.logging.logback.LogbackLoggingAdapter;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.engine.impl.LogFileFactoryImpl;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.sulky.buffers.FileBuffer;
import de.huxhorn.sulky.buffers.SerializingFileBuffer;
import de.huxhorn.sulky.buffers.Buffer;

import java.io.File;

/**
 * This class will always write into <user.home>/.lilith/sources/logs/Lilith.xxx. This s done so logging events during
 * movement of home directory can be handled. It's also quite handy because no instance of ApplicationPreferences
 * is actually needed.
 *
 * Attention: If SerializingFileBuffer is logging then SomethingBad(TM) will happen :)
 */
public class InternalLilithAppender extends AppenderBase<ch.qos.logback.classic.spi.LoggingEvent>
{
	private static final FileBuffer<EventWrapper<LoggingEvent>> fileBuffer;
	private static final SourceIdentifier sourceIdentifier;

	static
	{
		sourceIdentifier=new SourceIdentifier("Lilith");

		LogFileFactoryImpl logFileFactory = new LogFileFactoryImpl(new File(ApplicationPreferences.DEFAULT_APPLICATION_PATH, "sources/logs"), "ljlogging");
		File dataFile = logFileFactory.getDataFile(sourceIdentifier);
		File indexFile = logFileFactory.getIndexFile(sourceIdentifier);

		fileBuffer=new SerializingFileBuffer<EventWrapper<LoggingEvent>>(dataFile, indexFile);
	}

	public static Buffer<EventWrapper<LoggingEvent>> getBuffer()
	{
		return fileBuffer;
	}

	public static SourceIdentifier getSourceIdentifier()
	{
		try
		{
			return sourceIdentifier.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// won't happen
			return null;
		}
	}

	private LogbackLoggingAdapter adapter;
	private long localId;


	public InternalLilithAppender()
	{
		adapter=new LogbackLoggingAdapter();
		localId=0;
	}

	protected void append(ch.qos.logback.classic.spi.LoggingEvent event)
	{
		if(event!=null && fileBuffer!=null) // just to make sure...
		{
			localId++;
			event.getCallerData();
			LoggingEvent lilithEvent = adapter.convert(event);
			EventWrapper<LoggingEvent> wrapper = new EventWrapper<LoggingEvent>(sourceIdentifier, localId, lilithEvent);
			fileBuffer.add(wrapper);
		}
	}
}
