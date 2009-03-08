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
package de.huxhorn.lilith.conditions;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class EventContainsCondition
	implements LilithCondition
{
	private static final long serialVersionUID = -8094852331877521764L;

	private String searchString;

	public EventContainsCondition()
	{
		this(null);
	}

	public EventContainsCondition(String searchString)
	{
		setSearchString(searchString);
	}

	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
	}

	public String getSearchString()
	{
		return searchString;
	}

	private boolean checkString(String input)
	{
		if(searchString == null)
		{
			return false;
		}
		if(input != null)
		{
			if(input.contains(searchString))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isTrue(Object value)
	{
		if(searchString == null)
		{
			return false;
		}
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) value;
			Object eventObj = wrapper.getEvent();
			if(searchString.length() == 0)
			{
				return true;
			}
			if(eventObj instanceof LoggingEvent)
			{
				LoggingEvent event = (LoggingEvent) eventObj;

				{
					String message = null;
					Message messageObj = event.getMessage();
					if(messageObj != null)
					{
						message = messageObj.getMessage();
					}
					if(message != null)
					{
						if(checkString(message))
						{
							return true;
						}
					}
				}

				{
					String appId = event.getApplicationIdentifier();
					if(appId != null)
					{
						if(checkString(appId))
						{
							return true;
						}
					}
				}

				{
					String level = "" + event.getLevel();
					if(checkString(level))
					{
						return true;
					}
				}

				{
					String loggerName = event.getLogger();
					if(checkString(loggerName))
					{
						return true;
					}
				}

				{
					ThreadInfo threadInfo = event.getThreadInfo();
					if(threadInfo != null)
					{
						String threadName = threadInfo.getName();
						if(checkString(threadName))
						{
							return true;
						}
						Long threadId = threadInfo.getId();
						if(threadId != null)
						{
							if(checkString("" + threadId))
							{
								return true;
							}
						}
					}
				}

				{
					Map<String, String> mdcMap = event.getMdc();
					if(checkMap(mdcMap))
					{
						return true;
					}
				}


				{
					Marker marker = event.getMarker();
					if(marker != null)
					{
						if(checkMarker(marker, null))
						{
							return true;
						}
					}
				}

			}
			else if(eventObj instanceof AccessEvent)
			{
				AccessEvent event = (AccessEvent) eventObj;

				{
					String message = event.getRequestURL();
					if(checkString(message))
					{
						return true;
					}
				}

				{
					String appId = event.getApplicationIdentifier();
					if(appId != null)
					{
						if(checkString(appId))
						{
							return true;
						}
					}
				}

				{
					String message = "" + event.getStatusCode();
					if(checkString(message))
					{
						return true;
					}
				}

				{
					Map<String, String> propertyMap = event.getRequestHeaders();
					if(checkMap(propertyMap))
					{
						return true;
					}

				}

				{
					Map<String, String> propertyMap = event.getResponseHeaders();
					if(checkMap(propertyMap))
					{
						return true;
					}

				}

				{
					Map<String, String[]> propertyMap = event.getRequestParameters();
					if(checkArrayMap(propertyMap))
					{
						return true;
					}

				}
			}
		}
		return false;
	}

	private boolean checkMarker(Marker marker, List<String> processedMarkers)
	{
		if(marker != null)
		{
			if(processedMarkers == null)
			{
				processedMarkers = new ArrayList<String>();
			}
			if(checkString(marker.getName()))
			{
				return true;
			}
			if(!processedMarkers.contains(marker.getName()))
			{
				processedMarkers.add(marker.getName());
				if(marker.hasReferences())
				{
					Map<String, Marker> children = marker.getReferences();
					for(Map.Entry<String, Marker> current : children.entrySet())
					{
						Marker child = current.getValue();
						if(checkMarker(child, processedMarkers))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private boolean checkMap(Map<String, String> map)
	{
		if(map != null)
		{
			for(Map.Entry<String, String> entry : map.entrySet())
			{
				if(checkString(entry.getKey()))
				{
					return true;
				}
				if(checkString(entry.getValue()))
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkArrayMap(Map<String, String[]> map)
	{
		if(map != null)
		{
			for(Map.Entry<String, String[]> entry : map.entrySet())
			{
				if(checkString(entry.getKey()))
				{
					return true;
				}
				String[] array = entry.getValue();
				for(String s : array)
				{
					if(checkString(s))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	private void readObject(ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		setSearchString(this.searchString);
	}

	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		EventContainsCondition that = (EventContainsCondition) o;

		return !(searchString != null ? !searchString.equals(that.searchString) : that.searchString != null);
	}

	public int hashCode()
	{
		int result;
		result = (searchString != null ? searchString.hashCode() : 0);
		return result;
	}

	public EventContainsCondition clone()
		throws CloneNotSupportedException
	{
		return (EventContainsCondition) super.clone();
	}

	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append(getDescription()).append("(");
		if(searchString != null)
		{
			result.append("\"");
			result.append(searchString);
			result.append("\"");
		}
		else
		{
			result.append("null");
		}
		result.append(")");
		return result.toString();
	}

	public String getDescription()
	{
		return "event.contains";
	}
}
