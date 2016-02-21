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
				System.out.println("printStackTrace()");
				newEx.printStackTrace(System.out);
			}

			if(logger.isDebugEnabled()) logger.debug("Plain exception!", new RuntimeException());
		}

		public static void foobar()
		{
			RuntimeException t = new RuntimeException("Hi.");
			t.addSuppressed(new RuntimeException());
			t.addSuppressed(new RuntimeException("Single line"));
			RuntimeException r=new RuntimeException("With cause and suppressed", new RuntimeException("Cause"));
			r.addSuppressed(new RuntimeException("Inner Suppressed"));
			r.addSuppressed(new RuntimeException("Inner Suppressed with Cause", new RuntimeException("Inner Cause")));
			t.addSuppressed(r);
			t.addSuppressed(new RuntimeException("Multi\nline"));
			throw t;
		}
	}


	@SuppressWarnings({"ThrowableInstanceNeverThrown"})
	public static void main(String args[])
	{
		final Logger logger = Logger.getLogger(Log4jSandbox.class);

		RuntimeException x=new RuntimeException(
			new RuntimeException("Cause"));
		x.addSuppressed(new RuntimeException("Suppressed1"));
		x.addSuppressed(new RuntimeException("Suppressed2"));

		x.printStackTrace();

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
