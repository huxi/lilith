package de.huxhorn.lilith.debug;

import de.huxhorn.sulky.tasks.AbstractProgressingCallable;

public class DebugProgressingCallable
	extends AbstractProgressingCallable<Long>
{
	//private final Logger logger = LoggerFactory.getLogger(DebugProgressingCallable.class);

	public Long call()
		throws Exception
	{
		Thread.sleep(2000);
		setNumberOfSteps(100);
		for(int i = 0; i <= 100; i++)
		{
			setCurrentStep(i);
			Thread.sleep(500);
			//if(logger.isInfoEnabled()) logger.info("At step {}...", i);
		}

		return 31337L;
	}
}
