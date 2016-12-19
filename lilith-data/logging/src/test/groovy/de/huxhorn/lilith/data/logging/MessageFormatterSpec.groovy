/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2016 Joern Huxhorn
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
 * Copyright 2007-2016 Joern Huxhorn
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

package de.huxhorn.lilith.data.logging

import de.huxhorn.sulky.formatting.SafeString
import spock.lang.Specification
import spock.lang.Unroll

class MessageFormatterSpec extends Specification {

	private static final Throwable SOME_THROWABLE = new MessageFormatterUseCases.FooThrowable('FooException')

	@Unroll
	def "ArgumentResult.equals behaves as expected for #inputValue."() {
		setup:
		def empty = new MessageFormatter.ArgumentResult(null, null)

		expect:
		inputValue.equals(inputValue)
		!inputValue.equals(null)
		!inputValue.equals(new Object())
		!inputValue.equals(empty)
		!empty.equals(inputValue)

		where:
		inputValue << inputValues()
	}

	@Unroll
	def "ArgumentResult.hashCode behaves as expected for #inputValue."() {
		expect:
		inputValue.equals(otherInputValue)
		!(inputValue.is(otherInputValue))
		inputValue.hashCode() == otherInputValue.hashCode()

		where:
		inputValue << inputValues() + new MessageFormatter.ArgumentResult(null, null)
		otherInputValue << inputValues() + new MessageFormatter.ArgumentResult(null, null)
	}

	def "C'tor initialization, getArguments and getThrowable all work as expected."() {
		setup:
		def arguments = ['foo'] as String[]
		def throwable = new ExceptionWithEqualsAndHashCode('bar')

		when:
		def instance = new MessageFormatter.ArgumentResult(arguments, throwable)

		then:
		// stfu, IDEA.
		// noinspection GroovyAssignabilityCheck
		instance.arguments == arguments
		instance.throwable == throwable
	}

	def inputValues() {
		[
				new MessageFormatter.ArgumentResult([] as String[], null),
				new MessageFormatter.ArgumentResult(['first', 'second'] as String[], null),
				new MessageFormatter.ArgumentResult(['first', null, 'third'] as String[], null),
				new MessageFormatter.ArgumentResult(null, new ExceptionWithEqualsAndHashCode('foo')),
				new MessageFormatter.ArgumentResult([] as String[], new ExceptionWithEqualsAndHashCode('foo')),
				new MessageFormatter.ArgumentResult(['first', 'second'] as String[], new ExceptionWithEqualsAndHashCode('foo')),
				new MessageFormatter.ArgumentResult(['first', null, 'third'] as String[],  new ExceptionWithEqualsAndHashCode('foo')),
		]
	}

	private static class ExceptionWithEqualsAndHashCode extends RuntimeException {
		private String value
		ExceptionWithEqualsAndHashCode(String value) {
			super(value)
			this.value = value
		}

		boolean equals(o) {
			if (this.is(o)) return true
			if (o == null) return false
			if (getClass() != o.class) return false

			ExceptionWithEqualsAndHashCode that = (ExceptionWithEqualsAndHashCode) o

			if (value != that.value) return false

			return true
		}

		int hashCode() {
			return (value != null ? value.hashCode() : 0)
		}
	}

	def "Exception behaves as expected."() {
		expect:
		new ExceptionWithEqualsAndHashCode('foo').equals(new ExceptionWithEqualsAndHashCode('foo'))
		new ExceptionWithEqualsAndHashCode('foo').hashCode() == new ExceptionWithEqualsAndHashCode('foo').hashCode()
		!new ExceptionWithEqualsAndHashCode('foo').equals(new ExceptionWithEqualsAndHashCode('bar'))
		new ExceptionWithEqualsAndHashCode('foo').hashCode() != new ExceptionWithEqualsAndHashCode('bar').hashCode()
	}

	def "Coverage shall stop whining about not calling the default c'tor."() {
		expect:
		new MessageFormatter() != null
	}

	@Unroll
	def "MessageFormatter.format(#messagePattern, #arguments) returns expected #expectedResult."()
	{
		expect:
		MessageFormatter.format(messagePattern, arguments) == expectedResult

		where:
		messagePattern | arguments           | expectedResult
		null           | null                | null
		'foo {}'       | null                | 'foo {}'
		'foo {}'       | [] as String[]      | 'foo {}'
		'{} {}'        | ['foo'] as String[] | 'foo {}'
	}


	@Unroll
	def 'MessageFormatter.countArgumentPlaceholders(#messagePattern) returns expected #expectedResult.'() {
		expect:
		MessageFormatter.countArgumentPlaceholders(messagePattern) == expectedResult

		where:
		messagePattern | expectedResult
		// simple
		null           | 0
		'foo'          | 0
		'{}'           | 1
		'{} {} {}'     | 3
		// broken
		'{'            | 0
		'{} { {}'      | 2
		'{} {'         | 1
		// escaped
		'\\{}'         | 0
		'\\\\{}'       | 1
		'\\\\\\{}'     | 0
		'{} \\{}'      | 1
		'{} \\\\{}'    | 2
		'{} \\\\\\{}'  | 1
	}

