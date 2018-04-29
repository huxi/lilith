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

package de.huxhorn.lilith.data.logging.xml.codec;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.xml.LoggingEventWriter;
import de.huxhorn.sulky.codec.Encoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class LoggingXmlEncoder
	implements Encoder<LoggingEvent>
{
	// thread-safe, see http://www.cowtowncoder.com/blog/archives/2006/06/entry_2.html
	static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newFactory();

	private final LoggingEventWriter loggingEventWriter;
	private final boolean compressing;

	public LoggingXmlEncoder(boolean compressing)
	{
		this(compressing, false);
	}

	public LoggingXmlEncoder(boolean compressing, boolean sortingMaps)
	{
		this.compressing = compressing;
		loggingEventWriter = new LoggingEventWriter();
		loggingEventWriter.setWritingSchemaLocation(false);
		loggingEventWriter.setSortingMaps(sortingMaps);
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	@Override
	@SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
	public byte[] encode(LoggingEvent event)
	{
		if(event == null)
		{
			return null;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputStreamWriter osw;
		try
		{
			XMLStreamWriter writer;
			if(compressing)
			{
				GZIPOutputStream gos = new GZIPOutputStream(out);
				osw = new OutputStreamWriter(gos, StandardCharsets.UTF_8);
				writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(osw);
			}
			else
			{
				osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
				writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(osw);
			}

			loggingEventWriter.write(writer, event, true);
			writer.flush();
			writer.close();
			osw.flush(); // this is
			osw.close(); // absolutely necessary!!
			return out.toByteArray();
		}
		catch(XMLStreamException | IOException | NullPointerException ignore) // NOPMD
		{
			//e.printStackTrace();
		}
		return null;
	}
}
