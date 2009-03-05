/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.huxhorn.lilith.engine.impl;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.sulky.codec.DelegatingCodecBase;
import de.huxhorn.sulky.codec.XmlDecoder;
import de.huxhorn.sulky.codec.XmlEncoder;
import de.huxhorn.sulky.codec.Encoder;
import de.huxhorn.sulky.codec.Decoder;

public class LoggingEventWrapperXmlCodec
	extends DelegatingCodecBase<EventWrapper<LoggingEvent>>
{
	public LoggingEventWrapperXmlCodec(boolean compressed)
	{
		super(new XmlEncoder<EventWrapper<LoggingEvent>>(compressed, LoggingEvent.Level.class), new XmlDecoder<EventWrapper<LoggingEvent>>(compressed));
	}

	public void setCompressing(boolean compressing)
	{
		{
			Encoder<EventWrapper<LoggingEvent>> s = getEncoder();
			if(s instanceof XmlEncoder)
			{
				XmlEncoder ss= (XmlEncoder) s;
				ss.setCompressing(compressing);
			}
		}
		{
			Decoder<EventWrapper<LoggingEvent>> d = getDecoder();
			if(d instanceof XmlDecoder)
			{
				XmlDecoder sd= (XmlDecoder) d;
				sd.setCompressing(compressing);
			}
		}
	}

}
