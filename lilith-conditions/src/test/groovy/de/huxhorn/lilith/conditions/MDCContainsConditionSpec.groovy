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

class MDCContainsConditionSpec extends Specification {
	@Unroll
	def "Corpus works as expected for #condition (key=#key, value=#value)."() {
		expect:
		ConditionCorpus.executeConditionOnCorpus(condition) == expectedResult

		where:
		key      | value       | expectedResult
		null     | null        | [] as Set
		'mdcKey' | null        | [24, 68, 69] as Set
		'mdcKey' | 'mdcValue'  | [24] as Set

		condition = new MDCContainsCondition(key, value)
	}

	@Unroll
	def "serialization works with key=#key and value=#value."() {
		when:
		def condition = new MDCContainsCondition()
		condition.key = key
		condition.value = value

		and:
		def result = testSerialization(condition)

		then:
		result.key == key
		result.value == value

		where:
		key << keys()
		value << values()
	}

	@Unroll
	def "XML serialization works with key=#key and value=#value."() {
		when:
		def condition = new MDCContainsCondition()
		condition.key = key
		condition.value = value

		and:
		def result = testXmlSerialization(condition)

		then:
		result.key == key
		result.value == value

		where:
		key << keys()
		value << values()
	}

	@Unroll
	def "cloning works with key=#key and value=#value."() {
		when:
		def condition = new MDCContainsCondition()
		condition.key = key
		condition.value = value

		and:
		def result = testClone(condition)

		then:
		result.key == key
		result.value == value

		where:
		key << keys()
		value << values()
	}

	def "equals behaves as expected."() {
		setup:
		def instance = new MDCContainsCondition()
		def other = new MDCContainsCondition(key, value)

		expect:
		instance.equals(instance)
		!instance.equals(null)
		!instance.equals(new Object())
		!instance.equals(other)
		!other.equals(instance)

		where:
		key << ['', 'key', 'key', null]
		value << ['', 'value', null, 'value']
	}

	def keys() {
		[null, '', 'key', 'key', null]
	}

	def values() {
		[null, '', 'value', null, 'value']
	}
}
