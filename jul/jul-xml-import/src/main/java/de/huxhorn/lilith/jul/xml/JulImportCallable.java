/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2016 Joern Huxhorn
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
 * Copyright 2007-2016 Joern Huxhorn
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

package de.huxhorn.lilith.jul.xml;

import de.huxhorn.lilith.data.eventsource.EventIdentifier;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.sulky.buffers.AppendOperation;
import de.huxhorn.sulky.formatting.ReplaceInvalidXmlCharacterReader;
import de.huxhorn.sulky.tasks.AbstractProgressingCallable;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.input.CountingInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class JulImportCallable
	extends AbstractProgressingCallable<Long>
{
	private final Logger logger = LoggerFactory.getLogger(JulImportCallable.class);

	private File inputFile;
	private AppendOperation<EventWrapper<LoggingEvent>> buffer;
	private LoggingEventReader loggingEventReader;
	private long result;

	public JulImportCallable(File inputFile, AppendOperation<EventWrapper<LoggingEvent>> buffer)
	{
		this.buffer = buffer;
		this.inputFile = inputFile;
		loggingEventReader = new LoggingEventReader();
	}

	public AppendOperation<EventWrapper<LoggingEvent>> getBuffer()
	{
		return buffer;
	}

	public File getInputFile()
	{
		return inputFile;
	}

	public Long call()
		throws Exception
	{
		if(!inputFile.isFile())
		{
			throw new IllegalArgumentException("'" + inputFile.getAbsolutePath() + "' is not a file!");
		}
		if(!inputFile.canRead())
		{
			throw new IllegalArgumentException("'" + inputFile.getAbsolutePath() + "' is not a readable!");
		}
		long fileSize = inputFile.length();
		setNumberOfSteps(fileSize);
		FileInputStream fis = new FileInputStream(inputFile);
		CountingInputStream cis = new CountingInputStream(fis);

		String fileName=inputFile.getName().toLowerCase();
		XMLStreamReader xmlStreamReader;
		Reader reader;
		if(fileName.endsWith(".gz"))
		{
			reader = new InputStreamReader(new GZIPInputStream(cis), StandardCharsets.UTF_8);
		}
		else
		{
			reader = new InputStreamReader(cis, StandardCharsets.UTF_8);
		}

		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		inputFactory.setProperty(XMLInputFactory.IS_VALIDATING, false);
		xmlStreamReader = inputFactory.createXMLStreamReader(new ReplaceInvalidXmlCharacterReader(reader));
		for(; ;)
		{
			try
			{
				LoggingEvent event = loggingEventReader.read(xmlStreamReader);
				setCurrentStep(cis.getByteCount());
				if(event == null)
				{
					break;
				}
				result++;
				EventWrapper<LoggingEvent> wrapper = new EventWrapper<>();
				wrapper.setEvent(event);
				SourceIdentifier sourceIdentifier = new SourceIdentifier(inputFile.getAbsolutePath());
				EventIdentifier eventId = new EventIdentifier(sourceIdentifier, result);
				wrapper.setEventIdentifier(eventId);
				buffer.add(wrapper);
			}
			catch(XMLStreamException ex)
			{
				if(logger.isWarnEnabled()) logger.warn("Exception while importing...", ex);
			}
		}
		return result;
	}

}
