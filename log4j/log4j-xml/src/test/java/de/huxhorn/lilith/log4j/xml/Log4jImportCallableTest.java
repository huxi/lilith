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

package de.huxhorn.lilith.log4j.xml;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.sulky.buffers.AppendOperation;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@SuppressWarnings("PMD.ClassNamingConventions")
public class Log4jImportCallableTest
{
	private final Logger logger = LoggerFactory.getLogger(Log4jImportCallableTest.class);

	private File inputFile;

	@After
	public void cleanUp()
	{
		if(inputFile != null)
		{
			inputFile.delete();
			inputFile = null;
		}
	}

	@Test
	public void correctInputFactoryIsObtained()
	{
		String factoryClassName = Log4jImportCallable.XML_INPUT_FACTORY.getClass().getName();
		assertTrue(factoryClassName, factoryClassName.startsWith("com.ctc.wstx.stax"));
	}

	@Test
	public void example()
		throws Exception
	{
		createTempFile("/testcases/example.log");
		AppendOpStub buffer = new AppendOpStub();
		Log4jImportCallable instance = new Log4jImportCallable(inputFile, buffer);
		long result = instance.call();
		if(logger.isInfoEnabled()) logger.info("Call returned {}.", result);
		if(logger.isDebugEnabled()) logger.debug("Appended events: {}", buffer.getList());
		assertEquals(2, result);
		assertEquals(2, buffer.getList().size());
	}

	@Test
	public void exampleNoLF()
		throws Exception
	{
		createTempFile("/testcases/exampleNoLF.log");
		AppendOpStub buffer = new AppendOpStub();
		Log4jImportCallable instance = new Log4jImportCallable(inputFile, buffer);
		long result = instance.call();
		if(logger.isInfoEnabled()) logger.info("Call returned {}.", result);
		if(logger.isDebugEnabled()) logger.debug("Appended events: {}", buffer.getList());
		assertEquals(2, result);
		assertEquals(2, buffer.getList().size());
	}

	public void createTempFile(String resourceName)
		throws IOException
	{
		InputStream input = Log4jImportCallableTest.class.getResourceAsStream(resourceName);
		if(input == null)
		{
			fail("Couldn't resolve resource '" + resourceName + "'!");
		}
		inputFile = File.createTempFile("Import", "test");
		inputFile.delete();
		try(OutputStream output = Files.newOutputStream(inputFile.toPath()))
		{
			IOUtils.copyLarge(input, output);
		}
	}

	private static class AppendOpStub
		implements AppendOperation<EventWrapper<LoggingEvent>>
	{
		private final List<EventWrapper<LoggingEvent>> list;

		AppendOpStub()
		{
			list = new ArrayList<>();
		}

		public List<EventWrapper<LoggingEvent>> getList()
		{
			return list;
		}

		@Override
		public void add(EventWrapper<LoggingEvent> element)
		{
			list.add(element);
		}

		@Override
		public void addAll(List<EventWrapper<LoggingEvent>> elements)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void addAll(EventWrapper<LoggingEvent>[] elements)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isFull()
		{
			throw new UnsupportedOperationException();
		}
	}
}
