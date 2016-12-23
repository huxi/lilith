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

import spock.lang.Specification
import spock.lang.Unroll

import static de.huxhorn.sulky.junit.JUnitTools.testClone
import static de.huxhorn.sulky.junit.JUnitTools.testSerialization
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization

class EventContainsConditionSpec extends Specification {
	@Unroll
	def "Corpus works as expected for #condition (searchString=#input)."() {
		expect:
		ConditionCorpus.executeConditionOnCorpus(condition) == expectedResult

		where:
		input                            | expectedResult
		null                             | [] as Set
		''                               | ConditionCorpus.matchAllSet()
		'snafu'                          | [] as Set

		// level
		'EBU'                            | [9] as Set

		// logger
		'foo'                            | [13, 14, 74, 75] as Set
		'com'                            | [13, 14] as Set
		'com.foo'                        | [13, 14] as Set
		'com.foo.Foo'                    | [13] as Set
		'com.foo.Bar'                    | [14] as Set

		// message & ndc
		'message'                        | [17, 18, 19, 20, 21, 36, 37, 38, 39, 40] as Set
		'a message'                      | [17, 19, 20, 21, 36, 38, 39, 40] as Set
		'another message'                | [18, 37] as Set
		'a message.'                     | [17, 36] as Set
		'another message.'               | [18, 37] as Set
		'paramValue'                     | [19, 21, 22, 38, 40, 41] as Set
		'{}'                             | [20, 21, 23, 38, 39, 40, 41, 42] as Set

		// mdc, loggerContext properties, request headers, request parameters, response headers
		'Key'                            | [24, 68, 69, 80, 81, 98, 99, 101, 102, 103, 105, 106, 107, 109, 110, 111, 112, 113, 121] as Set

		// mdc, paramValue, loggerContext properties, request headers, request parameters, response headers
		'Value'                          | [19, 21, 22, 24, 38, 40, 41, 68, 80, 81, 98, 99, 101, 102, 103, 105, 106, 107, 109, 111, 112, 113, 121] as Set

		// throwable
		'java.lang.RuntimeException'     | [25, 26, 27, 28, 29, 30] as Set
		'java.lang.NullPointerException' | [26, 27, 28, 29, 30] as Set
		'java.lang.FooException'         | [27, 29, 30] as Set
		'java.lang.BarException'         | [30] as Set
		'RuntimeException'               | [25, 26, 27, 28, 29, 30] as Set
		'Exception'                      | [25, 26, 27, 28, 29, 30] as Set
		'java.lang'                      | [25, 26, 27, 28, 29, 30] as Set

		// Marker
		'-Marker'                        | [31, 32, 88] as Set
		'Foo-Marker'                     | [31] as Set
		'Bar-Marker'                     | [31, 32] as Set

		// loggerContext properties and name
		'Context'                        | [76, 77, 80, 81] as Set

		// thread info
		'threadName'                     | [83] as Set
		'11337'                          | [84] as Set
		'groupName'                      | [85] as Set
		'31337'                          | [86] as Set
		'1337'                           | [84, 86] as Set

		// threadName, groupName, contextName
		'Name'                           | [76, 77, 83, 85] as Set

		// broken ndc
		'b0rked3'                        | [87] as Set

		// exception message
		'exception1'                     | [89] as Set
		'exception2'                     | [90] as Set
		'exception3'                     | [91] as Set
		'exception4'                     | [92] as Set
		'exception5'                     | [92] as Set
		'exception'                      | [89, 90, 91, 92] as Set

		// status code
		'404'                            | [56] as Set
		'50'                             | [58] as Set

		// callStack, stackTrace
		'setPressed'                     | [44, 45, 46, 47, 48, 49, 50, 51, 65, 66, 95, 96, 116] as Set

		// thread priority, thread id, thread group id
		'7'                              | [84, 86, 117] as Set

		condition = new EventContainsCondition(input)
	}

	@Unroll
	def "serialization works with searchString #input."() {
		when:
		def condition = new EventContainsCondition()
		condition.searchString = input

		and:
		def result = testSerialization(condition)

		then:
		result.searchString == input

		where:
		input << inputValues()
	}

	@Unroll
	def "XML serialization works with searchString #input."() {
		when:
		def condition = new EventContainsCondition()
		condition.searchString = input

		and:
		def result = testXmlSerialization(condition)

		then:
		result.searchString == input

		where:
		input << inputValues()
	}

	@Unroll
	def "cloning works with searchString #input."() {
		when:
		def condition = new EventContainsCondition()
		condition.searchString = input

		and:
		def result = testClone(condition)

		then:
		result.searchString == input

		where:
		input << inputValues()
	}

	def "equals behaves as expected."() {
		setup:
		def instance = new EventContainsCondition()
		def other = new EventContainsCondition('foo')

		expect:
		instance.equals(instance)
		!instance.equals(null)
		!instance.equals(new Object())
		!instance.equals(other)
		!other.equals(instance)
	}

	def inputValues() {
		[null, '', 'value']
	}
}
