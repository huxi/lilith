package de.huxhorn.lilith.log4j2.producer;

import de.huxhorn.lilith.data.converter.Converter;
import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.lilith.data.logging.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.ThreadContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Log4j2LoggingConverter
		implements Converter<LoggingEvent>
{
	public static final String LOG4J_LEVEL_KEY = "log4j.level";
	public static final String LOG4J_LEVEL_VALUE_FATAL = "FATAL";
	private static final String APPLICATION_MDC_KEY = "application";

	public LoggingEvent convert(Object o)
	{
		if (o == null)
		{
			return null;
		}
		if (!(o instanceof org.apache.logging.log4j.core.LogEvent))
		{
			throw new IllegalArgumentException("" + o + " is not a " + getSourceClass() + "!");
		}
		org.apache.logging.log4j.core.LogEvent log4jEvent = (org.apache.logging.log4j.core.LogEvent) o;
		LoggingEvent result = new LoggingEvent();
		Map<String, String> mdc = new HashMap<String, String>();

		// loggerName
		result.setLogger(log4jEvent.getLoggerName());

		// level
		{
			Level log4jLevel = log4jEvent.getLevel();
			if (log4jLevel == Level.TRACE)
			{
				result.setLevel(LoggingEvent.Level.TRACE);
			}
			else if (log4jLevel == Level.DEBUG)
			{
				result.setLevel(LoggingEvent.Level.DEBUG);
			}
			else if (log4jLevel == Level.INFO)
			{
				result.setLevel(LoggingEvent.Level.INFO);
			}
			else if (log4jLevel == Level.WARN)
			{
				result.setLevel(LoggingEvent.Level.WARN);
			}
			else if (log4jLevel == Level.ERROR)
			{
				result.setLevel(LoggingEvent.Level.ERROR);
			}
			else if (log4jLevel == Level.FATAL)
			{
				mdc.put(LOG4J_LEVEL_KEY, LOG4J_LEVEL_VALUE_FATAL);
				result.setLevel(LoggingEvent.Level.ERROR);
			}
		}

		// timeStamp
		result.setTimeStamp(log4jEvent.getMillis());

		// Message
		{

			org.apache.logging.log4j.message.Message msg = log4jEvent.getMessage();
			if (msg != null)
			{
				result.setMessage(new Message(msg.getFormattedMessage()));
			}
		}

		// threadInfo
		{
			String threadName = log4jEvent.getThreadName();
			if (threadName != null)
			{
				ThreadInfo threadInfo = new ThreadInfo();
				threadInfo.setName(threadName);
				result.setThreadInfo(threadInfo);
			}
		}

		// MDC
		{
			Map<String, String> props = log4jEvent.getContextMap();
			if (props != null)
			{
				mdc.putAll(props);
			}
		}
		if (mdc.size() > 0)
		{
			result.setMdc(mdc);
		}

		// application / contextName
		if (mdc.containsKey(APPLICATION_MDC_KEY))
		{
			LoggerContext context = new LoggerContext();
			context.setName(mdc.get(APPLICATION_MDC_KEY));
			result.setLoggerContext(context);
		}

		// NDC
		{
			ThreadContext.ContextStack ndc = log4jEvent.getContextStack();
			if (ndc != null)
			{
				List<String> list = ndc.asList();
				if (list != null && !list.isEmpty())
				{
					Message[] ndcResult = new Message[list.size()];
					for (int i = 0; i < list.size(); i++)
					{
						String current = list.get(i);
						if (current != null)
						{
							ndcResult[i] = new Message(current);
						}
					}
					result.setNdc(ndcResult);
				}
			}
		}

		// location information
		{
			StackTraceElement location = log4jEvent.getSource();
			if (location != null)
			{
				ExtendedStackTraceElement ste = new ExtendedStackTraceElement();
				ste.setClassName(location.getClassName());
				ste.setMethodName(location.getMethodName());
				ste.setFileName(location.getFileName());
				int line = location.getLineNumber();
				if (line < 0)
				{
					ste.setLineNumber(line);
				}
				result.setCallStack(new ExtendedStackTraceElement[]{ste});
			}
		}

		// throwable information
		{
			result.setThrowable(convert(log4jEvent.getThrown()));
		}

		return result;
	}

	private ThrowableInfo convert(Throwable thrown)
	{
		if (thrown == null)
		{
			return null;
		}
		ThrowableInfo result = new ThrowableInfo();
		result.setCause(convert(thrown.getCause()));
		result.setMessage(thrown.getMessage());
		result.setName(thrown.toString()); // TODO: data is missing
		StackTraceElement[] st = thrown.getStackTrace();
		if (st != null && st.length > 0)
		{
			ExtendedStackTraceElement[] stResult = new ExtendedStackTraceElement[st.length];
			for (int i = 0; i < st.length; i++)
			{
				stResult[i] = convert(st[i]);
			}
			result.setStackTrace(stResult);
		}
		// TODO: missing data for result.setOmittedElements();
		// TODO: missing data for result.setSuppressed()

		return result;
	}

	private ExtendedStackTraceElement convert(StackTraceElement ste)
	{
		if (ste == null)
		{
			return null;
		}
		ExtendedStackTraceElement result = new ExtendedStackTraceElement();

		result.setClassName(ste.getClassName());
		result.setFileName(ste.getFileName());
		result.setLineNumber(ste.getLineNumber());
		result.setMethodName(ste.getMethodName());

		return result;
	}

	public Class getSourceClass()
	{
		return org.apache.logging.log4j.core.LogEvent.class;
	}
}
