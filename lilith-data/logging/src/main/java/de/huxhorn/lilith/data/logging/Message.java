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

package de.huxhorn.lilith.data.logging;

import java.io.Serializable;
import java.util.Arrays;

/**
 * This class encapsulates a parameterized message as used by LoggingEvent and NDC.
 *
 * The message is formatted lazily the first time it is actually retrieved using getMessage().
 */
@SuppressWarnings({"PMD.MethodReturnsInternalArray", "PMD.ArrayIsStoredDirectly"})
public final class Message
	implements Serializable, Cloneable
{
	private static final long serialVersionUID = 5086167676879398378L;

	public static final Message[] ARRAY_PROTOTYPE = new Message[0];

	private String messagePattern;
	private String[] arguments;
	private transient String formattedMessage;

	public Message()
	{
		// for XML serialization
	}

	public Message(String messagePattern)
	{
		this(messagePattern, null);
	}

	public Message(String messagePattern, String[] arguments)
	{
		this.messagePattern = messagePattern;
		this.arguments = arguments;
	}

	public String getMessage()
	{
		if(this.formattedMessage == null)
		{
			// lazy init
			this.formattedMessage = MessageFormatter.format(messagePattern, arguments);
		}
		return this.formattedMessage;
	}

	public String getMessagePattern()
	{
		return messagePattern;
	}

	public void setMessagePattern(String messagePattern)
	{
		this.messagePattern = messagePattern;
		this.formattedMessage = null;
	}

	public String[] getArguments()
	{
		return arguments;
	}

	public void setArguments(String[] arguments)
	{
		this.arguments = arguments;
		this.formattedMessage = null;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Message message = (Message) o;

		return (messagePattern != null ? messagePattern.equals(message.messagePattern) : message.messagePattern == null)
				&& Arrays.equals(arguments, message.arguments);
	}

	@Override
	public int hashCode()
	{
		int result = messagePattern != null ? messagePattern.hashCode() : 0;
		result = 31 * result + (arguments != null ? Arrays.hashCode(arguments) : 0);
		return result;
	}

	@Override
	public Message clone()
		throws CloneNotSupportedException
	{
		Message result = (Message) super.clone();
		if(arguments != null)
		{
			int len = arguments.length;
			result.arguments = new String[len];
			System.arraycopy(arguments, 0, result.arguments, 0, len);
		}
		return result;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder(200);
		sb.append("Message{messagePattern=");
		if(messagePattern == null)
		{
			sb.append((String)null);
		}
		else
		{
			sb.append('"').append(messagePattern).append('"');
		}

		sb.append(", arguments=");
		if(arguments == null)
		{
			sb.append((String)null);
		}
		else
		{
			sb.append('[');
			boolean first = true;
			for (String current : arguments)
			{
				if(first)
				{
					first = false;
				}
				else
				{
					sb.append(", ");
				}
				if(current == null)
				{
					sb.append("null");
				}
				else
				{
					sb.append('"').append(current).append('"');
				}
			}
			sb.append(']');
		}
		sb.append('}');
		return sb.toString();
	}
}
