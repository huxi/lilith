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

package de.huxhorn.lilith.log4j;

import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.ThrowableInfo;

import java.util.ArrayList;
import java.util.List;

public final class ThrowableInfoParser
{
	private static final String CAUSED_BY_PREFIX = "Caused by: ";
	private static final String AT_PREFIX = "at ";
	private static final String OMITTED_PREFIX = "... ";
	private static final String OMITTED_POSTFIX = " more";
	private static final String CLASS_MESSAGE_SEPARATOR = ": ";

	private ThrowableInfoParser()
	{}

	public static ThrowableInfo parseThrowableInfo(List<String> lines)
	{
		ThrowableInfo result = null;
		ThrowableInfo currentTI = null;
		List<ExtendedStackTraceElement> stackTraceElements = new ArrayList<ExtendedStackTraceElement>();
		boolean insideMessage = false;
		for(String current : lines)
		{
			current=current.trim();
			if(current.startsWith(AT_PREFIX))
			{
				current = current.substring(AT_PREFIX.length());
				ExtendedStackTraceElement este = ExtendedStackTraceElement.parseStackTraceElement(current);
				if(este != null)
				{
					stackTraceElements.add(este);
				}
				insideMessage = false;
			}
			else if(current.startsWith(OMITTED_PREFIX))
			{
				if(currentTI != null)
				{
					if(current.endsWith(OMITTED_POSTFIX))
					{
						String countStr = current
							.substring(OMITTED_PREFIX.length(), current.length() - OMITTED_POSTFIX.length());
						currentTI.setOmittedElements(Integer.parseInt(countStr));
						insideMessage = false;
					}
					else if(insideMessage)
					{
						String prevMessage = currentTI.getMessage();
						if(prevMessage == null)
						{
							currentTI.setMessage(current);
						}
						else
						{
							currentTI.setMessage(prevMessage + "\n" + current);
						}
					}
				}
			}
			else
			{
				if(current.startsWith(CAUSED_BY_PREFIX))
				{
					current = current.substring(CAUSED_BY_PREFIX.length());
				}
				if(currentTI != null)
				{
					if(!insideMessage)
					{
						ThrowableInfo newTI = new ThrowableInfo();
						currentTI.setCause(newTI);
						if(stackTraceElements.size() > 0)
						{
							currentTI
								.setStackTrace(stackTraceElements.toArray(new ExtendedStackTraceElement[stackTraceElements
									.size()]));
							stackTraceElements.clear();
						}
						currentTI = newTI;
					}
				}
				else
				{
					currentTI = new ThrowableInfo();
				}
				if(result == null)
				{
					result = currentTI;
				}
				if(insideMessage)
				{
					String prevMessage = currentTI.getMessage();
					if(prevMessage == null)
					{
						currentTI.setMessage(current);
					}
					else
					{
						currentTI.setMessage(prevMessage + "\n" + current);
					}
				}
				else
				{
					int colonIndex = current.indexOf(CLASS_MESSAGE_SEPARATOR);
					if(colonIndex > -1)
					{
						currentTI.setName(current.substring(0, colonIndex));
						currentTI.setMessage(current.substring(colonIndex + CLASS_MESSAGE_SEPARATOR.length()));
					}
					else
					{
						currentTI.setName(current);
					}
					insideMessage = true;
				}
			}
		}
		if(currentTI != null && stackTraceElements.size() > 0)
		{
			currentTI
				.setStackTrace(stackTraceElements.toArray(new ExtendedStackTraceElement[stackTraceElements.size()]));
			stackTraceElements.clear();
		}

		return result;
	}

}
