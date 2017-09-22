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

package de.huxhorn.lilith.data.eventsource.xml;

import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.sulky.stax.IndentingXMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SourceIdentifierIOTest
{
	// thread-safe, see http://www.cowtowncoder.com/blog/archives/2006/06/entry_2.html
	// XMLInputFactory.newFactory() is not deprecated. See http://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8183519
	@SuppressWarnings("deprecation")
	private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newFactory();
	static
	{
		XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		XML_INPUT_FACTORY.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_VALIDATING, false);
	}
	private static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newFactory();

	private final Logger logger = LoggerFactory.getLogger(SourceIdentifierIOTest.class);
	private SourceIdentifierWriter sourceIdentifierWriter;
	private SourceIdentifierReader sourceIdentifierReader;

	@Before
	public void setUp()
	{
		sourceIdentifierWriter = new SourceIdentifierWriter();
		sourceIdentifierWriter.setWritingSchemaLocation(true);
		sourceIdentifierReader = new SourceIdentifierReader();
	}

	@Test
	public void correctOutputFactoryIsObtained()
	{
		String factoryClassName = XML_OUTPUT_FACTORY.getClass().getName();
		assertTrue(factoryClassName, factoryClassName.startsWith("com.ctc.wstx.stax"));
	}

	@Test
	public void correctInputFactoryIsObtained()
	{
		String factoryClassName = XML_INPUT_FACTORY.getClass().getName();
		assertTrue(factoryClassName, factoryClassName.startsWith("com.ctc.wstx.stax"));
	}

	@Test
	public void minimal()
		throws XMLStreamException
	{
		SourceIdentifier identifier = createMinimalEventSource();
		check(identifier, true);
	}

	@Test
	public void full()
		throws XMLStreamException
	{
		SourceIdentifier identifier = createMinimalEventSource();
		identifier.setSecondaryIdentifier("secondary");
		check(identifier, true);
	}

	@Test
	public void fullPrefix()
		throws XMLStreamException
	{
		sourceIdentifierWriter.setPreferredPrefix("foo");
		SourceIdentifier identifier = createMinimalEventSource();
		identifier.setSecondaryIdentifier("secondary");
		check(identifier, true);
	}

	public SourceIdentifier createMinimalEventSource()
	{
		SourceIdentifier result = new SourceIdentifier();
		result.setIdentifier("primary");
		return result;
	}

	public void check(SourceIdentifier original, boolean indent)
		throws XMLStreamException
	{
		if(logger.isDebugEnabled()) logger.debug("Processing:\n{}", original);
		byte[] bytes = write(original, indent);
		String originalStr = new String(bytes, StandardCharsets.UTF_8);
		if(logger.isDebugEnabled()) logger.debug("Marshalled to:\n{}", originalStr);
		SourceIdentifier read = read(bytes);
		if(logger.isDebugEnabled()) logger.debug("Read.");
		assertEquals(original, read);
		if(logger.isDebugEnabled()) logger.debug("Equal.");
		bytes = write(read, indent);
		String readStr = new String(bytes, StandardCharsets.UTF_8);
		assertEquals(originalStr, readStr);
		if(logger.isDebugEnabled()) logger.debug("Strings equal.");
	}

	public byte[] write(SourceIdentifier sourceIdentifier, boolean indent)
		throws XMLStreamException
	{

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLStreamWriter writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
		if(logger.isDebugEnabled()) logger.debug("XMLStreamWriter class: {}", writer.getClass().getName());
		if(indent)
		{
			writer = new IndentingXMLStreamWriter(writer, "\n");
		}
		sourceIdentifierWriter.write(writer, sourceIdentifier, true);
		writer.flush();
		return out.toByteArray();
	}

	public SourceIdentifier read(byte[] bytes)
		throws XMLStreamException
	{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		XMLStreamReader reader = XML_INPUT_FACTORY.createXMLStreamReader(new InputStreamReader(in, StandardCharsets.UTF_8));
		return sourceIdentifierReader.read(reader);
	}
}

