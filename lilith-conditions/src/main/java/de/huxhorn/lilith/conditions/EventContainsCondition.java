/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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
import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class EventContainsCondition
	implements LilithCondition, SearchStringCondition, Cloneable
{
	private static final long serialVersionUID = -8094852331877521764L;

	public static final String DESCRIPTION = "event.contains";

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

	@Override
	public String getSearchString()
	{
		return searchString;
	}

	private boolean checkString(String input)
	{
		return input != null && input.contains(searchString);
	}

	@Override
	public boolean isTrue(Object value)
	{
		if(searchString == null)
		{
			return false;
		}
		if(searchString.length() == 0)
		{
			return true;
		}
		if(value instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) value;
			Object eventObj = wrapper.getEvent();
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
					if(checkString(message))
					{
						return true;
					}
				}

				{
					if (checkLoggerContext(event.getLoggerContext()))
					{
						return true;
					}
				}

				{
					String level = String.valueOf(event.getLevel());
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
					if(checkStackTraceElements(event.getCallStack()))
					{
						return true;
					}
				}

				{
					ThrowableInfo throwable = event.getThrowable();
					if(checkThrowable(throwable, null))
					{
						return true;
					}
				}

				{
					ThreadInfo threadInfo = event.getThreadInfo();
					if(threadInfo != null)
					{
						if(checkString(threadInfo.getName()))
						{
							return true;
						}

						Long threadId = threadInfo.getId();
						if(threadId != null && checkString(Long.toString(threadId)))
						{
							return true;
						}

						Integer threadPriority = threadInfo.getPriority();
						if(threadPriority != null && checkString(Integer.toString(threadPriority)))
						{
							return true;
						}

						if(checkString(threadInfo.getGroupName()))
						{
							return true;
						}

						Long groupId = threadInfo.getGroupId();
						if(groupId != null && checkString(Long.toString(groupId)))
						{
							return true;
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
					if(checkMarker(event.getMarker(), null))
					{
						return true;
					}
				}

				{
					Message[] ndc = event.getNdc();
					if(ndc != null)
					{
						for (Message current : ndc)
						{
							if(current == null)
							{
								continue;
							}
							if(checkString(current.getMessage()))
							{
								return true;
							}
							if(checkString(current.getMessagePattern()))
							{
								return true;
							}
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
					if (checkLoggerContext(event.getLoggerContext()))
					{
						return true;
					}
				}

				{
					String message = String.valueOf(event.getStatusCode());
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

	private boolean checkLoggerContext(LoggerContext context)
	{
		return context != null && (checkString(context.getName()) || checkMap(context.getProperties()));
	}

	private boolean checkThrowable(ThrowableInfo throwable, IdentityHashMap<ThrowableInfo, Object> dejaVu)
	{
		if(throwable == null)
		{
			return false;
		}
		if(dejaVu == null)
		{
			dejaVu = new IdentityHashMap<>();
		}
		if(dejaVu.containsKey(throwable))
		{
			return false;
		}
		dejaVu.put(throwable, null);
		if(checkString(throwable.getName()))
		{
			return true;
		}
		if(checkString(throwable.getMessage()))
		{
			return true;
		}
		if(checkThrowable(throwable.getCause(), dejaVu))
		{
			return true;
		}
		ThrowableInfo[] suppressed = throwable.getSuppressed();
		if(suppressed != null)
		{
			for (ThrowableInfo current : suppressed)
			{
				if(checkThrowable(current, dejaVu))
				{
					return true;
				}
			}
		}

		return checkStackTraceElements(throwable.getStackTrace());
	}

	private boolean checkStackTraceElements(ExtendedStackTraceElement[] callStack)
	{
		if(callStack == null)
		{
			return false;
		}
		for (ExtendedStackTraceElement current : callStack)
		{
			if(current == null)
			{
				continue;
			}
			if(checkString(current.toString(true)))
			{
				return true;
			}
		}
		return false;
	}

	private boolean checkMarker(Marker marker, Set<String> processedMarkers)
	{
		if(marker == null)
		{
			return false;
		}
		if(checkString(marker.getName()))
		{
			return true;
		}

		if(processedMarkers == null)
		{
			processedMarkers = new HashSet<>();
		}
		if(!processedMarkers.contains(marker.getName()))
		{
			processedMarkers.add(marker.getName());
			if(marker.hasReferences())
			{
				Map<String, Marker> children = marker.getReferences();
				//noinspection ConstantConditions
				for(Map.Entry<String, Marker> current : children.entrySet())
				{
					if(checkMarker(current.getValue(), processedMarkers))
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean checkMap(Map<String, String> map)
	{
		if(map == null)
		{
			return false;
		}

		for(Map.Entry<String, String> entry : map.entrySet())
		{
			if(checkString(entry.getKey()) || checkString(entry.getValue()))
			{
				return true;
			}
		}

		return false;
	}

	private boolean checkArrayMap(Map<String, String[]> map)
	{
		if(map == null)
		{
			return false;
		}

		for(Map.Entry<String, String[]> entry : map.entrySet())
		{
			if(checkString(entry.getKey()))
			{
				return true;
			}
			String[] array = entry.getValue();
			if(array == null)
			{
				continue;
			}
			for(String s : array)
			{
				if(checkString(s))
				{
					return true;
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

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		EventContainsCondition that = (EventContainsCondition) o;

		return !(searchString != null ? !searchString.equals(that.searchString) : that.searchString != null);
	}

	@Override
	public int hashCode()
	{
		int result;
		result = (searchString != null ? searchString.hashCode() : 0);
		return result;
	}

	@Override
	public EventContainsCondition clone()
		throws CloneNotSupportedException
	{
		return (EventContainsCondition) super.clone();
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append(getDescription()).append('(');
		if(searchString != null)
		{
			result.append('"').append(searchString).append('"');
		}
		else
		{
			result.append("null");
		}
		result.append(')');
		return result.toString();
	}

	@Override
	public String getDescription()
	{
		return DESCRIPTION;
	}
}
