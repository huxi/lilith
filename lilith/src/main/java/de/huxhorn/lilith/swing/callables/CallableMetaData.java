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

package de.huxhorn.lilith.swing.callables;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.lilith.swing.TextPreprocessor;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.FileBuffer;
import de.huxhorn.sulky.buffers.filtering.FilteringBuffer;
import de.huxhorn.sulky.conditions.Condition;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public final class CallableMetaData
{
	public static final String FIND_TASK_META_CONDITION = "Condition";
	public static final String FIND_TASK_META_SOURCE_IDENTIFIER = "SourceIdentifier";
	private static final String FIND_TASK_META_START_ROW = "StartRow";
	private static final String FIND_TASK_META_DATA_FILE = "DataFile";

	static
	{
		new CallableMetaData(); // stfu
	}

	private CallableMetaData() {}

	public static <T extends Serializable> Map<String, String> createFindMetaData(Condition condition, EventSource<T> eventSource, int startRow)
	{
		Map<String, String> metaData = createFilteringMetaData(condition, eventSource);
		metaData.put(FIND_TASK_META_START_ROW, Integer.toString(startRow));

		return metaData;
	}

	public static <T extends Serializable> Map<String, String> createFilteringMetaData(Condition condition, EventSource<T> eventSource)
	{
		Map<String, String> metaData = new HashMap<>();
		if(condition != null)
		{
			metaData.put(FIND_TASK_META_CONDITION, TextPreprocessor.formatCondition(condition));
		}
		if(eventSource == null)
		{
			return metaData;
		}

		metaData.put(FIND_TASK_META_SOURCE_IDENTIFIER, eventSource.getSourceIdentifier().toString());
		Buffer<EventWrapper<T>> buffer = eventSource.getBuffer();

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
