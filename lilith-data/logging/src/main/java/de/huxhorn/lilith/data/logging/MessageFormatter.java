/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
public class MessageFormatter
{
	private static final char DELIM_START = '{';
	private static final char DELIM_STOP = '}';
	private static final char ESCAPE_CHAR = '\\';

	public static final String RECURSION_PREFIX = "[...";
	public static final String RECURSION_SUFFIX = "...]";

	public static final String ERROR_PREFIX = "[!!!";
	public static final String ERROR_SEPARATOR = "=>";
	public static final String ERROR_MSG_SEPARATOR = ":";
	public static final String ERROR_SUFFIX = "!!!]";

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
				if(curChar == DELIM_START)
				{
					if(i < messagePattern.length() - 1)
					{
						if(messagePattern.charAt(i + 1) == DELIM_STOP)
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
								result.append(DELIM_START);
								result.append(DELIM_STOP);
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

		int delim = messagePattern.indexOf(DELIM_START);

		if(delim == -1)
		{
			// special case, no placeholders at all.
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
			else if(curChar == DELIM_START)
			{
				if(!isEscaped)
				{
					if(i < messagePattern.length() - 1)
					{
						if(messagePattern.charAt(i + 1) == DELIM_STOP)
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
	 * <p>This method returns a MessageFormatter.ArgumentResult which contains the arguments converted to String
	 * as well as an optional Throwable.</p>
	 * <p/>
	 * <p>If the last argument is a Throwable and is NOT used up by a placeholder in the message pattern it is returned
	 * in MessageFormatter.ArgumentResult.getThrowable() and won't be contained in the created String[].<br/>
	 * If it is used up getThrowable will return null even if the last argument was a Throwable!</p>
	 *
	 * @param messagePattern the message pattern that to be checked for placeholders.
	 * @param arguments      the argument array to be converted.
	 * @return a MessageFormatter.ArgumentResult containing the converted arformatted message and optionally a Throwable.
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
		if(argsCount < arguments.length)
		{
			if(arguments[arguments.length - 1] instanceof Throwable)
			{
				throwable = (Throwable) arguments[arguments.length - 1];
				resultArgCount--;
			}
		}

		String[] stringArgs;
		if(argsCount == 1 && throwable == null && arguments.length > 1)
		{
			// special case
			stringArgs = new String[1];
			stringArgs[0] = deepToString(arguments);
		}
		else
		{
			stringArgs = new String[resultArgCount];
			for(int i = 0; i < stringArgs.length; i++)
			{
				stringArgs[i] = deepToString(arguments[i]);
			}
		}
		return new ArgumentResult(stringArgs, throwable);
	}

	public static String deepToString(Object o)
	{
		if(o == null)
		{
			return null;
		}
		if(o instanceof String)
		{
			return (String) o;
		}
		StringBuilder str = new StringBuilder();
		Set<String> dejaVu = new HashSet<String>(); // that's actually a neat name ;)
		recursiveDeepToString(o, str, dejaVu);
		return str.toString();
	}

	/**
	 * This method performs a deep toString of the given Object.
	 * Primitive arrays are converted using their respective Arrays.toString methods while
	 * special handling is implemented for "container types", i.e. Object[], Map and Collection because those could
	 * contain themselves.
	 * <p/>
	 * dejaVu is used in case of those container types to prevent an endless recursion.
	 * <p/>
	 * It should be noted that neither AbstractMap.toString() nor AbstractCollection.toString() implement such a behavior.
	 * They only check if the container is directly contained in itself, but not if a contained container contains the
	 * original one. Because of that, Arrays.toString(Object[]) isn't safe either.
	 * Confusing? Just read the last paragraph again and check the respective toString() implementation.
	 * <p/>
	 * This means, in effect, that logging would produce a usable output even if an ordinary System.out.println(o)
	 * would produce a relatively hard-to-debug StackOverflowError.
	 *
	 * @param o      the Object to convert into a String
	 * @param str    the StringBuilder that o will be appended to
	 * @param dejaVu a list of container identities that were already used.
	 */
	private static void recursiveDeepToString(Object o, StringBuilder str, Set<String> dejaVu)
	{
		if(o == null)
		{
			str.append("null");
			return;
		}
		if(o instanceof String)
		{
			str.append(o);
			return;
		}

		Class oClass = o.getClass();
		if(oClass.isArray())
		{
			if(oClass == byte[].class)
			{
				str.append(Arrays.toString((byte[]) o));
			}
			else if(oClass == short[].class)
			{
				str.append(Arrays.toString((short[]) o));
			}
			else if(oClass == int[].class)
			{
				str.append(Arrays.toString((int[]) o));
			}
			else if(oClass == long[].class)
			{
				str.append(Arrays.toString((long[]) o));
			}
			else if(oClass == float[].class)
			{
				str.append(Arrays.toString((float[]) o));
			}
			else if(oClass == double[].class)
			{
				str.append(Arrays.toString((double[]) o));
			}
			else if(oClass == boolean[].class)
			{
				str.append(Arrays.toString((boolean[]) o));
			}
			else if(oClass == char[].class)
			{
				str.append(Arrays.toString((char[]) o));
			}
			else
			{
				// special handling of container Object[]
				String id = identityToString(o);
				if(dejaVu.contains(id))
				{
					str.append(RECURSION_PREFIX).append(id).append(RECURSION_SUFFIX);
				}
				else
				{
					dejaVu.add(id);
					Object[] oArray = (Object[]) o;
					str.append("[");
					boolean first = true;
					for(Object current : oArray)
					{
						if(first)
						{
							first = false;
						}
						else
						{
							str.append(", ");
						}
						recursiveDeepToString(current, str, new HashSet<String>(dejaVu));
					}
					str.append("]");
				}
				//str.append(Arrays.deepToString((Object[]) o));
			}
		}
		else if(o instanceof Map)
		{
			// special handling of container Map
			String id = identityToString(o);
			if(dejaVu.contains(id))
			{
				str.append(RECURSION_PREFIX).append(id).append(RECURSION_SUFFIX);
			}
			else
			{
				dejaVu.add(id);
				Map<?, ?> oMap = (Map<?, ?>) o;
				str.append("{");
				boolean isFirst = true;
				for(Map.Entry<?, ?> current : oMap.entrySet())
				{
					if(isFirst)
					{
						isFirst = false;
					}
					else
					{
						str.append(", ");
					}
					Object key = current.getKey();
					Object value = current.getValue();
					recursiveDeepToString(key, str, new HashSet<String>(dejaVu));
					str.append("=");
					recursiveDeepToString(value, str, new HashSet<String>(dejaVu));
				}
				str.append("}");
			}
		}
		else if(o instanceof Collection)
		{
			// special handling of container Collection
			String id = identityToString(o);
			if(dejaVu.contains(id))
			{
				str.append(RECURSION_PREFIX).append(id).append(RECURSION_SUFFIX);
			}
			else
			{
				dejaVu.add(id);
				Collection<?> oCol = (Collection<?>) o;
				str.append("[");
				boolean isFirst = true;
				for(Object current : oCol)
				{
					if(isFirst)
					{
						isFirst = false;
					}
					else
					{
						str.append(", ");
					}
					recursiveDeepToString(current, str, new HashSet<String>(dejaVu));
				}
				str.append("]");
			}
		}
		else if(o instanceof Date)
		{
			Date date = (Date) o;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			// I'll leave it like this for the moment... this could probably be optimized using ThreadLocal...
			str.append(format.format(date));
		}
		else
		{
			// it's just some other Object, we can only use toString().
			try
			{
				str.append(o.toString());
			}
			catch(Throwable t)
			{
				str.append(ERROR_PREFIX);
				str.append(identityToString(o));
				str.append(ERROR_SEPARATOR);
				String msg = t.getMessage();
				String className = t.getClass().getName();
				str.append(className);
				if(!className.equals(msg))
				{
					str.append(ERROR_MSG_SEPARATOR);
					str.append(msg);
				}
				str.append(ERROR_SUFFIX);
			}
		}
	}

	/**
	 * This method returns the same as if Object.toString() would not have been
	 * overridden in obj.
	 * <p/>
	 * Note that this isn't 100% secure as collisions can always happen with hash codes.
	 * <p/>
	 * Copied from Object.hashCode():
	 * As much as is reasonably practical, the hashCode method defined by
	 * class <tt>Object</tt> does return distinct integers for distinct
	 * objects. (This is typically implemented by converting the internal
	 * address of the object into an integer, but this implementation
	 * technique is not required by the
	 * Java<font size="-2"><sup>TM</sup></font> programming language.)
	 *
	 * @param obj the Object that is to be converted into an identity string.
	 * @return the identity string as also defined in Object.toString()
	 */
	public static String identityToString(Object obj)
	{
		if(obj == null)
		{
			return null;
		}
		return obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
	}

	/**
	 * <p>This is just a simple class containing the result of an evaluateArgument call. It's necessary because we need to
	 * return two results, i.e. the resulting String[] and the optional Throwable.</p>
	 * <p/>
	 * <p>This class is not Serializable because serializing a Throwable is generally a bad idea if the data is supposed
	 * to leave the current VM since it may result in ClassNotFoundExceptions if the given Throwable is not
	 * available/different in the deserializing VM.</p>
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
			if(arguments != null)
			{
				result.append("[");
				boolean isFirst = true;
				for(String current : arguments)
				{
					if(!isFirst)
					{
						result.append(", ");
					}
					else
					{
						isFirst = false;
					}
					if(current != null)
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
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;

			ArgumentResult result = (ArgumentResult) o;

			if(!Arrays.equals(arguments, result.arguments)) return false;
			if(throwable != null ? !throwable.equals(result.throwable) : result.throwable != null) return false;

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
