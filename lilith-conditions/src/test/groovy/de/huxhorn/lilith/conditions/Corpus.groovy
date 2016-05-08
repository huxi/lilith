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
package de.huxhorn.lilith.conditions

import de.huxhorn.lilith.data.access.AccessEvent
import de.huxhorn.lilith.data.eventsource.EventWrapper
import de.huxhorn.lilith.data.eventsource.SourceIdentifier
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement
import de.huxhorn.lilith.data.logging.LoggingEvent
import de.huxhorn.lilith.data.logging.Marker
import de.huxhorn.lilith.data.logging.Message
import de.huxhorn.lilith.data.logging.ThrowableInfo
import de.huxhorn.sulky.conditions.Condition
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class Corpus
{
	private static final Set<Integer> MATCH_ALL_SET = Collections.unmodifiableSet(matchAllSet(createCorpus()))

	private static final Marker FOO_MARKER=new Marker('Foo-Marker')
	private static final Marker BAR_MARKER=new Marker('Bar-Marker')

	static {
		FOO_MARKER.add(BAR_MARKER)
	}

	public static List<Object> createCorpus()
	{
		List<Object> result=new ArrayList<>()

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
		result.add(new EventWrapper<>(event: new AccessEvent(remoteUser: '-')))
		result.add(new EventWrapper<>(event: new AccessEvent(remoteUser: ' ')))
		result.add(new EventWrapper<>(event: new AccessEvent(remoteUser: ' - ')))
		result.add(new EventWrapper<>(event: new AccessEvent(remoteUser: 'sfalken')))
		result.add(new EventWrapper<>(event: new AccessEvent(remoteUser: ' sfalken ')))

		return result
	}

	public static Set<Integer> matchAllSet() {
		return MATCH_ALL_SET
	}

	public static Set<Integer> matchAllSet(List<Object> corpus) {
		Objects.requireNonNull(corpus, "corpus must not be null!")
		int size = corpus.size()
		if(size < 1) {
			return []
		}
		return 0..size-1
	}

	public static Set<Integer> executeConditionOnCorpus(Condition condition) {
		return executeConditionOnCorpus(condition, createCorpus())
	}

	public static Set<Integer> executeConditionOnCorpus(Condition condition, List<Object> corpus) {
		Objects.requireNonNull(condition, "condition must not be null!")
		Objects.requireNonNull(corpus, "corpus must not be null!")

		final Logger logger = LoggerFactory.getLogger(Corpus)

		Set<Integer> result=new HashSet<>();

		for(int i=0;i<corpus.size();i++) {
			Object current = corpus.get(i)
			boolean conditionResult = condition.isTrue(current)
			if(conditionResult) {
				result.add(i)
			}
			logger.debug('Condition #{}: {}.isTrue({}) evaluated to {}.', i, condition, current, conditionResult)
		}

		return result
	}

	private static class Foo implements Serializable
	{
		@SuppressWarnings("GroovyUnusedDeclaration")
		private static final long serialVersionUID = -5207922872610875882L
	}
}
