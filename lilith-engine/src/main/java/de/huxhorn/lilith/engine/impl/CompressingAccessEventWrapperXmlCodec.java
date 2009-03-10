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

import de.huxhorn.sulky.codec.DelegatingCodecBase;
import de.huxhorn.sulky.codec.XmlEncoder;
import de.huxhorn.sulky.codec.XmlDecoder;
import de.huxhorn.sulky.codec.Encoder;
import de.huxhorn.sulky.codec.Decoder;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.access.AccessEvent;

public class CompressingAccessEventWrapperXmlCodec
	extends DelegatingCodecBase<EventWrapper<AccessEvent>>
{
	public CompressingAccessEventWrapperXmlCodec()
	{
		super(new XmlEncoder<EventWrapper<AccessEvent>>(true),
			new XmlDecoder<EventWrapper<AccessEvent>>(true));
	}
}
