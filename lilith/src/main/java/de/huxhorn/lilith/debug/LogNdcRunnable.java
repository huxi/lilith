package de.huxhorn.lilith.debug;

import de.huxhorn.lilith.logback.classic.NDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogNdcRunnable
	extends AbstractDebugRunnable
{
	private final Logger logger = LoggerFactory.getLogger(LogNdcRunnable.class);

	public LogNdcRunnable(int delay)
	{
		super(delay);
	}

	@Override
	public void runIt()
		throws InterruptedException
	{
		if(logger.isInfoEnabled()) logger.info("Message before pushing to NDC.");
		sleep();

		NDC.push("Message with parameters: {} {}", "foo", "bar");
		if(logger.isInfoEnabled()) logger.info("Message after pushing to NDC.");
		sleep();

		NDC.push("Another message with parameters: {} {}", "foo", "bar");
		if(logger.isInfoEnabled()) logger.info("Message after pushing to NDC again.");
		sleep();

		NDC.push("Simple message without parameters.");
		if(logger.isInfoEnabled()) logger.info("Message after pushing to NDC a third time.");
		sleep();

		NDC.pop();
		if(logger.isInfoEnabled()) logger.info("Message after popping the NDC once.");
		sleep();

		NDC.pop();
		if(logger.isInfoEnabled()) logger.info("Message after popping the NDC twice.");
		sleep();

		NDC.pop();
		if(logger.isInfoEnabled()) logger.info("Message after popping the NDC thrice.");
		sleep();
	}
}
