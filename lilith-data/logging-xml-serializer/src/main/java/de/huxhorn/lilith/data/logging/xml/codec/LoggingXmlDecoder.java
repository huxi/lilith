/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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
 * Copyright 2007-2018 Joern Huxhorn
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
import de.huxhorn.lilith.data.logging.xml.LoggingEventReader;
import de.huxhorn.sulky.codec.Decoder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class LoggingXmlDecoder
	implements Decoder<LoggingEvent>
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

	private final LoggingEventReader loggingEventReader;
	private final boolean compressing;

	public LoggingXmlDecoder(boolean compressing)
	{
		this.compressing = compressing;
		loggingEventReader = new LoggingEventReader();
	}

	@Override
	public LoggingEvent decode(byte[] bytes)
	{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try
		{
			return loggingEventReader.read(createXmlStreamReader(in));
		}
		catch(XMLStreamException | IOException e)
		{
			e.printStackTrace(); // NOPMD
		}
		return null;
	}

	private XMLStreamReader createXmlStreamReader(ByteArrayInputStream in)
			throws IOException, XMLStreamException
	{
		if(compressing)
		{
			GZIPInputStream gis = new GZIPInputStream(in);
			return XML_INPUT_FACTORY.createXMLStreamReader(new InputStreamReader(gis, StandardCharsets.UTF_8));
		}
		return XML_INPUT_FACTORY.createXMLStreamReader(new InputStreamReader(in, StandardCharsets.UTF_8));
	}
}
