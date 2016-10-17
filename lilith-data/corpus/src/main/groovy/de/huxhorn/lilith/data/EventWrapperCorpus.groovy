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
package de.huxhorn.lilith.data

import de.huxhorn.lilith.data.access.AccessEvent
import de.huxhorn.lilith.data.eventsource.EventWrapper
import de.huxhorn.lilith.data.eventsource.LoggerContext
import de.huxhorn.lilith.data.eventsource.SourceIdentifier
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement
import de.huxhorn.lilith.data.logging.LoggingEvent
import de.huxhorn.lilith.data.logging.Marker
import de.huxhorn.lilith.data.logging.Message
import de.huxhorn.lilith.data.logging.ThreadInfo
import de.huxhorn.lilith.data.logging.ThrowableInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class EventWrapperCorpus
{
	private static final Logger logger = LoggerFactory.getLogger(EventWrapperCorpus)

	private static final Set<Integer> MATCH_ALL_SET = Collections.unmodifiableSet(matchAllSet(createCorpus()))
	private static final Set<Integer> MATCH_ANY_LOGGING_SET = Collections.unmodifiableSet(matchAnyLoggingEventSet(createCorpus()))
	private static final Set<Integer> MATCH_ANY_ACCESS_SET = Collections.unmodifiableSet(matchAnyAccessEventSet(createCorpus()))
	private static final Set<Integer> MATCH_ANY_LOGGING_OR_ACCESS_SET = Collections.unmodifiableSet(matchAnyLoggingOrAccessEventSet(createCorpus()))
	private static final Set<Integer> MATCH_ANY_WRAPPER_SET = Collections.unmodifiableSet(matchAnyEventWrapperSet(createCorpus()))

	private static final Marker FOO_MARKER=new Marker('Foo-Marker')
	private static final Marker BAR_MARKER=new Marker('Bar-Marker')
	private static final Marker RECURSIVE_MARKER=new Marker('Recursive-Marker')

	static {
		FOO_MARKER.add(BAR_MARKER)
		RECURSIVE_MARKER.add(RECURSIVE_MARKER)
		if(logger.isInfoEnabled())
		{
			StringBuilder builder = new StringBuilder()
			List<Object> corpus = createCorpus()
			for(int i=0;i<corpus.size();i++) {
				if(builder.length()>0) {
					builder.append('\n')
				}
				builder.append('#').append(i).append('\n')
				builder.append('\t').append(corpus[i])
			}
			logger.info('EventWrapperCorpus:\n{}', builder)
		}
	}

	public static List<Object> createCorpus()
	{
		List<Object> result=new ArrayList<>()



// #0
		result.add(null)
		result.add(new Foo())
		result.add(new EventWrapper())
		result.add(new EventWrapper<>(new SourceIdentifier("identifier", "secondaryIdentifier"), 17L, new Foo()))
		result.add(new EventWrapper<>(event: new Foo()))
		result.add(new EventWrapper<>(event: new AccessEvent()))
		result.add(new EventWrapper<>(event: new LoggingEvent()))
		result.add(new EventWrapper<>(event: new LoggingEvent()))

		// level
		result.add(new EventWrapper<>(event: new LoggingEvent(level: LoggingEvent.Level.TRACE)))
		result.add(new EventWrapper<>(event: new LoggingEvent(level: LoggingEvent.Level.DEBUG)))



// #10
		result.add(new EventWrapper<>(event: new LoggingEvent(level: LoggingEvent.Level.INFO)))
		result.add(new EventWrapper<>(event: new LoggingEvent(level: LoggingEvent.Level.WARN)))
		result.add(new EventWrapper<>(event: new LoggingEvent(level: LoggingEvent.Level.ERROR)))

		// logger
		result.add(new EventWrapper<>(event: new LoggingEvent(logger: 'com.foo.Foo')))
		result.add(new EventWrapper<>(event: new LoggingEvent(logger: 'com.foo.Bar')))

		// message
		result.add(new EventWrapper<>(event: new LoggingEvent(message: new Message())))
		result.add(new EventWrapper<>(event: new LoggingEvent(message: new Message(null))))
		result.add(new EventWrapper<>(event: new LoggingEvent(message: new Message('a message.'))))
		result.add(new EventWrapper<>(event: new LoggingEvent(message: new Message('another message.'))))
		result.add(new EventWrapper<>(event: new LoggingEvent(message: new Message('a message with parameter {}.', ['paramValue'] as String[]))))



// #20
		result.add(new EventWrapper<>(event: new LoggingEvent(message: new Message('a message with unresolved parameter {}.'))))
		result.add(new EventWrapper<>(event: new LoggingEvent(message: new Message('a message with parameter {} and unresolved parameter {}.', ['paramValue'] as String[]))))
		result.add(new EventWrapper<>(event: new LoggingEvent(message: new Message('{}', ['paramValue'] as String[]))))
		result.add(new EventWrapper<>(event: new LoggingEvent(message: new Message('{}'))))

		// mdc
		result.add(new EventWrapper<>(event: new LoggingEvent(mdc: ['mdcKey': 'mdcValue'])))

		// throwable
		result.add(new EventWrapper<>(event: new LoggingEvent(throwable: new ThrowableInfo(name: 'java.lang.RuntimeException'))))
		result.add(new EventWrapper<>(event: new LoggingEvent(throwable: new ThrowableInfo(name: 'java.lang.RuntimeException', cause: new ThrowableInfo(name: 'java.lang.NullPointerException')))))
		result.add(new EventWrapper<>(event: new LoggingEvent(throwable: new ThrowableInfo(name: 'java.lang.RuntimeException', cause: new ThrowableInfo(name: 'java.lang.NullPointerException', cause: new ThrowableInfo(name: 'java.lang.FooException'))))))
		result.add(new EventWrapper<>(event: new LoggingEvent(throwable: new ThrowableInfo(name: 'java.lang.RuntimeException', suppressed: [new ThrowableInfo(name: 'java.lang.NullPointerException')]))))
		result.add(new EventWrapper<>(event: new LoggingEvent(throwable: new ThrowableInfo(name: 'java.lang.RuntimeException', suppressed: [new ThrowableInfo(name: 'java.lang.NullPointerException'), new ThrowableInfo(name: 'java.lang.FooException')]))))



// #30
		result.add(new EventWrapper<>(event: new LoggingEvent(throwable: new ThrowableInfo(name: 'java.lang.RuntimeException', cause: new ThrowableInfo(name: 'java.lang.BarException'), suppressed: [new ThrowableInfo(name: 'java.lang.NullPointerException'), new ThrowableInfo(name: 'java.lang.FooException')]))))

		// marker
		result.add(new EventWrapper<>(event: new LoggingEvent(marker: FOO_MARKER)))
		result.add(new EventWrapper<>(event: new LoggingEvent(marker: BAR_MARKER)))

		// ndc
		result.add(new EventWrapper<>(event: new LoggingEvent(ndc: [])))
		result.add(new EventWrapper<>(event: new LoggingEvent(ndc: [new Message()])))
		result.add(new EventWrapper<>(event: new LoggingEvent(ndc: [new Message(null)])))
		result.add(new EventWrapper<>(event: new LoggingEvent(ndc: [new Message('a message.')])))
		result.add(new EventWrapper<>(event: new LoggingEvent(ndc: [new Message('another message.')])))
		result.add(new EventWrapper<>(event: new LoggingEvent(ndc: [new Message('a message with parameter {}.', ['paramValue'] as String[])])))
		result.add(new EventWrapper<>(event: new LoggingEvent(ndc: [new Message('a message with unresolved parameter {}.')])))



// #40
		result.add(new EventWrapper<>(event: new LoggingEvent(ndc: [new Message('a message with parameter {} and unresolved parameter {}.', ['paramValue'] as String[])])))
		result.add(new EventWrapper<>(event: new LoggingEvent(ndc: [new Message('{}', ['paramValue'] as String[])])))
		result.add(new EventWrapper<>(event: new LoggingEvent(ndc: [new Message('{}')])))

		// call stack
		result.add(new EventWrapper<>(event: new LoggingEvent(callStack: [])))
		result.add(new EventWrapper<>(event: new LoggingEvent(callStack: [
				ExtendedStackTraceElement.parseStackTraceElement('de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358) ~[de.huxhorn.lilith-8.1.0-SNAPSHOT.jar:na]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]'),
		])))
		result.add(new EventWrapper<>(event: new LoggingEvent(callStack: [
				ExtendedStackTraceElement.parseStackTraceElement('de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358) ~[de.huxhorn.lilith-8.1.0-SNAPSHOT.jar:na]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252) ~[na:1.8.0_92]'),
		])))
		result.add(new EventWrapper<>(event: new LoggingEvent(callStack: [
				ExtendedStackTraceElement.parseStackTraceElement('de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358)'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022)'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348)'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402)'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259)'),
		])))
		result.add(new EventWrapper<>(event: new LoggingEvent(callStack: [
				ExtendedStackTraceElement.parseStackTraceElement('de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358)'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022)'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348)'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402)'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259)'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252)'),
		])))

		result.add(new EventWrapper<>(event: new LoggingEvent(callStack: [
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]'),
		])))
		result.add(new EventWrapper<>(event: new LoggingEvent(callStack: [
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252) ~[na:1.8.0_92]'),
		])))



