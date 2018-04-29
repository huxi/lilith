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

package de.huxhorn.lilith.log4j.xml;

import de.huxhorn.lilith.data.eventsource.EventIdentifier;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.sulky.buffers.AppendOperation;
import de.huxhorn.sulky.tasks.AbstractProgressingCallable;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.io.input.CountingInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("PMD.ClassNamingConventions")
public class Log4jImportCallable
	extends AbstractProgressingCallable<Long>
{
	// thread-safe, see http://www.cowtowncoder.com/blog/archives/2006/06/entry_2.html
	// XMLInputFactory.newFactory() is not deprecated. See http://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8183519
	@SuppressWarnings("deprecation")
	static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newFactory();
	static
	{
		XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		XML_INPUT_FACTORY.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_VALIDATING, false);
	}

	private final Logger logger = LoggerFactory.getLogger(Log4jImportCallable.class);

	private static final String CLOSING_LOG4J_EVENT_TAG = "</log4j:event>";
	private static final String LOG4J_NAMESPACE = "xmlns:log4j=\"http://jakarta.apache.org/log4j/\"";
	private static final String OPENING_LOG4J_EVENT_TAG_EXCL_NS = "<log4j:event ";
	private static final String OPENING_LOG4J_EVENT_TAG_INCL_NS = OPENING_LOG4J_EVENT_TAG_EXCL_NS + LOG4J_NAMESPACE + " ";

	private final File inputFile;
	private final AppendOperation<EventWrapper<LoggingEvent>> buffer;
	private final LoggingEventReader instance;
	private long result;

	public Log4jImportCallable(File inputFile, AppendOperation<EventWrapper<LoggingEvent>> buffer)
	{
		this.buffer = buffer;
		this.inputFile = inputFile;
		instance = new LoggingEventReader();
	}

	public AppendOperation<EventWrapper<LoggingEvent>> getBuffer()
	{
		return buffer;
	}

	public File getInputFile()
	{
		return inputFile;
	}

	@Override
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
		InputStream fis = Files.newInputStream(inputFile.toPath());
		CountingInputStream cis = new CountingInputStream(fis);

		String fileName=inputFile.getName().toLowerCase(Locale.US);
		BufferedReader br;
		if(fileName.endsWith(".gz"))
		{
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(cis), StandardCharsets.UTF_8));
		}
		else
		{
			br = new BufferedReader(new InputStreamReader(cis, StandardCharsets.UTF_8));
		}

		StringBuilder builder = new StringBuilder();

		result = 0;
		for(;;)
		{
			String line = br.readLine();
			setCurrentStep(cis.getByteCount());
			if(line == null)
			{
				evaluate(builder.toString());
				break;
			}
			for(;;)
			{
				int closeIndex = line.indexOf(CLOSING_LOG4J_EVENT_TAG);
				if(closeIndex >= 0)
				{
					int endIndex = closeIndex + CLOSING_LOG4J_EVENT_TAG.length();
					builder.append(line.subSequence(0, endIndex));
					evaluate(builder.toString());
					builder.setLength(0);
					line = line.substring(endIndex);
				}
				else
				{
					builder.append(line).append('\n');
					break;
				}
			}
		}
		return result;
	}

	private void evaluate(String eventStr)
	{
		eventStr = prepare(eventStr);

		try
		{
			LoggingEvent event = readEvent(eventStr);
			if(event != null)
			{
				result++;
				EventWrapper<LoggingEvent> wrapper = new EventWrapper<>();
				wrapper.setEvent(event);
				SourceIdentifier sourceIdentifier = new SourceIdentifier(inputFile.getAbsolutePath());
				EventIdentifier eventId = new EventIdentifier(sourceIdentifier, result);
				wrapper.setEventIdentifier(eventId);
				buffer.add(wrapper);
			}
		}
		catch(XMLStreamException e)
		{
			// ignore
		}
	}

	private String prepare(String eventStr)
	{
		if(!eventStr.contains(LOG4J_NAMESPACE))
		{
			eventStr = eventStr.replace(OPENING_LOG4J_EVENT_TAG_EXCL_NS, OPENING_LOG4J_EVENT_TAG_INCL_NS);
			if(logger.isDebugEnabled()) logger.debug("After change: {}", eventStr);
		}

		return eventStr;
	}

	private LoggingEvent readEvent(String eventStr)
		throws XMLStreamException
	{
		byte[] bytes = eventStr.getBytes(StandardCharsets.UTF_8);

		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		XMLStreamReader reader = XML_INPUT_FACTORY.createXMLStreamReader(new InputStreamReader(in, StandardCharsets.UTF_8));
		return instance.read(reader);
	}
}
