/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

/*
 * Copyright 2007-2017 Joern Huxhorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.huxhorn.lilith.logback.servlet;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.StatusUtil;
import de.huxhorn.lilith.logback.tools.ContextHelper;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

/**
 * See also: ch.qos.logback.classic.selector.servlet.ContextDetachingSCL
 */
public class LogbackShutdownServletContextListener
	implements ServletContextListener
{
	private static final String LOGBACK_SHUTDOWN_DEBUG = "LogbackShutdownDebug";

	private static final String[] STATUS_TEXT=
			{
					"INFO : ",
					"WARN : ",
					"ERROR: ",
			};

	private boolean debug=false;

	@Override
	public void contextDestroyed(ServletContextEvent sce)
	{
		shutdownLogback();
	}

	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		ServletContext c = sce.getServletContext();
		if(c != null)
		{
			String debugString = c.getInitParameter(LOGBACK_SHUTDOWN_DEBUG);
			if(debugString != null)
			{
				debug=Boolean.parseBoolean(debugString);
			}
		}
	}

	private void shutdownLogback()
	{
		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
		if(loggerFactory instanceof LoggerContext)
		{
			LoggerContext context = (LoggerContext) loggerFactory;
			context.stop();
			System.err.println("Logback has been shut down."); // NOPMD
			String message = retrieveLogbackStatus(context);
			if(message != null)
			{
				System.err.println(message); // NOPMD
			}
		}
	}

	private String retrieveLogbackStatus(LoggerContext context)
	{
		StatusManager statusManager = context.getStatusManager();
		if(statusManager == null)
		{
			return null;
		}
		int statusLevel = ContextHelper.getHighestLevel(context);
		long threshold = ContextHelper.getTimeOfLastReset(context);
		if(debug || statusLevel > Status.INFO)
		{
			List<Status> statusList = StatusUtil.filterStatusListByTimeThreshold(statusManager.getCopyOfStatusList(), threshold);
			if(statusList != null)
			{
				StringBuilder statusBuilder=new StringBuilder();
				for(Status current : statusList)
				{
					appendStatus(statusBuilder, current, 0);
				}
				return statusBuilder.toString();
			}
		}
		return null;
	}

	@SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
	private static void appendStatus(StringBuilder builder, Status status, int indent)
	{
		int levelCode = status.getLevel();
		appendIndent(builder, indent);
		if(levelCode >= 0 && levelCode < STATUS_TEXT.length)
		{
			builder.append(STATUS_TEXT[levelCode]);
		}
		builder.append(status.getMessage()).append('\n');
		Throwable t = status.getThrowable();
		if(t != null)
		{
			appendIndent(builder, indent+1);
			builder.append(t.getMessage()).append('\n');
		}
		if(status.hasChildren())
		{
			Iterator<Status> children = status.iterator();
			while(children.hasNext())
			{
				appendStatus(builder, children.next(), indent+1);
			}
		}
	}

	private static void appendIndent(StringBuilder builder, int indent)
	{
		for(int i=0;i<indent;i++)
		{
			builder.append("       ");
		}
	}
}
