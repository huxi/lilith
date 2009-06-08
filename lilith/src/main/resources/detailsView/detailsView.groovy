import de.huxhorn.lilith.data.access.AccessEvent
import de.huxhorn.lilith.data.access.HttpStatus
import de.huxhorn.lilith.data.logging.LoggingEvent
import de.huxhorn.lilith.data.logging.Message
import de.huxhorn.lilith.data.logging.ThreadInfo
import groovy.xml.StreamingMarkupBuilder
import java.text.SimpleDateFormat

/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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

if(!binding.variables.containsKey('completeCallStack'))
{
	binding.setVariable('completeCallStack', false);
}
if(!binding.variables.containsKey('showStackTrace'))
{
	binding.setVariable('showStackTrace', true);
}

def dateFormat = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss.SSSZ');
def builder = new StreamingMarkupBuilder();
builder.encoding = "UTF-8";

def writer = new StringWriter();
writer << builder.bind {
	mkp.xmlDeclaration()
	mkp.yieldUnescaped '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">\n'
	namespaces << ['': 'http://www.w3.org/1999/xhtml']
	html {
		head {

			title('Title')
			link(href: 'detailsView.css', rel: 'stylesheet', type: 'text/css')
		}

		body
		{
			if(eventWrapper)
			{
				def event = eventWrapper.event;
				if(!event)
				{
					it.mkp.yield 'Connection closed.'
				}
				else if(event instanceof LoggingEvent)
				{
					buildLoggingEvent(it, eventWrapper, dateFormat, completeCallStack);
				}
				else if(event instanceof AccessEvent)
					{
						buildAccessEvent(it, eventWrapper, dateFormat);
					}
					else
					{
						it.mkp.yield 'Unsupported class.'
						it.br()
						it.mkp.yield event.toString()
					}
			}
			else
			{
				it.mkp.yield 'Could not load event!'
				it.br()
				it.mkp.yield 'If you just updated Lilith then the old event type is probably incompatible with the current one.'
				it.br()
				it.b()
					{
						it.mkp.yield 'Just clear the event view in that case.'
					}
			}
		}
	}
}

//print writer;

writer.toString()

// functions below

def buildAccessEvent(element, eventWrapper, dateFormat)
{
	def event = eventWrapper.event;

	def evenOdd = new EvenOddToggle();

	element.table {
		if(event.requestURL)
		{
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th
					{
						it.mkp.yieldUnescaped 'Request&nbsp;URL'
					}
					td
					{
						mkp.yield event.requestURL
					}
				}
		}

		if(event.statusCode)
		{
			def code = event.statusCode;
			def status = HttpStatus.getStatus(code)

			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th
					{
						it.mkp.yieldUnescaped 'Status&nbsp;Code'
					}
					td
					{
						mkp.yield code
						if(status)
						{
							mkp.yield " - ${status.description} (${status.type})"
						}
					}
				}
		}

		if(event.remoteHost)
		{
			def msg = event.remoteHost;
			if(event.remoteAddress && event.remoteHost != event.remoteAddress)
			{
				msg = msg + " (${event.remoteAddress})"
			}
			if(event.remoteUser && event.remoteUser != '-')
			{
				msg = "${event.remoteUser}@${msg}"
			}

			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th
					{
						it.mkp.yieldUnescaped 'Remote&nbsp;Info'
					}
					td(msg)
				}
		}

		if(event.requestParameters)
		{
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th
					{
						it.mkp.yieldUnescaped 'Request<br/>Parameters'
					}
					td
					{
						buildStringArrayMap(it, event.requestParameters)
					}
				}
		}

		if(event.requestHeaders)
		{
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th
					{
						it.mkp.yieldUnescaped 'Request<br/>Headers'
					}

					td
					{
						buildStringMap(it, event.requestHeaders)
					}
				}
		}

		if(event.responseHeaders)
		{
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th
					{
						it.mkp.yieldUnescaped 'Response<br/>Headers'
					}
					td
					{
						buildStringMap(it, event.responseHeaders)
					}
				}
		}

		if(event.localPort)
		{
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th
					{
						it.mkp.yieldUnescaped 'Local&nbsp;Port'
					}
					td(event.localPort)
				}
		}

		if(event.timeStamp)
		{
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th('Timestamp')
					td(dateFormat.format(event.timeStamp))
				}
		}

		if(event.loggerContext)
		{
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th
					{
						it.mkp.yieldUnescaped 'Logger&nbsp;Context'
					}
					td(event.loggerContext);
				}
		}

		buildEventWrapperSpecific(it, eventWrapper, evenOdd);
	}
}

