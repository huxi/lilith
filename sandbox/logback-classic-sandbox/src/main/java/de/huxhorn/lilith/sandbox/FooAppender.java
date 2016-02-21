package de.huxhorn.lilith.sandbox;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class FooAppender extends AppenderBase<ILoggingEvent>
{
	// http://jira.qos.ch/browse/LOGBACK-925
	protected void append(ILoggingEvent eventObject)
	{
	}

	public void start()
	{
		super.start();
		System.out.println("############################################################");
		System.out.println("############################################################");
		System.out.println(""+getName()+" started!");
		System.out.println("############################################################");
		System.out.println("############################################################");
	}

	public void stop()
	{
		super.stop();
		System.out.println("############################################################");
		System.out.println("############################################################");
		System.out.println(""+getName()+" stopped!");
		System.out.println("############################################################");
		System.out.println("############################################################");
	}

}
