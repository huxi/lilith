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

package de.huxhorn.lilith.services.clipboard

import de.huxhorn.lilith.data.access.AccessEvent
import de.huxhorn.lilith.data.eventsource.EventWrapper
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement
import de.huxhorn.lilith.data.logging.LoggingEvent
import de.huxhorn.lilith.data.logging.Message
import de.huxhorn.lilith.data.logging.ThreadInfo
import de.huxhorn.lilith.data.logging.ThrowableInfo
import spock.lang.Specification
import spock.lang.Unroll

class FormatterToolsSpec extends Specification {

	@Unroll
	def "isNullOrEmpty for #input#inputClass returns #expectedResult."() {
		expect:
		FormatterTools.isNullOrEmpty(input) == expectedResult

		where:
		input                             | expectedResult
		null                              | true
		''                                | true
		[:] as Map                        | true
		[] as Set                         | true
		[] as List                        | true
		[] as Object[]                    | true
		'foo'                             | false
		1                                 | false
		['fooKey': 'fooValue'] as Map     | false
		['foo'] as Set                    | false
		['foo'] as List                   | false
		['foo'] as Object[]               | false
		['foo'] as TreeSet                | false
		['fooKey': 'fooValue'] as TreeMap | false
		[null] as Set                     | false
		[null] as Set                     | false
		[null] as Object[]                | false
		[new Foo(value: '1')] as Set      | false
		FOO_MAP                           | false
		NULL_MAP                          | false

		inputClass = input == null ? '' : ' (' + input.getClass() + ')'
	}

	@Unroll
	def "toStringOrNull for #input#inputClass returns #expectedResult."() {
		expect:
		FormatterTools.toStringOrNull(input) == expectedResult

		where:

		input                             | expectedResult
		null                              | null
		''                                | null
		[:] as Map                        | null
		[] as Set                         | null
		[] as List                        | null
		[] as Object[]                    | null
		'foo'                             | 'foo'
		1                                 | '1'
		['fooKey': 'fooValue'] as Map     | '[\'fooKey\':\'fooValue\']'
		['foo'] as Set                    | '[\'foo\']'
		['foo'] as List                   | '[\'foo\']'
		['foo'] as Object[]               | '[\'foo\']'
		['foo'] as TreeSet                | '[\'foo\']'
		['fooKey': 'fooValue'] as TreeMap | '[\'fooKey\':\'fooValue\']'
		[null] as Set                     | '[null]'
		[null] as List                    | '[null]'
		[null] as Object[]                | '[null]'
		[new Foo(value: '1')] as Set      | '[Foo{value=\'1\'}]'
		FOO_MAP                           | '[Foo{value=\'1\'}:\'x\']'
		NULL_MAP                          | '[null:\'x\']'

		inputClass = input == null ? '' : ' (' + input.getClass() + ')'
	}

	@Unroll
	def 'resolveAccessEvent(#input#inputClass) is#not present.'() {
		expect:
		FormatterTools.resolveAccessEvent(input).present == present


		where:
		input                          | present
		null                           | false
		1                              | false
		wrapperFor(new AccessEvent())  | true
		wrapperFor(new LoggingEvent()) | false

		inputClass = input == null ? '' : ' (' + input.getClass() + ')'
		not = present ? '' : ' not'
	}

	@Unroll
	def 'resolveLoggingEvent(#input#inputClass) is#not present.'() {
		expect:
		FormatterTools.resolveLoggingEvent(input).present == present


		where:
		input                          | present
		null                           | false
		1                              | false
		wrapperFor(new AccessEvent())  | false
		wrapperFor(new LoggingEvent()) | true

		inputClass = input == null ? '' : ' (' + input.getClass() + ')'
		not = present ? '' : ' not'
	}