def buildLoggingEvent(element, eventWrapper, dateFormat, completeCallStack)
{
	def event = eventWrapper.event;

	def evenOdd = new EvenOddToggle();

	element.table {
		if(event.message)
		{
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th('Message')
					td
					{
						pre
						{
							def message = event.message.message;
							mkp.yield message
						}
					}
				}
		}

		if(event.throwable)
		{
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th('Throwable')
					td([class: "throwableContainer"])
						{
							buildThrowable(it, event.throwable, null, true, showStackTrace)
						}
				}
		}

		if(event.level)
		{
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th('Level')
					td(event.level);
				}
		}

		if(event.logger)
		{
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th('Logger')
					td(event.logger);
				}
		}

		if(event.threadInfo)
		{
			ThreadInfo threadInfo = event.threadInfo;
			String threadName = threadInfo.name;
			Long threadId = threadInfo.id;
			if(threadName || threadId)
			{
				evenOdd.toggle()

				it.tr([class: "${evenOdd}"])
					{
						th
						{
							it.mkp.yieldUnescaped 'Thread'
						}
						td
						{
							if(!threadName)
							{
								it.mkp.yield threadId
							}
							else if(!threadId)
							{
								it.mkp.yield threadName
							}
							else
							{
								it.mkp.yield threadName + ' (id=' + threadId + ")"
							}
						}
					}
			}
		}

		if(event.marker)
		{
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th('Marker')
					td
					{
						buildMarker(it, event.marker)
					}
				}
		}

		if(event.timeStamp)
		{
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th('Timestamp')
					td(dateFormat.format(event.timeStamp))
				}
		}

		if(event.mdc)
		{
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th([title: 'Mapped Diagnostic Context'], 'MDC')
					td
					{
						buildStringMap(it, event.mdc)
					}
				}
		}

		if(event.ndc)
		{
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th([title: 'Nested Diagnostic Context'], 'NDC')
					td
					{
						buildNdc(it, event.ndc)
					}
				}
		}

		if(event.callStack)
		{
			if(completeCallStack)
			{
				evenOdd.toggle()
				it.tr([class: "${evenOdd}"])
					{
						th
						{
							it.mkp.yieldUnescaped 'Call&nbsp;Stack'
						}
						td
						{
							buildStackTrace(it, event.callStack)
						}
					}
			}
			else
			{
				evenOdd.toggle()
				it.tr([class: "${evenOdd}"])
					{
						th
						{
							it.mkp.yieldUnescaped 'Call&nbsp;Location'
						}
						td
						{
							buildStackTrace(it, event.callStack, true)
						}
					}
			}
		}

		if(event.loggerContext)
		{
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"])
				{
					th
					{
						it.mkp.yieldUnescaped 'Logger&nbsp;Context'
					}
					td(event.loggerContext);
				}
		}

		buildEventWrapperSpecific(it, eventWrapper, evenOdd);
	}

}

def buildEventWrapperSpecific(table, eventWrapper, evenOdd)
{
	def si = eventWrapper.sourceIdentifier
	if(si)
	{
		evenOdd.toggle()
		table.tr([class: "${evenOdd}"])
			{
				th
				{
					it.mkp.yieldUnescaped 'Source&nbsp;ID'
				}
				td(si);
			}
	}

	evenOdd.toggle()
	table.tr([class: "${evenOdd}"])
		{
			th
			{
				it.mkp.yieldUnescaped 'Local&nbsp;ID'
			}
			td(eventWrapper.localId);
		}
}

