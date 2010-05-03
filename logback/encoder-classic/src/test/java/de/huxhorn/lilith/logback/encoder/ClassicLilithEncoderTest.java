/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
package de.huxhorn.lilith.logback.encoder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.recovery.ResilientFileOutputStream;
import de.huxhorn.lilith.data.access.protobuf.AccessEventProtobufEncoder;
import de.huxhorn.lilith.data.logging.logback.TransformingEncoder;
import de.huxhorn.lilith.api.FileConstants;
import de.huxhorn.lilith.data.logging.protobuf.LoggingEventProtobufEncoder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


public class ClassicLilithEncoderTest
{
	private final Logger logger = LoggerFactory.getLogger(ClassicLilithEncoderTest.class);

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testIt() throws IOException
	{
		ClassicLilithEncoder instance = new ClassicLilithEncoder();
		File file=folder.newFile("foo.lilith");
		ResilientFileOutputStream fos=new ResilientFileOutputStream(file, false);

		instance.init(fos);

		instance.setIncludeCallerData(true);

		LoggingEvent event=new LoggingEvent(logger.getClass().getName(), (ch.qos.logback.classic.Logger) logger, Level.INFO, "Test", null, null);
		for(int i=0;i<100;i++)
		{
			instance.doEncode(event);
		}

		instance.close();
		System.out.println("File "+file.getAbsolutePath()+" has a size of "+file.length()+".");
	}


}
