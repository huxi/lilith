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

import de.huxhorn.sulky.conditions.Condition
import spock.lang.Specification
import spock.lang.Unroll

class ConditionCorpusSpec extends Specification {
	@Unroll
	def "corpus works as expected for condition #condition"() {
		setup:
		def corpus = ConditionCorpus.createCorpus()

		when:
		def result = ConditionCorpus.executeConditionOnCorpus(condition, corpus)

		then:
		result == expectedResult

		where:
		condition                | expectedResult
		new MatchNoneCondition() | [] as Set
		new MatchAllCondition()  | ConditionCorpus.matchAllSet()
	}

	def "executeConditionOnCorpus(null) explodes as expected"() {
		when:
		ConditionCorpus.executeConditionOnCorpus(null)

		then:
		NullPointerException e = thrown()
		e.message == 'condition must not be null!'
	}

	def "executeConditionOnCorpus(null, null) explodes as expected"() {
		when:
		ConditionCorpus.executeConditionOnCorpus(null, null)

		then:
		NullPointerException e = thrown()
		e.message == 'condition must not be null!'
	}

	def "executeConditionOnCorpus(null, []) explodes as expected"() {
		when:
		ConditionCorpus.executeConditionOnCorpus(null, [])

		then:
		NullPointerException e = thrown()
		e.message == 'condition must not be null!'
	}

	def "executeConditionOnCorpus(condition, null) explodes as expected"() {
		when:
		ConditionCorpus.executeConditionOnCorpus(new MatchNoneCondition(), null)

		then:
		NullPointerException e = thrown()
		e.message == 'corpus must not be null!'
	}

	def "matchesAllSet() returns expected output."() {
		expect:
		ConditionCorpus.matchAllSet() == 0..ConditionCorpus.createCorpus().size()-1 as Set<Integer>
	}

	private static class MatchNoneCondition implements Condition {

		@Override
		boolean isTrue(Object element) {
			return false
		}

		@Override
		Condition clone() throws CloneNotSupportedException {
			return super.clone() as Condition
		}


		@Override
		public String toString() {
			return 'MatchNone'
		}
	}

	private static class MatchAllCondition implements Condition {

		@Override
		boolean isTrue(Object element) {
			return true
		}

		@Override
		Condition clone() throws CloneNotSupportedException {
			return super.clone() as Condition
		}

		@Override
		public String toString() {
			return 'MatchAll'
		}
	}
}