def buildThrowable(element, throwable, previousSTE, isFirst, showStackTrace)
{
	if(!isFirst)
	{
		element.br()
		element.mkp.yield 'Caused by:'
		element.br()
	}
	def stackTraceElement = null;
	if(throwable.stackTrace && throwable.stackTrace.length > 0)
	{
		stackTraceElement = throwable.stackTrace[0];
	}
	if(!stackTraceElement)
	{
		stackTraceElement = previousSTE;
	}
	element.div([class: "throwable"])
		{
			if(stackTraceElement)
			{
				def steStr = stackTraceElement.toString();
				it.a([href: 'ste://' + steStr])
					{
						buildThrowableText(it, throwable);
					}
			}
			else
			{
				buildThrowableText(it, throwable);
			}
			if(showStackTrace)
			{
				it.hr();
				buildStackTrace(it, throwable.stackTrace)
				if(throwable.omittedElements > 0)
				{
					it.mkp.yield("${throwable.omittedElements} common elements omitted.");
					it.br();
				}
			}

			if(throwable.cause)
			{
				buildThrowable(it, throwable.cause, stackTraceElement, false, showStackTrace)
			}
		}
}

def buildThrowableText(element, throwable)
{
	element.mkp.yield "${throwable.name}"
	if(throwable.message && throwable.message != throwable.name)
	{
		element.pre()
			{
				it.mkp.yield throwable.message;
			}
	}
}

def buildMarker(element, marker, handledMarkers = [])
{
	element.mkp.yield "${marker.name}"
	if(handledMarkers.contains(marker.name))
	{
		return;
	}
	handledMarkers.add(marker.name);
	if(marker.references)
	{
		element.ul
		{
			parent ->
			marker.references.each
			{
				key, value ->
				parent.li
				{
					buildMarker(it, value, handledMarkers)
				}
			}
		}
	}
}

def buildNdc(element, Message[] ndc)
{
	element.ul
	{
		ulIt ->
		ndc.each
		{
			message ->
			ulIt.li(message.message)
		}
	}
}

def buildStringMap(element, Map<String, String> map)
{
	element.table
	{
		tableIt ->
		tr
		{
			th('Key')
			th('Value')
		}
		map.each
		{
			key, value ->
			tableIt.tr
			{
				td(key)
				td(value)
			}
		}
	}
}

def buildStringArrayMap(element, Map<String, String[]> map)
{
	element.table
	{
		tableIt ->
		tr
		{
			th('Key')
			th('Value(s)')
		}
		map.each
		{
			key, value ->

			tableIt.tr
			{
				td(key)
				td
				{
					if(value.size() == 1)
					{
						it.mkp.yield value[0]
					}
					else
					{
						it.ul
						{
							ulIt ->
							value.each
							{
								ulIt.li(it)
							}
						}
					}
				}
			}
		}
	}
}

def buildStackTrace(element, callerData, onlyFirst = false)
{
	if(!callerData)
	{
		return false;
	}
	boolean isFirst = true;
	callerData.each {
		if(isFirst)
		{
			buildStackTraceElement(element, it, isFirst)
			isFirst = false;
		}
		else if(!onlyFirst)
		{
			buildStackTraceElement(element, it)
		}
	}
	return !isFirst;
}

def buildStackTraceElement(element, ste, isFirst = false)
{
	def steStr = ste.toString();
	def extendedStr = ste.getExtendedString();
	element.a([href: 'ste://' + steStr])
		{
			it.mkp.yieldUnescaped("at&nbsp;");
			it.mkp.yield steStr
			if(extendedStr)
			{
				it.mkp.yieldUnescaped("&nbsp;")
				it.mkp.yield(extendedStr)
			}
		}
}

class EvenOddToggle
{
	boolean odd = true;

	def toggle()
	{
		odd = !odd;
	}

	def String toString()
	{
		if(odd)
		{
			return 'odd'
		}
		return 'even';
	}
}
