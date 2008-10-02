package de.huxhorn.lilith.logback.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.LoggerFactory;
import org.slf4j.ILoggerFactory;
import ch.qos.logback.classic.LoggerContext;

/**
 *
 * See also: ch.qos.logback.classic.selector.servlet.ContextDetachingSCL
 */
public class LogbackShutdownServletContextListener
	implements ServletContextListener
{
	public void contextDestroyed(ServletContextEvent sce)
    {
		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if (loggerFactory instanceof LoggerContext) 
		{
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.shutdownAndReset();
        }
    }
	
	public void contextInitialized(ServletContextEvent sce)
	{
	}
}