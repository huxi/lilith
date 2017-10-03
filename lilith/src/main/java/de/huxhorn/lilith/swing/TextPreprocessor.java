/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

import de.huxhorn.sulky.conditions.And;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.conditions.ConditionGroup;
import de.huxhorn.sulky.conditions.ConditionWrapper;
import de.huxhorn.sulky.conditions.Not;
import de.huxhorn.sulky.conditions.Or;
import de.huxhorn.sulky.formatting.SimpleXml;
import java.util.List;

public final class TextPreprocessor
{
	private static final int MAX_LINE_LENGTH = 100;
	private static final int MAX_LINES = 40;
	private static final String TAB_REPLACEMENT = "    ";
	private static final String LINE_TRUNCATION = "[..]";
	private static final String INDENT = "    ";

	static
	{
		new TextPreprocessor(); // stfu
	}

	private TextPreprocessor() {}

	public static String cropLine(String text)
	{
		return cropLine(text, MAX_LINE_LENGTH, LINE_TRUNCATION);
	}

	public static String cropToSingleLine(String text)
	{
		return cropLine(text, 0, null);
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
			if(newlineCounter == 1)
			{
				result.append(" [+1 line]");
			}
			else if(newlineCounter > 1)
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

	public static String wrapWithPre(String text)
	{
		if(text == null)
		{
			return null;
		}
		text = SimpleXml.escape(text);
		text = text.replace("\n", "<br>");
		return "<tt><pre>" + text + "</pre></tt>";
	}

	public static String preformattedTooltip(String text)
	{
		if(text == null)
		{
			return null;
		}
		return "<html>" + wrapWithPre(text) + "</html>";
	}

	public static String formatCondition(Condition condition)
	{
		if(condition == null)
		{
			return null;
		}
		StringBuilder result = new StringBuilder();

		formatCondition(condition, result, 0);

		return result.toString();
	}

	private static void formatCondition(Condition condition, StringBuilder result, int indent) {
		if(condition == null)
		{
			appendIndent(result, indent);
			result.append("null");
		}
		else if(condition instanceof ConditionWrapper)
		{
			ConditionWrapper wrapper = (ConditionWrapper)condition;
			String operator;
			if(wrapper instanceof Not)
			{
				operator = "!";
			}
			else
			{
				// Unknown wrapper. Improvise.
				operator = wrapper.getClass().getSimpleName();
			}
			Condition c = wrapper.getCondition();
			appendIndent(result, indent);
			result.append(operator).append('(');
			if(c == null)
			{
				result.append("null");
			}
			else
			{
				result.append('\n');
				formatCondition(c, result, indent+1);
				appendIndent(result, indent);
			}
			result.append(')');
		}
		else if(condition instanceof ConditionGroup)
		{
			ConditionGroup group = (ConditionGroup) condition;
			String operator;
			if(group instanceof And)
			{
				operator = "&&";
			}
			else if(group instanceof Or)
			{
				operator = "||";
			}
			else
			{
				// Unknown group. Improvise.
				operator = group.getClass().getSimpleName();
			}
			List<Condition> conditions = group.getConditions();
			appendIndent(result, indent);
			result.append('(');
			if(conditions == null || conditions.isEmpty())
			{
				result.append('[').append(operator).append(" without conditions.]");
			}
			else
			{
				result.append('\n');
				boolean first = true;
				for(Condition current : conditions)
				{
					if(first)
					{
						first = false;
					}
					else
					{
						appendIndent(result, indent+1);
						result.append(operator).append('\n');
					}
					formatCondition(current, result, indent+1);
				}
				appendIndent(result, indent);
			}
			result.append(')');
		}
		else
		{
			// an "ordinary" condition.
			appendIndent(result, indent);
			result.append(condition);
		}

		if(indent > 0)
		{
			result.append('\n');
		}
	}

	private static void appendIndent(StringBuilder result, int indent)
	{
		for(int i=0;i<indent;i++)
		{
			result.append(INDENT);
		}
	}
}
