package de.huxhorn.lilith.sandbox;

import org.apache.log4j.*;

public class Log4jSandbox
{
	public static class InnerClass
	{
		public static void execute()
		{
			final Logger logger = Logger.getLogger(InnerClass.class);
			try
			{
				foobar();
			}
			catch(RuntimeException ex)
			{
				if(logger.isDebugEnabled()) logger.debug("Foo!",new RuntimeException("Hello", ex));
			}
		}
		
		public static void foobar()
		{
			throw new RuntimeException("Hi.");
		}
	}


	public static void main(String args[])
	{
		final Logger logger = Logger.getLogger(Log4jSandbox.class);
		NDC.push("NDC1");
		NDC.push("NDC2");
		MDC.put("key1", "value1");
		MDC.put("key2", "value2");
		if(logger.isDebugEnabled()) logger.debug("Foobar!", new Throwable());

		InnerClass.execute();
	}
}
