/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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
 * Copyright 2007-2017 Joern Huxhorn
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

import de.huxhorn.sulky.formatting.SafeString;
import java.util.Arrays;

/**
 * <p>Replacement for org.slf4j.helpers.MessageFormatter.</p>
 * <p>
 * In contrast to the mentioned class, the formatting of message pattern and arguments into the actual message
 * is split into three parts:
 * </p>
 * <ol>
 * <li>Counting of placeholders in the message pattern (cheap)</li>
 * <li>Conversion of argument array into an ArgumentResult, containing the arguments converted to String as well as
 * an optional Throwable if available (relatively cheap)</li>
 * <li>Replacement of placeholders in a message pattern with arguments given as String[]. (most expensive)</li>
 * </ol>
 * <p>
 * That way only the first two steps have to be done during event creation while the most expensive part, i.e. the
 * actual construction of the message, is only done on demand.
 * </p>
 */
public final class MessageFormatter
{
	private static final char DELIMITER_START = '{';
	private static final char DELIMITER_STOP = '}';
	private static final char ESCAPE_CHAR = '\\';

	static
	{
		new MessageFormatter(); // stfu
	}

	private MessageFormatter() {}

	/**
	 * Replace placeholders in the given messagePattern with arguments.
	 *
	 * @param messagePattern the message pattern containing placeholders.
	 * @param arguments      the arguments to be used to replace placeholders.
	 * @return the formatted message.
	 */
	public static String format(String messagePattern, String[] arguments)
	{
		if(messagePattern == null || arguments == null || arguments.length == 0)
		{
			return messagePattern;
		}

		StringBuilder result = new StringBuilder();
		int escapeCounter = 0;
		int currentArgument = 0;
		for(int i = 0; i < messagePattern.length(); i++)
		{
			char curChar = messagePattern.charAt(i);
			if(curChar == ESCAPE_CHAR)
			{
				escapeCounter++;
			}
			else
			{
				if(curChar == DELIMITER_START
						&& (i < messagePattern.length() - 1)
						&& (messagePattern.charAt(i + 1) == DELIMITER_STOP))
				{
					// write escaped escape chars
					int escapedEscapes = escapeCounter / 2;
					for(int j = 0; j < escapedEscapes; j++)
					{
						result.append(ESCAPE_CHAR);
					}

					if(escapeCounter % 2 == 1)
					{
						// i.e. escaped
						// write escaped escape chars
						result.append(DELIMITER_START);
						result.append(DELIMITER_STOP);
					}
					else
					{
						// unescaped
						if(currentArgument < arguments.length)
						{
							result.append(arguments[currentArgument]);
						}
						else
						{
							result.append(DELIMITER_START).append(DELIMITER_STOP);
						}
						currentArgument++;
					}
					// this is an optimization: charAt(i+1) has already been checked.
					// @cs-: ModifiedControlVariable
					i++;
					escapeCounter = 0;
					continue;
				}
				// any other char beside ESCAPE or DELIMITER_START/STOP-combo
				// write unescaped escape chars
				if(escapeCounter > 0)
				{
					for(int j = 0; j < escapeCounter; j++)
					{
						result.append(ESCAPE_CHAR);
					}
					escapeCounter = 0;
				}
				result.append(curChar);
			}
		}
		return result.toString();
	}

	/**
	 * Counts the number of unescaped placeholders in the given messagePattern.
	 *
	 * @param messagePattern the message pattern to be analyzed.
	 * @return the number of unescaped placeholders.
	 */
	public static int countArgumentPlaceholders(String messagePattern)
	{
		if(messagePattern == null)
		{
			return 0;
		}

		if(-1 == messagePattern.indexOf(DELIMITER_START))
		{
			// Special case: no placeholders at all.

			// This is an optimization because charAt checks bounds for every
			// single call while indexOf(char) isn't.

			// Big messages without placeholders will benefit from this shortcut.

			// the result of indexOf can't be used as start index in the loop
			// below because it could still be escaped.
			return 0;
		}

		int result = 0;
		boolean isEscaped = false;
		for(int i = 0; i < messagePattern.length(); i++)
		{
			char curChar = messagePattern.charAt(i);
			if(curChar == ESCAPE_CHAR)
			{
				isEscaped = !isEscaped;
			}
			else if(curChar == DELIMITER_START)
			{
				if(!isEscaped
						&& (i < messagePattern.length() - 1)
						&& (messagePattern.charAt(i + 1) == DELIMITER_STOP))
				{
					result++;
					// this is an optimization: charAt(i+1) has already been checked.
					// @cs-: ModifiedControlVariable
					i++;
				}
				isEscaped = false;
			}
			else
			{
				isEscaped = false;
			}
		}
		return result;
	}

