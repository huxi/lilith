package de.huxhorn.lilith.sandbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackClassicSandbox
{
	public static class InnerClass
	{
		int counter;
		public void execute()
		{
			final Logger logger = LoggerFactory.getLogger(InnerClass.class);
			if(logger.isDebugEnabled()) logger.debug("Foo! counter={}", counter);
			counter++;
		}
	}


	public static void main(String args[])
		throws Exception
	{
		final Logger logger = LoggerFactory.getLogger(LogbackClassicSandbox.class);

		if(logger.isDebugEnabled()) logger.debug("args: {}", args);

		InnerClass inner=new InnerClass();
		for(;;)
		{
			inner.execute();
			Thread.sleep(1000);
		}
	}
}
