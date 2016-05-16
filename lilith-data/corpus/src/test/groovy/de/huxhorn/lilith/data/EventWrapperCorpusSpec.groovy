package de.huxhorn.lilith.data

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

}
