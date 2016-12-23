/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2016 Joern Huxhorn
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


import de.huxhorn.lilith.data.access.AccessEvent
import de.huxhorn.lilith.data.access.HttpStatus
import de.huxhorn.lilith.data.eventsource.LoggerContext
import de.huxhorn.lilith.data.logging.LoggingEvent
import de.huxhorn.lilith.data.logging.Message
import de.huxhorn.lilith.data.logging.ThreadInfo
import groovy.xml.StreamingMarkupBuilder

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

if (!binding.variables.containsKey('completeCallStack')) {
	binding.setVariable('completeCallStack', false)
}
if (!binding.variables.containsKey('showStackTrace')) {
	binding.setVariable('showStackTrace', true)
}
if (!binding.variables.containsKey('wrappedExceptionStyle')) {
	binding.setVariable('wrappedExceptionStyle', false)
}
if (!binding.variables.containsKey('documentRoot')) {
	binding.setVariable('documentRoot', '')
}
if (!binding.variables.containsKey('dateTimeFormatter')) {
	binding.setVariable('dateTimeFormatter',
			DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss.SSS")
					.withZone(ZoneId.systemDefault()))
}

def builder = new StreamingMarkupBuilder()
builder.encoding = 'UTF-8'

def writer = new StringWriter()
writer << builder.bind {
	mkp.xmlDeclaration()
	mkp.yieldUnescaped '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">\n'
	namespaces << ['': 'http://www.w3.org/1999/xhtml']
	html {
		head {
			if (documentRoot) {
				it.base(href: documentRoot)
			}
			title('Title')
			link(href: 'detailsView.css', rel: 'stylesheet', type: 'text/css')
		}

		body {
			if (eventWrapper) {
				def event = eventWrapper.event
				if (!event) {
					it.mkp.yield 'Connection closed.'
				} else if (event instanceof LoggingEvent) {
					buildLoggingEvent(it, eventWrapper, dateTimeFormatter, completeCallStack)
				} else if (event instanceof AccessEvent) {
					buildAccessEvent(it, eventWrapper, dateTimeFormatter)
				} else {
					it.mkp.yield 'Unsupported class.'
					it.br()
					it.mkp.yield event.toString()
				}
			} else {
				it.mkp.yield 'Could not load event!'
				it.br()
				it.mkp.yield 'If you just updated Lilith then the old event type is probably incompatible with the current one.'
				it.br()
				it.b() {
					it.mkp.yield 'Just clear the event view in that case.'
				}
			}
		}
	}
}

writer.toString()

// functions below

def buildAccessEvent(element, eventWrapper, dateTimeFormatter) {
	def event = eventWrapper.event

	def evenOdd = new EvenOddToggle()

	element.table {
		if (event.requestURL) {
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th {
					it.mkp.yieldUnescaped 'Request&nbsp;URL'
				}
				td {
					mkp.yield event.requestURL
				}
			}
		}

		def code = event.statusCode
		if (code) {
			def status = HttpStatus.getStatus((int) code)

			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th {
					it.mkp.yieldUnescaped 'Status&nbsp;Code'
				}
				td {
					mkp.yield code
					if (status) {
						mkp.yield " - ${status.description} (${status.type})"
					}
				}
			}
		}

		if (event.remoteHost) {
			def msg = event.remoteHost
			if (event.remoteAddress && event.remoteHost != event.remoteAddress) {
				msg = msg + " (${event.remoteAddress})"
			}
			if (event.remoteUser && event.remoteUser != '-') {
				msg = "${event.remoteUser}@${msg}"
			}

			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th {
					it.mkp.yieldUnescaped 'Remote&nbsp;Info'
				}
				td(msg)
			}
		}

		if (event.requestParameters) {
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th {
					it.mkp.yieldUnescaped 'Request<br/>Parameters'
				}
				td {
					buildStringArrayMap(it, event.requestParameters)
				}
			}
		}

		if (event.requestHeaders) {
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th {
					it.mkp.yieldUnescaped 'Request<br/>Headers'
				}

				td {
					buildStringMap(it, event.requestHeaders)
				}
			}
		}

		if (event.responseHeaders) {
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th {
					it.mkp.yieldUnescaped 'Response<br/>Headers'
				}
				td {
					buildStringMap(it, event.responseHeaders)
				}
			}
		}

		if (event.localPort) {
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th {
					it.mkp.yieldUnescaped 'Local&nbsp;Port'
				}
				td(event.localPort)
			}
		}

		if (event.timeStamp) {
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th('Timestamp')
				td(dateTimeFormatter.format(Instant.ofEpochMilli(event.timeStamp)))
			}
		}

		if (event.elapsedTime) {
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th('Elapsed Time')
				td {
					it.mkp.yieldUnescaped "${event.elapsedTime}ms"
				}
			}
		}

		if (event.loggerContext instanceof LoggerContext) {
			LoggerContext loggerContext = event.loggerContext
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th {
					it.mkp.yieldUnescaped 'Logger&nbsp;Context'
				}
				td {
					buildLoggerContext(it, loggerContext, dateTimeFormatter)
				}
			}
		}

		buildEventWrapperSpecific(it, eventWrapper, evenOdd)
	}
}


