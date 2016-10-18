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

import de.huxhorn.lilith.swing.ViewContainer
import de.huxhorn.sulky.conditions.Condition
import spock.lang.Specification
import spock.lang.Unroll

import java.awt.event.ActionEvent

abstract class AbstractBasicFilterActionSpecBase extends Specification {

	abstract BasicFilterAction createAction()
	abstract Class expectedConditionClass()

	def 'isEnabled() with ViewContainer works as expected.'() {
		setup:
		def viewContainer = Mock(ViewContainer)
		def filterAction = createAction()
		filterAction.setViewContainer(viewContainer)

		expect:
		filterAction.isEnabled()
	}

	def 'isEnabled() without ViewContainer works as expected.'() {
		setup:
		def filterAction = createAction()

		expect:
		!filterAction.isEnabled()
	}

	def 'resolveCondition() with ViewContainer works as expected.'() {
		setup:
		def viewContainer = Mock(ViewContainer)
		def filterAction = createAction()
		filterAction.setViewContainer(viewContainer)

		when:
		def condition = filterAction.resolveCondition(null)

		then:
		condition != null
		condition.class == expectedConditionClass()
	}

	def 'resolveCondition() without ViewContainer works as expected.'() {
		setup:
		def filterAction = createAction()

		when:
		def condition = filterAction.resolveCondition(null)

		then:
		condition == null
	}


	def 'actionPerformed(ActionEvent) with ViewContainer works as expected.'() {
		setup:
		def viewContainer = Mock(ViewContainer)
		def filterAction = createAction()
		filterAction.setViewContainer(viewContainer)
		ActionEvent actionEvent = new ActionEvent("Foo", 0, null)

		when:
		filterAction.actionPerformed(actionEvent)

		then:
		1 * viewContainer.applyCondition(_, actionEvent)
	}

	def 'actionPerformed(ActionEvent) without ViewContainer works as expected.'() {
		setup:
		def filterAction = createAction()
		ActionEvent actionEvent = new ActionEvent("Foo", 0, null)

		when:
		filterAction.actionPerformed(actionEvent)

		then:
		noExceptionThrown()
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
	def 'resolveCondition() with and without ActionEvent works as expected.'() {
		setup:
		def viewContainerMock = Mock(ViewContainer)
		def filterAction = createAction()
		filterAction.setViewContainer(viewContainerMock)

		ActionEvent actionEvent = new ActionEvent("Foo", 0, null)
		ActionEvent altActionEvent = new ActionEvent("Foo", 0, null, ActionEvent.ALT_MASK)


		when:
		Condition nullEventCondition = filterAction.resolveCondition(null)
		Condition actionEventCondition = filterAction.resolveCondition(actionEvent)
		Condition altActionEventCondition = filterAction.resolveCondition(altActionEvent)

		then:
		assert nullEventCondition == actionEventCondition
		if(actionEventCondition != null && isExpectingAlternativeBehavior()) {
			assert actionEventCondition != altActionEventCondition
		} else {
			assert actionEventCondition == altActionEventCondition
		}
	}

	boolean isExpectingAlternativeBehavior() {
		false
	}
}