// #50
		result.add(new EventWrapper<>(event: new LoggingEvent(callStack: [
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022)'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348)'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402)'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259)'),
		])))
		result.add(new EventWrapper<>(event: new LoggingEvent(callStack: [
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022)'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348)'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402)'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259)'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:252)'),
		])))

		// status code
		result.add(new EventWrapper<>(event: new AccessEvent(statusCode: 100)))
		result.add(new EventWrapper<>(event: new AccessEvent(statusCode: 200)))
		result.add(new EventWrapper<>(event: new AccessEvent(statusCode: 202)))
		result.add(new EventWrapper<>(event: new AccessEvent(statusCode: 301)))
		result.add(new EventWrapper<>(event: new AccessEvent(statusCode: 404)))
		result.add(new EventWrapper<>(event: new AccessEvent(statusCode: 451)))
		result.add(new EventWrapper<>(event: new AccessEvent(statusCode: 500)))

		// remote user
		result.add(new EventWrapper<>(event: new AccessEvent(remoteUser: '')))



// #60
		result.add(new EventWrapper<>(event: new AccessEvent(remoteUser: '-')))
		result.add(new EventWrapper<>(event: new AccessEvent(remoteUser: ' ')))
		result.add(new EventWrapper<>(event: new AccessEvent(remoteUser: ' - ')))
		result.add(new EventWrapper<>(event: new AccessEvent(remoteUser: 'sfalken')))
		result.add(new EventWrapper<>(event: new AccessEvent(remoteUser: ' sfalken ')))

		// broken call stack
		result.add(new EventWrapper<>(event: new LoggingEvent(callStack: [
				null,
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]'),
		])))
		result.add(new EventWrapper<>(event: new LoggingEvent(callStack: [
				ExtendedStackTraceElement.parseStackTraceElement('de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358) ~[de.huxhorn.lilith-8.1.0-SNAPSHOT.jar:na]'),
				null,
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]'),
		])))

		// more mdc
		result.add(new EventWrapper<>(event: new LoggingEvent(mdc: [:])))
		result.add(new EventWrapper<>(event: new LoggingEvent(mdc: ['mdcKey': 'otherMdcValue'])))
		result.add(new EventWrapper<>(event: new LoggingEvent(mdc: ['mdcKey': null])))



