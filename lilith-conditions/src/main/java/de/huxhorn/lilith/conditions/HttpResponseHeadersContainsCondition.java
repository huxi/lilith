/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

package de.huxhorn.lilith.conditions;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import java.util.Map;

public class HttpResponseHeadersContainsCondition
	extends AbstractStringStringMapContainsCondition
	implements Cloneable
{
	private static final long serialVersionUID = -6804802011522314493L;

	public static final String DESCRIPTION = "ResponseHeaders.contains";

	public HttpResponseHeadersContainsCondition()
	{
		super();
	}

	public HttpResponseHeadersContainsCondition(String key, String value)
	{
		super(key, value);
	}

	@Override
	protected Map<String, String> resolveMap(Object element)
	{
		if(element instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) element;
			Object eventObj = wrapper.getEvent();
			if(eventObj instanceof AccessEvent)
			{
				AccessEvent event = (AccessEvent) eventObj;

				return event.getResponseHeaders();
			}
		}
		return null;
	}

	@Override
	public HttpResponseHeadersContainsCondition clone() throws CloneNotSupportedException {
		return (HttpResponseHeadersContainsCondition) super.clone();
	}

	@Override
	public String getDescription()
	{
		return DESCRIPTION;
	}
}
