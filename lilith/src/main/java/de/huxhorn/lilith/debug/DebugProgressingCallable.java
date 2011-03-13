package de.huxhorn.lilith.debug;

import de.huxhorn.sulky.tasks.AbstractProgressingCallable;

public class DebugProgressingCallable
	extends AbstractProgressingCallable<Long>
{
	public Long call()
		throws Exception
	{
		Thread.sleep(2000);
		setNumberOfSteps(100);
		for(int i = 0; i <= 100; i++)
		{
			setCurrentStep(i);
			Thread.sleep(500);
		}

		return 31337L;
	}
}
