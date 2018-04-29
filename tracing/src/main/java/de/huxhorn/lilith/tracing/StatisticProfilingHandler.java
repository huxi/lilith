/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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
 * Copyright 2007-2017 Joern Huxhorn
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

package de.huxhorn.lilith.tracing;

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class StatisticProfilingHandler
	extends BasicProfilingHandler
{
	private static final int DEFAULT_STEP_SIZE = 1000;
	private static final String STATISTICS_MARKER_NAME = "STATISTICS";

	private static final Marker STATISTICS_MARKER = MarkerFactory.getDetachedMarker(STATISTICS_MARKER_NAME);

	static
	{
		STATISTICS_MARKER.add(PROFILE_MARKER);
	}

	private static final char SEPARATOR = ';';

	private final ConcurrentMap<String, Entry> entries = new ConcurrentHashMap<>();
	private final AtomicInteger counter = new AtomicInteger();
	private int stepSize = DEFAULT_STEP_SIZE;

	public void setStepSize(int stepSize)
	{
		if(stepSize < 1)
		{
			throw new IllegalArgumentException("stepSize must be >= 1 but was "+stepSize+"!");
		}
		this.stepSize = stepSize;
	}

	@Override
	public void profile(Logger logger, String methodBaseName, String fullMethodSignature, long nanoSeconds)
	{
		super.profile(logger, methodBaseName, fullMethodSignature, nanoSeconds);
		if(methodBaseName == null)
		{
			// what?
			return;
		}
		addEntry(logger, methodBaseName, nanoSeconds);
	}

	private void addEntry(Logger logger, String methodBaseName, long nanoSeconds)
	{
		Entry entry = new Entry(methodBaseName);
		Entry previous = entries.putIfAbsent(methodBaseName, entry);
		if(previous != null)
		{
			entry = previous;
		}
		entry.addNanoSeconds(nanoSeconds);
		if(counter.incrementAndGet() % stepSize == 0)
		{
			logStatistics(logger);
		}
	}

	private void logStatistics(Logger logger)
	{
		if(!logger.isInfoEnabled(STATISTICS_MARKER))
		{
			return;
		}
		try
		{
			SortedSet<Entry> entrySet = new TreeSet<>();
			for(Entry current : entries.values())
			{
				entrySet.add(current.clone());
			}
			StringBuilder msg = new StringBuilder(1000);
			msg.append("methodName").append(SEPARATOR)
					.append("counter").append(SEPARATOR)
					.append("minimumNanoSeconds").append(SEPARATOR)
					.append("maximumNanoSeconds").append(SEPARATOR)
					.append("totalNanoSeconds").append(SEPARATOR)
					.append("averageNanoSeconds");
			for(Entry current : entrySet)
			{
				msg.append('\n');
				long counter = current.getCounter();
				long totalNanoSeconds = current.getTotalNanoSeconds();
				msg.append(current.getMethodBaseName()).append(SEPARATOR)
						.append(counter).append(SEPARATOR)
						.append(current.getMinimumNanoSeconds()).append(SEPARATOR)
						.append(current.getMaximumNanoSeconds()).append(SEPARATOR)
						.append(totalNanoSeconds).append(SEPARATOR);

				if(counter != 0)
				{
					msg.append((double)totalNanoSeconds / counter);
				}
			}
			logger.info(STATISTICS_MARKER, "{}", msg);
		}
		catch(CloneNotSupportedException ex)
		{
			if(logger.isErrorEnabled()) logger.error("Entry isn't cloneable!", ex);
		}
	}

	@Override
	public String toString()
	{
		return "StatisticProfilingHandler{" +
				"warnThresholdInSeconds=" + getWarnThresholdInSeconds() +
				", errorThresholdInSeconds=" + getErrorThresholdInSeconds() +
				", stepSize=" + stepSize +
				'}';
	}

	private static class Entry
		implements Cloneable, Comparable<Entry>
	{
		private final Lock lock = new ReentrantLock(true);
		private final String methodBaseName;
		private long counter;
		private long totalNanoSeconds;
		private long minimumNanoSeconds=Long.MAX_VALUE;
		private long maximumNanoSeconds;

		Entry(String methodBaseName)
		{
			this.methodBaseName = methodBaseName;
		}

		String getMethodBaseName()
		{
			return methodBaseName;
		}

		@Override
		public Entry clone()
				throws CloneNotSupportedException
		{
			lock.lock();
			try
			{
				return (Entry) super.clone();
			}
			finally
			{
				lock.unlock();
			}
		}

		void addNanoSeconds(long nanoSeconds)
		{
			lock.lock();
			try
			{
				counter++;
				totalNanoSeconds = totalNanoSeconds + nanoSeconds;
				if(nanoSeconds < minimumNanoSeconds)
				{
					minimumNanoSeconds = nanoSeconds;
				}
				if(nanoSeconds > maximumNanoSeconds)
				{
					maximumNanoSeconds = nanoSeconds;
				}
			}
			finally
			{
				lock.unlock();
			}
		}

		// only call on cloned instance
		long getCounter()
		{
			return counter;
		}

		// only call on cloned instance
		long getTotalNanoSeconds()
		{
			return totalNanoSeconds;
		}

		// only call on cloned instance
		private long getMinimumNanoSeconds()
		{
			return minimumNanoSeconds;
		}

		// only call on cloned instance
		private long getMaximumNanoSeconds()
		{
			return maximumNanoSeconds;
		}

		@Override
		public int compareTo(Entry other)
		{
			Objects.requireNonNull(other, "other must not be null!");
			long difference = totalNanoSeconds - other.totalNanoSeconds;
			if(difference > 0)
			{
				return 1;
			}
			if(difference < 0)
			{
				return -1;
			}
			return methodBaseName.compareTo(other.methodBaseName);
		}
	}
}
