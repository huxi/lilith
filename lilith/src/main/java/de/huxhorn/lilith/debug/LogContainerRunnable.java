/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2016 Joern Huxhorn
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
package de.huxhorn.lilith.debug;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogContainerRunnable
	extends AbstractDebugRunnable
{
	private final Logger logger = LoggerFactory.getLogger(LogContainerRunnable.class);

	public LogContainerRunnable(int delay)
	{
		super(delay);
	}

	@Override
	public void runIt()
		throws InterruptedException
	{
		Map<String, String> stringMap = new HashMap<>();
		stringMap.put("fooKey", "fooValue");
		stringMap.put("barKey", "barValue");
		stringMap.put("nullKey", null);
		stringMap.put("nullStringKey", "null");
		stringMap.put(null, "nullValue");
		stringMap.put("null", "nullStringValue");
		stringMap.put("emptyStringKey", "");
		stringMap.put("", "emptyStringValue");

		if(logger.isInfoEnabled()) logger.info("Map<String, String>: {}", stringMap);
		sleep();

		Set<String> stringSet = new HashSet<>();
		if(logger.isInfoEnabled()) logger.info("Empty Set<String>: {}", stringSet);
		stringSet.add("");
		if(logger.isInfoEnabled()) logger.info("Set<String> containing only empty String: {}", stringSet);
		stringSet.remove("");
		stringSet.add(null);
		if(logger.isInfoEnabled()) logger.info("Set<String> containing only null: {}", stringSet);
		stringSet.remove(null);
		stringSet.add("null");
		if(logger.isInfoEnabled()) logger.info("Set<String> containing only \"null\": {}", stringSet);

		stringSet.add(null);
		stringSet.add("");
		stringSet.add("foo");

		if(logger.isInfoEnabled()) logger.info("Set<String> containing various values: {}", stringSet);

		Object[] emptyObjectArray = new Object[0];
		if(logger.isInfoEnabled()) logger.info("Empty Object[]: {}", emptyObjectArray);
		if(logger.isInfoEnabled()) logger.info("Param {} and Empty Object[]: {}", "foo", emptyObjectArray);
		Object[] emptyStringObjectArray = new Object[]{""};
		if(logger.isInfoEnabled()) logger.info("Object[] containing empty String: {}", emptyStringObjectArray);
		if(logger.isInfoEnabled()) logger.info("Param {} and Object[] containing empty String: {}", "foo", emptyStringObjectArray);
		Object[] nullObjectArray = new Object[]{null};
		if(logger.isInfoEnabled()) logger.info("Object[] containing null: {}", nullObjectArray);
		if(logger.isInfoEnabled()) logger.info("Param {} and Object[] containing null: {}", "Foo", nullObjectArray);
		Object[] nullStringObjectArray = new Object[]{"null"};
		if(logger.isInfoEnabled()) logger.info("Object[] containing null: {}", nullStringObjectArray);
		if(logger.isInfoEnabled()) logger.info("Param {} and Object[] containing null: {}", "Foo", nullStringObjectArray);


		Map<String, Object> stringObjectMap=new HashMap<>();

		stringObjectMap.put("stringMap", stringMap);

		stringObjectMap.put("stringSet", stringSet);

		stringObjectMap.put("emptyObjectArray", emptyObjectArray);
		stringObjectMap.put("emptyStringObjectArray", emptyStringObjectArray);
		stringObjectMap.put("nullObjectArray", nullObjectArray);
		stringObjectMap.put("nullStringObjectArray", nullStringObjectArray);
		byte[] byteArray = new byte[] {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};
		stringObjectMap.put("byte[]", byteArray);
		Byte[] byteObjectArray = new Byte[] {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};
		stringObjectMap.put("Byte[]", byteObjectArray);


		if(logger.isInfoEnabled()) logger.info("Map<String, Object>: {}", stringObjectMap);

	}
}
