/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
package de.huxhorn.lilith.data.logging.xml;

import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.sulky.stax.IndentingXMLStreamWriter;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class StackTraceElementIOTest
	extends TestCase
{
	private final Logger logger = LoggerFactory.getLogger(StackTraceElementIOTest.class);
	private XMLOutputFactory outputFactory;
	private StackTraceElementWriter steWriter;
	private XMLInputFactory inputFactory;
	private StackTraceElementReader steReader;

	public void setUp()
	{
		outputFactory = XMLOutputFactory.newInstance();
		inputFactory = XMLInputFactory.newInstance();
		steWriter = new StackTraceElementWriter();
		steWriter.setWritingSchemaLocation(true);
		steReader = new StackTraceElementReader();
	}

	public ExtendedStackTraceElement createSTE()
	{
		ExtendedStackTraceElement elem=new ExtendedStackTraceElement();
		elem.setClassName("foo.Bar");
		elem.setMethodName("fooBar");
		return elem;
	}

	public void testMinimal() throws XMLStreamException, UnsupportedEncodingException
	{
		ExtendedStackTraceElement elem=createSTE();
		check(elem, true);
	}

	public void testFileName() throws XMLStreamException, UnsupportedEncodingException
	{
		ExtendedStackTraceElement elem=createSTE();
		elem.setFileName("fileName");
		check(elem, true);
	}

	public void testFileNameLineNumber() throws XMLStreamException, UnsupportedEncodingException
	{
		ExtendedStackTraceElement elem=createSTE();
		elem.setFileName("fileName");
		elem.setLineNumber(17);
		check(elem, true);
	}

	public void testNative() throws XMLStreamException, UnsupportedEncodingException
	{
		ExtendedStackTraceElement elem=createSTE();
		elem.setLineNumber(ExtendedStackTraceElement.NATIVE_METHOD);
		check(elem, true);
	}

	public void testCodeLocation() throws XMLStreamException, UnsupportedEncodingException
	{
		ExtendedStackTraceElement elem=createSTE();
		elem.setCodeLocation("codeLocation");
		check(elem, true);
	}

	public void testVersion() throws XMLStreamException, UnsupportedEncodingException
	{
		ExtendedStackTraceElement elem=createSTE();
		elem.setVersion("version");
		check(elem, true);
	}

	public void testExact() throws XMLStreamException, UnsupportedEncodingException
	{
		ExtendedStackTraceElement elem=createSTE();
		elem.setExact(true);
		check(elem, true);
	}

	public void testFull() throws XMLStreamException, UnsupportedEncodingException
	{
		ExtendedStackTraceElement elem=createSTE();
		elem.setFileName("fileName");
		elem.setLineNumber(17);
		elem.setCodeLocation("codeLocation");
		elem.setVersion("version");
		elem.setExact(true);
		check(elem, true);
	}

	public void check(ExtendedStackTraceElement event, boolean indent) throws UnsupportedEncodingException, XMLStreamException
	{
		if(logger.isDebugEnabled()) logger.debug("Processing ExtendedStackTraceElement:\n{}", event);
		byte[] bytes = write(event, indent);
		String eventStr =new String(bytes, "UTF-8");
		if(logger.isDebugEnabled()) logger.debug("ExtendedStackTraceElement marshalled to:\n{}", eventStr);
		ExtendedStackTraceElement readEvent = read(bytes);
		if(logger.isDebugEnabled()) logger.debug("ExtendedStackTraceElement read.");
		assertEquals(event, readEvent);
		if(logger.isDebugEnabled()) logger.debug("ExtendedStackTraceElements were equal.");
		bytes = write(event, indent);
		String readEventStr=new String(bytes, "UTF-8");
		assertEquals(eventStr, readEventStr);
		if(logger.isDebugEnabled()) logger.debug("ExtendedStackTraceElements xml were equal.");
	}

	public byte[] write(ExtendedStackTraceElement event, boolean indent) throws XMLStreamException, UnsupportedEncodingException
	{
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		XMLStreamWriter writer=outputFactory.createXMLStreamWriter(new OutputStreamWriter(out,"utf-8"));
		if(indent && writer.getClass().getName().equals("com.bea.xml.stream.XMLWriterBase"))
		{

			if(logger.isInfoEnabled()) logger.info("Won't indent because of http://jira.codehaus.org/browse/STAX-42");
			indent=false;
		}
		if(indent)
		{
			writer=new IndentingXMLStreamWriter(writer);
		}
		steWriter.write(writer, event, true);
		writer.flush();
		return out.toByteArray();
	}

	public ExtendedStackTraceElement read(byte[] bytes) throws XMLStreamException, UnsupportedEncodingException
	{
		ByteArrayInputStream in=new ByteArrayInputStream(bytes);
		XMLStreamReader reader=inputFactory.createXMLStreamReader(new InputStreamReader(in, "utf-8"));
		return steReader.read(reader);
	}

}