// #70
		// http method
		result.add(new EventWrapper<>(event: new AccessEvent(method: 'GET')))
		result.add(new EventWrapper<>(event: new AccessEvent(method: 'PUT')))

		// request URI
		result.add(new EventWrapper<>(event: new AccessEvent(requestURI: '/')))
		result.add(new EventWrapper<>(event: new AccessEvent(requestURI: '/index.html')))

		// request URL
		result.add(new EventWrapper<>(event: new AccessEvent(requestURL: 'GET /?foo=bar&foo=schnurz HTTP/1.1')))
		result.add(new EventWrapper<>(event: new AccessEvent(requestURL: 'GET /index.html?foo=bar&foo=schnurz HTTP/1.1')))

		// logger context name
		result.add(new EventWrapper<>(event: new LoggingEvent(loggerContext: new LoggerContext(name: 'loggerContextName'))))
		result.add(new EventWrapper<>(event: new AccessEvent(loggerContext: new LoggerContext(name: 'loggerContextName'))))

		// logger context properties
		result.add(new EventWrapper<>(event: new LoggingEvent(loggerContext: new LoggerContext(properties: [:]))))
		result.add(new EventWrapper<>(event: new AccessEvent(loggerContext: new LoggerContext(properties: [:]))))



// #80
		result.add(new EventWrapper<>(event: new LoggingEvent(loggerContext: new LoggerContext(properties: ['loggerContextKey':'loggerContextValue']))))
		result.add(new EventWrapper<>(event: new AccessEvent(loggerContext: new LoggerContext(properties: ['loggerContextKey':'loggerContextValue']))))

		// thread info
		result.add(new EventWrapper<>(event: new LoggingEvent(threadInfo: new ThreadInfo())))
		result.add(new EventWrapper<>(event: new LoggingEvent(threadInfo: new ThreadInfo(name: 'threadName'))))
		result.add(new EventWrapper<>(event: new LoggingEvent(threadInfo: new ThreadInfo(id: 11337))))
		result.add(new EventWrapper<>(event: new LoggingEvent(threadInfo: new ThreadInfo(groupName: 'groupName'))))
		result.add(new EventWrapper<>(event: new LoggingEvent(threadInfo: new ThreadInfo(groupId: 31337))))

		// broken ndc with gap
		result.add(new EventWrapper<>(event: new LoggingEvent(ndc: [new Message('b0rked1'), null, new Message('b0rked3')])))

		// recursive marker
		result.add(new EventWrapper<>(event: new LoggingEvent(marker: RECURSIVE_MARKER)))

		// throwable message
		result.add(new EventWrapper<>(event: new LoggingEvent(throwable: new ThrowableInfo(message: 'exception1'))))
		// cause
		result.add(new EventWrapper<>(event: new LoggingEvent(throwable: new ThrowableInfo(cause: new ThrowableInfo(message: 'exception2')))))



