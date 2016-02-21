package de.huxhorn.lilith.sandbox;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFormatMessage;
import org.apache.logging.log4j.message.ObjectArrayMessage;
import org.apache.logging.log4j.message.ObjectMessage;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.apache.logging.log4j.message.StructuredDataId;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.apache.logging.log4j.message.ThreadDumpMessage;
import org.apache.logging.log4j.util.MessageSupplier;

public class Log4j2Sandbox
{
	private static final Marker PRIVATE_FOOBAR_MARKER = new MarkerManager.Log4jMarker("detached-foobar");
	private static final Marker PRIVATE_FOO_MARKER = new MarkerManager.Log4jMarker("detached-foo");
	private static final Marker PRIVATE_BAR_MARKER = new MarkerManager.Log4jMarker("detached-bar");

	static
	{
		PRIVATE_BAR_MARKER.addParents(PRIVATE_FOO_MARKER);
		PRIVATE_FOOBAR_MARKER.addParents(PRIVATE_FOO_MARKER, PRIVATE_BAR_MARKER);
	}

	private static final Marker GLOBAL_FOOBAR_MARKER = MarkerManager.getMarker("global-foobar");
	private static final Marker GLOBAL_FOO_MARKER = MarkerManager.getMarker("global-foo");
	private static final Marker GLOBAL_BAR_MARKER = MarkerManager.getMarker("global-bar");

	static
	{
		GLOBAL_BAR_MARKER.addParents(GLOBAL_FOO_MARKER);
		GLOBAL_FOOBAR_MARKER.addParents(GLOBAL_FOO_MARKER, GLOBAL_BAR_MARKER);
	}


	public static class InnerClass
	{
		@SuppressWarnings({"ThrowableInstanceNeverThrown"})
		public static void execute()
		{
			final Logger logger = LogManager.getLogger(InnerClass.class);
			try
			{
				foobar();
			}
			catch(RuntimeException ex)
			{
				RuntimeException newEx = new RuntimeException("Hello", ex);
				if(logger.isDebugEnabled()) logger.debug("Foo!",newEx);
			}
			try
			{
				foobar();
			}
			catch(RuntimeException ex)
			{
				RuntimeException newEx = new RuntimeException("Multi\nline\nmessage", ex);
				if(logger.isDebugEnabled()) logger.debug("Foo!",newEx);
			}
		}

		public static void foobar()
		{
			throw prepareException();
		}
	}

	private static RuntimeException prepareException()
	{
		RuntimeException ex = new FooException("Hi.");

		ex.addSuppressed(new RuntimeException("Suppressed1"));
		ex.addSuppressed(new RuntimeException("Suppressed2"));

		return ex;
	}

