/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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

import java.text.SimpleDateFormat;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.access.HttpStatus;

//import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
//import de.huxhorn.lilith.data.eventsource.EventWrapper;
//import de.huxhorn.lilith.data.logging.Marker;
//import de.huxhorn.lilith.data.logging.ThrowableInfo;

//def si=new SourceIdentifier('identifier', 'secondaryIdentifier')

//def eventWrapper=new EventWrapper(si, 1, event);

/*
def event=eventWrapper.event;
event=new AccessEvent();
event.requestURI='Request URI'
event.requestURL='Request URL'
event.statusCode=200
event.remoteUser='user'
event.remoteHost='host'
event.remoteAddress='10.0.0.1'
event.requestParameters=[foo1: ['bar'], foo2: ['bar1', 'bar2']]
event.requestHeaders=[requestFoo1: 'bar', requestFoo2: 'bar']
event.responseHeaders=[responseFoo1: 'bar', responseFoo2: 'bar']
event.localPort=8080
event.timeStamp=new Date()
event.applicationIdentifier='AppId'

eventWrapper.event=event;
*/

/*
if(!event)
{
    even=new LoggingEvent();
}
event.message = 'Huhu'
event.level = LoggingEvent.Level.INFO
event.logger = "Logger Name";
event.threadName = "thread name";
event.marker = new Marker('foo');
def m1=new Marker('Hail Eris!!')
def m2=new Marker('All Hail Discordia!!!');
m1.add(m2);
event.marker.add(m1);
event.marker.add(new Marker('bar'))
m2.add(event.marker)
event.timeStamp = new Date()
event.mdc = [foo: 'bar', 'Hail Eris!!':'All Hail Discordia!!!'];
event.callStack = [
new StackTraceElement('class', '<methodName>', 'fileName', 17),
new StackTraceElement('class', '<methodName>', 'fileName', 18),
new StackTraceElement('class', '<methodName>', 'fileName', 19),
new StackTraceElement('class', '<methodName>', 'fileName', 20),
new StackTraceElement('class', '<methodName>', 'fileName', 21)
]


ThrowableInfo ti=new ThrowableInfo();
ti.name='a.exception.Class';
ti.message='A exeception message'
ti.stackTrace =  [
new StackTraceElement('class', '<methodName>', 'fileName', 1),
new StackTraceElement('class', '<methodName>', 'fileName', 2),
new StackTraceElement('class', '<methodName>', 'fileName', 3)
]

ThrowableInfo cause=new ThrowableInfo();
cause.name='another.exception.Class';
cause.message='Another exeception message'
cause.stackTrace =  [
new StackTraceElement('class', '<methodName>', 'fileName', 1),
new StackTraceElement('class', '<methodName>', 'fileName', 2),
new StackTraceElement('class', '<methodName>', 'fileName', 3),
new StackTraceElement('class', '<methodName>', 'fileName', 4),
new StackTraceElement('class', '<methodName>', 'fileName', 5)
]

ti.cause = cause;

event.throwable = ti;
event.applicationIdentifier='AppId'

eventWrapper.event=event;
*/


if(!binding.variables.'completeCallStack')
{
	binding.setVariable('completeCallStack', false);
}
//def completeCallStack=binding[completeCallStack];
//println event;

//def writer = new StringWriter();
//def builder = new groovy.xml.MarkupBuilder(writer);

//def builder = new groovy.util.NodeBuilder();
/*def node= */

def dateFormat=new SimpleDateFormat('yyyy-MM-dd HH:mm:ss.SSSZ');
def builder = new groovy.xml.StreamingMarkupBuilder();
builder.encoding="UTF-8";