// #90
		// suppressed
		result.add(new EventWrapper<>(event: new LoggingEvent(throwable: new ThrowableInfo(suppressed: [new ThrowableInfo(message: 'exception3')]))))
		// broken suppressed array
		result.add(new EventWrapper<>(event: new LoggingEvent(throwable: new ThrowableInfo(suppressed: [new ThrowableInfo(message: 'exception4'), null, new ThrowableInfo(message: 'exception5')]))))

		// recursive throwables
		ThrowableInfo recursiveCause = new ThrowableInfo(name: 'recursiveCause')
		recursiveCause.cause=recursiveCause
		result.add(new EventWrapper<>(event: new LoggingEvent(throwable: recursiveCause)))

		ThrowableInfo recursiveSuppressed = new ThrowableInfo(name: 'recursiveSuppressed')
		recursiveSuppressed.setSuppressed([recursiveSuppressed] as ThrowableInfo[])
		result.add(new EventWrapper<>(event: new LoggingEvent(throwable: recursiveSuppressed)))

		// broken throwable stack trace
		result.add(new EventWrapper<>(event: new LoggingEvent(throwable: new ThrowableInfo(stackTrace: [
				null,
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]'),
		]))))

		result.add(new EventWrapper<>(event: new LoggingEvent(throwable: new ThrowableInfo(cause: new ThrowableInfo(stackTrace: [
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:2022) ~[na:1.8.0_92]'),
				null,
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]'),
		])))))

		// empty mdc
		result.add(new EventWrapper<>(event: new LoggingEvent(mdc: [:])))
		// mdc null cases
		result.add(new EventWrapper<>(event: new LoggingEvent(mdc: ['nullMdcValueKey': null])))
		Map<String, String> nullMdcKeyMap = new HashMap<>()
		nullMdcKeyMap.put(null, 'nullMdcKeyValue')
		result.add(new EventWrapper<>(event: new LoggingEvent(mdc: nullMdcKeyMap)))



