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

import java.io.Serializable;

/**
 * Replacement for java.lang.StackTraceElement containing additional infos about
 * version and code location of the given class/package of the class.
 * Those informations will be available in the next version of logback, supposedly
 * 0.9.10.
 */
public class ExtendedStackTraceElement
	implements Serializable, Cloneable
{
	private static final long serialVersionUID = 4907919529165316605L;

	public static final int NATIVE_METHOD = -2;

	private String className;
	private String methodName;
	private String fileName;
	private int lineNumber;
	private String codeLocation;
	private String version;
	private boolean exact;

	public ExtendedStackTraceElement()
	{
		lineNumber = -1;
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
		return lineNumber == NATIVE_METHOD;
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

	public String toString()
	{
		return toString(false);
	}

	public String getExtendedString()
	{
		if(codeLocation != null || version != null)
		{
			StringBuilder result = new StringBuilder();
			if(exact)
			{
				result.append("[");
			}
			else
			{
				result.append("~[");
			}
			if(codeLocation != null)
			{
				result.append(codeLocation);
			}
			result.append(":");
			if(version != null)
			{
				result.append(version);
			}
			result.append("]");
			return result.toString();
		}
		return null;
	}

	public String toString(boolean extended)
	{
		StringBuilder result = new StringBuilder();

		result.append(className).append(".").append(methodName);
		if(isNativeMethod())
		{
			result.append("(Native Method)");
		}
		if(fileName != null)
		{
			result.append("(").append(fileName);
			if(lineNumber >= 0)
			{
				result.append(":").append(lineNumber);
			}
			result.append(")");
		}
		else
		{
			result.append("(Unknown Source)");
		}
		if(extended)
		{
			String extendedStr = getExtendedString();
			// same as logback
			if(extendedStr != null)
			{
				result.append(" ").append(extendedStr);
			}
		}
		return result.toString();
	}
}