def buildLoggerContext(element, LoggerContext loggerContext, dateTimeFormatter) {
	if (loggerContext) {
		if (loggerContext.name || loggerContext.birthTime || loggerContext.properties) {
			element.table {
				if (loggerContext.name) {
					tr {
						th('Name')
						td(loggerContext.name)
					}
				}
				if (loggerContext.birthTime) {
					tr {
						th('Birthtime')
						td(dateTimeFormatter.format(Instant.ofEpochMilli(loggerContext.birthTime)))
					}
				}
				if (loggerContext.properties) {
					tr {
						th('Properties')
						td() {
							buildStringMap(it, loggerContext.properties)
						}
					}
				}
			}
		}
	}
}

def buildLoggingEvent(element, eventWrapper, dateTimeFormatter, completeCallStack) {
	def event = eventWrapper.event

	def evenOdd = new EvenOddToggle()

	element.table {
		if (event.message) {
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th('Message')
				td {
					pre(event.message.message)
				}
			}
		}

		if (event.throwable) {
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th('Throwable')
				td([class: 'throwableContainer']) {
					buildThrowable(it, event.throwable, null, null, showStackTrace, wrappedExceptionStyle, null)
				}
			}
		}

		if (event.level) {
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th('Level')
				td(event.level)
			}
		}

		if (event.logger) {
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th('Logger')
				td(event.logger)
			}
		}

		if (event.threadInfo) {
			ThreadInfo threadInfo = event.threadInfo
			String threadName = threadInfo.name
			Long threadId = threadInfo.id
			Integer priority = threadInfo.priority
			if (threadName || threadId) {
				evenOdd.toggle()

				it.tr([class: "${evenOdd}"]) {
					th {
						it.mkp.yieldUnescaped 'Thread'
					}
					td {
						StringBuilder str = new StringBuilder()
						if (!threadName) {
							str.append(threadId)
						} else if (!threadId) {
							str.append(threadName)
						} else {
							str.append(threadName).append(' (id=').append(threadId).append(')')
						}
						if (priority > 0) {
							str.append(', priority=').append(priority)
							String description = null
							if (priority == Thread.NORM_PRIORITY) {
								description = 'default'
							} else if (priority == Thread.MIN_PRIORITY) {
								description = 'minimum'
							} else if (priority >= Thread.MAX_PRIORITY) {
								description = 'maximum'
							}

							if (description != null) {
								str.append(' (').append(description).append(')')
							}
						}

						String groupName = threadInfo.getGroupName()
						Long groupId = threadInfo.getGroupId()
						if (groupName != null || groupId != null) {
							str.append(', ')
							if (groupName == null) {
								str.append('groupId=').append(groupId)
							} else if (groupId == null) {
								str.append('group=').append(groupName)
							} else {
								str.append('group=').append(groupName).append(' (id=').append(groupId).append(')')
							}
						}

						it.mkp.yield str.toString()
					}
				}
			}
		}

		if (event.marker) {
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th('Marker')
				td {
					buildMarker(it, event.marker)
				}
			}
		}

		if (event.timeStamp) {
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th('Timestamp')
				td(dateTimeFormatter.format(Instant.ofEpochMilli(event.timeStamp)))
			}
		}

		if (event.mdc) {
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th([title: 'Mapped Diagnostic Context'], 'MDC')
				td {
					buildStringMap(it, event.mdc)
				}
			}
		}

		if (event.ndc) {
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th([title: 'Nested Diagnostic Context'], 'NDC')
				td {
					buildNdc(it, event.ndc)
				}
			}
		}

		if (event.callStack) {
			if (completeCallStack) {
				evenOdd.toggle()
				it.tr([class: "${evenOdd}"]) {
					th {
						it.mkp.yieldUnescaped 'Call&nbsp;Stack'
					}
					td {
						buildStackTrace(it, event.callStack, null)
					}
				}
			} else {
				evenOdd.toggle()
				it.tr([class: "${evenOdd}"]) {
					th {
						it.mkp.yieldUnescaped 'Call&nbsp;Location'
					}
					td {
						buildStackTrace(it, event.callStack, null, true)
					}
				}
			}
		}

		if (event.loggerContext instanceof LoggerContext) {
			LoggerContext loggerContext = event.loggerContext
			evenOdd.toggle()
			it.tr([class: "${evenOdd}"]) {
				th {
					it.mkp.yieldUnescaped 'Logger&nbsp;Context'
				}
				td {
					buildLoggerContext(it, loggerContext, dateTimeFormatter)
				}
			}
		}

		buildEventWrapperSpecific(it, eventWrapper, evenOdd)
	}
}

