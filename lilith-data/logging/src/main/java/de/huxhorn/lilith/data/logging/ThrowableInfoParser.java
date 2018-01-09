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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ThrowableInfoParser
{
	private static final String NEWLINE = "\n";
	private static final String CARRIAGE_RETURN = "\r";

	private static final String CLASS_MESSAGE_SEPARATOR = ": ";

	private static final Pattern OMITTED_PATTERN = Pattern.compile("^(\t+)\\.\\.\\. (\\d+) more");
	private static final Pattern AT_PATTERN = Pattern.compile("^(\t+)at (.+)");
	private static final Pattern MESSAGE_MATCHER = Pattern.compile("^(\t*)?("+ThrowableInfo.CAUSED_BY_PREFIX+"|"+ThrowableInfo.SUPPRESSED_PREFIX+")?(.*)");

	static
	{
		new ThrowableInfoParser(); // stfu
	}

	private ThrowableInfoParser() {}

	public static ThrowableInfo parse(String throwableInfoString)
	{
		if(throwableInfoString == null)
		{
			return null;
		}
		return parse(splitLines(throwableInfoString));
	}

	public static ThrowableInfo parse(List<String> throwableInfoLines)
	{
		if(throwableInfoLines == null)
		{
			return null;
		}

		if(throwableInfoLines.isEmpty())
		{
			return null;
		}

		return parse(throwableInfoLines, /*startIndex=*/0, /*indent=*/0).throwableInfo;
	}

	private static class ThrowableInfoParseResult
	{
		ThrowableInfo throwableInfo;
		int endIndex;

		ThrowableInfoParseResult(ThrowableInfo throwableInfo, int endIndex)
		{
			this.throwableInfo = throwableInfo;
			this.endIndex = endIndex;
		}
	}

	private static ThrowableInfoParseResult parse(List<String> throwableInfoLines, int startIndex, int indent)
	{
		// sorry, future huxi
		final int lineCount = throwableInfoLines.size();

		String name=null;
		StringBuilder message = null;
		List<ExtendedStackTraceElement> stackTraceElements = null;
		int omittedElements = 0;
		ThrowableInfo cause = null;
		List<ThrowableInfo> suppressedInfos = null;

		int index = startIndex;
		for(; index<lineCount; index++)
		{
			String currentLine = throwableInfoLines.get(index);
			Matcher atMatcher = atMatcher(currentLine);
			if(atMatcher.matches())
			{
				String indentString = atMatcher.group(1);
				if(indentString.length() != indent + 1)
				{
					// we reached wrong nesting...
					break;
				}
				String steString = atMatcher.group(2);
				ExtendedStackTraceElement este = ExtendedStackTraceElement.parseStackTraceElement(steString);
				if(este != null)
				{
					if(stackTraceElements == null)
					{
						stackTraceElements = new ArrayList<>(); // NOPMD - AvoidInstantiatingObjectsInLoops
					}
					stackTraceElements.add(este);
				}
				continue;
			}
			Matcher omittedMatcher = omittedMatcher(currentLine);
			if(omittedMatcher.matches())
			{
				String indentString = omittedMatcher.group(1);
				if(indentString.length() != indent + 1)
				{
					// we reached wrong nesting...
					break;
				}
				omittedElements = Integer.parseInt(omittedMatcher.group(2));
				continue;
			}

			Matcher messageMatcher = messageMatcher(currentLine); // will always match...
			if(messageMatcher.matches())
			{
				String indentString = messageMatcher.group(1);
				String type = messageMatcher.group(2); // either CAUSED_BY_PREFIX, SUPPRESSED_PREFIX or null
				String remainder = messageMatcher.group(3); // remainder of the String
				if(ThrowableInfo.CAUSED_BY_PREFIX.equals(type))
				{
					if(index != startIndex)
					{
						if(indentString.length() != indent)
						{
							// we reached wrong nesting...
							break;
						}
						ThrowableInfoParseResult parsed = parse(throwableInfoLines, index, indent);
						index = parsed.endIndex - 1;
						if(parsed.throwableInfo != null)
						{
							cause = parsed.throwableInfo;
						}
						continue;
					}
				}
				else if(ThrowableInfo.SUPPRESSED_PREFIX.equals(type)
						&& index != startIndex)
				{
					if(indentString.length() != indent + 1)
					{
						// we reached wrong nesting...
						break;
					}
					ThrowableInfoParseResult parsed = parse(throwableInfoLines, index, indent + 1);
					index = parsed.endIndex - 1;
					if(parsed.throwableInfo != null)
					{
						if(suppressedInfos == null)
						{
							suppressedInfos = new ArrayList<>(); // NOPMD - AvoidInstantiatingObjectsInLoops
						}
						suppressedInfos.add(parsed.throwableInfo);
					}
					continue;
				}
				if(message == null)
				{
					// first line
					int colonIndex = remainder.indexOf(CLASS_MESSAGE_SEPARATOR);
					if(colonIndex > -1)
					{
						name = remainder.substring(0, colonIndex);
						message = new StringBuilder(); // NOPMD - AvoidInstantiatingObjectsInLoops
						message.append(remainder.substring(colonIndex + CLASS_MESSAGE_SEPARATOR.length()));
					}
					else
					{
						name = remainder;
					}
				}
				else
				{
					message.append(NEWLINE);
					if(indentString != null)
					{
						message.append(indentString);
					}
					message.append(remainder);
				}

			}
			else
			{
				System.err.println("What? "+currentLine); // NOPMD
			}
		}

		ThrowableInfo throwableInfo = null;

		if
			(
					name != null ||
					message != null ||
					stackTraceElements != null ||
					omittedElements != 0 ||
					cause != null ||
					suppressedInfos != null
			)
		{
			// we found *any* info...
			throwableInfo = new ThrowableInfo();
			throwableInfo.setName(name);
			if(message != null)
			{
				throwableInfo.setMessage(message.toString());
			}
			if(stackTraceElements != null)
			{
				throwableInfo.setStackTrace(stackTraceElements.toArray(ExtendedStackTraceElement.ARRAY_PROTOTYPE));
			}
			throwableInfo.setOmittedElements(omittedElements);
			throwableInfo.setCause(cause);
			if(suppressedInfos != null)
			{
				throwableInfo.setSuppressed(suppressedInfos.toArray(ThrowableInfo.ARRAY_PROTOTYPE));
			}
		}

		return new ThrowableInfoParseResult(throwableInfo, index);
	}

	static List<String> splitLines(String input)
	{
		if(input == null)
		{
			return null;
		}

		StringTokenizer tok = new StringTokenizer(input, NEWLINE+CARRIAGE_RETURN, true);
		List<String> lines = new ArrayList<>();
		boolean foundAnything=false;
		boolean hadContent=false;
		while(tok.hasMoreTokens())
		{
			foundAnything = true;
			String current = tok.nextToken();
			if(NEWLINE.equals(current))
			{
				if(hadContent)
				{
					hadContent=false;
				}
				else
				{
					// support empty lines
					lines.add("");
				}
				continue;
			}
			if(CARRIAGE_RETURN.equals(current))
			{
				// just ignore carriage returns
				continue;
			}
			lines.add(current);
			hadContent=true;
		}

		if(!foundAnything)
		{
			// an empty string should result in a single empty line.
			lines.add("");
		}
		return lines;
	}

	/**
	 * group(1) is mandatory indent (1..n \t characters)
	 * group(2) is the number of omitted elements.
	 *
	 * @param input inputString
	 * @return atMatcher
	 */
	static Matcher omittedMatcher(String input)
	{
		return OMITTED_PATTERN.matcher(input);
	}

	/**
	 * group(1) is mandatory indent (1..n \t characters)
	 * group(2) is remainder, i.e. the StackTraceElement
	 *
	 * @param input inputString
	 * @return atMatcher
	 */
	static Matcher atMatcher(String input)
	{
		return AT_PATTERN.matcher(input);
	}

	/**
	 * group(1) is optional indent (0..n \t characters)
	 * group(2) is optional prefix, i.e. either "Caused by: ", "Suppressed: " or null.
	 * group(3) is the remainder
	 *
	 * @param input inputString
	 * @return messageMatcher
	 */
	static Matcher messageMatcher(String input)
	{
		return MESSAGE_MATCHER.matcher(input);
	}
}
