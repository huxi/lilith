/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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
package de.huxhorn.lilith.swing.callables;

import de.huxhorn.lilith.buffers.FilteringBuffer;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.FileBuffer;
import de.huxhorn.sulky.conditions.Condition;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CallableMetaData
{
	public static final String FIND_TASK_META_CONDITION = "Condition";
	public static final String FIND_TASK_META_SOURCE_IDENTIFIER = "SourceIdentifier";
	public static final String FIND_TASK_META_START_ROW = "StartRow";
	public static final String FIND_TASK_META_DATA_FILE = "DataFile";

	public static <T extends Serializable> Map<String, String> createFindMetaData(Condition condition, EventSource<T> eventSource, int startRow)
	{
		String conditionStr = null;
		if(condition != null)
		{
			conditionStr = condition.toString();
		}
		Buffer<EventWrapper<T>> buffer = null;
		String sourceIdentifierStr = null;
		if(eventSource != null)
		{
			buffer = eventSource.getBuffer();
			SourceIdentifier si = eventSource.getSourceIdentifier();
			if(si != null)
			{
				sourceIdentifierStr = si.toString();
			}
		}
		Map<String, String> metaData = new HashMap<String, String>();
		metaData.put(FIND_TASK_META_CONDITION, conditionStr);
		metaData.put(FIND_TASK_META_START_ROW, "" + startRow);
		if(sourceIdentifierStr != null)
		{
			metaData.put(FIND_TASK_META_SOURCE_IDENTIFIER, sourceIdentifierStr);
		}
		resolveDataFile(metaData, buffer);
		return metaData;
	}

	public static <T extends Serializable> Map<String, String> createFilteringMetaData(Condition condition, EventSource<T> eventSource)
	{
		String conditionStr = null;
		if(condition != null)
		{
			conditionStr = condition.toString();
		}
		Buffer<EventWrapper<T>> buffer = null;
		String sourceIdentifierStr = null;
		if(eventSource != null)
		{
			buffer = eventSource.getBuffer();
			SourceIdentifier si = eventSource.getSourceIdentifier();
			if(si != null)
			{
				sourceIdentifierStr = si.toString();
			}
		}
		Map<String, String> metaData = new HashMap<String, String>();
		metaData.put(FIND_TASK_META_CONDITION, conditionStr);
		if(sourceIdentifierStr != null)
		{
			metaData.put(FIND_TASK_META_SOURCE_IDENTIFIER, sourceIdentifierStr);
		}
		resolveDataFile(metaData, buffer);
		return metaData;
	}

	private static <T extends Serializable> void resolveDataFile(Map<String, String> metaData, Buffer<EventWrapper<T>> buffer)
	{
		Buffer<EventWrapper<T>> sourceBuffer = FilteringBuffer.resolveSourceBuffer(buffer);
		if(sourceBuffer instanceof FileBuffer)
		{
			FileBuffer<EventWrapper<T>> fileBuffer = (FileBuffer<EventWrapper<T>>) sourceBuffer;
			File file = fileBuffer.getDataFile();
			if(file != null)
			{
				metaData.put(FIND_TASK_META_DATA_FILE, file.getAbsolutePath());
			}
		}
	}
}
