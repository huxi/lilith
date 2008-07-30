package de.huxhorn.lilith.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class LogParamThrowableRunnable
	extends AbstractDebugRunnable
{
	private final Logger logger = LoggerFactory.getLogger(LogStuffRunnable.class);
	private Throwable throwable;

	public LogParamThrowableRunnable(int delay, Throwable throwable)
	{
		super(delay);
		this.throwable=throwable;
	}

	public void runIt() throws InterruptedException
	{
		Object[] params = new Object[]{"One", "Two", "Three", throwable};
		if(logger.isTraceEnabled()) logger.trace("A trace message. param1={}, param2={}, param3={}", params);
		sleep();
		if(logger.isDebugEnabled()) logger.debug("A debug message. param1={}, param2={}, param3={}", params);
		sleep();
		if(logger.isInfoEnabled()) logger.info("A info message. param1={}, param2={}, param3={}", params);
		sleep();
		if(logger.isWarnEnabled()) logger.warn("A warn message. param1={}, param2={}, param3={}", params);
		sleep();
		if(logger.isErrorEnabled()) logger.error("A error message. param1={}, param2={}, param3={}", params);
		sleep();
		if(logger.isInfoEnabled()) logger.info("A info message. param1={}, param2={}, param3={}, exceptionString={}", params);
		sleep();
	}
}