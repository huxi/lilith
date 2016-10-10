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
import de.huxhorn.lilith.data.access.HttpStatus
import spock.lang.Specification
import spock.lang.Unroll

class EventWrapperCorpusSpec extends Specification {
	def "matchesAllSet(null) explodes as expected"() {
		when:
		EventWrapperCorpus.matchAllSet(null)

		then:
		NullPointerException e = thrown()
		e.message == 'corpus must not be null!'
	}

	@Unroll
	def "matchesAllSet(#input) returns expected output #expectedResult"() {
		expect:
		EventWrapperCorpus.matchAllSet(input) == expectedResult

		where:
		input           | expectedResult
		[]              | [] as Set
		['a', 'b', 'c'] | [0, 1, 2] as Set
	}

	def "matchesAllSet() returns expected output."() {
		expect:
		EventWrapperCorpus.matchAllSet() == EventWrapperCorpus.matchAllSet(EventWrapperCorpus.createCorpus())
	}

	def "sanity check"() {
		setup:
		def corpus = EventWrapperCorpus.createCorpus()

		expect:
		corpus != null
		for (Object current : corpus) {
			if(current == null) {
				continue
			}

			// below code ensures that no basic operation on corpus entries is causing stack overflow
			assert current.equals(current)
			assert current.hashCode() == current.hashCode()
			assert current.toString() != null
		}
	}

	def 'unknown http status code is still unknown.'() {
		setup:
		def corpus = EventWrapperCorpus.createCorpus()
		AccessEvent unknownStatusCodeEvent = corpus[120].event
		def unknownStatusCode = unknownStatusCodeEvent.statusCode
		expect:
		unknownStatusCode
		HttpStatus.getStatus(unknownStatusCode) == null
	}

}
