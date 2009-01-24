/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.huxhorn.lilith.logback.servlet;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * See also: ch.qos.logback.classic.selector.servlet.ContextDetachingSCL
 */
public class LogbackShutdownServletContextListener
	implements ServletContextListener
{
	private ServletContext context;

	public void contextDestroyed(ServletContextEvent sce)
	{
		shutdownLogback();
		context = null;
	}

	public void contextInitialized(ServletContextEvent sce)
	{
		if(context != null)
		{
			System.err.println("There is a previous context.");
			shutdownLogback();
		}
		context = sce.getServletContext();
	}

	private void shutdownLogback()
	{
		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
		if(loggerFactory instanceof LoggerContext)
		{
			LoggerContext loggerContext = (LoggerContext) loggerFactory;
			loggerContext.stop();
			System.err.println("Logback has been shut down.");
		}
	}
}