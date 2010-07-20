package de.huxhorn.lilith.sandbox;

import org.apache.log4j.*;

public class Log4jSandbox
{
	public static class InnerClass
	{
		@SuppressWarnings({"ThrowableInstanceNeverThrown"})
		public static void execute()
		{
			final Logger logger = Logger.getLogger(InnerClass.class);
			try
			{
				foobar();
			}
			catch(RuntimeException ex)
			{
				RuntimeException newEx = new RuntimeException("Hello", ex);
				if(logger.isDebugEnabled()) logger.debug("Foo!",newEx);
			}
			try
			{
				foobar();
			}
			catch(RuntimeException ex)
			{
				RuntimeException newEx = new RuntimeException("Multi\nline\nmessage", ex);
				if(logger.isDebugEnabled()) logger.debug("Foo!",newEx);
			}
		}
		
		public static void foobar()
		{
			throw new RuntimeException("Hi.");
		}
	}


	@SuppressWarnings({"ThrowableInstanceNeverThrown"})
	public static void main(String args[])
	{
		final Logger logger = Logger.getLogger(Log4jSandbox.class);
		NDC.push("NDC1");
		NDC.push("NDC2");
		NDC.push("NDC with spaces...");
		MDC.put("key1", "value1");
		MDC.put("key2", "value2");
		if(logger.isDebugEnabled()) logger.debug("Foobar!", new Throwable());

		for(;;)
		{
			InnerClass.execute();
			logger.trace("Trace!");
			logger.debug("Debug!");
			logger.info("Info!");
			logger.warn("Warn!");
			logger.error("Error!");
			logger.fatal("Fatal!");
			try
			{
				Thread.sleep(100);
			}
			catch(InterruptedException ex)
			{
				break;
			}
		}
	}
}