def buildEventWrapperSpecific(table, eventWrapper, evenOdd) {
	def si = eventWrapper.sourceIdentifier
	if (si) {
		evenOdd.toggle()
		table.tr([class: "${evenOdd}"]) {
			th {
				it.mkp.yieldUnescaped 'Source&nbsp;ID'
			}
			td(si)
		}
	}

	evenOdd.toggle()
	table.tr([class: "${evenOdd}"]) {
		th {
			it.mkp.yieldUnescaped 'Local&nbsp;ID'
		}
		td(eventWrapper.localId)
	}
}

def buildThrowable(element, throwable, previousSTE, label, showStackTrace, wrappedExceptionStyle, previousStackTrace) {
	if (label && !wrappedExceptionStyle) {
		element.br()
		element.mkp.yield label
		element.br()
	}
	def stackTraceElement = null
	if (throwable.stackTrace && throwable.stackTrace.length > 0) {
		stackTraceElement = throwable.stackTrace[0]
	}
	if (!stackTraceElement) {
		stackTraceElement = previousSTE
	}
	element.div([class: 'throwable']) {
		if (throwable.cause && wrappedExceptionStyle) {
			buildThrowable(it, throwable.cause, stackTraceElement, null, showStackTrace, wrappedExceptionStyle, throwable.stackTrace)
			if (label) {
				element.br()
				element.mkp.yield label
				element.br()
			}
			element.br()
			element.mkp.yield 'Wrapped by: '
			element.br()
		}

		if (stackTraceElement) {
			buildThrowableText(it, throwable, 'ste://' + stackTraceElement.toString())
		} else {
			buildThrowableText(it, throwable)
		}
		if (showStackTrace && throwable.stackTrace != previousStackTrace) {
			it.hr()
			buildStackTrace(it, throwable.stackTrace, previousStackTrace)
		}

		if (throwable.suppressed) {
			throwable.suppressed.each { value ->
				buildThrowable(it, value, stackTraceElement, 'Suppressed: ', showStackTrace, wrappedExceptionStyle, throwable.stackTrace)
			}
		}

		if (throwable.cause && !wrappedExceptionStyle) {
			buildThrowable(it, throwable.cause, stackTraceElement, 'Caused by: ', showStackTrace, wrappedExceptionStyle, throwable.stackTrace)
		}
	}
}

def buildThrowableText(element, throwable, steHref = null) {
	if (steHref) {
		element.a([href: steHref]) {
			it.mkp.yield "${throwable.name}"
		}
		if (throwable.message && throwable.message != throwable.name) {
			element.pre() {
				it.a([href: steHref]) {
					it.mkp.yield throwable.message
				}
			}
		}
	} else {
		element.mkp.yield "${throwable.name}"
		if (throwable.message && throwable.message != throwable.name) {
			element.pre() {
				it.mkp.yield throwable.message
			}
		}
	}
}

def buildMarker(element, marker, handledMarkers = []) {
	element.mkp.yield "${marker.name}"
	if (handledMarkers.contains(marker.name)) {
		return
	}
	handledMarkers.add(marker.name)
	if (marker.references) {
		element.ul { parent ->
			marker.references.each { key, value ->
				parent.li {
					buildMarker(it, value, handledMarkers)
				}
			}
		}
	}
}

def buildNdc(element, Message[] ndc) {
	element.ul { ulIt ->
		ndc.each { message ->
			ulIt.li(message.message)
		}
	}
}

def buildStringMap(element, Map<String, String> map) {
	SortedMap<String, String> sorted = new TreeMap<String, String>(map)
	element.table { tableIt ->
		tr {
			th('Key')
			th('Value')
		}
		sorted.each { key, value ->
			tableIt.tr {
				td(key)
				td(value)
			}
		}
	}
}

def buildStringArrayMap(element, Map<String, String[]> map) {
	element.table { tableIt ->
		tr {
			th('Key')
			th('Value(s)')
		}
		map.each { key, value ->
			tableIt.tr {
				td(key)
				td {
					if (value.size() == 1) {
						it.mkp.yield value[0]
					} else {
						it.ul { ulIt ->
							value.each {
								ulIt.li(it)
							}
						}
					}
				}
			}
		}
	}
}

def buildStackTrace(element, callerData, previousStackTrace, onlyFirst = false) {
	if (!callerData) {
		return
	}

	int m = callerData.length - 1
	if (previousStackTrace) {
		int n = previousStackTrace.length - 1
		while (m >= 0 && n >= 0 && callerData[m].equals(previousStackTrace[n])) {
			m--
			n--
		}
	}

	for (int i = 0; i <= m; i++) {
		buildStackTraceElement(element, callerData[i])
		if (onlyFirst) {
			break
		}
	}
}

def buildStackTraceElement(element, ste) {
	def steStr = ste.toString()
	def extendedStr = ste.getExtendedString()
	element.a([href: 'ste://' + steStr]) {
		it.mkp.yieldUnescaped('at&nbsp;')
		it.mkp.yield steStr
		if (extendedStr) {
			it.mkp.yieldUnescaped('&nbsp;')
			it.mkp.yield(extendedStr)
		}
	}
}

class EvenOddToggle {
	boolean odd = true

	def toggle() {
		odd = !odd
	}

	def String toString() {
		odd ? 'odd' : 'even'
	}
}
