/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
 * Copyright 2007-2011 Joern Huxhorn
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
import java.util.HashSet;
import java.util.Set;

public class ThrowableInfo
	implements Serializable
{
	private static final long serialVersionUID = -6320441996003349426L;

	private static final int REGULAR_EXCEPTION_INDENT = 1;
	private static final int SUPPRESSED_EXCEPTION_INDENT = 2;
	private static final String CAUSED_BY = "Caused by: ";
	private static final String SUPPRESSED = "\tSuppressed: ";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final char TAB = '\t';

	private static final String WRAPPED_BY = "Wrapped by: ";

	private String name;
	private String message;
	private ThrowableInfo cause;
	private ThrowableInfo[] suppressed;
	private ExtendedStackTraceElement[] stackTrace;
	private int omittedElements;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public ExtendedStackTraceElement[] getStackTrace()
	{
		return stackTrace;
	}

	public void setStackTrace(ExtendedStackTraceElement[] stackTrace)
	{
		this.stackTrace = stackTrace;
	}

	public ThrowableInfo[] getSuppressed()
	{
		return suppressed;
	}

	public void setSuppressed(ThrowableInfo[] suppressed)
	{
		this.suppressed = suppressed;
	}

	public ThrowableInfo getCause()
	{
		return cause;
	}

	public void setCause(ThrowableInfo cause)
	{
		this.cause = cause;
	}

	public int getOmittedElements()
	{
		return omittedElements;
	}

	public void setOmittedElements(int omittedElements)
	{
		this.omittedElements = omittedElements;
	}

	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		final ThrowableInfo that = (ThrowableInfo) o;
		if(omittedElements != that.omittedElements) return false;
		if(name != null ? !name.equals(that.name) : that.name != null) return false;
		if(message != null ? !message.equals(that.message) : that.message != null) return false;
		if(!Arrays.equals(stackTrace, that.stackTrace)) return false;
		if(!Arrays.equals(suppressed, that.suppressed)) return false;
		return !(cause != null ? !cause.equals(that.cause) : that.cause != null);
	}

	public int hashCode()
	{
		int result = omittedElements;
		result = 29 * result + (name != null ? name.hashCode() : 0);
		result = 29 * result + (message != null ? message.hashCode() : 0);
		result = 29 * result + (suppressed != null ? Arrays.hashCode(suppressed) : 0);
		result = 29 * result + (cause != null ? cause.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return toString(false);
	}
	public String toString(boolean verbose)
	{
		StringBuilder result=new StringBuilder();
		result.append("ThrowableInfo[");
		result.append("name=").append(name);
		if(message != null)
		{
			result.append(", message=\"").append(message).append("\"");
		}
		if(stackTrace != null)
		{
			if(verbose)
			{
				result.append(", stackTrace=").append(Arrays.toString(stackTrace));
			}
			else
			{
				result.append(", stackTrace.length=").append(stackTrace.length);
			}
		}
		if(omittedElements>0)
		{
			result.append(", omittedElements=").append(omittedElements);
		}
		if(suppressed!=null)
		{
			result.append(", suppressed=[");
			boolean first = true;
			for(ThrowableInfo current : suppressed)
			{
				if(first)
				{
					first = false;
				}
				else
				{
					result.append(", ");
				}
				result.append(current.toString(verbose));
			}
			result.append("]");
		}
		if(cause!=null)
		{
			result.append(", cause=").append(cause.toString(verbose));
		}

		result.append("]");
		return result.toString();
	}

	/**
	 * Returns a string representation similar to printStackTrace.
	 *
	 * @param throwableInfo The ThrowableInfo to be transformed into a String.
	 * @param extended whether or not extended StackTraceElement information should be appended.
	 * @return the String representation of the given ThrowableInfo.
	 */
	public static String asString(ThrowableInfo throwableInfo, boolean extended)
	{
		StringBuilder sb = new StringBuilder();

		Set<ThrowableInfo> dejaVu = new HashSet<ThrowableInfo>();
		recursiveAppend(sb, dejaVu, null, REGULAR_EXCEPTION_INDENT, throwableInfo, extended);

		return sb.toString();
	}

	private static void recursiveAppend(StringBuilder sb, Set<ThrowableInfo> dejaVu, String prefix, int indent, ThrowableInfo throwableInfo, boolean extended)
	{
		if(throwableInfo == null)
		{
			return;
		}
		if(dejaVu.contains(throwableInfo))
		{
			sb.append("[CIRCULAR REFERENCE]");
			return;
		}
		dejaVu.add(throwableInfo);

		if(prefix != null)
		{
			sb.append(prefix);
		}
		String name = throwableInfo.getName();
		if(name != null)
		{
			sb.append(name);
			String message = throwableInfo.getMessage();
			if(message != null && !name.equals(message))
			{
				sb.append(": ").append(throwableInfo.getMessage());
			}
		}
		else
		{
			sb.append(throwableInfo.getMessage());
		}
		sb.append(LINE_SEPARATOR);

		appendSTEArray(sb, indent, throwableInfo, extended);

		ThrowableInfo[] suppressed = throwableInfo.getSuppressed();
		if(suppressed != null)
		{
			for(ThrowableInfo current : suppressed)
			{
				recursiveAppend(sb, dejaVu, SUPPRESSED, SUPPRESSED_EXCEPTION_INDENT, current, extended);
			}
		}
		recursiveAppend(sb, dejaVu, CAUSED_BY, indent, throwableInfo.getCause(), extended);
	}

	private static void appendSTEArray(StringBuilder sb, int indentLevel, ThrowableInfo throwableInfo, boolean extended)
	{
		ExtendedStackTraceElement[] steArray = throwableInfo.getStackTrace();

		int commonFrames = throwableInfo.getOmittedElements();

		if(steArray != null)
		{
			for(int i = 0; i < steArray.length - commonFrames; i++)
			{
				ExtendedStackTraceElement ste = steArray[i];
				for(int j = 0; j < indentLevel; j++)
				{
					sb.append(TAB);
				}
				sb.append(ste.toString(extended));
				sb.append(LINE_SEPARATOR);
			}
		}

		if(commonFrames > 0)
		{
			for(int j = 0; j < indentLevel; j++)
			{
				sb.append(TAB);
			}
			sb.append("... ").append(commonFrames).append(" more").append(LINE_SEPARATOR);
		}
	}
}
