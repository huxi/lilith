package de.huxhorn.lilith.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class LogAnonymousRunnable
	extends AbstractDebugRunnable
{
	private final Logger logger = LoggerFactory.getLogger(LogStuffRunnable.class);

	public LogAnonymousRunnable(int delay)
	{
		super(delay);
	}

	public interface StuffLogger
	{
		void logStuff();
	}

	public void runIt()
	{
		class MethodInternal
			implements StuffLogger
		{
			public void logStuff()
			{
				if(logger.isErrorEnabled()) logger.error("MethodInternalClass");

			}
		}

		StuffLogger stuffLogger=new StuffLogger()
		{
			class AnonymousInternalClass
				implements StuffLogger
			{
				public void logStuff()
				{
					if(logger.isErrorEnabled()) logger.error("AnonymousInternalClass");

				}
			}

			StuffLogger stuffLogger=new StuffLogger()
			{
				public void logStuff()
				{
					if(logger.isWarnEnabled()) logger.warn("SecondLevelAnonymous");
				}
			};

			public void logStuff()
			{
				if(logger.isInfoEnabled()) logger.info("FirstLevelAnonymous");
				stuffLogger.logStuff();
				new AnonymousInternalClass().logStuff();
				new MethodInternal().logStuff();
			}
		};
		stuffLogger.logStuff();
	}
}