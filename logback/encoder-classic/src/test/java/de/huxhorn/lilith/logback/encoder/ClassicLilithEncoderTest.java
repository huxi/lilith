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

package de.huxhorn.lilith.logback.encoder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import java.io.IOException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class ClassicLilithEncoderTest
{

	@Test
	public void headerBytesWorks() throws IOException
	{
		ClassicLilithEncoder instance = new ClassicLilithEncoder();

		byte[] header = instance.headerBytes();
		assertNotNull(header);
		assertTrue(header.length > 4);
	}

	@Test
	public void encodingWorks() throws IOException
	{
		final Logger logger = LoggerFactory.getLogger(ClassicLilithEncoderTest.class);
		ClassicLilithEncoder instance = new ClassicLilithEncoder();

		instance.setIncludeCallerData(true);

		LoggingEvent event=new LoggingEvent(logger.getClass().getName(), (ch.qos.logback.classic.Logger) logger, Level.INFO, "Test", null, null);

		byte[] encoded = instance.encode(event);
		assertNotNull(encoded);
		assertTrue(encoded.length > 0);
	}
}
