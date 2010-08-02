package de.huxhorn.lilith.log4j.producer;

import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.log4j.ThrowableInfoParser;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.ThrowableInformation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Log4jLoggingAdapter
{
	public static final String LOG4J_LEVEL_KEY="log4j.level";
	public static final String LOG4J_LEVEL_VALUE_FATAL = "FATAL";
	private static final String APPLICATION_MDC_KEY = "application";

	public LoggingEvent convert(org.apache.log4j.spi.LoggingEvent log4jEvent)
	{
		if(log4jEvent == null)
		{
			return null;
		}
		LoggingEvent result=new LoggingEvent();
		Map<String, String> mdc=new HashMap<String, String>();

		// loggerName
		result.setLogger(log4jEvent.getLoggerName());

		// level
		{
			org.apache.log4j.Level log4jLevel = log4jEvent.getLevel();
			if(log4jLevel.equals(org.apache.log4j.Level.TRACE))
			{
				result.setLevel(LoggingEvent.Level.TRACE);
			}
			else if(log4jLevel.equals(org.apache.log4j.Level.DEBUG))
			{
				result.setLevel(LoggingEvent.Level.DEBUG);
			}
			else if(log4jLevel.equals(org.apache.log4j.Level.INFO))
			{
				result.setLevel(LoggingEvent.Level.INFO);
			}
			else if(log4jLevel.equals(org.apache.log4j.Level.WARN))
			{
				result.setLevel(LoggingEvent.Level.WARN);
			}
			else if(log4jLevel.equals(org.apache.log4j.Level.ERROR))
			{
				result.setLevel(LoggingEvent.Level.ERROR);
			}
			else if(log4jLevel.equals(org.apache.log4j.Level.FATAL))
			{
				mdc.put(LOG4J_LEVEL_KEY, LOG4J_LEVEL_VALUE_FATAL);
				result.setLevel(LoggingEvent.Level.ERROR);
			}
		}

		// timeStamp
		result.setTimeStamp(log4jEvent.getTimeStamp());

		// Message
		{
			String msg = log4jEvent.getRenderedMessage();
			if(msg != null)
			{
				result.setMessage(new Message(msg));
			}
		}

		// threadInfo
		{
			String threadName=log4jEvent.getThreadName();
			if(threadName != null)
			{
				ThreadInfo threadInfo=new ThreadInfo();
				threadInfo.setName(threadName);
				result.setThreadInfo(threadInfo);
			}
		}

		// MDC
		{
			Map props = log4jEvent.getProperties();
			if(props != null)
			{
				for(Object currentObj : props.entrySet())
				{
					Map.Entry current= (Map.Entry) currentObj;
					String keyStr=null;
					String valueStr=null;
					Object key=current.getKey();
					Object value=current.getValue();
					if(key != null)
					{
						keyStr=key.toString(); // use safe toString
					}
					if(value != null)
					{
						valueStr=value.toString(); // use safe toString
					}
					if(keyStr != null && valueStr != null)
					{
						mdc.put(keyStr, valueStr);
					}
				}
			}
		}
		if(mdc.size()>0)
		{
			result.setMdc(mdc);
		}

		// application / contextName
		if(mdc.containsKey(APPLICATION_MDC_KEY))
		{
			LoggerContext context=new LoggerContext();
			context.setName(mdc.get(APPLICATION_MDC_KEY));
			result.setLoggerContext(context);
		}

		// NDC
		{
			String ndc=log4jEvent.getNDC();
			if("".equals(ndc))
			{
				ndc = null;
			}
			if(ndc != null)
			{
				// TODO: tokenize?
				result.setNdc(new Message[]{new Message(ndc)});
			}
		}

		// location information
		{
			LocationInfo location = log4jEvent.getLocationInformation();
			if(location != null)
			{
				ExtendedStackTraceElement ste = new ExtendedStackTraceElement();
				ste.setClassName(location.getClassName());
				ste.setMethodName(location.getMethodName());
				ste.setFileName(location.getFileName());
				String line = location.getLineNumber();
				if(line != null)
				{
					try
					{
						ste.setLineNumber(Integer.parseInt(line));
					}
					catch(NumberFormatException ex)
					{
						// ignore
					}
				}
				result.setCallStack(new ExtendedStackTraceElement[]{ste});
			}
		}

		// throwable information
		{
			// TODO: log4jEvent.getThrowableInformation();
			ThrowableInformation ti = log4jEvent.getThrowableInformation();
			if(ti != null)
			{
				String[] throwableStrRep = ti.getThrowableStrRep();
				if(throwableStrRep != null && throwableStrRep.length>0)
				{
					result.setThrowable(ThrowableInfoParser.parseThrowableInfo(Arrays.asList(throwableStrRep)));
				}
			}
		}

		return result;
	}

}
