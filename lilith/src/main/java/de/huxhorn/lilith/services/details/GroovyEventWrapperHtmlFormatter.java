/*
 * Lilith - a log event viewer.
 * Copyright (C) 2014-2017 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.huxhorn.lilith.services.details;

import de.huxhorn.lilith.DateTimeFormatters;
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.sulky.formatting.SimpleXml;
import de.huxhorn.sulky.groovy.GroovyInstance;
import groovy.lang.Binding;
import groovy.lang.Script;
import java.io.File;
import java.io.Serializable;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroovyEventWrapperHtmlFormatter
	extends AbstractHtmlFormatter
{
	private final Logger logger = LoggerFactory.getLogger(GroovyEventWrapperHtmlFormatter.class);

	private final ApplicationPreferences applicationPreferences;

	private final GroovyInstance groovyInstance;

	public GroovyEventWrapperHtmlFormatter(ApplicationPreferences applicationPreferences)
	{
		this.applicationPreferences = applicationPreferences;
		groovyInstance = new GroovyInstance();

		File detailsViewRoot = applicationPreferences.getDetailsViewRoot();
		File scriptFile = new File(detailsViewRoot, ApplicationPreferences.DETAILS_VIEW_GROOVY_FILENAME);

		groovyInstance.setGroovyFileName(scriptFile.getAbsolutePath());
	}

	@Override
	public boolean isCompatible(Object object)
	{
		return object instanceof EventWrapper;
	}

	@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
	@Override
	public String toString(Object object)
	{
		if(!(object instanceof EventWrapper))
		{
			return null;
		}
		EventWrapper wrapper = (EventWrapper) object;

		String message;

		Script instance = groovyInstance.getInstanceAs(Script.class);
		if(instance == null)
		{
			message = createErrorHtml("detailsView Script is broken!", groovyInstance.getErrorMessage(), groovyInstance.getErrorCause());
		}
		else
		{
			try
			{
				Serializable event = wrapper.getEvent();
				LoggingEvent loggingEvent = null;
				AccessEvent accessEvent = null;
				if(event instanceof LoggingEvent)
				{
					loggingEvent = (LoggingEvent) event;
				}
				else if(event instanceof AccessEvent)
				{
					accessEvent = (AccessEvent) event;
				}

				URL messageViewRootUrl = applicationPreferences.getDetailsViewRootUrl();

				Binding binding = new Binding();

				binding.setVariable(LOGGER_VARIABLE, logger);

				binding.setVariable(EVENT_WRAPPER_VARIABLE, wrapper);
				binding.setVariable(LOGGING_EVENT_VARIABLE, loggingEvent);
				binding.setVariable(ACCESS_EVENT_VARIABLE, accessEvent);

				binding.setVariable(COMPLETE_CALL_STACK_OPTION_VARIABLE, applicationPreferences.isShowingFullCallStack());
				binding.setVariable(SHOW_STACK_TRACE_OPTION_VARIABLE, applicationPreferences.isShowingStackTrace());
				binding.setVariable(WRAPPED_EXCEPTION_STYLE_OPTION_VARIABLE, applicationPreferences.isUsingWrappedExceptionStyle());

				binding.setVariable(DOCUMENT_ROOT_VARIABLE, messageViewRootUrl.toExternalForm());

				binding.setVariable(DATETIME_FORMATTER_VARIABLE, DateTimeFormatters.DATETIME_IN_SYSTEM_ZONE_SPACE);

				instance.setBinding(binding);
				Object result = instance.run();
				if(result instanceof String)
				{
					message = (String) result;
				}
				else if(result != null)
				{
					message = result.toString();
				}
				else
				{
					message = createErrorHtml("detailsView Script returned null!", null, null);
				}
			}
			catch(Throwable t)
			{
				String msg = "Exception while executing detailsView Script!";
				if(logger.isWarnEnabled()) logger.warn(msg, t);
				message = createErrorHtml(msg, null, t);
			}
		}

		if(logger.isDebugEnabled()) logger.debug("Message:\n{}", message);
		// I'm not sure who is to blame for the following line of code...
		// One could argue that it's a bug in the application logging the event but I think
		// that it shouldn't be possible to cause a problem logging something weird.
		// One could also argue that logback should prevent/fix logging events that contain a zero
		// byte but this would result in a serious performance impact even though the problem is rather rare.
		// On the other hand, zero bytes can cause all kind of weird followup problems, e.g. cut off log files or,
		// as in this case, a malformed XML.
		// The third and last one to blame is the groovy XML builder. It shouldn't be possible to write a malformed
		// XML document using the builder but what would be an acceptable behaviour? Throwing an exception
		// would be a bad idea, imho. It would probably be best to replace the zero byte with a space.
		// This is very acceptable in my special case but I'm not sure if this is a general use case...
		//
		// http://cse-mjmcl.cse.bris.ac.uk/blog/2007/02/14/1171465494443.html
		message = SimpleXml.replaceNonValidXMLCharacters(message, ' ');
		return message;
	}
}
