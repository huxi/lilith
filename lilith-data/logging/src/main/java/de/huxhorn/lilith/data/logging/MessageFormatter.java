/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
package de.huxhorn.lilith.data.logging;

import java.util.Arrays;

/**
 * <p>Replacement for org.slf4j.helpers.MessageFormatter.<p/>
 * <p>
 * In contrast to the mentioned class, the formatting of message pattern and arguments into the actual message
 * is split into three parts:
 * <p/>
 * <ol>
 * <li>Counting of placeholders in the message pattern (cheap)</li>
 * <li>Conversion of argument array into an ArgumentResult, containing the arguments converted to String as well as
 * an optional Throwable if available (relatively cheap)</li>
 * <li>Replacement of placeholders in a message pattern with arguments given as String[]. (most expensive)</li>
 * </ol>
 * <p/>
 * <p>
 * That way only the first two steps have to be done during event creation while the most expensive part, i.e. the
 * actual construction of the message, is only done on demand.
 * <p/>
 */
public class MessageFormatter
{
	private static final char DELIM_START = '{';
	private static final char DELIM_STOP = '}';
	private static final char ESCAPE_CHAR = '\\';

	/**
	 * Replace placeholders in the given messagePattern with arguments.
	 *
	 * @param messagePattern the message pattern containing placeholders.
	 * @param arguments	  the arguments to be used to replace placeholders.
	 * @return the formatted message.
	 */
	public static String format(String messagePattern, String[] arguments)
	{
		if (messagePattern == null || arguments == null || arguments.length == 0)
		{
			return messagePattern;
		}

		StringBuilder result = new StringBuilder();
		int escapeCounter = 0;
		int currentArgument = 0;
		for (int i = 0; i < messagePattern.length(); i++)
		{
			char curChar = messagePattern.charAt(i);
			if (curChar == ESCAPE_CHAR)
			{
				escapeCounter++;
			}
			else
			{
				if (curChar == DELIM_START)
				{
					if (i < messagePattern.length() - 1)
					{
						if (messagePattern.charAt(i + 1) == DELIM_STOP)
						{
							// write escaped escape chars
							int escapedEscapes = escapeCounter / 2;
							for (int j = 0; j < escapedEscapes; j++)
							{
								result.append(ESCAPE_CHAR);
							}

							if (escapeCounter % 2 == 1)
							{
								// i.e. escaped
								// write escaped escape chars
								result.append(DELIM_START);
								result.append(DELIM_STOP);
							}
							else
							{
								// unescaped
								if (currentArgument < arguments.length)
								{
									result.append(arguments[currentArgument]);
								}
								else
								{
									result.append(DELIM_START).append(DELIM_STOP);
								}
								currentArgument++;
							}
							i++;
							escapeCounter = 0;
							continue;
						}
					}
				}
				// any other char beside ESCAPE or DELIM_START/STOP-combo
				// write unescaped escape chars
				if (escapeCounter > 0)
				{
					for (int j = 0; j < escapeCounter; j++)
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
		if (messagePattern == null)
		{
			return 0;
		}

		int delim = messagePattern.indexOf(DELIM_START);

		if (delim == -1)
		{
			// special case, no placeholders at all.
			return 0;
		}
		int result = 0;
		boolean isEscaped = false;
		for (int i = 0; i < messagePattern.length(); i++)
		{
			char curChar = messagePattern.charAt(i);
			if (curChar == ESCAPE_CHAR)
			{
				isEscaped = !isEscaped;
			}
			else if (curChar == DELIM_START)
			{
				if (!isEscaped)
				{
					if (i < messagePattern.length() - 1)
					{
						if (messagePattern.charAt(i + 1) == DELIM_STOP)
						{
							result++;
							i++;
						}
					}
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
	 * This method returns a MessageFormatter.ArgumentResult which contains the arguments converted to String
	 * as well as an optional Throwable.
	 * <p/>
	 * If the last argument is a Throwable and is NOT used up by a placeholder in the message pattern it is returned
	 * in MessageFormatter.ArgumentResult.getThrowable() and won't be contained in the created String[].
	 * <p/>
	 * If it is used up getThrowable will return null even if the last argument was a Throwable!
	 *
	 * @param messagePattern the message pattern that to be checked for placeholders.
	 * @param arguments	  the argument array to be converted.
	 * @return a MessageFormatter.ArgumentResult containing the converted arformatted message and optionally a Throwable.
	 */
	public static ArgumentResult evaluateArguments(String messagePattern, Object[] arguments)
	{
		if (arguments == null)
		{
			return null;
		}
		int argsCount = countArgumentPlaceholders(messagePattern);
		int resultArgCount = arguments.length;
		Throwable throwable = null;
		if (argsCount < arguments.length)
		{
			if (arguments[arguments.length - 1] instanceof Throwable)
			{
				throwable = (Throwable) arguments[arguments.length - 1];
				resultArgCount--;
			}
		}

		String[] stringArgs = new String[resultArgCount];
		for (int i = 0; i < stringArgs.length; i++)
		{
			Object o = arguments[i];
			if (o != null)
			{
				String argStr;
				if (o.getClass().isArray())
				{
					if (o instanceof byte[])
					{
						argStr = Arrays.toString((byte[]) o);
					}
					else if (o instanceof short[])
					{
						argStr = Arrays.toString((short[]) o);
					}
					else if (o instanceof int[])
					{
						argStr = Arrays.toString((int[]) o);
					}
					else if (o instanceof long[])
					{
						argStr = Arrays.toString((long[]) o);
					}
					else if (o instanceof float[])
					{
						argStr = Arrays.toString((float[]) o);
					}
					else if (o instanceof double[])
					{
						argStr = Arrays.toString((double[]) o);
					}
					else if (o instanceof boolean[])
					{
						argStr = Arrays.toString((boolean[]) o);
					}
					else if (o instanceof char[])
					{
						argStr = Arrays.toString((char[]) o);
					}
					else
					{
						argStr = Arrays.deepToString((Object[]) o);
					}
				}
				else if (o instanceof String)
				{
					argStr = (String) o;
				}
				else
				{
					argStr = o.toString();
				}
				stringArgs[i] = argStr;
			}
		}
		return new ArgumentResult(stringArgs, throwable);
	}

	/**
	 * This is just a simple class containing the result of an evaluateArgument call. It's necessary because we need to
	 * return two results, i.e. the resulting String[] and the optional Throwable.
	 * <p/>
	 * This class is not Serializable because serializing a Throwable is generally a bad idea if the data is supposed
	 * to leave the current VM since it may result in ClassNotFoundExceptions if the given Throwable is not
	 * available/different in the deserializing VM.
	 */
	public static class ArgumentResult
	{
		private Throwable throwable;
		private String[] arguments;

		public ArgumentResult(String[] arguments, Throwable throwable)
		{
			this.throwable = throwable;
			this.arguments = arguments;
		}

		public Throwable getThrowable()
		{
			return throwable;
		}

		public String[] getArguments()
		{
			return arguments;
		}

		@Override
		public String toString()
		{
			StringBuilder result = new StringBuilder();
			result.append("ArgumentResult[throwable=").append(throwable);
			result.append(", arguments=");
			if (arguments != null)
			{
				result.append("[");
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
						result.append("'").append(current).append("'");
					}
					else
					{
						result.append("null");
					}
				}
				result.append("]");
			}
			return result.toString();
		}

		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ArgumentResult result = (ArgumentResult) o;

			if (!Arrays.equals(arguments, result.arguments)) return false;
			if (throwable != null ? !throwable.equals(result.throwable) : result.throwable != null) return false;

			return true;
		}

		public int hashCode()
		{
			int result;
			result = (throwable != null ? throwable.hashCode() : 0);
			result = 31 * result + (arguments != null ? Arrays.hashCode(arguments) : 0);
			return result;
		}
	}
}
