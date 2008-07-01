package de.huxhorn.lilith.debug;

import org.slf4j.MDC;
import org.slf4j.Marker;

public class LogStuffWithMdcRunnable
	extends LogStuffRunnable
{
	public LogStuffWithMdcRunnable(int delay, Marker marker)
	{
		super(delay, marker);
	}

	public void runIt() throws InterruptedException
	{
		MDC.put("foo","bar");
		super.runIt();
		MDC.remove("foo");
	}
}