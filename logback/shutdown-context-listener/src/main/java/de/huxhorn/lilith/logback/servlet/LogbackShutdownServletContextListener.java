package de.huxhorn.lilith.logback.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;

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
	private ServletContext context;

	public void contextDestroyed(ServletContextEvent sce)
    {
		shutdownLogback();
		context=null;
	}
	
	public void contextInitialized(ServletContextEvent sce)
	{
		if(context!=null)
		{
			System.err.println("There is a previous context.");
			shutdownLogback();
		}
		context=sce.getServletContext();
	}

	private void shutdownLogback()
	{
		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if (loggerFactory instanceof LoggerContext)
		{
            LoggerContext loggerContext = (LoggerContext) loggerFactory;
            loggerContext.shutdownAndReset();
			System.err.println("Logback has been shut down.");
        }
	}
}