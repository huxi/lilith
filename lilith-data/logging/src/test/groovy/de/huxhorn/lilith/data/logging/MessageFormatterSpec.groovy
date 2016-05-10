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

import spock.lang.Specification
import spock.lang.Unroll

class MessageFormatterSpec extends Specification {
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
}
