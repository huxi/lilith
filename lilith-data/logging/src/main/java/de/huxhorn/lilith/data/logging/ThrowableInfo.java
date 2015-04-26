/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2014 Joern Huxhorn
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
 * Copyright 2007-2014 Joern Huxhorn
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
	implements Serializable, Cloneable
{
	private static final long serialVersionUID = -6320441996003349426L;

	public static final String CAUSED_BY_PREFIX = "Caused by: ";
	public static final String SUPPRESSED_PREFIX = "Suppressed: ";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private String name;
	private String message;
	private ExtendedStackTraceElement[] stackTrace;
	private int omittedElements;
	private ThrowableInfo[] suppressed;
	private ThrowableInfo cause;

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
	public ThrowableInfo clone() throws CloneNotSupportedException
	{
		ThrowableInfo result = (ThrowableInfo) super.clone();

		if(stackTrace != null)
		{
			result.stackTrace = new ExtendedStackTraceElement[stackTrace.length];
			for(int i=0 ; i<stackTrace.length ; i++)
			{
				ExtendedStackTraceElement current = stackTrace[i];
				if(current != null)
				{
					result.stackTrace[i] = current.clone();
				}
			}
		}

		if(suppressed != null)
		{
			result.suppressed = new ThrowableInfo[suppressed.length];
			for(int i=0 ; i<suppressed.length ; i++)
			{
				ThrowableInfo current = suppressed[i];
				if(current != null)
				{
					result.suppressed[i] = current.clone();
				}
			}
		}

		if(cause != null)
		{
			result.cause = cause.clone();
		}

		return result;
	}

	@Override
	public String toString()
	{
		return toString(true);
	}

	/**
	 * Returns a string representation similar to printStackTrace.
	 *
	 * @param extended whether or not extended StackTraceElement information should be appended.
	 * @return the String representation of this ThrowableInfo.
	 */
	public String toString(boolean extended)
	{
		return appendTo(null, extended).toString();
	}

	public StringBuilder appendTo(StringBuilder result, boolean extended)
	{
		if(result == null)
		{
			result = new StringBuilder();
		}
		Set<ThrowableInfo> dejaVu = new HashSet<>();
		recursiveAppend(result, dejaVu, null, 0, this, extended);

		return result;
	}

	private static void recursiveAppend(StringBuilder sb, Set<ThrowableInfo> dejaVu, String prefix, int indent, ThrowableInfo throwableInfo, boolean extended)
	{
		if(throwableInfo == null)
		{
			return;
		}

		appendIndent(sb, indent);
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

		if(dejaVu.contains(throwableInfo))
		{
			sb.append("[CIRCULAR REFERENCE]");
			return;
		}
		dejaVu.add(throwableInfo);
		sb.append(LINE_SEPARATOR);

		appendSTEArray(sb, indent + 1, throwableInfo, extended);

		ThrowableInfo[] suppressed = throwableInfo.getSuppressed();
		if(suppressed != null)
		{
			for(ThrowableInfo current : suppressed)
			{
				recursiveAppend(sb, dejaVu, SUPPRESSED_PREFIX, indent + 1, current, extended);
			}
		}
		recursiveAppend(sb, dejaVu, CAUSED_BY_PREFIX, indent, throwableInfo.getCause(), extended);
	}

	private static void appendIndent(StringBuilder sb, int indent)
	{
		for(int i=0;i<indent;i++)
		{
			sb.append("\t");
		}
	}

	private static void appendSTEArray(StringBuilder sb, int indentLevel, ThrowableInfo throwableInfo, boolean extended)
	{
		ExtendedStackTraceElement[] steArray = throwableInfo.getStackTrace();

		int commonFrames = throwableInfo.getOmittedElements();

		if(steArray != null)
		{
			for(int i = 0; i < steArray.length; i++)
			{
				ExtendedStackTraceElement ste = steArray[i];
				if(ste != null)
				{
					appendIndent(sb, indentLevel);
					sb.append("at ");
					ste.appendTo(sb, extended);
					sb.append(LINE_SEPARATOR);
				}
			}
		}

		if(commonFrames > 0)
		{
			appendIndent(sb, indentLevel);
			sb.append("... ").append(commonFrames).append(" more").append(LINE_SEPARATOR);
		}
	}

}
