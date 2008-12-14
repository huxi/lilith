package de.huxhorn.lilith.data.logging.logback;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import ch.qos.logback.classic.spi.ThrowableInformation;
import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import junit.framework.TestCase;

public class LogbackAdapterTest
	extends TestCase
{
	private final Logger logger = LoggerFactory.getLogger(LogbackAdapterTest.class);

	private LogbackLoggingAdapter instance;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		instance=new LogbackLoggingAdapter();
	}

	public void testThrowableStrRep()
	{
		Throwable t = produceThrowable();
		ThrowableInformation ti=new ThrowableInformation(t);
		String[] thrStrRep=ti.getThrowableStrRep();
		if(logger.isInfoEnabled()) logger.info("");
		ThrowableInfo tinfo = instance.initFromThrowableStrRepRecursive(thrStrRep, 0);
		assertEquals("yyy", tinfo.getMessage());
		assertEquals("java.lang.RuntimeException: foo", tinfo.getCause().getMessage());
		assertEquals("foo", tinfo.getCause().getCause().getMessage());
		assertNull(tinfo.getCause().getCause().getCause());
	}

	private Throwable produceThrowable()
	{
		Throwable t;
		try
		{
			try
			{
				try
				{
			        throw new RuntimeException("foo");
				}
				catch(Throwable x)
				{
					throw new RuntimeException(x);
				}
			}
			catch(Throwable x)
			{
				throw new RuntimeException("yyy",x);
			}
		}
		catch(Throwable x)
		{
			t=x;
		}
		return t;
	}

	public void testThrowable()
	{
		Throwable t = produceThrowable();
		ThrowableInformation ti=new ThrowableInformation(t);
		String[] thrStrRep=ti.getThrowableStrRep();
		assertEquals(t, instance.getThrowable(ti));
		
		ThrowableInfo tinfo = instance.initFromThrowableStrRepRecursive(thrStrRep, 0);
		assertEquals("yyy", tinfo.getMessage());
		assertEquals("java.lang.RuntimeException: foo", tinfo.getCause().getMessage());
		assertEquals("foo", tinfo.getCause().getCause().getMessage());
		assertNull(tinfo.getCause().getCause().getCause());
	}

	public void testConvertEvent()
	{
		// LoggingEvent(String fqcn, Logger logger, Level level, String message, Throwable throwable, Object[] argArray)
		ch.qos.logback.classic.spi.LoggingEvent logbackEvent=
				new ch.qos.logback.classic.spi.LoggingEvent(
						"de.huxhorn.lilith.data.logging.logback.LogbackAdapterTest",
						(ch.qos.logback.classic.Logger)logger,
						Level.INFO,
						"Message",
						produceThrowable(),
						new String[]{"First", null, "Third"}

						);
		LoggingEvent lilithEvent = instance.convert(logbackEvent);
		if(logger.isInfoEnabled()) logger.info("lilithEvent: {}", lilithEvent);
		prettyPrint(lilithEvent);
	}

	private void prettyPrint(LoggingEvent event)
	{
		if(logger.isDebugEnabled())
		{
			StringBuilder msg=new StringBuilder();
			msg.append("Logger         : ").append(event.getLogger());
			msg.append("\n");

			msg.append("Message        : ").append(event.getMessage());
			msg.append("\n");

			msg.append("Level          : ").append(event.getLevel());
			msg.append("\n");

			msg.append("Thread-Name    : ").append(event.getThreadName());
			msg.append("\n");

			msg.append("TimeStamp      : ").append(event.getTimeStamp());
			msg.append("\n");

			msg.append("Message-Pattern: ").append(event.getMessagePattern());
			msg.append("\n");
			String[] args = event.getArguments();
			if(args!=null)
			{
				List<String> argList=new ArrayList<String>(args.length);
				argList.addAll(Arrays.asList(args));
				msg.append("Arguments      : ").append(argList);
				msg.append("\n");
			}

			ExtendedStackTraceElement[] callStack = event.getCallStack();
			if(callStack!=null)
			{
				msg.append("Call-Stack     : ");
				msg.append("\n");
				for(ExtendedStackTraceElement ste: callStack)
				{
					msg.append("\t").append(ste).append("\n");
				}
				msg.append("\n");
			}

			Marker marker = event.getMarker();
			if(marker!=null)
			{
				msg.append("Marker         : ");
				msg.append(marker);
				msg.append("\n");
			}
			Map<String, String> mdc = event.getMdc();
			if(mdc!=null)
			{
				msg.append("MDC            : ");
				msg.append("\n");
				for(Map.Entry<String, String> current:mdc.entrySet())
				{
					msg.append("\t").append(current.getKey()).append(": ").append(current.getValue());
					msg.append("\n");
				}
			}
			ThrowableInfo ti = event.getThrowable();
			if(ti!=null)
			{
				msg.append("Throwable      : ");
				msg.append("\n");
				ThrowableInfo current=ti;
				StringBuilder indent=new StringBuilder("  ");
				while(current!=null)
				{
					msg.append(indent.toString());
					msg.append("Name      : ").append(current.getName());
					msg.append("\n");
					msg.append(indent.toString());
					msg.append("Message   : ").append(current.getMessage());
					msg.append("\n");
					msg.append(indent.toString());
					msg.append("StackTrace: ");
					msg.append("\n");
					indent.append("  ");
					ExtendedStackTraceElement[] stackTrace = current.getStackTrace();
					if(stackTrace!=null)
					{
						for(ExtendedStackTraceElement ste:stackTrace)
						{
							msg.append(indent.toString());
							msg.append(ste);
							msg.append("\n");
						}
					}
					indent.append("  ");
					current=current.getCause();
				}
			}
			logger.debug(msg.toString());
		}
	}
}