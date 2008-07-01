package de.huxhorn.lilith.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class LogStuffRunnable
	extends AbstractDebugRunnable
{
	private final Logger logger = LoggerFactory.getLogger(LogStuffRunnable.class);
	private Marker marker;

	public LogStuffRunnable(int delay, Marker marker)
	{
		super(delay);
		this.marker=marker;
	}

	public void runIt() throws InterruptedException
	{
		if(logger.isTraceEnabled()) logger.trace(marker, "A trace message.");
		sleep();
		if(logger.isDebugEnabled()) logger.debug(marker, "A debug message.");
		sleep();
		if(logger.isInfoEnabled()) logger.info(marker, "A info message.");
		sleep();
		if(logger.isWarnEnabled()) logger.warn(marker, "A warn message.");
		sleep();
		if(logger.isErrorEnabled()) logger.error(marker, "A error message.");
		sleep();
	}
}
