package de.huxhorn.lilith.sandbox;

import org.apache.logging.log4j.*;

public class Log4j2Sandbox
{
	public static class InnerClass
	{
		@SuppressWarnings({"ThrowableInstanceNeverThrown"})
		public static void execute()
		{
			final Logger logger = LogManager.getLogger(InnerClass.class);
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
			FooException ex = new FooException("Hi.");
			
			ex.addSuppressed(new RuntimeException("Suppressed1"));
			ex.addSuppressed(new RuntimeException("Suppressed2"));
			
			throw ex;
		}
	}


	@SuppressWarnings({"ThrowableInstanceNeverThrown"})
	public static void main(String args[])
	{
		final Logger logger = LogManager.getLogger(Log4j2Sandbox.class);
		ThreadContext.push("NDC1");
		ThreadContext.push("NDC2");
		ThreadContext.push("NDC with spaces...");
		ThreadContext.put("key1", "value1");
		ThreadContext.put("key2", "value2");
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
	
	public static class FooException
		extends RuntimeException
	{
		public FooException(String msg)
		{
			super(msg);
		}
	}
}
