package de.huxhorn.lilith.debug;

import de.huxhorn.lilith.logback.classic.NDC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogNdcRunnable
	extends AbstractDebugRunnable
{
	private final Logger logger = LoggerFactory.getLogger(LogStuffRunnable.class);

	public LogNdcRunnable(int delay)
	{
		super(delay);
	}

	public void runIt()
		throws InterruptedException
	{
		if(logger.isInfoEnabled()) logger.info("Message before pushing to NDC.");
		sleep();

		NDC.push("Message with parameters: {} {}", new String[]{"foo", "bar"});
		if(logger.isInfoEnabled()) logger.info("Message after pushing to NDC.");
		sleep();

		NDC.push("Another message with parameters: {} {}", new String[]{"foo", "bar"});
		if(logger.isInfoEnabled()) logger.info("Message after pushing to NDC again.");
		sleep();

		NDC.pop();
		if(logger.isInfoEnabled()) logger.info("Message after popping the NDC once.");
		sleep();

		NDC.pop();
		if(logger.isInfoEnabled()) logger.info("Message after popping the NDC twice.");
		sleep();
	}
}
