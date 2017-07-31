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
import org.apache.logging.log4j.message.FormattedMessage;
import org.apache.logging.log4j.message.LocalizedMessage;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFormatMessage;
import org.apache.logging.log4j.message.ObjectMessage;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.apache.logging.log4j.message.StructuredDataId;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.apache.logging.log4j.message.ThreadDumpMessage;

import org.apache.logging.log4j.message.ObjectArrayMessage;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;

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


	private static class InnerClass
	{
		@SuppressWarnings({"ThrowableInstanceNeverThrown"})
		static void execute()
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

		static void foobar()
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

	@SuppressWarnings({"ThrowableInstanceNeverThrown", "ThrowableResultOfMethodCallIgnored"})
	public static void main(String args[])
	{
		final Logger logger = LogManager.getLogger(Log4j2Sandbox.class);
		ThreadContext.push("NDC1");
		ThreadContext.push("NDC2");
		ThreadContext.push("NDC with spaces...");
		ThreadContext.push("NDC with parameter {} and {}...", "foo", "bar");
		ThreadContext.put("key1", "value1");
		ThreadContext.put("key2", "value2");
		logger.debug("########## Start ##########");
		if(logger.isDebugEnabled()) logger.debug("Foobar!", new Throwable());

		for(;;)
		{
			logger.debug("########## loop ##########");
			InnerClass.execute();
			logger.trace("Trace!");
			logger.debug("Debug!");
			logger.info("Info!");
			logger.warn("Warn!");
			logger.error("Error!");
			logger.fatal("Fatal!");
			logger.catching(Level.INFO, prepareException());
			logger.catching(prepareException());
			logger.debug(PRIVATE_FOO_MARKER, "private Foo Marker");
			logger.debug(PRIVATE_BAR_MARKER, "private Bar Marker");
			logger.debug(PRIVATE_FOOBAR_MARKER, "private Foobar Marker");
			logger.debug(GLOBAL_FOO_MARKER, "global Foo Marker");
			logger.debug(GLOBAL_BAR_MARKER, "global Bar Marker");
			logger.debug(GLOBAL_FOOBAR_MARKER, "global Foobar Marker");

			// see
			// https://issues.apache.org/jira/browse/LOG4J2-1226
			// https://issues.apache.org/jira/browse/LOG4J2-1675
			// https://issues.apache.org/jira/browse/LOG4J2-1676
			logger.debug((Message)new SimpleMessage("simple message"));

			logger.debug(new FormattedMessage("curly-brackets FormattedMessage {} {}", new Object[]{"foo", "bar"}));
			logger.debug(new FormattedMessage("curly-brackets FormattedMessage {} {} with Throwable", new Object[]{"foo", "bar", prepareException()}));
			logger.debug(new FormattedMessage("curly-brackets FormattedMessage {} {} with Throwable {}", new Object[]{"foo", "bar", prepareException()}));
			logger.debug(new FormattedMessage("curly-brackets FormattedMessage {} {} with explicit Throwable", new Object[]{"foo", "bar"}, new FooException("foo exception")));
			logger.debug(new FormattedMessage("curly-brackets FormattedMessage {} {}", new Object[]{new Foo(), "bar"}));

			logger.debug(new FormattedMessage("percent-s FormattedMessage %s %s", new Object[]{"foo", "bar"}));
			logger.debug(new FormattedMessage("percent-s FormattedMessage %s %s with Throwable", new Object[]{"foo", "bar", prepareException()}));
			logger.debug(new FormattedMessage("percent-s FormattedMessage %s %s with Throwable %s", new Object[]{"foo", "bar", prepareException()}));

			logger.debug(new LocalizedMessage("LocalizedMessage %s %s", new Object[]{"foo", "bar"}));
			logger.debug(new LocalizedMessage("LocalizedMessage %s %s with Throwable", new Object[]{"foo", "bar", prepareException()}));
			logger.debug(new LocalizedMessage("LocalizedMessage %s %s with Throwable %s", new Object[]{"foo", "bar", prepareException()}));

			logger.debug(new MessageFormatMessage("MessageFormatMessage {0}", "foo"));
			logger.debug(new MessageFormatMessage("MessageFormatMessage {0}", new Foo()));
			logger.debug(new MessageFormatMessage("MessageFormatMessage {0} with Throwable", new Foo(), prepareException()));
			logger.debug(new MessageFormatMessage("MessageFormatMessage {0} with Throwable {1}", new Foo(), prepareException()));

			logger.debug(new ParameterizedMessage("ParameterizedMessage {}", "foo"));
			logger.debug(new ParameterizedMessage("ParameterizedMessage {}", new Foo()));
			logger.debug(new ParameterizedMessage("ParameterizedMessage {} with Throwable", new Foo(), prepareException()));
			logger.debug(new ParameterizedMessage("ParameterizedMessage {} with Throwable {}", new Foo(), prepareException()));

			logger.debug(new StringFormattedMessage("StringFormattedMessage %s", "foo"));
			logger.debug(new StringFormattedMessage("StringFormattedMessage %s", new Foo()));
			logger.debug(new StringFormattedMessage("StringFormattedMessage %s with Throwable", new Foo(), prepareException()));
			logger.debug(new StringFormattedMessage("StringFormattedMessage %s with Throwable %s", new Foo(), prepareException()));

			// https://issues.apache.org/jira/browse/LOG4J2-1226?focusedCommentId=15645784&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-15645784
			// "If you want to log a Throwable with a pre-existing Message object, you need to use one of the log(Message,Throwable) methods."
			logger.debug(new FormattedMessage("curly-brackets FormattedMessage {} {} with Throwable as expected", new Object[]{"foo", "bar"}), prepareException());
			logger.debug(new FormattedMessage("percent-s FormattedMessage %s %s with Throwable as expected", new Object[]{"foo", "bar"}), prepareException());
			logger.debug(new LocalizedMessage("LocalizedMessage %s %s with Throwable as expected", new Object[]{"foo", "bar"}), prepareException());
			logger.debug(new MessageFormatMessage("MessageFormatMessage {0} with Throwable as expected", new Foo()), prepareException());
			logger.debug(new ParameterizedMessage("ParameterizedMessage {} with Throwable as expected", new Foo()), prepareException());
			logger.debug(new StringFormattedMessage("StringFormattedMessage %s with Throwable as expected", new Foo()), prepareException());

			Map<String, String> map= new HashMap<>();
			map.put("fooKey", "fooValue");
			map.put("barKey", "barValue");
			logger.debug(new MapMessage(map));


			logger.debug(new ObjectMessage("ObjectMessage"));
			logger.debug(new ObjectMessage(new Foo()));

			logger.debug(new StructuredDataMessage(new StructuredDataId("dataIdName", 17, new String[]{"fooRequired"}, new String[]{"fooOptional"}), "StructuredDataMessage", "fooType", map));

			logger.debug(new ThreadDumpMessage("threadDumpTitle"));

			logger.debug(new FooMessage());

			objectArrayMessage(logger);
			deprecatedMessageSupplier(logger);
			supplier(logger);
			
			logger.debug("########## End ##########");
			try
			{
				Thread.sleep(500);
			}
			catch(InterruptedException ex)
			{
				break;
			}
		}
	}

	private static void objectArrayMessage(Logger logger)
	{
		logger.debug(new ObjectArrayMessage("ObjectArrayMessage", "String"));
		logger.debug(new ObjectArrayMessage("ObjectArrayMessage", new Foo()));
	}
	
	@SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef", "deprecation"})
	private static void deprecatedMessageSupplier(Logger logger)
	{
		MessageSupplier simpleMessageMessageSupplier=new MessageSupplier()
		{
			@Override
			public Message get()
			{
				return new SimpleMessage("simple message MessageSupplier");
			}
		};
		logger.debug(simpleMessageMessageSupplier);

		MessageSupplier formattedMessageMessageSupplier=new MessageSupplier()
		{
			@Override
			public Message get()
			{
				return new FormattedMessage("formatted message MessageSupplier {} {}", new Object[]{"foo", "bar"}, new FooException("foo exception"));
			}
		};
		logger.debug(formattedMessageMessageSupplier);


		MessageSupplier fooMessageMessageSupplier=new MessageSupplier()
		{
			@Override
			public Message get()
			{
				return new FooMessage();
			}
		};
		logger.debug(fooMessageMessageSupplier);

		logger.debug(fooMessageMessageSupplier, prepareException());
	}

	@SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
	private static void supplier(Logger logger)
	{
		Supplier<Message> simpleMessageSupplier=new Supplier<Message>()
		{
			@Override
			public Message get()
			{
				return new SimpleMessage("simple message Supplier<Message>");
			}
		};
		logger.debug(simpleMessageSupplier);

		Supplier<Message> formattedMessageSupplier=new Supplier<Message>()
		{
			@Override
			public Message get()
			{
				return new FormattedMessage("formatted message Supplier<Message> {} {}", new Object[]{"foo", "bar"}, new FooException("foo exception"));
			}
		};
		logger.debug(formattedMessageSupplier);


		Supplier<Message> fooMessageSupplier=new Supplier<Message>()
		{
			@Override
			public Message get()
			{
				return new FooMessage();
			}
		};
		logger.debug(fooMessageSupplier);

		logger.debug(fooMessageSupplier, prepareException());
	}

	private static class FooException
		extends RuntimeException
	{
		private static final long serialVersionUID = 8987753386120938525L;

		FooException(String msg)
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

	private static class Foo implements Serializable
	{
		private static final long serialVersionUID = 7746306314408528044L;

		public String toString()
		{
			return "Foo object";
		}
	}

}