	@Unroll
	def 'resolveFormattedMessage(#input#inputClass) is#not present.'() {
		when:
		Optional<String> optionalResult = FormatterTools.resolveFormattedMessage(input)

		then:
		optionalResult.present == present
		optionalResult.orElse(null) == expectedResult

		where:
		input                                                                         | present | expectedResult
		null                                                                          | false   | null
		1                                                                             | false   | null
		wrapperFor(new AccessEvent())                                                 | false   | null
		wrapperFor(new LoggingEvent())                                                | false   | null
		wrapperFor(new LoggingEvent(message: new Message('')))                        | false   | null
		wrapperFor(new LoggingEvent(message: new Message('foo')))                     | true    | 'foo'
		wrapperFor(new LoggingEvent(message: new Message('{}', ['foo'] as String[]))) | true    | 'foo'

		inputClass = input == null ? '' : ' (' + input.getClass() + ')'
		not = present ? '' : ' not'
	}

	@Unroll
	def 'resolveMessagePattern(#input#inputClass) is#not present.'() {
		when:
		Optional<String> optionalResult = FormatterTools.resolveMessagePattern(input)

		then:
		optionalResult.present == present
		optionalResult.orElse(null) == expectedResult

		where:
		input                                                                         | present | expectedResult
		null                                                                          | false   | null
		1                                                                             | false   | null
		wrapperFor(new AccessEvent())                                                 | false   | null
		wrapperFor(new LoggingEvent())                                                | false   | null
		wrapperFor(new LoggingEvent(message: new Message('')))                        | false   | null
		wrapperFor(new LoggingEvent(message: new Message('foo')))                     | true    | 'foo'
		wrapperFor(new LoggingEvent(message: new Message('{}', ['foo'] as String[]))) | true    | '{}'

		inputClass = input == null ? '' : ' (' + input.getClass() + ')'
		not = present ? '' : ' not'
	}

	@Unroll
	def 'resolveCallStack(#input#inputClass) is#not present.'() {
		when:
		Optional<StackTraceElement[]> optionalResult = FormatterTools.resolveCallStack(input)

		then:
		optionalResult.present == present
		optionalResult.orElse(null) == expectedResult

		where:
		input                                                                      | present | expectedResult
		null                                                                       | false   | null
		1                                                                          | false   | null
		wrapperFor(new AccessEvent())                                              | false   | null
		wrapperFor(new LoggingEvent())                                             | false   | null
		wrapperFor(new LoggingEvent(callStack: [] as ExtendedStackTraceElement[])) | false   | null
		wrapperFor(new LoggingEvent(callStack: callStack()))                       | true    | callStack()

		inputClass = input == null ? '' : ' (' + input.getClass() + ')'
		not = present ? '' : ' not'
	}

	@Unroll
	def 'resolveThrowableInfo(#input#inputClass) is#not present.'() {
		when:
		Optional<ThrowableInfo> optionalResult = FormatterTools.resolveThrowableInfo(input)

		then:
		optionalResult.present == present
		optionalResult.orElse(null) == expectedResult

		where:
		input                                                         | present | expectedResult
		null                                                          | false   | null
		1                                                             | false   | null
		wrapperFor(new AccessEvent())                                 | false   | null
		wrapperFor(new LoggingEvent())                                | false   | null
		wrapperFor(new LoggingEvent(throwable: throwableInfo(null)))  | true    | throwableInfo(null)
		wrapperFor(new LoggingEvent(throwable: throwableInfo('')))    | true    | throwableInfo('')
		wrapperFor(new LoggingEvent(throwable: throwableInfo('foo'))) | true    | throwableInfo('foo')

		inputClass = input == null ? '' : ' (' + input.getClass() + ')'
		not = present ? '' : ' not'
	}

