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

package de.huxhorn.lilith.tracing;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class BasicProfilingHandler
	implements ProfilingHandler
{
	protected static final Marker PROFILE_MARKER = MarkerFactory.getDetachedMarker(PROFILE_MARKER_NAME);

	public static final int DEFAULT_WARN_THRESHOLD_IN_SECONDS = 3;
	public static final int DEFAULT_ERROR_THRESHOLD_IN_SECONDS = 30;

	private int warnThresholdInSeconds;
	private int errorThresholdInSeconds;

	public BasicProfilingHandler()
	{
		warnThresholdInSeconds = DEFAULT_WARN_THRESHOLD_IN_SECONDS;
		errorThresholdInSeconds = DEFAULT_ERROR_THRESHOLD_IN_SECONDS;
	}

	public int getWarnThresholdInSeconds()
	{
		return warnThresholdInSeconds;
	}

	public void setWarnThresholdInSeconds(int warnThresholdInSeconds)
	{
		this.warnThresholdInSeconds = warnThresholdInSeconds;
	}

	public int getErrorThresholdInSeconds()
	{
		return errorThresholdInSeconds;
	}

	public void setErrorThresholdInSeconds(int errorThresholdInSeconds)
	{
		this.errorThresholdInSeconds = errorThresholdInSeconds;
	}

	@Override
	public void profile(Logger logger, String methodBaseName, String fullMethodSignature, long nanoSeconds)
	{
		if(nanoSeconds < 1_000_000L)
		{
			if(logger.isTraceEnabled(PROFILE_MARKER)) logger.trace(PROFILE_MARKER, "{}ns - {}", nanoSeconds, fullMethodSignature);
			return;
		}
		long milliSeconds = nanoSeconds / 1_000_000L;
		if(milliSeconds < 1000)
		{
			if(logger.isDebugEnabled(PROFILE_MARKER)) logger.debug(PROFILE_MARKER, "{}ms - {}", milliSeconds, fullMethodSignature);
			return;
		}
		long seconds = milliSeconds / 1000;
		if(seconds > errorThresholdInSeconds)
		{
			if(logger.isErrorEnabled(PROFILE_MARKER)) logger.error(PROFILE_MARKER, "{}ms - {}", milliSeconds, fullMethodSignature);
			return;
		}
		if(seconds > warnThresholdInSeconds)
		{
			if(logger.isWarnEnabled(PROFILE_MARKER)) logger.warn(PROFILE_MARKER, "{}ms - {}", milliSeconds, fullMethodSignature);
			return;
		}
		if(logger.isInfoEnabled(PROFILE_MARKER)) logger.info(PROFILE_MARKER, "{}ms - {}", milliSeconds, fullMethodSignature);
	}

	@Override
	public String toString()
	{
		return "BasicProfilingHandler{" +
				"warnThresholdInSeconds=" + warnThresholdInSeconds +
				", errorThresholdInSeconds=" + errorThresholdInSeconds +
				'}';
	}
}
