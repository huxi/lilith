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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Replacement for java.lang.StackTraceElement containing additional info about
 * version and code location of the given class.
 */
public class ExtendedStackTraceElement
	implements Serializable, Cloneable
{
	private static final long serialVersionUID = -6954579590347369344L;

	public static final ExtendedStackTraceElement[] ARRAY_PROTOTYPE = new ExtendedStackTraceElement[0];

	public static final int UNKNOWN_SOURCE_LINE_NUMBER = -1;
	public static final int NATIVE_METHOD_LINE_NUMBER = -2;

	private static final String NATIVE_METHOD_STRING = "Native Method";
	private static final String UNKNOWN_SOURCE_STRING = "Unknown Source";
	private static final String NA_PLACEHOLDER = "na";
	private static final String EXTENDED_EXACT_PREFIX = "[";
	private static final String EXTENDED_INEXACT_PREFIX = "~[";
	private static final String EXTENDED_POSTFIX = "]";
	private static final char SEPARATOR_CHAR = ':';
	private static final char MODULE_SEPARATOR_CHAR = '/';
	private static final char MODULE_VERSION_SEPARATOR_CHAR = '@';

	private static final Method GET_CLASS_LOADER_NAME;
	private static final Method GET_MODULE_NAME;
	private static final Method GET_MODULE_VERSION;
	private static final Constructor<StackTraceElement> FULL_CTOR;

	static
	{
		{
			Method method = null;
			try
			{
				//noinspection JavaReflectionMemberAccess
				method = StackTraceElement.class.getMethod("getClassLoaderNameFrom");
			}
			catch (NoSuchMethodException e)
			{
				// ignore
			}
			GET_CLASS_LOADER_NAME = method;
		}

		{
			Method method = null;
			try
			{
				//noinspection JavaReflectionMemberAccess
				method = StackTraceElement.class.getMethod("getModuleName");
			}
			catch (NoSuchMethodException e)
			{
				// ignore
			}
			GET_MODULE_NAME = method;
		}

		{
			Method method = null;
			try
			{
				//noinspection JavaReflectionMemberAccess
				method = StackTraceElement.class.getMethod("getModuleVersion");
			}
			catch (NoSuchMethodException e)
			{
				// ignore
			}
			GET_MODULE_VERSION = method;
		}

		{
			Constructor<StackTraceElement> ctor = null;
			try
			{
				//noinspection JavaReflectionMemberAccess
				ctor = StackTraceElement.class.getConstructor(
						String.class, // classLoaderName
						String.class, // moduleName
						String.class, // moduleVersion
						String.class, // declaringClass
						String.class, // methodName
						String.class, // fileName
						int.class // lineNumber
				);
			}
			catch (NoSuchMethodException e)
			{
				// ignore
			}
			FULL_CTOR = ctor;
		}
	}

	private String className;
	private String methodName;
	private String fileName;
	private int lineNumber;

	// Logback extended info
	private String codeLocation;
	private String version;
	private boolean exact;

	// Java 9
	private String classLoaderName;
	private String moduleName;
	private String moduleVersion;

	public ExtendedStackTraceElement()
	{
		lineNumber = UNKNOWN_SOURCE_LINE_NUMBER;
	}

	public ExtendedStackTraceElement(StackTraceElement stackTraceElement)
	{
		Objects.requireNonNull(stackTraceElement, "stackTraceElement must not be null!");
		this.className = stackTraceElement.getClassName();
		this.methodName = stackTraceElement.getMethodName();
		this.fileName = stackTraceElement.getFileName();
		this.lineNumber = stackTraceElement.getLineNumber();
		this.classLoaderName = getClassLoaderNameFrom(stackTraceElement);
		this.moduleName = getModuleNameFrom(stackTraceElement);
		this.moduleVersion= getModuleVersionFrom(stackTraceElement);
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

	@SuppressWarnings("WeakerAccess")
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

	public String getClassLoaderName()
	{
		return classLoaderName;
	}

	public void setClassLoaderName(String classLoaderName)
	{
		this.classLoaderName = classLoaderName;
	}

	public String getModuleName()
	{
		return moduleName;
	}

	public void setModuleName(String moduleName)
	{
		this.moduleName = moduleName;
	}

	public String getModuleVersion()
	{
		return moduleVersion;
	}

	public void setModuleVersion(String moduleVersion)
	{
		this.moduleVersion = moduleVersion;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ExtendedStackTraceElement that = (ExtendedStackTraceElement) o;

		return lineNumber == that.lineNumber
				&& exact == that.exact
				&& (className != null ? className.equals(that.className) : that.className == null)
				&& (methodName != null ? methodName.equals(that.methodName) : that.methodName == null)
				&& (fileName != null ? fileName.equals(that.fileName) : that.fileName == null)
				&& (codeLocation != null ? codeLocation.equals(that.codeLocation) : that.codeLocation == null)
				&& (version != null ? version.equals(that.version) : that.version == null)
				&& (classLoaderName != null ? classLoaderName.equals(that.classLoaderName) : that.classLoaderName == null)
				&& (moduleName != null ? moduleName.equals(that.moduleName) : that.moduleName == null)
				&& (moduleVersion != null ? moduleVersion.equals(that.moduleVersion) : that.moduleVersion == null);
	}

	@Override
	public int hashCode()
	{
		int result = className != null ? className.hashCode() : 0;
		result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
		result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
		result = 31 * result + lineNumber;
		result = 31 * result + (codeLocation != null ? codeLocation.hashCode() : 0);
		result = 31 * result + (version != null ? version.hashCode() : 0);
		result = 31 * result + (exact ? 1 : 0);
		result = 31 * result + (classLoaderName != null ? classLoaderName.hashCode() : 0);
		result = 31 * result + (moduleName != null ? moduleName.hashCode() : 0);
		result = 31 * result + (moduleVersion != null ? moduleVersion.hashCode() : 0);
		return result;
	}

	/**
	 * @return the basic StackTraceElement this instance represents or null if either className or methodName are null.
	 */
	public StackTraceElement getStackTraceElement()
	{
		if(className == null || methodName == null)
		{
			return null;
		}
		return createStackTraceElement(classLoaderName, moduleName, moduleVersion,
				className, methodName, fileName, lineNumber);
	}

	@Override
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
	@Override
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
	@SuppressWarnings("WeakerAccess")
	public StringBuilder appendTo(StringBuilder stringBuilder, boolean extended)
	{
		Objects.requireNonNull(stringBuilder, "stringBuilder must not be null!");
		boolean separatorRequired = false;
		if(classLoaderName != null && !classLoaderName.isEmpty())
		{
			stringBuilder.append(classLoaderName).append(MODULE_SEPARATOR_CHAR);
			separatorRequired = true;
		}
		if(moduleName != null && !moduleName.isEmpty())
		{
			stringBuilder.append(moduleName);
			if(moduleVersion != null && !moduleVersion.isEmpty())
			{
				stringBuilder.append(MODULE_VERSION_SEPARATOR_CHAR).append(moduleVersion);
			}
			separatorRequired = true;
		}
		if(separatorRequired)
		{
			stringBuilder.append(MODULE_SEPARATOR_CHAR);
		}
		stringBuilder.append(className).append('.').append(methodName);
		if(isNativeMethod())
		{
			stringBuilder.append('(').append(NATIVE_METHOD_STRING).append(')');
		}
		else if(fileName != null)
		{
			stringBuilder.append('(').append(fileName);
			if(lineNumber >= 0)
			{
				stringBuilder.append(SEPARATOR_CHAR).append(lineNumber);
			}
			stringBuilder.append(')');
		}
		else
		{
			stringBuilder.append('(').append(UNKNOWN_SOURCE_STRING).append(')');
		}
		if(extended && (codeLocation != null || version != null))
		{
			stringBuilder.append(' ');
			appendExtended(stringBuilder);
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
			return null; // invalid
		}
		final int sourceEndIndex = ste.lastIndexOf(')');
		if(sourceEndIndex < 0)
		{
			return null; // invalid
		}

		final String classAndMethod = ste.substring(0, idx);

		final int classAndMethodDotIndex = classAndMethod.lastIndexOf('.');
		if(classAndMethodDotIndex < 0)
		{
			return null; // invalid
		}

		String classLoaderName = null;
		String moduleName = null;
		String moduleVersion = null;

		String clazz = classAndMethod.substring(0, classAndMethodDotIndex);
		final int sourceStartIndex = idx + 1;
		idx = clazz.lastIndexOf(MODULE_SEPARATOR_CHAR);
		if(idx > -1)
		{
			String loaderModule = clazz.substring(0, idx);
			clazz = clazz.substring(idx + 1);
			idx = loaderModule.indexOf(MODULE_SEPARATOR_CHAR);
			if(idx > -1)
			{
				classLoaderName = loaderModule.substring(0, idx);
				moduleName = loaderModule.substring(idx + 1);
			}
			else
			{
				moduleName = loaderModule;
			}
			idx = moduleName.indexOf(MODULE_VERSION_SEPARATOR_CHAR);
			if(idx > -1)
			{
				moduleVersion = moduleName.substring(idx + 1);
				moduleName = moduleName.substring(0, idx);
			}
		}
		if(classLoaderName != null && classLoaderName.isEmpty())
		{
			classLoaderName = null;
		}
		if(moduleName != null && moduleName.isEmpty())
		{
			moduleName = null;
		}
		if(moduleVersion != null && moduleVersion.isEmpty())
		{
			moduleVersion = null;
		}

		String source = ste.substring(sourceStartIndex, sourceEndIndex);
		idx = source.lastIndexOf(SEPARATOR_CHAR);
		String file = null;
		int lineNumber = UNKNOWN_SOURCE_LINE_NUMBER;
		if(idx != -1)
		{
			file = source.substring(0, idx);
			String numberString = source.substring(idx + 1, source.length());
			try
			{
				lineNumber = Integer.parseInt(numberString);
			}
			catch(NumberFormatException ex)
			{
				return null; // invalid
			}
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

		String method = classAndMethod.substring(classAndMethodDotIndex + 1, classAndMethod.length());

		ExtendedStackTraceElement result = null;
		if(sourceEndIndex + 2 < ste.length())
		{
			String remainder = ste.substring(sourceEndIndex + 2);
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
						if (codeLocation.isEmpty() || NA_PLACEHOLDER.equals(codeLocation))
						{
							codeLocation = null;
						}
						if (version.isEmpty() || NA_PLACEHOLDER.equals(version))
						{
							version = null;
						}
						result = new ExtendedStackTraceElement(clazz, method, file, lineNumber, codeLocation, version, exact);
					}
				}
			}
		}
		if(result == null)
		{
			result = new ExtendedStackTraceElement(clazz, method, file, lineNumber);
		}
		result.setClassLoaderName(classLoaderName);
		result.setModuleName(moduleName);
		result.setModuleVersion(moduleVersion);
		return result;
	}

	private static String getClassLoaderNameFrom(StackTraceElement ste)
	{
		return getValueFrom(GET_CLASS_LOADER_NAME, ste);
	}

	private static String getModuleNameFrom(StackTraceElement ste)
	{
		return getValueFrom(GET_MODULE_NAME, ste);
	}

	private static String getModuleVersionFrom(StackTraceElement ste)
	{
		return getValueFrom(GET_MODULE_VERSION, ste);
	}

	private static String getValueFrom(Method method, StackTraceElement ste)
	{
		if(method == null)
		{
			return null;
		}
		try
		{
			String result = (String) method.invoke(ste);
			if(result != null && !result.isEmpty())
			{
				return result;
			}
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			// ignore
		}
		return null;
	}

	private static StackTraceElement createStackTraceElement(
			String classLoaderName, String moduleName, String moduleVersion,
			String declaringClass, String methodName, String fileName, int lineNumber)
	{
		if(FULL_CTOR != null)
		{
			try
			{
				return FULL_CTOR.newInstance(
						classLoaderName, moduleName, moduleVersion,
						declaringClass, methodName, fileName, lineNumber
				);
			}
			catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
			{
				// ignore
			}
		}
		return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
	}
}