	@Unroll
	def 'resolveThrowableInfoName(#input#inputClass) is#not present.'() {
		when:
		Optional<String> optionalResult = FormatterTools.resolveThrowableInfoName(input)

		then:
		optionalResult.present == present
		optionalResult.orElse(null) == expectedResult

		where:
		input                                                         | present | expectedResult
		null                                                          | false   | null
		1                                                             | false   | null
		wrapperFor(new AccessEvent())                                 | false   | null
		wrapperFor(new LoggingEvent())                                | false   | null
		wrapperFor(new LoggingEvent(throwable: throwableInfo(null)))  | false   | null
		wrapperFor(new LoggingEvent(throwable: throwableInfo('')))    | false   | null
		wrapperFor(new LoggingEvent(throwable: throwableInfo('foo'))) | true    | 'foo'

		inputClass = input == null ? '' : ' (' + input.getClass() + ')'
		not = present ? '' : ' not'
	}

	@Unroll
	def 'resolveThreadName(#input#inputClass) is#not present.'() {
		when:
		Optional<String> optionalResult = FormatterTools.resolveThreadName(input)

		then:
		optionalResult.present == present
		optionalResult.orElse(null) == expectedResult

		where:
		input                                                              | present | expectedResult
		null                                                               | false   | null
		1                                                                  | false   | null
		wrapperFor(new AccessEvent())                                      | false   | null
		wrapperFor(new LoggingEvent())                                     | false   | null
		wrapperFor(new LoggingEvent(threadInfo: threadInfo(null, null)))   | false   | null
		wrapperFor(new LoggingEvent(threadInfo: threadInfo('', '')))       | false   | null
		wrapperFor(new LoggingEvent(threadInfo: threadInfo('foo', 'bar'))) | true    | 'foo'

		inputClass = input == null ? '' : ' (' + input.getClass() + ')'
		not = present ? '' : ' not'
	}

	@Unroll
	def 'resolveThreadGroupName(#input#inputClass) is#not present.'() {
		when:
		Optional<String> optionalResult = FormatterTools.resolveThreadGroupName(input)

		then:
		optionalResult.present == present
		optionalResult.orElse(null) == expectedResult

		where:
		input                                                              | present | expectedResult
		null                                                               | false   | null
		1                                                                  | false   | null
		wrapperFor(new AccessEvent())                                      | false   | null
		wrapperFor(new LoggingEvent())                                     | false   | null
		wrapperFor(new LoggingEvent(threadInfo: threadInfo(null, null)))   | false   | null
		wrapperFor(new LoggingEvent(threadInfo: threadInfo('', '')))       | false   | null
		wrapperFor(new LoggingEvent(threadInfo: threadInfo('foo', 'bar'))) | true    | 'bar'

		inputClass = input == null ? '' : ' (' + input.getClass() + ')'
		not = present ? '' : ' not'
	}

	private static EventWrapper<AccessEvent> wrapperFor(AccessEvent event) {
		new EventWrapper<>(null, event)
	}

	private static EventWrapper<LoggingEvent> wrapperFor(LoggingEvent event) {
		new EventWrapper<>(null, event)
	}

	private static ExtendedStackTraceElement[] callStack() {
		[
		        new ExtendedStackTraceElement()
		] as ExtendedStackTraceElement[]
	}

	private static ThrowableInfo throwableInfo(String name) {
		new ThrowableInfo(name: name)
	}

	private static threadInfo(String name, String groupName) {
		return new ThreadInfo(name: name, groupName: groupName)
	}

	private static final Map<Foo, String> FOO_MAP = new HashMap<>()

	static
	{
		FOO_MAP.put(new Foo(value: '1'), 'x')
	}

	private static final Map<String, String> NULL_MAP = new HashMap<>()

	static
	{
		NULL_MAP.put(null, 'x')
	}

	private static class Foo {
		String value

		boolean equals(o) {
			if (this.is(o)) return true
			if (getClass() != o.class) return false

			Foo foo = (Foo) o

			if (value != foo.value) return false

			return true
		}

		int hashCode() {
			return (value != null ? value.hashCode() : 0)
		}


		@Override
		String toString() {
			return "Foo{" +
					"value='" + value + '\'' +
					'}';
		}
	}
}