// #100
		// request headers
		result.add(new EventWrapper<>(event: new AccessEvent(requestHeaders: [:])))
		result.add(new EventWrapper<>(event: new AccessEvent(requestHeaders: ['requestHeaderKey':'requestHeaderValue'])))
		result.add(new EventWrapper<>(event: new AccessEvent(requestHeaders: ['nullRequestHeaderValueKey':null])))
		Map<String, String> nullRequestHeaderKeyMap = new HashMap<>()
		nullRequestHeaderKeyMap.put(null, 'nullRequestHeaderKeyValue')
		result.add(new EventWrapper<>(event: new AccessEvent(requestHeaders: nullRequestHeaderKeyMap)))

		// response headers
		result.add(new EventWrapper<>(event: new AccessEvent(responseHeaders: [:])))
		result.add(new EventWrapper<>(event: new AccessEvent(responseHeaders: ['responseHeaderKey':'responseHeaderValue'])))
		result.add(new EventWrapper<>(event: new AccessEvent(responseHeaders: ['nullResponseHeaderValueKey':null])))
		Map<String, String> nullResponseHeaderKeyMap = new HashMap<>()
		nullResponseHeaderKeyMap.put(null, 'nullResponseHeaderKeyValue')
		result.add(new EventWrapper<>(event: new AccessEvent(responseHeaders: nullResponseHeaderKeyMap)))

		// request parameters
		result.add(new EventWrapper<>(event: new AccessEvent(requestParameters: [:])))
		result.add(new EventWrapper<>(event: new AccessEvent(requestParameters: ['nullRequestParameterValueKey':null])))



// #110
		result.add(new EventWrapper<>(event: new AccessEvent(requestParameters: ['requestParameterKey':[] as String[]])))
		result.add(new EventWrapper<>(event: new AccessEvent(requestParameters: ['requestParameterKey':['requestParameterValue1', 'requestParameterValue2'] as String[]])))
		result.add(new EventWrapper<>(event: new AccessEvent(requestParameters: ['requestParameterKey':['requestParameterValue1', null, 'requestParameterValue3'] as String[]])))
		Map<String, String[]> nullRequestParameterKeyMap = new HashMap<>()
		nullRequestParameterKeyMap.put(null, ['nullRequestHeaderKeyValue'] as String[])
		result.add(new EventWrapper<>(event: new AccessEvent(requestParameters: nullRequestParameterKeyMap)))

		// empty String logger name
		result.add(new EventWrapper<>(event: new LoggingEvent(logger: '')))

		// empty String throwable name
		result.add(new EventWrapper<>(event: new LoggingEvent(throwable: new ThrowableInfo(name: ''))))

		// missing first call stack entry
		result.add(new EventWrapper<>(event: new LoggingEvent(callStack: [
				null,
				ExtendedStackTraceElement.parseStackTraceElement('de.huxhorn.lilith.debug.DebugDialog$LogAllAction.actionPerformed(DebugDialog.java:358) ~[de.huxhorn.lilith-8.1.0-SNAPSHOT.jar:na]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2348) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:402) ~[na:1.8.0_92]'),
				ExtendedStackTraceElement.parseStackTraceElement('javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:259) ~[na:1.8.0_92]'),
		])))

		// thread priority
		result.add(new EventWrapper<>(event: new LoggingEvent(threadInfo: new ThreadInfo(priority: 7))))

		result.add(new EventWrapper<>(event: new AccessEvent(statusCode: 99))) // status code too small
		result.add(new EventWrapper<>(event: new AccessEvent(statusCode: 600))) // status code too large
