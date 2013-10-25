package de.huxhorn.lilith.sandbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LogbackClassicSandbox
{
	public static class InnerClass
	{
		@SuppressWarnings({"ThrowableInstanceNeverThrown"})
		public static void execute()
		{
			final Logger logger = LoggerFactory.getLogger(InnerClass.class);
			
			try
			{
				foobar();
			}
			catch(RuntimeException ex)
			{
				if(logger.isDebugEnabled()) logger.debug("Just an exception!", ex);
			}

			try
			{
				foobar();
			}
			catch(RuntimeException ex)
			{
				RuntimeException newEx = new RuntimeException("Hello", ex);
				if(logger.isDebugEnabled()) logger.debug("Exception with simple message!", newEx);
			}
			
			try
			{
				foobar();
			}
			catch(RuntimeException ex)
			{
				RuntimeException newEx = new RuntimeException("Multi\nline\nmessage", ex);
				if(logger.isDebugEnabled()) logger.debug("Exception with multiline message!", newEx);
			}
			
			try
			{
				foobar();
			}
			catch(RuntimeException ex)
			{
				RuntimeException newEx = new RuntimeException(ex);
				if(logger.isDebugEnabled()) logger.debug("Exception with no message!", newEx);
			}

			if(logger.isDebugEnabled()) logger.debug("Plain exception!", new RuntimeException());
		}
		
		public static void foobar()
		{
			RuntimeException t = new RuntimeException("Hi.");
			t.addSuppressed(new RuntimeException());
			t.addSuppressed(new RuntimeException("Single line"));
			t.addSuppressed(new RuntimeException("Multi\nline"));
			throw t;
		}
	}


	public static void main(String args[])
		throws Exception
	{
		final Logger logger = LoggerFactory.getLogger(LogbackClassicSandbox.class);

		if(logger.isDebugEnabled()) logger.debug("args: {}", (Object[])args);

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
