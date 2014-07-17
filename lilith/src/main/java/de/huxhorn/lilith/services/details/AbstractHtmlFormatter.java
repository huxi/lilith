/*
 * Lilith - a log event viewer.
 * Copyright (C) 2014 Joern Huxhorn
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

import de.huxhorn.lilith.services.BasicFormatter;
import de.huxhorn.sulky.formatting.SimpleXml;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class AbstractHtmlFormatter
		implements BasicFormatter
{
	public static final String LOGGER_VARIABLE = "logger";

	public static final String EVENT_WRAPPER_VARIABLE = "eventWrapper";
	public static final String LOGGING_EVENT_VARIABLE = "loggingEvent";
	public static final String ACCESS_EVENT_VARIABLE = "accessEvent";

	public static final String COMPLETE_CALL_STACK_OPTION_VARIABLE = "completeCallStack";
	public static final String SHOW_STACK_TRACE_OPTION_VARIABLE = "showStackTrace";
	public static final String WRAPPED_EXCEPTION_STYLE_OPTION_VARIABLE = "wrappedExceptionStyle";


	public static String createErrorHtml(String message, String additionalInfo, Throwable throwable)
	{
		StringBuilder msg = new StringBuilder();
		msg.append("<html><body>");
		msg.append(message);

		if(additionalInfo != null)
		{
			msg.append("<br/>");
			msg.append(additionalInfo);

		}

		if(throwable != null)
		{
			msg.append("<br/>");
			msg.append("<pre>");

			StringWriter sw = new StringWriter();
			throwable.printStackTrace(new PrintWriter(sw));
			String exceptionStr = sw.toString();


			exceptionStr = SimpleXml.escape(exceptionStr);

			msg.append(exceptionStr);
			msg.append("</pre>");
		}
		msg.append("</body></html>");

		return msg.toString();
	}
}
