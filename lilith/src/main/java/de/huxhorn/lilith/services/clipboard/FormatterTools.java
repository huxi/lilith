/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2016 Joern Huxhorn
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
package de.huxhorn.lilith.services.clipboard;

import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.sulky.formatting.SafeString;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

class FormatterTools
{
	static Optional<AccessEvent> resolveAccessEvent(Object object)
	{
		return Optional.ofNullable(resolveAccessEventInternal(object));
	}

	static Optional<LoggingEvent> resolveLoggingEvent(Object object)
	{
		return Optional.ofNullable(resolveLoggingEventInternal(object));
	}

	static Optional<String> resolveFormattedMessage(Object object)
	{
		Message message = resolveMessageInternal(object);
		if (message != null)
		{
			String value = message.getMessage();
			if (!isNullOrEmpty(value))
			{
				return Optional.of(value);
			}
		}
		return Optional.empty();
	}

	static Optional<String> resolveMessagePattern(Object object)
	{
		Message message = resolveMessageInternal(object);
		if (message != null)
		{
			String value = message.getMessagePattern();
			if (!isNullOrEmpty(value))
			{
				return Optional.of(value);
			}
		}
		return Optional.empty();
	}

	static Optional<ExtendedStackTraceElement[]> resolveCallStack(Object object)
	{
		LoggingEvent value = resolveLoggingEventInternal(object);
		if (value != null)
		{
			ExtendedStackTraceElement[] callStack = value.getCallStack();
			if (!isNullOrEmpty(callStack))
			{
				return Optional.of(callStack);
			}
		}
		return Optional.empty();
	}

	static Optional<ThrowableInfo> resolveThrowableInfo(Object object)
	{
		LoggingEvent value = resolveLoggingEventInternal(object);
		if (value != null)
		{
			return Optional.ofNullable(value.getThrowable());
		}
		return Optional.empty();
	}

	static Optional<String> resolveThrowableInfoName(Object object)
	{
		LoggingEvent value = resolveLoggingEventInternal(object);
		if (value != null)
		{
			ThrowableInfo throwable = value.getThrowable();
			if (throwable != null)
			{
				String name = throwable.getName();
				if (!isNullOrEmpty(name))
				{
					return Optional.of(name);
				}
			}
		}
		return Optional.empty();
	}

	static boolean isNullOrEmpty(String value)
	{
		return value == null || value.length() == 0;
	}

	static boolean isNullOrEmpty(Map<?, ?> value)
	{
		return value == null || value.size() == 0;
	}

	static boolean isNullOrEmpty(Collection<?> value)
	{
		return value == null || value.size() == 0;
	}

	static boolean isNullOrEmpty(Object[] value)
	{
		return value == null || value.length == 0;
	}


	static String toStringOrNull(String value)
	{
		if (isNullOrEmpty(value))
		{
			return null;
		}
		return value;
	}

	static String toStringOrNull(Map<String, ?> value)
	{
		if (isNullOrEmpty(value))
		{
			return null;
		}
		if (!value.containsKey(null))
		{
			return SafeString.toString(new TreeMap<>(value),
					SafeString.StringWrapping.CONTAINED,
					SafeString.StringStyle.GROOVY,
					SafeString.MapStyle.GROOVY);
		}
		// TreeMap can't handle null keys.
		return SafeString.toString(value,
				SafeString.StringWrapping.CONTAINED,
				SafeString.StringStyle.GROOVY,
				SafeString.MapStyle.GROOVY);
	}

	static String toStringOrNull(Set<String> value)
	{
		if (isNullOrEmpty(value))
		{
			return null;
		}
		if (!value.contains(null))
		{
			return SafeString.toString(new TreeSet<>(value),
					SafeString.StringWrapping.CONTAINED,
					SafeString.StringStyle.GROOVY,
					SafeString.MapStyle.GROOVY);
		}
		// TreeSet can't handle null values, right?.
		return SafeString.toString(value,
				SafeString.StringWrapping.CONTAINED,
				SafeString.StringStyle.GROOVY,
				SafeString.MapStyle.GROOVY);
	}

	private static AccessEvent resolveAccessEventInternal(Object object)
	{
		if (object instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) object;
			if (wrapper.getEvent() != null)
			{
				Object eventObj = wrapper.getEvent();
				if (eventObj instanceof AccessEvent)
				{
					return (AccessEvent) eventObj;
				}
			}
		}
		return null;
	}

	private static LoggingEvent resolveLoggingEventInternal(Object object)
	{
		if (object instanceof EventWrapper)
		{
			EventWrapper wrapper = (EventWrapper) object;
			if (wrapper.getEvent() != null)
			{
				Object eventObj = wrapper.getEvent();
				if (eventObj instanceof LoggingEvent)
				{
					return (LoggingEvent) eventObj;
				}
			}
		}
		return null;
	}

	private static Message resolveMessageInternal(Object object)
	{
		LoggingEvent event = resolveLoggingEventInternal(object);
		if (event != null)
		{
			return event.getMessage();
		}
		return null;
	}
}