// #120
		result.add(new EventWrapper<>(event: new AccessEvent(statusCode: 488))) // unknown status code

		// mdc with null key and other key/value
		nullMdcKeyMap = new HashMap<>()
		nullMdcKeyMap.put(null, 'nullMdcKeyValue')
		nullMdcKeyMap.put('nonNullKey', 'nonNullValue')
		result.add(new EventWrapper<>(event: new LoggingEvent(mdc: nullMdcKeyMap)))

		// multi-level request URI
		result.add(new EventWrapper<>(event: new AccessEvent(requestURI: '/foo/bar/foobar')))

		return result
	}

	public static Set<Integer> matchAllSet() {
		MATCH_ALL_SET
	}

	public static Set<Integer> matchAllSet(List<Object> corpus) {
		Objects.requireNonNull(corpus, "corpus must not be null!")
		int size = corpus.size()
		if(size < 1) {
			return []
		}
		return 0..size-1
	}

	public static Set<Integer> matchAnyLoggingEventSet() {
		MATCH_ANY_LOGGING_SET
	}

	public static Set<Integer> matchAnyLoggingEventSet(List<Object> corpus) {
		def result = []
		for(int i=0; i<corpus.size(); i++) {
			def current = corpus[i]
			if(current instanceof EventWrapper) {
				def event = ((EventWrapper)current).event
				if(event instanceof LoggingEvent) {
					result.add(i)
				}
			}
		}
		return result
	}

	public static Set<Integer> matchAnyAccessEventSet() {
		MATCH_ANY_ACCESS_SET
	}

	public static Set<Integer> matchAnyAccessEventSet(List<Object> corpus) {
		def result = []
		for(int i=0; i<corpus.size(); i++) {
			def current = corpus[i]
			if(current instanceof EventWrapper) {
				def event = ((EventWrapper)current).event
				if(event instanceof AccessEvent) {
					result.add(i)
				}
			}
		}
		return result
	}

	public static Set<Integer> matchAnyLoggingOrAccessEventSet() {
		MATCH_ANY_LOGGING_OR_ACCESS_SET
	}

	public static Set<Integer> matchAnyLoggingOrAccessEventSet(List<Object> corpus) {
		def result = []
		for(int i=0; i<corpus.size(); i++) {
			def current = corpus[i]
			if(current instanceof EventWrapper) {
				def event = ((EventWrapper)current).event
				if(event instanceof AccessEvent) {
					result.add(i)
				}
				if(event instanceof LoggingEvent) {
					result.add(i)
				}
			}
		}
		return result
	}

	public static Set<Integer> matchAnyEventWrapperSet() {
		MATCH_ANY_WRAPPER_SET
	}

	public static Set<Integer> matchAnyEventWrapperSet(List<Object> corpus) {
		def result = []
		for(int i=0; i<corpus.size(); i++) {
			def current = corpus[i]
			if(current instanceof EventWrapper) {
				result.add(i)
			}
		}
		return result
	}

	private static class Foo implements Serializable
	{
		@SuppressWarnings("GroovyUnusedDeclaration")
		private static final long serialVersionUID = -5207922872610875882L
	}
}
