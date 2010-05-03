/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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

import de.huxhorn.lilith.data.logging.LoggingEvent;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import de.huxhorn.sulky.codec.Decoder;

public class LoggingXmlDecoder
	implements Decoder<LoggingEvent>
{
	private LoggingEventReader loggingEventReader;
	private boolean compressing;

	public LoggingXmlDecoder(boolean compressing)
	{
		this.compressing = compressing;
		loggingEventReader = new LoggingEventReader();
	}

	public LoggingEvent decode(byte[] bytes)
	{
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		XMLStreamReader reader;
		try
		{
			if(compressing)
			{
				GZIPInputStream gis = new GZIPInputStream(in);
				reader = inputFactory.createXMLStreamReader(new InputStreamReader(gis, "utf-8"));
			}
			else
			{
				reader = inputFactory.createXMLStreamReader(new InputStreamReader(in, "utf-8"));
			}
			return loggingEventReader.read(reader);
		}
		catch(XMLStreamException e)
		{
// TODO: change body of catch statement
			e.printStackTrace();
		}
		catch(UnsupportedEncodingException e)
		{
// TODO: change body of catch statement
			e.printStackTrace();
		}
		catch(IOException e)
		{
// TODO: change body of catch statement
			e.printStackTrace();
		}
		finally
		{
			IOUtils.closeQuietly(in);
		}
		return null;
	}
}
