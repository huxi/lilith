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

public class MessageFormatter
{
	private static final char DELIM_START = '{';
	private static final char DELIM_STOP = '}';
	private static final char ESCAPE_CHAR = '\\';

//	public static String format(String message, String[] arguments)
//	{
//		if (arguments == null || message == null)
//		{
//			return message;
//		}
//		return org.slf4j.helpers.MessageFormatter.arrayFormat(message, arguments);
//	}

	public static String format(String messagePattern, String[] arguments)
	{
		if (arguments == null || messagePattern == null)
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
			else if (curChar == DELIM_START)
			{
				if (escapeCounter % 2 == 1)
				{
					// i.e. escaped
					// write escaped escape chars
					int escapedEscapes = escapeCounter / 2;
					for (int j = 0; j < escapedEscapes; j++)
					{
						result.append(ESCAPE_CHAR);
					}
					result.append(DELIM_START);
					escapeCounter = 0;
				}
				else
				{
					// unescaped start delimeter detected.
					boolean brokenPlaceholder = false;
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
							escapeCounter = 0;

							if (currentArgument < arguments.length)
							{
								result.append(arguments[currentArgument]);
							}
							else
							{
								result.append(DELIM_START).append(DELIM_STOP);
							}
							currentArgument++;
							i++;
						}
						else
						{
							brokenPlaceholder = true;
						}
					}
					else
					{
						brokenPlaceholder = true;
					}
					if (brokenPlaceholder)
					{
						// broken placeholder, leave rest of string untouched.
						// write unescaped escape chars
						for (int j = 0; j < escapeCounter; j++)
						{
							result.append(ESCAPE_CHAR);
						}
						result.append(messagePattern.substring(i, messagePattern.length()));
						return result.toString();
					}
				}
			}
			else
			{
				// any other char beside ESCAPE or DELIM_START
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
						else
						{
							// broken, unescaped DELIM_START without directly following DELIM_STOP
							return result;
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
				stringArgs[i]=argStr;
			}
		}
		return new ArgumentResult(stringArgs, throwable);
	}

	/**
	 * This method returns a MessageFormatter.Result which contains the formatted message as well as an optional
	 * Throwable.
	 * <p/>
	 * If the last argument is a Throwable and is NOT used by the message pattern it is returned
	 * in MessageFormatter.Result.getThrowable(). If it is used up getThrowable will return null even
	 * if the last argument was a Throwable!
	 *
	 * @return a MessageFormatter.Result containing the formatted message and optionally a Throwable.
	 */
/*
	public static Result evaluateMessage(String messagePattern, Object[] argArray)
	{

		Throwable throwable = null;
		if (argArray != null && argArray[argArray.length - 1] instanceof Throwable)
		{
			throwable = (Throwable) argArray[argArray.length - 1];
		}

		if (messagePattern == null)
		{
			return new Result(null, throwable);
		}

		if (argArray == null)
		{
			return new Result(messagePattern);
		}

		int i = 0;
		int len = messagePattern.length();
		int j;


		StringBuffer sbuf = new StringBuffer(messagePattern.length() + 50);

		for (int L = 0; L < argArray.length; L++)
		{

			boolean escaped = false;

			j = messagePattern.indexOf(DELIM_START, i);

			if (j == -1 || (j + 1 == len))
			{
				// no more variables
				if (i == 0)
				{ // this is a simple string
					return new Result(messagePattern, throwable);
				}
				else
				{ // add the tail string which contains no variables and return the result.
					sbuf.append(messagePattern.substring(i, messagePattern.length()));
					Result result;
					if (L <= argArray.length - 1)
					{ // there are args left, use throwable if available.
						result = new Result(sbuf.toString(), throwable);
					}
					else
					{ // all args are already used up
						result = new Result(sbuf.toString());
					}
					return result;
				}
			}
			else
			{
				char delimStop = messagePattern.charAt(j + 1);
				if (j > 0 && messagePattern.charAt(j - 1) == ESCAPE_CHAR)
				{
					escaped = true;
					if (j > 1 && messagePattern.charAt(j - 2) == ESCAPE_CHAR)
					{
						escaped = false;
						sbuf.append(messagePattern.substring(i, j - 2));
						i = j - 1;
					}
				}

				if (escaped)
				{
					L--; // DELIM_START was escaped, thus should not be incremented
					sbuf.append(messagePattern.substring(i, j - 1));
					sbuf.append(DELIM_START);
					i = j + 1;
				}
				else if ((delimStop != DELIM_STOP))
				{
					// invalid DELIM_START/DELIM_STOP pair
					sbuf.append(messagePattern.substring(i, messagePattern.length()));
					Result result;
					if (L < argArray.length - 1)
					{ // there are args left, use throwable if available.
						result = new Result(sbuf.toString(), throwable);
					}
					else
					{ // all args are already used up
						result = new Result(sbuf.toString());
					}
					return result;
				}
				else
				{
					// normal case
					sbuf.append(messagePattern.substring(i, j));
					sbuf.append(argArray[L]);
					i = j + 2;
				}
			}
		}
		// append the characters following the second {} pair.
		sbuf.append(messagePattern.substring(i, messagePattern.length()));
		return new Result(sbuf.toString()); // we used up all arguments
	}
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
			result.append("Result[throwable=").append(throwable);
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
