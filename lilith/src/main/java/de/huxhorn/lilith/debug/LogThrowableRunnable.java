package de.huxhorn.lilith.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class LogThrowableRunnable
	extends AbstractDebugRunnable
{
	private final Logger logger = LoggerFactory.getLogger(LogStuffRunnable.class);
	private Throwable throwable;

	public LogThrowableRunnable(int delay, Throwable throwable)
	{
		super(delay);
		this.throwable=throwable;
	}

	public void runIt() throws InterruptedException
	{
		if(logger.isTraceEnabled()) logger.trace("A trace message.", throwable);
		sleep();
		if(logger.isDebugEnabled()) logger.debug("A debug message.",throwable);
		sleep();
		if(logger.isInfoEnabled()) logger.info("A info message.",throwable);
		sleep();
		if(logger.isWarnEnabled()) logger.warn("A warn message.",throwable);
		sleep();
		if(logger.isErrorEnabled()) logger.error("A error message.",throwable);
		sleep();
	}
}