	/**
	 * <p>This method returns a MessageFormatter.ArgumentResult which contains the arguments converted to String
	 * as well as an optional Throwable.</p>
	 *
	 * <p>If the last argument is a Throwable and is NOT used up by a placeholder in the message pattern it is returned
	 * in MessageFormatter.ArgumentResult.getThrowable() and won't be contained in the created String[].</p>
	 * <p>If it is used up getThrowable will return null even if the last argument was a Throwable!</p>
	 *
	 * @param messagePattern the message pattern that to be checked for placeholders.
	 * @param arguments      the argument array to be converted.
	 * @return a MessageFormatter.ArgumentResult containing the converted formatted message and optionally a Throwable.
	 */
	public static ArgumentResult evaluateArguments(String messagePattern, Object[] arguments)
	{
		if(arguments == null)
		{
			return null;
		}
		int argsCount = countArgumentPlaceholders(messagePattern);
		int resultArgCount = arguments.length;
		Throwable throwable = null;
		if(argsCount < arguments.length
				&& arguments[arguments.length - 1] instanceof Throwable)
		{
			throwable = (Throwable) arguments[arguments.length - 1];
			resultArgCount--;
		}

		String[] stringArgs;
		if(argsCount == 1 && throwable == null && arguments.length > 1)
		{
			// special case
			stringArgs = new String[1];
			stringArgs[0] = SafeString.toString(arguments,
					SafeString.StringWrapping.CONTAINED, SafeString.StringStyle.GROOVY, SafeString.MapStyle.GROOVY);
		}
		else
		{
			stringArgs = new String[resultArgCount];
			for(int i = 0; i < stringArgs.length; i++)
			{
				stringArgs[i] = SafeString.toString(arguments[i],
						SafeString.StringWrapping.CONTAINED, SafeString.StringStyle.GROOVY, SafeString.MapStyle.GROOVY);
			}
		}
		return new ArgumentResult(stringArgs, throwable);
	}

	/**
	 * <p>This is just a simple class containing the result of an evaluateArgument call. It's necessary because we need to
	 * return two results, i.e. the resulting String[] and the optional Throwable.</p>
	 *
	 * <p>This class is not Serializable because serializing a Throwable is generally a bad idea if the data is supposed
	 * to leave the current VM since it may result in ClassNotFoundExceptions if the given Throwable is not
	 * available/different in the deserializing VM.</p>
	 */
	@SuppressWarnings({"PMD.MethodReturnsInternalArray", "PMD.ArrayIsStoredDirectly"})
	public static class ArgumentResult
	{
		private final String[] arguments;
		private final Throwable throwable;

		public ArgumentResult(String[] arguments, Throwable throwable)
		{
			this.arguments = arguments;
			this.throwable = throwable;
		}

		public String[] getArguments()
		{
			return arguments;
		}

		public Throwable getThrowable()
		{
			return throwable;
		}

		@Override
		public String toString()
		{
			final StringBuilder result = new StringBuilder(500);
			result.append("ArgumentResult{arguments=");
			if(arguments == null)
			{
				result.append("null");
			}
			else
			{
				result.append('[');
				boolean isFirst = true;
				for (String current : arguments)
				{
					if (!isFirst)
					{
						result.append(", ");
					}
					else
					{
						isFirst = false;
					}
					if (current != null)
					{
						result.append('"').append(current).append('"');
					}
					else
					{
						result.append("null");
					}
				}
				result.append(']');
			}
			result.append(", throwable=").append(throwable).append('}');
			return result.toString();
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ArgumentResult that = (ArgumentResult) o;

			return Arrays.equals(arguments, that.arguments)
					&& (throwable != null ? throwable.equals(that.throwable) : that.throwable == null);
		}

		@Override
		public int hashCode()
		{
			int result = Arrays.hashCode(arguments);
			result = 31 * result + (throwable != null ? throwable.hashCode() : 0);
			return result;
		}
	}
}
