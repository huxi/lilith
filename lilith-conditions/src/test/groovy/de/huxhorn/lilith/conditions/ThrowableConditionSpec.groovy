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

import de.huxhorn.lilith.data.logging.ThrowableInfo
import spock.lang.Specification
import spock.lang.Unroll

import static de.huxhorn.sulky.junit.JUnitTools.testClone
import static de.huxhorn.sulky.junit.JUnitTools.testSerialization
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization;


public class ThrowableConditionSpec extends Specification
{
	@Unroll
	def 'collectThrowableNames(#throwableInfo) works as expected.'() {
		when:
		def result = ThrowableCondition.collectThrowableNames(throwableInfo)

		then:
		result == expectedResult

		where:
		expectedResult                                                                                                              | throwableInfo
		[] as Set                                                                                                                   | null
		['java.lang.RuntimeException'] as Set                                                                                       | new ThrowableInfo(name: 'java.lang.RuntimeException')
		['java.lang.RuntimeException', 'java.lang.NullPointerException'] as Set                                                     | new ThrowableInfo(name: 'java.lang.RuntimeException', cause: new ThrowableInfo(name: 'java.lang.NullPointerException'))
		['java.lang.RuntimeException', 'java.lang.NullPointerException', 'java.lang.FooException'] as Set                           | new ThrowableInfo(name: 'java.lang.RuntimeException', cause: new ThrowableInfo(name: 'java.lang.NullPointerException', cause: new ThrowableInfo(name: 'java.lang.FooException')))
		['java.lang.RuntimeException', 'java.lang.NullPointerException'] as Set                                                     | new ThrowableInfo(name: 'java.lang.RuntimeException', suppressed: [new ThrowableInfo(name: 'java.lang.NullPointerException')])
		['java.lang.RuntimeException', 'java.lang.NullPointerException', 'java.lang.FooException'] as Set                           | new ThrowableInfo(name: 'java.lang.RuntimeException', suppressed: [new ThrowableInfo(name: 'java.lang.NullPointerException'), new ThrowableInfo(name: 'java.lang.FooException')])
		['java.lang.RuntimeException', 'java.lang.NullPointerException', 'java.lang.FooException', 'java.lang.BarException'] as Set | new ThrowableInfo(name: 'java.lang.RuntimeException', cause: new ThrowableInfo(name: 'java.lang.BarException'), suppressed: [new ThrowableInfo(name: 'java.lang.NullPointerException'), new ThrowableInfo(name: 'java.lang.FooException')])
		['java.lang.RuntimeException', 'java.lang.NullPointerException', 'java.lang.FooException'] as Set                           | new ThrowableInfo(name: 'java.lang.RuntimeException', cause: new ThrowableInfo(), suppressed: [new ThrowableInfo(name: 'java.lang.NullPointerException'), new ThrowableInfo(name: 'java.lang.FooException')])
	}

	@Unroll
	def "Corpus works as expected for #condition (searchString=#input)."() {
		expect:
		ConditionCorpus.executeConditionOnCorpus(condition) == expectedResult

		where:
		input                            | expectedResult
		null                             | [25, 26, 27, 28, 29, 30, 89, 90, 91, 92, 93, 94, 95, 96, 115] as Set
		''                               | [25, 26, 27, 28, 29, 30, 89, 90, 91, 92, 93, 94, 95, 96, 115] as Set
		'snafu'                          | [] as Set
		'java.lang.RuntimeException'     | [25, 26, 27, 28, 29, 30] as Set
		'java.lang.NullPointerException' | [26, 27, 28, 29, 30] as Set
		'java.lang.FooException'         | [27, 29, 30] as Set
		'java.lang.BarException'         | [30] as Set

		condition = new ThrowableCondition(input)
	}


	@Unroll
	def "serialization works with searchString #input."() {
		when:
		def condition = new ThrowableCondition()
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
		def condition = new ThrowableCondition()
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
		def condition = new ThrowableCondition()
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
		def instance = new ThrowableCondition()
		def other = new ThrowableCondition('foo')

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
