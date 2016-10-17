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

package de.huxhorn.lilith.swing.actions

import de.huxhorn.lilith.conditions.SearchStringCondition
import de.huxhorn.lilith.data.EventWrapperCorpus
import de.huxhorn.lilith.data.eventsource.EventWrapper
import de.huxhorn.lilith.swing.ViewContainer
import de.huxhorn.sulky.conditions.Condition
import spock.lang.Specification
import spock.lang.Unroll

import java.awt.event.ActionEvent

abstract class AbstractFilterActionSpecBase extends Specification {

	abstract FilterAction createAction()
	abstract Set<Integer> expectedEnabledIndices()
	abstract List<String> expectedSearchStrings()
	abstract Class expectedConditionClass()

	def 'isEnabled() with ViewContainer works as expected.'() {
		setup:
		def corpus = EventWrapperCorpus.createCorpus()
		def viewContainer = Mock(ViewContainer)
		def filterAction = createAction()
		filterAction.setViewContainer(viewContainer)
		def expectedEnabledIndices = expectedEnabledIndices()
		Set<Integer> enabledIndices = new HashSet<>()

		when:
		for (int i = 0; i < corpus.size(); i++) {
			def current = corpus[i]
			if (current instanceof EventWrapper) {
				filterAction.setEventWrapper(current)
				if(filterAction.isEnabled()) {
					enabledIndices.add(i)
				}
			}
		}

		then:
		enabledIndices == expectedEnabledIndices
	}

	def 'isEnabled() without ViewContainer works as expected.'() {
		setup:
		def corpus = EventWrapperCorpus.createCorpus()
		def filterAction = createAction()
		def expectedEnabledIndices = expectedEnabledIndices()
		Set<Integer> enabledIndices = new HashSet<>()

		when:
		for (int i = 0; i < corpus.size(); i++) {
			def current = corpus[i]
			if (current instanceof EventWrapper) {
				filterAction.setEventWrapper(current)
				if(filterAction.isEnabled()) {
					enabledIndices.add(i)
				}
			}
		}

		then:
		enabledIndices == expectedEnabledIndices
	}

	def 'resolveCondition() with ViewContainer works as expected.'() {
		setup:
		def corpus = EventWrapperCorpus.createCorpus()
		def viewContainer = Mock(ViewContainer)
		def filterAction = createAction()
		filterAction.setViewContainer(viewContainer)
		def expectedSearchStrings = expectedSearchStrings()
		List<String> searchStrings = new ArrayList<>()
		Set<String> conditionClasses = new HashSet<>()

		when:
		for (int i = 0; i < corpus.size(); i++) {
			def current = corpus[i]
			if (current instanceof EventWrapper) {
				filterAction.setEventWrapper(current)
				Condition condition = filterAction.resolveCondition(null)
				if(!condition) {
					continue
				}
				if(condition instanceof SearchStringCondition) {
					searchStrings.add(condition.searchString)
				} else {
					searchStrings.add(condition.toString())
				}

				conditionClasses.add(condition.class.name)
			}
		}

		then: 'the search strings of the resolved conditions are correct'
		searchStrings == expectedSearchStrings

		and: 'only the expected condition is returned'
		conditionClasses.contains(expectedConditionClass().name)
		conditionClasses.size() == 1
	}

	def 'resolveCondition() without ViewContainer works as expected.'() {
		setup:
		def corpus = EventWrapperCorpus.createCorpus()
		def filterAction = createAction()
		def expectedSearchStrings = expectedSearchStrings()
		List<String> searchStrings = new ArrayList<>()
		Set<String> conditionClasses = new HashSet<>()

		when:
		for (int i = 0; i < corpus.size(); i++) {
			def current = corpus[i]
			if (current instanceof EventWrapper) {
				filterAction.setEventWrapper(current)
				Condition condition = filterAction.resolveCondition(null)
				if(!condition) {
					continue
				}
				if(condition instanceof SearchStringCondition) {
					searchStrings.add(condition.searchString)
				} else {
					searchStrings.add(condition.toString())
				}

				conditionClasses.add(condition.class.name)
			}
		}

		then: 'the search strings of the resolved conditions are correct'
		searchStrings == expectedSearchStrings

		and: 'only the expected condition is returned'
		conditionClasses.contains(expectedConditionClass().name)
		conditionClasses.size() == 1
	}

	def 'actionPerformed(ActionEvent) with ViewContainer works as expected.'() {
		setup:
		def corpus = EventWrapperCorpus.createCorpus()
		def viewContainer = Mock(ViewContainer)
		def filterAction = createAction()
		filterAction.setViewContainer(viewContainer)
		def expectedSearchStrings = expectedSearchStrings()
		ActionEvent actionEvent = new ActionEvent("Foo", 0, null)

		when:
		for (int i = 0; i < corpus.size(); i++) {
			def current = corpus[i]
			if (current instanceof EventWrapper) {
				filterAction.setEventWrapper(current)
				filterAction.actionPerformed(actionEvent)
			}
		}

		then:
		expectedSearchStrings.size() * viewContainer.applyCondition(_, actionEvent)
	}

	def 'actionPerformed(ActionEvent) without ViewContainer works as expected.'() {
		setup:
		def corpus = EventWrapperCorpus.createCorpus()
		def filterAction = createAction()
		ActionEvent actionEvent = new ActionEvent("Foo", 0, null)

		when:
		for (int i = 0; i < corpus.size(); i++) {
			def current = corpus[i]
			if (current instanceof EventWrapper) {
				filterAction.setEventWrapper(current)
				filterAction.actionPerformed(actionEvent)
			}
		}

		then:
		true // no exception is thrown
	}

	def 'setting and getting ViewContainer works as expected.'() {
		setup:
		def viewContainer = Mock(ViewContainer)
		def filterAction = createAction()

		expect:
		filterAction.viewContainer == null

		when:
		filterAction.setViewContainer(viewContainer)

		then:
		filterAction.viewContainer == viewContainer

		when:
		filterAction.setViewContainer(null)

		then:
		filterAction.viewContainer == null
	}

	@Unroll
	def 'resolveCondition() with and without ActionEvent works as expected for #value.'() {
		setup:
		def filterAction = createAction()
		ActionEvent actionEvent = new ActionEvent("Foo", 0, null)
		ActionEvent altActionEvent = new ActionEvent("Foo", 0, null, ActionEvent.ALT_MASK)


		when:
		Condition nullEventCondition = null
		Condition actionEventCondition = null
		Condition altActionEventCondition = null
		if (value instanceof EventWrapper) {
			filterAction.setEventWrapper(value)
			nullEventCondition = filterAction.resolveCondition(null)
			actionEventCondition = filterAction.resolveCondition(actionEvent)
			altActionEventCondition = filterAction.resolveCondition(altActionEvent)
		}

		then:
		assert nullEventCondition == actionEventCondition
		if(actionEventCondition != null && isExpectingAlternativeBehavior()) {
			assert actionEventCondition != altActionEventCondition
		} else {
			assert actionEventCondition == altActionEventCondition
		}

		where:
		value << EventWrapperCorpus.createCorpus()
	}

	boolean isExpectingAlternativeBehavior() {
		false
	}

	def 'sanity check of expected values.'() {
		expect:
		expectedEnabledIndices().size() == expectedSearchStrings().size()
	}
}
