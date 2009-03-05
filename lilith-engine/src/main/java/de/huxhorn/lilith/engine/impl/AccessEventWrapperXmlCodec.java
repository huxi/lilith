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

import de.huxhorn.sulky.generics.io.DelegatingCodecBase;
import de.huxhorn.sulky.generics.io.XmlSerializer;
import de.huxhorn.sulky.generics.io.XmlDeserializer;
import de.huxhorn.sulky.generics.io.Serializer;
import de.huxhorn.sulky.generics.io.Deserializer;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.access.AccessEvent;

public class AccessEventWrapperXmlCodec
	extends DelegatingCodecBase<EventWrapper<AccessEvent>>
{
	public AccessEventWrapperXmlCodec(boolean compressed)
	{
		super(new XmlSerializer<EventWrapper<AccessEvent>>(compressed), new XmlDeserializer<EventWrapper<AccessEvent>>(compressed));
	}

	public void setCompressing(boolean compressing)
	{
		{
			Serializer<EventWrapper<AccessEvent>> s = getSerializer();
			if(s instanceof XmlSerializer)
			{
				XmlSerializer ss= (XmlSerializer) s;
				ss.setCompressing(compressing);
			}
		}
		{
			Deserializer<EventWrapper<AccessEvent>> d = getDeserializer();
			if(d instanceof XmlDeserializer)
			{
				XmlDeserializer sd= (XmlDeserializer) d;
				sd.setCompressing(compressing);
			}
		}
	}

}
