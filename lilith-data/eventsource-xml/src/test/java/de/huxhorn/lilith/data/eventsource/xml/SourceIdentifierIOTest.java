/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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
package de.huxhorn.lilith.data.eventsource.xml;

import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.sulky.stax.IndentingXMLStreamWriter;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class SourceIdentifierIOTest
	extends TestCase
{
	private final Logger logger = LoggerFactory.getLogger(SourceIdentifierIOTest.class);
	private XMLOutputFactory outputFactory;
	private SourceIdentifierWriter sourceIdentifierWriter;
	private XMLInputFactory inputFactory;
	private SourceIdentifierReader sourceIdentifierReader;

	public void setUp()
	{
		outputFactory = XMLOutputFactory.newInstance();
		inputFactory = XMLInputFactory.newInstance();
		sourceIdentifierWriter = new SourceIdentifierWriter();
		sourceIdentifierWriter.setWritingSchemaLocation(true);
		sourceIdentifierReader = new SourceIdentifierReader();
	}

	public SourceIdentifier createMinimalEventSource()
	{
		SourceIdentifier result = new SourceIdentifier();
		result.setIdentifier("primary");
		return result;
	}

	public void testMinimal()
		throws XMLStreamException, UnsupportedEncodingException
	{
		SourceIdentifier identifier = createMinimalEventSource();
		check(identifier, true);
	}

	public void testFull()
		throws XMLStreamException, UnsupportedEncodingException
	{
		SourceIdentifier identifier = createMinimalEventSource();
		identifier.setSecondaryIdentifier("secondary");
		check(identifier, true);
	}

	public void testFullPrefix()
		throws XMLStreamException, UnsupportedEncodingException
	{
		sourceIdentifierWriter.setPreferredPrefix("foo");
		SourceIdentifier identifier = createMinimalEventSource();
		identifier.setSecondaryIdentifier("secondary");
		check(identifier, true);
	}

	public void check(SourceIdentifier original, boolean indent)
		throws UnsupportedEncodingException, XMLStreamException
	{
		if(logger.isDebugEnabled()) logger.debug("Processing:\n{}", original);
		byte[] bytes = write(original, indent);
		if(bytes == null)
		{
			return; // skip this test because of http://jira.codehaus.org/browse/STAX-50
		}
		String originalStr = new String(bytes, "UTF-8");
		if(logger.isDebugEnabled()) logger.debug("Marshalled to:\n{}", originalStr);
		SourceIdentifier read = read(bytes);
		if(logger.isDebugEnabled()) logger.debug("Read.");
		assertEquals(original, read);
		if(logger.isDebugEnabled()) logger.debug("Equal.");
		bytes = write(read, indent);
		String readStr = new String(bytes, "UTF-8");
		assertEquals(originalStr, readStr);
		if(logger.isDebugEnabled()) logger.debug("Strings equal.");
	}

	public byte[] write(SourceIdentifier sourceIdentifier, boolean indent)
		throws XMLStreamException, UnsupportedEncodingException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLStreamWriter writer = outputFactory.createXMLStreamWriter(new OutputStreamWriter(out, "utf-8"));
		if(writer.getClass().getName().equals("com.bea.xml.stream.XMLWriterBase"))
		{
			if(logger.isInfoEnabled()) logger.info("Skipping test because of http://jira.codehaus.org/browse/STAX-50");
			return null;
		}
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
		throws XMLStreamException, UnsupportedEncodingException
	{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		XMLStreamReader reader = inputFactory.createXMLStreamReader(new InputStreamReader(in, "utf-8"));
		return sourceIdentifierReader.read(reader);
	}
}

