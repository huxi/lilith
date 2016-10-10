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

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static de.huxhorn.sulky.junit.JUnitTools.testClone
import static de.huxhorn.sulky.junit.JUnitTools.testSerialization
import static de.huxhorn.sulky.junit.JUnitTools.testXmlSerialization

class HttpRemoteUserConditionSpec extends Specification {

	@Shared
	private Set<Integer> accessEventsWithoutRemoteUser

	@Shared
	private Set<Integer> sfalkenEvents

	def setupSpec() {
		accessEventsWithoutRemoteUser = Collections.unmodifiableSet([
				5, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 70, 71, 72, 73, 74, 75, 77, 79, 81,
				100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 118, 119, 120
		] as Set<Integer>)

		sfalkenEvents = Collections.unmodifiableSet([
				63, 64
		] as Set<Integer>)
	}

	@Unroll
	def "Corpus works as expected for #condition (searchString=#input)."() {
		expect:
		ConditionCorpus.executeConditionOnCorpus(condition) == expectedResult

		where:
		input           | expectedResult
		null            | accessEventsWithoutRemoteUser
		''              | accessEventsWithoutRemoteUser
		'-'             | accessEventsWithoutRemoteUser
		'   '           | accessEventsWithoutRemoteUser
		' - '           | accessEventsWithoutRemoteUser
		'snafu'         | [] as Set
		'sfalken'       | sfalkenEvents as Set
		'   sfalken   ' | sfalkenEvents as Set

		condition = new HttpRemoteUserCondition(input)
	}

	@Unroll
	def "serialization works with searchString #input."() {
		when:
		def condition = new HttpRemoteUserCondition()
		condition.searchString = input

		and:
		def result = testSerialization(condition)

		then:
		result.searchString == input
		result.userName == condition.userName

		where:
		input << inputValues()
	}

	@Unroll
	def "XML serialization works with searchString #input."() {
		when:
		def condition = new HttpRemoteUserCondition()
		condition.searchString = input

		and:
		def result = testXmlSerialization(condition)

		then:
		result.searchString == input
		result.userName == condition.userName

		where:
		input << inputValues()
	}

	@Unroll
	def "cloning works with searchString #input."() {
		when:
		def condition = new HttpRemoteUserCondition()
		condition.searchString = input

		and:
		def result = testClone(condition)

		then:
		result.searchString == input
		result.userName == condition.userName

		where:
		input << inputValues()
	}

	def "equals behaves as expected."() {
		setup:
		def instance = new HttpRemoteUserCondition()
		def other = new HttpRemoteUserCondition('foo')

		expect:
		instance.equals(instance)
		!instance.equals(null)
		!instance.equals(new Object())
		!instance.equals(other)
		!other.equals(instance)
	}

	def inputValues() {
		[null, '', ' ', '-', ' - ', 'sfalken', ' sfalken ']
	}
}
