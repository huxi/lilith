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

class HttpRequestUriStartsWithConditionSpec extends Specification {
	@Unroll
	def "Corpus works as expected for #condition (searchString=#input)."() {
		when:
		def result = ConditionCorpus.executeConditionOnCorpus(condition)

		then:
		new TreeSet(result) == new TreeSet(expectedResult)

		where:
		input             | expectedResult
		null              | [] as Set
		''                | ConditionCorpus.matchAllSet()
		'snafu'           | [] as Set
		'/'               | [72, 73, 122] as Set
		'/index.html'     | [73] as Set
		'/foo'            | [122] as Set
		'/foo/bar'        | [122] as Set
		'/foo/bar/foobar' | [122] as Set

		condition = new HttpRequestUriStartsWithCondition(input)
	}

	@Unroll
	def "serialization works with searchString #input."() {
		when:
		def condition = new HttpRequestUriStartsWithCondition()
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
		def condition = new HttpRequestUriStartsWithCondition()
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
		def condition = new HttpRequestUriStartsWithCondition()
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
		def instance = new HttpRequestUriStartsWithCondition()
		def other = new HttpRequestUriStartsWithCondition('foo')

		expect:
		instance.equals(instance)
		!instance.equals(null)
		!instance.equals(new Object())
		!instance.equals(other)
		!other.equals(instance)
	}

	def inputValues() {
		[null, '', 'value', '/', '/index.html']
	}
}
