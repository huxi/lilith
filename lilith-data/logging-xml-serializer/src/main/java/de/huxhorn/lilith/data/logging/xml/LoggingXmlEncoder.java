/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
 * Copyright 2007-2011 Joern Huxhorn
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

import de.huxhorn.lilith.data.logging.LoggingEvent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import de.huxhorn.sulky.codec.Encoder;

public class LoggingXmlEncoder
	implements Encoder<LoggingEvent>
{
	private LoggingEventWriter loggingEventWriter;
	private boolean compressing;
	private boolean sortingMaps;

	public LoggingXmlEncoder(boolean compressing)
	{
		this(compressing, false);
	}

	public LoggingXmlEncoder(boolean compressing, boolean sortingMaps)
	{
		this.compressing = compressing;
		loggingEventWriter = new LoggingEventWriter();
		loggingEventWriter.setWritingSchemaLocation(false);
		setCompressing(compressing);
		setSortingMaps(sortingMaps);
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	public void setCompressing(boolean compressing)
	{
		this.compressing = compressing;
	}

	public boolean isSortingMaps()
	{
		return sortingMaps;
	}

	public void setSortingMaps(boolean sortingMaps)
	{
		this.sortingMaps = sortingMaps;
		loggingEventWriter.setSortingMaps(sortingMaps);
	}

	public byte[] encode(LoggingEvent event)
	{
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

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
