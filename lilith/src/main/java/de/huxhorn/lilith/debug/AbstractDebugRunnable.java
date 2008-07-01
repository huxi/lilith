package de.huxhorn.lilith.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDebugRunnable
	implements Runnable
{
	private final Logger logger = LoggerFactory.getLogger(AbstractDebugRunnable.class);

	private int delay;

	public AbstractDebugRunnable(int delay)
	{
		this.delay=delay;
	}

	public void sleep() throws InterruptedException
	{
		if(delay>0)
		{
			Thread.sleep(delay);
		}
	}

	public final void run()
	{
		try
		{
			runIt();
		}
		catch (InterruptedException e)
		{
			if(logger.isInfoEnabled()) logger.info("Execution of DebugRunnable was interrupted!");
		}
	}

	public abstract void runIt() throws InterruptedException;
}
