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
package de.huxhorn.lilith.data.logging.xml;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.sulky.generics.io.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class LoggingXmlSerializer
	implements Serializer<LoggingEvent>
{
	private LoggingEventWriter loggingEventWriter;
	private XMLOutputFactory outputFactory;
	private boolean compressing;

	public LoggingXmlSerializer(boolean compressing)
	{
		this.compressing = compressing;
		outputFactory = XMLOutputFactory.newInstance();
		loggingEventWriter = new LoggingEventWriter();
		loggingEventWriter.setWritingSchemaLocation(false);
	}

	public byte[] serialize(LoggingEvent event)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputStreamWriter osw;
		try
		{
			XMLStreamWriter writer;
			if(compressing)
			{
				GZIPOutputStream gos = new GZIPOutputStream(out);
				osw = new OutputStreamWriter(gos, "utf-8");
				writer = outputFactory.createXMLStreamWriter(osw);
			}
			else
			{
				osw = new OutputStreamWriter(out, "utf-8");
				writer = outputFactory.createXMLStreamWriter(osw);
			}

			loggingEventWriter.write(writer, event, true);
			writer.flush();
			writer.close();
			osw.flush(); // this is
			osw.close(); // absolutely necessary!!
			return out.toByteArray();
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
		return null;
	}
}