def writer = new StringWriter();
writer << builder.bind {
    mkp.xmlDeclaration()
    mkp.yieldUnescaped '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">\n'
    namespaces << ['':'http://www.w3.org/1999/xhtml']
    html {
        head {

            title('Title')
            link(href: 'detailsView.css', rel: 'stylesheet', type: 'text/css')
        }

        body
        {
            if (eventWrapper)
            {
                def event=eventWrapper.event;
				if(!event)
				{
					it.mkp.yield 'Connection closed.'
				}
				else if (event instanceof LoggingEvent)
                {
                    buildLoggingEvent(it, eventWrapper, dateFormat, completeCallStack);
                }
                else if (event instanceof AccessEvent)
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
    def event=eventWrapper.event;

    def evenOdd=new EvenOddToggle();

    element.table {
        if (event.requestURL)
        {
            evenOdd.toggle()
            it.tr([class: "${evenOdd}"])
            {
                th('Request URL')
                td
                {
                    mkp.yield event.requestURL
                }
            }
        }

        if (event.statusCode)
        {
            def code=event.statusCode;
            def status=HttpStatus.getStatus(code)

            evenOdd.toggle()
            it.tr([class: "${evenOdd}"])
            {
                th('Status Code')
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

        if (event.remoteHost)
        {
            def msg=event.remoteHost;
            if(event.remoteAddress && event.remoteHost != event.remoteAddress)
            {
                msg=msg+" (${event.remoteAddress})"
            }
            if(event.remoteUser && event.remoteUser!='-')
            {
                msg="${event.remoteUser}@${msg}"
            }

            evenOdd.toggle()
            it.tr([class: "${evenOdd}"])
            {
                th('Remote Info')
                td(msg)
            }
        }

        if (event.requestParameters)
        {
            evenOdd.toggle()
            it.tr([class: "${evenOdd}"])
            {
                th('Request Parameters')
                td
                {
                    buildStringArrayMap(it, event.requestParameters)
                }
            }
        }

        if (event.requestHeaders)
        {
            evenOdd.toggle()
            it.tr([class: "${evenOdd}"])
            {
                th('Request Headers')
                td
                {
                    buildStringMap(it, event.requestHeaders)
                }
            }
        }

        if (event.responseHeaders)
        {
            evenOdd.toggle()
            it.tr([class: "${evenOdd}"])
            {
                th('Response Headers')
                td
                {
                    buildStringMap(it, event.responseHeaders)
                }
            }
        }

        if (event.localPort)
        {
            evenOdd.toggle()
            it.tr([class: "${evenOdd}"])
            {
                th('Local Port')
                td(event.localPort)
            }
        }

        if (event.timeStamp)
        {
            evenOdd.toggle()
            it.tr([class: "${evenOdd}"])
            {
                th('Timestamp')
                td(dateFormat.format(event.timeStamp))
            }
        }

        if (event.applicationIdentifier)
        {
            evenOdd.toggle()
            it.tr([class: "${evenOdd}"])
            {
                th('Application Identifier')
                td(event.applicationIdentifier);
            }
        }

        buildEventWrapperSpecific(it, eventWrapper, evenOdd);
    }
}

def buildLoggingEvent(element, eventWrapper, dateFormat, completeCallStack)
{
    def event=eventWrapper.event;

    def evenOdd=new EvenOddToggle();

    element.table {
        if (event.message)
        {
            evenOdd.toggle()
            it.tr([class: "${evenOdd}"])
            {
                th('Message')
                td
				{
					pre
					{
						tt
						{
							def message=event.message;
							mkp.yield message
						}
					}
				}
            }
        }

        if (event.throwable)
        {
            evenOdd.toggle()
            it.tr([class: "${evenOdd}"])
            {
                th('Throwable')
                td(class: 'throwable')
                {
                    buildThrowable(it, event.throwable, true)
                }
            }
        }

        if (event.level)
        {
            evenOdd.toggle()
            it.tr([class: "${evenOdd}"])
            {
                th('Level')
                td(event.level);
            }
        }

        if (event.logger)
        {
            evenOdd.toggle()
            it.tr([class: "${evenOdd}"])
            {
                th('Logger')
                td(event.logger);
            }
        }

        if (event.threadName)
        {
            evenOdd.toggle()
            it.tr([class: "${evenOdd}"])
            {
                th('Thread Name')
                td(event.threadName);
            }
        }

        if (event.marker)
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

        if (event.timeStamp)
        {
            evenOdd.toggle()
            it.tr([class: "${evenOdd}"])
            {
                th('Timestamp')
                td(dateFormat.format(event.timeStamp))
            }
        }

        if (event.mdc)
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

        if(event.callStack)
        {
            if(completeCallStack)
            {
                evenOdd.toggle()
                it.tr([class: "${evenOdd}"])
                {
                    th('Call Stack')
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
                    th('Call Location')
                    td
                    {
                        buildStackTrace(it, event.callStack, true)
                    }
                }
            }
        }

        if (event.applicationIdentifier)
        {
            evenOdd.toggle()
            it.tr([class: "${evenOdd}"])
            {
                th('Application Identifier')
                td(event.applicationIdentifier);
            }
        }

        buildEventWrapperSpecific(it, eventWrapper, evenOdd);
    }

}

def buildEventWrapperSpecific(table, eventWrapper, evenOdd)
{
    def si=eventWrapper.sourceIdentifier
    if(si)
    {
        evenOdd.toggle()
        table.tr([class: "${evenOdd}"])
        {
            th('Source Identifier')
            td(si);
        }
    }

    evenOdd.toggle()
    table.tr([class: "${evenOdd}"])
    {
        th('Local ID')
        td(eventWrapper.localId);
    }
}

def buildThrowable(element, throwable, isFirst=false)
{
    if(!isFirst)
    {
        element.mkp.yield 'Caused by:'
        element.br()
    }
    element.mkp.yield "${throwable.name}"
    if(throwable.message && throwable.message!=throwable.name)
    {
        element.mkp.yield " - ${throwable.message}"
    }
    if(throwable.stackTrace)
    {
        element.p
        {
            buildStackTrace(it, throwable.stackTrace)
        }
    }
    if(throwable.cause)
    {
        buildThrowable(element, throwable.cause)
    }
}

def buildMarker(element, marker, handledMarkers=[])
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

def buildStringMap(element, Map<String,String> map)
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

def buildStringArrayMap(element, Map<String,String[]> map)
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

def buildStackTrace(element, callerData, onlyFirst=false)
{
    boolean isFirst=true;
    callerData.each {
        if(isFirst)
        {
            buildStackTraceElement(element, it, isFirst)
            isFirst=false;
        }
        else if(!onlyFirst)
        {
            buildStackTraceElement(element, it)
        }
    }
}

def buildStackTraceElement(element, ste, isFirst=false)
{
    if(!isFirst)
    {
        element.br()
    }
    def steStr=ste.toString(true);
    element.a([href:'ste://'+ste.toString()]) { it.mkp.yield steStr}
}

class EvenOddToggle
{
    boolean odd=true;

    def toggle()
    {
        odd=!odd;
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