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

package de.huxhorn.lilith.swing.menu

import de.huxhorn.lilith.data.EventWrapperCorpus
import de.huxhorn.lilith.data.eventsource.EventWrapper
import de.huxhorn.lilith.swing.ViewContainer
import spock.lang.Specification
import spock.lang.Unroll

abstract class AbstractFilterMenuSpecBase extends Specification {

	abstract AbstractFilterMenu createMenu()
	abstract Set<Integer> expectedEnabledIndices()

	def 'isEnabled() with ViewContainer works as expected.'() {
		setup:
		def corpus = EventWrapperCorpus.createCorpus()
		def viewContainer = Mock(ViewContainer)
		def menu = createMenu()
		menu.setViewContainer(viewContainer)
		def expectedEnabledIndices = expectedEnabledIndices()
		Set<Integer> enabledIndices = new HashSet<>()

		when:
		for (int i = 0; i < corpus.size(); i++) {
			def current = corpus[i]
			if (current instanceof EventWrapper) {
				menu.setEventWrapper(current)
				if(menu.isEnabled()) {
					enabledIndices.add(i)
				}
			}
		}

		then:
		new TreeSet(enabledIndices) == new TreeSet(expectedEnabledIndices)
	}

	@Unroll
	def 'isEnabled() with ViewContainer works as expected - componentCount for #entry.'() {
		setup:
		def viewContainer = Mock(ViewContainer)
		def menu = createMenu()
		menu.setViewContainer(viewContainer)

		when:
		def enabled = false
		def componentCount = 0
		if (entry instanceof EventWrapper) {
			menu.setEventWrapper(entry)
			enabled = menu.isEnabled()
			componentCount = menu.menuComponentCount
		}

		then:
		!enabled || componentCount != 0

		where:
		entry << EventWrapperCorpus.createCorpus()
	}

	def 'isEnabled() without ViewContainer works as expected.'() {
		setup:
		def corpus = EventWrapperCorpus.createCorpus()
		def menu = createMenu()
		def expectedEnabledIndices = expectedEnabledIndices()
		Set<Integer> enabledIndices = new HashSet<>()

		when:
		for (int i = 0; i < corpus.size(); i++) {
			def current = corpus[i]
			if (current instanceof EventWrapper) {
				menu.setEventWrapper(current)
				if(menu.isEnabled()) {
					enabledIndices.add(i)
				}
			}
		}

		then:
		new TreeSet(enabledIndices) == new TreeSet(expectedEnabledIndices)
	}

	int expectedGetSelectedEventCalls() {
		1
	}

	def 'setting and getting ViewContainer works as expected.'() {
		setup:
		def menu = createMenu()
		def viewContainerMock = Mock(ViewContainer)

		when:
		menu.viewContainer = null

		then:
		menu.viewContainer == null


		when:
		menu.viewContainer = viewContainerMock

		then:
		expectedGetSelectedEventCalls() * viewContainerMock.getSelectedEvent()
		and:
		menu.viewContainer == viewContainerMock


		when:
		menu.viewContainer = null

		then:
		menu.viewContainer == null
	}
}
