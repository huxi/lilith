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

package de.huxhorn.lilith.data.logging.xml;

import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
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

public class StackTraceElementIOTest
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

	private final Logger logger = LoggerFactory.getLogger(StackTraceElementIOTest.class);
	private StackTraceElementWriter steWriter;
	private StackTraceElementReader steReader;

	@Before
	public void setUp()
	{
		steWriter = new StackTraceElementWriter();
		steWriter.setWritingSchemaLocation(true);
		steReader = new StackTraceElementReader();
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
		ExtendedStackTraceElement elem = createSTE();
		check(elem, true);
	}

	@Test
	public void fileName()
		throws XMLStreamException
	{
		ExtendedStackTraceElement elem = createSTE();
		elem.setFileName("fileName");
		check(elem, true);
	}

	@Test
	public void fileNameLineNumber()
		throws XMLStreamException
	{
		ExtendedStackTraceElement elem = createSTE();
		elem.setFileName("fileName");
		elem.setLineNumber(17);
		check(elem, true);
	}

	@Test
	public void nativ3()
		throws XMLStreamException
	{
		ExtendedStackTraceElement elem = createSTE();
		elem.setLineNumber(ExtendedStackTraceElement.NATIVE_METHOD_LINE_NUMBER);
		check(elem, true);
	}

	@Test
	public void codeLocation()
		throws XMLStreamException
	{
		ExtendedStackTraceElement elem = createSTE();
		elem.setCodeLocation("codeLocation");
		check(elem, true);
	}

	@Test
	public void version()
		throws XMLStreamException
	{
		ExtendedStackTraceElement elem = createSTE();
		elem.setVersion("version");
		check(elem, true);
	}

	@Test
	public void exact()
		throws XMLStreamException
	{
		ExtendedStackTraceElement elem = createSTE();
		elem.setExact(true);
		check(elem, true);
	}

	@Test
	public void full()
		throws XMLStreamException
	{
		ExtendedStackTraceElement elem = createSTE();
		elem.setFileName("fileName");
		elem.setLineNumber(17);
		elem.setCodeLocation("codeLocation");
		elem.setVersion("version");
		elem.setExact(true);
		check(elem, true);
	}

	public ExtendedStackTraceElement createSTE()
	{
		ExtendedStackTraceElement elem = new ExtendedStackTraceElement();
		elem.setClassName("foo.Bar");
		elem.setMethodName("fooBar");
		return elem;
	}

	public void check(ExtendedStackTraceElement event, boolean indent)
		throws XMLStreamException
	{
		if(logger.isDebugEnabled()) logger.debug("Processing ExtendedStackTraceElement:\n{}", event);
		byte[] bytes = write(event, indent);
		String eventStr = new String(bytes, StandardCharsets.UTF_8);
		if(logger.isDebugEnabled()) logger.debug("ExtendedStackTraceElement marshalled to:\n{}", eventStr);
		ExtendedStackTraceElement readEvent = read(bytes);
		if(logger.isDebugEnabled()) logger.debug("ExtendedStackTraceElement read.");
		assertEquals(event, readEvent);
		if(logger.isDebugEnabled()) logger.debug("ExtendedStackTraceElements were equal.");
		bytes = write(event, indent);
		String readEventStr = new String(bytes, StandardCharsets.UTF_8);
		assertEquals(eventStr, readEventStr);
		if(logger.isDebugEnabled()) logger.debug("ExtendedStackTraceElements xml were equal.");
	}

	public byte[] write(ExtendedStackTraceElement event, boolean indent)
		throws XMLStreamException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLStreamWriter writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
		if(indent)
		{
			writer = new IndentingXMLStreamWriter(writer);
		}
		steWriter.write(writer, event, true);
		writer.flush();
		return out.toByteArray();
	}

	public ExtendedStackTraceElement read(byte[] bytes)
		throws XMLStreamException
	{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		XMLStreamReader reader = XML_INPUT_FACTORY.createXMLStreamReader(new InputStreamReader(in, StandardCharsets.UTF_8));
		return steReader.read(reader);
	}

}
