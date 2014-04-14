/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2014 Joern Huxhorn
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
 * Copyright 2007-2014 Joern Huxhorn
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

import de.huxhorn.sulky.io.IOUtilities;

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
		inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		inputFactory.setProperty(XMLInputFactory.IS_VALIDATING, false);

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
			IOUtilities.closeQuietly(in);
		}
		return null;
	}
}
