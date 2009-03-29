package de.huxhorn.lilith.log4j.xml;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.sulky.buffers.AppendOperation;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Log4jImporterTest
{
	private final Logger logger = LoggerFactory.getLogger(Log4jImporterTest.class);

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
	public void example()
		throws Exception
	{
		createTempFile("/testcases/example.log");
		AppendOpStub buffer = new AppendOpStub();
		Log4jImporter instance = new Log4jImporter(inputFile, buffer);
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
		Log4jImporter instance = new Log4jImporter(inputFile, buffer);
		long result = instance.call();
		if(logger.isInfoEnabled()) logger.info("Call returned {}.", result);
		if(logger.isDebugEnabled()) logger.debug("Appended events: {}", buffer.getList());
		assertEquals(2, result);
		assertEquals(2, buffer.getList().size());
	}

	public void createTempFile(String resourceName)
		throws IOException
	{
		InputStream input = Log4jImporterTest.class.getResourceAsStream(resourceName);
		if(input == null)
		{
			fail("Couldn't resolve resource '" + resourceName + "'!");
		}
		inputFile = File.createTempFile("Import", "test");
		inputFile.delete();
		FileOutputStream output = new FileOutputStream(inputFile);
		IOUtils.copyLarge(input, output);
		IOUtils.closeQuietly(output);
	}

	private static class AppendOpStub
		implements AppendOperation<LoggingEvent>
	{
		private List<LoggingEvent> list;

		private AppendOpStub()
		{
			list = new ArrayList<LoggingEvent>();
		}

		public List<LoggingEvent> getList()
		{
			return list;
		}

		public void add(LoggingEvent element)
		{
			list.add(element);
		}

		public void addAll(List<LoggingEvent> elements)
		{
			throw new UnsupportedOperationException();
		}

		public void addAll(LoggingEvent[] elements)
		{
			throw new UnsupportedOperationException();
		}

		public boolean isFull()
		{
			throw new UnsupportedOperationException();
		}
	}
}
