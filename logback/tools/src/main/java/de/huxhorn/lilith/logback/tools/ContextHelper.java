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

package de.huxhorn.lilith.logback.tools;

import ch.qos.logback.core.Context;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class ContextHelper
{
	private static final Constructor<?> UTIL_CTOR;
	private static final Method TIME_OF_LAST_RESET_METHOD;
	private static final Method GET_HIGHEST_LEVEL_METHOD;

	public static final int FAIL = -17;

	private ContextHelper() {}

	static
	{
		Class<?> clazz=null;
		Constructor<?> ctor = null;
		Method timeOfLastResetMethod = null;
		Method getHighestLevelMethod = null;
		Throwable t = null;
		try
		{
			clazz = Class.forName("ch.qos.logback.core.status.StatusChecker");
		}
		catch (ClassNotFoundException e)
		{
			try
			{
				clazz = Class.forName("ch.qos.logback.core.status.StatusUtil");
			}
			catch (ClassNotFoundException ex)
			{
				t = ex;
			}
		}
		if(clazz != null)
		{
			try
			{
				ctor = clazz.getDeclaredConstructor(Context.class);
				timeOfLastResetMethod = clazz.getMethod("timeOfLastReset");
				getHighestLevelMethod = clazz.getMethod("getHighestLevel", Long.TYPE);
			}
			catch (NoSuchMethodException e)
			{
				t= e;
			}
		}

		UTIL_CTOR = ctor;
		TIME_OF_LAST_RESET_METHOD = timeOfLastResetMethod;
		GET_HIGHEST_LEVEL_METHOD = getHighestLevelMethod;

		if(t != null)
		{
			t.printStackTrace(); // NOPMD
		}

		new ContextHelper(); // stfu
	}

	/**
	 * Returns the highest status level of the given context since the last reset.
	 *
	 * Status.INFO, Status.WARN or Status.ERROR
	 *
	 * @param context the Context.
	 * @return the highest status level since the last reset.
	 */
	public static int getHighestLevel(Context context)
	{
		int result = FAIL;
		if(UTIL_CTOR == null || TIME_OF_LAST_RESET_METHOD == null || GET_HIGHEST_LEVEL_METHOD == null)
		{
			return result;
		}

		try
		{
			Object instance = UTIL_CTOR.newInstance(context);
			Object timeObject = TIME_OF_LAST_RESET_METHOD.invoke(instance);
			if(timeObject instanceof Long)
			{
				Long timeOfLastReset = (Long) timeObject;
				Object highest = GET_HIGHEST_LEVEL_METHOD.invoke(instance, timeOfLastReset);
				if(highest instanceof Integer)
				{
					result = (Integer)highest;
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(); // NOPMD
		}
		return result;
	}

	public static long getTimeOfLastReset(Context context)
	{
		long result = FAIL;
		if(UTIL_CTOR == null || TIME_OF_LAST_RESET_METHOD == null || GET_HIGHEST_LEVEL_METHOD == null)
		{
			return result;
		}

		try
		{
			Object instance = UTIL_CTOR.newInstance(context);
			Object timeObject = TIME_OF_LAST_RESET_METHOD.invoke(instance);
			if(timeObject instanceof Long)
			{
				result = (Long)timeObject;
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(); // NOPMD
		}
		return result;
	}
}
