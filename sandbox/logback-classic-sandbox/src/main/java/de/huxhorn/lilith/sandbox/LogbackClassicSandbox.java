package de.huxhorn.lilith.sandbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.MarkerFactory;
import org.slf4j.Marker;
import ch.qos.logback.classic.LoggerContext;


public class LogbackClassicSandbox
{
	private static final Marker FOO_MARKER = MarkerFactory.getDetachedMarker("foo-marker");
	private static final Marker BAR_MARKER = MarkerFactory.getDetachedMarker("bar-marker");
	
	static
	{
		FOO_MARKER.add(BAR_MARKER);
	}
		
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

		int count = 50;

		if(args != null && args.length > 0)
		{
			count = Integer.parseInt(args[0]);
		}

		if(logger.isDebugEnabled()) logger.debug("args: {}", (Object[])args);

		MDC.put("key1", "value1");
		MDC.put("key2", "value2");
		if(logger.isDebugEnabled()) logger.debug("Foobar!", new Throwable());

		for(int i=0;i<count;i++)
		{
			InnerClass.execute();
			logger.trace("Trace!");
			logger.debug("Debug!");
			logger.info("Info!");
			logger.warn("Warn!");
			logger.error("Error!");
			logger.info(FOO_MARKER, "Info with marker!");
			try
			{
				Thread.sleep(100);
			}
			catch(InterruptedException ex)
			{
				break;
			}
		}

		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.reset();

		for(int i=0;i<count;i++)
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
		System.out.println("before context.stop()");
		loggerContext.stop();
		System.out.println("End of main()");
	}
}
