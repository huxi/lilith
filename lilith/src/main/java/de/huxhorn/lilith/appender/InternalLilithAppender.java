/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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

import de.huxhorn.lilith.data.converter.Converter;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.logback.SameThreadLogbackLoggingConverter;
import de.huxhorn.lilith.engine.FileBufferFactory;
import de.huxhorn.lilith.api.FileConstants;
import de.huxhorn.lilith.engine.LoggingFileBufferFactory;
import de.huxhorn.lilith.engine.impl.LogFileFactoryImpl;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.FileBuffer;

import ch.qos.logback.core.AppenderBase;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class will always write into &lt;user.home&gt;/.lilith/sources/logs/Lilith.xxx. This s done so logging events during
 * movement of home directory can be handled. It's also quite handy because no instance of ApplicationPreferences
 * is actually needed.
 *
 * Attention: If SerializingFileBuffer is logging then SomethingBad(TM) will happen :)
 */
public class InternalLilithAppender
	extends AppenderBase<ch.qos.logback.classic.spi.LoggingEvent>
{
	private static final FileBuffer<EventWrapper<LoggingEvent>> fileBuffer;
	private static final SourceIdentifier sourceIdentifier;

	static
	{
		sourceIdentifier = new SourceIdentifier("Lilith");

		LogFileFactoryImpl logFileFactory =
			new LogFileFactoryImpl(new File(ApplicationPreferences.DEFAULT_APPLICATION_PATH, "logs/logging"));

		Map<String, String> loggingMetaData = new HashMap<String, String>();
		loggingMetaData.put(FileConstants.CONTENT_TYPE_KEY, FileConstants.CONTENT_TYPE_VALUE_LOGGING);
		loggingMetaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF);
		loggingMetaData.put(FileConstants.COMPRESSION_KEY, FileConstants.COMPRESSION_VALUE_GZIP);
		// TODO: configurable format and compressed

		FileBufferFactory<LoggingEvent> fileBufferFactory = new LoggingFileBufferFactory(logFileFactory, loggingMetaData);
		fileBuffer = fileBufferFactory.createActiveBuffer(sourceIdentifier);
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
		catch(CloneNotSupportedException e)
		{
			// won't happen
			return null;
		}
	}

	private Converter<LoggingEvent> converter = new SameThreadLogbackLoggingConverter();
	private long localId;


	public InternalLilithAppender()
	{
		localId = 0;
	}

	protected void append(ch.qos.logback.classic.spi.LoggingEvent event)
	{
		if(event != null && fileBuffer != null) // just to make sure...
		{
			localId++;
			event.getCallerData();
			LoggingEvent lilithEvent = converter.convert(event);
			EventWrapper<LoggingEvent> wrapper = new EventWrapper<LoggingEvent>(sourceIdentifier, localId, lilithEvent);
			fileBuffer.add(wrapper);
		}
	}
}
