/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2013 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.huxhorn.lilith.swing;

import de.huxhorn.sulky.formatting.SimpleXml;

public class TextPreprocessor
{
	private static final int MAX_LINE_LENGTH = 80;
	private static final int MAX_LINES = 25;
	private static final String TAB_REPLACEMENT = "    ";
	private static final String LINE_TRUNCATION = "[..]";

	public static String cropLine(String text)
	{
		return cropLine(text, MAX_LINE_LENGTH, LINE_TRUNCATION);
	}

	private static String cropLine(String text, int maxLineLength, String lineTruncationMarker)
	{
		if(text == null)
		{
			return null;
		}
		StringBuilder result = new StringBuilder();
		int newlineIndex = text.indexOf('\n');
		if(newlineIndex > -1)
		{
			int newlineCounter = 0;
			for(int i = 0; i < text.length(); i++)
			{
				if(text.charAt(i) == '\n')
				{
					newlineCounter++;
				}
			}
			appendTruncated(text.subSequence(0, newlineIndex), result, maxLineLength, lineTruncationMarker);
			newlineCounter--;
			if(newlineCounter > 0)
			{
				result.append(" [+").append(newlineCounter).append(" lines]");
			}
		}
		else
		{
			appendTruncated(text, result, maxLineLength, lineTruncationMarker);
		}
		return result.toString();
	}


	public static String cropTextBlock(String text)
	{
		return cropTextBlock(text, MAX_LINE_LENGTH, LINE_TRUNCATION, MAX_LINES, TAB_REPLACEMENT);
	}

	private static String cropTextBlock(String text, int maxLineLength, String lineTruncationMarker, int maxLines, String tabReplacement)
	{
		if(text == null)
		{
			return null;
		}
		// crop to a sane size, e.g. 80x25 characters
		StringBuilder result=new StringBuilder();
		StringBuilder lineBuilder=new StringBuilder();
		int lineCounter = 0;
		for(int i=0;i<text.length();i++)
		{
			char current = text.charAt(i);
			if(current == '\t')
			{
				if(tabReplacement != null)
				{
					lineBuilder.append(tabReplacement);
				}
				else
				{
					lineBuilder.append('\t');
				}
			}
			else if(current == '\n')
			{
				if(lineCounter < maxLines)
				{
					appendTruncated(lineBuilder, result, maxLineLength, lineTruncationMarker);
					result.append('\n');
				}
				lineBuilder.setLength(0);
				lineCounter++;
			}
			else if(current != '\r')
			{
				lineBuilder.append(current);
			}
		}
		if(lineCounter >= maxLines)
		{
			int remaining = lineCounter - maxLines + 1;
			result.append("[.. ").append(remaining).append(" more lines ..]");
		}
		else
		{
			appendTruncated(lineBuilder, result, maxLineLength, lineTruncationMarker);
		}
		return result.toString();
	}

	private static void appendTruncated(CharSequence sourceBuilder, StringBuilder targetBuilder, int maxLineLength, String lineTruncationMarker)
	{
		if(maxLineLength < 1 || sourceBuilder.length() <= maxLineLength)
		{
			targetBuilder.append(sourceBuilder);
			return;
		}
		if(lineTruncationMarker == null)
		{
			targetBuilder.append(sourceBuilder.subSequence(0, maxLineLength));
		}
		else
		{
			targetBuilder.append(sourceBuilder.subSequence(0, maxLineLength - lineTruncationMarker.length()));
			targetBuilder.append(lineTruncationMarker);
		}
	}

	public static String preformattedTooltip(String text)
	{
		if(text == null)
		{
			return null;
		}
		text = SimpleXml.escape(text);
		text = text.replace("\n", "<br>");
		return "<html><tt><pre>" + text + "</pre></tt></html>";

	}
}
