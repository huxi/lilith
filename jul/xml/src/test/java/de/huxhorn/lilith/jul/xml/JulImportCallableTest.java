package de.huxhorn.lilith.jul.xml;

import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.sulky.buffers.AppendOperation;

import de.huxhorn.sulky.io.IOUtilities;
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

public class JulImportCallableTest
{
	private final Logger logger = LoggerFactory.getLogger(JulImportCallableTest.class);

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
		createTempFile("/testcases/log.xml");
		AppendOpStub buffer = new AppendOpStub();
		JulImportCallable instance = new JulImportCallable(inputFile, buffer);
		long result = instance.call();
		if(logger.isInfoEnabled()) logger.info("Call returned {}.", result);
		if(logger.isDebugEnabled()) logger.debug("Appended events: {}", buffer.getList());
		assertEquals(3, result);
		assertEquals(3, buffer.getList().size());
	}

	public void createTempFile(String resourceName)
		throws IOException
	{
		InputStream input = JulImportCallableTest.class.getResourceAsStream(resourceName);
		if(input == null)
		{
			fail("Couldn't resolve resource '" + resourceName + "'!");
		}
		inputFile = File.createTempFile("Import", "test");
		inputFile.delete();
		FileOutputStream output = new FileOutputStream(inputFile);
		IOUtils.copyLarge(input, output);
		IOUtilities.closeQuietly(output);
	}

	private static class AppendOpStub
		implements AppendOperation<EventWrapper<LoggingEvent>>
	{
		private List<EventWrapper<LoggingEvent>> list;

		private AppendOpStub()
		{
			list = new ArrayList<EventWrapper<LoggingEvent>>();
		}

		public List<EventWrapper<LoggingEvent>> getList()
		{
			return list;
		}

		public void add(EventWrapper<LoggingEvent> element)
		{
			list.add(element);
		}

		public void addAll(List<EventWrapper<LoggingEvent>> elements)
		{
			throw new UnsupportedOperationException();
		}

		public void addAll(EventWrapper<LoggingEvent>[] elements)
		{
			throw new UnsupportedOperationException();
		}

		public boolean isFull()
		{
			throw new UnsupportedOperationException();
		}
	}
}
