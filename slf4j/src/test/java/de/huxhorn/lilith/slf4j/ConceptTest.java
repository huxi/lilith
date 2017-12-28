package de.huxhorn.lilith.slf4j;

import java.io.IOException;
import org.junit.After;
import org.junit.Test;


public class ConceptTest
{
	private final Logger logger = LoggerFactory.getLogger(ConceptTest.class);
	private final org.slf4j.Logger slf4jLogger = org.slf4j.LoggerFactory.getLogger(ConceptTest.class);

	@After
	public void afterTest()
		throws InterruptedException
	{
		Thread.sleep(1000);
	}

	@Test
	@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
	public void log()
	{
		logger.log(Logger.Level.DEBUG, "Foobar");
		logger.log(Logger.Level.DEBUG, "Params {}{}", "Arg1", "Arg2");
		try
		{
			throw new RuntimeException("Bar");
		}
		catch(RuntimeException ex)
		{
			logger.log(Logger.Level.DEBUG, "Params with Throwable {}{}", "Arg1", "Arg2", ex);
			slf4jLogger.debug("SLF4J-Message with Throwable", ex);
		}
		try
		{
			throw new RuntimeException("Bar", new IOException("Foo"));
		}
		catch(RuntimeException ex)
		{
			logger.log(Logger.Level.DEBUG, "Params with Throwable & Cause {}{}", "Arg1", "Arg2", ex);
			slf4jLogger.debug("SLF4J-Message with Throwable & Cause", ex);
		}
	}

	@Test
	public void info()
	{
		logger.info("Foobar");
		logger.info("Params {}{}", "Arg1", "Arg2");
		logger.info("Params with Throwable {}{}", "Arg1", "Arg2", new RuntimeException("Bar"));
	}
}