	@Unroll
	def 'UseCases: MessageFormatter.countArgumentPlaceholders(#messagePattern) returns expected #expectedResult.'() {
		expect:
		MessageFormatter.countArgumentPlaceholders(messagePattern) == expectedResult

		where:
		useCase << MessageFormatterUseCases.generateUseCases()
		messagePattern = useCase.getMessagePattern()
		expectedResult = useCase.getNumberOfPlaceholders()
	}

	@Unroll
	def 'UseCases: MessageFormatter.evaluateArguments(#messagePattern, #argumentsString) returns expected #expectedResult.'() {
		expect:
		MessageFormatter.evaluateArguments(messagePattern, arguments) == expectedResult

		where:
		useCase << MessageFormatterUseCases.generateUseCases()
		messagePattern = useCase.getMessagePattern()
		arguments = useCase.getArguments()
		argumentsString = SafeString.toString(arguments)
		expectedResult = useCase.getArgumentResult()
	}

	@Unroll
	def 'MessageFormatter.evaluateArguments(#messagePattern, #argumentsString) returns expected #expectedResult.'() {
		expect:
		MessageFormatter.evaluateArguments(messagePattern, arguments) == expectedResult

		where:
		messagePattern | arguments                                           | argumentStrings                                         | throwable
		null           | null                                                | null                                                    | null
		'{}{}{}'       | ['foo', null, 1L] as Object[]                       | ['foo', 'null', '1'] as String[]                        | null
		'{}{}'         | ['foo', null, SOME_THROWABLE] as Object[]           | ['foo', 'null'] as String[]                             | SOME_THROWABLE
		'{}{}{}'       | ['foo', null, SOME_THROWABLE] as Object[]           | ['foo', 'null', 'FooException'] as String[]             | null
		'{}{}{}'       | ['foo', null, SOME_THROWABLE, 17L, 18L] as Object[] | ['foo', 'null', 'FooException', '17', '18'] as String[] | null
		'{}{}{}'       | ['foo', null, 17L, 18L, SOME_THROWABLE] as Object[] | ['foo', 'null', '17', '18'] as String[]                 | SOME_THROWABLE
		argumentsString = SafeString.toString(arguments)
		expectedResult = argumentStrings == null ? null : new MessageFormatter.ArgumentResult(argumentStrings, throwable)
	}

	@Unroll
	def 'UseCases: MessageFormatter.format(#messagePattern, #argumentsString) returns expected #expectedResult.'() {
		expect:
		MessageFormatter.format(messagePattern, arguments) == expectedResult

		where:
		useCase << MessageFormatterUseCases.generateUseCases()
		messagePattern = useCase.getMessagePattern()
		arguments = useCase.getArgumentStrings()
		argumentsString = SafeString.toString(arguments)
		expectedResult = useCase.getExpectedResult()
	}

	@Unroll
	def "messagePattern '#messagePattern' with arguments #arguments produces #expectedResult."() {
		when:
		def argumentResult = MessageFormatter.evaluateArguments(messagePattern, arguments)
		and:
		def result = MessageFormatter.format(messagePattern, argumentResult.arguments)

		then:
		result == expectedResult
		String.valueOf(argumentResult.throwable) == String.valueOf(expectedThrowable)

		where:

		messagePattern                                        | arguments                                                                       | expectedThrowable      | expectedResult
		'param1={}, param2={}, param3={}'                     | ['One', 'Two', 'Three', new RuntimeException()] as Object[]                     | new RuntimeException() | 'param1=One, param2=Two, param3=Three'
		'param1={}, param2={}, param3={}, exceptionString={}' | ['One', 'Two', 'Three', new RuntimeException()] as Object[]                     | null                   | 'param1=One, param2=Two, param3=Three, exceptionString=java.lang.RuntimeException'
		'exceptionString={}'                                  | [new RuntimeException()] as Object[]                                            | null                   | 'exceptionString=java.lang.RuntimeException'
		'param={}'                                            | ['One', 'Two', 'Three'] as Object[]                                             | null                   | 'param=[\'One\', \'Two\', \'Three\']'
		'param1={}, param2={}, param3={}'                     | ['One', 'Two', 'Three', 'Unused', 'Unused', new RuntimeException()] as Object[] | new RuntimeException() | 'param1=One, param2=Two, param3=Three'
		'param1={}, param2={}, param3={}'                     | ['One', ['Two.1', 'Two.2'], 'Three'] as Object[]                                | null                   | 'param1=One, param2=[\'Two.1\', \'Two.2\'], param3=Three'
	}
}