	@SuppressWarnings({"ThrowableInstanceNeverThrown"})
	public static void main(String args[])
	{
		final Logger logger = LogManager.getLogger(Log4j2Sandbox.class);
		ThreadContext.push("NDC1");
		ThreadContext.push("NDC2");
		ThreadContext.push("NDC with spaces...");
		ThreadContext.put("key1", "value1");
		ThreadContext.put("key2", "value2");
		if(logger.isDebugEnabled()) logger.debug("Foobar!", new Throwable());

		for(;;)
		{
			InnerClass.execute();
			logger.trace("Trace!");
			logger.debug("Debug!");
			logger.info("Info!");
			logger.warn("Warn!");
			logger.error("Error!");
			logger.fatal("Fatal!");
			logger.catching(Level.INFO, prepareException());
			logger.catching(prepareException());
			logger.debug(PRIVATE_FOO_MARKER, "private Foo");
			logger.debug(PRIVATE_BAR_MARKER, "private Bar");
			logger.debug(PRIVATE_FOOBAR_MARKER, "private Foobar");
			logger.debug(GLOBAL_FOO_MARKER, "global Foo");
			logger.debug(GLOBAL_BAR_MARKER, "global Bar");
			logger.debug(GLOBAL_FOOBAR_MARKER, "global Foobar");

			// see https://issues.apache.org/jira/browse/LOG4J2-1226

			// broken logger.debug(new FormattedMessage("formatted message {} {}", new Object[]{"foo", "bar"}));
			// broken logger.debug(new FormattedMessage("formatted message %s %s", new Object[]{"foo", "bar"}));

			// does nothing logger.debug(new LocalizedMessage("LocalizedMessage %s %s", new Object[]{"foo", "bar"}));

			Map<String, String> map=new HashMap<String, String>();
			map.put("fooKey", "fooValue");
			map.put("barKey", "barValue");
			logger.debug(new MapMessage(map));

			logger.debug(new MessageFormatMessage("MessageFormatMessage {0}", "Moep"));
			logger.debug(new MessageFormatMessage("MessageFormatMessage {0}", new Foo()));

			logger.debug(new ObjectArrayMessage("ObjectArrayMessage", "String"));
			// broken logger.debug(new ObjectArrayMessage("ObjectArrayMessage", new Foo()));

			logger.debug(new ObjectMessage("ObjectMessage"));
			// broken logger.debug(new ObjectMessage(new Foo()));

			logger.debug(new ParameterizedMessage("ParameterizedMessage {}", "foo"));
			logger.debug(new ParameterizedMessage("ParameterizedMessage {}", new Foo()));

			logger.debug(new SimpleMessage("simple message"));


			logger.debug(new StringFormattedMessage("StringFormattedMessage %s", "String"));
			logger.debug(new StringFormattedMessage("StringFormattedMessage %s", new Foo()));

			logger.debug(new StructuredDataMessage(new StructuredDataId("dataIdName", 17, new String[]{"fooRequired"}, new String[]{"fooOptional"}), "StructuredDataMessage", "fooType", map));

			logger.debug(new ThreadDumpMessage("title"));

			MessageSupplier simpleMessageSupplier=new MessageSupplier()
			{
				@Override
				public Message get()
				{
					return new SimpleMessage("simple message supplier");
				}
			};
			logger.debug(simpleMessageSupplier);

			//logger.debug(new FormattedMessage("formatted message {} {}", new Object[]{new Foo(), "bar"}));

//			MessageSupplier formattedMessageSupplier=new MessageSupplier()
//			{
//				@Override
//				public Message get()
//				{
//					return new FormattedMessage("formatted message supplier {} {}", new Object[]{"foo", "bar"}, new FooException("foo exception"));
//				}
//			};
//			logger.debug(formattedMessageSupplier);
//			logger.debug(new FormattedMessage("formatted message {} {}", new Object[]{"foo", "bar"}, new FooException("foo exception")));
//
//
//			MessageSupplier fooMessageSupplier=new MessageSupplier()
//			{
//				@Override
//				public Message get()
//				{
//					return new FooMessage();
//				}
//			};
//			logger.debug(fooMessageSupplier);
//			logger.debug(new FooMessage());

			try
			{
				Thread.sleep(100);
			}
			catch(InterruptedException ex)
			{
				break;
			}
		}
	}

	public static class FooException
		extends RuntimeException
	{
		private static final long serialVersionUID = 8987753386120938525L;

		public FooException(String msg)
		{
			super(msg);
		}
	}

	private static class FooMessage
			implements Message
	{
		private static final long serialVersionUID = -3246229797480524695L;

		@Override
		public String getFormattedMessage()
		{
			return "foo message";
		}

		@Override
		public String getFormat()
		{
			return "foo message format";
		}

		@Override
		public Object[] getParameters()
		{
			return new Object[]{"param1", "param2"};
		}

		@Override
		public Throwable getThrowable()
		{
			return new RuntimeException();
		}
	}

	public static class Foo implements Serializable
	{
		public String toString()
		{
			return "Foo object";
		}
	}

}
