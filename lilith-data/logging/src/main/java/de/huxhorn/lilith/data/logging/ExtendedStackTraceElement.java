/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2016 Joern Huxhorn
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
 * Copyright 2007-2016 Joern Huxhorn
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
import java.util.Objects;

/**
 * Replacement for java.lang.StackTraceElement containing additional info about
 * version and code location of the given class.
 */
public class ExtendedStackTraceElement
	implements Serializable, Cloneable
{
	private static final long serialVersionUID = 4907919529165316605L;

	public static final int UNKNOWN_SOURCE_LINE_NUMBER = -1;
	public static final int NATIVE_METHOD_LINE_NUMBER = -2;

	private static final String NATIVE_METHOD_STRING = "Native Method";
	private static final String UNKNOWN_SOURCE_STRING = "Unknown Source";
	private static final String NA_PLACEHOLDER = "na";
	private static final String EXTENDED_EXACT_PREFIX = "[";
	private static final String EXTENDED_INEXACT_PREFIX = "~[";
	private static final String EXTENDED_POSTFIX = "]";
	private static final char SEPARATOR_CHAR = ':';

	private String className;
	private String methodName;
	private String fileName;
	private int lineNumber;
	private String codeLocation;
	private String version;
	private boolean exact;

	public ExtendedStackTraceElement()
	{
		lineNumber = UNKNOWN_SOURCE_LINE_NUMBER;
	}

	public ExtendedStackTraceElement(StackTraceElement ste)
	{
		this(ste.getClassName(), ste.getMethodName(), ste.getFileName(), ste.getLineNumber());
	}

	public ExtendedStackTraceElement(String className, String methodName, String fileName, int lineNumber)
	{
		this(className, methodName, fileName, lineNumber, null, null, false);
	}

	public ExtendedStackTraceElement(String className, String methodName, String fileName, int lineNumber, String codeLocation, String version, boolean exact)
	{
		this.className = className;
		this.methodName = methodName;
		this.fileName = fileName;
		this.lineNumber = lineNumber;
		this.codeLocation = codeLocation;
		this.version = version;
		this.exact = exact;
	}

	public boolean isNativeMethod()
	{
		return lineNumber == NATIVE_METHOD_LINE_NUMBER;
	}

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public String getMethodName()
	{
		return methodName;
	}

	public void setMethodName(String methodName)
	{
		this.methodName = methodName;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}

	public String getCodeLocation()
	{
		return codeLocation;
	}

	public void setCodeLocation(String codeLocation)
	{
		this.codeLocation = codeLocation;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public boolean isExact()
	{
		return exact;
	}

	public void setExact(boolean exact)
	{
		this.exact = exact;
	}

	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		ExtendedStackTraceElement that = (ExtendedStackTraceElement) o;

		if(exact != that.exact) return false;
		if(lineNumber != that.lineNumber) return false;
		if(className != null ? !className.equals(that.className) : that.className != null) return false;
		if(codeLocation != null ? !codeLocation.equals(that.codeLocation) : that.codeLocation != null) return false;
		if(fileName != null ? !fileName.equals(that.fileName) : that.fileName != null) return false;
		if(methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;
		if(version != null ? !version.equals(that.version) : that.version != null) return false;

		return true;
	}

	public int hashCode()
	{
		int result;
		result = (className != null ? className.hashCode() : 0);
		result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
		result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
		result = 31 * result + lineNumber;
		result = 31 * result + (codeLocation != null ? codeLocation.hashCode() : 0);
		result = 31 * result + (version != null ? version.hashCode() : 0);
		result = 31 * result + (exact ? 1 : 0);
		return result;
	}

	public StackTraceElement getStackTraceElement()
	{
		return new StackTraceElement(className, methodName, fileName, lineNumber);
	}

	public ExtendedStackTraceElement clone()
		throws CloneNotSupportedException
	{
		return (ExtendedStackTraceElement) super.clone();
	}

	public String getExtendedString()
	{
		if(codeLocation != null || version != null)
		{
			return appendExtended(new StringBuilder()).toString();
		}
		return null;
	}

	private StringBuilder appendExtended(StringBuilder stringBuilder)
	{
		if (exact)
		{
			stringBuilder.append(EXTENDED_EXACT_PREFIX);
		}
		else
		{
			stringBuilder.append(EXTENDED_INEXACT_PREFIX);
		}

		if (codeLocation != null)
		{
			stringBuilder.append(codeLocation);
		}
		else
		{
			stringBuilder.append(NA_PLACEHOLDER);
		}

		stringBuilder.append(SEPARATOR_CHAR);

		if (version != null)
		{
			stringBuilder.append(version);
		}
		else
		{
			stringBuilder.append(NA_PLACEHOLDER);
		}

		stringBuilder.append(EXTENDED_POSTFIX);

		return stringBuilder;
	}

	/**
	 * Returns the string representation of this instance, but without extended info.
	 *
	 * Shortcut for toString(false).
	 *
	 * @return String representation of this instance, but without extended info.
	 */
	public String toString()
	{
		return toString(false);
	}

	/**
	 * Returns the string representation of this instance.
	 * Extended info will be included if the parameter extended is true and info is available.
	 *
	 * @param extended Whether or not extended info should be included, if available.
	 * @return String representation of this instance.
	 */
	public String toString(boolean extended)
	{
		return appendTo(new StringBuilder(), extended).toString();
	}

	/**
	 * Appends this instance to the given StringBuilder.
	 *
	 * @param stringBuilder the StringBuilder to append this instance to.
	 * @param extended Whether or not extended info should be included, if available.
	 * @return the given StringBuilder instance.
	 * @throws NullPointerException if stringBuilder is null.
	 */
	public StringBuilder appendTo(StringBuilder stringBuilder, boolean extended)
	{
		Objects.requireNonNull(stringBuilder, "stringBuilder must not be null!");
		stringBuilder.append(className).append(".").append(methodName);
		if(isNativeMethod())
		{
			stringBuilder.append("(").append(NATIVE_METHOD_STRING).append(")");
		}
		else if(fileName != null)
		{
			stringBuilder.append("(").append(fileName);
			if(lineNumber >= 0)
			{
				stringBuilder.append(SEPARATOR_CHAR).append(lineNumber);
			}
			stringBuilder.append(")");
		}
		else
		{
			stringBuilder.append("(").append(UNKNOWN_SOURCE_STRING).append(")");
		}
		if(extended)
		{
			if(codeLocation != null || version != null)
			{
				stringBuilder.append(' ');
				appendExtended(stringBuilder);
			}
		}
		return stringBuilder;
	}



	public static ExtendedStackTraceElement parseStackTraceElement(final String ste)
	{
		if(ste == null)
		{
			return null;
		}
		int idx = ste.lastIndexOf('(');
		if(idx < 0)
		{
			return null; // not a ste
		}
		int endIdx = ste.lastIndexOf(')');
		if(endIdx < 0)
		{
			return null; // not a ste
		}

		String classAndMethod = ste.substring(0, idx);
		String source = ste.substring(idx + 1, endIdx);
		idx = classAndMethod.lastIndexOf('.');
		if(idx < 0)
		{
			return null; // not a ste
		}
		String clazz = classAndMethod.substring(0, idx);
		String method = classAndMethod.substring(idx + 1, classAndMethod.length());
		idx = source.lastIndexOf(SEPARATOR_CHAR);
		String file = null;
		int lineNumber = UNKNOWN_SOURCE_LINE_NUMBER;
		if(idx != -1)
		{
			file = source.substring(0, idx);
			lineNumber = Integer.parseInt(source.substring(idx + 1, source.length()));
		}
		else
		{
			if(source.equals(NATIVE_METHOD_STRING))
			{
				lineNumber = ExtendedStackTraceElement.NATIVE_METHOD_LINE_NUMBER;
			}
			else if(!source.equals(UNKNOWN_SOURCE_STRING))
			{
				file = source;
			}
		}

		if(endIdx + 2 < ste.length())
		{
			String remainder = ste.substring(endIdx + 2);
			int vEndIdx = remainder.lastIndexOf(']');
			if(vEndIdx >= 0)
			{
				boolean exact = false;
				String versionStr = null;
				if (remainder.startsWith(EXTENDED_EXACT_PREFIX))
				{
					exact = true;
					versionStr = remainder.substring(EXTENDED_EXACT_PREFIX.length(), vEndIdx);
				}
				else if (remainder.startsWith(EXTENDED_INEXACT_PREFIX))
				{
					exact = false;
					versionStr = remainder.substring(EXTENDED_INEXACT_PREFIX.length(), vEndIdx);
				}
				if (versionStr != null)
				{
					int colonIdx = versionStr.indexOf(SEPARATOR_CHAR);
					if (colonIdx > -1)
					{
						String codeLocation = versionStr.substring(0, colonIdx);
						String version = versionStr.substring(colonIdx + 1);
						if ("".equals(codeLocation) || NA_PLACEHOLDER.equals(codeLocation))
						{
							codeLocation = null;
						}
						if ("".equals(version) || NA_PLACEHOLDER.equals(version))
						{
							version = null;
						}
						return new ExtendedStackTraceElement(clazz, method, file, lineNumber, codeLocation, version, exact);
					}
				}
			}
		}

		return new ExtendedStackTraceElement(clazz, method, file, lineNumber);
	}


}
